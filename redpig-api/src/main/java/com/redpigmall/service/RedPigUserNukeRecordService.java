package com.redpigmall.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.UserNukeRecordMapper;
import com.redpigmall.domain.UserNukeRecord;
import com.redpigmall.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class RedPigUserNukeRecordService extends BaseService<UserNukeRecord>  {

	@Autowired
	private UserNukeRecordMapper userNukeRecordMapper;

	@Transactional(readOnly = false)
	public void batchDelObjs(List<UserNukeRecord> objs) {
		if (objs != null && objs.size() > 0) {
			userNukeRecordMapper.batchDelete(objs);
		}
	}

	public UserNukeRecord getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<UserNukeRecord> objs = userNukeRecordMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<UserNukeRecord> selectObjByProperty(Map<String, Object> maps) {
		return userNukeRecordMapper.selectObjByProperty(maps);
	}


	public List<UserNukeRecord> queryPages(Map<String, Object> params) {
		return userNukeRecordMapper.queryPages(params);
	}


	public List<UserNukeRecord> queryPageListWithNoRelations(Map<String, Object> param) {
		return userNukeRecordMapper.queryPageListWithNoRelations(param);
	}


	public List<UserNukeRecord> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return userNukeRecordMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		userNukeRecordMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(UserNukeRecord obj) {
		userNukeRecordMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(UserNukeRecord obj) {
		userNukeRecordMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		userNukeRecordMapper.deleteById(id);
	}


	public UserNukeRecord selectByPrimaryKey(Long id) {
		return userNukeRecordMapper.selectByPrimaryKey(id);
	}


	public List<UserNukeRecord> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<UserNukeRecord> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = userNukeRecordMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;
	}

}
