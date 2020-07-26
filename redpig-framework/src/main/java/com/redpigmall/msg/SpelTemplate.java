package com.redpigmall.msg;

import org.springframework.expression.ParserContext;

/**
 * 
 * <p>
 * Title: SpelTemplate.java
 * </p>
 * 
 * <p>
 * * Description:自定义SPEL模板。定义为以${#user.userName} 获取 user对象的userName。
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2015-2-27
 * 
 * @version redpigmall_b2b2c 2016
 */
public class SpelTemplate implements ParserContext {
	public String getExpressionPrefix() {
		return "${";
	}

	public String getExpressionSuffix() {
		return "}";
	}

	public boolean isTemplate() {
		return true;
	}
}
