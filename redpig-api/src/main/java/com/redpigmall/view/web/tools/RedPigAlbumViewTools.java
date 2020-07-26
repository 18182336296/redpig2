package com.redpigmall.view.web.tools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Accessory;
import com.redpigmall.service.RedPigAccessoryService;


/**
 * <p>
 * Title: RedPigAlbumViewTools.java
 * </p>
 * 
 * <p>
 * Description:商城图片处理工具类
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
@Component
public class RedPigAlbumViewTools {

	@Autowired
	private RedPigAccessoryService accessoryService;

	public List<Accessory> query_album(String id) {
		List<Accessory> list = Lists.newArrayList();
		Map<String,Object> params = Maps.newHashMap();
		if ((id != null) && (!id.equals(""))) {
			params.put("album_id", CommUtil.null2Long(id));
			list = this.accessoryService.queryPageList(params);
		} else {
			params.put("album_id", -1);
			list = this.accessoryService.queryPageList(params);
		}
		return list;
	}
}
