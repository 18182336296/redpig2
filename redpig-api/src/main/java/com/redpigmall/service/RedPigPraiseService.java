package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.Praise;
import com.redpigmall.dao.PraiseMapper;
import com.redpigmall.service.RedPigPraiseService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigPraiseService extends BaseService<Praise>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Praise> objs) {
		if (objs != null && objs.size() > 0) {
			redPigPraiseMapper.batchDelete(objs);
		}
	}


	public Praise getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Praise> objs = redPigPraiseMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Praise> selectObjByProperty(Map<String, Object> maps) {
		return redPigPraiseMapper.selectObjByProperty(maps);
	}


	public List<Praise> queryPages(Map<String, Object> params) {
		return redPigPraiseMapper.queryPages(params);
	}


	public List<Praise> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigPraiseMapper.queryPageListWithNoRelations(param);
	}


	public List<Praise> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigPraiseMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private PraiseMapper redPigPraiseMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigPraiseMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Praise obj) {
		redPigPraiseMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Praise obj) {
		redPigPraiseMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigPraiseMapper.deleteById(id);
	}


	public Praise selectByPrimaryKey(Long id) {
		return redPigPraiseMapper.selectByPrimaryKey(id);
	}


	public List<Praise> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Praise> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigPraiseMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
