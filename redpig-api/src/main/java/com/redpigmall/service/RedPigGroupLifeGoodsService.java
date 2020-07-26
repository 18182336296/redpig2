package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.GroupLifeGoods;
import com.redpigmall.dao.GroupLifeGoodsMapper;
import com.redpigmall.service.RedPigGroupLifeGoodsService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGroupLifeGoodsService extends BaseService<GroupLifeGoods> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GroupLifeGoods> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGroupLifeGoodsMapper.batchDelete(objs);
		}
	}


	public GroupLifeGoods getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GroupLifeGoods> objs = redPigGroupLifeGoodsMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GroupLifeGoods> selectObjByProperty(Map<String, Object> maps) {
		return redPigGroupLifeGoodsMapper.selectObjByProperty(maps);
	}


	public List<GroupLifeGoods> queryPages(Map<String, Object> params) {
		return redPigGroupLifeGoodsMapper.queryPages(params);
	}


	public List<GroupLifeGoods> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGroupLifeGoodsMapper.queryPageListWithNoRelations(param);
	}


	public List<GroupLifeGoods> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGroupLifeGoodsMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private GroupLifeGoodsMapper redPigGroupLifeGoodsMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGroupLifeGoodsMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GroupLifeGoods obj) {
		redPigGroupLifeGoodsMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GroupLifeGoods obj) {
		redPigGroupLifeGoodsMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGroupLifeGoodsMapper.deleteById(id);
	}


	public GroupLifeGoods selectByPrimaryKey(Long id) {
		return redPigGroupLifeGoodsMapper.selectByPrimaryKey(id);
	}


	public List<GroupLifeGoods> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GroupLifeGoods> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGroupLifeGoodsMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}

}
