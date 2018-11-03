package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.vo.ProductDetailVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
	public ServerResponse<String> productSave(Product product){
		ServerResponse response = null;
		response = iProductService.saveOrUpdateProduct(product);
		System.out.println(response);
		return response;
	}

	@RequestMapping("set_sale_status.do")
	@ResponseBody
	public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
		ServerResponse response = null;
		response = iProductService.updateSaleStatus(productId,status);
		System.out.println(response);
		return response;
	}

	@RequestMapping("detail.do")
	@ResponseBody
	public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
		ServerResponse response = null;
		response = iProductService.getProductById(productId);
		System.out.println(response);
		return response;
	}

	@RequestMapping("list.do")
	@ResponseBody
	public ServerResponse getList(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize ){
		ServerResponse response = null;
		response = iProductService.getProductList(pageNum,pageSize);
		System.out.println(response);
		return response;
	}

	@RequestMapping("search.do")
	@ResponseBody
	public ServerResponse searchProductList(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize
											,String productName,String productId){
		ServerResponse response = null;
		response = iProductService.searchProduct(pageNum,pageSize,productName,productId);
		System.out.println(response);
		return response;
	}

	@RequestMapping("upload.do")
	@ResponseBody
	public ServerResponse upload(@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){
		ServerResponse response = null;
		String path = request.getSession().getServletContext().getRealPath("upload");
		String tragetFileName = iFileService.upload(file,path);
		String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+tragetFileName;
		Map fileMap = Maps.newHashMap();
		fileMap.put("uri",tragetFileName);
		fileMap.put("url",url);
		response = ServerResponse.createBySuccess(fileMap);
		System.out.println(response);
		return response;
	}


	@RequestMapping("richtext_img_upload.do")
	@ResponseBody
	public Map richTextImgUpload( @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
		//富文本上传图片，返回值有特殊的要求格式要遵循。此处使用了simditor富文本插件的上传
		//返回值为key value：“success”：true/false
		//"msg":"error message"
		//"file_path":"real file path"
		Map resultMap =Maps.newHashMap();
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
		System.out.println(resultMap);
		return resultMap;
	}


}
