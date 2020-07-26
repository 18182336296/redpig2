package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.Res;
import com.redpigmall.dao.ResMapper;
import com.redpigmall.service.RedPigResService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigResService extends BaseService<Res>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Res> objs) {
		if (objs != null && objs.size() > 0) {
			redPigResMapper.batchDelete(objs);
		}
	}


	public Res getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Res> objs = redPigResMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Res> selectObjByProperty(Map<String, Object> maps) {
		return redPigResMapper.selectObjByProperty(maps);
	}


	public List<Res> queryPages(Map<String, Object> params) {
		return redPigResMapper.queryPages(params);
	}


	public List<Res> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigResMapper.queryPageListWithNoRelations(param);
	}


	public List<Res> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigResMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private ResMapper redPigResMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigResMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Res obj) {
		redPigResMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Res obj) {
		redPigResMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigResMapper.deleteById(id);
	}


	public Res selectByPrimaryKey(Long id) {
		return redPigResMapper.selectByPrimaryKey(id);
	}


	public List<Res> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Res> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigResMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<String> queryResLists(Long id) {
		
		return redPigResMapper.queryResLists(id);
	}
}
