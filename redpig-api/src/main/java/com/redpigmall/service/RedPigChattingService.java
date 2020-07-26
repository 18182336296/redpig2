package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.Chatting;
import com.redpigmall.dao.ChattingMapper;
import com.redpigmall.service.RedPigChattingService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public  class RedPigChattingService extends BaseService<Chatting> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Chatting> objs) {
		if (objs != null && objs.size() > 0) {
			redPigChattingMapper.batchDelete(objs);
		}
	}


	public Chatting getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Chatting> objs = redPigChattingMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Chatting> selectObjByProperty(Map<String, Object> maps) {
		return redPigChattingMapper.selectObjByProperty(maps);
	}


	public List<Chatting> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigChattingMapper.queryPageListWithNoRelations(param);
	}


	public List<Chatting> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigChattingMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}

	@Autowired
	private ChattingMapper redPigChattingMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigChattingMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Chatting obj) {
		redPigChattingMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Chatting obj) {
		redPigChattingMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigChattingMapper.deleteById(id);
	}


	public Chatting selectByPrimaryKey(Long id) {
		return redPigChattingMapper.selectByPrimaryKey(id);
	}


	public List<Chatting> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Chatting> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigChattingMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<Chatting> queryPages(Map<String, Object> params) {
		return redPigChattingMapper.queryPages(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}
}


