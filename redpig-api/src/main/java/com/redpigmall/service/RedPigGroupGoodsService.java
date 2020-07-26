package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.GroupGoods;
import com.redpigmall.dao.GroupGoodsMapper;
import com.redpigmall.service.RedPigGroupGoodsService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGroupGoodsService extends BaseService<GroupGoods>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GroupGoods> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGroupGoodsMapper.batchDelete(objs);
		}
	}


	public GroupGoods getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GroupGoods> objs = redPigGroupGoodsMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GroupGoods> selectObjByProperty(Map<String, Object> maps) {
		return redPigGroupGoodsMapper.selectObjByProperty(maps);
	}


	public List<GroupGoods> queryPages(Map<String, Object> params) {
		return redPigGroupGoodsMapper.queryPages(params);
	}


	public List<GroupGoods> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGroupGoodsMapper.queryPageListWithNoRelations(param);
	}


	public List<GroupGoods> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGroupGoodsMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private GroupGoodsMapper redPigGroupGoodsMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGroupGoodsMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GroupGoods obj) {
		redPigGroupGoodsMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GroupGoods obj) {
		redPigGroupGoodsMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGroupGoodsMapper.deleteById(id);
	}


	public GroupGoods selectByPrimaryKey(Long id) {
		return redPigGroupGoodsMapper.selectByPrimaryKey(id);
	}


	public List<GroupGoods> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GroupGoods> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGroupGoodsMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<GroupGoods> queryPageListOrderByGgSelledCount(Map<String, Object> params) {
		List<GroupGoods> gg = this.redPigGroupGoodsMapper.queryPageListOrderByGgSelledCount(params);
		if (gg == null) {
			return Lists.newArrayList();
		}
		return gg;
	}

}
