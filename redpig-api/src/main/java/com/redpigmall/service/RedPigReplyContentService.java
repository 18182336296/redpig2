package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.ReplyContent;
import com.redpigmall.dao.ReplyContentMapper;
import com.redpigmall.service.RedPigReplyContentService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigReplyContentService extends BaseService<ReplyContent>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<ReplyContent> objs) {
		if (objs != null && objs.size() > 0) {
			redPigReplyContentMapper.batchDelete(objs);
		}
	}


	public ReplyContent getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<ReplyContent> objs = redPigReplyContentMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<ReplyContent> selectObjByProperty(Map<String, Object> maps) {
		return redPigReplyContentMapper.selectObjByProperty(maps);
	}


	public List<ReplyContent> queryPages(Map<String, Object> params) {
		return redPigReplyContentMapper.queryPages(params);
	}


	public List<ReplyContent> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigReplyContentMapper.queryPageListWithNoRelations(param);
	}


	public List<ReplyContent> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigReplyContentMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private ReplyContentMapper redPigReplyContentMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigReplyContentMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(ReplyContent obj) {
		redPigReplyContentMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(ReplyContent obj) {
		redPigReplyContentMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigReplyContentMapper.deleteById(id);
	}


	public ReplyContent selectByPrimaryKey(Long id) {
		return redPigReplyContentMapper.selectByPrimaryKey(id);
	}


	public List<ReplyContent> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<ReplyContent> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigReplyContentMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
