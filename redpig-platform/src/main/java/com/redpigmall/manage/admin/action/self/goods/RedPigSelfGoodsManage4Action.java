package com.redpigmall.manage.admin.action.self.goods;

import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.domain.virtual.SysMap;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Album;
import com.redpigmall.domain.Evaluate;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsTag;
import com.redpigmall.domain.GoodsType;
import com.redpigmall.domain.GoodsTypeProperty;
import com.redpigmall.domain.User;
import com.redpigmall.domain.WaterMark;

/**
 * 
 * <p>
 * Title: RedPigGoodsSelfManageAction.java
 * </p>
 * 
 * <p>
 * Description:自营商品管理控制器，平台可发布商品并进行管理
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
 * @date 2014年4月25日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigSelfGoodsManage4Action extends BaseAction{
	
	/**
	 * 商品图片列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param type
	 * @param album_id
	 * @return
	 */
	@SecurityMapping(title = "商品图片列表", value = "/goods_img*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping({ "/goods_img" })
	public ModelAndView goods_img(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String type,
			String album_id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/" + type + ".html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,20,"addTime", "desc");
        maps.put("album_id",CommUtil.null2Long(album_id));
        maps.put("redpig_userRole","admin");
        
		IPageList pList = this.accessoryService.list(maps);
		String photo_url = CommUtil.getURL(request) + "/goods_img";
		mv.addObject("photos", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(photo_url,
				"", pList.getCurrentPage(), pList.getPages(),
				pList.getPageSize()));
		mv.addObject("album_id", album_id);
		return mv;
	}
	
	/**
	 * 商品二维码生成
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @param uncheck_mulitId
	 * @param op
	 * @param brand_id
	 * @param goods_name
	 * @param u_admin_id
	 * @return
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "商品二维码生成", value = "/goods_self_qr*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping({ "/goods_self_qr" })
	public String goods_self_qr(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage,
			String uncheck_mulitId, String op, String brand_id,
			String goods_name, String u_admin_id) throws ClassNotFoundException {
		int status = 0;
		if (CommUtil.null2String(op).equals("storage")) {
			status = 1;
		}
		if (CommUtil.null2String(op).equals("out")) {
			status = -2;
		}
		Goods goods;
		String qr_img_path;
		if ("all".equals(mulitId)) {
			List<Goods> list = generatorQuery(status, mulitId, uncheck_mulitId,
					brand_id, goods_name, u_admin_id);
			for (int i = 0; i < list.size(); i++) {
				goods = (Goods) list.get(i);
				qr_img_path = this.goodsTools.createGoodsQR(request, goods);
				goods.setQr_img_path(qr_img_path);
				this.goodsService.updateById(goods);
			}
		} else {
			String[] ids = mulitId.split(",");
			for (String id : ids) {
				if (id != null) {
					goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
					if (goods != null) {
						qr_img_path = this.goodsTools.createGoodsQR(request,goods);
						goods.setQr_img_path(qr_img_path);
						this.goodsService.updateById(goods);
					}
				}
			}
		}
		return "redirect:goods_self_list?currentPage=" + currentPage;
	}
	
	/**
	 * 商品二维码Excel生成并下载
	 * @param request
	 * @param response
	 * @param id
	 * @throws IOException
	 */
	@SecurityMapping(title = "商品二维码Excel生成并下载", value = "/goods_self_f_code_download*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping({ "/goods_self_f_code_download" })
	public void goods_self_f_code_download(HttpServletRequest request,
			HttpServletResponse response, String id) throws IOException {
		Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		String excel_url = "";
		if ((obj.getF_sale_type() == 1) && (obj.getGoods_type() == 0)
				&& (!CommUtil.null2String(obj.getGoods_f_code()).equals(""))) {
			List<Map> list = JSON.parseArray(obj.getGoods_f_code(), Map.class);
			String name = CommUtil.null2String(UUID.randomUUID());
			String path = request.getSession().getServletContext()
					.getRealPath("/")
					+ File.separator + "excel";
			CommUtil.createFolder(path);
			path = path + File.separator + name + ".xls";
			FileOutputStream out = new FileOutputStream(new File(path));
			exportList2Excel("F码列表", new String[] { "F码信息", "F码状态" }, list, out);
			excel_url = CommUtil.getURL(request) + "/excel/" + name + ".xls";
		}
		response.sendRedirect(excel_url);
	}
	
	/**
	 * 商品详情中的图片上传
	 * @param request
	 * @param response
	 * @param album_id
	 * @param ajaxUploadMark
	 */
	@SecurityMapping(title = "商品详情中的图片上传", value = "/goods_detail_image_upload*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping({ "/goods_detail_image_upload" })
	public void album_image_upload(HttpServletRequest request,
			HttpServletResponse response, String album_id, String ajaxUploadMark) {
		Boolean html5Uploadret = Boolean.valueOf(false);
		Map ajaxUploadInfo = Maps.newHashMap();
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		String path = this.storeTools.createAdminFolder(request);
		String url = this.storeTools.createAdminFolderURL();
		try {
			Map map = CommUtil.saveFileToServer(request, "fileImage", path,null, null);
			Map params = Maps.newHashMap();
			params.put("user_id", user.getId());
			
			List<WaterMark> wms = this.waterMarkService.queryPageList(params);
			
			if (wms.size() > 0) {
				WaterMark mark = (WaterMark) wms.get(0);
				if (mark.getWm_image_open()) {
					String pressImg = request.getSession().getServletContext()
							.getRealPath("")
							+ File.separator
							+ mark.getWm_image().getPath()
							+ File.separator + mark.getWm_image().getName();
					String targetImg = path + File.separator
							+ map.get("fileName");
					int pos = mark.getWm_image_pos();
					float alpha = mark.getWm_image_alpha();
					CommUtil.waterMarkWithImage(pressImg, targetImg, pos, alpha);
				}
				if (mark.getWm_text_open()) {
					String targetImg = path + File.separator
							+ map.get("fileName");
					int pos = mark.getWm_text_pos();
					String text = mark.getWm_text();
					String markContentColor = mark.getWm_text_color();
					CommUtil.waterMarkWithText(targetImg, targetImg, text,
							markContentColor, new Font(mark.getWm_text_font(),
									1, mark.getWm_text_font_size()), pos,
							100.0F);
				}
			}
			Accessory image = new Accessory();
			image.setAddTime(new Date());
			image.setExt((String) map.get("mime"));
			image.setPath(url);
			image.setWidth(CommUtil.null2Int(map.get("width")));
			image.setHeight(CommUtil.null2Int(map.get("height")));
			image.setName(CommUtil.null2String(map.get("fileName")));
			image.setUser(user);
			Album album = null;
			if ((album_id != null) && (!album_id.equals(""))) {
				album = this.albumService.selectByPrimaryKey(CommUtil
						.null2Long(album_id));
			} else {
				album = this.albumService.getDefaultAlbum(user.getId());
				if (album == null) {
					album = new Album();
					album.setAddTime(new Date());
					album.setAlbum_name("默认相册【" + user.getUserName() + "】");
					album.setAlbum_sequence(55536);
					album.setAlbum_default(true);
					album.setUser(user);
					this.albumService.saveEntity(album);
				}
			}
			image.setAlbum(album);
			this.accessoryService.saveEntity(image);
			html5Uploadret = Boolean.valueOf(true);
			ajaxUploadInfo.put("url", image.getPath() + "/" + image.getName());

			String ext = image.getExt().indexOf(".") < 0 ? "." + image.getExt()
					: image.getExt();
			String source = request.getSession().getServletContext()
					.getRealPath("/")
					+ image.getPath() + File.separator + image.getName();
			String target = source + "_small" + ext;
			CommUtil.createSmall(source, target, this.configService
					.getSysConfig().getSmallWidth(), this.configService
					.getSysConfig().getSmallHeight());

			String midext = image.getExt().indexOf(".") < 0 ? "."
					+ image.getExt() : image.getExt();
			String midtarget = source + "_middle" + ext;
			CommUtil.createSmall(source, midtarget, this.configService
					.getSysConfig().getMiddleWidth(), this.configService
					.getSysConfig().getMiddleHeight());
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
	 * 添加商品标签
	 * @param request
	 * @param response
	 * @param tagname
	 */
	@SecurityMapping(title = "添加商品标签", value = "/add_goodstag*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping({ "/add_goodstag" })
	public void add_goodstag(HttpServletRequest request,
			HttpServletResponse response, String tagname) {
		Map map = Maps.newHashMap();
		if ((tagname != null) && (!tagname.equals(""))) {
			GoodsTag goodsTag = getGoodsTag(tagname);
			map.put("id", goodsTag.getId());
			map.put("name", goodsTag.getTagname());
		}
		String json = JSON.toJSONString(map);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 商品快速补库存
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param uncheck_mulitId
	 * @param op
	 * @param brand_id
	 * @param goods_name
	 * @param u_admin_id
	 * @param count
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商品快速补库存", value = "/goods_self_quick_inventory*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping({ "/goods_self_quick_inventory" })
	public ModelAndView goods_self_quick_inventory(HttpServletRequest request,
			HttpServletResponse response, String mulitId,
			String uncheck_mulitId, String op, String brand_id,
			String goods_name, String u_admin_id, String count,
			String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		int status_op = 0;
		if (CommUtil.null2String(op).equals("storage")) {
			status_op = 1;
		}
		if (CommUtil.null2String(op).equals("out")) {
			status_op = -2;
		}
		String status = "0";
		Goods goods;
		if ("all".equals(mulitId)) {
			List<Goods> list = generatorQuery(status_op, mulitId,
					uncheck_mulitId, brand_id, goods_name, u_admin_id);
			for (int i = 0; i < list.size(); i++) {
				goods = (Goods) list.get(i);
				goods.setGoods_inventory(goods.getGoods_inventory() + CommUtil.null2Int(count));
				if (goods.getGoods_inventory() > goods.getGoods_warn_inventory()) {
					goods.setWarn_inventory_status(0);
				}
				this.goodsService.updateById(goods);
			}
		} else {
			String[] ids = mulitId.split(",");

			for (String id : ids) {
				if (!id.equals("")) {
					goods = this.goodsService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
					goods.setGoods_inventory(goods.getGoods_inventory() + CommUtil.null2Int(count));
					if (goods.getGoods_inventory() > goods.getGoods_warn_inventory()) {
						goods.setWarn_inventory_status(0);
					}
					this.goodsService.updateById(goods);
				}
			}
		}
		mv.addObject("op_title", "补仓成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/goods_self_list?goods_status=1&currentPage="
				+ currentPage);
		return mv;
	}
	
	/**
	 * 属性Ajax保存
	 * @param request
	 * @param response
	 * @param count
	 * @param params
	 * @param goodsType
	 * @param goodsclass
	 * @return
	 */
	@SecurityMapping(title = "属性Ajax保存", value = "/ajax_save_gtp*", rtype = "seller", rname = "属性管理", rcode = "gtp_seller", rgroup = "商品")
	@RequestMapping({ "/ajax_save_gtp" })
	public ModelAndView ajax_save_gtp(HttpServletRequest request,
			HttpServletResponse response, String count, String params,
			String goodsType, String goodsclass) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_type_load.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		String[] gtps = params.substring(1).split("&");
		List gtpList = Lists.newArrayList();
		for (int i = 0; i < CommUtil.null2Int(count); i++) {
			if (gtps.length > 0) {
				String[] gtp_info = gtps[i].split("/");
				GoodsTypeProperty goodsTypeProperty = new GoodsTypeProperty();
				goodsTypeProperty.setName(gtp_info[0]);
				goodsTypeProperty.setValue(gtp_info[1]);
				goodsTypeProperty.setSequence(CommUtil.null2Int(gtp_info[2]));
				goodsTypeProperty.setAddTime(new Date());
				goodsTypeProperty.setDisplay(true);
				GoodsType goodstype = this.goodsTypeService.selectByPrimaryKey(CommUtil.null2Long(goodsType));
				if (goodstype == null) {
					GoodsClass goodsClass = this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(goodsclass));
					goodstype = new GoodsType();
					goodstype.setAddTime(new Date());
					this.goodsTypeService.saveEntity(goodstype);
					goodsClass.setGoodsType(goodstype);
					this.goodsClassService.updateById(goodsClass);
				}
				goodsTypeProperty.setGoodsType(goodstype);
				this.gtpService.saveEntity(goodsTypeProperty);
				gtpList.add(goodsTypeProperty);
			}
		}

		Comparator comparator = new Comparator<GoodsTypeProperty>() {
			public int compare(GoodsTypeProperty g1, GoodsTypeProperty g2) {

				if (g1.getSequence() != g2.getSequence()) {
					return g1.getSequence() - g2.getSequence();
				}
				return g1.getSequence();
			}
		};
		Collections.sort(gtpList, comparator);
		mv.addObject("gtpList", gtpList);
		return mv;
	}

	private void goodsListDel(Goods goods) {
		Map map = Maps.newHashMap();
		map.put("goods_id", goods.getId());
		
		List<GoodsCart> goodCarts = this.goodsCartService.queryPageList(map);
		
		Long ofid = null;
		List<Evaluate> evaluates = goods.getEvaluates();
		for (Evaluate e : evaluates) {
			this.evaluateService.deleteById(e.getId());
		}
		goods.getGoods_ugcs().clear();
		goods.getGoods_ugcs().clear();
		goods.getGoods_photos().clear();
		goods.getGoods_ugcs().clear();
		goods.getGoods_specs().clear();
		for (GoodsCart gc : goods.getCarts()) {
			gc.getGsps().clear();
			this.goodsCartService.deleteById(gc.getId());
		}
		this.goodsService.deleteById(goods.getId());

		this.goodsTools.deleteGoodsLucene(goods);
	}
	
	private List<Goods> generatorQuery(int status, String mulitId,
			String uncheck_mulitId, String brand_id, String goods_name,
			String u_admin_id) {
		Map<String,Object> maps = Maps.newHashMap();
        maps.put("goods_type",0);
        maps.put("goods_status",status);
        
		if ((uncheck_mulitId != null) && (!"".equals(uncheck_mulitId))) {
			uncheck_mulitId = uncheck_mulitId.substring(0,
					uncheck_mulitId.length() - 1);
			String[] mIds = uncheck_mulitId.split(",");
			List<Long> ids = Lists.newArrayList();
			for(String id:mIds){
				ids.add(Long.parseLong(id));
			}
			maps.put("ids",ids);
		}
		if ((brand_id != null) && (!brand_id.equals(""))) {
			maps.put("goods_brand_id",brand_id);
		}
		if ((u_admin_id != null) && (!u_admin_id.equals(""))) {
			maps.put("user_admin_id",u_admin_id);
		}
		if ((goods_name != null) && (!goods_name.equals(""))) {
			maps.put("goods_name_like",goods_name);
		}
		List<Goods> list = this.goodsService.queryPageList(maps);
		return list;
	}

	private static boolean exportList2Excel(String title, String[] headers,
			List<Map> list, OutputStream out) {
		boolean ret = true;

		HSSFWorkbook workbook = new HSSFWorkbook();

		HSSFSheet sheet = workbook.createSheet(title);

		sheet.setDefaultColumnWidth(20);

		HSSFCellStyle style = workbook.createCellStyle();

		style.setFillForegroundColor((short) 40);
		style.setFillPattern((short) 1);

		style.setBorderBottom((short) 1);
		style.setBorderLeft((short) 1);
		style.setBorderRight((short) 1);
		style.setBorderTop((short) 1);

		style.setAlignment((short) 2);

		HSSFFont font = workbook.createFont();
		font.setColor((short) 20);
		font.setFontHeightInPoints((short) 14);
		font.setBoldweight((short) 700);

		style.setFont(font);

		HSSFCellStyle style_ = workbook.createCellStyle();
		style_.setFillForegroundColor((short) 43);
		style_.setFillPattern((short) 1);
		style_.setBorderBottom((short) 1);
		style_.setBorderLeft((short) 1);
		style_.setBorderRight((short) 1);
		style_.setBorderTop((short) 1);
		style_.setAlignment((short) 2);
		style_.setVerticalAlignment((short) 1);

		HSSFFont font_ = workbook.createFont();

		font_.setFontHeightInPoints((short) 14);

		style_.setFont(font_);

		HSSFRow row = sheet.createRow(0);
		for (int i = 0; i < headers.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(new HSSFRichTextString(headers[i]));
		}
		int index = 0;
		for (Map map : list) {
			index++;
			row = sheet.createRow(index);
			String value = CommUtil.null2String(map.get("code"));
			HSSFCell cell = row.createCell(0);
			cell.setCellStyle(style_);
			cell.setCellValue(value);
			value = CommUtil.null2Int(map.get("status")) == 0 ? "未使用" : "已使用";
			cell = row.createCell(1);
			cell.setCellStyle(style_);
			cell.setCellValue(value);
		}
		try {
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			ret = false;
			e.printStackTrace();
		}
		return ret;
	}

	void generate_goodstag(Goods goods, String ids) {
		Set<Long> set = Sets.newTreeSet();
		Map map = Maps.newHashMap();

		if ((goods.getGoods_tags() != null)
				&& (!goods.getGoods_tags().equals(""))) {
			map = JSON.parseObject(goods.getGoods_tags());

			if (map.containsKey("generated_tag")) {
				set = Sets.newTreeSet();
			} else {
				set = (Set) map.get("generated_tag");
			}
		}
		if (goods.getGc() != null) {
			GoodsTag goodsTag = getGoodsTag(goods.getGc().getClassName());
			set.add(goodsTag.getId());
		}
		if (goods.getGoods_brand() != null) {
			GoodsTag goodsTag = getGoodsTag(goods.getGoods_brand().getName());
			set.add(goodsTag.getId());
		}
		map.put("generated_tag", set);

		Set<Long> mSet = Sets.newTreeSet();
		if ((ids != null) && (!ids.equals(""))) {

			for (String str : ids.split(",")) {

				mSet.add(CommUtil.null2Long(str));
			}
		}
		map.put("custom_tag", mSet);

		goods.setGoods_tags(JSON.toJSONString(map));
	}

	public GoodsTag getGoodsTag(String tagname) {
		Map params = Maps.newHashMap();
		params.put("tagname", tagname);
		
		List<GoodsTag> list = this.goodsTagService.queryPageList(params);
		
		if (list.size() > 0) {
			return (GoodsTag) list.get(0);
		}
		GoodsTag goodsTag = new GoodsTag();
		goodsTag.setAddTime(new Date());
		goodsTag.setTagname(tagname);
		this.goodsTagService.saveEntity(goodsTag);
		return goodsTag;
	}

	private void isAdminAlbumExist() {
		Map params = Maps.newHashMap();
		params.put("user_userRole1", "ADMIN");
		params.put("user_userRole2", "ADMIN_SELLER");
		
		List<Album> albums = this.albumService.queryPageList(params);
		
		if (albums.size() == 0) {
			Album album = new Album();
			album.setAddTime(new Date());
			album.setAlbum_default(true);
			album.setAlbum_name("默认相册");
			album.setAlbum_sequence(55536);
			album.setUser(SecurityUserHolder.getCurrentUser());
			this.albumService.saveEntity(album);
		}
	}
}
