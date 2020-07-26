package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.SnsAttention;
import com.redpigmall.dao.SnsAttentionMapper;
import com.redpigmall.service.RedPigSnsAttentionService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigSnsAttentionService extends BaseService<SnsAttention>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<SnsAttention> objs) {
		if (objs != null && objs.size() > 0) {
			redPigSnsAttentionMapper.batchDelete(objs);
		}
	}


	public SnsAttention getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<SnsAttention> objs = redPigSnsAttentionMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<SnsAttention> selectObjByProperty(Map<String, Object> maps) {
		return redPigSnsAttentionMapper.selectObjByProperty(maps);
	}


	public List<SnsAttention> queryPages(Map<String, Object> params) {
		return redPigSnsAttentionMapper.queryPages(params);
	}


	public List<SnsAttention> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigSnsAttentionMapper.queryPageListWithNoRelations(param);
	}


	public List<SnsAttention> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigSnsAttentionMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private SnsAttentionMapper redPigSnsAttentionMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigSnsAttentionMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(SnsAttention obj) {
		redPigSnsAttentionMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(SnsAttention obj) {
		redPigSnsAttentionMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigSnsAttentionMapper.deleteById(id);
	}


	public SnsAttention selectByPrimaryKey(Long id) {
		return redPigSnsAttentionMapper.selectByPrimaryKey(id);
	}


	public List<SnsAttention> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<SnsAttention> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigSnsAttentionMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
