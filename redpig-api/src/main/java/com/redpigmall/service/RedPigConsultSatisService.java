package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.ConsultSatis;
import com.redpigmall.dao.ConsultSatisMapper;
import com.redpigmall.service.RedPigConsultSatisService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigConsultSatisService extends BaseService<ConsultSatis>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<ConsultSatis> objs) {
		if (objs != null && objs.size() > 0) {
			redPigConsultSatisMapper.batchDelete(objs);
		}
	}


	public ConsultSatis getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<ConsultSatis> objs = redPigConsultSatisMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<ConsultSatis> selectObjByProperty(Map<String, Object> maps) {
		return redPigConsultSatisMapper.selectObjByProperty(maps);
	}


	public List<ConsultSatis> queryPages(Map<String, Object> params) {
		return redPigConsultSatisMapper.queryPages(params);
	}


	public List<ConsultSatis> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigConsultSatisMapper.queryPageListWithNoRelations(param);
	}


	public List<ConsultSatis> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigConsultSatisMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private ConsultSatisMapper redPigConsultSatisMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigConsultSatisMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(ConsultSatis obj) {
		redPigConsultSatisMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(ConsultSatis obj) {
		redPigConsultSatisMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigConsultSatisMapper.deleteById(id);
	}


	public ConsultSatis selectByPrimaryKey(Long id) {
		return redPigConsultSatisMapper.selectByPrimaryKey(id);
	}


	public List<ConsultSatis> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<ConsultSatis> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigConsultSatisMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
