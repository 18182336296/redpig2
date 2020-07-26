package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.MessageMapper;
import com.redpigmall.service.RedPigMessageService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigMessageService extends BaseService<Message>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Message> objs) {
		if (objs != null && objs.size() > 0) {
			redPigMessageMapper.batchDelete(objs);
		}
	}


	public Message getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Message> objs = redPigMessageMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Message> selectObjByProperty(Map<String, Object> maps) {
		return redPigMessageMapper.selectObjByProperty(maps);
	}


	public List<Message> queryPages(Map<String, Object> params) {
		return redPigMessageMapper.queryPages(params);
	}


	public List<Message> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigMessageMapper.queryPageListWithNoRelations(param);
	}


	public List<Message> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigMessageMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private MessageMapper redPigMessageMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigMessageMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Message obj) {
		redPigMessageMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Message obj) {
		redPigMessageMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigMessageMapper.deleteById(id);
	}


	public Message selectByPrimaryKey(Long id) {
		return redPigMessageMapper.selectByPrimaryKey(id);
	}


	public List<Message> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		List<Message> mesgs = super.queryPageList(params, begin, max);
		if (mesgs == null) {
			return Lists.newArrayList();
		}
		return mesgs;
	}


	public List<Message> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigMessageMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<Message> queryPageListByParentIsNull(Map<String, Object> params, int begin, int max) {

		return redPigMessageMapper.queryPageListByParentIsNull(params);
	}
}
