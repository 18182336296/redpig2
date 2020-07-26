package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.domain.GroupAreaInfo;
import com.redpigmall.dao.GroupAreaInfoMapper;
import com.redpigmall.service.RedPigGroupAreaInfoService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGroupAreaInfoService extends BaseService<GroupAreaInfo>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GroupAreaInfo> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGroupAreaInfoMapper.batchDelete(objs);
		}
	}


	public GroupAreaInfo getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GroupAreaInfo> objs = redPigGroupAreaInfoMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GroupAreaInfo> selectObjByProperty(Map<String, Object> maps) {
		return redPigGroupAreaInfoMapper.selectObjByProperty(maps);
	}


	public List<GroupAreaInfo> queryPages(Map<String, Object> params) {
		return redPigGroupAreaInfoMapper.queryPages(params);
	}


	public List<GroupAreaInfo> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGroupAreaInfoMapper.queryPageListWithNoRelations(param);
	}


	public List<GroupAreaInfo> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGroupAreaInfoMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private GroupAreaInfoMapper redPigGroupAreaInfoMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		if (RedPigCommonUtil.isNotNull(ids)) {
			redPigGroupAreaInfoMapper.batchDeleteByIds(ids);
		}
	}


	@Transactional(readOnly = false)
	public void saveEntity(GroupAreaInfo obj) {
		redPigGroupAreaInfoMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GroupAreaInfo obj) {
		redPigGroupAreaInfoMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGroupAreaInfoMapper.deleteById(id);
	}


	public GroupAreaInfo selectByPrimaryKey(Long id) {
		return redPigGroupAreaInfoMapper.selectByPrimaryKey(id);
	}


	public List<GroupAreaInfo> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GroupAreaInfo> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGroupAreaInfoMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
