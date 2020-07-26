package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.Area;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.AreaMapper;
import com.redpigmall.service.RedPigAreaService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigAreaService extends BaseService<Area>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Area> objs) {
		if (objs != null && objs.size() > 0) {
			redPigAreaMapper.batchDelete(objs);
		}
	}


	public Area getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Area> objs = redPigAreaMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Area> selectObjByProperty(Map<String, Object> maps) {
		return redPigAreaMapper.selectObjByProperty(maps);
	}


	public List<Area> queryPages(Map<String, Object> params) {
		return redPigAreaMapper.queryPages(params);
	}


	public List<Area> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigAreaMapper.queryPageListWithNoRelations(param);
	}


	public List<Area> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigAreaMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}

	@Autowired
	private AreaMapper redPigAreaMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigAreaMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Area obj) {
		redPigAreaMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Area obj) {
		redPigAreaMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigAreaMapper.deleteById(id);
	}


	public Area selectByPrimaryKey(Long id) {
		return redPigAreaMapper.selectByPrimaryKey(id);
	}


	public List<Area> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Area> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigAreaMapper.selectCount(params);
		if (c == null) {
			return 0;
		}
		return c;
	}


	public List<Area> queryPageListByParentIsNull(Map<String, Object> params, int begin, int max) {
		return redPigAreaMapper.queryPageListByParentIsNull(params);
	}


	public IPageList list(Map<String, Object> maps) {
		return super.listPages(maps);
	}


	@Transactional(readOnly = false)
	public void update(Area obj) {
		redPigAreaMapper.update(obj);
	}


	@Transactional(readOnly = false)
	public void delete(Long id) {
		Area area = new Area();
		area.setId(id);
		redPigAreaMapper.delete(area);
	}


	public List<Area> queryChilds(Long id) {
		Area area = new Area();
		area.setId(id);
		return redPigAreaMapper.queryChilds(area);
	}

	@Transactional(readOnly = false)

	public void deleteBatch(List<Long> listIds) {
		redPigAreaMapper.deleteBatch(listIds);
	}


	@Transactional(readOnly = false)
	public void save(Area area) {
		redPigAreaMapper.save(area);
	}

}
