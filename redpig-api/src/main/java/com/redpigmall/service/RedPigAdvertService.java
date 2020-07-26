package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.Advert;
import com.redpigmall.dao.AdvertMapper;
import com.redpigmall.service.RedPigAdvertService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigAdvertService extends BaseService<Advert>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Advert> objs) {
		if (objs != null && objs.size() > 0) {
			redPigAdvertMapper.batchDelete(objs);
		}
	}


	public Advert getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Advert> objs = redPigAdvertMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Advert> selectObjByProperty(Map<String, Object> maps) {
		return redPigAdvertMapper.selectObjByProperty(maps);
	}


	public List<Advert> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigAdvertMapper.queryPageListWithNoRelations(param);
	}


	public List<Advert> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigAdvertMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}

	@Autowired
	private AdvertMapper redPigAdvertMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigAdvertMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Advert obj) {
		redPigAdvertMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Advert obj) {
		redPigAdvertMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigAdvertMapper.deleteById(id);
	}


	public Advert selectByPrimaryKey(Long id) {
		return redPigAdvertMapper.selectByPrimaryKey(id);
	}


	public List<Advert> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Advert> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigAdvertMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<Advert> queryPages(Map<String, Object> params) {

		return redPigAdvertMapper.queryPages(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}


	@SuppressWarnings("rawtypes")
	public List<Map> getAdvertImg(Map<String, Object> map) {
		return redPigAdvertMapper.getAdvertImg(map);
	}

}
