package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.domain.MerchantServices;
import com.redpigmall.dao.MerchantServicesMapper;
import com.redpigmall.service.RedPigMerchantServicesService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigMerchantServicesService extends BaseService<MerchantServices>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<MerchantServices> objs) {
		if (objs != null && objs.size() > 0) {
			redPigMerchantServicesMapper.batchDelete(objs);
		}
	}


	public MerchantServices getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<MerchantServices> objs = redPigMerchantServicesMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<MerchantServices> selectObjByProperty(Map<String, Object> maps) {
		return redPigMerchantServicesMapper.selectObjByProperty(maps);
	}


	public List<MerchantServices> queryPages(Map<String, Object> params) {
		return redPigMerchantServicesMapper.queryPages(params);
	}


	public List<MerchantServices> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigMerchantServicesMapper.queryPageListWithNoRelations(param);
	}


	public List<MerchantServices> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigMerchantServicesMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private MerchantServicesMapper redPigMerchantServicesMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigMerchantServicesMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(MerchantServices obj) {
		redPigMerchantServicesMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(MerchantServices obj) {
		redPigMerchantServicesMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigMerchantServicesMapper.deleteById(id);
	}


	public MerchantServices selectByPrimaryKey(Long id) {
		return redPigMerchantServicesMapper.selectByPrimaryKey(id);
	}


	public List<MerchantServices> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<MerchantServices> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigMerchantServicesMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	@Transactional(readOnly = false)
	public void batchDeleteByIds(String[] ids) {
		List<Long> listIds = Lists.newArrayList();

		for (String id : ids) {
			if (StringUtils.isNotBlank(id)) {
				listIds.add(Long.parseLong(id));
			}
		}

		redPigMerchantServicesMapper.batchDeleteByIds(listIds);

	}
}
