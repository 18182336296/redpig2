package com.redpigmall.module.circle.view.tools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Circle;
import com.redpigmall.domain.CircleInvitation;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;
import com.redpigmall.service.RedPigCircleInvitationService;
import com.redpigmall.service.RedPigCircleService;
import com.redpigmall.service.RedPigSysConfigService;
import com.redpigmall.service.RedPigUserService;

/**
 * 
 * <p>
 * Title: CircleViewTools.java
 * </p>
 * 
 * <p>
 * Description: 圈子前台显示工具类
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
 * @date 2014-12-3
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("rawtypes")
@Component
public class RedPigCircleViewTools {
	@Autowired
	private RedPigSysConfigService configService;
	@Autowired
	private RedPigUserService userService;
	@Autowired
	private RedPigCircleService circleService;
	@Autowired
	private RedPigCircleInvitationService invitationService;
	/**
	 * 判断当前用户是否关注了指定圈子
	 * 
	 * @param cid
	 * @param uid
	 * @return
	 */
	public String generUserAttention(String cid, String uid) {
		String ret = "false";
		User user = this.userService
				.selectByPrimaryKey(CommUtil.null2Long(uid));
		List<Map> map_list = Lists.newArrayList();
		if ((user.getCircle_attention_info() != null)
				&& (!user.getCircle_attention_info().equals(""))) {
			map_list = JSON.parseArray(user.getCircle_attention_info(),
					Map.class);
			for (Map temp : map_list) {
				if (CommUtil.null2String(temp.get("id")).equals(cid)) {
					ret = "true";
					break;
				}
			}
		}
		return ret;
	}
	/**
	 * 根据圈子id获取圈子图标
	 * 
	 * @param cid
	 * @param uid
	 * @return
	 */
	public Map genercircleImage(String cid) {
		Circle obj = this.circleService.selectByPrimaryKey(CommUtil
				.null2Long(cid));
		if ((obj != null)
				&& (!CommUtil.null2String(obj.getPhotoInfo()).equals(""))) {
			Map<String, Object> map = JSON.parseObject(obj.getPhotoInfo());
			return map;
		}
		return Maps.newHashMap();
	}
	/**
	 * 根据圈子id获取圈子
	 * 
	 * @param cid
	 * @param uid
	 * @return
	 */
	public Circle genercircleInfo(String cid) {
		Circle obj = this.circleService.selectByPrimaryKey(CommUtil
				.null2Long(cid));
		return obj;
	}
	/**
	 * 根据圈子id获取用户图标
	 * 
	 * @param cid
	 * @param uid
	 * @return
	 */
	public String generUserIcon(String uid) {
		String src = "";
		User user = this.userService
				.selectByPrimaryKey(CommUtil.null2Long(uid));
		SysConfig sc = this.configService.getSysConfig();
		src = sc.getMemberIcon().getPath() + "/" + sc.getMemberIcon().getName();
		if (user.getPhoto() != null) {
			src = user.getPhoto().getPath() + "/" + user.getPhoto().getName();
		}
		return src;
	}
	/**
	 * 根据帖子id和用户id获取当前用户是否点赞该帖子
	 * 
	 * @param cid
	 * @param uid
	 * @return
	 */
	public String generInvitationParise(String id, String uid) {
		String ret = "false";
		CircleInvitation invit = this.invitationService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		String temp = "," + uid + ",";
		if ((invit.getPraiseInfo() != null)
				&& (!invit.getPraiseInfo().equals(""))
				&& (invit.getPraiseInfo().indexOf(temp) >= 0)) {
			ret = "true";
		}
		return ret;
	}
	/**
	 * 清除帖子详情中的图片信息
	 * 
	 * @param cid
	 * @param uid
	 * @return
	 */
	public String clearImages(String content) {
		content = content.replaceAll("<img.*/>", "");
		return content;
	}
}
