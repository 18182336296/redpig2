package com.redpigmall.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.redpigmall.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.dao.UserMapper;
import com.redpigmall.domain.ChattingConfig;
import com.redpigmall.domain.ChattingLog;
import com.redpigmall.domain.ChattingUser;
import com.redpigmall.dao.ChattingConfigMapper;
import com.redpigmall.dao.ChattingLogMapper;
import com.redpigmall.service.RedPigChattingLogService;
import com.redpigmall.service.RedPigChattingUserService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigChattingLogService extends BaseService<ChattingLog>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<ChattingLog> objs) {
		if (objs != null && objs.size() > 0) {
			redPigChattingLogMapper.batchDelete(objs);
		}
	}


	public ChattingLog getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<ChattingLog> objs = redPigChattingLogMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<ChattingLog> selectObjByProperty(Map<String, Object> maps) {
		return redPigChattingLogMapper.selectObjByProperty(maps);
	}


	public List<ChattingLog> queryPages(Map<String, Object> params) {
		return redPigChattingLogMapper.queryPages(params);
	}


	public List<ChattingLog> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigChattingLogMapper.queryPageListWithNoRelations(param);
	}


	public List<ChattingLog> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigChattingLogMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private ChattingLogMapper redPigChattingLogMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigChattingLogMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(ChattingLog obj) {
		redPigChattingLogMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(ChattingLog obj) {
		redPigChattingLogMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigChattingLogMapper.deleteById(id);
	}


	public ChattingLog selectByPrimaryKey(Long id) {
		return redPigChattingLogMapper.selectByPrimaryKey(id);
	}


	public List<ChattingLog> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<ChattingLog> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigChattingLogMapper.selectCount(params);
		if (c == null) {
			return 0;
		}
		return c;
	}

