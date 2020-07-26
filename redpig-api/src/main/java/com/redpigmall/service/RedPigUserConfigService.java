package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.User;
import com.redpigmall.domain.UserConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.dao.UserMapper;
import com.redpigmall.dao.UserConfigMapper;
import com.redpigmall.service.RedPigUserConfigService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigUserConfigService extends BaseService<UserConfig>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<UserConfig> objs) {
		if (objs != null && objs.size() > 0) {
			redPigUserConfigMapper.batchDelete(objs);
		}
	}


	public UserConfig getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<UserConfig> objs = redPigUserConfigMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<UserConfig> selectObjByProperty(Map<String, Object> maps) {
		return redPigUserConfigMapper.selectObjByProperty(maps);
	}


	public List<UserConfig> queryPages(Map<String, Object> params) {
		return redPigUserConfigMapper.queryPages(params);
	}


	public List<UserConfig> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigUserConfigMapper.queryPageListWithNoRelations(param);
	}


	public List<UserConfig> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigUserConfigMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private UserConfigMapper redPigUserConfigMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigUserConfigMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(UserConfig obj) {
		redPigUserConfigMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(UserConfig obj) {
		redPigUserConfigMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigUserConfigMapper.deleteById(id);
	}

	@Autowired
	private UserMapper redPigUserMapper;


	public UserConfig selectByPrimaryKey(Long id) {
		return redPigUserConfigMapper.selectByPrimaryKey(id);
	}


	public List<UserConfig> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public UserConfig getUserConfig() {

		UserConfig config = null;
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.redPigUserMapper.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			config = user.getConfig();
		} else {
			config = new UserConfig();
		}

		return config;
	}


	public List<UserConfig> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigUserConfigMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
