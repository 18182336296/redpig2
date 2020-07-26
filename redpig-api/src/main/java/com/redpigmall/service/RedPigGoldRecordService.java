package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.GoldRecord;
import com.redpigmall.dao.GoldRecordMapper;
import com.redpigmall.service.RedPigGoldRecordService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGoldRecordService extends BaseService<GoldRecord>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GoldRecord> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGoldRecordMapper.batchDelete(objs);
		}
	}


	public GoldRecord getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GoldRecord> objs = redPigGoldRecordMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GoldRecord> selectObjByProperty(Map<String, Object> maps) {
		return redPigGoldRecordMapper.selectObjByProperty(maps);
	}


	public List<GoldRecord> queryPages(Map<String, Object> params) {
		return redPigGoldRecordMapper.queryPages(params);
	}


	public List<GoldRecord> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGoldRecordMapper.queryPageListWithNoRelations(param);
	}


	public List<GoldRecord> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGoldRecordMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}

	@Autowired
	private GoldRecordMapper redPigGoldRecordMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGoldRecordMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GoldRecord obj) {
		redPigGoldRecordMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GoldRecord obj) {
		redPigGoldRecordMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGoldRecordMapper.deleteById(id);
	}


	public GoldRecord selectByPrimaryKey(Long id) {
		return redPigGoldRecordMapper.selectByPrimaryKey(id);
	}


	public List<GoldRecord> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GoldRecord> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGoldRecordMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public IPageList list(Map<String, Object> params) {
		
		return super.listPages(params);
	}
}
