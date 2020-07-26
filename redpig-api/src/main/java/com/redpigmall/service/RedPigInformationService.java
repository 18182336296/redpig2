package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.Information;
import com.redpigmall.dao.InformationMapper;
import com.redpigmall.service.RedPigInformationService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigInformationService extends BaseService<Information>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Information> objs) {
		if (objs != null && objs.size() > 0) {
			redPigInformationMapper.batchDelete(objs);
		}
	}


	public Information getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Information> objs = redPigInformationMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Information> selectObjByProperty(Map<String, Object> maps) {
		return redPigInformationMapper.selectObjByProperty(maps);
	}


	public List<Information> queryPages(Map<String, Object> params) {
		return redPigInformationMapper.queryPages(params);
	}


	public List<Information> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigInformationMapper.queryPageListWithNoRelations(param);
	}


	public List<Information> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigInformationMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private InformationMapper redPigInformationMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigInformationMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Information obj) {
		redPigInformationMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Information obj) {
		redPigInformationMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigInformationMapper.deleteById(id);
	}


	public Information selectByPrimaryKey(Long id) {
		return redPigInformationMapper.selectByPrimaryKey(id);
	}


	public List<Information> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Information> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigInformationMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
