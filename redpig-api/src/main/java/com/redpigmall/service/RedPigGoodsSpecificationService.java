package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.GoodsSpecification;
import com.redpigmall.dao.GoodsSpecificationMapper;
import com.redpigmall.service.RedPigGoodsSpecificationService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGoodsSpecificationService extends BaseService<GoodsSpecification> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GoodsSpecification> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGoodsSpecificationMapper.batchDelete(objs);
		}
	}


	public GoodsSpecification getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GoodsSpecification> objs = redPigGoodsSpecificationMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GoodsSpecification> selectObjByProperty(Map<String, Object> maps) {
		return redPigGoodsSpecificationMapper.selectObjByProperty(maps);
	}


	public List<GoodsSpecification> queryPages(Map<String, Object> params) {
		return redPigGoodsSpecificationMapper.queryPages(params);
	}


	public List<GoodsSpecification> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGoodsSpecificationMapper.queryPageListWithNoRelations(param);
	}


	public List<GoodsSpecification> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGoodsSpecificationMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private GoodsSpecificationMapper redPigGoodsSpecificationMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGoodsSpecificationMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GoodsSpecification obj) {
		redPigGoodsSpecificationMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GoodsSpecification obj) {
		redPigGoodsSpecificationMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGoodsSpecificationMapper.deleteById(id);
	}


	public GoodsSpecification selectByPrimaryKey(Long id) {
		return redPigGoodsSpecificationMapper.selectByPrimaryKey(id);
	}


	public List<GoodsSpecification> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GoodsSpecification> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGoodsSpecificationMapper.selectCount(params);
		if (c == null) {
			return 0;
		}
		return c;
	}


	@Transactional(readOnly = false)
	public boolean save(GoodsSpecification goodsSpecification) {
		int ret = redPigGoodsSpecificationMapper.save(goodsSpecification);
		if (ret == 1) {
			return true;
		}
		return false;
	}


	@Transactional(readOnly = false)
	public void update(GoodsSpecification goodsSpecification) {
		redPigGoodsSpecificationMapper.update(goodsSpecification);
	}


	@Transactional(readOnly = false)
	public void delete(Long id) {
		redPigGoodsSpecificationMapper.delete(id);
	}
	
	@Transactional(readOnly = false)

	public void saveGoodsSpecificationGoodsClassDetail(List<Map<String, Object>> gspgcIds) {
		if(gspgcIds!=null && gspgcIds.size()>0){
			redPigGoodsSpecificationMapper.saveGoodsSpecificationGoodsClassDetail(gspgcIds);
		}
	}
	
	@Transactional(readOnly = false)

	public void deleteGoodsSpecificationGoodsClassDetail(List<Map<String, Object>> gsgcds) {
		if(gsgcds!=null && gsgcds.size()>0){
			redPigGoodsSpecificationMapper.deleteGoodsSpecificationGoodsClassDetail(gsgcds);
		}
	}
}
