package com.redpigmall.manage.admin.action.goods;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.lucene.LuceneVo;
import com.redpigmall.lucene.RedPigLuceneUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.msg.SpelTemplate;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Evaluate;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsSpecProperty;
import com.redpigmall.domain.Template;
import com.redpigmall.domain.User;
import com.redpigmall.domain.UserGoodsClass;

/**
 * 
 * <p>
 * Title: RedPigGoodsManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商品管理类
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
public class RedPigGoodsManageAction extends BaseAction{

	/**
     * 已删商品列表
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @param store_name
     * @param brand_id
     * @param gc_id
     * @param goods_type
     * @param goods_name
     * @param store_recommend
     * @param goods_activity_status
     * @return
     */
	@SecurityMapping(title="商品列表", value="/goods_deleted*", rtype="admin", rname="商品管理", rcode="admin_goods", rgroup="商品")
    @RequestMapping({"/goods_deleted"})
    public ModelAndView goods_deleted(
    		HttpServletRequest request, 
    		HttpServletResponse response, 
    		String currentPage, String orderBy, String orderType, String store_name, 
    		String brand_id, String gc_id, String goods_type, String goods_name, 
    		String store_recommend, String goods_activity_status)
    {
      ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_deleted.html", 
        this.redPigSysConfigService.getSysConfig(), 
        this.redPigUserConfigService.getUserConfig(), 0, request, response);
      
      Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
      
      List<String> activity = Lists.newArrayList();
      if ((goods_activity_status != null) && (!goods_activity_status.equals("")))
      {
        activity.add(goods_activity_status);
        this.redPigQueryTools.queryActivityGoods(params, activity);
        
        mv.addObject("goods_activity_status", goods_activity_status);
      }
      
      if ((store_name != null) && (!store_name.equals("")))
      {

        params.put("redPig_store_name", CommUtil.null2String(store_name));
        
        mv.addObject("store_name", store_name);
      }
      
      if ((brand_id != null) && (!brand_id.equals("")))
      {

        params.put("redPig_goods_brand_id", CommUtil.null2Long(brand_id));
        
        mv.addObject("brand_id", brand_id);
      }
      
      if ((gc_id != null) && (!gc_id.equals("")))
      {

		List<Long> gc_ids = Lists.newArrayList();
		gc_ids.add(CommUtil.null2Long(gc_id));

		params.put("redPig_gc_ids", gc_ids);
        
        
        mv.addObject("gc_id", gc_id);
      }
      
      if ((goods_type != null) && (!goods_type.equals(""))){

        params.put("goods_type", Integer.valueOf(CommUtil.null2Int(goods_type)));
        
        mv.addObject("goods_type", goods_type);
      }
      
      if ((goods_name != null) && (!goods_name.equals(""))) {

        params.put("redpig_goods_name", goods_name);
        
        mv.addObject("goods_name", goods_name);
      }
      
      if ((store_recommend != null) && (!store_recommend.equals(""))){

        params.put("store_recommend", Boolean.valueOf(CommUtil.null2Boolean(store_recommend)));
        
        mv.addObject("store_recommend", store_recommend);
      }
      
      params.put("deleteStatus", Integer.valueOf(CommUtil.null2Int(Integer.valueOf(1))));
      
      IPageList pList = this.redPigGoodsService.list(params);
      
      CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
      
      params.clear();
      
      List<GoodsBrand> gbs = this.redPigGoodsBrandService.queryPageList(params);

      
      params.put("parent", -1);
      
      List<GoodsClass> gcs = this.redPigGoodsClassService.queryPageList(params);

      
      mv.addObject("gcs", gcs);
      mv.addObject("gbs", gbs);
      return mv;
    }
    
    /**
     * 商品二维码生成
     * @param request
     * @param mulitId
     * @param redirect
     * @param currentPage
     * @param store_name
     * @param brand_id
     * @param gc_id
     * @param goods_type
     * @param goods_name
     * @param store_recommend
     * @param status
     * @param uncheck_mulitId
     * @return
     * @throws ClassNotFoundException
     */
	@SecurityMapping(title = "商品二维码生成", value = "/goods_qr*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping({ "/goods_qr" })
	public String goods_qr(
			HttpServletRequest request, 
			String mulitId,
			String redirect, String currentPage, String store_name,
			String brand_id, String gc_id, String goods_type,
			String goods_name, String store_recommend, String status,
			String uncheck_mulitId) throws ClassNotFoundException {
		
		if (mulitId == null) {
			mulitId = "";
		}
		Goods goods;
		if ("all".equals(mulitId)) {
			List<Goods> list = generatorQuery(CommUtil.null2Int(status),
					store_name, brand_id, gc_id, goods_type, goods_name,
					store_recommend, uncheck_mulitId);
			for (int i = 0; i < list.size(); i++) {
				goods = (Goods) list.get(i);
				generatorQR(request, goods);
			}
		} else {
			String[] ids = mulitId.split(",");

			for (String id : ids) {
				if (!id.equals("")) {
					goods = this.redPigGoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
					generatorQR(request, goods);
				}
			}
		}
		if ("deleted".equals(redirect)) {
			return "redirect:goods_deleted?currentPage=" + currentPage;
		}
		if ("outline".equals(redirect)) {
			return "redirect:goods_outline?currentPage=" + currentPage;
		}
		return "redirect:goods_list?currentPage" + currentPage;
	}
    
	private void generatorQR(HttpServletRequest request, Goods goods) {
		String qr_img_path = this.redPigGoodsTools.createGoodsQR(request, goods);
		goods.setQr_img_path(qr_img_path);
		this.redPigGoodsService.update(goods);
	}
	
    /**
     * 商品拒绝
     * @param request
     * @param response
     * @param good_id
     * @param refuse_info
     * @param status
     * @param currentPage
     */
	@SecurityMapping(title = "商品拒绝", value = "/goods_refuse*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping({ "/goods_refuse" })
	public void goods_refuse(
			HttpServletRequest request,
			HttpServletResponse response, 
			String good_id, String refuse_info,
			String status, String currentPage) {
		
		Goods good = this.redPigGoodsService.selectByPrimaryKey(CommUtil.null2Long(good_id));
		
		String ret = "yes";
		if (good != null) {
			good.setGoods_status(-6);
			good.setRefuse_info(refuse_info);
			this.redPigGoodsService.update(good);
		} else {
			ret = "no";
		}
		try {
			response.setCharacterEncoding("utf-8");
			response.setHeader("Cache-Control", "no-cache");
			response.setContentType("text/plain");
			response.getWriter().print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    /**
     * 商品审核
     * @param request
     * @param mulitId
     * @param redirect
     * @param currentPage
     * @param store_name
     * @param brand_id
     * @param gc_id
     * @param goods_type
     * @param goods_name
     * @param store_recommend
     * @param status
     * @param uncheck_mulitId
     * @return
     */
	@SecurityMapping(title = "商品审核", value = "/goods_audit*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping({ "/goods_audit" })
	public String goods_audit(
			HttpServletRequest request, 
			String mulitId,
			String redirect, String currentPage, String store_name,
			String brand_id, String gc_id, String goods_type,
			String goods_name, String store_recommend, String status,
			String uncheck_mulitId)

	{
		if (mulitId == null) {
			mulitId = "";
		}
		
		Goods obj;
		if ("all".equals(mulitId)) {
			List<Goods> list = generatorQuery(CommUtil.null2Int(status),
					store_name, brand_id, gc_id, goods_type, goods_name,
					store_recommend, uncheck_mulitId);
			for (int i = 0; i < list.size(); i++) {
				obj = (Goods) list.get(i);
				obj.setGoods_status(obj.getPublish_goods_status());
				this.redPigGoodsService.update(obj);
				this.redPigGoodsTools.updateGoodsLucene(obj);
			}
		} else {
			String[] ids = mulitId.split(",");

			for (String id : ids) {
				if (!id.equals("")) {
					obj = this.redPigGoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
					obj.setGoods_status(obj.getPublish_goods_status());
					this.redPigGoodsService.update(obj);
					this.redPigGoodsTools.updateGoodsLucene(obj);
				}
			}
		}
		return "redirect:goods_list?status=" + status;
	}

	/**
	 * 商品AJAX更新
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "商品AJAX更新", value = "/goods_ajax*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping({ "/goods_ajax" })
	public void goods_ajax(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, String fieldName,
			String value)  
	{
	
		Goods obj = this.redPigGoodsService.selectByPrimaryKey(Long.parseLong(id));
		
		Field[] fields = Goods.class.getDeclaredFields();

		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);

		if (fieldName.equals("store_recommend")) {
			if (obj.getStore_recommend()) {
				obj.setStore_recommend_time(new Date());
			} else {
				obj.setStore_recommend_time(null);
			}
		}

		this.redPigGoodsService.update(obj);
		// 如果是上架商品，需要更新lucene索引
		if (obj.getGoods_status() == 0) {
			String goods_lucene_path = ClusterSyncTools.getClusterRoot()
					+ File.separator + "luence" + File.separator + "goods";
			File file = new File(goods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(goods_lucene_path);
			}

			LuceneVo vo = new LuceneVo();
			vo.setVo_id(obj.getId());
			vo.setVo_title(obj.getGoods_name());
			vo.setVo_content(obj.getGoods_details());
			vo.setVo_type("goods");
			vo.setVo_store_price(CommUtil.null2Double(obj.getGoods_current_price()));
			vo.setVo_add_time(obj.getAddTime().getTime());
			vo.setVo_goods_salenum(obj.getGoods_salenum());
			RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
			RedPigLuceneUtil.setConfig(this.redPigSysConfigService.getSysConfig());
			RedPigLuceneUtil.setIndex_path(goods_lucene_path);
			lucene.update(CommUtil.null2String(obj.getId()), vo);
		} else {
			String goods_lucene_path = ClusterSyncTools.getClusterRoot()
					+ File.separator + "luence" + File.separator + "goods";
			
			File file = new File(goods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(goods_lucene_path);
			}
			
			RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
			RedPigLuceneUtil.setConfig(this.redPigSysConfigService.getSysConfig());
			RedPigLuceneUtil.setIndex_path(goods_lucene_path);
			lucene.delete_index(CommUtil.null2String(id));
		}
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
     * 商品删除
     * @param request
     * @param mulitId
     * @param redirect
     * @param currentPage
     * @param store_name
     * @param brand_id
     * @param gc_id
     * @param goods_type
     * @param goods_name
     * @param store_recommend
     * @param status
     * @param uncheck_mulitId
     * @return
     * @throws Exception
     */
	  @SecurityMapping(title="商品删除", value="/goods_del*", rtype="admin", rname="商品管理", rcode="admin_goods", rgroup="商品")
	  @RequestMapping({"/goods_del"})
	  public String goods_del(
			  HttpServletRequest request, 
			  String mulitId, String redirect, String currentPage, 
			  String store_name, String brand_id, String gc_id, 
			  String goods_type, String goods_name, String store_recommend, 
			  String status, String uncheck_mulitId
			  )
	    throws Exception
	  {
	    if (mulitId == null) {
	      mulitId = "";
	    }
	    
	    Goods goods;
	    if ("all".equals(mulitId))
	    {
	      List<Goods> list = generatorQuery(
	    		  CommUtil.null2Int(status), 
	    		  store_name, 
	    		  brand_id, 
	    		  gc_id, 
	    		  goods_type,
	    		  goods_name, 
	    		  store_recommend, 
	    		  uncheck_mulitId);
	      
	      for (int i = 0; i < list.size(); i++)
	      {
	        goods = (Goods)list.get(i);
	        goodsListDel(goods, request);
	      }
	    }
	    else
	    {
	      String[] ids = mulitId.split(",");
	      
	      for (String id:ids)
	      {
	        if (!id.equals(""))
	        {
	          Goods good = this.redPigGoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
	          goodsListDel(good, request);
	        }
	      }
	    }
	    if ("deleted".equals(redirect)) {
	      return "redirect:goods_deleted?currentPage=" + currentPage;
	    }
	    if ("outline".equals(redirect)) {
	      return "redirect:goods_outline?currentPage=" + currentPage;
	    }
	    return "redirect:goods_list?currentPage" + currentPage;
	  }


	  private void goodsListDel(Goods goods, HttpServletRequest request)
	    throws Exception
	  {
	    User seller = null;
	    String goods_name = goods.getGoods_name();
	    if (goods.getGoods_store() != null)
	    {
	      try {
			Long seller_id = goods.getGoods_store().getUser().getId();
			seller = this.redPigUserService.selectByPrimaryKey(seller_id);
		} catch (Exception e) {
		}
	    }
	    Map<String,Object> params = Maps.newHashMap();
	    
	    List<Evaluate> evaluates = goods.getEvaluates();
	    
	    this.redPigEvaluateService.batchDeleteEvaluates(evaluates);
	    
	    params.clear();
	    params.put("goods_id", goods.getId());
	    
	    this.redPigFavoriteService.deleteByGoodsId(goods.getId());
	    
	    List<Accessory> goodsPhotos = goods.getGoods_photos();
	    //删除关系即可
	    this.redPigGoodsService.batchDeleteGoodsPhotos(goods.getId(),goodsPhotos);
	    
	    List<UserGoodsClass> ugcs = goods.getGoods_ugcs();
	    
	    //删除关系即可
	    this.redPigGoodsService.batchDeleteUserGoodsClass(goods.getId(),ugcs);
	    
	    List<GoodsSpecProperty> gsps = goods.getGoods_specs();
	    
	    //删除关系即可
	    this.redPigGoodsService.batchDeleteGoodsSpecProperty(goods.getId(),gsps);
	    
	    
	    params.clear();
	    
	    List<GoodsCart> gcs = goods.getCarts();
	    
	    for (GoodsCart goodsCart : gcs) {
			goodsCart.setGoods(null);
			redPigGoodsCartService.updateById(goodsCart);
		}
	    
	    this.redPigZTCGoldLogService.deleteByGoodsId(goods.getId());
	    
	    this.redPigGoodsService.deleteGoodsMainPhoto(goods.getId());
	    
	    this.redPigGoodsService.delete(goods.getId());
	    
	    this.redPigGoodsTools.deleteGoodsLucene(goods);
	    
	    if (goods.getGoods_type() == 1 && seller !=null) {
	      send_site_msg(request, 
	        "sms_toseller_goods_delete_by_admin_notify", seller, 
	        goods_name, "商品存在违规，系统已删除");
	    }
	  }
	  
	/**
	 *   发送站内消息
	 * @param request
	 * @param mark
	 * @param seller
	 * @param goods_name
	 * @param reason
	 * @throws Exception
	 */
	private void send_site_msg(HttpServletRequest request, String mark,
			User seller, String goods_name, String reason) throws Exception {
		Map<String,Object> maps = Maps.newHashMap();
		maps.put("mark", mark);
		
		List<Template> templates = this.redPigTemplateService.queryPageList(maps);
		
		if(templates!=null && templates.size()>0){
			Template template = templates.get(0);
			
			if ((template != null) && (template.getOpen()) && (seller != null)
					&& (seller.getMobile() != null)) {
				ExpressionParser exp = new SpelExpressionParser();
				EvaluationContext context = new StandardEvaluationContext();
				context.setVariable("reason", reason);
				context.setVariable("user", seller);
				context.setVariable("goods_name", goods_name);
				context.setVariable("config", this.redPigSysConfigService.getSysConfig());
				context.setVariable("webPath", CommUtil.getURL(request));
				Expression ex = exp.parseExpression(template.getContent(),
						new SpelTemplate());
				String content = (String) ex.getValue(context, String.class);
				this.redPigMsgTools.sendSMS(seller.getMobile(), content);
			}
		}
		
	}
	
	/**
	 * 
	 * @param status
	 * @param store_name
	 * @param brand_id
	 * @param gc_id
	 * @param goods_type
	 * @param goods_name
	 * @param store_recommend
	 * @param uncheck_mulitId
	 * @return
	 */
	private List<Goods> generatorQuery(int status, String store_name, String brand_id, String gc_id, String goods_type, String goods_name, String store_recommend, String uncheck_mulitId)
	  {
		Map<String,Object> params = Maps.newHashMap();
		params.put("goods_status", status);
		
	    if ((uncheck_mulitId != null) && (!"".equals(uncheck_mulitId)))
	    {
	      uncheck_mulitId = uncheck_mulitId.substring(0,uncheck_mulitId.length() - 1);
	      String[] ids = uncheck_mulitId.split(",");
	      
	      List<Long> redPig_ids = Lists.newArrayList();
	      for(String id:ids){
	    	  redPig_ids.add(Long.valueOf(id.trim()));
	      }
	      
	      params.put("redPig_ids", redPig_ids);
	    }
	    
	    if ((brand_id != null) && (!brand_id.equals("")))
	    {
	      params.put("redPig_goods_brand_id", CommUtil.null2Long(brand_id));
	      
	    }
	    
	    if ((goods_name != null) && (!goods_name.equals(""))) {
	      params.put("redpig_goods_name", goods_name);
	    }
	    
	    if ((store_name != null) && (!store_name.equals(""))) {
	      
	      params.put("redPig_store_name", store_name);
	    }
	    
	    if ((gc_id != null) && (!gc_id.equals("")))
	    {
	      List<Long> redPig_gc_ids = Lists.newArrayList();
	      redPig_gc_ids.add(CommUtil.null2Long(gc_id));
	      
	      params.put("redPig_gc_ids", redPig_gc_ids);
	      
	    }
	    
	    if ((goods_type != null) && (!goods_type.equals("")))
	    {
	      
	      params.put("goods_type", Integer.valueOf(CommUtil.null2Int(goods_type)));
	      
	    }
	    
	    if ((store_recommend != null) && (!store_recommend.equals("")))
	    {
	      
	      params.put("store_recommend", Integer.valueOf(CommUtil.null2Int(goods_type)));
	      
	    }
	    
	    List<Goods> list = this.redPigGoodsService.queryPageList(params);
	    return list;
	  }
	  
	/**
	 * 违规商品列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param goods_name
	 * @param gb_id
	 * @param gc_id
	 * @param goods_activity_status
	 * @return
	 */
	@SecurityMapping(title = "违规商品列表", value = "/goods_outline*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping({ "/goods_outline" })
	public ModelAndView goods_outline(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, String orderBy,
			String orderType, String goods_name, 
			String gb_id, String gc_id,
			String goods_activity_status
			) 
	{
	
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/goods_outline.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}

		String params = "";
		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);

		List<String> activity = Lists.newArrayList();
		if ((goods_activity_status != null)
				&& (!goods_activity_status.equals(""))) {
			activity.add(goods_activity_status);

			this.redPigQueryTools.queryActivityGoods(maps, activity);
			mv.addObject("goods_activity_status", goods_activity_status);
		}

		if ((goods_name != null) && (!goods_name.equals(""))) {

			maps.put("goods_name_like", goods_name);

			mv.addObject("goods_name", goods_name);
		}
		
		maps.put("goods_name_no", -1);
		
		if ((gb_id != null) && (!gb_id.equals(""))) {
			
			maps.put("goods_brand_id", CommUtil.null2Long(gb_id));
			
			mv.addObject("gb_id", gb_id);
		}
		if ((gc_id != null) && (!gc_id.equals(""))) {
			List<Long> gc_ids = Lists.newArrayList();
			gc_ids.add(CommUtil.null2Long(gb_id));
			
			maps.put("gc_id", gc_ids);
			
			mv.addObject("gc_id", gc_id);
		}
		
		maps.put("goods_status", -2);

		IPageList pList = this.redPigGoodsService.list(maps);

		CommUtil.saveIPageList2ModelAndView(url + "/goods_list.html", "",params, pList, mv);

		maps.clear();

		List<GoodsBrand> gbs = this.redPigGoodsBrandService.queryPageList(maps);
		maps.clear();
		maps.put("parent", -1);
		
		List<GoodsClass> gcs = this.redPigGoodsClassService.queryPageListWithNoRelations(maps);
		
		mv.addObject("gcs", gcs);
		mv.addObject("gbs", gbs);
		return mv;
	}
	
	
	/**
	 * 商品列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param store_name
	 * @param brand_id
	 * @param gc_id
	 * @param goods_type
	 * @param goods_name
	 * @param store_recommend
	 * @param status
	 * @param goods_activity_status
	 * @return
	 */
	@SecurityMapping(title = "商品列表", value = "/goods_list*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping({ "/goods_list" })
	public ModelAndView goods_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, String orderBy,
			String orderType, String store_name, String brand_id, String gc_id,
			String goods_type, String goods_name, String store_recommend,
			String status, String goods_activity_status
			) 
	{
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		List<String> activity = Lists.newArrayList();

		Map<String, Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);

		if ((goods_activity_status != null)
				&& (!goods_activity_status.equals(""))) {
			
			activity.add(goods_activity_status);
			
			this.redPigQueryTools.queryActivityGoods(params, activity);
			mv.addObject("goods_activity_status", goods_activity_status);
		}
		
		if ((store_name != null) && (!store_name.equals(""))) {
			params.put("redPig_store_name", CommUtil.null2String(store_name));
			mv.addObject("store_name", store_name);
		}
		
		if ((brand_id != null) && (!brand_id.equals(""))) {
			params.put("goods_brand_id", CommUtil.null2Long(brand_id));
			mv.addObject("brand_id", brand_id);
		}
		
		if ((gc_id != null) && (!gc_id.equals(""))) {
			Map<String,Object> maps = Maps.newHashMap();
			maps.put("parent", gc_id);
			
			List<GoodsClass> goodsClassList = this.goodsClassService.queryPageList(maps);
			List<Long> gcIds = Lists.newArrayList();
			for (GoodsClass goodsClass : goodsClassList) {
				Set<Long> goodsClassIds = genericGcIds(goodsClass);
				gcIds.addAll(goodsClassIds);
				
				gcIds.add(goodsClass.getId());
			}
			gcIds.add(CommUtil.null2Long(gc_id));
			
			params.put("gc_ids", gcIds);
			mv.addObject("gc_id", gc_id);
		}
		
		if ((goods_type != null) && (!goods_type.equals(""))) {
			params.put("goods_type", CommUtil.null2Int(goods_type));
			mv.addObject("goods_type", goods_type);
		}
		
		if ((goods_name != null) && (!goods_name.equals(""))) {
			params.put("redpig_goods_name", goods_name);
			mv.addObject("goods_name", goods_name);
		}
		
		//商品名字不能为空
		params.put("goods_name_no", -1);
		
		if ((store_recommend != null) && (!store_recommend.equals(""))) {

			params.put("store_recommend",
					CommUtil.null2Boolean(store_recommend));

			mv.addObject("store_recommend", store_recommend);
		}
		if ((status != null) && (!status.equals(""))) {
			params.put("goods_status",Integer.valueOf(CommUtil.null2Int(status)));

			mv.addObject("status", status);
			List<String> activity1 = Lists.newArrayList();
			if ((goods_activity_status != null) && (!goods_activity_status.equals(""))) {
				activity1.add(goods_activity_status);
				this.redPigQueryTools.queryActivityGoods(params, activity1);
				mv.addObject("goods_activity_status", goods_activity_status);
			}
		} else {
			params.put("redpig_goods_status", Integer.valueOf(-2));
		}
		
//		IPageList pList = this.redPigGoodsService.list(params);
		
		IPageList pList = this.redPigGoodsService.list(params);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		
		params.clear();
		
		List<GoodsBrand> gbs = this.redPigGoodsBrandService.queryPageList(params);
		
		params.put("parent", -1);
		
		List<GoodsClass> gcs = this.redPigGoodsClassService.queryPageListWithNoRelations(params);
//		List<GoodsClass> gcs = this.redPigGoodsClassService.queryPageListWithNoRelations(params);
		
		mv.addObject("gcs", gcs);
		mv.addObject("gbs", gbs);
		return mv;
	}
	
	private Set<Long> genericGcIds(GoodsClass gc) {
		Set<Long> ids = Sets.newHashSet();
		ids.add(gc.getId());
		for (GoodsClass child : gc.getChilds()) {
			Set<Long> cids = genericGcIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}

}