//	@Autowired
//	private ChattingUserMapper chattingUserMapper;
	
	@Autowired
	private RedPigChattingUserService chattingUserService;
	
	@Transactional(readOnly = false)
	public List<ChattingLog> queryServiceUnread(Long service_id, Long user_id) {
		List<ChattingLog> logs = Lists.newArrayList();
		ChattingUser service = this.chattingUserService.getObjByProperty("chatting_user_id", "=", service_id);
		
		if (service != null) {
			Map<String, Object> params = Maps.newHashMap();
			if ((user_id != null) && (!user_id.equals(""))) {
				params.put("service_id", service_id);
				params.put("service_read", Integer.valueOf(0));
				params.put("user_id", user_id);
				logs = this.chattingLogMapper.queryPageList(params);
			} else {
				params.put("service_id", service_id);
				params.put("service_read", Integer.valueOf(0));
				logs = this.chattingLogMapper.queryPageList(params);
			}
		}
		
		for (ChattingLog log : logs) {
			log.setService_read(1);
			this.chattingLogMapper.updateById(log);
		}
		
		return logs;
	}

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private ChattingLogMapper chattingLogMapper;

	@Transactional(readOnly = false)
	public List<ChattingLog> queryUserUnread(Long user_id, Long service_id) {
		List<ChattingLog> logs = Lists.newArrayList();
		User user = userMapper.selectByPrimaryKey(user_id);

		if (user != null) {
			Map<String, Object> params = Maps.newHashMap();
			if ((service_id != null) && (!service_id.equals(""))) {
//				params.put("service_id", service_id);
				params.put("chatting_user_service_id", service_id);
				params.put("user_read", Integer.valueOf(0));
				
				params.put("user_id", user.getId());
				params.put("orderBy", "addTime");
				params.put("orderType", "asc");

				logs = this.chattingLogMapper.queryPageList(params);

			} else {
				params.put("user_read", Integer.valueOf(0));

				params.put("user_id", user.getId());
				params.put("orderBy", "addTime");
				params.put("orderType", "asc");

				logs = this.chattingLogMapper.queryPageList(params);

			}
		}

		for (ChattingLog log : logs) {
			log.setUser_read(1);
			this.chattingLogMapper.updateById(log);
		}

		return logs;
	}

	@Autowired
	private ChattingConfigMapper chattingConfigMapper;

	@Transactional(readOnly = false)
	public ChattingConfig getConfig(Long user_id, Long service_id) {
		ChattingConfig config = null;
		Map<String, Object> params = Maps.newHashMap();
		if ((user_id != null) && (!user_id.equals(""))) {
			params.put("user_id", user_id);
			params.put("begin", 0);
			params.put("end", 1);

			List<ChattingConfig> configs = this.chattingConfigMapper.queryPageList(params);

			if (configs.size() > 0) {
				config = (ChattingConfig) configs.get(0);
			} else {
				config = new ChattingConfig();
				config.setAddTime(new Date());
				config.setUser_id(user_id);
				this.chattingConfigMapper.saveEntity(config);
			}
		}
		if ((service_id != null) && (!service_id.equals(""))) {
			params.put("service_id", service_id);
			params.put("begin", 0);
			params.put("end", 1);
			List<ChattingConfig> configs = this.chattingConfigMapper.queryPageList(params);

			if (configs.size() > 0) {
				config = (ChattingConfig) configs.get(0);
			} else {
				config = new ChattingConfig();
				config.setAddTime(new Date());
				config.setService_id(service_id);
				this.chattingConfigMapper.saveEntity(config);
			}
		}
		return config;
	}

	@Transactional(readOnly = false)
	public ChattingLog saveServiceChattLog(String content, Long service_id, Long user_id, String font,
			String font_colour, String font_size) {
		ChattingUser cuser = this.chattingUserService.selectByPrimaryKey(service_id);

		User user = this.userMapper.selectByPrimaryKey(CommUtil.null2Long(user_id));

		ChattingConfig config = getConfig(null, service_id);
		if ((config.getFont() == null) || (config.getFont_colour() == null) || (config.getFont_size() == null)
				|| (!config.getFont().equals(font)) || (!config.getFont_colour().equals(font_colour))
				|| (!config.getFont_size().equals(font_size))) {
			config.setFont(font);
			config.setFont_colour(font_colour);
			config.setFont_size(font_size);
			this.chattingConfigMapper.updateById(config);
		}
		ChattingLog log = new ChattingLog();
		log.setAddTime(new Date());
		log.setContent(content);
		log.setFont(config.getFont());
		log.setFont_size(config.getFont_size());
		log.setFont_colour(config.getFont_colour());
		log.setUser_id(user.getId());
		log.setUser_name(user.getUserName());
		log.setService_id(service_id);
		log.setService_name(cuser.getChatting_name());
		log.setService_read(1);
		log.setSend_from("ServiceImpl");
		this.chattingLogMapper.saveEntity(log);
		return log;
	}

	@Transactional(readOnly = false)
	public ChattingLog saveUserChattLog(String content, Long service_id, Long user_id, String font, String font_colour,
			String font_size) {
		
		ChattingUser cuser = this.chattingUserService.getObjByProperty("chatting_user_id", "=", service_id);
		
		User user = this.userMapper.selectByPrimaryKey(CommUtil.null2Long(user_id));
		
		ChattingConfig config = getConfig(user_id, null);
		if ((config.getFont() == null) || (config.getFont_colour() == null) || (config.getFont_size() == null)
				|| (!config.getFont().equals(font)) || (!config.getFont_colour().equals(font_colour))
				|| (!config.getFont_size().equals(font_size))) {
			config.setFont(font);
			config.setFont_colour(font_colour);
			config.setFont_size(font_size);
			this.chattingConfigMapper.updateById(config);
		}
		ChattingLog log = new ChattingLog();
		log.setAddTime(new Date());
		log.setContent(content);
		log.setFont(config.getFont());
		log.setFont_size(config.getFont_size());
		log.setFont_colour(config.getFont_colour());
		log.setUser_id(user.getId());
		log.setUser_name(user.getUserName());
		log.setService_id(service_id);
		log.setService_name(cuser.getChatting_name());
		log.setUser_read(1);
		log.setSend_from("user");
		this.chattingLogMapper.saveEntity(log);
		if (config.getQuick_reply_open() == 1) {
			ChattingLog log2 = new ChattingLog();
			log2.setAddTime(new Date());
			log2.setContent(config.getQuick_reply_content() + "[自动回复]");
			log2.setFont(config.getFont());
			log2.setFont_size(config.getFont_size());
			log2.setFont_colour(config.getFont_colour());
			log2.setService_read(1);
			this.chattingLogMapper.saveEntity(log2);
		}
		return log;
	}

}
