package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.GroupFloor;
import com.redpigmall.dao.GroupFloorMapper;
import com.redpigmall.service.RedPigGroupFloorService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGroupFloorService extends BaseService<GroupFloor>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GroupFloor> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGroupFloorMapper.batchDelete(objs);
		}
	}


	public GroupFloor getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GroupFloor> objs = redPigGroupFloorMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GroupFloor> selectObjByProperty(Map<String, Object> maps) {
		return redPigGroupFloorMapper.selectObjByProperty(maps);
	}


	public List<GroupFloor> queryPages(Map<String, Object> params) {
		return redPigGroupFloorMapper.queryPages(params);
	}


	public List<GroupFloor> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGroupFloorMapper.queryPageListWithNoRelations(param);
	}


	public List<GroupFloor> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGroupFloorMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private GroupFloorMapper redPigGroupFloorMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGroupFloorMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GroupFloor obj) {
		redPigGroupFloorMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GroupFloor obj) {
		redPigGroupFloorMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGroupFloorMapper.deleteById(id);
	}


	public GroupFloor selectByPrimaryKey(Long id) {
		return redPigGroupFloorMapper.selectByPrimaryKey(id);
	}


	public List<GroupFloor> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GroupFloor> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGroupFloorMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}

}
