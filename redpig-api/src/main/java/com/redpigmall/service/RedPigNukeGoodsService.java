package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.NukeGoods;
import com.redpigmall.dao.NukeGoodsMapper;
import com.redpigmall.service.RedPigNukeGoodsService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigNukeGoodsService extends BaseService<NukeGoods>  {

	@Transactional(readOnly = false)
	public void batchDelObjs(List<NukeGoods> objs) {
		if (objs != null && objs.size() > 0) {
			redPigNukeGoodsMapper.batchDelete(objs);
		}
	}

	public NukeGoods getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<NukeGoods> objs = redPigNukeGoodsMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<NukeGoods> selectObjByProperty(Map<String, Object> maps) {
		return redPigNukeGoodsMapper.selectObjByProperty(maps);
	}


	public List<NukeGoods> queryPages(Map<String, Object> params) {
		return redPigNukeGoodsMapper.queryPages(params);
	}


	public List<NukeGoods> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigNukeGoodsMapper.queryPageListWithNoRelations(param);
	}


	public List<NukeGoods> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigNukeGoodsMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private NukeGoodsMapper redPigNukeGoodsMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigNukeGoodsMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(NukeGoods obj) {
		redPigNukeGoodsMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(NukeGoods obj) {
		redPigNukeGoodsMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigNukeGoodsMapper.deleteById(id);
	}


	public NukeGoods selectByPrimaryKey(Long id) {
		return redPigNukeGoodsMapper.selectByPrimaryKey(id);
	}


	public List<NukeGoods> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<NukeGoods> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigNukeGoodsMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<NukeGoods> queryPageListOrderByGgSelledCount(Map<String, Object> params) {
		List<NukeGoods> ng = this.redPigNukeGoodsMapper.queryPageListOrderByGgSelledCount(params);
		if (ng == null) {
			return Lists.newArrayList();
		}
		return ng;
	}

}
