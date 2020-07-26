package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.BuyGift;
import com.redpigmall.dao.BuyGiftMapper;
import com.redpigmall.service.RedPigBuyGiftService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigBuyGiftService extends BaseService<BuyGift> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<BuyGift> objs) {
		if (objs != null && objs.size() > 0) {
			redPigBuyGiftMapper.batchDelete(objs);
		}
	}


	public BuyGift getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<BuyGift> objs = redPigBuyGiftMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<BuyGift> selectObjByProperty(Map<String, Object> maps) {
		return redPigBuyGiftMapper.selectObjByProperty(maps);
	}


	public List<BuyGift> queryPages(Map<String, Object> params) {
		return redPigBuyGiftMapper.queryPages(params);
	}


	public List<BuyGift> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigBuyGiftMapper.queryPageListWithNoRelations(param);
	}


	public List<BuyGift> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigBuyGiftMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private BuyGiftMapper redPigBuyGiftMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigBuyGiftMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(BuyGift obj) {
		redPigBuyGiftMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(BuyGift obj) {
		redPigBuyGiftMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigBuyGiftMapper.deleteById(id);
	}


	public BuyGift selectByPrimaryKey(Long id) {
		return redPigBuyGiftMapper.selectByPrimaryKey(id);
	}


	public List<BuyGift> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<BuyGift> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigBuyGiftMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
