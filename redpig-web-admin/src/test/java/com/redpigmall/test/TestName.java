package com.redpigmall.test;
/**
 * 
 * Title: TestName.java
 * 
 * Description:
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年6月24日 下午5:37:37
 * 
 * @version redpigmall_b2b2c v8.0 2018版  
 */
public class TestName {

	public static void main(String[] args) {
		String url = "http://localhost:8080/items_123";
		
		System.out.println(url.substring(url.indexOf("items")));
		
	}

}

