package com.mmall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.controller.portal.OrderController;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FTPUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderListVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.ShippingVo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/23
 */
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

	Logger log = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	OrderMapper orderMapper;

	@Autowired
	OrderItemMapper orderItemMapper;

	@Autowired
	PayInfoMapper payInfoMapper;

	@Autowired
	CartMapper cartMapper;

	@Autowired
	ShippingMapper shippingMapper;

	@Autowired
	ProductMapper productMapper;

	@Autowired
	MiaoshaOrderMapper miaoshaOrderMapper;

	@Autowired
	IUserService iUserService;

	@Autowired
	UserMapper userMapper;

	@Autowired
	MiaoshaProductMapper miaoshaProductMapper;


	// 支付宝当面付2.0服务
	private static AlipayTradeService   tradeService;
	static {
		/** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
		 *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
		 */
		Configs.init("zfbinfo.properties");

		/** 使用Configs提供的默认参数
		 *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
		 */
		tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
	}


	@Override
	public ServerResponse<Map> pay(Long orderNo,String path) {
		Map resultMap = Maps.newHashMap();

		Order order = orderMapper.selectByOrderNo(orderNo);
		if (order == null){
			return ServerResponse.createByErrorMessage("未找到该订单");
		}
		resultMap.put("orderNo",String.valueOf(order.getOrderNo()));

		// (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
		// 需保证商户系统端不能重复，建议通过数据库sequence生成，
		String outTradeNo = order.getOrderNo().toString();

		// (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
		String subject = new StringBuilder().append("订单：").append(order.getOrderNo()).append("付款").toString();

		// (必填) 订单总金额，单位为元，不能超过1亿元
		// 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
		String totalAmount = order.getPayment().toString();

		// (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
		// 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
		String undiscountableAmount = "0";

		// 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
		// 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
		String sellerId = "";

		// 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
		String body = new StringBuilder().append("购买n件商品，一共").append(order.getPayment().toString()).append("元").toString();

		// 商户操作员编号，添加此参数可以为商户操作员做销售统计
		String operatorId = "test_operator_id";

		// (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
		String storeId = "test_store_id";

		// 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
		ExtendParams extendParams = new ExtendParams();
		extendParams.setSysServiceProviderId("2088100200300400500");

		// 支付超时，定义为120分钟
		String timeoutExpress = "120m";

		// 商品明细列表，需填写购买商品详细信息，
		List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

		List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
		for (OrderItem orderItem:orderItemList) {
			// 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
			GoodsDetail goods = GoodsDetail.newInstance(orderItem.getId().toString(), orderItem.getProductName(), orderItem.getCurrentUnitPrice().longValue(), orderItem.getQuantity());
			// 创建好一个商品后添加至商品明细列表
			goodsDetailList.add(goods);
		}

		// 创建扫码支付请求builder，设置请求参数
		AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
				.setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
				.setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
				.setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
				.setTimeoutExpress(timeoutExpress)
				.setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
				.setGoodsDetailList(goodsDetailList);

		AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
		switch (result.getTradeStatus()) {
			case SUCCESS:
				log.info("支付宝预下单成功: )");

				AlipayTradePrecreateResponse response = result.getResponse();
				dumpResponse(response);

				File folder = new File(path);
				if(!folder.exists()){
					folder.setWritable(true);
					folder.mkdirs();
				}
				// 需要修改为运行机器上的路径
				String qrPath = String.format(path + "/qr-%s.png",response.getOutTradeNo());
				String qrFileName = String.format("qr-%s.png",response.getOutTradeNo());
				ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
				File targetFile = new File(path,qrFileName);
				try {
					FTPUtil.uploadFile(Lists.newArrayList(targetFile));
				} catch (IOException e) {
					log.error("上传二维码异常",e);
				}
				log.info("filePath:" + qrPath);
				String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile.getName();
				resultMap.put("qrUrl",qrUrl);
				return ServerResponse.createBySuccess(resultMap);

			case FAILED:
				log.error("支付宝预下单失败!!!");
				return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");
			case UNKNOWN:
				log.error("系统异常，预下单状态未知!!!");
				return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");
			default:
				log.error("不支持的交易状态，交易返回异常!!!");
				return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
		}
	}

	// 简单打印应答
	private void dumpResponse(AlipayResponse response) {
		if (response != null) {
			log.info(String.format("code:%s , msg:%s", response.getCode(), response.getMsg()));
			if (StringUtils.isNotEmpty(response.getSubCode())) {
				log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
						response.getSubMsg()));
			}
			log.info("body:" + response.getBody());
		}
	}

	public ServerResponse aliCallback(Map<String,String> params){
		Long orderNo = Long.parseLong(params.get("out_trade_no"));
		String tradeNo = params.get("trade_no");
		String tradeStatus = params.get("trade_status");
		Order order = orderMapper.selectByOrderNo(orderNo);
		if(order == null){
			return ServerResponse.createByErrorMessage("非快乐慕商城的订单,回调忽略");
		}
		if(order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
			return ServerResponse.createBySuccess("支付宝重复调用");
		}
		if(Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
			order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
			order.setStatus(Const.OrderStatusEnum.PAID.getCode());
			orderMapper.updateByPrimaryKeySelective(order);
		}

		PayInfo payInfo = new PayInfo();
		payInfo.setUserId(order.getUserId());
		payInfo.setOrderNo(order.getOrderNo());
		payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
		payInfo.setPlatformNumber(tradeNo);
		payInfo.setPlatformStatus(tradeStatus);

		payInfoMapper.insert(payInfo);

		return ServerResponse.createBySuccess();
	}

	@Override
	public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo) {
		if (orderNo == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Order order = orderMapper.selectByOrderNoAndUserId(userId,orderNo);
		if (order == null){
			return ServerResponse.createByErrorMessage("该用户没有该订单，查询无效");
		}
		if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
			return ServerResponse.createBySuccess(true);
		}
		return ServerResponse.createBySuccess(false);
	}

	private Long generateOrderNo(){//生成订单号
		int r1=new Random().nextInt(100);//产生2个0-9的随机数
		int r2=new Random().nextInt(1000);
		Long currentTime = System.currentTimeMillis();
		return currentTime+r1 +r2;
	}

	@Override
	public ServerResponse<OrderListVo> createOrder(Integer userId, Integer shippingId) {
		Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);	//dress
		if (shipping == null || shipping.getUserId() != userId){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		//todo 事务管理
		List<Cart> cartList = cartMapper.selectCheckedProductByUserId(userId);
		if (cartList == null || cartList.size()==0){
			return ServerResponse.createByErrorMessage("创建订单失败，购物车为空");
		}
		int rowCount;
		Order order = new Order();
		Long orderNum = generateOrderNo();
		BigDecimal payment = new BigDecimal("0.0");
		for (Cart cartItem:cartList) {
			OrderItem orderItem = new OrderItem();
			Product productItem = productMapper.selectByPrimaryKey(cartItem.getProductId());
			if (productItem.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
				return ServerResponse.createByErrorMessage("产品"+productItem.getName()+"不是在线售卖状态");
			}
			if (cartItem.getQuantity() > productItem.getStock()){
				return ServerResponse.createByErrorMessage("产品"+productItem.getName()+"库存不足");
			}

			orderItem.setUserId(userId);
			orderItem.setOrderNo(orderNum);
			orderItem.setProductId(cartItem.getProductId());
			orderItem.setProductName(productItem.getName());
			orderItem.setProductImage(productItem.getMainImage());
			orderItem.setCurrentUnitPrice(productItem.getPrice());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setTotalPrice(BigDecimalUtil.mul(productItem.getPrice().doubleValue(),cartItem.getQuantity()));

			payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());	//计算总价
			rowCount = orderItemMapper.insertSelective(orderItem);	//插入订单项
			productItem.setStock(productItem.getStock() - cartItem.getQuantity());
			rowCount = productMapper.updateByPrimaryKeySelective(productItem);//减少产品库存
			rowCount = cartMapper.deleteByPrimaryKey(cartItem.getId());			//清空购物车
		}
		order.setOrderNo(orderNum);
		order.setUserId(userId);
		order.setShippingId(shippingId);
		order.setPayment(payment);
		order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
		order.setPostage(0);	//邮费0
		order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
		rowCount = orderMapper.insertSelective(order);
		return ServerResponse.createBySuccess(assembleOrderListVoByOrder(order));
	}

	public ServerResponse<PageInfo> listOrder(Integer userId, int pageNum,int pageSize) {

		if (userId == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		List<OrderListVo> orderListVos = Lists.newArrayList();
		PageHelper.startPage(pageNum,pageSize);
		List<Order> orderList = orderMapper.selectByUserId(userId);
		if (orderList != null){
			for (Order orderItem:orderList) {
				orderListVos.add(assembleOrderListVoByOrder(orderItem));
			}
		}
		PageInfo pageResult = new PageInfo(orderListVos);	//使用原返回的list计算分页的一些信息
		//pageResult.setList(productListVoList);				//将转为视图的Vo返回,没区别
		return ServerResponse.createBySuccess(pageResult);
	}

	public OrderItemVo assembleOrderItemVo(OrderItem orderItemItem){
		OrderItemVo orderItemVo = new OrderItemVo();
		orderItemVo.setOrderNo(orderItemItem.getOrderNo());
		orderItemVo.setProductId(orderItemItem.getProductId());
		orderItemVo.setProductName(orderItemItem.getProductName());
		orderItemVo.setProductImage(orderItemItem.getProductImage());
		orderItemVo.setCurrentUnitPrice(orderItemItem.getCurrentUnitPrice());
		orderItemVo.setQuantity(orderItemItem.getQuantity());
		orderItemVo.setTotalPrice(orderItemItem.getTotalPrice());
		orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItemItem.getCreateTime()));
		return orderItemVo;
	}

	public OrderListVo assembleOrderListVoByOrder(Order orderItem){
		OrderListVo orderListVo = new OrderListVo();
		orderListVo.setOrderNo(orderItem.getOrderNo());
		orderListVo.setPayment(orderItem.getPayment());
		orderListVo.setPaymentType(orderItem.getPaymentType());
		orderListVo.setPaymentTypeDesc((Const.PaymentTypeEnum.codeOf(orderItem.getPaymentType())).getValue());
		orderListVo.setPostage(orderItem.getPostage());
		orderListVo.setStatus(orderItem.getStatus());
		orderListVo.setStatusDesc(Const.OrderStatusEnum.codeOf(orderItem.getStatus()).getValue());

		orderListVo.setPaymentTime(DateTimeUtil.dateToStr(orderItem.getPaymentTime()));
		orderListVo.setSendTime(DateTimeUtil.dateToStr(orderItem.getSendTime()));
		orderListVo.setEndTime(DateTimeUtil.dateToStr(orderItem.getEndTime()));
		orderListVo.setCloseTime(DateTimeUtil.dateToStr(orderItem.getCloseTime()));
		orderListVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));

		List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderItem.getOrderNo());
		List<OrderItemVo> orderItemVoList = Lists.newArrayList();
		if (orderItemList != null){
			for (OrderItem orderItemItem:orderItemList) {
				orderItemVoList.add(assembleOrderItemVo(orderItemItem));
			}
		}
		orderListVo.setOrderItemVoList(orderItemVoList);
		orderListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
		orderListVo.setShippingId(orderItem.getShippingId());
		Shipping shipping = shippingMapper.selectByPrimaryKey(orderItem.getShippingId());
		if (shipping != null){
			orderListVo.setReceiverName(shipping.getReceiverName());
			orderListVo.setShippingVo(assembleShippingVo(shipping));
		}
		return orderListVo;
	}

	private ShippingVo assembleShippingVo(Shipping shipping){
		ShippingVo shippingVo = new ShippingVo();
		shippingVo.setReceiverName(shipping.getReceiverName());
		shippingVo.setReceiverAddress(shipping.getReceiverAddress());
		shippingVo.setReceiverProvince(shipping.getReceiverProvince());
		shippingVo.setReceiverCity(shipping.getReceiverCity());
		shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
		shippingVo.setReceiverMobile(shipping.getReceiverMobile());
		shippingVo.setReceiverZip(shipping.getReceiverZip());
		shippingVo.setReceiverPhone(shippingVo.getReceiverPhone());
		return shippingVo;
	}

	@Override
	public ServerResponse getDetailByOrderNo(Integer userId, Long orderNo) {
		Order order = orderMapper.selectByOrderNoAndUserId(userId,orderNo);
		if (order == null ){
			return ServerResponse.createByErrorMessage("该用户没有此订单");
		}
		return ServerResponse.createBySuccess(assembleOrderListVoByOrder(order));
	}

	@Override
	public ServerResponse cancelOrder(Integer userId, Long orderNo) {
		Order order = orderMapper.selectByOrderNoAndUserId(userId,orderNo);
		if (order == null){
			return ServerResponse.createByErrorMessage("该用户没有此订单");
		}
		if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
			return ServerResponse.createByErrorMessage("此订单已经付款，无法被取消");
		}
		order.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
		int rowCount = orderMapper.updateByPrimaryKeySelective(order);
		if (rowCount > 0){
			return ServerResponse.createBySuccess();
		}
		return ServerResponse.createByErrorMessage("删除失败");
	}

	@Override
	public ServerResponse getOrderCartProduct(Integer userId) {
		OrderProductVo orderProductVo = new OrderProductVo();
		BigDecimal productTotalPrice = new BigDecimal("0");

		List<Cart> cartList = cartMapper.selectCheckedProductByUserId(userId);
		List<OrderItemVo> orderItemVoList = Lists.newArrayList();
		for (Cart cartItem:cartList) {
			OrderItemVo orderItemVo = new OrderItemVo();
			Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());

			orderItemVo.setProductId(cartItem.getProductId());
			orderItemVo.setProductName(product.getName());
			orderItemVo.setProductImage(product.getMainImage());
			orderItemVo.setCurrentUnitPrice(product.getPrice());
			orderItemVo.setQuantity(cartItem.getQuantity());
			orderItemVo.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartItem.getQuantity()));
			productTotalPrice = BigDecimalUtil.add(productTotalPrice.doubleValue(),orderItemVo.getTotalPrice().doubleValue());	//计算总价
			orderItemVoList.add(orderItemVo);
		}

		orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
		orderProductVo.setOrderItemVoList(orderItemVoList);
		orderProductVo.setProductTotalPrice(productTotalPrice);

		return ServerResponse.createBySuccess(orderProductVo);
	}

	//backend

	@Override
	public ServerResponse<PageInfo> manageListOrder(int pageNum,int pageSize) {

		List<OrderListVo> orderListVos = Lists.newArrayList();
		PageHelper.startPage(pageNum,pageSize);
		List<Order> orderList  = orderMapper.selectAll();

		if (orderList != null){
			for (Order orderItem:orderList) {
				orderListVos.add(assembleOrderListVoByOrder(orderItem));
			}
		}
		PageInfo pageResult = new PageInfo(orderListVos);	//使用原返回的list计算分页的一些信息
		//pageResult.setList(productListVoList);				//将转为视图的Vo返回,没区别
		return ServerResponse.createBySuccess(pageResult);
	}

	@Override
	public ServerResponse manageGetDetailByOrderNo( Long orderNo) {
		Order order = orderMapper.selectByOrderNo(orderNo);
		if (order == null ){
			return ServerResponse.createByErrorMessage("没有此订单");
		}
		return ServerResponse.createBySuccess(assembleOrderListVoByOrder(order));
	}

	//todo 模糊搜索
	@Override
	public ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize) {
		Order order = orderMapper.selectByOrderNo(orderNo);
		PageHelper.startPage(pageNum,pageSize);	//不能和上一条换位置，要不然分页不起作用
		if (order == null ){
			return ServerResponse.createByErrorMessage("没有此订单");
		}
		OrderListVo orderListVo =assembleOrderListVoByOrder(order);
		PageInfo pageResult = new PageInfo(Lists.newArrayList(orderListVo));

		return ServerResponse.createBySuccess(pageResult);
	}

	@Override
	public ServerResponse manageSendGoods(Long orderNo) {
		Order order = orderMapper.selectByOrderNo(orderNo);
		if (order == null ){
			return ServerResponse.createByErrorMessage("没有此订单");
		}
		if (order.getStatus() == Const.OrderStatusEnum.PAID.getCode()){
			order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
			order.setSendTime(new Date());
			orderMapper.updateByPrimaryKeySelective(order);
			return ServerResponse.createBySuccess("发货成功");
		}
		return ServerResponse.createBySuccess("发货失败");
	}

	@Override
	public void closeOrder(int hour) {
		Date closeDateTime = DateUtils.addHours(new Date(),-hour);
		List<Order> orderList = orderMapper.selectByStatusAndTime(Const.OrderStatusEnum.NO_PAY.getCode(),closeDateTime);
		for (Order order:orderList) {
			List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
			for (OrderItem orderItemItem:orderItemList) {
				//Product product = productMapper.selectByPrimaryKey(orderItemItem.getProductId());
				Integer stock = productMapper.selectStockByPrimaryKey(orderItemItem.getProductId());//这里只查库存(节省sql效率)，且在sql语句的后面加上for update作为乐观锁
				if (stock == null){	//查询的商品已经被删除了
					continue;
				}
				Product product = new Product();
				product.setId(orderItemItem.getProductId());
				product.setStock(stock + orderItemItem.getQuantity());//更新库存
				productMapper.updateByPrimaryKeySelective(product);
			}
			order.setCloseTime(new Date());
			order.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
			orderMapper.updateByPrimaryKeySelective(order);	//这种更新方法，会使得更新的语句和字段很长，不利于执行效率。
			log.info("取消订单：{}",order.getOrderNo());
		}
	}

	//秒杀订单的创建，商品数量只能为1
	@Override
	public ServerResponse createMiaoshaOrder(Integer userId, Integer shippingId, Integer miaoshaProductId) {
		Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);	//dress
		if (shipping == null || shipping.getUserId() != userId){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}

		int rowCount = -1;
		Order order = new Order();
		Long orderNum = generateOrderNo();

		MiaoshaProduct miaoshaProduct = miaoshaProductMapper.selectByPrimaryKey(miaoshaProductId);
		//后台再加一个秒杀时间控制
		if (miaoshaProduct.getStartTime().getTime() > System.currentTimeMillis() || miaoshaProduct.getEndTime().getTime() < System.currentTimeMillis()){
			return ServerResponse.createByErrorMessage("秒杀时间有误，请稍后再试");
		}
		//MiaoshaOrder表中 userid和ProductId中存在唯一索引，实现一个用户只能秒杀一个秒杀商品
		MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
		miaoshaOrder.setUserId(userId);
		miaoshaOrder.setOrderId(orderNum);
		miaoshaOrder.setMiaoshaProductId(miaoshaProductId);
		try {
			rowCount = miaoshaOrderMapper.insertSelective(miaoshaOrder);
		}catch (Exception e){
			log.info("userId:{}重复秒杀ProductId:{}商品",userId,miaoshaProductId);
			if (rowCount <= 0){
				return ServerResponse.createByErrorMessage("您已经秒杀过此商品了，请勿重复下单");
			}
		}

		OrderItem orderItem = new OrderItem();

		Product productItem = productMapper.selectByPrimaryKey(miaoshaProduct.getProductId());
		if (miaoshaProduct.getMiaoshaStock() < 1){
			return ServerResponse.createByErrorMessage("产品"+productItem.getName()+"库存不足,秒杀失败,已结被抢完啦");
		}
		orderItem.setUserId(userId);
		orderItem.setOrderNo(orderNum);
		orderItem.setProductId(miaoshaProduct.getProductId());
		orderItem.setProductName(productItem.getName());
		orderItem.setProductImage(productItem.getMainImage());
		//这里注意是秒杀价格
		orderItem.setCurrentUnitPrice(miaoshaProduct.getMiaoshaPrice());
		orderItem.setQuantity(1);
		orderItem.setTotalPrice(miaoshaProduct.getMiaoshaPrice());


		rowCount = orderItemMapper.insertSelective(orderItem);	//插入订单项
		miaoshaProduct.setMiaoshaStock(miaoshaProduct.getMiaoshaStock() - 1);
		//减少秒杀产品库存,新new一个对象出来，只有两个要更新的值，增加sql语句的执行效率
		rowCount = miaoshaProductMapper.updateByPrimaryKeySelective(new MiaoshaProduct(miaoshaProduct.getId(),miaoshaProduct.getMiaoshaStock()));

		order.setOrderNo(orderNum);
		order.setUserId(userId);
		order.setShippingId(shippingId);
		order.setPayment(orderItem.getTotalPrice());
		order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
		order.setPostage(0);	//邮费0
		order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
		rowCount = orderMapper.insertSelective(order);
		return ServerResponse.createBySuccess(assembleOrderListVoByOrder(order));
	}
}
