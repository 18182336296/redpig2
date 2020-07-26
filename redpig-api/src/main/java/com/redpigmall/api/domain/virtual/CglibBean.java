package com.redpigmall.api.domain.virtual;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.beans.BeanMap;

/**
 * 
 * <p>
 * Title: CglibBean.java
 * </p>
 * 
 * <p>
 * Description: 动态实体类,该类可以实现动态实体类的加载与使用
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
 * @author redpigmall
 * 
 * @date 2016-4-24
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("rawtypes")
public class CglibBean {
	/**
	 * 实体Object
	 */
	public Object object = null;
	/**
	 * 属性map
	 */
	public BeanMap beanMap = null;
	
	public CglibBean() {
	}

	
	public CglibBean(Map propertyMap) {
		this.object = generateBean(propertyMap);
		this.beanMap = BeanMap.create(this.object);
	}

	/**
	 * 给bean属性赋值
	 * 
	 * @param property
	 *            属性名
	 * @param value
	 *            值
	 */
	public void setValue(String property, Object value) {
		this.beanMap.put(property, value);
	}

	/**
	 * 通过属性名得到属性值
	 * 
	 * @param property
	 *            属性名
	 * @return 值
	 */
	public Object value(Object property) {
		if ((property != null) && (!property.equals(""))) {
			return this.beanMap.get(property);
		}
		return "";
	}

	/**
	 * 得到该实体bean对象
	 * 
	 * @return
	 */
	public Object getObject() {
		return this.object;
	}

	private Object generateBean(Map propertyMap) {
		BeanGenerator generator = new BeanGenerator();
		Set keySet = propertyMap.keySet();
		for (Iterator i = keySet.iterator(); i.hasNext();) {
			String key = (String) i.next();
			generator.addProperty(key, (Class) propertyMap.get(key));
		}
		return generator.create();
	}
}
