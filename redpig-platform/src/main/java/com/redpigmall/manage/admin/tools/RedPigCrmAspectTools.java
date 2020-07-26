package com.redpigmall.manage.admin.tools;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Consult;
import com.redpigmall.domain.CustomerRelMana;
import com.redpigmall.domain.Favorite;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.service.RedPigCustomerRelManaService;


/**
 * 
 * <p>
 * Title: RedPigCrmAspectTools.java
 * </p>
 * 
 * <p>
 * Description:crm的aop
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
 * @date 2014-11-4
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("rawtypes")
@Aspect
@Component
public class RedPigCrmAspectTools {
	@Autowired
	private RedPigCustomerRelManaService customerRelManaService;
	private static final String ORDER_AOP = "execution(* com.redpigmall.service.impl.RedPigOrderFormServiceImpl.*(..))";
	private static final String CONSULT_AOP = "execution(* com.redpigmall.service.impl.RedPigConsultServiceImpl.*(..))";
	private static final String FAVORITE_AOP = "execution(* com.redpigmall.service.impl.RedPigFavoriteServiceImpl.*(..))";
	
	@After(ORDER_AOP)
	public void order_aop(JoinPoint point) {
		if ((point.getArgs().length > 0)
				&& (point.getSignature().getName().equals("save"))) {
			OrderForm of = (OrderForm) point.getArgs()[0];
			if ((of != null) && (!of.getId().toString().equals(""))
					&& (of.getOrder_cat() == 0)) {
				CustomerRelMana crm = new CustomerRelMana();
				crm.setAddTime(new Date());
				crm.setCus_type(0);
				crm.setUser_id(CommUtil.null2Long(of.getUser_id()));
				crm.setUserName(of.getUser_name());
				crm.setWhether_self(of.getOrder_form());
				if (of.getOrder_form() == 0) {
					crm.setStore_id(CommUtil.null2Long(of.getStore_id()));
				}
				List<Map> list = JSON.parseArray(of.getGoods_info(), Map.class);
				if (list.size() > 0) {
					crm.setGoods_id(CommUtil.null2Long(((Map) list.get(0)).get("goods_id")));
					crm.setGoods_name(((Map) list.get(0)).get("goods_name")
							.toString());
				}
				this.customerRelManaService.saveEntity(crm);
			}
		}
	}
	
	@After(CONSULT_AOP)
	public void consult_aop(JoinPoint point) {
		if ((point.getArgs().length > 0)
				&& (point.getSignature().getName().equals("save"))) {
			Consult c = (Consult) point.getArgs()[0];
			if ((c != null) && (!c.getId().toString().equals(""))) {
				CustomerRelMana crm = new CustomerRelMana();
				crm.setAddTime(new Date());
				crm.setCus_type(1);
				crm.setUser_id(c.getConsult_user_id());
				crm.setUserName(c.getConsult_user_name());
				crm.setWhether_self(c.getWhether_self());
				if (c.getWhether_self() == 0) {
					crm.setStore_id(c.getStore_id());
				}
				List<Map> list = JSON.parseArray(c.getGoods_info(), Map.class);
				if (list.size() > 0) {
					crm.setGoods_id(CommUtil.null2Long(((Map) list.get(0)).get("goods_id")));
					crm.setGoods_name(((Map) list.get(0)).get("goods_name").toString());
				}
				this.customerRelManaService.saveEntity(crm);
			}
		}
	}

	@After(FAVORITE_AOP)
	public void favorite_aop(JoinPoint point) {
		if ((point.getArgs().length > 0)
				&& (point.getSignature().getName().equals("save"))) {
			Favorite f = (Favorite) point.getArgs()[0];
			if ((f != null) && (!f.getId().toString().equals(""))
					&& (f.getType() == 0)) {
				CustomerRelMana crm = new CustomerRelMana();
				crm.setAddTime(new Date());
				crm.setCus_type(2);
				crm.setUser_id(f.getUser_id());
				crm.setUserName(f.getUser_name());
				if (f.getGoods_type() == 0) {
					crm.setWhether_self(1);
				} else {
					crm.setWhether_self(0);
				}
				if (f.getGoods_type() == 1) {
					crm.setStore_id(f.getGoods_store_id());
				}
				crm.setGoods_id(f.getGoods_id());
				crm.setGoods_name(f.getGoods_name());
				this.customerRelManaService.saveEntity(crm);
			}
		}
	}
}
