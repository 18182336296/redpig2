package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.ShowClass;
import com.redpigmall.dao.ShowClassMapper;
import com.redpigmall.service.RedPigShowClassService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigShowClassService extends BaseService<ShowClass>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<ShowClass> objs) {
		if (objs != null && objs.size() > 0) {
			redPigShowClassMapper.batchDelete(objs);
		}
	}


	public ShowClass getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<ShowClass> objs = redPigShowClassMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<ShowClass> selectObjByProperty(Map<String, Object> maps) {
		return redPigShowClassMapper.selectObjByProperty(maps);
	}


	public List<ShowClass> queryPages(Map<String, Object> params) {
		return redPigShowClassMapper.queryPages(params);
	}


	public List<ShowClass> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigShowClassMapper.queryPageListWithNoRelations(param);
	}


	public List<ShowClass> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigShowClassMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private ShowClassMapper redPigShowClassMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigShowClassMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(ShowClass obj) {
		redPigShowClassMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(ShowClass obj) {
		redPigShowClassMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigShowClassMapper.deleteById(id);
	}


	public ShowClass selectByPrimaryKey(Long id) {
		return redPigShowClassMapper.selectByPrimaryKey(id);
	}


	public List<ShowClass> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<ShowClass> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigShowClassMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<ShowClass> queryPageListByParentIsNull(Map<String, Object> params, int begin, int max) {

		return redPigShowClassMapper.queryPageListByParentIsNull(params);
	}


	public List<ShowClass> queryListWithNoRelations(Long id) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("parent_id", id);
		params.put("deleteStatus", "0");
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		// TODO Auto-generated method stub
		return redPigShowClassMapper.queryListWithNoRelations(params);
	}


	public List<Map<String, Object>> queryListWithNoRelations4fir(Long id) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("parent_id", id);
		params.put("deleteStatus", "0");
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		return redPigShowClassMapper.queryListWithNoRelations4fir(params);
	}

}
