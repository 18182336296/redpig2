package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.dao.GoodsClassMapper;
import com.redpigmall.service.RedPigGoodsClassService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGoodsClassService extends BaseService<GoodsClass>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GoodsClass> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGoodsClassMapper.batchDelete(objs);
		}
	}


	public GoodsClass getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GoodsClass> objs = redPigGoodsClassMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GoodsClass> selectObjByProperty(Map<String, Object> maps) {
		return redPigGoodsClassMapper.selectObjByProperty(maps);
	}


	public List<GoodsClass> queryPages(Map<String, Object> params) {
		return redPigGoodsClassMapper.queryPages(params);
	}


	public List<GoodsClass> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGoodsClassMapper.queryPageListWithNoRelations(param);
	}


	public List<GoodsClass> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGoodsClassMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}

	@Autowired
	private GoodsClassMapper redPigGoodsClassMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGoodsClassMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GoodsClass obj) {
		redPigGoodsClassMapper.save(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GoodsClass obj) {
		redPigGoodsClassMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGoodsClassMapper.deleteById(id);
	}


	public GoodsClass selectByPrimaryKey(Long id) {
		return redPigGoodsClassMapper.selectByPrimaryKey(id);
	}


	public List<GoodsClass> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GoodsClass> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGoodsClassMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<GoodsClass> queryPageListByParentIsNull(Map<String, Object> params, int begin, int max) {
		return redPigGoodsClassMapper.queryPageListByParentIsNull(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}


	@Transactional(readOnly = false)
	public void update(GoodsClass gc) {
		redPigGoodsClassMapper.update(gc);
	}


	@Transactional(readOnly = false)
	public void save(GoodsClass goodsClass) {
		redPigGoodsClassMapper.save(goodsClass);
	}


	public List<GoodsClass> queryGoodsClassByIds(Map<String, Object> params) {
		return redPigGoodsClassMapper.queryGoodsClassByIds(params);
	}

	@Transactional(readOnly = false)

	public void removeGoodsType(Map<String, Object> maps) {
		redPigGoodsClassMapper.removeGoodsType(maps);
	}

	@Transactional(readOnly = false)

	public void removeGoodsSpecification(Map<String, Object> maps) {
		redPigGoodsClassMapper.removeGoodsSpecification(maps);
	}

	@Transactional(readOnly = false)

	public void removeClilds(Map<String, Object> maps) {
		redPigGoodsClassMapper.removeClilds(maps);
	}


	@Transactional(readOnly = false)
	public void delete(Long id) {
		redPigGoodsClassMapper.delete(id);
	}


	public List<Map<String, Object>> queryListWithNoRelations(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return redPigGoodsClassMapper.queryListWithNoRelations(params);
	}

}
