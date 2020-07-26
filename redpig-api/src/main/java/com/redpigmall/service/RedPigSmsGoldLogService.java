package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.SmsGoldLog;
import com.redpigmall.dao.SmsGoldLogMapper;
import com.redpigmall.service.RedPigSmsGoldLogService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigSmsGoldLogService extends BaseService<SmsGoldLog>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<SmsGoldLog> objs) {
		if (objs != null && objs.size() > 0) {
			redPigSmsGoldLogMapper.batchDelete(objs);
		}
	}


	public SmsGoldLog getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<SmsGoldLog> objs = redPigSmsGoldLogMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<SmsGoldLog> selectObjByProperty(Map<String, Object> maps) {
		return redPigSmsGoldLogMapper.selectObjByProperty(maps);
	}


	public List<SmsGoldLog> queryPages(Map<String, Object> params) {
		return redPigSmsGoldLogMapper.queryPages(params);
	}


	public List<SmsGoldLog> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigSmsGoldLogMapper.queryPageListWithNoRelations(param);
	}


	public List<SmsGoldLog> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigSmsGoldLogMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private SmsGoldLogMapper redPigSmsGoldLogMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigSmsGoldLogMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(SmsGoldLog obj) {
		redPigSmsGoldLogMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(SmsGoldLog obj) {
		redPigSmsGoldLogMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigSmsGoldLogMapper.deleteById(id);
	}


	public SmsGoldLog selectByPrimaryKey(Long id) {
		return redPigSmsGoldLogMapper.selectByPrimaryKey(id);
	}


	public List<SmsGoldLog> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<SmsGoldLog> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigSmsGoldLogMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}

}
