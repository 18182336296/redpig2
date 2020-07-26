package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.ArticleClass;
import com.redpigmall.dao.ArticleClassMapper;
import com.redpigmall.service.RedPigArticleClassService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigArticleClassService extends BaseService<ArticleClass> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<ArticleClass> objs) {
		if (objs != null && objs.size() > 0) {
			redPigArticleClassMapper.batchDelete(objs);
		}
	}


	public ArticleClass getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<ArticleClass> objs = redPigArticleClassMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<ArticleClass> selectObjByProperty(Map<String, Object> maps) {
		return redPigArticleClassMapper.selectObjByProperty(maps);
	}


	public List<ArticleClass> queryPages(Map<String, Object> params) {
		return redPigArticleClassMapper.queryPages(params);
	}


	public List<ArticleClass> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigArticleClassMapper.queryPageListWithNoRelations(param);
	}


	public List<ArticleClass> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigArticleClassMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private ArticleClassMapper redPigArticleClassMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigArticleClassMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(ArticleClass obj) {
		redPigArticleClassMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(ArticleClass obj) {
		redPigArticleClassMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigArticleClassMapper.deleteById(id);
	}


	public ArticleClass selectByPrimaryKey(Long id) {
		return redPigArticleClassMapper.selectByPrimaryKey(id);
	}


	public List<ArticleClass> queryPageList(Map<String, Object> params) {
		List<ArticleClass> acs = redPigArticleClassMapper.queryPageList(params);
		if (acs == null) {
			return Lists.newArrayList();
		}
		return acs;
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigArticleClassMapper.selectCount(params);
		if (c == null) {
			return 0;
		}
		return c;
	}


	public List<ArticleClass> queryPageListByParentIsNull(Map<String, Object> params, int begin, int max) {

		return redPigArticleClassMapper.queryPageListByParentIsNull(params);
	}


	public List<ArticleClass> findArticleClassByMark(List<String> marks) {
		return redPigArticleClassMapper.findArticleClassByMark(marks);
	}


	@Transactional(readOnly = false)
	public void update(ArticleClass ac) {
		redPigArticleClassMapper.update(ac);
	}
}
