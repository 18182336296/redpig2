package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.GoodsSpecProperty;
import com.redpigmall.dao.GoodsSpecPropertyMapper;
import com.redpigmall.service.RedPigGoodsSpecPropertyService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGoodsSpecPropertyService extends BaseService<GoodsSpecProperty>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GoodsSpecProperty> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGoodsSpecPropertyMapper.batchDelete(objs);
		}
	}


	public GoodsSpecProperty getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GoodsSpecProperty> objs = redPigGoodsSpecPropertyMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GoodsSpecProperty> selectObjByProperty(Map<String, Object> maps) {
		return redPigGoodsSpecPropertyMapper.selectObjByProperty(maps);
	}


	public List<GoodsSpecProperty> queryPages(Map<String, Object> params) {
		return redPigGoodsSpecPropertyMapper.queryPages(params);
	}


	public List<GoodsSpecProperty> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGoodsSpecPropertyMapper.queryPageListWithNoRelations(param);
	}


	public List<GoodsSpecProperty> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGoodsSpecPropertyMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private GoodsSpecPropertyMapper redPigGoodsSpecPropertyMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGoodsSpecPropertyMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GoodsSpecProperty obj) {
		redPigGoodsSpecPropertyMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GoodsSpecProperty obj) {
		redPigGoodsSpecPropertyMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGoodsSpecPropertyMapper.deleteById(id);
	}


	public GoodsSpecProperty selectByPrimaryKey(Long id) {
		return redPigGoodsSpecPropertyMapper.selectByPrimaryKey(id);
	}


	public List<GoodsSpecProperty> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GoodsSpecProperty> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGoodsSpecPropertyMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	@Transactional(readOnly = false)
	public boolean save(GoodsSpecProperty property) {
		int ret = redPigGoodsSpecPropertyMapper.save(property);
		if (ret == 1) {
			return true;
		}
		return false;
	}


	@Transactional(readOnly = false)
	public void update(GoodsSpecProperty property) {
		redPigGoodsSpecPropertyMapper.update(property);
	}


	@Transactional(readOnly = false)
	public int delete(Long id) {
		return redPigGoodsSpecPropertyMapper.delete(id);
	}

	@Transactional(readOnly = false)

	public void deleteGoodsCartAndGoodsSpecProperty(Long id, List<GoodsSpecProperty> gsps) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("goodsCartId", id);
		params.put("gsps", gsps);
		if (id != null && gsps != null && gsps.size() > 0) {
			redPigGoodsSpecPropertyMapper.deleteGoodsCartAndGoodsSpecProperty(params);
		}
	}
}
