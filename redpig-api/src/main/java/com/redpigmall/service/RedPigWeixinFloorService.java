package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.WeixinFloor;
import com.redpigmall.dao.WeixinFloorMapper;
import com.redpigmall.service.RedPigWeixinFloorService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigWeixinFloorService extends BaseService<WeixinFloor> {

	@Autowired
	private WeixinFloorMapper redPigWeixinFloorMapper;


	@Transactional(readOnly = false)
	public void batchDelObjs(List<WeixinFloor> objs) {
		if (objs != null && objs.size() > 0) {
			redPigWeixinFloorMapper.batchDelete(objs);
		}
	}


	public WeixinFloor getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<WeixinFloor> objs = redPigWeixinFloorMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<WeixinFloor> selectObjByProperty(Map<String, Object> maps) {
		return redPigWeixinFloorMapper.selectObjByProperty(maps);
	}


	public List<WeixinFloor> queryPages(Map<String, Object> params) {
		return redPigWeixinFloorMapper.queryPages(params);
	}


	public List<WeixinFloor> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigWeixinFloorMapper.queryPageListWithNoRelations(param);
	}


	public List<WeixinFloor> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigWeixinFloorMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigWeixinFloorMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void save(WeixinFloor obj) {
		redPigWeixinFloorMapper.save(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(WeixinFloor obj) {
		redPigWeixinFloorMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigWeixinFloorMapper.deleteById(id);
	}


	public WeixinFloor selectByPrimaryKey(Long id) {
		return redPigWeixinFloorMapper.selectByPrimaryKey(id);
	}


	public List<WeixinFloor> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<WeixinFloor> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigWeixinFloorMapper.selectCount(params);
		if (c == null) {
			return 0;
		}
		return c;
	}

}
