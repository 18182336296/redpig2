package com.redpigmall.module.weixin.view.action;

import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.module.weixin.view.action.base.BaseAction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


/**
 * 
 * <p>
 * Title: RedPigWapGroupViewAction.java
 * </p>
 * 
 * <p>
 * Description:认证管理控制器
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
 * @date 2018-9-11
 * 
 * @version redpigmall_b2b2c v8.0 2018版
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigWeixinCertificationViewAction extends BaseAction{

	/**
	 * 列出秒杀的所有商品
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping({ "/certification/index" })
	public ModelAndView nuke(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("weixin/certification/certification.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		String url = this.configService.getSysConfig().getAddress();

		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}

		String params = "";

		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);

		/*IPageList pList = this.nukeGoodsService.list(maps);*/
		/*CommUtil.saveIPageList2ModelAndView(url + "/nuke/nuke_list.html", "",params, pList, mv);*/
		return mv;
	}

}
