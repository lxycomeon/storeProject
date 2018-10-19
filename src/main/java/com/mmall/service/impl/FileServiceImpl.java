package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/18
 * @path 传过来的webapp下的真实路径，在controller层通过request获取
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {


	private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

	@Override
	public String upload(MultipartFile file, String path) {


		String fileName = file.getOriginalFilename();
//		获取扩展名
		String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
//		重新指定一个新的文件名，防止上传文件名重复
		String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
		logger.info("开始上传文件，上传的文件名为：{}，上传的路径为：{}，新文件名为：{}",fileName,path,uploadFileName);

		File fileDir = new File(path);
		if (!fileDir.exists()){
			//如果不存在改路径，重新创建该目录
			fileDir.setWritable(true);	//设置写权限
			fileDir.mkdirs();			//可以创建多级连续目录
		}
		File targetFile = new File(path,uploadFileName);

		try {
			file.transferTo(targetFile);	//将controller层传过来的文件上传到指定目录

			//将文件上传到FTP服务器
			FTPUtil.uploadFile(Lists.newArrayList(targetFile));
			//上传完，删除upload文件夹下的文件
			targetFile.delete();
		} catch (IOException e) {
			logger.error("上传文件异常",e);
			return null;
		}

		return targetFile.getName();
	}
}
