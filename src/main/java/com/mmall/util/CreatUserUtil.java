package com.mmall.util;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created with IntelliJ IDEA By lxy on 2018/12/31
 * 测试使用，目的是将用户登录记录token记录供给jmeter使用
 */
public class CreatUserUtil {


	public static void main(String[] args) {
		try {
			recToken();
		}catch (Exception e){
			e.printStackTrace();
		}

	}
	public static void recToken() throws Exception{
		String urlString = "http://192.168.50.111:80/user/login.do";
		File file = new File("D:/userCookie.txt");
		if(file.exists()) {
			file.delete();
		}
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		file.createNewFile();
		raf.seek(0);
		for(int i=0;i<5000;i++) {
			URL url = new URL(urlString);
			HttpURLConnection co = (HttpURLConnection)url.openConnection();
			co.setRequestMethod("POST");
			co.setDoOutput(true);
			OutputStream out = co.getOutputStream();
			String params = "username=TestUser_"+i+"&password=123456";
			out.write(params.getBytes());
			out.flush();
			InputStream inputStream = co.getInputStream();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte buff[] = new byte[1024];
			int len = 0;
			while((len = inputStream.read(buff)) >= 0) {
				bout.write(buff, 0 ,len);
			}
			inputStream.close();
			bout.close();
			String response = new String(bout.toByteArray());
			//生成的时候，要改一下登录页面的返回接口，只返回产生的UUID的token，这样就不用再进行转换了
			String token = response;//jo.getString("data");
			System.out.println("create token :TestUser_" +i);

			String row = "TestUser_"+i+","+token;
			raf.seek(raf.length());
			raf.write(row.getBytes());
			raf.write("\r\n".getBytes());
			System.out.println("write to file :TestUser_"+i);
		}
		raf.close();

		System.out.println("over");
	}

	//生成token




}
