package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.domain.GroupIndex;
import com.redpigmall.dao.GroupIndexMapper;
import com.redpigmall.service.RedPigGroupIndexService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGroupIndexService extends BaseService<GroupIndex> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GroupIndex> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGroupIndexMapper.batchDelete(objs);
		}
	}


	public GroupIndex getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GroupIndex> objs = redPigGroupIndexMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GroupIndex> selectObjByProperty(Map<String, Object> maps) {
		return redPigGroupIndexMapper.selectObjByProperty(maps);
	}


	public List<GroupIndex> queryPages(Map<String, Object> params) {
		return redPigGroupIndexMapper.queryPages(params);
	}


	public List<GroupIndex> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGroupIndexMapper.queryPageListWithNoRelations(param);
	}


	public List<GroupIndex> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGroupIndexMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private GroupIndexMapper redPigGroupIndexMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		if (RedPigCommonUtil.isNotNull(ids)) {
			redPigGroupIndexMapper.batchDeleteByIds(ids);
		}
	}


	@Transactional(readOnly = false)
	public void saveEntity(GroupIndex obj) {
		redPigGroupIndexMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GroupIndex obj) {
		redPigGroupIndexMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGroupIndexMapper.deleteById(id);
	}


	public GroupIndex selectByPrimaryKey(Long id) {
		return redPigGroupIndexMapper.selectByPrimaryKey(id);
	}


	public List<GroupIndex> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GroupIndex> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGroupIndexMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
