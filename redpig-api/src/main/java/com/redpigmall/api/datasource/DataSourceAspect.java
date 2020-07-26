package com.redpigmall.api.datasource;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * <p>
 * Title: DataSourceAspect.java
 * </p>
 * 
 * <p>
 * Description:动态切换数据源
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
 * @date 2017-4-24
 * 
 * @version redpigmall_b2b2c v8.0 2017版
 */
@SuppressWarnings("unchecked")
@Aspect
@Component
public class DataSourceAspect {
	/**
	 * 在dao层方法获取datasource对象之前，在切面中指定当前线程数据源
	 */
	@Before("execution(* com.redpigmall.service..*.*(..))")
	public void before(JoinPoint point) {
		
		String method = point.getSignature().getName();
		
		Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();//获取接口上参数
		try {
			Method mm = point.getSignature().getDeclaringType().getMethod(method, parameterTypes);
			
			//这里做判断,如果带有@Transactional注解表示是增删改-写库
			if (mm != null && mm.isAnnotationPresent(Transactional.class)) {
				HandleDataSource.putDataSource("master");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}