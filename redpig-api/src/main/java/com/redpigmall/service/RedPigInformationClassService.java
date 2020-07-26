package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.InformationClass;
import com.redpigmall.dao.InformationClassMapper;
import com.redpigmall.service.RedPigInformationClassService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigInformationClassService extends BaseService<InformationClass>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<InformationClass> objs) {
		if (objs != null && objs.size() > 0) {
			redPigInformationClassMapper.batchDelete(objs);
		}
	}


	public InformationClass getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<InformationClass> objs = redPigInformationClassMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<InformationClass> selectObjByProperty(Map<String, Object> maps) {
		return redPigInformationClassMapper.selectObjByProperty(maps);
	}


	public List<InformationClass> queryPages(Map<String, Object> params) {
		return redPigInformationClassMapper.queryPages(params);
	}


	public List<InformationClass> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigInformationClassMapper.queryPageListWithNoRelations(param);
	}


	public List<InformationClass> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigInformationClassMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private InformationClassMapper redPigInformationClassMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigInformationClassMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(InformationClass obj) {
		redPigInformationClassMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(InformationClass obj) {
		redPigInformationClassMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigInformationClassMapper.deleteById(id);
	}


	public InformationClass selectByPrimaryKey(Long id) {
		return redPigInformationClassMapper.selectByPrimaryKey(id);
	}


	public List<InformationClass> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<InformationClass> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigInformationClassMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
