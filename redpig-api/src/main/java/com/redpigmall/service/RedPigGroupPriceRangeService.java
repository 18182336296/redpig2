package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.domain.GroupPriceRange;
import com.redpigmall.dao.GroupPriceRangeMapper;
import com.redpigmall.service.RedPigGroupPriceRangeService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGroupPriceRangeService extends BaseService<GroupPriceRange> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GroupPriceRange> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGroupPriceRangeMapper.batchDelete(objs);
		}
	}


	public GroupPriceRange getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GroupPriceRange> objs = redPigGroupPriceRangeMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GroupPriceRange> selectObjByProperty(Map<String, Object> maps) {
		return redPigGroupPriceRangeMapper.selectObjByProperty(maps);
	}


	public List<GroupPriceRange> queryPages(Map<String, Object> params) {
		return redPigGroupPriceRangeMapper.queryPages(params);
	}


	public List<GroupPriceRange> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGroupPriceRangeMapper.queryPageListWithNoRelations(param);
	}


	public List<GroupPriceRange> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGroupPriceRangeMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private GroupPriceRangeMapper redPigGroupPriceRangeMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		if (RedPigCommonUtil.isNotNull(ids)) {
			redPigGroupPriceRangeMapper.batchDeleteByIds(ids);
		}
	}


	@Transactional(readOnly = false)
	public void saveEntity(GroupPriceRange obj) {
		redPigGroupPriceRangeMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GroupPriceRange obj) {
		redPigGroupPriceRangeMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGroupPriceRangeMapper.deleteById(id);
	}


	public GroupPriceRange selectByPrimaryKey(Long id) {
		return redPigGroupPriceRangeMapper.selectByPrimaryKey(id);
	}


	public List<GroupPriceRange> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GroupPriceRange> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGroupPriceRangeMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
