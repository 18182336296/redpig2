package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.LimitSelling;
import com.redpigmall.dao.LimitSellingMapper;
import com.redpigmall.service.RedPigLimitSellingService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigLimitSellingService extends BaseService<LimitSelling>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<LimitSelling> objs) {
		if (objs != null && objs.size() > 0) {
			redPigLimitSellingMapper.batchDelete(objs);
		}
	}


	public LimitSelling getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<LimitSelling> objs = redPigLimitSellingMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<LimitSelling> selectObjByProperty(Map<String, Object> maps) {
		return redPigLimitSellingMapper.selectObjByProperty(maps);
	}


	public List<LimitSelling> queryPages(Map<String, Object> params) {
		return redPigLimitSellingMapper.queryPages(params);
	}


	public List<LimitSelling> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigLimitSellingMapper.queryPageListWithNoRelations(param);
	}


	public List<LimitSelling> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigLimitSellingMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private LimitSellingMapper redPigLimitSellingMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigLimitSellingMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(LimitSelling obj) {
		redPigLimitSellingMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(LimitSelling obj) {
		redPigLimitSellingMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigLimitSellingMapper.deleteById(id);
	}


	public LimitSelling selectByPrimaryKey(Long id) {
		return redPigLimitSellingMapper.selectByPrimaryKey(id);
	}


	public List<LimitSelling> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		List<LimitSelling> LimitSellings = super.queryPageList(params, begin, max);
		if (LimitSellings == null) {
			return Lists.newArrayList();
		}
		return LimitSellings;
	}


	public List<LimitSelling> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigLimitSellingMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}

}
