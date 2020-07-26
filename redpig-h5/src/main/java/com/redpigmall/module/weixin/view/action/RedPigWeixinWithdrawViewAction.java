package com.redpigmall.module.weixin.view.action;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.User;
import com.redpigmall.domain.WithawalRecord;
import com.redpigmall.module.weixin.view.action.base.BaseAction;
import com.redpigmall.service.RedPigWithdrawRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 
 * <p>
 * Title: RedPigWapGroupViewAction.java
 * </p>
 * 
 * <p>
 * Description:微信提现申请
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
 * @date 2018-9-11
 * 
 * @version redpigmall_b2b2c v8.0 2018版
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigWeixinWithdrawViewAction extends BaseAction{

	@Autowired
	private RedPigWithdrawRecordService withdrawRecordService;
	/**
	 * 提现申请
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "提现申请", value = "/withdraw_apply", rtype = "admin", rname = "提现申请", rcode = "direct_set", rgroup = "提现申请")
	@RequestMapping({ "/income/withdraw_apply" })
	public ModelAndView withdraw_apply(HttpServletRequest request,
							 HttpServletResponse response, String currentPage, String orderBy,
							 String orderType,String user_id,String withdrawal_way) {

		ModelAndView mv = new RedPigJModelAndView("weixin/income/withdraw_record.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (SecurityUserHolder.getCurrentUser().getId()==null){
			return mv;
		}

		String url = this.configService.getSysConfig().getAddress();

		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}

		WithawalRecord record = new WithawalRecord();
		record.setUserId(SecurityUserHolder.getCurrentUser().getId());
		record.setWithdrawalWay(Byte.valueOf(withdrawal_way));
		record.setCreateTime(new Date());
		record.setAcceptStatus((byte)1);

		this.withdrawRecordService.insertSelective(record);
		return mv;
	}

	/**
	 * 提现记录
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "提现记录", value = "/withdraw_record", rtype = "admin", rname = "提现记录", rcode = "direct_set", rgroup = "提现记录")
	@RequestMapping({ "/income/withdraw_record" })
	public @ResponseBody Map<String,Object> withdraw_record(HttpServletRequest request,
								HttpServletResponse response, String currentPage, String orderBy,
								String orderType) {
		Map<String,Object> resultMap = new HashMap<>();

		User user = SecurityUserHolder.getCurrentUser();
		if (user == null){
			resultMap.put("code","530");
			resultMap.put("data",null);
			resultMap.put("msg","用户未登录！");
			return resultMap;
		}
		String url = this.configService.getSysConfig().getAddress();

		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";

		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, "createTime", "desc");
		maps.put("userId",SecurityUserHolder.getCurrentUser().getId());
		List<WithawalRecord> withawalRecords = this.withdrawRecordService.list(maps).getResult();

		if (withawalRecords!=null){
			resultMap.put("code","200");
			resultMap.put("data",withawalRecords);
			resultMap.put("msg","查询成功！");
		}
		return resultMap;
	}

}
