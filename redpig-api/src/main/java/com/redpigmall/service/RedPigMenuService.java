package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.MenuMapper;
import com.redpigmall.service.RedPigMenuService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigMenuService extends BaseService<Menu>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Menu> objs) {
		if (objs != null && objs.size() > 0) {
			redPigMenuMapper.batchDelete(objs);
		}
	}


	public Menu getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Menu> objs = redPigMenuMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Menu> selectObjByProperty(Map<String, Object> maps) {
		return redPigMenuMapper.selectObjByProperty(maps);
	}


	public List<Menu> queryPages(Map<String, Object> params) {
		return redPigMenuMapper.queryPages(params);
	}


	public List<Menu> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigMenuMapper.queryPageListWithNoRelations(param);
	}


	public List<Menu> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigMenuMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}

	@Autowired
	private MenuMapper redPigMenuMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigMenuMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Menu obj) {
		redPigMenuMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Menu obj) {
		redPigMenuMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigMenuMapper.deleteById(id);
	}


	public Menu selectByPrimaryKey(Long id) {
		return redPigMenuMapper.selectByPrimaryKey(id);
	}


	public List<Menu> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Menu> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigMenuMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	@Transactional(readOnly = false)
	public void batchDelete(List<Menu> goodsPhotos) {
		redPigMenuMapper.batchDelete(goodsPhotos);
	}


	@Transactional(readOnly = false)
	public void save(Menu photo) {
		redPigMenuMapper.save(photo);
	}


	@Transactional(readOnly = false)
	public void update(Menu photo) {
		redPigMenuMapper.update(photo);
	}


	@Transactional(readOnly = false)
	public void delete(Long id) {
		redPigMenuMapper.delete(id);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}


	public List<Menu> getUserMenus(Long userId, Long menuId) {
		Map<String,Object> maps = Maps.newHashMap();
		maps.put("userId", userId);
		maps.put("menuId", menuId);
		
		return redPigMenuMapper.getUserMenus(maps);
	}

}
