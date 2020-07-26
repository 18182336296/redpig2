package com.redpigmall.api.tools;

import java.beans.Transient;
import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.google.common.collect.Lists;

public class ClassTools {
	/**
	 * 根据
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> getFieldNameFromClass(Class clazz){
		
		Field[] fields = clazz.getDeclaredFields();
		List<String> names = Lists.newArrayList();
		
		for (Field field : fields) {
			String fieldName = "";
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
					&& field.getAnnotation(OneToOne.class).mappedBy().length()>0){
				fieldName = field.getName()+"_id";
			}
			
			if(field.isAnnotationPresent(OneToOne.class)
					&& field.getAnnotation(OneToOne.class).mappedBy()!=null
					&& field.getAnnotation(OneToOne.class).mappedBy().length()==0){
				continue;
			}
			
			fieldName = field.getName();
			
			names.add(fieldName);
		}
		
		
		return names;
	}
	
}
