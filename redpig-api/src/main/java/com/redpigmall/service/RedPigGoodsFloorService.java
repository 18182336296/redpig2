package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.GoodsFloor;
import com.redpigmall.dao.GoodsFloorMapper;
import com.redpigmall.service.RedPigGoodsFloorService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGoodsFloorService extends BaseService<GoodsFloor>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GoodsFloor> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGoodsFloorMapper.batchDelete(objs);
		}
	}


	public GoodsFloor getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GoodsFloor> objs = redPigGoodsFloorMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GoodsFloor> selectObjByProperty(Map<String, Object> maps) {
		return redPigGoodsFloorMapper.selectObjByProperty(maps);
	}


	public List<GoodsFloor> queryPages(Map<String, Object> params) {
		return redPigGoodsFloorMapper.queryPages(params);
	}


	public List<GoodsFloor> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGoodsFloorMapper.queryPageListWithNoRelations(param);
	}


	public List<GoodsFloor> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGoodsFloorMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private GoodsFloorMapper redPigGoodsFloorMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGoodsFloorMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GoodsFloor obj) {
		redPigGoodsFloorMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GoodsFloor obj) {
		redPigGoodsFloorMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGoodsFloorMapper.deleteById(id);
	}


	public GoodsFloor selectByPrimaryKey(Long id) {
		return redPigGoodsFloorMapper.selectByPrimaryKey(id);
	}


	public List<GoodsFloor> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		List<GoodsFloor> gfs = super.queryPageList(params, begin, max);

		if (gfs == null) {
			return Lists.newArrayList();
		}
		return gfs;
	}


	public List<GoodsFloor> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGoodsFloorMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<GoodsFloor> queryPageListByParentIsNull(Map<String, Object> params, int begin, int max) {
		return redPigGoodsFloorMapper.queryPageListByParentIsNull(params);
	}


	public List<GoodsFloor> queryAll() {
		List<GoodsFloor> gfs = redPigGoodsFloorMapper.queryAll();
		if (gfs == null) {
			return Lists.newArrayList();
		}
		return gfs;
	}


	@Transactional(readOnly = false)
	public void save(GoodsFloor goodsfloor) {
		redPigGoodsFloorMapper.save(goodsfloor);
	}


	@Transactional(readOnly = false)
	public void update(GoodsFloor goodsfloor) {
		redPigGoodsFloorMapper.update(goodsfloor);
	}


	@Transactional(readOnly = false)
	public void delete(Long id) {
		redPigGoodsFloorMapper.delete(id);
	}
}
