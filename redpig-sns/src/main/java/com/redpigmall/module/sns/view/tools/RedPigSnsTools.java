package com.redpigmall.module.sns.view.tools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Favorite;
import com.redpigmall.domain.UserShare;
import com.redpigmall.service.RedPigFavoriteService;
import com.redpigmall.service.RedPigSnsAttentionService;
import com.redpigmall.service.RedPigUserShareService;

/**
 * <p>
 * Title: RedPigSnsTools.java
 * </p>
 * 
 * <p>
 * Description: SNS相关工具类
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
 * @date 2014-11-23
 * 
 * @version redpigmall_b2b2c 2016
 */
@Component
public class RedPigSnsTools {
	@Autowired
	private RedPigSnsAttentionService snsAttentionService;
	@Autowired
	private RedPigFavoriteService favoriteService;
	@Autowired
	private RedPigUserShareService userShareService;

	public int queryFans(String user_id) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("toUser_id", CommUtil.null2Long(user_id));
		
		int fans = this.snsAttentionService.selectCount(params);
		return fans;
	}

	public int queryAtts(String user_id) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("fromUser_id", CommUtil.null2Long(user_id));
		
		int atts = this.snsAttentionService.selectCount(params);
		
		return atts;
	}

	public int queryfavCount(String user_id) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", CommUtil.null2Long(user_id));
		params.put("type",0);
		
		int favsCount = this.favoriteService.selectCount(params);
		
		return favsCount;
	}

	public UserShare querylastUserShare(Long user_id) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", user_id);
		
		List<UserShare> userShares = this.userShareService.queryPageList(params,0,1);
		if (userShares.size() > 0) {
			return (UserShare) userShares.get(0);
		}
		return null;
	}
	
	public Favorite queryLastUserFav(Long user_id) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", user_id);
		params.put("type", 0);
		
		List<Favorite> favorites = this.favoriteService.queryPageList(params,0,1);
		
		if (favorites.size() > 0) {
			return (Favorite) favorites.get(0);
		}
		return null;
	}

	public boolean whetherAttention(String fromUser, String toUser) {
		boolean ret = false;
		Map<String, Object> params = Maps.newHashMap();
		params.put("fromUser_id", CommUtil.null2Long(fromUser));
		params.put("toUser_id", CommUtil.null2Long(toUser));
		
		int count = this.snsAttentionService.selectCount(params);
		
		if (count > 0) {
			ret = true;
		}
		return ret;
	}
	
	public boolean whetherAttentionId(String fromUser_id, String toUser_id) {
		boolean ret = false;
		Map<String, Object> params = Maps.newHashMap();
		params.put("fromUser_id", CommUtil.null2Long(fromUser_id));
		params.put("toUser_id", CommUtil.null2Long(toUser_id));
		
		int count = this.snsAttentionService.selectCount(params);
		
		if (count > 0) {
			ret = true;
		}
		return ret;
	}
	
	@SuppressWarnings("rawtypes")
	public List getDynamic_img_info(String img_info) {
		List<Map> list = JSON.parseArray(img_info, Map.class);
		return list;
	}
}
