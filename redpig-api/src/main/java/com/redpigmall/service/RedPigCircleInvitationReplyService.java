package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.CircleInvitationReply;
import com.redpigmall.dao.CircleInvitationReplyMapper;
import com.redpigmall.service.RedPigCircleInvitationReplyService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigCircleInvitationReplyService extends BaseService<CircleInvitationReply> {



	@Transactional(readOnly = false)
	public void batchDelObjs(List<CircleInvitationReply> objs) {
		if (objs != null && objs.size() > 0) {
			redPigCircleInvitationReplyMapper.batchDelete(objs);
		}
	}


	public CircleInvitationReply getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<CircleInvitationReply> objs = redPigCircleInvitationReplyMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<CircleInvitationReply> selectObjByProperty(Map<String, Object> maps) {
		return redPigCircleInvitationReplyMapper.selectObjByProperty(maps);
	}


	public List<CircleInvitationReply> queryPages(Map<String, Object> params) {
		return redPigCircleInvitationReplyMapper.queryPages(params);
	}


	public List<CircleInvitationReply> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigCircleInvitationReplyMapper.queryPageListWithNoRelations(param);
	}


	public List<CircleInvitationReply> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigCircleInvitationReplyMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private CircleInvitationReplyMapper redPigCircleInvitationReplyMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigCircleInvitationReplyMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(CircleInvitationReply obj) {
		redPigCircleInvitationReplyMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(CircleInvitationReply obj) {
		redPigCircleInvitationReplyMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigCircleInvitationReplyMapper.deleteById(id);
	}


	public CircleInvitationReply selectByPrimaryKey(Long id) {
		return redPigCircleInvitationReplyMapper.selectByPrimaryKey(id);
	}


	public List<CircleInvitationReply> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<CircleInvitationReply> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigCircleInvitationReplyMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
