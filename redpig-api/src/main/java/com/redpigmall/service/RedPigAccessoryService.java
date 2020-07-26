package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.Accessory;
import com.redpigmall.dao.AccessoryMapper;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigAccessoryService extends BaseService<Accessory>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Accessory> objs) {
		if (objs != null && objs.size() > 0) {
			redPigAccessoryMapper.batchDelete(objs);
		}
	}


	public Accessory getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Accessory> objs = redPigAccessoryMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Accessory> selectObjByProperty(Map<String, Object> maps) {
		return redPigAccessoryMapper.selectObjByProperty(maps);
	}


	public List<Accessory> queryPages(Map<String, Object> params) {
		return redPigAccessoryMapper.queryPages(params);
	}


	public List<Accessory> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigAccessoryMapper.queryPageListWithNoRelations(param);
	}


	public List<Accessory> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigAccessoryMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}

	@Autowired
	private AccessoryMapper redPigAccessoryMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigAccessoryMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Accessory obj) {
		redPigAccessoryMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Accessory obj) {
		redPigAccessoryMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigAccessoryMapper.deleteById(id);
	}


	public Accessory selectByPrimaryKey(Long id) {
		return redPigAccessoryMapper.selectByPrimaryKey(id);
	}


	public List<Accessory> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Accessory> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigAccessoryMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	@Transactional(readOnly = false)
	public void batchDelete(List<Accessory> goodsPhotos) {
		redPigAccessoryMapper.batchDelete(goodsPhotos);
	}


	@Transactional(readOnly = false)
	public void save(Accessory photo) {
		redPigAccessoryMapper.save(photo);
	}


	@Transactional(readOnly = false)
	public void update(Accessory photo) {
		redPigAccessoryMapper.update(photo);
	}


	@Transactional(readOnly = false)
	public void delete(Long id) {
		redPigAccessoryMapper.delete(id);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

}
