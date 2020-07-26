package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.CmsIndexTemplate;
import com.redpigmall.dao.CmsIndexTemplateMapper;
import com.redpigmall.service.RedPigCmsIndexTemplateService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigCmsIndexTemplateService extends BaseService<CmsIndexTemplate> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<CmsIndexTemplate> objs) {
		if (objs != null && objs.size() > 0) {
			redPigCmsIndexTemplateMapper.batchDelete(objs);
		}
	}


	public CmsIndexTemplate getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<CmsIndexTemplate> objs = redPigCmsIndexTemplateMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<CmsIndexTemplate> selectObjByProperty(Map<String, Object> maps) {
		return redPigCmsIndexTemplateMapper.selectObjByProperty(maps);
	}


	public List<CmsIndexTemplate> queryPages(Map<String, Object> params) {
		return redPigCmsIndexTemplateMapper.queryPages(params);
	}


	public List<CmsIndexTemplate> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigCmsIndexTemplateMapper.queryPageListWithNoRelations(param);
	}


	public List<CmsIndexTemplate> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigCmsIndexTemplateMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private CmsIndexTemplateMapper redPigCmsIndexTemplateMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigCmsIndexTemplateMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(CmsIndexTemplate obj) {
		redPigCmsIndexTemplateMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(CmsIndexTemplate obj) {
		redPigCmsIndexTemplateMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigCmsIndexTemplateMapper.deleteById(id);
	}


	public CmsIndexTemplate selectByPrimaryKey(Long id) {
		return redPigCmsIndexTemplateMapper.selectByPrimaryKey(id);
	}


	public List<CmsIndexTemplate> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<CmsIndexTemplate> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigCmsIndexTemplateMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
