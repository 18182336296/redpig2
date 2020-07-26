package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.VMenu;
import com.redpigmall.dao.VMenuMapper;
import com.redpigmall.service.RedPigVMenuService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigVMenuService extends BaseService<VMenu>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<VMenu> objs) {
		if (objs != null && objs.size() > 0) {
			redPigVMenuMapper.batchDelete(objs);
		}
	}


	public VMenu getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<VMenu> objs = redPigVMenuMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<VMenu> selectObjByProperty(Map<String, Object> maps) {
		return redPigVMenuMapper.selectObjByProperty(maps);
	}


	public List<VMenu> queryPages(Map<String, Object> params) {
		return redPigVMenuMapper.queryPages(params);
	}


	public List<VMenu> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigVMenuMapper.queryPageListWithNoRelations(param);
	}


	public List<VMenu> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigVMenuMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private VMenuMapper redPigVMenuMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigVMenuMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(VMenu obj) {
		redPigVMenuMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(VMenu obj) {
		redPigVMenuMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigVMenuMapper.deleteById(id);
	}


	public VMenu selectByPrimaryKey(Long id) {
		return redPigVMenuMapper.selectByPrimaryKey(id);
	}


	public List<VMenu> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<VMenu> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigVMenuMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<VMenu> queryPageListByParentIsNull(Map<String, Object> params, int begin, int max) {

		return redPigVMenuMapper.queryPageListByParentIsNull(params);
	}
}
