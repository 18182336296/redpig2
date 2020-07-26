package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.ChannelFloor;
import com.redpigmall.dao.ChannelFloorMapper;
import com.redpigmall.service.RedPigChannelFloorService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigChannelFloorService extends BaseService<ChannelFloor> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<ChannelFloor> objs) {
		if (objs != null && objs.size() > 0) {
			redPigChannelFloorMapper.batchDelete(objs);
		}
	}


	public ChannelFloor getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<ChannelFloor> objs = redPigChannelFloorMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<ChannelFloor> selectObjByProperty(Map<String, Object> maps) {
		return redPigChannelFloorMapper.selectObjByProperty(maps);
	}


	public List<ChannelFloor> queryPages(Map<String, Object> params) {
		return redPigChannelFloorMapper.queryPages(params);
	}


	public List<ChannelFloor> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigChannelFloorMapper.queryPageListWithNoRelations(param);
	}


	public List<ChannelFloor> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigChannelFloorMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private ChannelFloorMapper redPigChannelFloorMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigChannelFloorMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(ChannelFloor obj) {
		redPigChannelFloorMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(ChannelFloor obj) {
		redPigChannelFloorMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigChannelFloorMapper.deleteById(id);
	}


	public ChannelFloor selectByPrimaryKey(Long id) {
		return redPigChannelFloorMapper.selectByPrimaryKey(id);
	}


	public List<ChannelFloor> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<ChannelFloor> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigChannelFloorMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
