package com.redpigmall.view.web.tools;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.redpigmall.domain.Menu;
import com.redpigmall.service.RedPigMenuService;



/**
 * 菜单权限处理
 * 
 * @author redpig
 * 
 */
@Component
public class RedPigUserMenuTools {
	
	@Autowired
	private RedPigMenuService menuService;
	
	public List<Menu> getMenus(Long userId,Long menuId){
		List<Menu> menus =  this.menuService.getUserMenus(userId,menuId);
		
		return menus;
	}
	
}
