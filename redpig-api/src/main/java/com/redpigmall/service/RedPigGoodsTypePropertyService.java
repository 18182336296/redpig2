package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.GoodsTypeProperty;
import com.redpigmall.dao.GoodsTypePropertyMapper;
import com.redpigmall.service.RedPigGoodsTypePropertyService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGoodsTypePropertyService extends BaseService<GoodsTypeProperty> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GoodsTypeProperty> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGoodsTypePropertyMapper.batchDelete(objs);
		}
	}


	public GoodsTypeProperty getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GoodsTypeProperty> objs = redPigGoodsTypePropertyMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GoodsTypeProperty> selectObjByProperty(Map<String, Object> maps) {
		return redPigGoodsTypePropertyMapper.selectObjByProperty(maps);
	}


	public List<GoodsTypeProperty> queryPages(Map<String, Object> params) {
		return redPigGoodsTypePropertyMapper.queryPages(params);
	}


	public List<GoodsTypeProperty> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGoodsTypePropertyMapper.queryPageListWithNoRelations(param);
	}


	public List<GoodsTypeProperty> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGoodsTypePropertyMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private GoodsTypePropertyMapper redPigGoodsTypePropertyMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGoodsTypePropertyMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GoodsTypeProperty obj) {
		redPigGoodsTypePropertyMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GoodsTypeProperty obj) {
		redPigGoodsTypePropertyMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGoodsTypePropertyMapper.deleteById(id);
	}


	public GoodsTypeProperty selectByPrimaryKey(Long id) {
		return redPigGoodsTypePropertyMapper.selectByPrimaryKey(id);
	}


	public List<GoodsTypeProperty> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GoodsTypeProperty> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGoodsTypePropertyMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	@Transactional(readOnly = false)
	public void save(GoodsTypeProperty gtp) {
		redPigGoodsTypePropertyMapper.save(gtp);
	}


	@Transactional(readOnly = false)
	public void update(GoodsTypeProperty gtp) {
		redPigGoodsTypePropertyMapper.update(gtp);
	}


	@Transactional(readOnly = false)
	public int delete(Long id) {
		return redPigGoodsTypePropertyMapper.delete(id);
	}
}
