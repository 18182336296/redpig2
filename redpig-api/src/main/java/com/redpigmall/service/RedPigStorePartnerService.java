package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.StorePartner;
import com.redpigmall.dao.StorePartnerMapper;
import com.redpigmall.service.RedPigStorePartnerService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigStorePartnerService extends BaseService<StorePartner>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<StorePartner> objs) {
		if (objs != null && objs.size() > 0) {
			redPigStorePartnerMapper.batchDelete(objs);
		}
	}


	public StorePartner getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<StorePartner> objs = redPigStorePartnerMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<StorePartner> selectObjByProperty(Map<String, Object> maps) {
		return redPigStorePartnerMapper.selectObjByProperty(maps);
	}


	public List<StorePartner> queryPages(Map<String, Object> params) {
		return redPigStorePartnerMapper.queryPages(params);
	}


	public List<StorePartner> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigStorePartnerMapper.queryPageListWithNoRelations(param);
	}


	public List<StorePartner> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigStorePartnerMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private StorePartnerMapper redPigStorePartnerMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigStorePartnerMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(StorePartner obj) {
		redPigStorePartnerMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(StorePartner obj) {
		redPigStorePartnerMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigStorePartnerMapper.deleteById(id);
	}


	public StorePartner selectByPrimaryKey(Long id) {
		return redPigStorePartnerMapper.selectByPrimaryKey(id);
	}


	public List<StorePartner> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<StorePartner> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigStorePartnerMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
