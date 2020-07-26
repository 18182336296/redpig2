package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.InformationReply;
import com.redpigmall.dao.InformationReplyMapper;
import com.redpigmall.service.RedPigInformationReplyService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigInformationReplyService extends BaseService<InformationReply>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<InformationReply> objs) {
		if (objs != null && objs.size() > 0) {
			redPigInformationReplyMapper.batchDelete(objs);
		}
	}


	public InformationReply getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<InformationReply> objs = redPigInformationReplyMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<InformationReply> selectObjByProperty(Map<String, Object> maps) {
		return redPigInformationReplyMapper.selectObjByProperty(maps);
	}


	public List<InformationReply> queryPages(Map<String, Object> params) {
		return redPigInformationReplyMapper.queryPages(params);
	}


	public List<InformationReply> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigInformationReplyMapper.queryPageListWithNoRelations(param);
	}


	public List<InformationReply> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigInformationReplyMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private InformationReplyMapper redPigInformationReplyMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigInformationReplyMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(InformationReply obj) {
		redPigInformationReplyMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(InformationReply obj) {
		redPigInformationReplyMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigInformationReplyMapper.deleteById(id);
	}


	public InformationReply selectByPrimaryKey(Long id) {
		return redPigInformationReplyMapper.selectByPrimaryKey(id);
	}


	public List<InformationReply> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<InformationReply> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigInformationReplyMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
