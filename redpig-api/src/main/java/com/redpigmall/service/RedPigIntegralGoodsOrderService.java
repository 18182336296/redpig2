package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.IntegralGoods;
import com.redpigmall.domain.IntegralGoodsOrder;
import com.redpigmall.dao.IntegralGoodsMapper;
import com.redpigmall.dao.IntegralGoodsOrderMapper;
import com.redpigmall.service.RedPigIntegralGoodsOrderService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigIntegralGoodsOrderService extends BaseService<IntegralGoodsOrder>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<IntegralGoodsOrder> objs) {
		if (objs != null && objs.size() > 0) {
			redPigIntegralGoodsOrderMapper.batchDelete(objs);
		}
	}


	public IntegralGoodsOrder getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<IntegralGoodsOrder> objs = redPigIntegralGoodsOrderMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<IntegralGoodsOrder> selectObjByProperty(Map<String, Object> maps) {
		return redPigIntegralGoodsOrderMapper.selectObjByProperty(maps);
	}


	public List<IntegralGoodsOrder> queryPages(Map<String, Object> params) {
		return redPigIntegralGoodsOrderMapper.queryPages(params);
	}


	public List<IntegralGoodsOrder> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigIntegralGoodsOrderMapper.queryPageListWithNoRelations(param);
	}


	public List<IntegralGoodsOrder> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigIntegralGoodsOrderMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private IntegralGoodsOrderMapper redPigIntegralGoodsOrderMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigIntegralGoodsOrderMapper.batchDeleteByIds(ids);
	}

	@Autowired
	private IntegralGoodsMapper redPigIntegralGoodsMapper;


	@Transactional(readOnly = false)
	public void saveEntity(IntegralGoodsOrder obj) {
		redPigIntegralGoodsOrderMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(IntegralGoodsOrder obj) {
		redPigIntegralGoodsOrderMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigIntegralGoodsOrderMapper.deleteById(id);
	}


	public IntegralGoodsOrder selectByPrimaryKey(Long id) {
		return redPigIntegralGoodsOrderMapper.selectByPrimaryKey(id);
	}


	public List<IntegralGoodsOrder> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<IntegralGoodsOrder> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigIntegralGoodsOrderMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}

	@SuppressWarnings("rawtypes")

	public List<IntegralGoods> queryIntegralGoods(String order_id) {
		IntegralGoodsOrder igo = this.redPigIntegralGoodsOrderMapper.selectByPrimaryKey(CommUtil.null2Long(order_id));

		List<IntegralGoods> objs = Lists.newArrayList();
		List<Map> maps = JSON.parseArray(igo.getGoods_info(), Map.class);
		for (Map obj : maps) {
			IntegralGoods ig = this.redPigIntegralGoodsMapper.selectByPrimaryKey(CommUtil.null2Long(obj.get("id")));
			if (ig != null) {
				objs.add(ig);
			}
		}
		return objs;
	}

	@SuppressWarnings("rawtypes")

	public int queryIntegralOneGoodsCount(IntegralGoodsOrder igo, String ig_id) {
		int count = 0;
		List<Map> maps = JSON.parseArray(igo.getGoods_info(), Map.class);
		for (Map obj : maps) {
			if (CommUtil.null2String(obj.get("id")).equals(ig_id)) {
				count = CommUtil.null2Int(obj.get("ig_goods_count"));
				break;
			}
		}
		return count;
	}

}
