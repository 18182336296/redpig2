package com.redpigmall.manage.admin.action.user;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Advert;
import com.redpigmall.domain.Album;
import com.redpigmall.domain.ComplaintGoods;
import com.redpigmall.domain.CouponInfo;
import com.redpigmall.domain.Evaluate;
import com.redpigmall.domain.GoldLog;
import com.redpigmall.domain.GoldRecord;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.domain.GoodsSpecProperty;
import com.redpigmall.domain.GoodsSpecification;
import com.redpigmall.domain.GroupInfo;
import com.redpigmall.domain.GroupLifeGoods;
import com.redpigmall.domain.IntegralGoodsOrder;
import com.redpigmall.domain.IntegralLog;
import com.redpigmall.domain.Message;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.PayoffLog;
import com.redpigmall.domain.PredepositCash;
import com.redpigmall.domain.Role;
import com.redpigmall.domain.SnsAttention;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.StoreGrade;
import com.redpigmall.domain.StorePoint;
import com.redpigmall.domain.SysLog;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigUserManageAction.java
 * </p>
 * 
 * <p>
 * Description:
 * 商城会员管理
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
 * @date 2016年5月27日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigUserManageAction extends BaseAction{
  
	/**
	 * 会员添加
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "会员添加", value = "/user_add*", rtype = "admin", rname = "会员管理", rcode = "user_manage", rgroup = "会员")
	@RequestMapping({ "/user_add" })
	public ModelAndView user_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/user_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		return mv;
	}
	
	/**
	 * 会员编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param op
	 * @return
	 */
	@SecurityMapping(title = "会员编辑", value = "/user_edit*", rtype = "admin", rname = "会员管理", rcode = "user_manage", rgroup = "会员")
	@RequestMapping({ "/user_edit" })
	public ModelAndView user_edit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, String op) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/user_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		mv.addObject("obj", this.redPigUserService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id))));
		
		mv.addObject("edit", Boolean.valueOf(true));
		return mv;
	}
	
	/**
	 * 企业用户
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "企业用户", value = "/company_user*", rtype = "admin", rname = "会员管理", rcode = "user_manage", rgroup = "会员")
	@RequestMapping({ "/company_user" })
	public ModelAndView company_user(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/company_user.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		mv.addObject("obj", this.redPigUserService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id))));
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * 会员列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param condition
	 * @param value
	 * @return
	 */
	@SecurityMapping(title = "会员列表", value = "/user_list*", rtype = "admin", rname = "会员管理", rcode = "user_manage", rgroup = "会员")
	@RequestMapping({ "/user_list" })
	public ModelAndView user_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType, 
			String condition, 
			String value) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/user_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		Map<String, Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);

		params.put("redpig_no_userRole", "ADMIN");
		
		if ((condition != null) && (!CommUtil.null2String(value).equals(""))) {
			if (condition.equals("userName")) {
				params.put("redpig_like_userName", value);
			}
			
			if (condition.equals("email")) {
				params.put("email", value);
			}
			
			if (condition.equals("trueName")) {
				params.put("trueName", value);
			}
			
			mv.addObject("value", value);
			mv.addObject("condition", condition);
		}

		params.put("parent", -1);

		IPageList pList = this.redPigUserService.queryPagesWithNoRelations(params);

		String url = this.redPigSysConfigService.getSysConfig().getAddress();

		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		
		CommUtil.saveIPageList2ModelAndView(url + "/user_list", "", "",pList, mv);
		
		mv.addObject("userRole", "USER");
		mv.addObject("storeTools", this.redPigStoreTools);
		mv.addObject("integralViewTools", this.redPigIntegralViewTools);
		return mv;
	}
	
	/**
	 * 会员保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param role_ids
	 * @param list_url
	 * @param add_url
	 * @param userName
	 * @param password
	 * @return
	 */
	@SecurityMapping(title = "会员保存", value = "/user_save*", rtype = "admin", rname = "会员管理", rcode = "user_manage", rgroup = "会员")
	@RequestMapping({ "/user_save" })
	public ModelAndView user_save(HttpServletRequest request,
			HttpServletResponse response, String id, String role_ids,
			String list_url, String add_url, String userName, String password) {
		User user = null;
		if (id.equals("")) {
			user = (User) WebForm.toPo(request, User.class);
			user.setAddTime(new Date());
		} else {
			User u = this.redPigUserService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			user = (User) WebForm.toPo(request, u);
		}
		if ((userName != null) && (!userName.equals(""))) {
			user.setUserName(userName);
		}
		if ((password != null) && (!password.equals(""))) {
			user.setPassword(Md5Encrypt.md5(password).toLowerCase());
		}
		if (id.equals("")) {
			Map<String, Object> params = Maps.newHashMap();
			
			user.setUserRole("BUYER");
			
			params.put("type", "BUYER");
			List<Role> roles = this.redPigRoleService.queryPageList(params);
			
			// 1、先把用户保存了
			this.redPigUserService.save(user);
			
			// 2、在把user-role关系关联上
			
			this.redPigUserService.saveUserRole(user.getId(), roles);
			
			Album album = new Album();
			album.setAddTime(new Date());
			album.setAlbum_default(true);
			album.setAlbum_name("默认相册");
			album.setAlbum_sequence(55536);
			album.setUser(user);
			this.redPigAlbumService.saveEntity(album);
		} else {
			this.redPigUserService.updateById(user);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存用户成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url);
		}
		return mv;
	}
	
	/**
	 * 会员删除
	 * @param request
	 * @param mulitId
	 * @param currentPage
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "会员删除", value = "/user_del*", rtype = "admin", rname = "会员管理", rcode = "user_manage", rgroup = "会员")
	@RequestMapping({ "/user_del" })
	public String user_del(
			HttpServletRequest request, 
			String mulitId,
			String currentPage) {
		
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");

		for (String id : ids) {

			if (!id.equals("")) {
				User parent = this.redPigUserService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));

				if (!parent.getUsername().equals("admin")) {
					List<PredepositCash> PredepositCash_list;
					List<IntegralGoodsOrder> integralGoodsOrders;
					for (User user : parent.getChilds()) {
						user.getRoles().clear();
						List<OrderForm> ofs;
						if (user.getStore() != null) {
							if (parent.getStore() != null) {
								store_del(request, user.getStore().getId());
							}
							Map<String, Object> map = Maps.newHashMap();
							map.put("user_id", user.getId().toString());
							ofs = this.redPigOrderFormService
									.queryPageList(map);
							// .query("select obj.id from OrderForm obj where obj.user_id=:uid",
							// map, -1, -1);

							for (OrderForm of : ofs) {
								this.redPigOrderFormService
										.deleteById(of.getId());
							}
						}

						for (CouponInfo ci : parent.getCouponinfos()) {
							this.redPigCouponInfoService.deleteById(ci.getId());
						}

						parent.getCouponinfos().remove(parent.getCouponinfos());

						for (Accessory acc : parent.getFiles()) {
							if ((acc.getAlbum() != null)
									&& (acc.getAlbum().getAlbum_cover() != null)) {
								if (acc.getAlbum().getAlbum_cover().getId()
										.equals(acc.getId())) {
									acc.getAlbum().setAlbum_cover(null);
									this.redPigAlbumService.updateById(acc
											.getAlbum());
								}
							}
							// CommUtil.del_acc(request, acc);
							this.redPigAccessoryService.deleteById(acc.getId());
						}
						parent.getFiles().removeAll(parent.getFiles());
						parent.getCouponinfos().remove(parent.getCouponinfos());
						for (GoodsCart cart : parent.getGoodscarts()) {
							this.redPigGoodsCartService.deleteById(cart.getId());
						}
						Map<String, Object> params = Maps.newHashMap();
						params.put("redpig_cash_user_id", user.getId());

						PredepositCash_list = this.redPigPredepositcashService
								.queryPageList(params);
						// .query("select obj from PredepositCash obj where obj.cash_user.id=:uid",
						// params, -1, -1);

						for (PredepositCash pc : PredepositCash_list) {
							this.redPigPredepositcashService.deleteById(pc
									.getId());
						}

						params.clear();
						params.put("redpig_igo_user_id", parent.getId());
						integralGoodsOrders = this.redPigIntegralGoodsOrderService
								.queryPageList(params);

						// .query("select obj from IntegralGoodsOrder obj where obj.igo_user.id=:user_id",
						// params, -1, -1);

						for (IntegralGoodsOrder integralGoodsOrder : integralGoodsOrders) {
							this.redPigIntegralGoodsOrderService
									.deleteById(integralGoodsOrder.getId());
						}

						params.clear();
						params.put("redpig_integral_user_id", parent.getId());
						List<IntegralLog> integralLogs = this.redPigIntegralLogService
								.queryPageList(params);

						// .query("select obj from IntegralLog obj where obj.integral_user.id=:user_id",
						// params, -1, -1);

						for (IntegralLog integralLog : integralLogs) {
							this.redPigIntegralLogService.deleteById(integralLog
									.getId());
						}

						params.clear();
						params.put("redpig_gl_user_id", user.getId());
						List<GoldLog> list = this.redPigGoldlogService
								.queryPageList(params);

						// .query("select obj from GoldLog obj where obj.gl_user.id=:uid",
						// params, -1, -1);

						for (GoldLog gl : list) {
							this.redPigGoldlogService.deleteById(gl.getId());
						}

						params.clear();
						params.put("user_id", user.getId());
						List<StorePoint> storePointlist = this.redPigStorepointService
								.queryPageList(params);

						for (StorePoint sp : storePointlist) {
							this.redPigStorepointService.deleteById(sp.getId());
						}

						params.clear();
						params.put("ad_user_id", user.getId());
						List<Advert> adv_list = this.redPigAdvertService
								.queryPageList(params);

						for (Advert ad : adv_list) {
							this.redPigAdvertService.deleteById(ad.getId());
						}

						this.redPigUserService.deleteById(user.getId());

						if ((user.getDelivery_id() != null)
								&& (!user.getDelivery_id().equals(""))) {
							this.redPigDeliveryAddressService.deleteById(user
									.getDelivery_id());
						}

						params.clear();
						params.put("fromUser_id", user.getId());
						params.put("toUser_id", user.getId());

						List<SnsAttention> snsAttentionlist = this.redPigSnsAttentionService
								.queryPageList(params);

						for (SnsAttention sa : snsAttentionlist) {
							this.redPigSnsAttentionService.deleteById(sa.getId());
						}

						params.clear();
						params.put("user_id", user.getId());
						List<SysLog> logs = this.redPigSyslogService
								.queryPageList(params);

						for (SysLog log : logs) {
							this.redPigSyslogService.deleteById(log.getId());
						}
					}

					parent.getRoles().clear();

					this.redPigUserService.deleteUserRole(parent.getId(),
							parent.getRoles());

					if (parent.getStore() != null) {
						store_del(request, parent.getStore().getId());
					}

					for (Accessory acc : parent.getFiles()) {
						if ((acc.getAlbum() != null)
								&& (acc.getAlbum().getAlbum_cover() != null)) {
							if (acc.getAlbum().getAlbum_cover().getId()
									.equals(acc.getId())) {
								acc.getAlbum().setAlbum_cover(null);
								this.redPigAlbumService.updateById(acc
										.getAlbum());
							}
						}
						// CommUtil.del_acc(request, acc);
						try {
							this.redPigAccessoryService.delete(acc.getId());
						} catch (Exception e) {
							System.out.println("删除图片出错：图片ID：" + acc.getId());

						}
					}

					for (CouponInfo ci : parent.getCouponinfos()) {
						this.redPigCouponInfoService.deleteById(ci.getId());
					}

					for (GoodsCart cart : parent.getGoodscarts()) {
						this.redPigGoodsCartService.deleteById(cart.getId());
					}

					Map<String,Object> params = Maps.newHashMap();
					try {
						params.put("cash_user_id", parent.getId());
						List<PredepositCash> predepositCashlist = this.redPigPredepositcashService
								.queryPageList(params);

						for (PredepositCash pc : predepositCashlist) {
							this.redPigPredepositcashService.deleteById(pc
									.getId());
						}
					} catch (Exception e) {
						System.out.println("删除PredepositCash出错：ID="
								+ parent.getId());
					}
					params.clear();
					params.put("gl_user_id", parent.getId());
					List<GoldLog> GoldLog_list = this.redPigGoldlogService
							.queryPageList(params);

					for (GoldLog gl : GoldLog_list) {
						this.redPigGoldlogService.deleteById(gl.getId());
					}

					params.clear();
					params.put("user_id", parent.getId());
					List<StorePoint> storepoint_list = this.redPigStorepointService
							.queryPageList(params);

					for (StorePoint sp : storepoint_list) {
						this.redPigStorepointService.deleteById(sp.getId());
					}
					params.clear();

					params.put("ad_user_id", parent.getId());
					List<Advert> adv_list = this.redPigAdvertService
							.queryPageList(params);

					for (Advert ad : adv_list) {
						this.redPigAdvertService.deleteById(ad.getId());
					}

					if ((parent.getDelivery_id() != null)
							&& (!parent.getDelivery_id().equals(""))) {
						this.redPigDeliveryAddressService.deleteById(parent
								.getDelivery_id());
					}

					params.clear();
					params.put("fromUser_id", parent.getId());
					params.put("toUser_id", parent.getId());
					List<SnsAttention> list = this.redPigSnsAttentionService
							.queryPageList(params);

					for (SnsAttention sa : list) {
						this.redPigSnsAttentionService.deleteById(sa.getId());
					}

					params.clear();
					params.put("user_id", parent.getId());
					List<SysLog> logs = this.redPigSyslogService
							.queryPageList(params);
					for (SysLog log : logs) {
						this.redPigSyslogService.delete(log.getId());
					}
					this.redPigUserService.deleteById(parent.getId());
				}
			}
		}
		return "redirect:user_list?currentPage=" + currentPage;
	}

  	/**
  	 * 删除店铺
  	 * @param request
  	 * @param id
  	 * @throws Exception
  	 */
	private void store_del(HttpServletRequest request, Long id)
			 {
		if (!id.equals("")) {
			Store store = this.redPigStoreService.selectByPrimaryKey(id);
			if (store.getUser() != null) {
				store.getUser().setStore(null);
			}
			User user = store.getUser();
			if (user != null) {
				user.getRoles().clear();
				user.setUserRole("BUYER");

				Map<String, Object> params = Maps.newHashMap();
				params.put("type", "BUYER");
				List<Role> roles = this.redPigRoleService.queryPageList(params);

				user.setStore_apply_step(0);
				this.redPigUserService.saveUserRole(user.getId(),roles);

				this.redPigUserService.updateById(user);

				for (Goods goods : store.getGoods_list()) {
					goods.setGoods_main_photo(null);
					goods.setGoods_brand(null);
					this.redPigGoodsService.update(goods);
					goods.getGoods_photos().clear();
					goods.getGoods_specs().clear();
					goods.getGoods_ugcs().clear();
				}

				List<Evaluate> evaluates;
				for (Goods goods : store.getGoods_list()) {
					for (GoodsCart gc : goods.getCarts()) {
						this.redPigGoodsCartService.deleteById(gc.getId());
					}
					evaluates = goods.getEvaluates();
					for (Evaluate e : evaluates) {
						this.redPigEvaluateService.deleteById(e.getId());
					}

					for (ComplaintGoods cg : goods.getCgs()) {
						this.redPigComplaintGoodsService.deleteById(cg.getId());
					}

					this.redPigGoodsService.delete(goods.getId());
				}

				for (GoldRecord gr : user.getGold_record()) {
					try {
						this.redPigGoldRecordService.deleteById(gr.getId());
					} catch (Exception e) {
						System.out.println("删除GoldRecord出错：ID=" + gr.getId());
					}
				}

				params.clear();
				params.put("gl_user_id", user.getId());
				List<GoldLog> gls = this.redPigGoldlogService
						.queryPageList(params);

				for (GoldLog gl : gls) {
					gl.setGr(null);
					this.redPigGoldlogService.deleteById(gl.getId());
				}
				for (GoldRecord gr : user.getGold_record()) {
					this.redPigGoldlogService.deleteById(gr.getId());
				}
				for (GroupLifeGoods glg : user.getGrouplifegoods()) {
					for (GroupInfo gi : glg.getGroupInfos()) {
						this.redPigGroupinfoService.deleteById(gi.getId());
					}

					this.redPigGrouplifegoodsService.deleteById(CommUtil
							.null2Long(glg.getId()));
				}
				for (PayoffLog log : user.getPaylogs()) {
					this.redPigPayoffLogService.deleteById(log.getId());
				}

				for (Album album : user.getAlbums()) {
					if (album != null) {
						album.setAlbum_cover(null);
						this.redPigAlbumService.updateById(album);
						params.clear();
						params.put("album_id", album.getId());
						List<Accessory> accs = this.redPigAccessoryService
								.queryPageList(params);

						for (Accessory acc : accs) {
							// CommUtil.del_acc(request, acc);
							this.redPigAccessoryService.delete(acc.getId());
						}
						this.redPigAlbumService.deleteById(album.getId());
					}
				}
				for (GoodsSpecification spec : store.getSpecs()) {
					for (GoodsSpecProperty pro : spec.getProperties()) {
						this.redPigGoodsSpecPropertyService.delete(pro.getId());
					}

				}
			}
			String path = request.getSession().getServletContext()
					.getRealPath("/")
					+ this.redPigSysConfigService.getSysConfig()
							.getUploadFilePath()
					+ File.separator
					+ "store"
					+ File.separator + store.getId();
			CommUtil.deleteFolder(path);
			this.redPigStoreService.deleteById(id);
		}
	}

  	/**
  	 * 会员通知
  	 * @param request
  	 * @param response
  	 * @param userName
  	 * @param list_url
  	 * @return
  	 */
	@SecurityMapping(title = "会员通知", value = "/user_msg*", rtype = "admin", rname = "会员通知", rcode = "user_msg", rgroup = "会员")
	@RequestMapping({ "/user_msg" })
	public ModelAndView user_msg(
			HttpServletRequest request,
			HttpServletResponse response, 
			String userName, 
			String list_url) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/user_msg.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		Map<String, Object> params = Maps.newHashMap();
		List<StoreGrade> grades = this.redPigStoreGradeService.queryPageList(params);
		
		params.clear();
		
		mv.addObject("grades", grades);
		List<Store> store_list = this.redPigStoreService.queryPageList(params);
		
		mv.addObject("store_list", store_list);
		
		if (!"".equals(userName)) {
			mv.addObject("userName", userName);
		}
		if (!"".equals(list_url)) {
			mv.addObject("list_url", list_url);
		}
		return mv;
	}
	
	/**
	 * 会员通知发送
	 * @param request
	 * @param response
	 * @param type
	 * @param list_url
	 * @param users
	 * @param grades
	 * @param content
	 * @param stores
	 * @return
	 * @throws IOException
	 */
	@SecurityMapping(title = "会员通知发送", value = "/user_msg_send*", rtype = "admin", rname = "会员通知", rcode = "user_msg", rgroup = "会员")
	@RequestMapping({ "/user_msg_send" })
	public ModelAndView user_msg_send(
			HttpServletRequest request,
			HttpServletResponse response, String type, String list_url,
			String users, String grades, String content, String stores)
			throws IOException {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		List<User> user_list = Lists.newArrayList();
		List<Store> store_list = Lists.newArrayList();
		boolean judge = true;
		if (type.equals("all_user")) {
			Map<String, Object> params = Maps.newHashMap();
//			params.put("redpig_userRole", "ADMIN");

			user_list = this.redPigUserService.queryPageList(params);

		}

		if (type.equals("the_user")) {
			List<String> user_names = CommUtil.str2list(users);
			for (String user_name : user_names) {
				Map<String, Object> params = Maps.newHashMap();
				params.put("userName", user_name);
				
				List<User> userLists = this.redPigUserService.queryPageList(params);
				
				if (userLists != null && userLists.size()>0) {
					user_list.add(userLists.get(0));
				}

			}
		}
		if (type.equals("all_store")) {
			Map<String, Object> params = Maps.newHashMap();
			judge = false;
			store_list = this.redPigStoreService.queryPageList(params);
		}

		if (type.equals("the_store") && StringUtils.isNotBlank(grades)) {
			Map<String,Object> params = Maps.newHashMap();
			Set<Long> grade_ids = Sets.newTreeSet();
			String[] arrGrades = grades.split(",");
			
			for (String grade : arrGrades) {
				grade_ids.add(Long.valueOf(grade));
			}
			params.put("redpig_grade_ids", grade_ids);
			store_list = this.redPigStoreService.queryPageList(params);
			judge = false;
		}

		if (type.equals("the_store_id")) {
			List<String> store_names = CommUtil.str2list(stores);
			for (String store_name : store_names) {
				Map<String, Object> params = Maps.newHashMap();
				params.put("store_name", store_name);

				List<Store> storeList = this.redPigStoreService.queryPageList(params);

				store_list.addAll(storeList);
			}
			judge = false;
		}
		if (type.equals("all_store_id")) {
			judge = false;
			Map<String, Object> params = Maps.newHashMap();
			store_list = this.redPigStoreService.queryPageList(params);
		}
		if (judge) {
			for (User user : user_list) {
				if (user != null) {
					Message msg = new Message();
					msg.setAddTime(new Date());
					msg.setContent(content);
					User currentUser = new User();
					currentUser.setId(SecurityUserHolder.getCurrentUser().getId());
					
					msg.setFromUser(currentUser);
					msg.setToUser(user);
//					msg.setToStore_id(user.getStore().getId());
					this.redPigMessageService.saveEntity(msg);

					mv.addObject("op_title", "通知发送成功");
				} else {
					mv.addObject("op_title", "店铺信息错误，通知发送失败");
				}
			}
		} else {
			for (Store store : store_list) {
				if (store != null) {
					Message msg = new Message();
					msg.setAddTime(new Date());
					msg.setContent(content);
					User currentUser = new User();
					currentUser.setId(SecurityUserHolder.getCurrentUser().getId());
					
					msg.setFromUser(currentUser);
					msg.setToStore_id(store.getId());
					this.redPigMessageService.saveEntity(msg);
					mv.addObject("op_title", "通知发送成功");
				} else {
					mv.addObject("op_title", "店铺信息错误，通知发送失败");
				}
			}
		}
		mv.addObject("list_url", list_url);
		return mv;
	}
}
