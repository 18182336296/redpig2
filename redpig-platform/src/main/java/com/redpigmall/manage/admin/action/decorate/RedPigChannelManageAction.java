package com.redpigmall.manage.admin.action.decorate;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Advert;
import com.redpigmall.domain.AdvertPosition;
import com.redpigmall.domain.Channel;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;

/**
 * 
 * <p>
 * Title: ChannelManageAction.java
 * </p>
 * 
 * <p>
 * Description:频道管理类
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
 * @date 2014-5-21
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "unchecked", "rawtypes"})
@Controller
public class RedPigChannelManageAction  extends BaseAction{
	
	/**
	 * 频道列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "频道列表", value = "/channel_list*", rtype = "admin", rname = "频道管理", rcode = "channel", rgroup = "装修")
	@RequestMapping({ "/channel_list" })
	public ModelAndView channel_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/channel_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "ch_sequence", "asc");

		IPageList pList = this.channelService.queryPagesWithNoRelations(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/channel_list.html",
				"", params, pList, mv);
		return mv;
	}
	
	/**
	 * 频道新增
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "频道新增", value = "/channel_add*", rtype = "admin", rname = "频道管理", rcode = "channel", rgroup = "装修")
	@RequestMapping({ "/channel_add" })
	public ModelAndView channel_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/channel_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 频道编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "频道编辑", value = "/channel_edit*", rtype = "admin", rname = "频道管理", rcode = "channel", rgroup = "装修")
	@RequestMapping({ "/channel_edit" })
	public ModelAndView channel_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/channel_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			Channel channel = this.channelService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			mv.addObject("obj", channel);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 频道保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param list_url
	 * @param add_url
	 * @return
	 */
	@SecurityMapping(title = "频道保存", value = "/channel_save*", rtype = "admin", rname = "频道管理", rcode = "channel", rgroup = "装修")
	@RequestMapping({ "/channel_save" })
	public ModelAndView channel_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String list_url, String add_url) {
		
		Channel channel = null;
		if (id.equals("")) {
			channel = (Channel) WebForm.toPo(request, Channel.class);
			channel.setAddTime(new Date());
		} else {
			Channel obj = this.channelService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			channel = (Channel) WebForm.toPo(request, obj);
		}
		if (id.equals("")) {
			this.channelService.saveEntity(channel);
		} else {
			this.channelService.updateById(channel);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存频道成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}
	
	/**
	 * 频道删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "频道删除", value = "/channel_del*", rtype = "admin", rname = "频道管理", rcode = "channel", rgroup = "装修")
	@RequestMapping({ "/channel_del" })
	public String channel_deleteById(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		
		this.channelService.batchDeleteByIds(RedPigCommonUtil.getListByArr(ids));
		
		return "redirect:channel_list?currentPage=" + currentPage;
	}
	
	/**
	 * 频道AJAX更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	
	@SecurityMapping(title = "频道AJAX更新", value = "/channel_ajax*", rtype = "admin", rname = "频道管理", rcode = "channel", rgroup = "装修")
	@RequestMapping({ "/channel_ajax" })
	public void channel_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Channel obj = this.channelService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
		Field[] fields = Channel.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		this.channelService.updateById(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 频道导航模板管理
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "频道导航模板管理", value = "/channel_template*", rtype = "admin", rname = "频道管理", rcode = "channel", rgroup = "装修")
	@RequestMapping({ "/channel_template" })
	public ModelAndView template(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = null;
		if ((id != null) && (!"".equals(id))) {
			Channel ch = this.channelService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (ch != null) {
				if ("blue".equals(ch.getCh_nav_style())) {
					mv = new RedPigJModelAndView(
							"admin/blue/channel_style_blue.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
				} else if ("brown".equals(ch.getCh_nav_style())) {
					mv = new RedPigJModelAndView(
							"admin/blue/channel_style_brown.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
				} else {
					mv = new RedPigJModelAndView(
							"admin/blue/channel_style_green.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
				}
				mv.addObject("obj", ch);
				mv.addObject("gf_tools", this.gf_tools);
				mv.addObject("ch_tools", this.ch_tools);
			} else {
				mv = new RedPigJModelAndView("admin/blue/tip.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_tip", "不存在该频道");
				mv.addObject("list_url", "/channel_list");
			}
		}
		return mv;
	}
	
	/**
	 * 频道模板导航信息编辑管理
	 * @param request
	 * @param response
	 * @param id
	 * @param seq
	 * @param style
	 * @return
	 */
	@SecurityMapping(title = "频道模板导航信息编辑管理", value = "/channel_style*", rtype = "admin", rname = "频道管理", rcode = "channel", rgroup = "装修")
	@RequestMapping({ "/channel_style" })
	public ModelAndView channel_style(HttpServletRequest request,
			HttpServletResponse response, String id, String seq, String style) {
		ModelAndView mv = null;
		
		Map<String,Object> maps = Maps.newHashMap();
        maps.put("parent", -1);
        maps.put("orderBy", "sequence");
        maps.put("orderType", "asc");


		List<GoodsClass> gcs = this.goodsClassService.queryPageList(maps);
		
		maps.clear();
		maps.put("ap_type", "img");
		
		List<AdvertPosition> aps_img = this.advertPositionService.queryPageList(maps);
		
		maps.clear();
		List<AdvertPosition> aps = this.advertPositionService.queryPageList(maps);
		
		if ((id != null) && (!"".equals(id))) {
			Channel ch = this.channelService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (ch != null) {
				if ("1".equals(style)) {
					mv = new RedPigJModelAndView("admin/blue/channel_style1.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
				} else if ("2".equals(style)) {
					mv = new RedPigJModelAndView("admin/blue/channel_style2.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
				} else if ("3".equals(style)) {
					mv = new RedPigJModelAndView("admin/blue/channel_style3.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
				} else if ("4".equals(style)) {
					mv = new RedPigJModelAndView("admin/blue/channel_style4.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
				} else if ("5".equals(style)) {
					mv = new RedPigJModelAndView("admin/blue/channel_style5.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
				}
				mv.addObject("seq", seq);
				mv.addObject("style", style);
				mv.addObject("obj", ch);
				mv.addObject("gcs", gcs);
				mv.addObject("ch_tools", this.ch_tools);
				mv.addObject("aps_img", aps_img);
				mv.addObject("aps", aps);
			} else {
				mv = new RedPigJModelAndView("admin/blue/tip.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_tip", "不存在该频道");
				mv.addObject("list_url", "/channel_list");
			}
		}
		return mv;
	}
	
	/**
	 * 频道模板导航信息保存管理
	 * @param request
	 * @param response
	 * @param id
	 * @param seq
	 * @param type
	 * @param name
	 * @param name_href
	 * @param adv_type
	 * @param adv_id
	 * @param ad_id
	 * @param adv_url
	 * @param advp_id
	 * @param goods
	 * @return
	 */
	@SecurityMapping(title = "频道模板导航信息保存管理", value = "/channel_style_save_on*", rtype = "admin", rname = "频道管理", rcode = "channel", rgroup = "装修")
	@RequestMapping({ "/channel_style_save_on" })
	public String channel_style_save_on(HttpServletRequest request,
			HttpServletResponse response, String id, String seq, String type,
			String name, String name_href, String adv_type, String adv_id,
			String ad_id, String adv_url, String advp_id, String goods) {
		Channel ch = null;
		int count = 0;
		if ((id != null) && (!"".equals(id))) {
			ch = this.channelService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (ch != null) {
				List<Map> list = Lists.newArrayList();
				Map<String, Object> map = Maps.newHashMap();
				if ("1".equals(type)) {
					if ((name != null) && (!"".equals(name))
							&& (name_href != null) && (!"".equals(name_href))) {
						if ((ch.getCh_nav_word_list() != null)
								&& (!"".equals(ch.getCh_nav_word_list()))) {
							list = JSON.parseArray(ch.getCh_nav_word_list(),
									Map.class);
							if (list.size() > 0) {
								for (Map m : list) {
									if (seq.equals(CommUtil.null2String(m
											.get("seq")))) {
										list.remove(m);
										extract_on(seq, name, name_href, list,
												map);
										count = 1;
										break;
									}
								}
								if (count == 0) {
									extract_on(seq, name, name_href, list, map);
								}
							} else {
								extract_on(seq, name, name_href, list, map);
							}
						} else {
							extract_on(seq, name, name_href, list, map);
						}
						ch.setCh_nav_word_list(JSON.toJSONString(list));
					}
				} else if ("3".equals(type)) {
					if ((advp_id != null) && (!"".equals(advp_id))) {
						ch.setCh_nav_advp_id(CommUtil.null2Long(advp_id));
					}
				} else if ("4".equals(type)) {
					if (ch.getCh_nav_right_img_list() != null) {
						list = JSON.parseArray(ch.getCh_nav_right_img_list(),
								Map.class);
						if (list.size() > 0) {
							for (Map m : list) {
								if (seq.equals(CommUtil.null2String(m
										.get("seq")))) {
									map = m;
									extract_ad_info(request, seq, adv_type,
											adv_id, list, map, adv_url, goods);
									count = 1;
									break;
								}
							}
							if (count == 0) {
								extract_ad_info(request, seq, adv_type, adv_id,
										list, map, adv_url, goods);
							}
						} else {
							extract_ad_info(request, seq, adv_type, adv_id,
									list, map, adv_url, goods);
						}
					} else {
						extract_ad_info(request, seq, adv_type, adv_id, list,
								map, adv_url, goods);
					}
					ch.setCh_nav_right_img_list(JSON.toJSONString(list));
				} else if ("5".equals(type)) {
					if (ch.getCh_nav_bottom_adv_list() != null) {
						list = JSON.parseArray(ch.getCh_nav_bottom_adv_list(),
								Map.class);
						if (list.size() > 0) {
							for (Map m : list) {
								if (seq.equals(CommUtil.null2String(m
										.get("seq")))) {
									map = m;
									extract_ad_info(request, seq, adv_type,
											adv_id, list, map, adv_url, goods);
									count = 1;
									break;
								}
							}
							if (count == 0) {
								extract_ad_info(request, seq, adv_type, adv_id,
										list, map, adv_url, goods);
							}
						} else {
							extract_ad_info(request, seq, adv_type, adv_id,
									list, map, adv_url, goods);
						}
					} else {
						extract_ad_info(request, seq, adv_type, adv_id, list,
								map, adv_url, goods);
					}
					ch.setCh_nav_bottom_adv_list(JSON.toJSONString(list));
				}
				this.channelService.updateById(ch);
			}
		}
		return "redirect:/channel_template?id=" + id;
	}

	private void extract_ad_info(HttpServletRequest request, String seq,
			String adv_type, String adv_id, List<Map> list, Map map,
			String adv_url, String goods_id) {
		if ("img".equals(adv_type)) {
			String uploadFilePath = this.configService.getSysConfig()
					.getUploadFilePath();
			String saveFilePathName = request.getSession().getServletContext()
					.getRealPath("/")
					+ uploadFilePath + File.separator + "channel";
			Map json_map = Maps.newHashMap();
			map.put("href", adv_url);
			try {
				json_map = CommUtil.saveFileToServer(request, "img",
						saveFilePathName, "", null);
				if (json_map.get("fileName") == "") {
					return;
				}
				Accessory photo = new Accessory();
				photo.setName(CommUtil.null2String(json_map.get("fileName")));
				photo.setExt(CommUtil.null2String(json_map.get("mime")));
				photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(json_map
						.get("fileSize"))));
				photo.setPath(uploadFilePath + "/channel");
				photo.setWidth(CommUtil.null2Int(json_map.get("width")));
				photo.setHeight(CommUtil.null2Int(json_map.get("height")));
				photo.setAddTime(new Date());
				this.accessoryService.saveEntity(photo);
				map.put("seq", seq);
				map.put("path", photo.getPath() + "/" + photo.getName());
				map.put("img_id", photo.getId());
				map.put("type", "img");
				list.add(map);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (adv_type.equals("adv")) {
			AdvertPosition ap = this.advertPositionService.selectByPrimaryKey(CommUtil
					.null2Long(adv_id));
			if (ap != null) {
				List<Advert> ads = ap.getAdvs();
				Iterator<Advert> e = ads.iterator();

				if (e.hasNext()) {
					Advert ad = (Advert) e.next();
					map.put("seq", seq);
					map.put("type", "adv");
					map.put("img_id", adv_id);
					if (ad != null) {
						map.put("path", ad.getAd_acc() != null ? ad.getAd_acc()
								.getPath() + "/" + ad.getAd_acc().getName()
								: "");
						map.put("href", ad.getAd_url());
					} else {
						map.put("path", "");
						map.put("href", "");
					}
					list.add(map);
				}
			}
		} else {
			Goods gs = this.goodsService.selectByPrimaryKey(CommUtil
					.null2Long(goods_id));
			map.put("seq", seq);
			map.put("type", "goods");
			map.put("goods_id", goods_id);
			if (gs != null) {
				map.put("path", gs.getGoods_main_photo() != null ? gs
						.getGoods_main_photo().getPath()
						+ "/"
						+ gs.getGoods_main_photo().getName() : "");
				map.put("href", "goods_" + goods_id + "");
				map.put("original", gs.getGoods_main_photo() != null ? gs
						.getGoods_main_photo().getPath()
						+ "/"
						+ gs.getGoods_main_photo().getName() + "_middle.jpg"
						: "");
			} else {
				map.put("path", "");
				map.put("href", "");
				map.put("original", "");
			}
			list.add(map);
		}
	}

	private void extract_on(String seq, String name, String name_href,
			List<Map> list, Map map) {
		map.put("seq", seq);
		map.put("name", name);
		map.put("href", name_href);
		list.add(map);
	}
	
	/**
	 * 频道模板导航左侧商品分类信息保存管理
	 * @param request
	 * @param response
	 * @param id
	 * @param seq
	 * @param type
	 * @param ids
	 * @param name
	 * @param name_href
	 */
	@SecurityMapping(title = "频道模板导航左侧商品分类信息保存管理", value = "/channel_style_save*", rtype = "admin", rname = "频道管理", rcode = "channel", rgroup = "装修")
	@RequestMapping({ "/channel_style_save" })
	public void channel_style_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String seq, String type,
			String ids, String name, String name_href) {
		Channel ch = null;
		int count = 0;
		if ((id != null) && (!"".equals(id))) {
			ch = this.channelService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (ch != null) {
				List<Map> list = Lists.newArrayList();
				Map<String, Object> map = Maps.newHashMap();
				if (("2".equals(type)) && (ids != null) && (!"".equals(ids))) {
					ids = ids.substring(5);
					String[] gc_ids = ids.split(",cid:");
					if ((ch.getCh_nav_gc_list() != null)
							&& (!"".equals(ch.getCh_nav_gc_list()))) {
						list = JSON.parseArray(ch.getCh_nav_gc_list(),
								Map.class);
						if (list.size() > 0) {
							for (Map m : list) {
								if (seq.equals(CommUtil.null2String(m
										.get("seq")))) {
									list.remove(m);
									extra_gc_list_left(seq, list, map, gc_ids);
									count = 1;
									break;
								}
							}
							if (count == 0) {
								extra_gc_list_left(seq, list, map, gc_ids);
							}
						} else {
							extra_gc_list_left(seq, list, map, gc_ids);
						}
					} else {
						extra_gc_list_left(seq, list, map, gc_ids);
					}
					ch.setCh_nav_gc_list(JSON.toJSONString(list));
				}
				this.channelService.updateById(ch);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(ch);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void extra_gc_list_left(String seq, List list, Map map,
			String[] gc_ids) {
		map.put("seq", seq);
		map.put("pid", gc_ids[0]);
		map.put("count", Integer.valueOf(gc_ids.length));
		if (gc_ids.length > 1) {
			for (int i = 1; i < gc_ids.length; i++) {
				map.put("gc_id_" + i, gc_ids[i]);
			}
		}
		list.add(map);
	}
	
	/**
	 * 频道导航模板商品分类加载
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param gc_id
	 * @param type
	 * @param effect
	 * @param adv_id
	 * @return
	 */
	@SecurityMapping(title = "频道导航模板商品分类加载", value = "/channel_style_load*", rtype = "admin", rname = "频道管理", rcode = "channel", rgroup = "装修")
	@RequestMapping({ "/channel_style_load" })
	public ModelAndView goods_class_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gc_id,
			String type, String effect, String adv_id) {
		ModelAndView mv = null;
		if ((type != null) && (!"".equals(type))) {
			if ("1".equals(type)) {
				mv = new RedPigJModelAndView("admin/blue/channel_style_load1.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil
						.null2Long(gc_id));
				if ((gc.getChilds() != null) && (gc.getChilds().size() > 0)) {
					mv.addObject("gcs", gc.getChilds());
				} else {
					mv.addObject("child", "child");
				}
			} else {
				mv = new RedPigJModelAndView("admin/blue/channel_style_load2.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				AdvertPosition aps = this.advertPositionService
						.selectByPrimaryKey(CommUtil.null2Long(adv_id));
				if ((aps.getAdvs() != null) && (aps.getAdvs().size() > 0)) {
					mv.addObject("ap", aps.getAdvs());
				} else {
					mv.addObject("child", "child");
				}
			}
		}
		if ((effect != null) && (!"".equals(effect)) && ("show".equals(effect))) {
			mv = new RedPigJModelAndView("admin/blue/channel_style_load1.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil
					.null2Long(gc_id));
			if (gc != null) {
				mv.addObject("show", "show");
				if (gc.getLevel() == 1) {
					mv.addObject("goodsClass", gc);
				} else {
					GoodsClass p_gc = gc.getParent();
					mv.addObject("goodsClass", p_gc);
				}
			}
		}
		mv.addObject("ch_tools", this.ch_tools);
		return mv;
	}
	
	/**
	 * 频道模板分类商品编辑
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param gc_id
	 * @param goods_name
	 * @param module_id
	 * @param activity_name
	 * @return
	 */
	@SecurityMapping(title = "频道模板分类商品编辑", value = "/channel_goods_load*", rtype = "admin", rname = "频道管理", rcode = "channel", rgroup = "装修")
	@RequestMapping({ "/channel_goods_load" })
	public ModelAndView channel_list_goods_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gc_id,
			String goods_name, String module_id, String activity_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/channel_goods_load.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,6, "addTime", "desc");

		
		if (!CommUtil.null2String(gc_id).equals("")) {
			Set<Long> ids = genericIds(this.goodsClassService
					.selectByPrimaryKey(CommUtil.null2Long(gc_id)));
			
			maps.put("gc_ids", ids);
			
		}
		if (!CommUtil.null2String(goods_name).equals("")) {
			
			maps.put("goods_name_like", goods_name);
		}
		if ((activity_name != null) && (!"".equals(activity_name))) {
			List<String> str_list = Lists.newArrayList();
			str_list.add(activity_name);
			
			this.queryTools.queryActivityGoods(maps, str_list);
		}
		
		maps.put("goods_status", 0);
		IPageList pList = this.goodsService.queryPagesWithNoRelations(maps);
		
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/channel_goods_load.html", "", "", pList, mv);
		mv.addObject("module_id", module_id);
		return mv;
	}

	private Set<Long> genericIds(GoodsClass gc) {
		Set<Long> ids = Sets.newHashSet();
		ids.add(gc.getId());
		for (GoodsClass child : gc.getChilds()) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}
	
	/**
	 * 删除导航上方文字
	 * @param request
	 * @param response
	 * @param id
	 * @param seq
	 */
	@SecurityMapping(title = "删除导航上方文字", value = "/channel_del_word_ajax*", rtype = "admin", rname = "频道管理", rcode = "channel", rgroup = "装修")
	@RequestMapping({ "/channel_del_word_ajax" })
	public void channel_del_word_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String seq) {
		if ((id != null) && (!"".equals(id))) {
			Channel ch = this.channelService.selectByPrimaryKey(CommUtil.null2Long(id));
			if ((ch != null) && (!"".equals(ch.getCh_nav_word_list()))
					&& (ch.getCh_nav_word_list() != null)) {
				List<Map> wcs = JSON.parseArray(ch.getCh_nav_word_list(), Map.class);
				if (wcs.size() > 0) {
					for (Map map : wcs) {
						if (seq.equals(CommUtil.null2String(map.get("seq")))) {
							wcs.remove(map);
						}
					}
					ch.setCh_nav_word_list(JSON.toJSONString(wcs));
				}
				this.channelService.updateById(ch);
			}
		}
	}
}
