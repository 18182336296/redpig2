package com.redpigmall.manage.admin.action.self.group;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Advert;
import com.redpigmall.domain.AdvertPosition;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GroupClass;
import com.redpigmall.domain.GroupIndex;
import com.redpigmall.domain.GroupLifeGoods;

/**
 * 
 * <p>
 * Title: GroupIndexManageAction.java
 * </p>
 * 
 * <p>
 * Description: 团购首页设计
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
 * @date 2014年5月27日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigGroupIndexManageAction extends BaseAction{
	/**
	 * 团购首页设计列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "团购首页设计列表", value = "/groupindex_design*", rtype = "admin", rname = "团购设置", rcode = "group", rgroup = "促销管理")
	@RequestMapping({ "/groupindex_design" })
	public ModelAndView groupIndex(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/groupindex_design_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "gid_sequence", "asc");
		
		IPageList pList = this.groupIndexService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView(url
				+ "/groupindex_design_list.html", "", params, pList, mv);
		return mv;
	}
	
	/**
	 * 团购首页设计基本信息编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购首页设计基本信息编辑", value = "/groupindex_design_edit*", rtype = "admin", rname = "团购设置", rcode = "group", rgroup = "促销管理")
	@RequestMapping({ "/groupindex_design_edit" })
	public ModelAndView groupindex_design_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/groupindex_design_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		if ((id != null) && (!id.equals(""))) {
			GroupIndex groupindex = this.groupIndexService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			mv.addObject("obj", groupindex);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 团购首页设计基本信息保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @param gid_name
	 * @param gid_default
	 * @param gid_sequence
	 * @return
	 */
	@SecurityMapping(title = "团购首页设计基本信息保存", value = "/groupindex_design_save*", rtype = "admin", rname = "团购设置", rcode = "group", rgroup = "促销管理")
	@RequestMapping({ "/groupindex_design_save" })
	public ModelAndView groupindex_design_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url, String gid_name,
			String gid_default, String gid_sequence) {
		Map<String,Object> params = Maps.newHashMap();
		params.put("deleteStatus", 0);
		if (gid_default.equals("1")) {
			List<GroupIndex> groupindexes = this.groupIndexService.queryPageList(params);
			
			for (GroupIndex gi : groupindexes) {
				gi.setGid_default(0);
				this.groupIndexService.updateById(gi);
			}
		}
		if ((id == null) || (id.equals(""))) {
			GroupIndex groupindex = new GroupIndex();
			groupindex.setAddTime(new Date());
			groupindex.setDeleteStatus(0);
			groupindex.setGid_default(CommUtil.null2Int(gid_default));
			groupindex.setGid_name(gid_name);
			groupindex.setGid_nav_advp_id(null);
			groupindex.setGid_nav_gc_list("[]");
			groupindex.setGid_nav_right_img_list("");
			groupindex.setGid_nav_word_list("[]");
			groupindex.setGid_sequence(CommUtil.null2Int(gid_sequence));
			this.groupIndexService.saveEntity(groupindex);
		} else {
			GroupIndex obj = this.groupIndexService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			obj.setGid_default(CommUtil.null2Int(gid_default));
			obj.setGid_name(gid_name);
			obj.setGid_sequence(CommUtil.null2Int(gid_sequence));
			this.groupIndexService.updateById(obj);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}
	
	/**
	 * 团购首页设计_删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购首页设计_删除", value = "/groupindex_design_del*", rtype = "admin", rname = "团购设置", rcode = "group", rgroup = "促销管理")
	@RequestMapping({ "/groupindex_design_del" })
	public String groupindex_design_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		
		this.groupIndexService.batchDeleteByIds(RedPigCommonUtil.getListByArr(ids));
		
		return "redirect:groupindex_design?currentPage=" + currentPage;
	}
	
	/**
	 * 团购首页设计
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "团购首页设计", value = "/groupindex_design_design*", rtype = "admin", rname = "团购设置", rcode = "group", rgroup = "促销管理")
	@RequestMapping({ "/groupindex_design_design" })
	public ModelAndView groupindex_design_design(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/groupindex_design_design.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GroupIndex groupindex = this.groupIndexService.selectByPrimaryKey(CommUtil
				.null2Long(id));
        Map<String,Object> maps = Maps.newHashMap();
        maps.put("gc_type", 1);
        maps.put("parent", -1);
        maps.put("orderBy", "gc_sequence");
        maps.put("orderType", "asc");
        
		List<GroupClass> gpcs = this.groupClassService.queryPageList(maps);
		
		mv.addObject("obj", groupindex);
		mv.addObject("group_tools", this.groupTools);
		mv.addObject("gcs", gpcs);
		return mv;
	}
	
	/**
	 * 团购首页设计_弹窗样式
	 * @param request
	 * @param response
	 * @param id
	 * @param seq
	 * @param style
	 * @return
	 */
	@SecurityMapping(title = "团购首页设计_弹窗样式", value = "/groupindex_style*", rtype = "admin", rname = "团购设置", rcode = "group", rgroup = "促销管理")
	@RequestMapping({ "/groupindex_style" })
	public ModelAndView groupindex_style(HttpServletRequest request,
			HttpServletResponse response, String id, String seq, String style) {
		ModelAndView mv = null;
		Map<String,Object> maps = Maps.newHashMap();
        maps.put("parent", -1);
        
        maps.put("orderBy", "sequence");
        maps.put("orderType", "asc");
        
		List<GoodsClass> gcs = this.goodsClassService.queryPageList(maps);
		
		maps.clear();
		maps.put("gc_type", 1);
		maps.put("parent", -1);
		maps.put("orderBy", "gc_sequence");
		maps.put("orderType", "asc");
		
		List<GroupClass> gpcs = this.groupClassService.queryPageList(maps);
		
		maps.clear();
		maps.put("ap_type", "img");
		maps.put("parent", -1);
		
		List<AdvertPosition> aps_img = this.advertPositionService.queryPageList(maps);
		
		maps.clear();
		maps.put("ap_width", 700);
		maps.put("ap_height", 280);
		
		List aps = this.advertPositionService.queryPageList(maps);
		
		if ((id != null) && (!"".equals(id))) {
			GroupIndex gi = this.groupIndexService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (gi != null) {
				if ("1".equals(style)) {
					mv = new RedPigJModelAndView("admin/blue/groupindex_style1.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
				} else if ("2".equals(style)) {
					mv = new RedPigJModelAndView("admin/blue/groupindex_style2.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
				} else if ("3".equals(style)) {
					mv = new RedPigJModelAndView("admin/blue/groupindex_style3.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
				} else if ("4".equals(style)) {
					mv = new RedPigJModelAndView("admin/blue/groupindex_style6.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
				} else if ("5".equals(style)) {
					mv = new RedPigJModelAndView("admin/blue/groupindex_style5.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
				}
				mv.addObject("seq", seq);
				mv.addObject("style", style);
				mv.addObject("obj", gi);
				mv.addObject("gcs", gcs);
				mv.addObject("gpcs", gpcs);
				mv.addObject("group_tools", this.groupTools);
				mv.addObject("aps_img", aps_img);
				mv.addObject("aps", aps);
			} else {
				mv = new RedPigJModelAndView("admin/blue/tip.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_tip", "不存在该设计");
				mv.addObject("list_url", "/groupindex_design");
			}
		}
		return mv;
	}
	
	/**
	 * 团购首页设计信息保存管理
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
	 * @param ggid
	 * @param gcid
	 * @return
	 */
	@SecurityMapping(title = "团购首页设计信息保存管理", value = "/groupindex_style_save*", rtype = "admin", rname = "团购设置", rcode = "group", rgroup = "促销管理")
	@RequestMapping({ "/groupindex_style_save" })
	public String groupindex_style_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String seq, String type,
			String name, String name_href, String adv_type, String adv_id,
			String ad_id, String adv_url, String advp_id, String goods,
			String ggid, String gcid) {
		GroupIndex gi = null;
		int count = 0;
		if ((id != null) && (!"".equals(id))) {
			gi = this.groupIndexService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (gi != null) {
				List<Map> list = Lists.newArrayList();
				Map<String, Object> map = Maps.newHashMap();
				if ("1".equals(type)) {
					if ((name != null) && (!"".equals(name)) && (name_href != null) && (!"".equals(name_href))) {
						
						if ((gi.getGid_nav_word_list() != null) && (!"".equals(gi.getGid_nav_word_list()))) {
							
							list = JSON.parseArray(gi.getGid_nav_word_list(), Map.class);
							if (list.size() > 0) {
								for (Map m : list) {
									if (seq.equals(CommUtil.null2String(m.get("seq")))) {
										list.remove(m);
										extract_on(seq, name, name_href, list, map);
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
						gi.setGid_nav_word_list(JSON.toJSONString(list));
					}
				} else if ("3".equals(type)) {
					if ((advp_id != null) && (!"".equals(advp_id))) {
						gi.setGid_nav_advp_id(CommUtil.null2Long(advp_id));
					}
				} else if ("4".equals(type)) {
					if ((ggid != null) && (!ggid.equals(""))) {
						gi.setGid_nav_right_img_list(ggid);
					}
				} else if ("5".equals(type)) {
					gi.setGid_nav_hot_class(gcid);
				}
				this.groupIndexService.updateById(gi);
			}
		}
		return "redirect:/groupindex_design_design?id=" + id;
	}
	
	/**
	 * 左侧商品分类信息保存管理
	 * @param request
	 * @param response
	 * @param id
	 * @param seq
	 * @param type
	 * @param ids
	 * @param name
	 * @param name_href
	 * @param pid
	 */
	@SecurityMapping(title = "左侧商品分类信息保存管理", value = "/groupindex_style_gc_save*", rtype = "admin", rname = "团购设置", rcode = "group", rgroup = "促销管理")
	@RequestMapping({ "/groupindex_style_gc_save" })
	public void groupindex_style_gc_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String seq, String type,
			String ids, String name, String name_href, String pid) {
		GroupIndex gi = null;
		int count = 0;
		if ((id != null) && (!"".equals(id))) {
			gi = this.groupIndexService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (gi != null) {
				List<Map> list = Lists.newArrayList();
				Map<String, Object> map = Maps.newHashMap();
				if (("2".equals(type)) && (ids != null) && (!"".equals(ids))) {
					String[] gc_ids = ids.split(",");
					if ((gi.getGid_nav_gc_list() != null)
							&& (!"".equals(gi.getGid_nav_gc_list()))) {
						list = JSON.parseArray(gi.getGid_nav_gc_list(),
								Map.class);
						if (list.size() > 0) {
							for (Map m : list) {
								if (seq.equals(CommUtil.null2String(m
										.get("seq")))) {
									list.remove(m);
									extra_gc_list_left(seq, list, map, gc_ids, pid);
									count = 1;
									break;
								}
							}
							if (count == 0) {
								extra_gc_list_left(seq, list, map, gc_ids, pid);
							}
						} else {
							extra_gc_list_left(seq, list, map, gc_ids, pid);
						}
					} else {
						extra_gc_list_left(seq, list, map, gc_ids, pid);
					}
					gi.setGid_nav_gc_list(JSON.toJSONString(list));
				}
				gi.setGid_nav_gc_list(JSON.toJSONString(list));
			}
			this.groupIndexService.updateById(gi);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(gi);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 频道模板分类商品编辑
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param gc_id
	 * @param type
	 * @param effect
	 * @param adv_id
	 * @return
	 */
	@SecurityMapping(title = "频道模板分类商品编辑", value = "/groupindex_style_class*", rtype = "admin", rname = "团购设置", rcode = "group", rgroup = "促销管理")
	@RequestMapping({ "/groupindex_style_class" })
	public ModelAndView groupindex_style_class(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gc_id,
			String type, String effect, String adv_id) {
		ModelAndView mv = null;
		if ((type != null) && (!"".equals(type))) {
			mv = new RedPigJModelAndView("admin/blue/groupindex_style_load.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			
			GroupClass gc = this.groupClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
			
			if ((gc.getChilds() != null) && (gc.getChilds().size() > 0)) {
				mv.addObject("gcs", gc.getChilds());
			} else {
				mv.addObject("child", "child");
			}
			
		} else if ((effect != null) && (!"".equals(effect)) && ("show".equals(effect))) {
			mv = new RedPigJModelAndView("admin/blue/groupindex_style_load.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			
			GroupClass gc = this.groupClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
			if (gc != null) {
				mv.addObject("show", "show");
				if (gc.getGc_level() == 1) {
					mv.addObject("groupClass", gc);
				} else {
					GroupClass p_gc = gc.getParent();
					mv.addObject("groupClass", p_gc);
				}
			}
		}
		mv.addObject("groupTools", this.groupTools);
		return mv;
	}
	
	
	private void extract_ad_info(HttpServletRequest request, String seq,
			String adv_type, String adv_id, List<Map> list, Map map,
			String adv_url, String goods_id) {
		
		if ("img".equals(adv_type)) {
			String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
			
			String saveFilePathName = request.getSession().getServletContext()
					.getRealPath("/")
					+ uploadFilePath + File.separator + "channel";
			
			Map json_map = Maps.newHashMap();
			try {
				json_map = CommUtil.saveFileToServer(request, "img",saveFilePathName, "", null);
				
				if (json_map.get("fileName") == "") {
					return;
				}
				
				Accessory photo = new Accessory();
				photo.setName(CommUtil.null2String(json_map.get("fileName")));
				photo.setExt(CommUtil.null2String(json_map.get("mime")));
				photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(json_map
						.get("fileSize"))));
				photo.setPath(uploadFilePath + "/groupindex");
				photo.setWidth(CommUtil.null2Int(json_map.get("width")));
				photo.setHeight(CommUtil.null2Int(json_map.get("height")));
				photo.setAddTime(new Date());
				this.accessoryService.saveEntity(photo);
				map.put("seq", seq);
				map.put("path", photo.getPath() + "/" + photo.getName());
				map.put("href", adv_url);
				map.put("img_id", photo.getId());
				map.put("type", "img");
				list.add(map);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (adv_type.equals("adv")) {
			AdvertPosition ap = this.advertPositionService.selectByPrimaryKey(CommUtil
					.null2Long(adv_id));
			if (ap != null) {
				List ads = ap.getAdvs();
				Iterator e = ads.iterator();
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
			Goods gs = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
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

	private void extract_on(String seq, String name, String name_href, List<Map> list, Map map) {
		map.put("seq", seq);
		map.put("name", name);
		map.put("href", name_href);
		list.add(map);
	}

	private void extra_gc_list_left(String seq, List list, Map map,
			String[] gc_ids, String pid) {
		map.put("seq", seq);
		map.put("pid", pid);
		map.put("count", Integer.valueOf(gc_ids.length));
		for (int i = 0; i < gc_ids.length; i++) {
			map.put("gc_id_" + i, gc_ids[i]);
		}
		list.add(map);
	}

	private Set<Long> genericIds(GroupClass gc) {
		Set<Long> ids = Sets.newHashSet();
		
		ids.add(gc.getId());
		for (GroupClass child : gc.getChilds()) {
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
	@SecurityMapping(title = "删除导航上方文字", value = "/groupindex_del_word_ajax*", rtype = "admin", rname = "团购设置", rcode = "group", rgroup = "促销管理")
	@RequestMapping({ "/groupindex_del_word_ajax" })
	public void groupindex_del_word_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String seq) {
		if ((id != null) && (!"".equals(id))) {
			
			GroupIndex gi = this.groupIndexService.selectByPrimaryKey(CommUtil.null2Long(id));
			if ((gi != null) && (!"".equals(gi.getGid_nav_word_list())) && (gi.getGid_nav_word_list() != null)) {
				
				List<Map> wcs = JSON.parseArray(gi.getGid_nav_word_list(),Map.class);
				
				if (wcs.size() > 0) {
					for (Map map : wcs) {
						if (seq.equals(CommUtil.null2String(map.get("seq")))) {
							wcs.remove(map);
						}
					}
					gi.setGid_nav_word_list(JSON.toJSONString(wcs));
				}
				this.groupIndexService.updateById(gi);
			}
		}
	}
	
	/**
	 * 获取子分类
	 * @param request
	 * @param response
	 * @param pid
	 * @param flag
	 * @return
	 */
	@SecurityMapping(title = "获取子分类", value = "/getGroupClass*", rtype = "admin", rname = "团购设置", rcode = "group", rgroup = "促销管理")
	@RequestMapping({ "/getGroupClass" })
	public ModelAndView getGroupClass(HttpServletRequest request,
			HttpServletResponse response, String pid, String flag) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/groupclassselect.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String, Object> params = Maps.newHashMap();
		params.put("parent", CommUtil.null2Long(pid));
		
		List<GroupClass> gclist = this.groupClassService.queryPageList(params);
		
		mv.addObject("gclist", gclist);
		return mv;
	}
	
	/**
	 * 获取生活团商品
	 * @param request
	 * @param response
	 * @param pid
	 * @return
	 */
	@SecurityMapping(title = "获取生活团商品", value = "/getGroupitems*", rtype = "admin", rname = "团购设置", rcode = "group", rgroup = "促销管理")
	@RequestMapping({ "/getGroupitems" })
	public ModelAndView getGroupGoods(HttpServletRequest request,
			HttpServletResponse response, String pid) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/groupclassselect.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("gg_gc_id", CommUtil.null2Long(pid));
		List<GroupLifeGoods> goodslist = this.grouplifeGoodsService.queryPageList(params);
		
		mv.addObject("container", "container");
		mv.addObject("goodslist", goodslist);
		return mv;
	}
	
	/**
	 * 获取生活购二级分类
	 * @param request
	 * @param response
	 * @param pid
	 * @param flag
	 * @return
	 */
	@SecurityMapping(title = "获取生活购二级分类", value = "/getSecondClass*", rtype = "admin", rname = "团购设置", rcode = "group", rgroup = "促销管理")
	@RequestMapping({ "/getSecondClass" })
	public ModelAndView getSecondClass(HttpServletRequest request,
			HttpServletResponse response, String pid, String flag) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/groupclassselect.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String, Object> params = Maps.newHashMap();
		params.put("parent", CommUtil.null2Long(pid));
		
		List<GroupClass> gclist = this.groupClassService.queryPageList(params);
		
		mv.addObject("secondClass", gclist);
		return mv;
	}
	
	/**
	 * 轮播商品编辑
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "轮播商品编辑", value = "/groupindex_items*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/groupindex_items" })
	public ModelAndView goods_floor_list_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id,
			String type) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/goods_floor_list_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps = Maps.newHashMap();
        maps.put("parent", -1);
        maps.put("orderBy", "sequence");
        maps.put("orderType", "asc");

		List<GroupClass> gcs = this.groupClassService.queryPageList(maps);
		
		mv.addObject("gcs", gcs);
		mv.addObject("groupTools", this.groupTools);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 轮播商品载入
	 * @param request
	 * @param response
	 * @param gid
	 * @param currentPage
	 * @param goods_name
	 * @return
	 */
	@SecurityMapping(title = "轮播商品载入", value = "/groupindex_groupgoods_load*", rtype = "admin", rname = "团购设置", rcode = "group", rgroup = "促销管理")
	@RequestMapping({ "/groupindex_groupgoods_load" })
	public ModelAndView groupindex_groupgoods_load(HttpServletRequest request,
			HttpServletResponse response, String gid, String currentPage,
			String goods_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/groupindex_grouplifegoodslist.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, 6,"addTime", "desc");
		
		if (!CommUtil.null2String(gid).equals("")) {
			Set<Long> ids = genericIds(this.groupClassService.selectByPrimaryKey(CommUtil.null2Long(gid)));
			
			maps.put("gg_gc_ids", ids);
			
			
		}
		if (!CommUtil.null2String(goods_name).equals("")) {
			
			maps.put("gg_name_like", goods_name);
		}
		
		maps.put("group_status", 1);
		
		IPageList pList = this.grouplifeGoodsService.list(maps);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/groupindex_groupgoods_load.html", "", "", pList, mv);
		return mv;
	}
}
