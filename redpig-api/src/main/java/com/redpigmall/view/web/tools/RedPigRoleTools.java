package com.redpigmall.view.web.tools;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.Lists;
import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.service.RedPigResService;
import com.redpigmall.service.RedPigRoleGroupService;



/**
 * 菜单权限处理
 * 
 * @author redpig
 * 
 */
@Component
public class RedPigRoleTools {
	
	public static List<String> groupNames = Lists.newArrayList();
	
	@Autowired
	private RedPigRoleGroupService roleGroupService;
	
	@Autowired
	private RedPigResService resService;
	
	/**
	 * 判断是否有这个权限组
	 * @param groupName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean hasGroup(String groupName){
		
		List<String> res = Lists.newArrayList();
		
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		
		if (RequestContextHolder.getRequestAttributes() != null) {
			
			res = (List<String>) request.getSession().getAttribute(Globals.USER_ROLE_GROUP);
		}
		
		if(res == null || res.isEmpty()){
			
			res = this.roleGroupService.queryGroupNameLists(SecurityUserHolder.getCurrentUser().getId());
			
			request.getSession().setAttribute(Globals.USER_ROLE_GROUP, res);
		}
		
		return res.contains(groupName);
	}
	
	/**
	 * 判断用户是否拥有此菜单
	 * @param url
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean hasMenu(String url){
		List<String> res = Lists.newArrayList();
		
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		
		if (RequestContextHolder.getRequestAttributes() != null) {
			
			res = (List<String>) request.getSession().getAttribute(Globals.USER_RES);
		}
		
		if(res == null || res.isEmpty()){
			
			res = this.resService.queryResLists(SecurityUserHolder.getCurrentUser().getId());
			
			request.getSession().setAttribute(Globals.USER_RES, res);
		}
		
		
		return res.contains(url);
		
	}
	
}
