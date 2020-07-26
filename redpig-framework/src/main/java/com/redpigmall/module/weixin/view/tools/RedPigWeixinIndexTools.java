package com.redpigmall.module.weixin.view.tools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.redpigmall.domain.WeixinFloor;
import com.redpigmall.service.RedPigWeixinFloorService;

@Component
public class RedPigWeixinIndexTools {
	@Autowired
	private RedPigWeixinFloorService weixinfloorService;

	@SuppressWarnings("rawtypes")
	public List<Map> getFloorInfo(Long id) {
		WeixinFloor weixinFloor = this.weixinfloorService.selectByPrimaryKey(id);
		List<Map> list = JSON.parseArray(weixinFloor.getLines_info(), Map.class);
		return list;
	}
}
