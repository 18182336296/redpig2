package com.redpigmall.manage.seller.tools;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.domain.User;
import com.redpigmall.service.RedPigUserService;

/**
 * 
 * <p>
 * Title: RedPigSellerAspect.java
 * </p>
 * 
 * <p>
 * Description: 商家相关操作的前切面，当店铺到期关闭时只可使用“交易管理”
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
 * @date 2014-12-30
 * 
 * @version redpigmall_b2b2c_2015
 */
@Aspect
@Component
public class RedPigSellerAspect {
	@Autowired
	private RedPigUserService userService;
	private static final String AOP = "execution(* com.redpigmall.manage.seller.action..*.*(..))&&args(request,response,..)";
	private static final String URL = "order_ship,order_confirm,order,ship_address,group_code,transport_list,ecc_set,error,index";

	@Before(AOP)
	public void seller_aspect(JoinPoint joinPoint, HttpServletRequest request,
			HttpServletResponse response) {
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			
			user = user.getParent() == null ? user : user.getParent();
			int store_status = user.getStore() == null ? 0 : user.getStore()
					.getStore_status();
			if (store_status != 15) {
				String[] urls = URL
						.split(",");
				Boolean ret = Boolean.valueOf(false);
				for (String url : urls) {
					if (joinPoint.getSignature().getName().equals(url)) {
						ret = Boolean.valueOf(true);
						break;
					}
				}
				if (!ret.booleanValue()) {
					request.getSession().setAttribute("op_title", "该操作不能执行");
					request.getSession().setAttribute("url", "index");
					try {
						response.sendRedirect("error");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
