package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.Favorite;
import com.redpigmall.dao.FavoriteMapper;
import com.redpigmall.service.RedPigFavoriteService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigFavoriteService extends BaseService<Favorite>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Favorite> objs) {
		if (objs != null && objs.size() > 0) {
			redPigFavoriteMapper.batchDelete(objs);
		}
	}


	public Favorite getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Favorite> objs = redPigFavoriteMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Favorite> selectObjByProperty(Map<String, Object> maps) {
		return redPigFavoriteMapper.selectObjByProperty(maps);
	}


	public List<Favorite> queryPages(Map<String, Object> params) {
		return redPigFavoriteMapper.queryPages(params);
	}


	public List<Favorite> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigFavoriteMapper.queryPageListWithNoRelations(param);
	}


	public List<Favorite> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigFavoriteMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private FavoriteMapper redPigFavoriteMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigFavoriteMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Favorite obj) {
		redPigFavoriteMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Favorite obj) {
		redPigFavoriteMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigFavoriteMapper.deleteById(id);
	}


	public Favorite selectByPrimaryKey(Long id) {
		return redPigFavoriteMapper.selectByPrimaryKey(id);
	}


	public List<Favorite> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Favorite> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigFavoriteMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	@Transactional(readOnly = false)
	public void delete(Favorite obj) {
		redPigFavoriteMapper.delete(obj);
	}

	@Transactional(readOnly = false)

	public void deleteByGoodsId(Long id) {
		redPigFavoriteMapper.deleteByGoodsId(id);
	}


	public List<Map<String, Object>> PersonaCollectionList(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return redPigFavoriteMapper.PersonaCollectionList(params);
	}


	public List<Map<String, Object>> storeCollectionList(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return redPigFavoriteMapper.storeCollectionList(params);
	}
}
