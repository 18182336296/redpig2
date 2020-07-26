package com.redpigmall.manage.admin.action.console;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.StoreStat;
import com.redpigmall.domain.SystemTip;
/**
 * 
 * <p>
 * Title: RedPigSuperManageAction.java
 * </p>
 * 
 * <p>
 * Description: 控制台，这里包含控制台的一些方法
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
 * @date 2016-5-9
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigConsoleManageAction extends BaseAction{

	/**
	 * 欢迎页面
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "欢迎页面", value = "/welcome*", rtype = "admin", rname = "欢迎页面", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping({ "/welcome" })
	public ModelAndView welcome(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/welcome.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		Properties props = System.getProperties();
		mv.addObject("os", props.getProperty("os.name"));
		mv.addObject("java_version", props.getProperty("java.version"));
		mv.addObject("shop_version", Globals.DEFAULT_SHOP_VERSION);
		mv.addObject("database_version",
				this.databaseTools.queryDatabaseVersion());
		mv.addObject("web_server_version", request.getSession(false)
				.getServletContext().getServerInfo());
		
		List<StoreStat> stats = this.redPigStoreStatService.queryPageList(RedPigMaps.newMap());
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("st_status", Integer.valueOf(0));
		List<SystemTip> sts = this.redPigSystemTipService.queryPageList(params);
		StoreStat stat = null;
		if (stats.size() > 0) {
			stat = (StoreStat) stats.get(0);
		} else {
			stat = new StoreStat();
		}
		mv.addObject("stat", stat);
		mv.addObject("sts", sts);
		return mv;
	}
	
	/**
	 * 关于我们
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "关于我们", value = "/aboutus*", rtype = "admin", rname = "关于我们", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping({ "/aboutus" })
	public ModelAndView aboutus(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/aboutus.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		return mv;
	}
}
