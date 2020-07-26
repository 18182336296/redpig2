package com.redpigmall.api.tools;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.redpigmall.api.beans.BeanUtils;
import com.redpigmall.api.beans.BeanWrapper;

/**
 * <p>
 * Title: ObjectUtils.java
 * </p>
 * 
 * <p>
 * Description: 对象工具
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2014-4-24
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
public class ObjectUtils {

	/**
	 * 通过方法名字来获取指定Class对象的返回值 主要是获取属性值坐拥
	 * 
	 * @param obj
	 * @param clazz
	 * @param methodName
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object getValueByMethodName(Object obj, Class clazz,
			String methodName) {
		try {
			Method get = clazz.getMethod(methodName);
			Object value = get.invoke(obj);
			return value;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * @param fieldName
	 *            属性名字
	 * @param value
	 *            属性值
	 * @param obj
	 *            对象
	 * @param fields
	 *            属性
	 * @return
	 */
	public static Object setValue(String fieldName, String value, Object obj,Field[] fields) {
		try {
			BeanWrapper wrapper = new BeanWrapper(obj);
			Object val = null;
			for (Field field : fields) {
				if (field.getName().equals(fieldName)) {
					Class<?> clz = Class.forName("java.lang.String");
					if (field.getType().getName().equals("int")) {
						clz = Class.forName("java.lang.Integer");
					}
					if (field.getType().getName().equals("boolean")) {
						clz = Class.forName("java.lang.Boolean");
					}
					if (!value.equals("")) {
						val = BeanUtils.convertType(value, clz);
					} else {
						val = Boolean.valueOf(!CommUtil.null2Boolean(wrapper.getPropertyValue(fieldName)));
					}
					wrapper.setPropertyValue(fieldName, val);
				}
			}
			return val;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
