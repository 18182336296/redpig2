package com.redpigmall.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * <p>
 * Title: Title.java
 * </p>
 * 
 * <p>
 * Description:系统自动生成模板标签，通过该标签控制POJO中的自动和html模板中的中文列名对应
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.com
 * </p>
 * 
 * @author redpigmall
 * 
 * @date 2016-4-24
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Target({ java.lang.annotation.ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Title {
	/**
	 * 
	 * @return
	 */
	public String value() default "";
}
