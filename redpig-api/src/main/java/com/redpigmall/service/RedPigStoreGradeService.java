package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.StoreGrade;
import com.redpigmall.dao.StoreGradeMapper;
import com.redpigmall.service.RedPigStoreGradeService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigStoreGradeService extends BaseService<StoreGrade> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<StoreGrade> objs) {
		if (objs != null && objs.size() > 0) {
			redPigStoreGradeMapper.batchDelete(objs);
		}
	}


	public StoreGrade getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<StoreGrade> objs = redPigStoreGradeMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<StoreGrade> selectObjByProperty(Map<String, Object> maps) {
		return redPigStoreGradeMapper.selectObjByProperty(maps);
	}


	public List<StoreGrade> queryPages(Map<String, Object> params) {
		return redPigStoreGradeMapper.queryPages(params);
	}


	public List<StoreGrade> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigStoreGradeMapper.queryPageListWithNoRelations(param);
	}


	public List<StoreGrade> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigStoreGradeMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private StoreGradeMapper redPigStoreGradeMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigStoreGradeMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(StoreGrade obj) {
		redPigStoreGradeMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(StoreGrade obj) {
		redPigStoreGradeMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigStoreGradeMapper.deleteById(id);
	}


	public StoreGrade selectByPrimaryKey(Long id) {
		return redPigStoreGradeMapper.selectByPrimaryKey(id);
	}


	public List<StoreGrade> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<StoreGrade> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigStoreGradeMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
