package com.redpigmall.api.tools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.google.common.collect.Lists;
import com.redpigmall.domain.Accessory;

/**
 * <p>
 * Title: AnnotationUtils.java
 * </p>
 * 
 * <p>
 * Description:Annotation注解类的操作工具
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
 * @date 2017-3-21
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("rawtypes")
public class AnnotationUtils {
	public static void testmai(String[] args) throws Exception {

//		tableName(Partner.class);
		
		List<String> names = getFieldNameFromClass(Accessory.class);
		
		for (String string : names) {
			System.out.println(string);
		}
		
	}

	/**
	 * 根据类名获取所有对应表字段
	 * @param clazz
	 * @return
	 */
	
	public static List<String> getFieldNameFromClass(Class clazz){
		
		Field[] fields = clazz.getDeclaredFields();
		List<String> names = Lists.newArrayList();
		
		for (Field field : fields) {
			String fieldName = field.getName();
			if(field.isAnnotationPresent(Transient.class)){
				continue;
			}
			
			if(field.isAnnotationPresent(ManyToMany.class)){
				continue;
			}

			if(field.isAnnotationPresent(ManyToOne.class)){
				fieldName = field.getName()+"_id";
			}
			
			if(field.isAnnotationPresent(OneToMany.class)){
				continue;
			}
			
			if(field.isAnnotationPresent(OneToOne.class)
					&& field.getAnnotation(OneToOne.class).mappedBy()!=null
					&& field.getAnnotation(OneToOne.class).mappedBy().length()==0){
				fieldName = field.getName()+"_id";
			}
			
			if(field.isAnnotationPresent(OneToOne.class)
					&& field.getAnnotation(OneToOne.class).mappedBy()!=null
					&& field.getAnnotation(OneToOne.class).mappedBy().trim().length()>=0){
				continue;
			}
			
			
			
			names.add(fieldName);
		}
		
		
		return names;
	}
	
	public static String tableName(Class clazz) {

		Annotation[] ans = clazz.getAnnotations();

		for (Annotation annotation : ans) {
			String an = annotation.toString();
			if (an.contains("name") && an.contains("redpigmall_")) {
				String tableName = an.substring(an.indexOf("name="));
				tableName = tableName.replace("name=", "");
				tableName = tableName.replace(")", "");
				return tableName;

			}
		}

		return null;
	}

	
	public static String getManyToManyTable(Field field)  {
		
		try {
			String mappedBy = field.getAnnotation(ManyToMany.class).mappedBy();
			
			Type genType = field.getGenericType();
			int len = genType.toString().length() - 1;
			String ofType = genType.toString().substring(15, len);
			String domainClass = ofType.substring(ofType.lastIndexOf(".") + 1);
			
			Class clazz = Class.forName("com.redpigmall.domain." + domainClass);
			
			Field fd = clazz.getDeclaredField(mappedBy);
			
			String tableName = fd.getAnnotation(JoinTable.class).name();
			
			return tableName;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	public static JoinColumn[] getManyToManyJoinColumns(Field field) throws Exception {

		String mappedBy = field.getAnnotation(ManyToMany.class).mappedBy();

		Type genType = field.getGenericType();
		int len = genType.toString().length() - 1;
		String ofType = genType.toString().substring(15, len);
		String domainClass = ofType.substring(ofType.lastIndexOf(".") + 1);

		Class clazz = Class.forName("com.redpigmall.domain."
				+ domainClass);

		Field fd = clazz.getDeclaredField(mappedBy);

		return fd.getAnnotation(JoinTable.class).joinColumns();
	}

	public static JoinColumn[] getManyToManyInverseJoinColumns(Field field)
			throws Exception {

		String mappedBy = field.getAnnotation(ManyToMany.class).mappedBy();

		Type genType = field.getGenericType();
		int len = genType.toString().length() - 1;
		String ofType = genType.toString().substring(15, len);
		String domainClass = ofType.substring(ofType.lastIndexOf(".") + 1);

		Class clazz = Class.forName("com.redpigmall.domain."
				+ domainClass);

		Field fd = clazz.getDeclaredField(mappedBy);

		return fd.getAnnotation(JoinTable.class).inverseJoinColumns();
	}

	@SuppressWarnings("unused")
	public static String getPropertyNameByField(Field field) {
		Type genType = field.getGenericType();
		int len = genType.toString().length() - 1;
		String ofType = genType.toString().substring(15, len);

		String property = field.getAnnotation(ManyToMany.class).mappedBy();
		return property;
	}

}
