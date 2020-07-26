package com.redpigmall.manage.admin.action.direct_selling;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.*;
import com.redpigmall.manage.admin.action.base.BaseAction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * <p>
 * Title: RedPigAreaManageAction.java
 * </p>
 *
 * <p>
 * Description: 直销设置
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 *
 * <p>
 * Company: www.redpigmall.net
 * </p>
 *
 * @author redpig
 *
 * @date 2018年4月17日
 *
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigDirectSellingManageAction extends BaseAction{
	/**
	 * 微商城配置
	 *
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "直销配置", value = "/direct_set*", rtype = "admin", rname = "直销配置", rcode = "direct_set", rgroup = "分销管理")
	@RequestMapping({ "/direct_set" })
	public ModelAndView direct_set(HttpServletRequest request,
								   HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/direct_set.html",
				this.configService.getSysConfig(),
				//登录用户信息
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	/**
	 * 分销等级添加
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "分销等级", value = "/distribution_grade*", rtype = "admin", rname = "分销配置", rcode = "direct_set", rgroup = "分销管理")
	@RequestMapping({ "/distribution_grade" })
	public ModelAndView distribution_grade(HttpServletRequest request,
										   HttpServletResponse response,String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/distribution/distribution_grade.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if(StringUtils.isNotBlank(id)){
			DistributionGrade distributionGrade = this.redPigDistributionGradeService.selectByPrimaryKey(Long.parseLong(id));
			mv.addObject("grade",distributionGrade);
			mv.addObject("flag",true);
		}
		return mv;
	}



	@SecurityMapping(title = "分销等级添加", value = "/distribution_grade_save*", rtype = "admin", rname = "分销等级添加 ", rcode = "direct_set", rgroup = "分销管理")
	@RequestMapping({ "/distribution_grade_save" })
	public ModelAndView distribution_grade_save(HttpServletRequest request,
												HttpServletResponse response,String id,String grade,Integer count_user,
												String count_price,String down_count_price,int inner_rebate,int out_rebate) {
		SysConfig obj = this.configService.getSysConfig();
		DistributionGrade grade1 = new DistributionGrade();
		grade1.setGrade(grade);
		grade1.setCount_user((int)count_user);
		grade1.setCount_price(new BigDecimal(count_price));
		grade1.setDown_count_price(new BigDecimal(down_count_price));
		grade1.setInner_rebate(inner_rebate);
		grade1.setOut_rebate(out_rebate);
		grade1.setOperator(obj.getId());
		//通过id判断add or update
		if(id.equals("")){
			grade1.setAddTime(new Date());
			this.redPigDistributionGradeService.saveEntity(grade1);
		}else{
			grade1.setId(Long.parseLong(id));
			grade1.setUpdate_time(new Date());
			this.redPigDistributionGradeService.updateById(grade1);
		}
		return this.distribution_grade_goods_list(request,response,"1","addTime","desc");
	}

	/**
	 * 分销列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "分销等级列表", value = "/distribution_grade_goods_list*", rtype = "admin", rname = "分销商品", rcode = "distribution_grade_goods_list", rgroup = "分销管理")
	@RequestMapping({ "/distribution_grade_goods_list" })
	public ModelAndView distribution_grade_goods_list(
			HttpServletRequest request, HttpServletResponse response,
			String currentPage, String orderBy, String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/distribution/distribution_grade_goods_list.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		//做分页并且排序
		Map<String, Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		//分页查询用的
		IPageList pList = this.redPigDistributionGradeService.list(params);
		//把分页信息封装到ModelAndView的工具类
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}



	/**
	 *
	 * direct_selling_set_save:直销设置保存. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @param id
	 * @param direct_selling
	 * @return
	 * @since JDK 1.8
	 */
	@SecurityMapping(title = "直销设置保存", value = "/direct_selling_set_save*", rtype = "admin", rname = "直销基本设置", rcode = "weixin_plat_admin", rgroup = "分销管理")
	@RequestMapping({ "/direct_selling_set_save" })
	public ModelAndView direct_selling_set_save(
			HttpServletRequest request,
			HttpServletResponse response,
			String id,
			String direct_selling_first_level_rate,
			String direct_selling_second_level_rate,
			String direct_selling) {

		SysConfig obj = this.configService.getSysConfig();

		SysConfig sysConfig = null;

		if (id.equals("")) {
			sysConfig = (SysConfig) WebForm.toPo(request, SysConfig.class);
			sysConfig.setAddTime(new Date());
		} else {
			sysConfig = (SysConfig) WebForm.toPo(request, obj);
		}
		//CommUtil.null2Int(direct_selling) 转换为int
		sysConfig.setDirect_selling(CommUtil.null2Int(direct_selling));

		if((direct_selling_first_level_rate != null) && (direct_selling_second_level_rate != null)) {
			//new BigDecimal(e) 转换类型
			sysConfig.setDirect_selling_first_level_rate(new BigDecimal(direct_selling_first_level_rate));
			sysConfig.setDirect_selling_second_level_rate(new BigDecimal(direct_selling_second_level_rate));
		}
		//通过id判断add or update
		if (id.equals("")) {
			this.configService.saveEntity(sysConfig);
		} else {
			this.configService.updateById(sysConfig);
		}

		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "分销设置成功");
		mv.addObject("list_url", CommUtil.getURL(request) + "/direct_set");

		return mv;
	}

	/**
	 *
	 * direct_selling_goods_list:. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param class_id
	 * @param brand_id
	 * @param goods_name
	 * @return
	 * @since JDK 1.8
	 */
	@SecurityMapping(title = "分销商品", value = "/direct_selling_goods_list*", rtype = "admin", rname = "分销商品", rcode = "direct_selling_goods_list", rgroup = "分销管理")
	@RequestMapping({ "/direct_selling_goods_list" })
	public ModelAndView direct_selling_goods_list(
			HttpServletRequest request, HttpServletResponse response,
			String currentPage, String orderBy, String orderType,
			String class_id, String brand_id, String goods_name) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/direct_selling_goods_list.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);

		Map<String, Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);

		params.put("goods_type", 0);

		if ((goods_name != null) && (!goods_name.equals(""))) {

			params.put("goods_name_like", goods_name);

			mv.addObject("goods_name", goods_name);

		}
		// 分类查询
		if ((class_id != null) && (!class_id.equals(""))) {
			params.put("rgc_id", CommUtil.null2Long(class_id));
			params.put("rgc_parent_id", CommUtil.null2Long(class_id));
			params.put("rgc_parent_parent_id", CommUtil.null2Long(class_id));

			mv.addObject("class_id", class_id);

		}
		if ((brand_id != null) && (!brand_id.equals(""))) {

			params.put("goods_brand_id", CommUtil.null2Long(brand_id));

			mv.addObject("brand_id", brand_id);

		}

		IPageList pList = this.goodsService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		params.clear();
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");

		List<GoodsBrand> gbs = this.goodsBrandService.queryPageList(params);
		params.clear();

		params.put("parent", -1);

		List<GoodsClass> gcs = this.goodsClassService.queryPageListWithNoRelations(params);

		mv.addObject("gcs", gcs);
		mv.addObject("gbs", gbs);
		return mv;
	}

	/**
	 *
	 * direct_selling_goods_ajax:(这里用一句话描述这个方法的作用). <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @param id
	 * @since JDK 1.8
	 */
	@RequestMapping({ "/direct_selling_goods_ajax" })
	public void direct_selling_goods_ajax(HttpServletRequest request,
										  HttpServletResponse response, String id)  {
		Goods obj = this.goodsService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		int flag = obj.getDirect_selling();
		boolean data = false;

		//是否开启
		if(flag==0) {
			obj.setDirect_selling(1);
			data = true;
		}else {
			obj.setDirect_selling(0);
		}

		this.goodsService.updateById(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * direct_selling_goods_update:直销商品批量管理. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @param type
	 * @return
	 * @since JDK 1.8
	 */
	@SecurityMapping(title = "直销商品批量管理", value = "/direct_selling_goods_update*", rtype = "admin", rname = "直销商品批量管理", rcode = "direct_selling_goods_update", rgroup = "分销管理")
	@RequestMapping({ "/direct_selling_goods_update" })
	public String direct_selling_goods_update(HttpServletRequest request,
											  HttpServletResponse response, String mulitId, String currentPage,
											  String type) {

		String[] ids = mulitId.split(",");

		for (String id : ids) {

			if (!id.equals("")) {
				Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));

				if("add".equals(type)) {
					goods.setDirect_selling(1);
				}else if("remove".equals(type)) {
					goods.setDirect_selling(0);
				}
				this.goodsService.updateById(goods);
			}
		}

		return "redirect:direct_selling_goods_list?currentPage=" + currentPage;
	}

	/**
	 * 分销协议列表
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "分销协议", value = "/distribution_agreement_list*", rtype = "admin", rname = "分销协议", rcode = "direct_set", rgroup = "分销管理")
	@RequestMapping({ "/distribution_agreement_list" })
	public ModelAndView distribution_agreement_list(HttpServletRequest request,
											   HttpServletResponse response, String currentPage, String orderBy,
													String orderType,String agreement_name,String beginTime,String endTime) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/agreement/agreement_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		mv.addObject("agreement_name",agreement_name);
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}

		String params = "";

		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		 if (StringUtils.isNotBlank(agreement_name) && agreement_name !=null){
             maps.put("agreement_name",agreement_name);
         }
        if (StringUtils.isNotBlank(beginTime) && beginTime !=null){
            maps.put("beginTime",CommUtil.formatDate(beginTime));
        }
        if (StringUtils.isNotBlank(endTime) && endTime !=null){
            maps.put("endTime",CommUtil.formatDate(endTime));
        }

		IPageList pList = this.redPigDistributionAgreementService.list(maps);
        CommUtil.saveIPageList2ModelAndView(url + "/agreement/agreement_list.html", "",params, pList, mv);
		return mv;
	}

	/**
	 * 新增协议页面跳转
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "新增协议页面", value = "/distribution_agreement_add*", rtype = "admin", rname = "新增协议页面", rcode = "direct_set", rgroup = "分销管理")
	@RequestMapping({ "/distribution_agreement_add" })
	public ModelAndView distribution_agreement_add(HttpServletRequest request,
													 HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				//test用agreement_add.html
				"admin/blue/agreement/agreement_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}


	/**
	 * 协议保存
	 * @param request
	 * @param response
	 * @param agreement_type
	 * @param agreement_name
	 * @param agreement
	 * @param agreement_state
	 * @param agreement_version
	 * @return
	 */
	@SecurityMapping(title = "保存协议", value = "/distribution_agreement_save*", rtype = "admin", rname = "协议设置", rcode = "direct_set", rgroup = "分销管理")
	@RequestMapping({ "/distribution_agreement_save" })
	public String distribution_agreement_save(HttpServletRequest request,
												   HttpServletResponse response,String agreement_type,String agreement_name,String agreement,
												   String agreement_state,String agreement_version) {

		DistributionAgreement distributionAgreement = new DistributionAgreement();

		if (StringUtils.isNotBlank(agreement)){
            distributionAgreement.setAgreement(agreement);
        }
        if (StringUtils.isNotBlank(agreement_name)){
            distributionAgreement.setAgreement_name(agreement_name);
        }
        if (StringUtils.isNotBlank(agreement_type)){
            distributionAgreement.setAgreement_type(agreement_type);
        }
        if (StringUtils.isNotBlank(agreement_state)){
            if (agreement_state.equals(0)){
                distributionAgreement.setAgreement_state("隐藏");
            }else {
                distributionAgreement.setAgreement_state("显示");
            }
        }
        if (StringUtils.isNotBlank(agreement_version)){
            distributionAgreement.setAgreement_version(agreement_version);
        }

//        distributionAgreement.setAddTime(CommUtil.formatDate(CommUtil.getCurrentDate()));
		distributionAgreement.setAddTime(new Date());

		redPigDistributionAgreementService.saveEntity(distributionAgreement);

        return "redirect:distribution_agreement_list";
	}

	/**
	 * 查看协议
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "查看协议", value = "/distribution_agreement_look*", rtype = "admin", rname = "查看协议", rcode = "direct_set", rgroup = "分销管理")
	@RequestMapping({ "/distribution_agreement_look" })
	public ModelAndView distribution_agreement_look(HttpServletRequest request, HttpServletResponse response,String id,String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/agreement/agreement_look.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);



        if (StringUtils.isNotBlank(id)){
            DistributionAgreement distributionAgreement = redPigDistributionAgreementService.selectByPrimaryKey(Long.parseLong(id));
            mv.addObject("objs",distributionAgreement);

            mv.addObject("currentPage",currentPage);

        }
		return mv;
	}

    /**
     * 编辑协议页面
     * @param request
     * @param response
     * @return
     */
    @SecurityMapping(title = "编辑协议页面", value = "/distribution_agreement_update*", rtype = "admin", rname = "编辑协议页面 ", rcode = "direct_set", rgroup = "分销管理")
    @RequestMapping({ "/distribution_agreement_update" })
    public ModelAndView distribution_agreement_update(HttpServletRequest request,
                                                      HttpServletResponse response,String id,String currentPage) {
        ModelAndView mv = new RedPigJModelAndView(
                "admin/blue/agreement/agreement_update.html",
                this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 0, request, response);
        if (StringUtils.isNotBlank(id)){
            DistributionAgreement distributionAgreement = redPigDistributionAgreementService.selectByPrimaryKey(Long.parseLong(id));
            mv.addObject("objs",distributionAgreement);
            mv.addObject("currentPage",currentPage);
        }
        return mv;
    }

	@SecurityMapping(title = "编辑协议", value = "/distribution_agreement_update_save*", rtype = "admin", rname = "编辑协议 ", rcode = "direct_set", rgroup = "分销管理")
	@RequestMapping({ "/distribution_agreement_update_save" })
	public String distribution_agreement_update_save(HttpServletRequest request,
													  HttpServletResponse response,String id,String agreement_name,
													  String agreement_type,String agreement,String currentPage,String agreement_state,String agreement_version) {

		DistributionAgreement distributionAgreement = new DistributionAgreement();

		if (StringUtils.isNotBlank(id)){
			distributionAgreement.setId(Long.parseLong(id));
		}
		if (StringUtils.isNotBlank(agreement_type)){
			distributionAgreement.setAgreement_type(agreement_type);
		}
		if (StringUtils.isNotBlank(agreement_name)){
			distributionAgreement.setAgreement_name(agreement_name);
		}
		if (StringUtils.isNotBlank(agreement)){
			//剔出<html>的标签,去除字符串中的空格,回车,换行符,制表符
			agreement  =agreement.replaceAll("</?[^>]+>", "").replaceAll("\\s*|\t|\r|\n", "");
			distributionAgreement.setAgreement(agreement);
		}
		if (StringUtils.isNotBlank(agreement_state)){
			if (agreement_state.equals(0)){
				distributionAgreement.setAgreement_state("隐藏");
			}else {
				distributionAgreement.setAgreement_state("显示");
			}
		}
		if (StringUtils.isNotBlank(agreement_version)){
			distributionAgreement.setAgreement_version(agreement_version);
		}

		distributionAgreement.setUpdate_time(new Date());

		String number = currentPage;

		redPigDistributionAgreementService.updateById(distributionAgreement);
        return "redirect:distribution_agreement_list?currentPage=$!currentPage";
	}

    /**
     * 删除协议
     * @param request
     * @param response
     * @param id
     * @return
     */
	@SecurityMapping(title = "删除协议", value = "/distribution_agreement_delete*", rtype = "admin", rname = "删除协议 ", rcode = "direct_set", rgroup = "分销管理")
	@RequestMapping({ "/distribution_agreement_delete" })
	public String distribution_agreement_delete(HttpServletRequest request,
													  HttpServletResponse response,String id,String currentPage) {
		if (StringUtils.isNotBlank(id) && id != null){
                redPigDistributionAgreementService.deleteById(Long.parseLong(id));
            }
        return "redirect:distribution_agreement_list?currentPage=" + currentPage;
	}


	/**
	 * 一级分销商
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "一级分销商", value = "/stair_distribution_manage*", rtype = "admin", rname = "一级分销商", rcode = "direct_selling_goods_list", rgroup = "分销管理")
	@RequestMapping({ "/stair_distribution_manage" })
	public ModelAndView stair_distribution_manage(
			HttpServletRequest request, HttpServletResponse response,
			String currentPage, String orderBy, String orderType,
			String grade_id, String mobile,String beginTime, String endTime) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/distribution/stair_distribution_manage.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);

		Map<String, Object> params = this.redPigQueryTools.getParams(currentPage,"", "");
		if(StringUtils.isNotBlank(grade_id)){
			mv.addObject("grade_id",grade_id);
			params.put("grade_id",grade_id);
		}
		if(StringUtils.isNotBlank(mobile)){
			mv.addObject("mobile",mobile);
			params.put("mobile",mobile);
		}
		if(StringUtils.isNotBlank(beginTime)){
			mv.addObject("beginTime",beginTime);
			params.put("beginTime",CommUtil.formatDate(beginTime));
		}
		if(StringUtils.isNotBlank(endTime)){
			mv.addObject("endTime",endTime);
			params.put("endTime",CommUtil.formatDate(endTime));
		}
		params.put("type",1);
		IPageList list = this.redPigDistributionCommissionService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", list, mv);
		//		分销等级
		List<DistributionGrade> distributionGrades = redPigDistributionGradeService.queryPages(params);

		mv.addObject("first",distributionGrades);
		return mv;
	}


	@SecurityMapping(title = "二级分销商", value = "/two_distribution_manage*", rtype = "admin", rname = "一级分销商", rcode = "direct_selling_goods_list", rgroup = "分销管理")
	@RequestMapping({ "/two_distribution_manage" })
	public ModelAndView two_distribution_manage(
			HttpServletRequest request, HttpServletResponse response,
			String currentPage, String orderBy, String orderType,
			String grade_id, String mobile,String beginTime, String endTime) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/distribution/two_distribution_manage.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);

		Map<String, Object> params = this.redPigQueryTools.getParams(currentPage,"", "");
		if(StringUtils.isNotBlank(grade_id)){
			mv.addObject("grade_id",grade_id);
			params.put("grade_id",grade_id);
		}
		if(StringUtils.isNotBlank(mobile)){
			mv.addObject("mobile",mobile);
			params.put("mobile",mobile);
		}
		if(StringUtils.isNotBlank(beginTime)){
			mv.addObject("beginTime",beginTime);
			params.put("beginTime",CommUtil.formatDate(beginTime));
		}
		if(StringUtils.isNotBlank(endTime)){
			mv.addObject("endTime",endTime);
			params.put("endTime",CommUtil.formatDate(endTime));
		}
		params.put("type",2);
		IPageList list = this.redPigDistributionCommissionService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", list, mv);
		//		分销等级
		List<DistributionGrade> distributionGrades = redPigDistributionGradeService.queryPages(params);

		mv.addObject("first",distributionGrades);
		return mv;
	}



}
