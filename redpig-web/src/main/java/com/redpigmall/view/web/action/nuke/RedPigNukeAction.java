package com.redpigmall.view.web.action.nuke;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.domain.Nuke;
import com.redpigmall.domain.NukeGoods;
import com.redpigmall.manage.buyer.action.base.BaseAction;

/**
 * 
 * Title: RedPigNukeAction.java
 * 
 * Description:秒杀
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年8月6日 下午3:15:41
 * 
 * @version redpigmall_b2b2c v8.0 2018版  
 */
@Controller
public class RedPigNukeAction extends BaseAction {
	
	/**
	 * 
	 * nuke:商城首页秒杀部分. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @return
	 * @since JDK 1.8
	 */
	@RequestMapping({ "/nuke/index" })
	public ModelAndView nuke_index(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("nuke/nuke_index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String,Object> maps = RedPigMaps.newMap();
		
		List<Nuke> nukes = this.nukeService.queryPageList(maps,0,1);
		
		Nuke nuke = nukes.get(0);
		
		mv.addObject("nuke",nuke);
		
		int nukeTime =(int) ((nuke.getEndTime().getTime()-new Date().getTime())/1000);
		
		mv.addObject("nukeTime",nukeTime);
		
		maps.clear();
		maps.put("nuke_id", nuke.getId());
		
		List<NukeGoods> nukeGoods = this.nukeGoodsService.queryPageList(maps, 0, 8);
		
		mv.addObject("nukeGoods", nukeGoods);
		
		return mv;
	}

}

