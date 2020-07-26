package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.Navigation;
import com.redpigmall.dao.NavigationMapper;
import com.redpigmall.service.RedPigNavigationService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigNavigationService extends BaseService<Navigation>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Navigation> objs) {
		if (objs != null && objs.size() > 0) {
			redPigNavigationMapper.batchDelete(objs);
		}
	}


	public Navigation getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Navigation> objs = redPigNavigationMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Navigation> selectObjByProperty(Map<String, Object> maps) {
		return redPigNavigationMapper.selectObjByProperty(maps);
	}


	public List<Navigation> queryPages(Map<String, Object> params) {
		return redPigNavigationMapper.queryPages(params);
	}


	public List<Navigation> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigNavigationMapper.queryPageListWithNoRelations(param);
	}


	public List<Navigation> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigNavigationMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private NavigationMapper redPigNavigationMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigNavigationMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Navigation obj) {
		redPigNavigationMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Navigation obj) {
		redPigNavigationMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigNavigationMapper.deleteById(id);
	}


	public Navigation selectByPrimaryKey(Long id) {
		return redPigNavigationMapper.selectByPrimaryKey(id);
	}


	public List<Navigation> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Navigation> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigNavigationMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
