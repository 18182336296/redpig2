package com.redpigmall.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.ChattingConfig;
import com.redpigmall.domain.ChattingUser;
import com.redpigmall.dao.ChattingConfigMapper;
import com.redpigmall.service.RedPigChattingConfigService;
import com.redpigmall.service.RedPigChattingUserService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigChattingConfigService extends BaseService<ChattingConfig> {
	@Autowired
	private RedPigChattingUserService chattingUserService;

	@Transactional(readOnly = false)
	public void batchDelObjs(List<ChattingConfig> objs) {
		if (objs != null && objs.size() > 0) {
			redPigChattingConfigMapper.batchDelete(objs);
		}
	}


	public ChattingConfig getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<ChattingConfig> objs = redPigChattingConfigMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<ChattingConfig> selectObjByProperty(Map<String, Object> maps) {
		return redPigChattingConfigMapper.selectObjByProperty(maps);
	}


	public List<ChattingConfig> queryPages(Map<String, Object> params) {
		return redPigChattingConfigMapper.queryPages(params);
	}


	public List<ChattingConfig> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigChattingConfigMapper.queryPageListWithNoRelations(param);
	}


	public List<ChattingConfig> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigChattingConfigMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private ChattingConfigMapper redPigChattingConfigMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigChattingConfigMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(ChattingConfig obj) {
		redPigChattingConfigMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(ChattingConfig obj) {
		redPigChattingConfigMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigChattingConfigMapper.deleteById(id);
	}


	public ChattingConfig selectByPrimaryKey(Long id) {
		return redPigChattingConfigMapper.selectByPrimaryKey(id);
	}


	public List<ChattingConfig> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<ChattingConfig> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigChattingConfigMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}

	@Transactional(readOnly = false)
	public ChattingConfig getChattingConfig(Long user_id, Long service_id) {
		ChattingConfig config = null;
		Map<String, Object> params = Maps.newHashMap();
		if ((user_id != null) && (!user_id.equals(""))) {
			params.put("user_id", user_id);
			List<ChattingConfig> configs = this.queryPageList(params, 0, 1);
			if (configs.size() > 0) {
				config = (ChattingConfig) configs.get(0);
			} else {
				config = new ChattingConfig();
				config.setAddTime(new Date());
				config.setUser_id(user_id);
				this.saveEntity(config);
			}
		}
		if ((service_id != null) && (!service_id.equals(""))) {
			
			ChattingUser cu = this.chattingUserService.selectByPrimaryKey(CommUtil.null2Long(service_id));
			
			params.put("service_id", service_id);
			List<ChattingConfig> configs = this.queryPageList(params, 0, 1);
			if (configs.size() > 0) {
				config = (ChattingConfig) configs.get(0);
			} else {
				config = new ChattingConfig();
				config.setAddTime(new Date());
				config.setService_id(service_id);
				config.setKf_name(cu.getChatting_name());
				this.saveEntity(config);
			}
		}
		return config;
	}

}
