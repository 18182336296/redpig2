package com.redpigmall.manage.admin.action.base;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.Menu;
import com.redpigmall.domain.Role;
import com.redpigmall.domain.RoleGroup;
import com.redpigmall.domain.User;
/**
 * 
 * <p>
 * Title: RedPigAdminManageAction.java
 * </p>
 * 
 * <p>
 * Description:管理员
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
 * @date 2017-3-21
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigAdminManageAction extends BaseAction{
	  
	  
	  /**
	   * 管理员密码保存
	   * @param request
	   * @param response
	   * @param old_password
	   * @param password
	   * @return
	   */
	  @SecurityMapping(title="管理员密码保存", value="/admin_pws_save*", rtype="admin", rname="商城后台管理", rcode="admin_index", display=false, rgroup="设置")
	  @RequestMapping({"/admin_pws_save"})
	  public ModelAndView admin_pws_save(HttpServletRequest request, HttpServletResponse response, 
			  String old_password, String password) {
	    ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html", 
	      this.redPigSysConfigService.getSysConfig(), 
	      this.redPigUserConfigService.getUserConfig(), 0, request, response);
	    
	    User user = this.redPigUserService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
	    
	    if (Md5Encrypt.md5(old_password).toLowerCase().equals(user.getPassword())) {
	      user.setPassword(Md5Encrypt.md5(password).toLowerCase());
	      this.redPigUserService.updateById(user);
	      mv.addObject("op_title", "修改密码成功");
	    }else{
	      mv = new RedPigJModelAndView("admin/blue/error.html", 
	        this.redPigSysConfigService.getSysConfig(), 
	        this.redPigUserConfigService.getUserConfig(), 0, request, 
	        response);
	      mv.addObject("op_title", "原密码错误");
	    }
	    mv.addObject("list_url", CommUtil.getURL(request) + "/admin_pws");
	    return mv;
	  }
	  
	  
	  /**
	   * 管理员修改密码
	   * @param request
	   * @param response
	   * @return
	   */
	  @SecurityMapping(title="管理员修改密码", value="/admin_pws*", rtype="admin", rname="商城后台管理", rcode="admin_index", display=false, rgroup="设置")
	  @RequestMapping({"/admin_pws"})
	  public ModelAndView admin_pws(HttpServletRequest request, HttpServletResponse response){
	    ModelAndView mv = new RedPigJModelAndView("admin/blue/admin_pws.html", 
	      this.redPigSysConfigService.getSysConfig(), 
	      this.redPigUserConfigService.getUserConfig(), 0, request, response);
	      mv.addObject("user", this.redPigUserConfigService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId()));
	    return mv;
	  }
	  
	  /**
	   * 管理员添加
	   * @param request
	   * @param response
	   * @return
	   */
	  @SecurityMapping(title="管理员添加", value="/admin_add*", rtype="admin", rname="管理员管理", rcode="admin_manage", rgroup="设置")
	  @RequestMapping({"/admin_add"})
	  public ModelAndView admin_add(HttpServletRequest request, HttpServletResponse response){
		
	    ModelAndView mv = new RedPigJModelAndView("admin/blue/admin_add.html", 
	      this.redPigSysConfigService.getSysConfig(), 
	      this.redPigUserConfigService.getUserConfig(), 0, request, response);
	    Map<String,Object> params = Maps.newHashMap();
	    params.put("type", "ADMIN");
	    
		List<RoleGroup> rgs = this.redPigRoleGroupService.queryPageList(params);
	    mv.addObject("rgs", rgs);
	    mv.addObject("op", "admin_add");
	    params.clear();
	    params.put("parent_id", -1);
	    params.put("type", "ADMIN");
	    
	    params.put("orderBy", "sequence");
	    params.put("orderType", "asc");
	    List<Menu> menus = this.menuService.queryPageList(params);
	    
	    mv.addObject("menus", menus);
	    
	    User user = SecurityUserHolder.getCurrentUser();
	    if(user!=null && user.getId() !=null){
	    	List<Menu> umenus = this.redPigUserService.selectByPrimaryKey(user.getId()).getMenus();
	    	mv.addObject("umenus", umenus);
	    }
	    
	    return mv;
	  }
	  
	  /**
	   * 管理员编辑
	   * @param request
	   * @param response
	   * @param id
	   * @param op
	   * @return
	   */
	  @SecurityMapping(title="管理员编辑", value="/admin_edit*", rtype="admin", rname="管理员管理", rcode="admin_manage", rgroup="设置")
	  @RequestMapping({"/admin_edit"})
	  public ModelAndView admin_edit(HttpServletRequest request, HttpServletResponse response, String id, String op) {
	    ModelAndView mv = new RedPigJModelAndView("admin/blue/admin_add.html", 
	    	  this.redPigSysConfigService.getSysConfig(), 
	  	      this.redPigUserConfigService.getUserConfig(), 0, request, response);
	    Map<String,Object> params = Maps.newHashMap();
	    params.put("type", "ADMIN");
	    List<RoleGroup> rgs = this.redPigRoleGroupService.queryPageList(params);
	    
	    if ( StringUtils.isNotBlank(id) ) {
	      User user = this.redPigUserService.selectByPrimaryKey(Long.valueOf(id));
	      mv.addObject("obj", user);
	    }
	    
	    params.put("parent_id", -1);
	    params.put("orderBy", "sequence");
	    params.put("orderType", "asc");
	    List<Menu> menus = this.menuService.queryPageList(params);
	    
	    mv.addObject("menus", menus);
	    
	    User user = SecurityUserHolder.getCurrentUser();
	    if(user!=null && user.getId() !=null){
	    	List<Menu> umenus = this.redPigUserService.selectByPrimaryKey(user.getId()).getMenus();
	    	mv.addObject("umenus", umenus);
	    }
	    
	    mv.addObject("rgs", rgs);
	    mv.addObject("op", op);
	    return mv;
	  }
	  

	  /**
	   * @param request
	   * @param response
	   * @param id 用户ID
	   * @param menu_ids 角色ID
	   * @param list_url 
	   * @param add_url 
	   * @param userName 用户名
	   * @param password 密码
	   * @param new_password 新密码
	   * @return
	   */
	  @SecurityMapping(title="管理员保存", value="/admin_save*", rtype="admin", rname="管理员管理", rcode="admin_manage", rgroup="设置")
	  @RequestMapping({"/admin_save"})
	  public ModelAndView admin_save(
			  HttpServletRequest request, 
			  HttpServletResponse response, 
			  String id, String menu_ids, 
			  String list_url, String add_url, 
			  String userName, String password, 
			  String new_password){
		
	    User user = null;
	    if (id.equals("")){
	      user = (User)WebForm.toPo(request, User.class);
	      user.setAddTime(new Date());
	      
	      if ((userName != null) && (!userName.equals(""))) {
	        user.setUserName(userName);
	      }
	      
	      if (CommUtil.null2String(password).equals("")) {
	        user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
	      } else {
	        user.setPassword(Md5Encrypt.md5(password).toLowerCase());
	      }
	      
	    } else {
	      User u = this.redPigUserService.selectByPrimaryKey(CommUtil.null2Long(id));
	      user = (User)WebForm.toPo(request, u);
	      if (!CommUtil.null2String(new_password).equals("")) {
	        user.setPassword(Md5Encrypt.md5(new_password).toLowerCase());
	      }
	      
	    }
	    
	    //如果是编辑管理员账号,先删除所有关联菜单在进行添加
	    if(StringUtils.isNotBlank(id)){
    	  
    	  Long userId = user.getId();
    	  
    	  redPigUserService.deleteUserMenu(userId);
    	  user.getMenus().clear();
    	  
	    }
	    
	    //将传过来的菜单按照逗号分隔成String数组添加到User上
	    String[] mids = menu_ids.split(",");
	    for(String mid:mids) {
	    	if (!mid.equals("")) {
	    		Menu menu = this.menuService.selectByPrimaryKey(Long.parseLong(mid));
	    		user.getMenus().add(menu);
	    		//如果没有添加父类,需要加上,下面的批量保存会过滤掉重复值,并且user_menu表示联合主键不会重复
	    		if(menu.getParent()!=null){
	    			user.getMenus().add(menu.getParent());
	    		}
	    	}
	    }
	    
	    if (id.equals("")) { //保存用户
	      this.redPigUserService.save(user);
	      this.redPigUserService.saveUserMenu(user.getId(),user.getMenus());
	      
	    } else {//编辑用户
	      this.redPigUserService.updateById(user);
	      
	      this.redPigUserService.saveUserMenu(user.getId(),user.getMenus());
	    }
	    //跳转到成功页面
	    ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html", 
	      this.redPigSysConfigService.getSysConfig(), 
	      this.redPigUserConfigService.getUserConfig(), 0, request, response);
	    mv.addObject("list_url", list_url);
	    mv.addObject("op_title", "保存管理员成功");
	    if (add_url != null) {
	      mv.addObject("add_url", add_url);
	    }
	    return mv;
	  }
	  
	  
	  /**
	   * 
	   * @param request
	   * @param response
	   * @param id 用户ID
	   * @param role_ids 角色ID
	   * @param list_url 
	   * @param add_url 
	   * @param userName 用户名
	   * @param password 密码
	   * @param new_password 新密码
	   * @return
	   */
	  @SecurityMapping(title="管理员保存", value="/admin_save*", rtype="admin", rname="管理员管理", rcode="admin_manage", rgroup="设置")
	  @RequestMapping({"/admin_save2"})
	  public ModelAndView admin_save2(HttpServletRequest request, HttpServletResponse response, 
			  String id, String role_ids, String list_url, String add_url, 
			  String userName, String password, String new_password){

	    User user = null;
	    if (id.equals("")){
	      user = (User)WebForm.toPo(request, User.class);
	      user.setAddTime(new Date());
	      
	      if ((userName != null) && (!userName.equals(""))) {
	        user.setUserName(userName);
	      }
	      
	      if (CommUtil.null2String(password).equals("")) {
	        user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
	      } else {
	        user.setPassword(Md5Encrypt.md5(password).toLowerCase());
	      }
	      
	    } else {
	      User u = this.redPigUserService.selectByPrimaryKey(CommUtil.null2Long(id));
	      user = (User)WebForm.toPo(request, u);
	      if (!CommUtil.null2String(new_password).equals("")) {
	        user.setPassword(Md5Encrypt.md5(new_password).toLowerCase());
	      }
	      
	    }
	    
	    Boolean ret = Boolean.valueOf((!id.equals("")) && (CommUtil.null2String(new_password).equals("")));
	    if ((id.equals("")) || (ret.booleanValue()))
	    {
	      //如果是编辑管理员账号,先删除所有角色在进行添加
	      if(StringUtils.isNotBlank(id)){
	    	  
	    	  Long userId = user.getId();
	    	  
	    	  redPigUserService.deleteUserRole(userId,user.getRoles());
	    	  
	    	  user.getRoles().clear();
	      }
	      
	      if (user.getUserRole().equalsIgnoreCase("ADMIN")) {
	        Map<String,Object> params = Maps.newHashMap();
	        params.put("display", Boolean.valueOf(false));
	        params.put("type", "ADMIN");
	        params.put("type1", "BUYER");
	        List<Role> roles = this.redPigRoleService.queryPageListByDisplayAndType(params);
	        user.getRoles().addAll(roles);
	      }
	      
	      //将传过来的角色按照逗号分隔成String数组添加到User上
	      String[] rids = role_ids.split(",");
	      for(String rid:rids) {
	        if (!rid.equals("")) {
	          Role role = this.redPigRoleService.selectByPrimaryKey(CommUtil.null2Long(rid));
	          user.getRoles().add(role);
	        }
	      }
	      
	    }
	    
	    if (id.equals("")) { //保存用户
	      this.redPigUserService.save(user);
	      this.redPigUserService.saveUserRole(user.getId(),user.getRoles());
	    
	    } else {//编辑用户
	      this.redPigUserService.updateById(user);
	      this.redPigUserService.deleteUserRole(user.getId(),user.getRoles());
	      this.redPigUserService.saveUserRole(user.getId(),user.getRoles());
	    }
	    //跳转到成功页面
	    ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html", 
	      this.redPigSysConfigService.getSysConfig(), 
	      this.redPigUserConfigService.getUserConfig(), 0, request, response);
	    mv.addObject("list_url", list_url);
	    mv.addObject("op_title", "保存管理员成功");
	    if (add_url != null) {
	      mv.addObject("add_url", add_url);
	    }
	    return mv;
	  }
	  
}
