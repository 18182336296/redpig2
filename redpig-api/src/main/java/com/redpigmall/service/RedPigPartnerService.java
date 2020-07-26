package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.Partner;
import com.redpigmall.dao.PartnerMapper;
import com.redpigmall.service.RedPigPartnerService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigPartnerService extends BaseService<Partner>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Partner> objs) {
		if (objs != null && objs.size() > 0) {
			redPigPartnerMapper.batchDelete(objs);
		}
	}


	public Partner getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Partner> objs = redPigPartnerMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Partner> selectObjByProperty(Map<String, Object> maps) {
		return redPigPartnerMapper.selectObjByProperty(maps);
	}


	public List<Partner> queryPages(Map<String, Object> params) {
		return redPigPartnerMapper.queryPages(params);
	}


	public List<Partner> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigPartnerMapper.queryPageListWithNoRelations(param);
	}


	public List<Partner> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigPartnerMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private PartnerMapper redPigPartnerMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigPartnerMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Partner obj) {
		redPigPartnerMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Partner obj) {
		redPigPartnerMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigPartnerMapper.deleteById(id);
	}


	public Partner selectByPrimaryKey(Long id) {
		return redPigPartnerMapper.selectByPrimaryKey(id);
	}


	public List<Partner> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Partner> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigPartnerMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
