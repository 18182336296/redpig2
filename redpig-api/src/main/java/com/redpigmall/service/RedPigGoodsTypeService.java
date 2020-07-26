package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.GoodsType;
import com.redpigmall.dao.GoodsTypeMapper;
import com.redpigmall.service.RedPigGoodsTypeService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGoodsTypeService extends BaseService<GoodsType>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GoodsType> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGoodsTypeMapper.batchDelete(objs);
		}
	}


	public GoodsType getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GoodsType> objs = redPigGoodsTypeMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GoodsType> selectObjByProperty(Map<String, Object> maps) {
		return redPigGoodsTypeMapper.selectObjByProperty(maps);
	}


	public List<GoodsType> queryPages(Map<String, Object> params) {
		return redPigGoodsTypeMapper.queryPages(params);
	}


	public List<GoodsType> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGoodsTypeMapper.queryPageListWithNoRelations(param);
	}


	public List<GoodsType> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGoodsTypeMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private GoodsTypeMapper redPigGoodsTypeMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGoodsTypeMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GoodsType obj) {
		redPigGoodsTypeMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GoodsType obj) {
		redPigGoodsTypeMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGoodsTypeMapper.deleteById(id);
	}


	public GoodsType selectByPrimaryKey(Long id) {
		return redPigGoodsTypeMapper.selectByPrimaryKey(id);
	}


	public List<GoodsType> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GoodsType> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGoodsTypeMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	@Transactional(readOnly = false)
	public void update(GoodsType type) {
		redPigGoodsTypeMapper.update(type);
	}


	@Transactional(readOnly = false)
	public void save(GoodsType goodsType) {
		redPigGoodsTypeMapper.save(goodsType);
	}

	@Transactional(readOnly = false)

	public void deleteGoodsTypeAndGoodsBrand(String[] ids) {
		List<Long> listIds = Lists.newArrayList();
		for (String id : ids) {
			listIds.add(CommUtil.null2Long(id));
		}
		if (listIds != null && listIds.size() > 0) {
			redPigGoodsTypeMapper.deleteGoodsTypeAndGoodsBrand(listIds);
		}
	}


	@Transactional(readOnly = false)
	public void delete(String[] ids) {
		List<Long> listIds = Lists.newArrayList();
		for (String id : ids) {
			listIds.add(CommUtil.null2Long(id));
		}
		if (listIds != null && listIds.size() > 0) {
			redPigGoodsTypeMapper.delete(listIds);
		}
	}
}
