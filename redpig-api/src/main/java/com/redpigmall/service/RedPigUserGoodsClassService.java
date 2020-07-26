package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.UserGoodsClass;
import com.redpigmall.dao.UserGoodsClassMapper;
import com.redpigmall.service.RedPigUserGoodsClassService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigUserGoodsClassService extends BaseService<UserGoodsClass> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<UserGoodsClass> objs) {
		if (objs != null && objs.size() > 0) {
			redPigUserGoodsClassMapper.batchDelete(objs);
		}
	}


	public UserGoodsClass getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<UserGoodsClass> objs = redPigUserGoodsClassMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<UserGoodsClass> selectObjByProperty(Map<String, Object> maps) {
		return redPigUserGoodsClassMapper.selectObjByProperty(maps);
	}


	public List<UserGoodsClass> queryPages(Map<String, Object> params) {
		return redPigUserGoodsClassMapper.queryPages(params);
	}


	public List<UserGoodsClass> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigUserGoodsClassMapper.queryPageListWithNoRelations(param);
	}


	public List<UserGoodsClass> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigUserGoodsClassMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private UserGoodsClassMapper redPigUserGoodsClassMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigUserGoodsClassMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(UserGoodsClass obj) {
		redPigUserGoodsClassMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(UserGoodsClass obj) {
		redPigUserGoodsClassMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigUserGoodsClassMapper.deleteById(id);
	}


	public UserGoodsClass selectByPrimaryKey(Long id) {
		return redPigUserGoodsClassMapper.selectByPrimaryKey(id);
	}


	public List<UserGoodsClass> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<UserGoodsClass> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigUserGoodsClassMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<UserGoodsClass> queryPageListByParentIsNull(Map<String, Object> params, int begin, int max) {

		return redPigUserGoodsClassMapper.queryPageListByParentIsNull(params);
	}
}
