package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.InventoryOperation;
import com.redpigmall.dao.InventoryOperationMapper;
import com.redpigmall.service.RedPigInventoryOperationService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigInventoryOperationService extends BaseService<InventoryOperation>
		 {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<InventoryOperation> objs) {
		if (objs != null && objs.size() > 0) {
			redPigInventoryOperationMapper.batchDelete(objs);
		}
	}


	public InventoryOperation getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<InventoryOperation> objs = redPigInventoryOperationMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<InventoryOperation> selectObjByProperty(Map<String, Object> maps) {
		return redPigInventoryOperationMapper.selectObjByProperty(maps);
	}


	public List<InventoryOperation> queryPages(Map<String, Object> params) {
		return redPigInventoryOperationMapper.queryPages(params);
	}


	public List<InventoryOperation> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigInventoryOperationMapper.queryPageListWithNoRelations(param);
	}


	public List<InventoryOperation> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigInventoryOperationMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private InventoryOperationMapper redPigInventoryOperationMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		if (ids != null && ids.size() > 0) {
			redPigInventoryOperationMapper.batchDeleteByIds(ids);
		}
	}


	@Transactional(readOnly = false)
	public void saveEntity(InventoryOperation obj) {
		redPigInventoryOperationMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(InventoryOperation obj) {
		redPigInventoryOperationMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigInventoryOperationMapper.deleteById(id);
	}


	public InventoryOperation selectByPrimaryKey(Long id) {
		return redPigInventoryOperationMapper.selectByPrimaryKey(id);
	}


	public List<InventoryOperation> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<InventoryOperation> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigInventoryOperationMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
