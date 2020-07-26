package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.GroupInfo;
import com.redpigmall.dao.GroupInfoMapper;
import com.redpigmall.service.RedPigGroupInfoService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGroupInfoService extends BaseService<GroupInfo> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GroupInfo> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGroupInfoMapper.batchDelete(objs);
		}
	}


	public GroupInfo getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GroupInfo> objs = redPigGroupInfoMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GroupInfo> selectObjByProperty(Map<String, Object> maps) {
		return redPigGroupInfoMapper.selectObjByProperty(maps);
	}


	public List<GroupInfo> queryPages(Map<String, Object> params) {
		return redPigGroupInfoMapper.queryPages(params);
	}


	public List<GroupInfo> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGroupInfoMapper.queryPageListWithNoRelations(param);
	}


	public List<GroupInfo> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGroupInfoMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private GroupInfoMapper redPigGroupInfoMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGroupInfoMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GroupInfo obj) {
		redPigGroupInfoMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GroupInfo obj) {
		redPigGroupInfoMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGroupInfoMapper.deleteById(id);
	}


	public GroupInfo selectByPrimaryKey(Long id) {
		return redPigGroupInfoMapper.selectByPrimaryKey(id);
	}


	public List<GroupInfo> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GroupInfo> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGroupInfoMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


}
