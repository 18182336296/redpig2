package com.redpigmall.view.web.action.index;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.cache.GenerateVelocityStatisHTML;
import com.redpigmall.domain.ArticleClass;
import com.redpigmall.domain.Channel;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.domain.GoodsCase;
import com.redpigmall.domain.GoodsFloor;
import com.redpigmall.domain.ShowClass;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;
import com.redpigmall.domain.WeixinFloor;
import com.redpigmall.view.web.action.base.BaseAction;

/**
*
* <p>
* Title: IndexViewAction.java
* </p>
*
* <p>
* Description:商城首页控制器
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
* @date 2014-4-25
*
* @version redpigmall_b2b2c v8.0 2016版
*/
@SuppressWarnings("unchecked")
@Controller
public class RedPigIndexViewAction extends BaseAction{

	/**
	 * 页面top部分
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/top" })
	public ModelAndView top(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("top.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("navTools", this.navTools);

		Map<String,Object> params = Maps.newHashMap();
		if (SecurityUserHolder.getCurrentUser() != null) {

			params.clear();
			params.put("status", 0);
			params.put("toUser_id", SecurityUserHolder.getCurrentUser().getId());
			params.put("parent", -1);

			int msgs = this.messageService.selectCount(params);

			mv.addObject("msg_size", msgs);
		}
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());

			mv.addObject("user", user);
		}
		List<GoodsCart> carts = this.goodsCartService.cart_list(request, null,
				null, null, false);
		mv.addObject("carts", carts);
		return mv;
	}

	/**
	 * 页面导航
	 * @param request
	 * @param response
	 * @param pid
	 * @return
	 */
	@RequestMapping({ "/nav" })
	public ModelAndView nav(HttpServletRequest request,
			HttpServletResponse response, String pid) {
		ModelAndView mv = new RedPigJModelAndView("nav.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String,Object> params = Maps.newHashMap();
		params.put("parent",-1);
		params.put("orderBy","sequence");
		params.put("orderType", "asc");

		List<ShowClass> sc_list = this.showclassService.queryPageList(params,0,14);

		mv.addObject("sc_list", sc_list);
		mv.addObject("navTools", this.navTools);
		mv.addObject("gcViewTools", this.gcViewTools);
		mv.addObject("showClassTools", this.showClassTools);
		return mv;
	}

	/**
	 * 新版导航
	 * @param request
	 * @param response
	 * @param pid
	 * @return
	 */
	@RequestMapping({ "/nav_nc" })
	public ModelAndView nav_nc(HttpServletRequest request,
			HttpServletResponse response, String pid) {
		ModelAndView mv = new RedPigJModelAndView("nav_nc.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String,Object> params = Maps.newHashMap();
		params.put("parent",-1);
		params.put("orderBy","sequence");
		params.put("orderType", "asc");

		List<ShowClass> sc_list = this.showclassService.queryPageList(params,0,14);

		mv.addObject("sc_list", sc_list);
		mv.addObject("navTools", this.navTools);
		mv.addObject("gcViewTools", this.gcViewTools);
		mv.addObject("showClassTools", this.showClassTools);
		return mv;
	}

	/**
	 * 置废不用
	 * @deprecated
	 */
	@RequestMapping({ "/nav_data" })
	public ModelAndView nav_data(HttpServletRequest request,
			HttpServletResponse response, String sc_id) {
		ModelAndView mv = new RedPigJModelAndView("nav_data.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		ShowClass sc = this.showclassService.selectByPrimaryKey(CommUtil.null2Long(sc_id));
		
		mv.addObject("sc", sc);
		mv.addObject("showClassTools", this.showClassTools);
		return mv;
	}

	/**
	 * 导航
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/nav1" })
	public ModelAndView nav1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("nav1.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		Map<String,Object> params = Maps.newHashMap();
		params.put("parent",-1);
		params.put("orderBy","sequence");
		params.put("orderType", "asc");

		List<ShowClass> sc_list = this.showclassService.queryPageList(params,0,14);

		mv.addObject("sc_list", sc_list);
		mv.addObject("navTools", this.navTools);
		mv.addObject("gcViewTools", this.gcViewTools);
		mv.addObject("showClassTools", this.showClassTools);
		String op = CommUtil.null2String(request.getAttribute("op"));
		String id = CommUtil.null2String(request.getAttribute("id"));
		mv.addObject("op", "/" + op + "/index");
		if (("activity".equals(op)) && (id != null) && (!id.equals(""))) {
			mv.addObject("op", "activity/index_" + id + "");
		}
		if (("subject".equals(op)) && (id != null) && (!id.equals(""))) {
			mv.addObject("op", "subject/view_" + id + "");
		}
		return mv;
	}

	/**
	 * head头部分
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/head" })
	public ModelAndView head(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("head.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String type = CommUtil.null2String(request.getAttribute("type"));
		mv.addObject("type", type.equals("") ? "goods" : type);
		if ((id != null) && (!"".equals(id))) {
			Channel channel = this.channelService.selectByPrimaryKey(CommUtil.null2Long(id));

			mv.addObject("channel", channel);
		}
		return mv;
	}

	/**
	 * 新版head
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/head_nc" })
	public ModelAndView head_nc(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("head_nc.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		return mv;
	}

	/**
	 * 新版head
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/head_static" })
	public ModelAndView head_static(HttpServletRequest request,
			HttpServletResponse response) throws Exception{

		ModelAndView mv = new RedPigJModelAndView("statics/head_static.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 2, request, response);

		String path = this.redPigSysConfigService.getSysConfig().getStatic_folder() + "statics/head_static.html";

		generate_head_static(request, response, path);

		return mv;
	}

	private void generate_head_static(HttpServletRequest request, HttpServletResponse response, String path) throws Exception{
		if(CommUtil.fileNotExist(path)){

			Map<String,Object> params = Maps.newHashMap();

			new RedPigJModelAndView(
					this.redPigSysConfigService.getSysConfig(),
					this.userConfigService.getUserConfig(), request, response, params);

			String filePath = this.redPigSysConfigService.getSysConfig().getStatic_folder() + "statics"+File.separator+"head_static.html";
			GenerateVelocityStatisHTML.generate(params, "head_static.vm", filePath);

		}
	}

	/**
	 * 异步加载
	 * @param request
	 * @param response
	 * @param id
	 * @param count
	 * @return
	 * @throws InterruptedException
	 */
	@RequestMapping({ "/floor_data" })
	public ModelAndView floor_data(HttpServletRequest request,
			HttpServletResponse response, String id, String count)
			throws InterruptedException {
		ModelAndView mv = new RedPigJModelAndView("floor_data.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GoodsFloor goodsfloor = this.goodsFloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((goodsfloor != null) && (goodsfloor.getGf_type() == 1)) {
			mv = new RedPigJModelAndView("floor_data_wide.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		mv.addObject("floor", goodsfloor);
		mv.addObject("obj", goodsfloor);
		mv.addObject("velocityCount", count);
		mv.addObject("gf_tools", this.gf_tools);
		mv.addObject("url", CommUtil.getURL(request));
		return mv;
	}


	@RequestMapping({ "/floor_data_nc" })
	public ModelAndView floor_data_nc(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mv = new RedPigJModelAndView("statics/floor_data_nc.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 2, request, response);

		String path = this.redPigSysConfigService.getSysConfig().getStatic_folder() + "statics/floor_data_nc.html";

		generate_floor_static(request, response, path);


		return mv;
	}

	/**
	 * 导航静态页
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/nav_static" })
	public ModelAndView nav_static(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mv = new RedPigJModelAndView("statics/nav_static.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 2, request, response);

		String path = this.redPigSysConfigService.getSysConfig().getStatic_folder() + "statics/nav_static.html";

		generate_nav_static(request, response, path);

		return mv;
	}

	private void generate_nav_static(HttpServletRequest request, HttpServletResponse response, String path) throws Exception{
		if(CommUtil.fileNotExist(path)){

			Map<String,Object> params = Maps.newHashMap();
			params.put("parent",-1);
			params.put("orderBy","sequence");
			params.put("orderType", "asc");

			List<ShowClass> sc_list = this.showclassService.queryPageList(params,0,14);

			params.clear();


			params.put("sc_list", sc_list);
			params.put("navTools", this.navTools);
			params.put("gcViewTools", this.gcViewTools);
			params.put("showClassTools", this.showClassTools);

			new RedPigJModelAndView(
					this.redPigSysConfigService.getSysConfig(),
					this.userConfigService.getUserConfig(), request, response, params);

			String filePath = this.redPigSysConfigService.getSysConfig().getStatic_folder() + "statics"+File.separator+"nav_static.html";

			GenerateVelocityStatisHTML.generate(params, "nav_static.vm", filePath);

		}
	}

	/**
	 * 这里提供的是删除静态页。生成静态页还是访问/index
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping({ "/generate_static_files" })
	public void generate_floor_data_nc(HttpServletRequest request,HttpServletResponse response) throws Exception {

		String path = this.redPigSysConfigService.getSysConfig().getStatic_folder() + "statics";

		if(CommUtil.fileExist(path)){
			CommUtil.deleteDirectoryChilds(path);
		}

		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;

		writer = response.getWriter();
		writer.print("success");

	}

	private void generate_floor_static(HttpServletRequest request, HttpServletResponse response, String path) throws Exception{
		if(CommUtil.fileNotExist(path)){

			Map<String,Object> params = Maps.newHashMap();

			params.put("gf_display", true);
			params.put("gf_type", 0);
			params.put("parent", -1);
			params.put("orderBy","gf_sequence");
			params.put("orderType", "asc");

			List<GoodsFloor> floors = this.goodsFloorService.queryPageList(params);

			params.clear();
			params.put("floors",  floors);

			params.put("gf_tools", this.gf_tools);
			params.put("url", this.configService.getSysConfig().getImageWebServer());

			new RedPigJModelAndView(
					this.redPigSysConfigService.getSysConfig(),
					this.userConfigService.getUserConfig(), request, response, params);

			String filePath = this.redPigSysConfigService.getSysConfig().getStatic_folder() + "statics"+File.separator+"floor_data_nc.html";

			GenerateVelocityStatisHTML.generate(params, "floor_data_nc.vm", filePath);

		}
	}

	@RequestMapping({ "/footer" })
	public ModelAndView footer(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("footer.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("navTools", this.navTools);

		Map<String,Object> map = Maps.newHashMap();

		map.put("one_type", Integer.valueOf(1));
		map.put("two_type", "bottom");
		map.put("parent", -1);
		map.put("orderBy","sequence");
		map.put("orderType", "asc");

		List<ArticleClass> asc_bottom = this.articleClassService.queryPageList(map, 0, 8);

		mv.addObject("acs", asc_bottom);
		return mv;
	}

	@RequestMapping({ "/footer_nc" })
	public ModelAndView footer_nc(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("footer_nc.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("navTools", this.navTools);

		Map<String,Object> map = Maps.newHashMap();

		map.put("one_type", Integer.valueOf(1));
		map.put("two_type", "bottom");
		map.put("parent", -1);
		map.put("orderBy","sequence");
		map.put("orderType", "asc");

		List<ArticleClass> asc_bottom = this.articleClassService.queryPageList(map, 0, 8);

		mv.addObject("acs", asc_bottom);
		return mv;
	}

	@RequestMapping({ "/footer_static" })
	public ModelAndView footer_static(HttpServletRequest request,
			HttpServletResponse response) throws  Exception{
		ModelAndView mv = new RedPigJModelAndView("statics/footer_static.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 2, request, response);

		String path = this.redPigSysConfigService.getSysConfig().getStatic_folder() + "statics/footer_static.html";

		generate_footer_static(request, response, path);
		return mv;
	}

	private void generate_footer_static(HttpServletRequest request, HttpServletResponse response, String path) throws Exception{
		if(CommUtil.fileNotExist(path)){

			Map<String,Object> params = Maps.newHashMap();

			params.put("navTools", this.navTools);


			params.put("one_type", Integer.valueOf(1));
			params.put("two_type", "bottom");
			params.put("parent", -1);
			params.put("orderBy","sequence");
			params.put("orderType", "asc");

			List<ArticleClass> asc_bottom = this.articleClassService.queryPageList(params, 0, 8);

			params.put("acs", asc_bottom);

			new RedPigJModelAndView(
					this.redPigSysConfigService.getSysConfig(),
					this.userConfigService.getUserConfig(), request, response, params);

			String filePath = this.redPigSysConfigService.getSysConfig().getStatic_folder() + "statics"+File.separator+"footer_static.html";

			GenerateVelocityStatisHTML.generate(params, "footer_static.vm", filePath);

		}
	}

	@RequestMapping({ "/footer1" })
	public ModelAndView footer1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("footer1.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("navTools", this.navTools);
		return mv;
	}

	/**
	 * 商城首页
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/index" })
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) {
		String lang = request.getParameter("lang");
		SysConfig sysConfig = this.configService.getSysConfig();
		ModelAndView mv = new RedPigJModelAndView("index.html",
                sysConfig,
				this.userConfigService.getUserConfig(), 1, request, response);
        HttpSession session = request.getSession(false);
        if(lang == null){
            session.setAttribute("lang",sysConfig.getSysPcLanguage());
        }else{
            session.setAttribute("lang",lang);
        }

		String user_agent = request.getHeader("User-Agent").toLowerCase();
		if ((user_agent.indexOf("iphone") >= 0)
				|| (user_agent.indexOf("android") >= 0)) {
			mv = new RedPigJModelAndView("weixin/index.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			goto_wap_index(request, response, mv);
			return mv;
		}

		Map<String,Object> params = Maps.newHashMap();

		params.put("one_type", 1);

		List<ArticleClass> ac_list = this.articleClassService.queryPageListWithNoRelations(params);

		if (ac_list.size() == 0) {
			this.article_Tools.change();
		}

		if (SecurityUserHolder.getCurrentUser() != null) {
			mv.addObject("user", this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId()));
		}

		List<GoodsCart> carts = this.goodsCartService.cart_list(request, null,null, null, false);

		mv.addObject("carts", carts);

		return mv;
	}

	/**
	 * 橱窗
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/goodscases" })
	public ModelAndView goodscases(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("close.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		mv.addObject("goodsCaseViewTools", this.goodsCaseViewTools);

		return mv;
	}

	/**
	 * 橱窗静态页
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/goodscases_static" })
	public ModelAndView goodscases_static(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView mv = new RedPigJModelAndView("statics/goodscases_static.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 2, request, response);

		String path = this.redPigSysConfigService.getSysConfig().getStatic_folder() + "statics/goodscases_static.html";

		generate_goodscases_static(request, response, path);
		return mv;
	}

	/**
	 * 生成橱窗静态页
	 * @param request
	 * @param response
	 * @return
	 */
	private void generate_goodscases_static(HttpServletRequest request, HttpServletResponse response, String path) throws Exception{
		if(CommUtil.fileNotExist(path)){

			Map<String,Object> params = Maps.newHashMap();

			params.put("goodsCaseViewTools",  this.goodsCaseViewTools);

			new RedPigJModelAndView(
					this.redPigSysConfigService.getSysConfig(),
					this.userConfigService.getUserConfig(), request, response, params);

			GenerateVelocityStatisHTML.generate(params, "goodscases_static.vm", path);

		}
	}

	/**
	 * 关闭系统提示
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/close" })
	public ModelAndView close(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("close.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}

	@RequestMapping({ "/404" })
	public ModelAndView error404(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("weixin/404.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String userAgent = request.getHeader("user-agent");
		mv.addObject("url", CommUtil.getURL(request) + "/wap/index");
		if ((userAgent == null) || (userAgent.indexOf("Mobile") == -1)) {
			mv = new RedPigJModelAndView("404.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		mv.addObject("navTools", this.navTools);
		return mv;
	}

	@RequestMapping({ "/500" })
	public ModelAndView error500(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("weixin/500.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String userAgent = request.getHeader("user-agent");
		mv.addObject("url", CommUtil.getURL(request) + "/wap/index");
		if ((userAgent == null) || (userAgent.indexOf("Mobile") == -1)) {
			mv = new RedPigJModelAndView("500.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		mv.addObject("navTools", this.navTools);
		return mv;
	}

	@RequestMapping({ "/goods_class" })
	public ModelAndView goods_class(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("goods_class.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String,Object> params = Maps.newHashMap();

		params.put("parent", -1);
		params.put("display", Boolean.valueOf(true));
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");

		List<ShowClass> scs = this.showclassService.queryPageList(params);

		params.put("recommend", Boolean.valueOf(true));

		List<ShowClass> recommend_scs = this.showclassService.queryPageList(params, 0, 7);

		mv.addObject("goodsViewTools", this.goodsViewTools);
		mv.addObject("showClassTools", this.showClassTools);
		mv.addObject("scs", scs);
		mv.addObject("recommend_scs", recommend_scs);
		return mv;
	}

	@RequestMapping({ "/switch_case_goods" })
	public ModelAndView switch_case_goods(HttpServletRequest request,
			HttpServletResponse response, String goods_random, String caseid) {
		ModelAndView mv = new RedPigJModelAndView("switch_case_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GoodsCase goodscase = this.goodsCaseService.selectByPrimaryKey(CommUtil.null2Long(caseid));

		List<?> list = JSON.parseArray(goodscase.getCase_content());
		List<Goods> goods_list = Lists.newArrayList();

		int length = list.size();
		int i;
		if (length > 5) {
			int begin = CommUtil.null2Int(goods_random) * 5;
			i = 0;
			while (i < 5) {
				long id = CommUtil.null2Long(list.get((begin + i) % length)).longValue();

				Map<String,Object> params = Maps.newHashMap();
				params.put("id", CommUtil.null2Long(Long.valueOf(id)));

				List<Goods> objs = this.goodsService.queryPageList(params, 0, 1);

				if (objs.size() > 0) {
					goods_list.add((Goods) objs.get(0));
				}
				i++;
			}
		} else {
			for (Object id : list) {
				Map<String,Object> params = Maps.newHashMap();
				params.put("id", CommUtil.null2Long(id));

				List<Goods> objs = this.goodsService.queryPageList(params, 0, 1);

				if (objs.size() > 0) {
					goods_list.add((Goods) objs.get(0));
				}
			}
		}
		mv.addObject("goods", goods_list);
		return mv;
	}

	@RequestMapping({ "/outline" })
	public ModelAndView outline(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("error.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "该用户在其他地点登录，您被迫下线！");
		mv.addObject("url", CommUtil.getURL(request) + "/index");
		return mv;
	}

	@SuppressWarnings("rawtypes")
	private void goto_wap_index(HttpServletRequest request,
			HttpServletResponse response, ModelAndView mv) {
		Map<String,Object> params = Maps.newHashMap();
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");

		List<WeixinFloor> list = this.weixinfloorService.queryPageList(params);

		mv.addObject("floor_list", list);
		mv.addObject("weixinIndexTools", this.weixinIndexTools);
		String weixin_navigator = this.configService.getSysConfig()
				.getWeixin_navigator();

		Map index_nav_map = Maps.newHashMap();
		Map weixin_nav_map;
		if ((weixin_navigator != null) && (!weixin_navigator.equals(""))) {
			weixin_nav_map = JSON.parseObject(weixin_navigator);
			for (Object obj : weixin_nav_map.keySet()) {
				if (obj.toString().indexOf("index") >= 0) {
					index_nav_map.put(obj, weixin_nav_map.get(obj));
				}
			}
		} else {
			weixin_nav_map = Maps.newHashMap();
		}
		mv.addObject("index_nav_map", index_nav_map);
	}
}
