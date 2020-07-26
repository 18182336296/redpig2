package com.redpigmall.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
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
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Album;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.User;
import com.redpigmall.domain.WeixinFloor;

/**
 * 
 * <p>
 * Title: RedPigWeixinFloorManageAction.java
 * </p>
 * 
 * <p>
 * Description:微信楼层设置
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
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigWeixinFloorManageAction extends BaseAction{

	/**
	 * 微信楼层设置
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "微信楼层设置", value = "/weixin_floor*", rtype = "admin", rname = "微商城楼层", rcode = "admin_weixin_floor", rgroup = "运营")
	@RequestMapping({ "/weixin_floor" })
	public ModelAndView weixin_floor(HttpServletRequest request,
			HttpServletResponse response) {

		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixin_index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String, Object> params = Maps.newHashMap();
		List<WeixinFloor> list = this.weixinfloorService
				.queryPageList(RedPigMaps.newMap());

		mv.addObject("floor_list", list);
		params.put("parent", -1);

		List<GoodsClass> gcs = this.goodsClassService.queryPageList(params);

		mv.addObject("gcs", gcs);
		String[] strs = this.configService.getSysConfig().getImageSuffix()
				.split("\\|");

		StringBuffer sb = new StringBuffer();
		for (String str : strs) {
			sb.append("." + str + ",");
		}
		mv.addObject("imageSuffix1", sb);
		params.clear();

		params.put("redpig_user_userRole", "ADMIN");
		List<Album> albums = this.albumService.queryPageList(params);

		mv.addObject("albums", albums);
		mv.addObject("weixinIndexTools", this.weixinIndexTools);
		return mv;
	}

	/**
	 * 微信楼层设置
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "微信楼层设置", value = "/weixin_index_preview*", rtype = "admin", rname = "微商城楼层", rcode = "admin_weixin_floor", rgroup = "运营")
	@RequestMapping({ "/weixin_index_preview" })
	public ModelAndView weixin_index_preview(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {

		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixin_index_preview.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		List<WeixinFloor> list = this.weixinfloorService.queryPageList(RedPigMaps.newMap());
		
		mv.addObject("floor_list", list);
		mv.addObject("weixinIndexTools", this.weixinIndexTools);
		return mv;
	}

	/**
	 * 微信楼层设置
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "微信楼层设置", value = "/weixinindexfloor_add*", rtype = "admin", rname = "微商城楼层", rcode = "admin_weixin_floor", rgroup = "运营")
	@RequestMapping({ "/weixinindexfloor_add" })
	public ModelAndView weixinindexfloor_add(HttpServletRequest request,
			HttpServletResponse response) {

		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixin_index_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("edit_type", "all");
		return mv;
	}

	/**
	 * 微信楼层设置
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "微信楼层设置", value = "/weixinindexfloor_edit*", rtype = "admin", rname = "微商城楼层", rcode = "admin_weixin_floor", rgroup = "运营")
	@RequestMapping({ "/weixinindexfloor_edit" })
	public ModelAndView weixinfloor_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {

		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixin_index_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			WeixinFloor weixinfloor = this.weixinfloorService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", weixinfloor);
			mv.addObject("sequence", Integer.valueOf(weixinfloor.getSequence()));
			mv.addObject("edit_type", "floor");
			mv.addObject("status", Integer.valueOf(weixinfloor.getDisplay()));
		}
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @return
	 */
	@RequestMapping({ "/weixinfloor_save" })
	public ModelAndView weixinfloor_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {

		WeixinFloor weixinfloor = null;
		if (id.equals("")) {
			weixinfloor = (WeixinFloor) WebForm
					.toPo(request, WeixinFloor.class);
			weixinfloor.setAddTime(new Date());
		} else {
			WeixinFloor obj = this.weixinfloorService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			weixinfloor = (WeixinFloor) WebForm.toPo(request, obj);
		}
		if (id.equals("")) {
			this.weixinfloorService.save(weixinfloor);
			
		} else {
			this.weixinfloorService.updateById(weixinfloor);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存weixinfloor成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	/**
	 * 微信楼层设置
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 */
	@SecurityMapping(title = "微信楼层设置", value = "/weixinindexfloor_del*", rtype = "admin", rname = "微商城楼层", rcode = "admin_weixin_floor", rgroup = "运营")
	@RequestMapping({ "/weixinindexfloor_del" })
	public void weixinindexfloor_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId) {

		String return_str = "";
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				this.weixinfloorService.deleteById(Long.valueOf(Long
						.parseLong(id)));
				return_str = "true";
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(return_str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 微信楼层设置
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "微信楼层设置", value = "/weixinindex_line_add*", rtype = "admin", rname = "微商城楼层", rcode = "admin_weixin_floor", rgroup = "运营")
	@RequestMapping({ "/weixinindex_line_add" })
	public ModelAndView weixinindex_line_add(HttpServletRequest request,
			HttpServletResponse response, String id) {

		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixin_indexfloor_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		if ((id != null) && (!id.equals(""))) {
			WeixinFloor weixinFloor = this.weixinfloorService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", weixinFloor);
			mv.addObject("edit_type", "line");
		}
		return mv;
	}

	/**
	 * 微信楼层设置
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param num
	 * @return
	 */
	@SecurityMapping(title = "微信楼层设置", value = "/weixinindex_line_edit*", rtype = "admin", rname = "微商城楼层", rcode = "admin_weixin_floor", rgroup = "运营")
	@RequestMapping({ "/weixinindex_line_edit" })
	public ModelAndView weixinindex_line_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String num) {

		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixin_indexfloor_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		if ((id != null) && (!id.equals(""))) {
			WeixinFloor weixinFloor = this.weixinfloorService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", weixinFloor);
			mv.addObject("edit_type", "line");
			mv.addObject("line_num",
					Integer.valueOf(CommUtil.null2Int(num) - 1));
			List<Map> list = JSON.parseArray(weixinFloor.getLines_info(),
					Map.class);
			Map<String, Object> map = (Map) list
					.get(CommUtil.null2Int(num) - 1);
			mv.addObject("line", map);
			mv.addObject("sequence", map.get("sequence"));
			mv.addObject("line_type", map.get("line_type"));
			mv.addObject("line_info", map.get("line_info"));
			mv.addObject("status", map.get("display"));
			mv.addObject("weixinIndexTools", this.weixinIndexTools);
		}
		return mv;
	}

	/**
	 * 微信楼层设置
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param title
	 * @param sequence
	 * @param status
	 * @param edit_type
	 * @param line_num
	 * @param line_type
	 * @param line_info
	 * @param more_link
	 */
	@SecurityMapping(title = "微信楼层设置", value = "/weixinindex_line_save*", rtype = "admin", rname = "微商城楼层", rcode = "admin_weixin_floor", rgroup = "运营")
	@RequestMapping({ "/weixinindex_line_save" })
	public void weixinindex_line_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String title,
			String sequence, String status, String edit_type, String line_num,
			String line_type, String line_info, String more_link) {

		WeixinFloor weixinFloor = null;
		if ((id != null) && (!id.equals(""))) {
			weixinFloor = this.weixinfloorService.selectByPrimaryKey(CommUtil
					.null2Long(id));
		} else {
			weixinFloor = new WeixinFloor();
			weixinFloor.setAddTime(new Date());
		}

		Map<String, Object> map = Maps.newHashMap();
		map.put("line_type", line_type);
		if ((title != null) && (!title.equals(""))) {
			weixinFloor.setTitle(title);
			weixinFloor.setMore_link(more_link);
			weixinFloor.setSequence(CommUtil.null2Int(sequence));
			map.put("sequence", Integer.valueOf(0));
			if (CommUtil.null2Boolean(status)) {
				weixinFloor.setDisplay(1);
			} else {
				weixinFloor.setDisplay(0);
			}
		} else {
			map.put("sequence", Integer.valueOf(CommUtil.null2Int(sequence)));
			if (CommUtil.null2Boolean(status)) {
				map.put("display", Integer.valueOf(1));
			} else {
				map.put("display", Integer.valueOf(0));
			}
		}
		String weixin_line_info = weixinFloor.getLines_info();
		List<Map> weixin_line_info_list = null;
		if ((weixin_line_info != null) && (!weixin_line_info.equals(""))) {
			weixin_line_info_list = JSON
					.parseArray(weixin_line_info, Map.class);
		} else {
			weixin_line_info_list = Lists.newArrayList();
		}
		List list = Lists.newArrayList();
		for (String str : line_info.split(";")) {
			if (str != "") {
				String[] arr = str.split(",");
				Map line_map = Maps.newHashMap();
				line_map.put("click_type", arr[0]);
				line_map.put("click_info", arr[1]);
				String aa = arr[2];
				String[] arr2 = aa.split("\\?");
				String[] arr3 = arr2[1].split("&");
				line_map.put("img_url", arr[2]);
				line_map.put("height", arr3[0]);
				line_map.put("width", arr3[1]);
				list.add(line_map);
			}
		}
		map.put("line_info", list);

		int index = CommUtil.null2Int(line_num);
		if ((line_num != null) && (!line_num.equals(""))) {
			weixin_line_info_list.remove(index);
			weixin_line_info_list.add(index, map);
		} else {
			weixin_line_info_list.add(map);
		}
		Collections.sort(weixin_line_info_list, new MyComparator());
		weixinFloor.setLines_info(JSON.toJSONString(weixin_line_info_list));
		if ((id != null) && (!id.equals(""))) {
			this.weixinfloorService.updateById(weixinFloor);
		} else {
			this.weixinfloorService.save(weixinFloor);
		}
		Object jsonmap = Maps.newHashMap();
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(jsonmap));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 微信楼层设置
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param gc_id
	 * @param goods_name
	 * @return
	 */
	@SecurityMapping(title = "微信楼层设置", value = "/weixin_index_goods_search*", rtype = "admin", rname = "微商城楼层", rcode = "admin_weixin_floor", rgroup = "运营")
	@RequestMapping({ "/weixin_index_goods_search" })
	public ModelAndView weixin_index_goods_search(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gc_id,
			String goods_name) {

		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixin_index_goods_load.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		Map<String, Object> params = this.redPigQueryTools.getParams(
				currentPage, 5, null, null);

		if (!CommUtil.null2String(gc_id).equals("")) {
			Set<Long> ids = genericIds(this.goodsClassService
					.selectByPrimaryKey(CommUtil.null2Long(gc_id)));
			params.put("ids", ids);
		}

		if (!CommUtil.null2String(goods_name).equals("")) {
			params.put("goods_name_like", goods_name);

		}

		params.put("goods_status", 0);

		IPageList pList = this.goodsService.list(params);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/weixin_index_goods_search.html", "", "&gc_id="
				+ gc_id + "&goods_name=" + goods_name, pList, mv);
		return mv;
	}

	/**
	 * 微信楼层设置
	 * 
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "微信楼层设置", value = "/weixin_index_image_upload*", rtype = "admin", rname = "手机端首页", rcode = "admin_app_index", rgroup = "运营")
	@RequestMapping({ "/weixin_index_image_upload" })
	public void weixin_index_image_upload(HttpServletRequest request,
			HttpServletResponse response) {

		Map ajaxUploadInfo = Maps.newHashMap();
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());

		String path = this.storeTools.createAdminFolder(request);
		String url = this.storeTools.createAdminFolderURL();

		try {
			Map<String, Object> map = CommUtil.saveFileToServer(request,
					"upload_img", path, null, null);
			Accessory image = new Accessory();
			image.setAddTime(new Date());
			image.setExt((String) map.get("mime"));
			image.setPath(url);
			image.setWidth(CommUtil.null2Int(map.get("width")));
			image.setHeight(CommUtil.null2Int(map.get("height")));
			image.setName(CommUtil.null2String(map.get("fileName")));
			image.setUser(user);
			Album album = this.albumService.getDefaultAlbum(user.getId());
			if (album == null) {
				album = new Album();
				album.setAddTime(new Date());
				album.setAlbum_name("默认相册【" + user.getUserName() + "】");
				album.setAlbum_sequence(55536);
				album.setAlbum_default(true);
				album.setUser(user);
				this.albumService.saveEntity(album);
			}
			image.setAlbum(album);
			this.accessoryService.saveEntity(image);
			ajaxUploadInfo.put("url", image.getPath() + "/" + image.getName()
					+ "?" + image.getHeight() + "&" + image.getWidth());
		} catch (IOException e) {
			e.printStackTrace();
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(ajaxUploadInfo));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping({ "/weixinfloor_ajax" })
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {

		WeixinFloor obj = this.weixinfloorService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(id)));
		Field[] fields = WeixinFloor.class.getDeclaredFields();
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);

		this.weixinfloorService.updateById(obj);
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

	private Set<Long> genericIds(GoodsClass gc) {
		Set<Long> ids = new HashSet();
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

	class MyComparator implements Comparator {
		MyComparator() {
		}

		public int compare(Object o1, Object o2) {
			Map map1 = (Map) o1;
			Map map2 = (Map) o2;
			int sequence1 = CommUtil.null2Int(map1.get("sequence"));
			int sequence2 = CommUtil.null2Int(map2.get("sequence"));
			return sequence1 - sequence2;
		}
	}
}
