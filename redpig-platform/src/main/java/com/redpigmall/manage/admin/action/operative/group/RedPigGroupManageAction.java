package com.redpigmall.manage.admin.action.operative.group;

import java.io.File;
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
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.lucene.RedPigLuceneUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.Group;
import com.redpigmall.domain.GroupGoods;
import com.redpigmall.domain.GroupLifeGoods;

/**
 * 
 * <p>
 * Title: RedPigGroupManageAction.java
 * </p>
 * 
 * <p>
 * Description: 团购活动平台管理控制器，用来添加（编辑、删除）团购信息、审核参团商品
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
@Controller
public class RedPigGroupManageAction extends BaseAction {
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "团购列表", value = "/group_list*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_list" })
	public ModelAndView group_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/group_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		IPageList pList = this.redPigGroupService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/group_list.html", "",
				params, pList, mv);
		return mv;
	}
	
	/**
	 * 团购增加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购增加", value = "/group_add*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_add" })
	public ModelAndView group_add(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/group_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		Group goods_last = null;
		Group life_last = null;
		Map<String, Object> params = Maps.newHashMap();
		params.put("group_type", Integer.valueOf(0));
		params.put("status1", Integer.valueOf(-1));
		params.put("status2", Integer.valueOf(-2));
		
		List<Group> goods_group_lasts = this.redPigGroupService.queryPageList(params,0,1);
		
		params.clear();
		params.put("group_type", Integer.valueOf(1));
		params.put("status1", Integer.valueOf(-1));
		params.put("status2", Integer.valueOf(-2));
		List<Group> life_group_lasts = this.redPigGroupService.queryPageList(params,0,1);
		
		if (goods_group_lasts.size() > 0) {
			goods_last = (Group) goods_group_lasts.get(0);
			mv.addObject("goods_last_time",
					CommUtil.formatShortDate(goods_last.getEndTime()));
		}
		if (life_group_lasts.size() > 0) {
			life_last = (Group) life_group_lasts.get(0);
			mv.addObject("life_last_time",
					CommUtil.formatShortDate(life_last.getEndTime()));
		}
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 团购保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param begin_hour
	 * @param end_hour
	 * @param join_hour
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@SecurityMapping(title = "团购保存", value = "/group_save*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_save" })
	public ModelAndView group_saveEntity(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage,
			String cmd, 
			String begin_hour, 
			String end_hour, 
			String join_hour) {
		
		Group group = null;
		if (id.equals("")) {
			group = (Group) WebForm.toPo(request, Group.class);
			group.setAddTime(new Date());
		} else {
			Group obj = this.redPigGroupService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			group = (Group) WebForm.toPo(request, obj);
		}
		Date beginTime = group.getBeginTime();
		beginTime.setHours(CommUtil.null2Int(begin_hour));
		group.setBeginTime(beginTime);
		Date endTime = group.getEndTime();
		endTime.setHours(CommUtil.null2Int(end_hour));
		group.setEndTime(endTime);
		Date joinEndTime = group.getJoinEndTime();
		joinEndTime.setHours(CommUtil.null2Int(join_hour));
		group.setJoinEndTime(joinEndTime);
		if (beginTime.after(new Date())) {
			group.setStatus(1);
		}
		if (id.equals("")) {
			this.redPigGroupService.saveEntity(group);
		} else {
			this.redPigGroupService.updateById(group);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		mv.addObject("list_url", CommUtil.getURL(request) + "/group_list");
		mv.addObject("op_title", "保存团购成功");
		mv.addObject("add_url", CommUtil.getURL(request) + "/group_add"
				+ "?currentPage=" + currentPage);
		return mv;
	}
	
	/**
	 * 团购删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购删除", value = "/group_del*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_del" })
	public String group_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Group group = this.redPigGroupService.selectByPrimaryKey(CommUtil.null2Long(id));
				if ((group.getGroup_type() == 0) && (group.getGg_status() == 0)) {
					for (Goods goods : group.getGoods_list()) {
						goods.setGroup_buy(0);
						goods.setGroup(null);
						goods.setGoods_current_price(goods.getStore_price());
						this.redPigGoodsService.updateById(goods);
					}
					for (GroupGoods gg : group.getGg_list()) {
						this.redPigGroupGoodsService.deleteById(gg.getId());
					}
					this.redPigGroupService.deleteById(CommUtil.null2Long(id));
				}
				if ((group.getGroup_type() == 1) && (group.getGl_status() == 0)) {
					for (GroupLifeGoods goods : group.getGl_list()) {
						this.redPigGroupLifeGoodsService.deleteById(goods.getId());
					}
					this.redPigGroupService.deleteById(CommUtil.null2Long(id));
				}
			}
		}
		return "redirect:group_list?currentPage=" + currentPage;
	}
	
	/**
	 * 团购关闭
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购关闭", value = "/group_close*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_close" })
	public String group_close(
			HttpServletRequest request,
			HttpServletResponse response, 
			String mulitId, 
			String currentPage) {
		
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Group group = this.redPigGroupService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				group.setStatus(-1);
				this.redPigGroupService.updateById(group);
				if (group.getGroup_type() == 0) {
					for (GroupGoods gg : group.getGg_list()) {
						gg.setGg_status(-2);
						this.redPigGroupGoodsService.updateById(gg);
					}
					for (Goods goods : group.getGoods_list()) {
						goods.setGroup(null);
						goods.setGroup_buy(0);
						goods.setGoods_current_price(goods.getStore_price());
						this.redPigGoodsService.updateById(goods);

						String goodsgroup_lucene_path = ClusterSyncTools
								.getClusterRoot()
								+ File.separator
								+ "luence"
								+ File.separator + "groupgoods";
						File filegroup = new File(goodsgroup_lucene_path);
						if (!filegroup.exists()) {
							CommUtil.createFolder(goodsgroup_lucene_path);
						}
						RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
						RedPigLuceneUtil.setIndex_path(goodsgroup_lucene_path);
						lucene.delete_index(CommUtil.null2String(group.getId()));
					}
				}
				if (group.getGroup_type() == 1) {
					for (GroupLifeGoods glg : group.getGl_list()) {
						glg.setGroup_status(-2);
						this.redPigGroupLifeGoodsService.updateById(glg);

						String goodslife_lucene_path = ClusterSyncTools
								.getClusterRoot()
								+ File.separator
								+ "luence"
								+ File.separator + "lifegoods";
						File filelife = new File(goodslife_lucene_path);
						if (!filelife.exists()) {
							CommUtil.createFolder(goodslife_lucene_path);
						}
						RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
						RedPigLuceneUtil.setIndex_path(goodslife_lucene_path);
						lucene.delete_index(CommUtil.null2String(group.getId()));
					}
				}
			}
		}
		return "redirect:group_list?currentPage=" + currentPage;
	}
	
	/**
	 * 团购申请列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param group_id
	 * @param gg_status
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "团购申请列表", value = "/group_goods_list*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_goods_list" })
	public ModelAndView group_goods_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String group_id,
			String gg_status, 
			String type) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/group_goods_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		if (("goods".equals(type)) || ("".equals(type)) || (type == null)) {
			
			params.put("group_id", CommUtil.null2Long(group_id));
			
			if ((gg_status == null) || (gg_status.equals(""))) {
				params.put("gg_status", Integer.valueOf(0));
				
			} else {
				params.put("gg_status", CommUtil.null2Int(gg_status));
				
			}
			
			IPageList pList = this.redPigGroupGoodsService.list(params);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("group_id", group_id);
			mv.addObject("gg_status",
					Integer.valueOf(CommUtil.null2Int(gg_status)));
		}
		params.clear();
		
		if ("life".equals(type)) {
			mv = new RedPigJModelAndView(
					"admin/blue/group_lifegoods_list.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
			
			params = this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
			
			params.put("group_id", CommUtil.null2Long(group_id));
			
			if ((gg_status == null) || (gg_status.equals(""))) {
				params.put("group_status", Integer.valueOf(0));
			} else {
				params.put("group_status", CommUtil.null2Int(gg_status));
			}
			
			IPageList pList = this.redPigGroupLifeGoodsService.list(params);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("group_id", group_id);
			mv.addObject("gg_status",
					Integer.valueOf(CommUtil.null2Int(gg_status)));
		}
		mv.addObject("type", type);
		return mv;
	}
	
	/**
	 * 团购商品审核通过
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param group_id
	 * @param gg_status
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购商品审核通过", value = "/group_goods_audit*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_goods_audit" })
	public String group_goods_audit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String mulitId, 
			String group_id,
			String gg_status, 
			String currentPage) {
		
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GroupGoods gg = this.redPigGroupGoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
				Goods goods = gg.getGg_goods();
				if ((gg.getBeginTime().before(new Date()))
						||
						(CommUtil.formatShortDate(gg.getBeginTime())
								.equals(CommUtil.formatShortDate(new Date())))) {
					gg.setGg_status(1);
					goods.setGroup_buy(2);
					goods.setGroup(gg.getGroup());
					goods.setGoods_current_price(gg.getGg_price());
				} else {
					gg.setGg_status(2);
					goods.setGroup_buy(4);
				}
				this.redPigGoodsService.updateById(goods);
				gg.setGg_audit_time(new Date());
				this.redPigGroupGoodsService.updateById(gg);
				this.goodsTools.updateGoodsLucene(goods);
				this.goodsTools.addGroupGoodsLucene(gg);
			}
		}
		return "redirect:group_goods_list?group_id=" + group_id + "&gg_status="
				+ gg_status + "&currentPage=" + currentPage;
	}
	
	/**
	 * 团购商品审核拒绝
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param group_id
	 * @param gg_status
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购商品审核拒绝", value = "/group_goods_refuse*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_goods_refuse" })
	public String group_goods_refuse(
			HttpServletRequest request,
			HttpServletResponse response, 
			String mulitId, 
			String group_id,
			String gg_status, 
			String currentPage) {
		
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GroupGoods gg = this.redPigGroupGoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
				Goods goods = gg.getGg_goods();
				goods.setGroup_buy(0);
				goods.setGroup(null);
				goods.setGoods_current_price(goods.getStore_price());
				this.redPigGoodsService.updateById(goods);
				gg.setGg_status(-1);
				this.redPigGroupGoodsService.updateById(gg);
				this.goodsTools.updateGoodsLucene(goods);
				this.goodsTools.deleteGroupGoodsLucene(gg);
			}
		}
		return "redirect:group_goods_list?group_id=" + group_id + "&gg_status="
				+ gg_status + "&currentPage=" + currentPage;
	}
	
	/**
	 * 团购商品审核推荐
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param group_id
	 * @param gg_status
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购商品审核推荐", value = "/group_goods_recommend*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_goods_recommend" })
	public String group_goods_recommend(
			HttpServletRequest request,
			HttpServletResponse response, 
			String mulitId, 
			String group_id,
			String gg_status, 
			String currentPage) {
		
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GroupGoods gg = this.redPigGroupGoodsService
						.selectByPrimaryKey(CommUtil.null2Long(id));
				if (gg.getGg_recommend()) {
					gg.setGg_recommend(false);
				} else {
					gg.setGg_recommend(true);
				}
				gg.setGg_recommend_time(new Date());
				this.redPigGroupGoodsService.updateById(gg);
			}
		}
		return "redirect:group_goods_list?group_id=" + group_id + "&gg_status="
				+ gg_status + "&currentPage=" + currentPage;
	}
	
	/**
	 * 团购商品审核推荐
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param group_id
	 * @param gg_status
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购商品审核推荐", value = "/group_lifegoods_recommend*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_lifegoods_recommend" })
	public String group_lifegoods_recommend(
			HttpServletRequest request,
			HttpServletResponse response, 
			String mulitId, 
			String group_id,
			String gg_status, 
			String currentPage) {
		
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GroupLifeGoods gg = this.redPigGroupLifeGoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
				if (gg.getGroup_recommend()) {
					gg.setGroup_recommend(false);
				} else {
					gg.setGroup_recommend(true);
				}
				this.redPigGroupLifeGoodsService.updateById(gg);
			}
		}
		return

		"redirect:group_goods_list?group_id=" + group_id + "&gg_status="
				+ gg_status + "&currentPage=" + currentPage + "&type=life";
	}
	
	/**
	 * 团购商品审核通过
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param group_id
	 * @param gg_status
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购商品审核通过", value = "/group_lifegoods_audit*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_lifegoods_audit" })
	public String group_lifegoods_audit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String mulitId, 
			String group_id,
			String gg_status, 
			String currentPage) {
		
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GroupLifeGoods gg = this.redPigGroupLifeGoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
				if ((gg.getBeginTime().before(new Date()))
						||
						(CommUtil.formatShortDate(gg.getBeginTime())
								.equals(CommUtil.formatShortDate(new Date())))) {
					gg.setGroup_status(1);
				} else {
					gg.setGroup_status(2);
				}
				this.redPigGroupLifeGoodsService.updateById(gg);
				this.goodsTools.addGroupLifeLucene(gg);
			}
		}
		return

		"redirect:group_goods_list?group_id=" + group_id + "&gg_status="
				+ gg_status + "&currentPage=" + currentPage + "&type=life";
	}
	
	/**
	 * 团购商品审核拒绝
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param group_id
	 * @param gg_status
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购商品审核拒绝", value = "/group_lifegoods_refuse*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_lifegoods_refuse" })
	public String group_lifegoods_refuse(
			HttpServletRequest request,
			HttpServletResponse response, 
			String mulitId, 
			String group_id,
			String gg_status, 
			String currentPage) {
		
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GroupLifeGoods gg = this.redPigGroupLifeGoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
				gg.setGroup_status(-1);
				this.redPigGroupLifeGoodsService.updateById(gg);
				this.goodsTools.deleteGroupLifeLucene(gg);
			}
		}
		return

		"redirect:group_goods_list?group_id=" + group_id + "&gg_status="
				+ gg_status + "&currentPage=" + currentPage + "&type=life";
	}
}
