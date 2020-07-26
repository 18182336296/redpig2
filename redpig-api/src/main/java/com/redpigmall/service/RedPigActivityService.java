package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.Activity;
import com.redpigmall.dao.ActivityMapper;
import com.redpigmall.service.RedPigActivityService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigActivityService extends BaseService<Activity>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Activity> objs) {
		if (objs != null && objs.size() > 0) {
			redPigActivityMapper.batchDelete(objs);
		}
	}


	public Activity getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Activity> objs = redPigActivityMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Activity> selectObjByProperty(Map<String, Object> maps) {
		return redPigActivityMapper.selectObjByProperty(maps);
	}


	public List<Activity> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigActivityMapper.queryPageListWithNoRelations(param);
	}


	public List<Activity> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigActivityMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}

	@Autowired
	private ActivityMapper redPigActivityMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigActivityMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Activity obj) {
		redPigActivityMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Activity obj) {
		redPigActivityMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigActivityMapper.deleteById(id);
	}


	public Activity selectByPrimaryKey(Long id) {
		return redPigActivityMapper.selectByPrimaryKey(id);
	}


	public List<Activity> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Activity> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigActivityMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<Activity> queryPages(Map<String, Object> params) {
		return redPigActivityMapper.queryPages(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}
}
