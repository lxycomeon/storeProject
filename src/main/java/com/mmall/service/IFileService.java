package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/18
 */
public interface IFileService {
	public String upload(MultipartFile file,String path);

}
