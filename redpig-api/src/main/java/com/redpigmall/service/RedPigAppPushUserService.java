package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.AppPushUser;
import com.redpigmall.dao.AppPushUserMapper;
import com.redpigmall.service.RedPigAppPushUserService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigAppPushUserService extends BaseService<AppPushUser>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<AppPushUser> objs) {
		if (objs != null && objs.size() > 0) {
			redPigAppPushUserMapper.batchDelete(objs);
		}
	}


	public AppPushUser getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<AppPushUser> objs = redPigAppPushUserMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<AppPushUser> selectObjByProperty(Map<String, Object> maps) {
		return redPigAppPushUserMapper.selectObjByProperty(maps);
	}


	public List<AppPushUser> queryPages(Map<String, Object> params) {
		return redPigAppPushUserMapper.queryPages(params);
	}


	public List<AppPushUser> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigAppPushUserMapper.queryPageListWithNoRelations(param);
	}


	public List<AppPushUser> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigAppPushUserMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private AppPushUserMapper redPigAppPushUserMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigAppPushUserMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(AppPushUser obj) {
		redPigAppPushUserMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(AppPushUser obj) {
		redPigAppPushUserMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigAppPushUserMapper.deleteById(id);
	}


	public AppPushUser selectByPrimaryKey(Long id) {
		return redPigAppPushUserMapper.selectByPrimaryKey(id);
	}


	public List<AppPushUser> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<AppPushUser> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigAppPushUserMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
