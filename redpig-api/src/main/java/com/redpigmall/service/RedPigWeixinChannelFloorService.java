package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.WeixinChannelFloor;
import com.redpigmall.dao.WeixinChannelFloorMapper;
import com.redpigmall.service.RedPigWeixinChannelFloorService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigWeixinChannelFloorService extends BaseService<WeixinChannelFloor> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<WeixinChannelFloor> objs) {
		if (objs != null && objs.size() > 0) {
			redPigWeixinChannelFloorMapper.batchDelete(objs);
		}
	}


	public WeixinChannelFloor getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<WeixinChannelFloor> objs = redPigWeixinChannelFloorMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<WeixinChannelFloor> selectObjByProperty(Map<String, Object> maps) {
		return redPigWeixinChannelFloorMapper.selectObjByProperty(maps);
	}


	public List<WeixinChannelFloor> queryPages(Map<String, Object> params) {
		return redPigWeixinChannelFloorMapper.queryPages(params);
	}


	public List<WeixinChannelFloor> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigWeixinChannelFloorMapper.queryPageListWithNoRelations(param);
	}


	public List<WeixinChannelFloor> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigWeixinChannelFloorMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private WeixinChannelFloorMapper redPigWeixinChannelFloorMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigWeixinChannelFloorMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(WeixinChannelFloor obj) {
		redPigWeixinChannelFloorMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(WeixinChannelFloor obj) {
		redPigWeixinChannelFloorMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigWeixinChannelFloorMapper.deleteById(id);
	}


	public WeixinChannelFloor selectByPrimaryKey(Long id) {
		return redPigWeixinChannelFloorMapper.selectByPrimaryKey(id);
	}


	public List<WeixinChannelFloor> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<WeixinChannelFloor> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigWeixinChannelFloorMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
