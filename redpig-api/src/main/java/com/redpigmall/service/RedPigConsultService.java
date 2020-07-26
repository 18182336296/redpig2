package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.Consult;
import com.redpigmall.dao.ConsultMapper;
import com.redpigmall.service.RedPigConsultService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigConsultService extends BaseService<Consult>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Consult> objs) {
		if (objs != null && objs.size() > 0) {
			redPigConsultMapper.batchDelete(objs);
		}
	}


	public Consult getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Consult> objs = redPigConsultMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Consult> selectObjByProperty(Map<String, Object> maps) {
		return redPigConsultMapper.selectObjByProperty(maps);
	}


	public List<Consult> queryPages(Map<String, Object> params) {
		return redPigConsultMapper.queryPages(params);
	}


	public List<Consult> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigConsultMapper.queryPageListWithNoRelations(param);
	}


	public List<Consult> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigConsultMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private ConsultMapper redPigConsultMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigConsultMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Consult obj) {
		redPigConsultMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Consult obj) {
		redPigConsultMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigConsultMapper.deleteById(id);
	}


	public Consult selectByPrimaryKey(Long id) {
		return redPigConsultMapper.selectByPrimaryKey(id);
	}


	public List<Consult> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Consult> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigConsultMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
