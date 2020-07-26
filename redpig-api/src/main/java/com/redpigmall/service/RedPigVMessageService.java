package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.VMessage;
import com.redpigmall.dao.VMessageMapper;
import com.redpigmall.service.RedPigVMessageService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigVMessageService extends BaseService<VMessage> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<VMessage> objs) {
		if (objs != null && objs.size() > 0) {
			redPigVMessageMapper.batchDelete(objs);
		}
	}


	public VMessage getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<VMessage> objs = redPigVMessageMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<VMessage> selectObjByProperty(Map<String, Object> maps) {
		return redPigVMessageMapper.selectObjByProperty(maps);
	}


	public List<VMessage> queryPages(Map<String, Object> params) {
		return redPigVMessageMapper.queryPages(params);
	}


	public List<VMessage> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigVMessageMapper.queryPageListWithNoRelations(param);
	}


	public List<VMessage> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigVMessageMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private VMessageMapper redPigVMessageMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigVMessageMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(VMessage obj) {
		redPigVMessageMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(VMessage obj) {
		redPigVMessageMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigVMessageMapper.deleteById(id);
	}


	public VMessage selectByPrimaryKey(Long id) {
		return redPigVMessageMapper.selectByPrimaryKey(id);
	}


	public List<VMessage> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<VMessage> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigVMessageMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
