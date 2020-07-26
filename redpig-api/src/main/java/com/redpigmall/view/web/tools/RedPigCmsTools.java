package com.redpigmall.view.web.tools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.InformationClass;
import com.redpigmall.service.RedPigInformationClassService;
import com.redpigmall.service.RedPigInformationReplyService;
/**
 * <p>
 * Title: RedPigCmsTools.java
 * </p>
 * 
 * <p>
 * Description:CMS咨询处理工具
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2017-3-21
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("rawtypes")
@Component
public class RedPigCmsTools {
	@Autowired
	private RedPigInformationClassService informationClassService;
	@Autowired
	private RedPigInformationReplyService informationReplyService;

	
	public List<Map> getCmsList(String json) {
		List<Map> maps = null;
		if ((json != null) && (!json.equals(""))) {
			JSON.parse(json);
		}
		return maps;
	}

	public Map getCmsMap(String json) {
		Map<String, Object> map = null;
		if ((json != null) && (!json.equals(""))) {
			JSON.parse(json);
		}
		return map;
	}

	public String queryInforClass(String id) {
		InformationClass informationClass = this.informationClassService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (informationClass != null) {
			return informationClass.getIc_name();
		}
		return "";
	}

	public List<InformationClass> queryChildClass(String id) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("ic_pid", CommUtil.null2Long(id));
		
		List<InformationClass> informationClasses = this.informationClassService.queryPageList(map);
		return informationClasses;
	}

	public int queryComment(String id) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("info_id", CommUtil.null2Long(id));
		return this.informationReplyService.selectCount(params);
	}
}
