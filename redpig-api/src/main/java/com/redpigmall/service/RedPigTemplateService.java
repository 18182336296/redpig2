package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.dao.TemplateMapper;
import com.redpigmall.service.RedPigTemplateService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigTemplateService extends BaseService<Template>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Template> objs) {
		if (objs != null && objs.size() > 0) {
			redPigTemplateMapper.batchDelete(objs);
		}
	}


	public Template getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Template> objs = redPigTemplateMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Template> selectObjByProperty(Map<String, Object> maps) {
		return redPigTemplateMapper.selectObjByProperty(maps);
	}


	public List<Template> queryPages(Map<String, Object> params) {
		return redPigTemplateMapper.queryPages(params);
	}


	public List<Template> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigTemplateMapper.queryPageListWithNoRelations(param);
	}


	public List<Template> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigTemplateMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private TemplateMapper redPigTemplateMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigTemplateMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Template obj) {
		redPigTemplateMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Template obj) {
		redPigTemplateMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigTemplateMapper.deleteById(id);
	}


	public Template selectByPrimaryKey(Long id) {
		return redPigTemplateMapper.selectByPrimaryKey(id);
	}


	public List<Template> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Template> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigTemplateMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	@Transactional(readOnly = false)
	public void batchDeleteByIds(String[] ids) {
		List<Long> listIds = Lists.newArrayList();
		for (String id : ids) {
			if (StringUtils.isNotBlank(id)) {
				listIds.add(Long.parseLong(id));
			}
		}

		this.redPigTemplateMapper.batchDeleteByIds(listIds);

	}

}
