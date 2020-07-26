package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.SystemTip;
import com.redpigmall.dao.SystemTipMapper;
import com.redpigmall.service.RedPigSystemTipService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigSystemTipService extends BaseService<SystemTip>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<SystemTip> objs) {
		if (objs != null && objs.size() > 0) {
			redPigSystemTipMapper.batchDelete(objs);
		}
	}


	public SystemTip getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<SystemTip> objs = redPigSystemTipMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<SystemTip> selectObjByProperty(Map<String, Object> maps) {
		return redPigSystemTipMapper.selectObjByProperty(maps);
	}


	public List<SystemTip> queryPages(Map<String, Object> params) {
		return redPigSystemTipMapper.queryPages(params);
	}


	public List<SystemTip> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigSystemTipMapper.queryPageListWithNoRelations(param);
	}


	public List<SystemTip> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigSystemTipMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private SystemTipMapper redPigSystemTipMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigSystemTipMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(SystemTip obj) {
		redPigSystemTipMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(SystemTip obj) {
		redPigSystemTipMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigSystemTipMapper.deleteById(id);
	}


	public SystemTip selectByPrimaryKey(Long id) {
		return redPigSystemTipMapper.selectByPrimaryKey(id);
	}


	public List<SystemTip> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<SystemTip> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigSystemTipMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
