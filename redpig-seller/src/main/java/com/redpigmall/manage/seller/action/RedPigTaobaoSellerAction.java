package com.redpigmall.manage.seller.action;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.csvreader.CsvReader;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Album;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.domain.UserGoodsClass;
import com.redpigmall.domain.WaterMark;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigTaobaoSellerAction.java
 * </p>
 * 
 * <p>
 * Description:淘宝数据控制器 1.0版本
 * 功能包括：淘宝csv导入，目前支持淘宝助理5.7的数据导入，包括tbi图片的导入（自动生成大、中、小三种图片尺寸）
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
 * @date 2014-7-17
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigTaobaoSellerAction extends BaseAction{
	/**
	 * 导入淘宝CSV
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "导入淘宝CSV", value = "/taobao*", rtype = "seller", rname = "淘宝导入", rcode = "taobao_seller", rgroup = "商品管理")
	@RequestMapping({ "/taobao" })
	public ModelAndView taobao(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/taobao.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String taobao_upload_status = CommUtil.null2String(request.getSession(
				false).getAttribute("taobao_upload_status"));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (taobao_upload_status.equals("")) {
			Map<String, Object> params = RedPigMaps.newParent(-1, "sequence", "asc");
			params.put("user_id", user.getId());
			params.put("display", Boolean.valueOf(true));
			
			List<UserGoodsClass> ugcs = this.userGoodsClassService.queryPageList(params);
			
			mv.addObject("ugcs", ugcs);
			List<GoodsClass> gcs = this.storeTools.query_store_detail_MainGc(user.getStore().getGc_detail_info());
			if (gcs.size() > 0) {
				mv.addObject("gcs", gcs);
			} else {
				GoodsClass parent = this.goodsClassService.selectByPrimaryKey(user.getStore().getGc_main_id());
				mv.addObject("gcs", parent.getChilds());
			}
		}
		if (taobao_upload_status.equals("upload_img")) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/taobao_import_img.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			HashMap<String, Object> params = Maps.newHashMap();
			params.put("user_id", user.getId());
			
			List<Album> alubms = this.albumService.queryPageList(params);
			
			mv.addObject("albums", alubms);
			mv.addObject("already_import_count", request.getSession(false).getAttribute("already_import_count"));
			mv.addObject("no_import_count", request.getSession(false).getAttribute("no_import_count"));
			mv.addObject("jsessionid", request.getSession().getId());
		}
		if (taobao_upload_status.equals("upload_finish")) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/taobao_import_finish.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		return mv;
	}
	
	/**
	 * 导入淘宝CSV
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param ugc_ids
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@SecurityMapping(title = "导入淘宝CSV", value = "/taobao_import_csv*", rtype = "seller", rname = "淘宝导入", rcode = "taobao_seller", rgroup = "商品管理")
	@RequestMapping({ "/taobao_import_csv" })
	public ModelAndView taobao_import_csv(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String ugc_ids) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/taobao_import_img.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		String taobao_upload_status = CommUtil.null2String(request.getSession(false).getAttribute("taobao_upload_status"));
		String path = request.getSession().getServletContext().getRealPath("")
				+ File.separator + "csv" + File.separator
				+ user.getStore().getId();
		CommUtil.createFolder(path);
		int already_import_count = 0;
		int no_import_count = 0;
		List<Goods> taobao_goods_list = Lists.newArrayList();
		try {
			Map<String, Object> map = CommUtil.saveFileToServer(request,"taobao_cvs", path, "taobao.cvs", null);
			if (!map.get("fileName").equals("")) {
				String csvFilePath = path + File.separator + "taobao.cvs";
				CsvReader reader = new CsvReader(csvFilePath, '\t',
						Charset.forName("UTF-16LE"));
				reader.readHeaders();
				reader.readHeaders();
				reader.readHeaders();
				int goods_name_pos = 0;
				int goods_price_pos = 7;
				int goods_count_pos = 9;
				int goods_detail_pos = 20;
				int goods_transfee_pos = 11;
				int goods_recommend_pos = 18;
				Album album = this.albumService.getDefaultAlbum(user.getId());
				if (album == null) {
					album = new Album();
					album.setAddTime(new Date());
					album.setAlbum_name("默认相册");
					album.setAlbum_sequence(55536);
					album.setAlbum_default(true);
					this.albumService.saveEntity(album);
				}
				while (reader.readRecord()) {
					Goods goods = new Goods();
					goods.setGoods_name(reader.get(goods_name_pos));
					goods.setStore_price(BigDecimal.valueOf(CommUtil
							.null2Double(reader.get(goods_price_pos))));
					goods.setGoods_price(goods.getStore_price());
					goods.setGoods_inventory(CommUtil.null2Int(reader
							.get(goods_count_pos)));
					goods.setPublish_goods_status(0);
					goods.setGoods_recommend(CommUtil.null2Boolean(reader
							.get(goods_recommend_pos)));
					goods.setGoods_details(reader.get(goods_detail_pos));
					goods.setGoods_type(1);
					goods.setGoods_transfee(CommUtil.null2Int(reader
							.get(goods_transfee_pos)) - 1);
					goods.setGoods_current_price(goods.getStore_price());
					goods.setAddTime(new Date());
					goods.setGoods_seller_time(new Date());
					goods.setGoods_type(1);
					goods.setGoods_store(user.getStore());
					goods.setInventory_type("all");
					GoodsClass gc = null;
					if (gc_id != null) {
						gc = this.goodsClassService.selectByPrimaryKey(CommUtil
								.null2Long(gc_id));
					}
					goods.setGc(gc);
					String[] ugc_id_list = ugc_ids.split(",");

					for (String ugc_id : ugc_id_list) {

						UserGoodsClass ugc = this.userGoodsClassService
								.selectByPrimaryKey(CommUtil.null2Long(ugc_id));
						goods.getGoods_ugcs().add(ugc);
					}
					goods.setGoods_status(-5);
					goods.setPublish_goods_status(0);
					if (store.getGrade().getGoods_audit() == 1) {
						goods.setGoods_status(0);
						goods.setPublish_goods_status(0);
					}
					this.goodsService.saveEntity(goods);
					taobao_goods_list.add(goods);
					already_import_count++;
				}
				reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (already_import_count > 0) {
			HashMap<String, Object> params = Maps.newHashMap();
			params.put("user_id", user.getId());
			
			List<Album> alubms = this.albumService.queryPageList(params);
			
			mv.addObject("albums", alubms);
			mv.addObject("jsessionid", request.getSession().getId());
			request.getSession(false).setAttribute("taobao_goods_list",
					taobao_goods_list);
			request.getSession(false).setAttribute("taobao_upload_status",
					"upload_img");
			request.getSession(false).setAttribute("already_import_count",
					Integer.valueOf(already_import_count));
			request.getSession(false).setAttribute("no_import_count",
					Integer.valueOf(no_import_count));
		}
		mv.addObject("already_import_count",
				Integer.valueOf(already_import_count));
		mv.addObject("no_import_count", Integer.valueOf(no_import_count));
		return mv;
	}
	
	/**
	 * 上传淘宝图片
	 * @param request
	 * @param response
	 * @param album_id
	 * @param ajaxUploadMark
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	@SecurityMapping(title = "上传淘宝图片", value = "/taobao_img_upload*", rtype = "seller", rname = "淘宝导入", rcode = "taobao_seller", rgroup = "商品管理")
	@RequestMapping({ "/taobao_img_upload" })
	public void taobao_img_upload(HttpServletRequest request,
			HttpServletResponse response, String album_id, String ajaxUploadMark) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		String csv_path = request.getSession().getServletContext()
				.getRealPath("")
				+ File.separator
				+ "csv"
				+ File.separator
				+ user.getStore().getId();
		Boolean html5Uploadret = Boolean.valueOf(false);
		Map ajaxUploadInfo = null;
		try {
			String csvFilePath = csv_path + File.separator + "taobao.cvs";
			CsvReader reader = new CsvReader(csvFilePath, '\t',
					Charset.forName("UTF-16LE"));
			reader.readHeaders();
			reader.readHeaders();
			reader.readHeaders();
			int goods_name_pos = 0;
			int goods_price_pos = 7;
			int goods_photo_pos = 28;
			String photo_path = this.storeTools.createUserFolder(request,
					user.getStore())
					+ File.separator + "taobao";

			String photo_url = this.storeTools.createUserFolderURL(user
					.getStore()) + "/taobao";

			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest
					.getFile("imgFile");
			String upload_img_name = file.getOriginalFilename();
			upload_img_name = upload_img_name.substring(0,
					upload_img_name.indexOf("."));
			double fileSize = Double.valueOf(file.getSize()).doubleValue();
			fileSize /= 1048576.0D;
			double csize = CommUtil.fileSize(new File(photo_path));
			double remainSpace = 0.0D;
			if (user.getStore().getGrade().getSpaceSize() != 0.0F) {
				remainSpace = (user.getStore().getGrade().getSpaceSize() * 1024.0F - csize) * 1024.0D;
			} else {
				remainSpace = 1.0E7D;
			}
			Map json_map = Maps.newHashMap();
			List<Goods> goods_list = (List) request.getSession(false).getAttribute("taobao_goods_list");
			Goods goods = new Goods();
			Boolean ret = Boolean.valueOf(false);
			while (reader.readRecord()) {
				if (reader.get(goods_photo_pos).indexOf(upload_img_name) >= 0) {
					String goods_name = reader.get(goods_name_pos);
					double goods_price = CommUtil.null2Double(reader
							.get(goods_price_pos));
					for (Goods temp_goods : goods_list) {
						if ((temp_goods.getGoods_name().equals(goods_name))
								&& (CommUtil.null2Double(temp_goods
										.getGoods_price()) == goods_price)) {
							goods = temp_goods;
							if (reader.get(goods_photo_pos).indexOf(
									upload_img_name) == 0) {
								ret = Boolean.valueOf(true);
							}
						}
					}
				}
			}
			reader.close();
			if (goods != null) {
				if (remainSpace > fileSize) {
					try {
						Map<String, Object> map = CommUtil.saveFileToServer(
								request, "imgFile", photo_path, upload_img_name
										+ ".tbi", null);
						Map<String, Object> params = Maps.newHashMap();
						params.put("store_id", user.getStore().getId());
						
						List<WaterMark> wms = this.waterMarkService.queryPageList(params);
						
						if (wms.size() > 0) {
							WaterMark mark = (WaterMark) wms.get(0);
							if ((mark.getWm_image_open())
									&& (mark.getWm_image() != null)) {
								String pressImg = request.getSession()
										.getServletContext().getRealPath("")
										+ File.separator
										+ mark.getWm_image().getPath()
										+ File.separator
										+ mark.getWm_image().getName();
								String targetImg = photo_path + File.separator
										+ map.get("fileName");
								int pos = mark.getWm_image_pos();
								float alpha = mark.getWm_image_alpha();
								CommUtil.waterMarkWithImage(pressImg,
										targetImg, pos, alpha);
							}
							
							if (mark.getWm_text_open()) {
								String targetImg = photo_path + File.separator
										+ map.get("fileName");
								int pos = mark.getWm_text_pos();
								String text = mark.getWm_text();
								String markContentColor = mark
										.getWm_text_color();
								CommUtil.waterMarkWithText(targetImg,
										targetImg, text, markContentColor,
										new Font(mark.getWm_text_font(), 1,
												mark.getWm_text_font_size()),
										pos, 100.0F);
							}
						}
						Accessory image = new Accessory();
						image.setAddTime(new Date());
						image.setExt((String) map.get("mime"));
						image.setPath(photo_url);
						image.setWidth(CommUtil.null2Int(map.get("width")));
						image.setHeight(CommUtil.null2Int(map.get("height")));
						image.setName(CommUtil.null2String(map.get("fileName")));
						image.setUser(user);
						Album album = null;
						if ((album_id != null) && (!album_id.equals(""))) {
							album = this.albumService.selectByPrimaryKey(CommUtil
									.null2Long(album_id));
						} else {
							album = this.albumService.getDefaultAlbum(CommUtil
									.null2Long(user.getId()));
							if (album == null) {
								album = new Album();
								album.setAddTime(new Date());
								album.setAlbum_name("默认相册");
								album.setAlbum_sequence(55536);
								album.setAlbum_default(true);
								this.albumService.saveEntity(album);
							}
						}
						image.setAlbum(album);
						this.accessoryService.saveEntity(image);
						html5Uploadret = true;
						
						if ((html5Uploadret.booleanValue())
								&& (ajaxUploadMark != null)) {
							ajaxUploadInfo = Maps.newHashMap();
							ajaxUploadInfo.put("url", image.getPath() + "/"
									+ image.getName());
						}
						if (ret.booleanValue()) {
							goods.setGoods_main_photo(image);
						} else {
							goods.getGoods_photos().add(image);
						}
						this.goodsService.updateById(goods);
						json_map.put("url", CommUtil.getURL(request) + "/"
								+ photo_url + "/" + image.getName());
						json_map.put("id", image.getId());
						json_map.put("remainSpace", Double
								.valueOf(remainSpace == 1.0E7D ? 0.0D
										: remainSpace));

						String ext = image.getExt().indexOf(".") < 0 ? "."
								+ image.getExt() : image.getExt();
						String source = request.getSession()
								.getServletContext().getRealPath("/")
								+ image.getPath()
								+ File.separator
								+ image.getName();
						String target = source + "_small" + ext;
						CommUtil.createSmall(source, target, this.configService
								.getSysConfig().getSmallWidth(),
								this.configService.getSysConfig()
										.getSmallHeight());

						String midext = image.getExt().indexOf(".") < 0 ? "."
								+ image.getExt() : image.getExt();
						String midtarget = source + "_middle" + ext;
						CommUtil.createSmall(source, midtarget,
								this.configService.getSysConfig()
										.getMiddleWidth(), this.configService
										.getSysConfig().getMiddleHeight());
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					json_map.put("url", "");
					json_map.put("id", "");
					json_map.put("remainSpace", Integer.valueOf(0));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			if (ajaxUploadMark != null) {
				writer.print(JSON.toJSONString(ajaxUploadInfo));
			} else {
				writer.print(html5Uploadret);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 淘宝导入完成
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "淘宝导入完成", value = "/taobao_import_finish*", rtype = "seller", rname = "淘宝导入", rcode = "taobao_seller", rgroup = "商品管理")
	@RequestMapping({ "/taobao_import_finish" })
	public ModelAndView taobao_import_finish(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/taobao_import_finish.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		request.getSession(false).removeAttribute("taobao_upload_status");
		request.getSession(false).removeAttribute("taobao_goods_list");
		request.getSession(false).removeAttribute("already_import_count");
		request.getSession(false).removeAttribute("no_import_count");
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param code
	 * @param state
	 * @return
	 */
	@RequestMapping({ "/taobao_authorize" })
	public ModelAndView taobao_authorize(HttpServletRequest request,
			HttpServletResponse response, String code, String state) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/taobao_import_finish.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		return mv;
	}
}
