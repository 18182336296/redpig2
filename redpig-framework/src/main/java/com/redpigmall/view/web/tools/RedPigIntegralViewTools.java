package com.redpigmall.view.web.tools;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.User;
import com.redpigmall.domain.UserLevel;
import com.redpigmall.service.RedPigSysConfigService;
import com.redpigmall.service.RedPigUserLevelService;
import com.redpigmall.service.RedPigUserService;

/**
 * 
 * 
 * <p>
 * Title:IntegralViewTools.java
 * </p>
 * 
 * <p>
 * Description:积分商城工具类
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
 * 
 * @date 2014年4月19日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("rawtypes")
@Component
public class RedPigIntegralViewTools {
	@Autowired
	private RedPigSysConfigService configService;
	@Autowired
	private RedPigUserService userService;
	@Autowired
	private RedPigUserLevelService userLevelService;

	/**
	 * 根据会员的id，确定其会员等级
	 * 
	 * @param user_goods_fee
	 * @return 0—铜牌会员1—银牌会员2—金牌会员3—超级会员
	 */

	public Map query_user_level(String id) {
		Map result_map = null;
		int i = 0;
		Map target = Maps.newHashMap();
		if (this.configService.getSysConfig().getUser_level() != null) {
			if (!this.configService.getSysConfig().getUser_level().equals("")) {
				String json = this.configService.getSysConfig().getUser_level();
				User user = this.userService.selectByPrimaryKey(CommUtil
						.null2Long(id));
				if (user.getUser_goods_fee() == null) {
					user.setUser_goods_fee(new BigDecimal(0));
					this.userService.updateById(user);
				}
				int goods_fee = Math.round(CommUtil.null2Float(user
						.getUser_goods_fee()));

				List<Map> maps = JSON.parseArray(json, Map.class);
				if (maps.size() >= 1) {
					Map heigh = null;
					for (Map map : maps) {
						if (CommUtil.null2Int(map.get("credit_up")) == -1) {
							heigh = map;
							break;
						}
					}
					if (heigh == null) {
						for (Map level_map : maps) {
							if (heigh == null) {
								heigh = level_map;
							} else if (CommUtil.null2Int(heigh.get("level")) < CommUtil
									.null2Int(level_map.get("level"))) {
								heigh = level_map;
							}
						}
					}
					if (CommUtil.null2Int(heigh.get("credit_down")) <= goods_fee) {
						target = heigh;
					} else {
						for (Map map : maps) {
							if ((CommUtil.null2Int(map.get("credit_up")) != -1)
									&& (CommUtil.null2Int(map
											.get("credit_down")) <= goods_fee)
									&& (CommUtil.null2Int(map.get("credit_up")) >= goods_fee)) {
								target = map;
								break;
							}
						}
					}
				}
				i = CommUtil.null2Int(target.get("level"));
			}
		} else {
			UserLevel userlevel = new UserLevel();
			userlevel.setAddTime(new Date());
			userlevel.setLevel_down(0);
			userlevel.setLevel_up(1000);
			userlevel
					.setLevel_icon("/resources/style/common/images/userlevel_0.png");
			userlevel.setLevel_icon_type(0);
			userlevel.setLevel_name("铜牌会员");
			userlevel.setSys_seq("0");
			this.userLevelService.saveEntity(userlevel);
			List<Map> list = Lists.newArrayList();
			Map<String, Object> map = Maps.newHashMap();
			map.put("name", "铜牌会员");
			map.put("level", Integer.valueOf(1));
			map.put("icon", "/resources/style/common/images/userlevel_0.png");
			map.put("credit_down", Integer.valueOf(0));
			map.put("credit_up", Integer.valueOf(1000));
			map.put("sys_seq", "0");
			map.put("style", "system");
			list.add(map);
			String json = JSON.toJSONString(list);
			this.configService.getSysConfig().setUser_level(json);
			this.configService.update(this.configService.getSysConfig());
			result_map = map;
		}
		if (i != 0) {
			result_map = query_level(CommUtil.null2String(Integer.valueOf(i)));
		}
		return result_map;
	}

	public List<Map> query_all_level() {
		List<Map> list = Lists.newArrayList();
		if (this.configService.getSysConfig().getUser_level() != null) {
			if (!this.configService.getSysConfig().getUser_level().equals("")) {
				list = JSON.parseArray(this.configService.getSysConfig()
						.getUser_level(), Map.class);
				return list;
			}
		}
		UserLevel userlevel = new UserLevel();
		userlevel.setAddTime(new Date());
		userlevel.setLevel_down(0);
		userlevel.setLevel_up(1000);
		userlevel
				.setLevel_icon("/resources/style/common/images/userlevel_0.png");
		userlevel.setLevel_icon_type(0);
		userlevel.setLevel_name("铜牌会员");
		userlevel.setSys_seq("0");
		this.userLevelService.saveEntity(userlevel);
		list = Lists.newArrayList();
		Map<String, Object> map = Maps.newHashMap();
		map.put("name", "铜牌会员");
		map.put("level", Integer.valueOf(1));
		map.put("icon", "/resources/style/common/images/userlevel_0.png");
		map.put("credit_down", Integer.valueOf(0));
		map.put("credit_up", Integer.valueOf(1000));
		map.put("sys_seq", "0");
		map.put("style", "system");
		list.add(map);
		String json = JSON.toJSONString(list);
		this.configService.getSysConfig().setUser_level(json);
		this.configService.update(this.configService.getSysConfig());

		return list;
	}

	@SuppressWarnings("unchecked")
	public Map query_level(String level) {
		int temp_level = CommUtil.null2Int(level);
		List<Map> list = null;
		Map<String, Object> map = Maps.newHashMap();
		if (this.configService.getSysConfig().getUser_level() != null) {
			if (!this.configService.getSysConfig().getUser_level().equals("")) {
				list = JSON.parseArray(this.configService.getSysConfig()
						.getUser_level(), Map.class);
				for (Map level_map : list) {
					if (CommUtil.null2Int(level_map.get("level")) == temp_level) {
						map = level_map;
						break;
					}
				}
			}
		}
		list = Lists.newArrayList();
		UserLevel userlevel = new UserLevel();
		userlevel.setAddTime(new Date());
		userlevel.setLevel_down(0);
		userlevel.setLevel_up(1000);
		userlevel
				.setLevel_icon("/resources/style/common/images/userlevel_0.png");
		userlevel.setLevel_icon_type(0);
		userlevel.setLevel_name("铜牌会员");
		userlevel.setSys_seq("0");
		this.userLevelService.saveEntity(userlevel);
		map.put("name", "铜牌会员");
		map.put("level", Integer.valueOf(1));
		map.put("icon", "/resources/style/common/images/userlevel_0.png");
		map.put("credit_down", Integer.valueOf(0));
		map.put("credit_up", Integer.valueOf(1000));
		map.put("sys_seq", "0");
		map.put("style", "system");
		list.add(map);
		String json = JSON.toJSONString(list);
		this.configService.getSysConfig().setUser_level(json);
		this.configService.update(this.configService.getSysConfig());

		return map;
	}
}
