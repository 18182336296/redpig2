package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.ArticleMapper;
import com.redpigmall.service.RedPigArticleService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigArticleService extends BaseService<Article>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Article> objs) {
		if (objs != null && objs.size() > 0) {
			redPigArticleMapper.batchDelete(objs);
		}
	}


	public Article getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Article> objs = redPigArticleMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Article> selectObjByProperty(Map<String, Object> maps) {
		return redPigArticleMapper.selectObjByProperty(maps);
	}


	public List<Article> queryPages(Map<String, Object> params) {
		return redPigArticleMapper.queryPages(params);
	}


	public List<Article> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigArticleMapper.queryPageListWithNoRelations(param);
	}


	public List<Article> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigArticleMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private ArticleMapper redPigArticleMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigArticleMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Article obj) {
		redPigArticleMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Article obj) {
		redPigArticleMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigArticleMapper.deleteById(id);
	}


	public Article selectByPrimaryKey(Long id) {
		return redPigArticleMapper.selectByPrimaryKey(id);
	}


	public List<Article> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Article> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigArticleMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
