package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.ExpressCompany;
import com.redpigmall.dao.ExpressCompanyMapper;
import com.redpigmall.service.RedPigExpressCompanyService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigExpressCompanyService extends BaseService<ExpressCompany> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<ExpressCompany> objs) {
		if (objs != null && objs.size() > 0) {
			redPigExpressCompanyMapper.batchDelete(objs);
		}
	}


	public ExpressCompany getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<ExpressCompany> objs = redPigExpressCompanyMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<ExpressCompany> selectObjByProperty(Map<String, Object> maps) {
		return redPigExpressCompanyMapper.selectObjByProperty(maps);
	}


	public List<ExpressCompany> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigExpressCompanyMapper.queryPageListWithNoRelations(param);
	}


	public List<ExpressCompany> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigExpressCompanyMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}

	@Autowired
	private ExpressCompanyMapper redPigExpressCompanyMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigExpressCompanyMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(ExpressCompany obj) {
		redPigExpressCompanyMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(ExpressCompany obj) {
		redPigExpressCompanyMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigExpressCompanyMapper.deleteById(id);
	}


	public ExpressCompany selectByPrimaryKey(Long id) {
		return redPigExpressCompanyMapper.selectByPrimaryKey(id);
	}


	public List<ExpressCompany> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<ExpressCompany> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigExpressCompanyMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<ExpressCompany> queryPages(Map<String, Object> params) {
		return redPigExpressCompanyMapper.queryPages(params);
	}


	public IPageList list(Map<String, Object> maps) {
		return super.listPages(maps);
	}


	@Transactional(readOnly = false)
	public void save(ExpressCompany expresscompany) {
		redPigExpressCompanyMapper.save(expresscompany);
	}


	@Transactional(readOnly = false)
	public void update(ExpressCompany expresscompany) {
		redPigExpressCompanyMapper.update(expresscompany);
	}


	@Transactional(readOnly = false)
	public void deleteByIds(String[] ids) {

		List<Long> listIds = Lists.newArrayList();
		for (String id : ids) {
			listIds.add(Long.valueOf(id));
		}

		if (listIds != null && listIds.size() > 0) {
			redPigExpressCompanyMapper.deleteByIds(listIds);
		}

	}


	public int selectCountByNotId(Map<String, Object> params) {
		Integer c = redPigExpressCompanyMapper.selectCount(params);
		if (c == null) {
			return 0;
		}
		return redPigExpressCompanyMapper.selectCountByNotId(params);
	}
}
