package com.redpigmall.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Album;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.User;
import com.redpigmall.domain.WeixinChannelFloor;

/**
 * <p>
 * Title: RedPigWeixinChannelFloorManageAction.java
 * </p>
 * 
 * <p>
 * Description:微信频道楼层管理
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
@SuppressWarnings({"unchecked","rawtypes"})
@Controller
public class RedPigWeixinChannelFloorManageAction extends BaseAction{
	
	/**
	 * 微信二级频道楼层列表页
	 * @param request
	 * @param response
	 * @param channel_id
	 * @return
	 */
	@SecurityMapping(title = "微信二级频道楼层列表页", value = "/weixinchannelfloor_list*", rtype = "admin", rname = "微信二级频道楼层", rcode = "admin_weixinchannelfloor", rgroup = "运营")
	@RequestMapping({ "/weixinchannelfloor_list" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String channel_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixinchannelfloor_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params1 = Maps.newHashMap();
		
		params1.put("channel_id", CommUtil.null2Long(channel_id));
		params1.put("orderBy", "sequence");
		params1.put("orderType", "asc");
		
		List<WeixinChannelFloor> list = this.weixinchannelfloorService.queryPageList(params1);
		
		mv.addObject("floor_list", list);
		params1.put("parent", -1);
		params1.put("orderBy", "sequence");
		params1.put("orderType", "asc");
		
		List<GoodsClass> gcs = this.goodsClassService.queryPageList(params1);
		
		mv.addObject("gcs", gcs);
		String[] strs = this.configService.getSysConfig().getImageSuffix()
				.split("\\|");
		StringBuffer sb = new StringBuffer();
		for (String str : strs) {
			sb.append("." + str + ",");
		}
		mv.addObject("channel_id", channel_id);
		mv.addObject("imageSuffix1", sb);
		Map<String, Object> params = Maps.newHashMap();
		params.put("redpig_userRole", "ADMIN");
		params.put("orderBy", "album_sequence");
		params.put("orderType", "asc");
		
		List<Album> albums = this.albumService.queryPageList(params);
		
		
		mv.addObject("albums", albums);
		mv.addObject("weixinChannelFloorTools", this.weixinChannelFloorTools);
		return mv;
	}
	
	/**
	 * 微信二级频道楼层列表页预览
	 * @param request
	 * @param response
	 * @param channel_id
	 * @return
	 */
	@SecurityMapping(title = "微信二级频道楼层列表页预览", value = "/weixinchannelfloor_list_preview*", rtype = "admin", rname = "微信二级频道楼层", rcode = "admin_weixinchannelfloor", rgroup = "运营")
	@RequestMapping({ "/weixinchannelfloor_list_preview" })
	public ModelAndView list_preview(HttpServletRequest request,
			HttpServletResponse response, String channel_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixinchannelfloor_list_preview.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params1 = Maps.newHashMap();
		params1.put("channel_id", CommUtil.null2Long(channel_id));
		params1.put("orderBy", "sequence");
		params1.put("orderType", "asc");
		
		List<WeixinChannelFloor> list = this.weixinchannelfloorService.queryPageList(params1);
		
		mv.addObject("floor_list", list);
		mv.addObject("weixinChannelFloorTools", this.weixinChannelFloorTools);
		return mv;
	}
	
	/**
	 * 微信二级频道楼层修改
	 * @param request
	 * @param response
	 * @param id
	 * @param channel_id
	 * @return
	 */
	@SecurityMapping(title = "微信二级频道楼层修改", value = "/weixinchannelfloor_edit*", rtype = "admin", rname = "微信二级频道楼层", rcode = "admin_weixinchannelfloor", rgroup = "运营")
	@RequestMapping({ "/weixinchannelfloor_edit" })
	public ModelAndView weixinchannelfloor_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String channel_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixinchannelfloor_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			WeixinChannelFloor weixinChannelFloor = this.weixinchannelfloorService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", weixinChannelFloor);
			mv.addObject("sequence",
					Integer.valueOf(weixinChannelFloor.getSequence()));
			mv.addObject("edit_type", "floor");
			mv.addObject("status",
					Integer.valueOf(weixinChannelFloor.getDisplay()));
			mv.addObject("channel_id", channel_id);
		}
		return mv;
	}
	
	/**
	 * 微信二级频道楼层删除
	 * @param request
	 * @param response
	 * @param mulitId
	 */
	@SecurityMapping(title = "微信二级频道楼层删除", value = "/weixinchannelfloor_del*", rtype = "admin", rname = "微信二级频道楼层", rcode = "admin_weixinchannelfloor", rgroup = "运营")
	@RequestMapping({ "/weixinchannelfloor_del" })
	public void weixinchannelfloor_deleteById(HttpServletRequest request,
			HttpServletResponse response, String mulitId) {
		String return_str = "";
		String[] ids = mulitId.split(",");
		List<Long> wIds = Lists.newArrayList();
		for (String id : ids) {
			if (!id.equals("")) {
				wIds.add(Long.parseLong(id.trim()));
			}
		}
		
		this.weixinChannelFloorService.batchDeleteByIds(wIds);
		
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
	 * 微信二级频道行修改
	 * @param request
	 * @param response
	 * @param id
	 * @param num
	 * @return
	 */
	
	@SecurityMapping(title = "微信二级频道行修改", value = "/weixinchannelfloor_line_edit*", rtype = "admin", rname = "微信二级频道楼层", rcode = "admin_weixinchannelfloor", rgroup = "运营")
	@RequestMapping({ "/weixinchannelfloor_line_edit" })
	public ModelAndView weixinchannelfloor_line_edit(
			HttpServletRequest request, HttpServletResponse response,
			String id, String num) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixinchannelfloor_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			WeixinChannelFloor weixinChannelFloor = this.weixinchannelfloorService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			mv.addObject("obj", weixinChannelFloor);
			mv.addObject("edit_type", "line");
			mv.addObject("channel_id", weixinChannelFloor.getWeixin_ch_id());
			mv.addObject("line_num",
					Integer.valueOf(CommUtil.null2Int(num) - 1));
			List list = JSON.parseArray(weixinChannelFloor.getLines_info());
			Map<String, Object> map = (Map) list
					.get(CommUtil.null2Int(num) - 1);
			mv.addObject("line", map);
			mv.addObject("sequence", map.get("sequence"));
			mv.addObject("line_type", map.get("line_type"));
			mv.addObject("line_info", map.get("line_info"));
			mv.addObject("status", map.get("display"));
			mv.addObject("weixinChannelFloorTools",
					this.weixinChannelFloorTools);
		}
		return mv;
	}
	
	/**
	 * 微信二级频道楼层添加
	 * @param request
	 * @param response
	 * @param channel_id
	 * @return
	 */
	@SecurityMapping(title = "微信二级频道楼层添加", value = "/weixinchannelfloor_add*", rtype = "admin", rname = "微信二级频道楼层", rcode = "admin_weixinchannelfloor", rgroup = "运营")
	@RequestMapping({ "/weixinchannelfloor_add" })
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String channel_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixinchannelfloor_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("edit_type", "all");
		mv.addObject("channel_id", channel_id);
		return mv;
	}
	
	/**
	 * 微信二级频道行保存
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
	 * @param channel_id
	 * @param more_link
	 */
	@SecurityMapping(title = "微信二级频道行保存", value = "/weixinchannelfloor_line_save*", rtype = "admin", rname = "微信二级频道楼层", rcode = "admin_weixinchannelfloor", rgroup = "运营")
	@RequestMapping({ "/weixinchannelfloor_line_save" })
	public void weixinchannelfloor_line_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String title,
			String sequence, String status, String edit_type, String line_num,
			String line_type, String line_info, String channel_id,
			String more_link) {
		WeixinChannelFloor weixinChannelFloor = null;
		if ((id != null) && (!id.equals(""))) {
			weixinChannelFloor = this.weixinchannelfloorService
					.selectByPrimaryKey(CommUtil.null2Long(id));
		} else {
			weixinChannelFloor = new WeixinChannelFloor();
			weixinChannelFloor.setAddTime(new Date());
		}
		if (StringUtils.isNotBlank(channel_id)) {
			weixinChannelFloor.setWeixin_ch_id(CommUtil.null2Long(channel_id));
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("line_type", line_type);
		if ((title != null) && (!title.equals(""))) {
			weixinChannelFloor.setTitle(title);
			if (StringUtils.isNotBlank(more_link)) {
				weixinChannelFloor.setMore_link(more_link);
			}
			weixinChannelFloor.setSequence(CommUtil.null2Int(sequence));
			map.put("sequence", Integer.valueOf(0));
			if (CommUtil.null2Boolean(status)) {
				weixinChannelFloor.setDisplay(1);
			} else {
				weixinChannelFloor.setDisplay(0);
			}
			map.put("display", Integer.valueOf(0));
		} else {
			map.put("sequence", Integer.valueOf(CommUtil.null2Int(sequence)));
			map.put("display", Integer.valueOf(1));
		}
		String app_line_info = weixinChannelFloor.getLines_info();
		List app_line_info_list = null;
		if ((app_line_info != null) && (!app_line_info.equals(""))) {
			app_line_info_list = JSON.parseArray(app_line_info);
		} else {
			app_line_info_list = Lists.newArrayList();
		}
		List list = Lists.newArrayList();
		for (String str : line_info.split(";")) {
			if (!str.equals("")) {
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
			app_line_info_list.remove(index);
			app_line_info_list.add(index, map);
		} else {
			app_line_info_list.add(map);
		}
		Collections.sort(app_line_info_list, new MyComparator());
		weixinChannelFloor.setLines_info(JSON.toJSONString(app_line_info_list));
		if ((id != null) && (!id.equals(""))) {
			this.weixinchannelfloorService.updateById(weixinChannelFloor);
		} else {
			this.weixinchannelfloorService.saveEntity(weixinChannelFloor);
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
	 * 微信二级频道楼层ajax
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "微信二级频道楼层ajax", value = "/weixinchannelfloor_ajax*", rtype = "admin", rname = "微信二级频道楼层", rcode = "admin_weixinchannelfloor", rgroup = "运营")
	@RequestMapping({ "/weixinchannelfloor_ajax" })
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		WeixinChannelFloor obj = this.weixinchannelfloorService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(id)));
		Field[] fields = WeixinChannelFloor.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		this.weixinchannelfloorService.updateById(obj);
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
	 * 微信二级频道图片加载
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param album_id
	 * @return
	 */
	@SecurityMapping(title = "微信二级频道图片加载", value = "/weixinchannelfloor_imgs*", rtype = "admin", rname = "微信二级频道楼层", rcode = "admin_weixinchannelfloor", rgroup = "运营")
	@RequestMapping({ "/weixinchannelfloor_imgs" })
	public ModelAndView weixin_index_imgs(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String album_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixinchannelfloor_imgs.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,5, "addTime", "desc");
		maps.put("album_id", CommUtil.null2Long(album_id));
		maps.put("redpig_userRole", "admin");
		
		IPageList pList = this.accessoryService.list(maps);
		String photo_url = CommUtil.getURL(request) + "/app_index_imgs";
		mv.addObject("photos", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(photo_url,
				"", pList.getCurrentPage(), pList.getPages(),
				pList.getPageSize()));
		mv.addObject("album_id", album_id);
		return mv;
	}
	
	/**
	 * 微信二级频道图片上传
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "微信二级频道图片上传", value = "/weixinchannelfloor_image_upload*", rtype = "admin", rname = "微信二级频道图片上传", rcode = "weixinchannelfloor_image_upload", rgroup = "运营")
	@RequestMapping({ "/weixinchannelfloor_image_upload" })
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
	
	/**
	 * 微信二级频道商品查找
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param gc_id
	 * @param goods_name
	 * @return
	 */
	@SecurityMapping(title = "微信二级频道商品查找", value = "/weixinchannelfloor_goods_search*", rtype = "admin", rname = "微信二级频道楼层", rcode = "admin_weixinchannelfloor", rgroup = "运营")
	@RequestMapping({ "/weixinchannelfloor_goods_search" })
	public ModelAndView weixin_index_goods_search(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gc_id,
			String goods_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixinchannelfloor_goods_load.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,5, "addTime","desc");
		
		if (!CommUtil.null2String(gc_id).equals("")) {
			Set ids = genericIds(this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id)));
			maps.put("ids", ids);
		}
		
		if (!CommUtil.null2String(goods_name).equals("")) {
			maps.put("goods_name_like", goods_name);
		}
		
		maps.put("goods_status", 0);
		IPageList pList = this.goodsService.list(maps);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/weixinchannelfloor_goods_search.html", "", "&gc_id="
				+ gc_id + "&goods_name=" + goods_name, pList, mv);
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
