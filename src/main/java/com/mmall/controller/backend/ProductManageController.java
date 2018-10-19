package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/18
 */
@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {

	@Autowired
	private IUserService iUserService;

	@Autowired
	private IProductService iProductService;

	@Autowired
	private IFileService iFileService;

	@RequestMapping("save.do")
	@ResponseBody
	public ServerResponse<String> productSave(HttpSession session, Product product){
		ServerResponse response = null;
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			response = iProductService.saveOrUpdateProduct(product);
		} else {
			response = ServerResponse.createByErrorMessage("无权限，请管理员登陆");
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("set_sale_status.do")
	@ResponseBody
	public ServerResponse<String> setSaleStatus(HttpSession session, Integer productId,Integer status){
		ServerResponse response = null;
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			response = iProductService.updateSaleStatus(productId,status);
		} else {
			response = ServerResponse.createByErrorMessage("无权限，请管理员登陆");
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("detail.do")
	@ResponseBody
	public ServerResponse<ProductDetailVo> getProductDetail(HttpSession session, Integer productId){
		ServerResponse response = null;
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			response = iProductService.getProductById(productId);
		} else {
			response = ServerResponse.createByErrorMessage("无权限，请管理员登陆");
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("list.do")
	@ResponseBody
	public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize ){
		ServerResponse response = null;
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			response = iProductService.getProductList(pageNum,pageSize);
		} else {
			response = ServerResponse.createByErrorMessage("无权限，请管理员登陆");
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("search.do")
	@ResponseBody
	public ServerResponse searchProductList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize
											,String productName,String productId){
		ServerResponse response = null;
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			response = iProductService.searchProduct(pageNum,pageSize,productName,productId);
		} else {
			response = ServerResponse.createByErrorMessage("无权限，请管理员登陆");
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("upload.do")
	@ResponseBody
	public ServerResponse upload(HttpSession session,@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){
		ServerResponse response = null;
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			String path = request.getSession().getServletContext().getRealPath("upload");
			String tragetFileName = iFileService.upload(file,path);
			String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+tragetFileName;
			Map fileMap = Maps.newHashMap();
			fileMap.put("uri",tragetFileName);
			fileMap.put("url",url);
			response = ServerResponse.createBySuccess(fileMap);
		} else {
			response = ServerResponse.createByErrorMessage("无权限，请管理员登陆");
		}
		System.out.println(response);
		return response;
	}


	@RequestMapping("richtext_img_upload.do")
	@ResponseBody
	public Map richTextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
		//富文本上传图片，返回值有特殊的要求格式要遵循。此处使用了simditor富文本插件的上传
		//返回值为key value：“success”：true/false
		//"msg":"error message"
		//"file_path":"real file path"
		Map resultMap =Maps.newHashMap();
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			resultMap.put("success",false);
			resultMap.put("msg","请登陆管理员");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			String path = request.getSession().getServletContext().getRealPath("upload");
			String tragetFileName = iFileService.upload(file,path);
			if (StringUtils.isBlank(tragetFileName)){
				resultMap.put("success",false);
				resultMap.put("msg","上传失败");
			}
			String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+tragetFileName;
			resultMap.put("success",true);
			resultMap.put("msg","上传成功");
			resultMap.put("file_path",url);
			//前端约定，对于富文本的上传成功后，要在responseHeader中添加这个
			response.addHeader("Access-Control-Allow-Headers","X-File-Name");
		} else {
			resultMap.put("success",false);
			resultMap.put("msg","无权限操作");
		}
		System.out.println(resultMap);

		return resultMap;
	}




}
