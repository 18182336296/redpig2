package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.RechargeCard;
import com.redpigmall.dao.RechargeCardMapper;
import com.redpigmall.service.RedPigRechargeCardService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigRechargeCardService extends BaseService<RechargeCard>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<RechargeCard> objs) {
		if (objs != null && objs.size() > 0) {
			redPigRechargeCardMapper.batchDelete(objs);
		}
	}


	public RechargeCard getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<RechargeCard> objs = redPigRechargeCardMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<RechargeCard> selectObjByProperty(Map<String, Object> maps) {
		return redPigRechargeCardMapper.selectObjByProperty(maps);
	}


	public List<RechargeCard> queryPages(Map<String, Object> params) {
		return redPigRechargeCardMapper.queryPages(params);
	}


	public List<RechargeCard> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigRechargeCardMapper.queryPageListWithNoRelations(param);
	}


	public List<RechargeCard> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigRechargeCardMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private RechargeCardMapper redPigRechargeCardMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigRechargeCardMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(RechargeCard obj) {
		redPigRechargeCardMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(RechargeCard obj) {
		redPigRechargeCardMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigRechargeCardMapper.deleteById(id);
	}


	public RechargeCard selectByPrimaryKey(Long id) {
		return redPigRechargeCardMapper.selectByPrimaryKey(id);
	}


	public List<RechargeCard> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<RechargeCard> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigRechargeCardMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}

}
