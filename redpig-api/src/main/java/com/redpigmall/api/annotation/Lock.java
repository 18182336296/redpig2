package com.redpigmall.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * <p>
 * Title: Lock.java
 * </p>
 * 
 * <p>
 * Description:使用webForm toPO方法时候，需要保护的字段使用该标签控制，避免非法用户使用自定义表单修改数据
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * </p>
 * 
 * 
 * @date 2014-4-24
 * 
 * @version redpigmall_b2b2c 1.0
 */
@Target({ java.lang.annotation.ElementType.METHOD,
		java.lang.annotation.ElementType.CONSTRUCTOR,
		java.lang.annotation.ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Lock {
}
