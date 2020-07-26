package com.redpigmall.module.circle.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.module.circle.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Circle;
import com.redpigmall.domain.CircleInvitation;
import com.redpigmall.domain.CircleInvitationReply;
import com.redpigmall.domain.Favorite;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;


/**
 * 
 * <p>
 * Title: RedPigCircleManageAction.java
 * </p>
 * 
 * <p>
 * Description:
 * 商城圈子控制器，用户可以申请圈子，由平台审核，审核通过后该用户成为该圈子管理员，其他用户可以进入该圈子发布帖子，帖子由圈子管理员审核，
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
 * @date 2014-11-20
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({"rawtypes","unchecked"})
@Controller
public class RedPigInvitationViewAction extends BaseAction{

	/**
	 * 帖子发布
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "帖子发布", value = "/circle/invitation_publish*", rtype = "buyer", rname = "发布帖子", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping({ "/circle/invitation_publish" })
	public ModelAndView invitation_publish(HttpServletRequest request,
			HttpServletResponse response, String cid) {
		ModelAndView mv = new RedPigJModelAndView("circle/invitation_publish.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if ((cid != null) && (!cid.equals(""))) {
			User user = SecurityUserHolder.getCurrentUser();
			SysConfig sc = this.configService.getSysConfig();
			int temp = sc.getPublish_post_limit();
			int seller_limit = sc.getPublish_seller_limit();
			int level = 0;
			Map level_map = this.IntegralViewTools.query_user_level(CommUtil.null2String(user.getId()));
			level = CommUtil.null2Int(level_map.get("level"));
			boolean limit = false;
			if (seller_limit == 0) {
				limit = true;
			} else if (user.getUserRole().equalsIgnoreCase("seller")) {
				limit = true;
			}
			if (level >= temp) {
				if (limit) {
					Circle cir = this.circleService.selectByPrimaryKey(CommUtil
							.null2Long(cid));
					String invitation_publish_session = "invitation_publish_"
							+ UUID.randomUUID();
					request.getSession(false).setAttribute(
							"invitation_publish_session",
							invitation_publish_session);
					mv.addObject("cir", cir);
					mv.addObject("invitation_publish_session",
							invitation_publish_session);

					
					Map<String,Object> maps= this.redPigQueryTools.getParams("1",5, null, null);
					maps.put("type", 0);
					maps.put("user_id", user.getId());
					
					IPageList pList = this.favoriteService.list(maps);
					CommUtil.saveIPageList2ModelAndView2(
							CommUtil.getURL(request)
									+ "/circle/invitation_items.html", "", "",
							"goods", pList, mv);
					maps= this.redPigQueryTools.getParams("1",5, null, null);
					maps.put("type", 1);
					maps.put("user_id", user.getId());
					
					
					pList = this.favoriteService.list(maps);
					CommUtil.saveIPageList2ModelAndView2(
							CommUtil.getURL(request)
									+ "/circle/invitation_store.html", "", "",
							"store", pList, mv);
				} else {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("url", "/circle/index");
					mv.addObject("op_title", "只有商家用户可以发布帖子");
				}
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", "/circle/index");
				Map op_title = this.IntegralViewTools
						.query_level(CommUtil.null2String(Integer.valueOf(sc
								.getPublish_post_limit())));
				mv.addObject("op_title", "只有" + op_title.get("name") + "可以发布帖子");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", "/circle/index");
			mv.addObject("op_title", "请求参数错误");
		}
		return mv;
	}

	/**
	 * 帖子中收藏店铺ajax加载,前端使用Ajax加载分页数据
	 */
	@SecurityMapping(title = "帖子中收藏店铺ajax加载", value = "/circle/invitation_store*", rtype = "buyer", rname = "发布帖子", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping({ "/circle/invitation_store" })
	public ModelAndView invitation_store(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("/circle/invitation_store.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,5, null, null);
		maps.put("type", 1);
		maps.put("user_id", user.getId());
		
		IPageList pList = this.favoriteService.list(maps);
		CommUtil.saveIPageList2ModelAndView2(CommUtil.getURL(request)
				+ "/circle/invitation_store.html", "", "", "store", pList, mv);
		return mv;
	}
	
	/**
	 * 帖子中收藏商品ajax加载,前端使用Ajax加载分页数据
	 */
	@SecurityMapping(title = "帖子中商品ajax加载", value = "/circle/invitation_goods*", rtype = "buyer", rname = "发布帖子", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping({ "/circle/invitation_goods" })
	public ModelAndView invitation_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("/circle/invitation_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();

		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,5, null, null);
		maps.put("type",0);
		maps.put("user_id", user.getId());
		
		IPageList pList = this.favoriteService.list(maps);
		CommUtil.saveIPageList2ModelAndView2(CommUtil.getURL(request)
				+ "/circle/invitation_items.html", "", "", "goods", pList, mv);
		return mv;
	}

	/**
	 * 帖子发布
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "帖子发布", value = "/circle/invitation_publish_save*", rtype = "buyer", rname = "发布帖子", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping({ "/circle/invitation_publish_save" })
	public ModelAndView invitation_publish_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String cid,
			String invitation_publish_session, String item_ids) {
		ModelAndView mv = new RedPigJModelAndView("circle/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("url", "/circle/index");
		mv.addObject("op_title", "帖子发布成功，请等待圈主审核");
		if ((cid != null) && (!cid.equals(""))) {
			String invitation_publish_session1 = CommUtil.null2String(request
					.getSession(false).getAttribute(
							"invitation_publish_session"));
			if (invitation_publish_session1.equals(invitation_publish_session)) {
				request.getSession(false).removeAttribute(
						"invitation_publish_session");
				Circle cir = this.circleService.selectByPrimaryKey(CommUtil
						.null2Long(cid));
				
				CircleInvitation invitation = (CircleInvitation) WebForm.toPo(
						request, CircleInvitation.class);
				invitation.setAddTime(new Date());

				cir.setInvitation_count(cir.getInvitation_count() + 1);
				this.circleService.updateById(cir);
				invitation.setCircle_id(cir.getId().longValue());
				invitation.setCircle_name(cir.getTitle());
				invitation.setUser_id(SecurityUserHolder.getCurrentUser()
						.getId().longValue());
				invitation.setUserName(SecurityUserHolder.getCurrentUser()
						.getUserName());

				String[] item_list = CommUtil.null2String(item_ids).split(",");
				List<Map> item_maps = Lists.newArrayList();
				for (String item : item_list) {
					String[] items = item.split("-");
					if (items.length == 2) {
						Map<String, Object> map = Maps.newHashMap();
						String item_url = "";
						String item_img = "";
						if (items[0].indexOf("_fav") >= 0) {
							Favorite fav = this.favoriteService
									.selectByPrimaryKey(CommUtil.null2Long(items[1]));
							if (items[0].indexOf("goods") >= 0) {
								if (CommUtil.null2String(fav.getGoods_photo())
										.equals("")) {
									item_img =

									CommUtil.getURL(request)
											+ "/"
											+ this.configService.getSysConfig()
													.getGoodsImage().getPath()
											+ "/"
											+ this.configService.getSysConfig()
													.getGoodsImage().getName();
								} else {
									item_img = CommUtil.getURL(request) + "/"
											+ fav.getGoods_photo();
								}
								if ((this.configService.getSysConfig()
										.getSecond_domain_open())
										&& (fav.getGoods_type() == 1)) {
									if (!CommUtil.null2String(
											fav.getGoods_store_second_domain())
											.equals("")) {
										item_url =

										"http://"
												+ fav.getGoods_store_second_domain()
												+ "."
												+ CommUtil
														.generic_domain(request)
												+ "/items_" + fav.getGoods_id()
												+ "";
									}
								}
								item_url =

								CommUtil.getURL(request) + "/items_"
										+ fav.getGoods_id() + "";
							}
							if (items[0].indexOf("store") >= 0) {
								if (!CommUtil.null2String(fav.getStore_photo())
										.equals("")) {
									item_img = CommUtil.getURL(request) + "/"
											+ fav.getStore_photo();
								} else {
									item_img =

									CommUtil.getURL(request)
											+ "/"
											+ this.configService.getSysConfig()
													.getStoreImage().getPath()
											+ "/"
											+ this.configService.getSysConfig()
													.getStoreImage().getName();
								}
								if (this.configService.getSysConfig()
										.getSecond_domain_open()) {
									if (!CommUtil.null2String(
											fav.getGoods_store_second_domain())
											.equals("")) {
										item_url =

										"http://"
												+ fav.getGoods_store_second_domain()
												+ "."
												+ CommUtil
														.generic_domain(request);
									}
								}
								item_url =

								CommUtil.getURL(request) + "/store_"
										+ fav.getStore_id() + "";
							}
						}
						if (items[0].indexOf("_url") >= 0) {
							if (items[0].indexOf("goods") >= 0) {
								Goods goods = this.goodsService
										.selectByPrimaryKey(CommUtil
												.null2Long(items[1]));
								if (goods.getGoods_main_photo() == null) {
									item_img =

									CommUtil.getURL(request)
											+ "/"
											+ this.configService.getSysConfig()
													.getGoodsImage().getPath()
											+ "/"
											+ this.configService.getSysConfig()
													.getGoodsImage().getName();
								} else {
									item_img =

									CommUtil.getURL(request)
											+ "/"
											+ goods.getGoods_main_photo()
													.getPath()
											+ "/"
											+ goods.getGoods_main_photo()
													.getName();
								}
								if ((this.configService.getSysConfig()
										.getSecond_domain_open())
										&& (goods.getGoods_type() == 1)) {
									if (!CommUtil.null2String(
											goods.getGoods_store()
													.getStore_second_domain())
											.equals("")) {
										item_url =

										"http://"
												+ goods.getGoods_store()
														.getStore_second_domain()
												+ "."
												+ CommUtil
														.generic_domain(request)
												+ "/items_" + goods.getId()
												+ "";
									}
								}
								item_url =

								CommUtil.getURL(request) + "/items_"
										+ goods.getId() + "";
							}
							if (items[0].indexOf("store") >= 0) {
								Store store = this.storeService
										.selectByPrimaryKey(CommUtil
												.null2Long(items[1]));
								if (!CommUtil
										.null2String(store.getStore_logo())
										.equals("")) {
									item_img =

									CommUtil.getURL(request) + "/"
											+ store.getStore_logo().getPath()
											+ "/"
											+ store.getStore_logo().getName();
								} else {
									item_img =

									CommUtil.getURL(request)
											+ "/"
											+ this.configService.getSysConfig()
													.getStoreImage().getPath()
											+ "/"
											+ this.configService.getSysConfig()
													.getStoreImage().getName();
								}
								if (this.configService.getSysConfig()
										.getSecond_domain_open()) {
									if (!CommUtil.null2String(
											store.getStore_second_domain())
											.equals("")) {
										item_url =

										"http://"
												+ store.getStore_second_domain()
												+ "."
												+ CommUtil
														.generic_domain(request);
									}
								}
								item_url =

								CommUtil.getURL(request) + "/store_"
										+ store.getId() + "";
							}
						}
						String item_id = items[1];
						map.put("item_type", items[0]);
						map.put("item_url", item_url);
						map.put("item_img", item_img);
						map.put("item_id", item_id);
						item_maps.add(map);
					}
				}
				invitation.setItem_info(JSON.toJSONString(item_maps));
				this.invitationService.saveEntity(invitation);
				mv.addObject("url",
						"/circle/invitation_detail_" + invitation.getId() + "");
				mv.addObject("op_title", "帖子发布成功");
			} else {
				mv = new RedPigJModelAndView("circle/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", "/circle/index");
				mv.addObject("op_title", "禁止表单重复提交");
			}
		} else {
			mv = new RedPigJModelAndView("circle/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", "/circle/index");
			mv.addObject("op_title", "请求参数错误");
		}
		return mv;
	}

	/**
	 * 帖子列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "帖子点赞", value = "/circle/invitation_parise*", rtype = "buyer", rname = "帖子点赞", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping({ "/circle/invitation_parise" })
	public void invitation_parise(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Map json_map = Maps.newHashMap();
		int code = 100;
		CircleInvitation obj = this.invitationService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		String uid = CommUtil.null2String(SecurityUserHolder.getCurrentUser()
				.getId());
		String ret = this.circleViewTools.generInvitationParise(id, uid);
		if (ret == "false") {
			if ((obj.getPraiseInfo() != null)
					&& (!obj.getPraiseInfo().equals(""))) {
				obj.setPraiseInfo(obj.getPraiseInfo() + uid + ",");
			} else {
				obj.setPraiseInfo("," + uid + ",");
			}
			obj.setPraise_count(obj.getPraise_count() + 1);
			this.invitationService.updateById(obj);
			json_map.put("count", Integer.valueOf(obj.getPraise_count()));
		} else {
			code = -100;
		}
		json_map.put("code", Integer.valueOf(code));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 帖子详情
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/circle/invitation_detail" })
	public ModelAndView invitation_detail(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("circle/invitation_detail.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		CircleInvitation obj = this.invitationService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		if (obj != null) {
			mv.addObject("obj", obj);
			mv.addObject("circleViewTools", this.circleViewTools);

			Map<String, Object> params = Maps.newHashMap();
			params.put("status", Integer.valueOf(5));
			params.put("orderBy", "attention_count");
			params.put("orderType", "desc");
			
			List<Circle> recommends = this.circleService.queryPageList(params, 0, 10);
			
			mv.addObject("recommends", recommends);
			
			params.clear();
			params.put("orderBy", "reply_count");
			params.put("orderType", "desc");
			
			List<CircleInvitation> hots = this.invitationService.queryPageList(params, 0, 10);
			
			mv.addObject("hots", hots);
			
			int code = 100;
			if (SecurityUserHolder.getCurrentUser() != null) {
				User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
				List<Map> map_list = Lists.newArrayList();
				if ((user.getCircle_attention_info() != null)
						&& (!user.getCircle_attention_info().equals(""))) {
					map_list = JSON.parseArray(user.getCircle_attention_info(),
							Map.class);
					for (Map temp : map_list) {
						if (CommUtil.null2Long(temp.get("id")).equals(
								Long.valueOf(obj.getCircle_id()))) {
							code = -100;
							break;
						}
					}
				}
			}
			if (obj.getItem_info() != null) {
				mv.addObject("items",
						JSON.parseArray(obj.getItem_info(), Map.class));
			}
			mv.addObject("code", Integer.valueOf(code));
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", "/circle/index");
			mv.addObject("op_title", "您所访问的帖子不存在！");
		}
		return mv;
	}

	/**
	 * 帖子回复
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "帖子回复", value = "/circle/invitation_reply_save*", rtype = "buyer", rname = "帖子回复", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping({ "/circle/invitation_reply_save" })
	public void invitation_reply_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String reply_content) {
		Map json_map = Maps.newHashMap();
		int code = 100;
		if ((id != null) && (!id.equals(""))) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			CircleInvitation obj = this.invitationService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			obj.setReply_count(obj.getReply_count() + 1);
			this.invitationService.updateById(obj);
			CircleInvitationReply reply = new CircleInvitationReply();
			reply.setAddTime(new Date());
			reply.setContent(reply_content);
			reply.setInvitation_id(obj.getId());
			reply.setUser_id(user.getId());
			reply.setUser_name(user.getUserName());
			reply.setLevel_count(obj.getReply_count());
			String photo = "";
			if (user.getPhoto() != null) {
				photo = user.getPhoto().getPath() + "/"
						+ user.getPhoto().getName();
			} else {
				photo =

				this.configService.getSysConfig().getMemberIcon().getPath()
						+ "/"
						+ this.configService.getSysConfig().getMemberIcon()
								.getName();
			}
			reply.setUser_photo(photo);
			this.invitationReplyService.saveEntity(reply);
		} else {
			code = -100;
		}
		json_map.put("code", Integer.valueOf(code));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 帖子回复列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/circle/invitation_reply" })
	public ModelAndView invitation_reply(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String owner_id) {
		ModelAndView mv = new RedPigJModelAndView("circle/invitation_reply.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		CircleInvitation inv = this.invitationService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,15, "addTime", "asc");
		maps.put("invitation_id", CommUtil.null2Long(id));
		
		if ((owner_id != null) && (!owner_id.equals(""))) {
			maps.put("user_id", CommUtil.null2Long(owner_id));
		}
		
		maps.put("parent", -1);
		
		IPageList pList = this.invitationReplyService.list(maps);
		String url = CommUtil.getURL(request) + "/circle/invitation_reply";
		mv.addObject("objs", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url, "",
				pList.getCurrentPage(), pList.getPages(), pList.getPageSize()));
		mv.addObject("inv", inv);
		return mv;
	}

	/**
	 * 帖子回复他人信息请求列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/circle/invitation_reply_others" })
	public ModelAndView invitation_reply_others(HttpServletRequest request,
			HttpServletResponse response, String pid, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"circle/invitation_reply_others.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,10, "addTime", "asc");
		maps.put("parent", CommUtil.null2Long(pid));
		
		IPageList pList = this.invitationReplyService.list(maps);
		String url = CommUtil.getURL(request)
				+ "/circle/invitation_reply_others";
		mv.addObject("objs", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url, "",
				pList.getCurrentPage(), pList.getPages(), pList.getPageSize()));
		mv.addObject("pid", pid);
		return mv;
	}

	/**
	 * 帖子回复他人信息方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "回复他人信息", value = "/circle/invitation_reply_others_save*", rtype = "buyer", rname = "帖子回复", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping({ "/circle/invitation_reply_others_save" })
	public void invitation_reply_others_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String pid, String reply_content) {
		Map json_map = Maps.newHashMap();
		int code = 100;
		if ((pid != null) && (!pid.equals(""))) {
			User Fromuser = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			CircleInvitationReply parent = this.invitationReplyService
					.selectByPrimaryKey(CommUtil.null2Long(pid));
			String userName = parent.getUser_name();
			String temp_str = "回复" + userName + ":";
			if (reply_content.indexOf(temp_str) >= 0) {
				CircleInvitationReply reply = new CircleInvitationReply();
				reply.setAddTime(new Date());
				reply.setContent(Fromuser.getUserName() + reply_content);
				reply.setUser_id(Fromuser.getId());
				reply.setUser_name(Fromuser.getUserName());
				String photo = "";
				if (Fromuser.getPhoto() != null) {
					photo = Fromuser.getPhoto().getPath() + "/"
							+ Fromuser.getPhoto().getName();
				} else {
					photo =

					this.configService.getSysConfig().getMemberIcon().getPath()
							+ "/"
							+ this.configService.getSysConfig().getMemberIcon()
									.getName();
				}
				reply.setParent_id(parent.getId());
				reply.setInvitation_id(parent.getInvitation_id());
				reply.setUser_photo(photo);
				this.invitationReplyService.saveEntity(reply);
				parent.setReply_count(parent.getReply_count() + 1);
				this.invitationReplyService.saveEntity(parent);
			} else {
				code = -100;
			}
		} else {
			code = -100;
		}
		json_map.put("code", Integer.valueOf(code));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除帖子，只有楼主可以删除
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "删除帖子", value = "/circle/invitation_reply_others_save*", rtype = "buyer", rname = "帖子回复", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping({ "/circle/invitation_delete" })
	public void invitation_deleteById(HttpServletRequest request,
			HttpServletResponse response, String id) {
		CircleInvitation obj = this.invitationService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		Long cid = Long.valueOf(obj.getCircle_id());
		boolean ret = true;
		if (obj != null) {
			if (CommUtil.null2String(Long.valueOf(obj.getUser_id())).equals(
					CommUtil.null2String(SecurityUserHolder.getCurrentUser()
							.getId()))) {
				this.invitationService.deleteById(CommUtil.null2Long(id));
				ret = true;
				if (ret) {
					Circle cir = this.circleService.selectByPrimaryKey(cid);
					cir.setInvitation_count(cir.getInvitation_count() - 1);
					this.circleService.updateById(cir);

					Map<String, Object> params = Maps.newHashMap();
					params.put("invitation_id", CommUtil.null2Long(id));
					
					List reply_ids = this.invitationReplyService.queryPageList(params);
					
					List dele_ids = Lists.newArrayList();
					for (Object temp_id : reply_ids) {
						dele_ids.add(CommUtil.null2Long(temp_id));
					}
					this.invitationReplyService.batchDeleteByIds(dele_ids);
					
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 商品URL解析，根据商品URL解析出商品信息，并以json返回到前台，适用于redpigmall_b2b2c v2015版
	 * 
	 * @param request
	 * @param response
	 * @param goods_url
	 */
	@SecurityMapping(title = "商品URL解析", value = "/circle/invitation_goods_parse*", rtype = "buyer", rname = "帖子回复", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping({ "/circle/invitation_goods_parse" })
	public void invitation_goods_parse(HttpServletRequest request,
			HttpServletResponse response, String goods_url) {
		Map<String, Object> map = Maps.newHashMap();
		int error = 1;
		String img_path = "";
		String img_url = "";
		Long id = Long.valueOf(-1L);
		Goods obj = null;
		String site_url = CommUtil.getURL(request);
		String regr1 = site_url + "/items_[0-9]+_*[a-z]*$";
		String regr2 = site_url + "/items?id=[0-9]+&*.*$";
		Pattern pattern1 = Pattern.compile(regr1);
		Pattern pattern2 = Pattern.compile(regr2);
		Matcher matcher1 = pattern1.matcher(goods_url);
		Matcher matcher2 = pattern2.matcher(goods_url);
		if (matcher1.matches()) {
			error = 0;
			if (goods_url.indexOf("_") < goods_url.lastIndexOf("_")) {
				id = CommUtil
						.null2Long(goods_url.substring(
								goods_url.indexOf("_") + 1,
								goods_url.lastIndexOf("_")));
			} else {
				id = CommUtil
						.null2Long(goods_url.substring(
								goods_url.indexOf("_") + 1,
								goods_url.lastIndexOf(".")));
			}
		}
		if (matcher2.matches()) {
			error = 0;
			if (goods_url.indexOf("&") > 0) {
				id = CommUtil.null2Long(goods_url.substring(
						goods_url.indexOf("?id=") + 4, goods_url.indexOf("&")));
			} else {
				id = CommUtil.null2Long(goods_url.substring(goods_url
						.indexOf("?id=") + 4));
			}
		}
		obj = this.goodsService.selectByPrimaryKey(id);
		if (obj != null) {
			if (obj.getGoods_main_photo() != null) {
				img_path =

				CommUtil.getURL(request) + "/"
						+ obj.getGoods_main_photo().getPath() + "/"
						+ obj.getGoods_main_photo().getName() + "_small" + "."
						+ obj.getGoods_main_photo().getExt();
			} else {
				img_path =

				CommUtil.getURL(request)
						+ "/"
						+ this.configService.getSysConfig().getGoodsImage()
								.getPath()
						+ "/"
						+ this.configService.getSysConfig().getGoodsImage()
								.getName();
			}
			img_url = site_url + "/items_" + id + "";
			if (this.configService.getSysConfig().getSecond_domain_open()) {
				if (!CommUtil.null2String(
						obj.getGoods_store().getStore_second_domain()).equals(
						"")) {
					img_url =

					"http://" + obj.getGoods_store().getStore_second_domain()
							+ "." + CommUtil.generic_domain(request)
							+ "/items_" + id + "";
				}
			}
		}
		map.put("error", Integer.valueOf(error));
		map.put("img_path", img_path);
		map.put("id", id);
		map.put("img_url", img_url);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 店铺url继续，根据店铺地址，解析出店铺信息，并以json返回到前端处理
	 * 
	 * @param request
	 * @param response
	 * @param goods_url
	 */
	@SecurityMapping(title = "店铺URL解析", value = "/circle/invitation_store_parse*", rtype = "buyer", rname = "帖子回复", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping({ "/circle/invitation_store_parse" })
	public void invitation_store_parse(HttpServletRequest request,
			HttpServletResponse response, String store_url) {
		Map<String, Object> map = Maps.newHashMap();
		int error = 1;
		String img_path = "";
		String img_url = "";
		Long id = Long.valueOf(-1L);
		Store obj = null;
		String site_url = CommUtil.getURL(request);
		String regr1 = site_url + "/store_[0-9]+$";
		String regr2 = site_url + "/store?id=[0-9]+&*.*$";
		String regr3 = "http://.*" + CommUtil.generic_domain(request);
		Pattern pattern1 = Pattern.compile(regr1);
		Pattern pattern2 = Pattern.compile(regr2);
		Pattern pattern3 = Pattern.compile(regr3);
		Matcher matcher1 = pattern1.matcher(store_url);
		Matcher matcher2 = pattern2.matcher(store_url);
		Matcher matcher3 = pattern3.matcher(store_url);
		if (matcher1.matches()) {
			error = 0;
			id = CommUtil.null2Long(store_url.substring(
					store_url.indexOf("_") + 1, store_url.indexOf(".")));

			obj = this.storeService.selectByPrimaryKey(id);
		}
		if (matcher2.matches()) {
			error = 0;
			if (store_url.indexOf("&") > 0) {
				id = CommUtil.null2Long(store_url.substring(
						store_url.indexOf("?id=") + 4, store_url.indexOf("&")));
			} else {
				id = CommUtil.null2Long(store_url.substring(
						store_url.indexOf("?id=") + 4, store_url.indexOf(".")));
			}
			obj = this.storeService.selectByPrimaryKey(id);
		}
		if (matcher3.matches()) {
			error = 0;
			String store_name = store_url.substring(7,
					store_url.indexOf(CommUtil.generic_domain(request)));
			obj = this.storeService.getObjByProperty("store_name","=",store_name);
		}
		if (obj != null) {
			if (obj.getStore_logo() != null) {
				img_path =

				CommUtil.getURL(request) + "/" + obj.getStore_logo().getPath()
						+ "/" + obj.getStore_logo().getName();
			} else {
				img_path =

				CommUtil.getURL(request)
						+ "/"
						+ this.configService.getSysConfig().getStoreImage()
								.getPath()
						+ "/"
						+ this.configService.getSysConfig().getStoreImage()
								.getName();
			}
			img_url = site_url + "/store_" + id + "";
			if (this.configService.getSysConfig().getSecond_domain_open()) {
				if (!CommUtil.null2String(obj.getStore_second_domain()).equals(
						"")) {
					img_url = "http://" + obj.getStore_second_domain() + "."
							+ CommUtil.generic_domain(request);
				}
			}
		}
		map.put("error", Integer.valueOf(error));
		map.put("img_path", img_path);
		map.put("id", id);
		map.put("img_url", img_url);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
