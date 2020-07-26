package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.ComplaintGoods;
import com.redpigmall.dao.ComplaintGoodsMapper;
import com.redpigmall.service.RedPigComplaintGoodsService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigComplaintGoodsService extends BaseService<ComplaintGoods> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<ComplaintGoods> objs) {
		if (objs != null && objs.size() > 0) {
			redPigComplaintGoodsMapper.batchDelete(objs);
		}
	}


	public ComplaintGoods getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<ComplaintGoods> objs = redPigComplaintGoodsMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<ComplaintGoods> selectObjByProperty(Map<String, Object> maps) {
		return redPigComplaintGoodsMapper.selectObjByProperty(maps);
	}


	public List<ComplaintGoods> queryPages(Map<String, Object> params) {
		return redPigComplaintGoodsMapper.queryPages(params);
	}


	public List<ComplaintGoods> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigComplaintGoodsMapper.queryPageListWithNoRelations(param);
	}


	public List<ComplaintGoods> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigComplaintGoodsMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private ComplaintGoodsMapper redPigComplaintGoodsMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigComplaintGoodsMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(ComplaintGoods obj) {
		redPigComplaintGoodsMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(ComplaintGoods obj) {
		redPigComplaintGoodsMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigComplaintGoodsMapper.deleteById(id);
	}


	public ComplaintGoods selectByPrimaryKey(Long id) {
		return redPigComplaintGoodsMapper.selectByPrimaryKey(id);
	}


	public List<ComplaintGoods> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<ComplaintGoods> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigComplaintGoodsMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
	
	@Transactional(readOnly = false)

	public void batchInsert(List<ComplaintGoods> cgs) {
		for (ComplaintGoods complaintGoods : cgs) {
			redPigComplaintGoodsMapper.saveEntity(complaintGoods);
		}
	}
}
