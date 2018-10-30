package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/29
 */
@Slf4j
public class JsonUtil {

	private static ObjectMapper objectMapper = new ObjectMapper();
	static {	//静态配置块

		//对象的所有字段全部列入,json序列化出来的字符串，是否有哪些空值不显示出来的一些设置。可查看源码内的注释
		objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);

		//取消默认转换timestamps时间戳的形式，就是转换为一个LONG型的数值表示为S就是时间戳
		objectMapper.configure(SerializationConfig.Feature.WRITE_DATE_KEYS_AS_TIMESTAMPS,false);

		//忽略空Bean对象转json的错误，将空对象转json出来的字符串也为空
		objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);

		//所有的日期格式统一为这个标准格式"yyyy-MM-dd HH:mm:ss";
		objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));


		//反序列化的一些配置
		//忽略在json字符串中存在但是在需要反序列的java对象中不存在对应属性的情况，防止错误
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_NULL_FOR_PRIMITIVES,false);
	}

	//对象转字符串
	public static <T> String obj2String(T obj){
		if (obj == null){
			return null;
		}
		try {
			return obj instanceof String ? (String) obj:objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			log.warn("Parse object to string error",e);
			return null;
		}
	}

	//对象转出一个格式化好的字符串
	public static <T> String obj2StringPretty(T obj){
		if (obj == null){
			return null;
		}
		try {
			return obj instanceof String ? (String) obj:objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (Exception e) {
			log.warn("Parse object to string error",e);
			return null;
		}
	}

	public static <T> T string2Obj(String str,Class<T> clazz){
		if (StringUtils.isEmpty(str) || clazz==null){
			return null;
		}
		try {
			return clazz.equals(String.class)? (T) str : objectMapper.readValue(str,clazz);
		} catch (IOException e) {
			log.warn("Object to parse error",e);
			return null;
		}
	}

	public static <T> T string2Obj(String str, TypeReference<T> typeReference){
		if (StringUtils.isEmpty(str) || typeReference==null){
			return null;
		}
		try {
			return (T)(typeReference.getType().equals(String.class) ? str : objectMapper.readValue(str,typeReference));
		} catch (IOException e) {
			log.warn("Object to parse error",e);
			return null;
		}
	}

	//一些复杂对象。list和set，map等都可以反序列化
	public static <T> T string2Obj(String str, Class<?> collectionClass,Class<?>... elementClasses){
		JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClasses);
		try {
			return objectMapper.readValue(str,javaType);
		} catch (IOException e) {
			log.warn("Object to parse error",e);
			return null;
		}
	}


}
