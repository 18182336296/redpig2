package com.redpigmall.view.web.action;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Maps;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Menu;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigInitRoleViewAction.java
 * </p>
 * 
 * <p>
 * Description: 添加URL权限,目前这个功能管理平台端页面没有,直接从地址访问
 * 
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2017-5-16
 * 
 * @version redpigmall_b2b2c v8.0 2017版
 */
@Controller
public class RedPigInitRoleViewAction  extends BaseAction {
	
	/**
	 * 新增URL权限添加到系统中
	 * @return
	 */
	@RequestMapping({ "/addRole" })
	public String addRole(){
		
		return null;
	}
	
	/**
	 * 
	 * 给所有的商家添加卖家菜单
	 * 只针对新写的URL
	 * @return
	 */
	@RequestMapping({ "/updateMenu" })
	public void updateMenu() {
		//查询所有卖家菜单
		Map<String,Object> params = Maps.newHashMap();
		params.put("parent",-1);
		params.put("type","SELLER");
		
		List<Menu> menus = this.menuService.queryPageList(params);
		
		params.clear();
		//查询所有商家
		List<Store> stores = this.storeService.queryPageList(params);
		//将卖家菜单添加到商家账户中
		for(Store store : stores){
			User user = store.getUser();
			
			this.userService.deleteUserMenu(user.getId());
			
			this.userService.saveUserMenu(user.getId(), menus);
			
		}
		
		System.out.println("success");
		
	}
	

}
