package com.redpigmall.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.CollageBuyMapper;
import com.redpigmall.domain.CollageBuy;
import com.redpigmall.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class RedPigCollageBuyService extends BaseService<CollageBuy>  {

	@Transactional(readOnly = false)
	public void batchDelObjs(List<CollageBuy> objs) {
		if (objs != null && objs.size() > 0) {
			collageBuyMapper.batchDelete(objs);
		}
	}

	public CollageBuy getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<CollageBuy> objs = collageBuyMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<CollageBuy> selectObjByProperty(Map<String, Object> maps) {
		return collageBuyMapper.selectObjByProperty(maps);
	}


	public List<CollageBuy> queryPages(Map<String, Object> params) {
		return collageBuyMapper.queryPages(params);
	}


	public List<CollageBuy> queryPageListWithNoRelations(Map<String, Object> param) {
		return collageBuyMapper.queryPageListWithNoRelations(param);
	}


	public List<CollageBuy> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return collageBuyMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private CollageBuyMapper collageBuyMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		collageBuyMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(CollageBuy obj) {
		collageBuyMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(CollageBuy obj) {
		collageBuyMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		collageBuyMapper.deleteById(id);
	}


	public CollageBuy selectByPrimaryKey(Long id) {
		return collageBuyMapper.selectByPrimaryKey(id);
	}


	public List<CollageBuy> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<CollageBuy> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = collageBuyMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<CollageBuy> queryPageListOrderByGgSelledCount(Map<String, Object> params) {
		List<CollageBuy> ng = this.collageBuyMapper.queryPageListOrderByGgSelledCount(params);
		if (ng == null) {
			return Lists.newArrayList();
		}
		return ng;
	}

    /*public Integer selectCounts(Map<String,Integer> pa) {
		return collageBuyMapper.selectCounts(pa);
    }*/
}
