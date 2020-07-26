package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.StoreAdjustInfo;
import com.redpigmall.dao.StoreAdjustInfoMapper;
import com.redpigmall.service.RedPigStoreAdjustInfoService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigStoreAdjustInfoService extends BaseService<StoreAdjustInfo>
		 {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<StoreAdjustInfo> objs) {
		if (objs != null && objs.size() > 0) {
			redPigStoreAdjustInfoMapper.batchDelete(objs);
		}
	}


	public StoreAdjustInfo getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<StoreAdjustInfo> objs = redPigStoreAdjustInfoMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<StoreAdjustInfo> selectObjByProperty(Map<String, Object> maps) {
		return redPigStoreAdjustInfoMapper.selectObjByProperty(maps);
	}


	public List<StoreAdjustInfo> queryPages(Map<String, Object> params) {
		return redPigStoreAdjustInfoMapper.queryPages(params);
	}


	public List<StoreAdjustInfo> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigStoreAdjustInfoMapper.queryPageListWithNoRelations(param);
	}


	public List<StoreAdjustInfo> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigStoreAdjustInfoMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private StoreAdjustInfoMapper redPigStoreAdjustInfoMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigStoreAdjustInfoMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(StoreAdjustInfo obj) {
		redPigStoreAdjustInfoMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(StoreAdjustInfo obj) {
		redPigStoreAdjustInfoMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigStoreAdjustInfoMapper.deleteById(id);
	}


	public StoreAdjustInfo selectByPrimaryKey(Long id) {
		return redPigStoreAdjustInfoMapper.selectByPrimaryKey(id);
	}


	public List<StoreAdjustInfo> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<StoreAdjustInfo> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigStoreAdjustInfoMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
