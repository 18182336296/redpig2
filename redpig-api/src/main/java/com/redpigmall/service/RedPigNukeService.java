package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.Nuke;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.dao.NukeMapper;
import com.redpigmall.service.RedPigNukeService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigNukeService extends BaseService<Nuke> {

	@Autowired
	private NukeMapper redPigNukeMapper;

	@Transactional(readOnly = false)
	public void batchDelObjs(List<Nuke> objs) {
		if (objs != null && objs.size() > 0) {
			redPigNukeMapper.batchDelete(objs);
		}
	}

	public Nuke getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Nuke> objs = redPigNukeMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}

	public List<Nuke> selectObjByProperty(Map<String, Object> maps) {
		return redPigNukeMapper.selectObjByProperty(maps);
	}

	public List<Nuke> queryPages(Map<String, Object> params) {
		return redPigNukeMapper.queryPages(params);
	}

	public List<Nuke> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigNukeMapper.queryPageListWithNoRelations(param);
	}

	public List<Nuke> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigNukeMapper.queryPagesWithNoRelations(params);
	}

	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}

	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigNukeMapper.batchDeleteByIds(ids);
	}

	@Transactional(readOnly = false)
	public void saveEntity(Nuke obj) {
		redPigNukeMapper.saveEntity(obj);
	}

	@Transactional(readOnly = false)
	public void updateById(Nuke obj) {
		redPigNukeMapper.updateById(obj);
	}

	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigNukeMapper.deleteById(id);
	}

	public Nuke selectByPrimaryKey(Long id) {
		return redPigNukeMapper.selectByPrimaryKey(id);
	}

	public List<Nuke> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		List<Nuke> Nukes = super.queryPageList(params, begin, max);
		if (Nukes == null) {
			return Lists.newArrayList();
		}
		return Nukes;
	}

	public List<Nuke> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}

	public int selectCount(Map<String, Object> params) {
		Integer c = redPigNukeMapper.selectCount(params);
		if (c == null) {
			return 0;
		}
		return c;
	}
	
	@Transactional(readOnly = false)
	public void batchDeleteByIds(String[] ids) {
		List<Long> lists = Lists.newArrayList();
		for (String id : ids) {
			Nuke nuke = this.redPigNukeMapper.selectByPrimaryKey(CommUtil.null2Long(id));
			if(nuke.getNg_list() ==null || nuke.getNg_list().size()==0){
				lists.add(nuke.getId());
			}
		}
		
		redPigNukeMapper.batchDeleteByIds(lists);
	}

}
