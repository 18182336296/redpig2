package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.TransArea;
import com.redpigmall.dao.TransAreaMapper;
import com.redpigmall.service.RedPigTransAreaService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigTransAreaService extends BaseService<TransArea> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<TransArea> objs) {
		if (objs != null && objs.size() > 0) {
			redPigTransAreaMapper.batchDelete(objs);
		}
	}


	public TransArea getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<TransArea> objs = redPigTransAreaMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<TransArea> selectObjByProperty(Map<String, Object> maps) {
		return redPigTransAreaMapper.selectObjByProperty(maps);
	}


	public List<TransArea> queryPages(Map<String, Object> params) {
		return redPigTransAreaMapper.queryPages(params);
	}


	public List<TransArea> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigTransAreaMapper.queryPageListWithNoRelations(param);
	}


	public List<TransArea> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigTransAreaMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private TransAreaMapper redPigTransAreaMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigTransAreaMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(TransArea obj) {
		redPigTransAreaMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(TransArea obj) {
		redPigTransAreaMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigTransAreaMapper.deleteById(id);
	}


	public TransArea selectByPrimaryKey(Long id) {
		return redPigTransAreaMapper.selectByPrimaryKey(id);
	}


	public List<TransArea> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<TransArea> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigTransAreaMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<TransArea> queryPageListByParentIsNull(Map<String, Object> params, int begin, int max) {

		return redPigTransAreaMapper.queryPageListByParentIsNull(params);
	}


	@Transactional(readOnly = false)
	public void update(TransArea area) {
		redPigTransAreaMapper.update(area);
	}


	@Transactional(readOnly = false)
	public void save(TransArea area) {
		redPigTransAreaMapper.save(area);
	}

	@Transactional(readOnly = false)

	public void deleteByIds(List<Long> listIds) {
		if (listIds != null && listIds.size() > 0)
			redPigTransAreaMapper.deleteByIds(listIds);
	}
}
