package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.CircleInvitation;
import com.redpigmall.dao.CircleInvitationMapper;
import com.redpigmall.service.RedPigCircleInvitationService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigCircleInvitationService extends BaseService<CircleInvitation> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<CircleInvitation> objs) {
		if (objs != null && objs.size() > 0) {
			redPigCircleInvitationMapper.batchDelete(objs);
		}
	}


	public CircleInvitation getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<CircleInvitation> objs = redPigCircleInvitationMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<CircleInvitation> selectObjByProperty(Map<String, Object> maps) {
		return redPigCircleInvitationMapper.selectObjByProperty(maps);
	}


	public List<CircleInvitation> queryPages(Map<String, Object> params) {
		return redPigCircleInvitationMapper.queryPages(params);
	}


	public List<CircleInvitation> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigCircleInvitationMapper.queryPageListWithNoRelations(param);
	}


	public List<CircleInvitation> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigCircleInvitationMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private CircleInvitationMapper redPigCircleInvitationMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigCircleInvitationMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(CircleInvitation obj) {
		redPigCircleInvitationMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(CircleInvitation obj) {
		redPigCircleInvitationMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigCircleInvitationMapper.deleteById(id);
	}


	public CircleInvitation selectByPrimaryKey(Long id) {
		return redPigCircleInvitationMapper.selectByPrimaryKey(id);
	}


	public List<CircleInvitation> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<CircleInvitation> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigCircleInvitationMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
