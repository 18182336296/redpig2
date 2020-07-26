package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.Transport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.TransportMapper;
import com.redpigmall.service.RedPigTransportService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigTransportService extends BaseService<Transport>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Transport> objs) {
		if (objs != null && objs.size() > 0) {
			redPigTransportMapper.batchDelete(objs);
		}
	}


	public Transport getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Transport> objs = redPigTransportMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Transport> selectObjByProperty(Map<String, Object> maps) {
		return redPigTransportMapper.selectObjByProperty(maps);
	}


	public List<Transport> queryPages(Map<String, Object> params) {
		return redPigTransportMapper.queryPages(params);
	}


	public List<Transport> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigTransportMapper.queryPageListWithNoRelations(param);
	}


	public List<Transport> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigTransportMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private TransportMapper redPigTransportMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigTransportMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Transport obj) {
		redPigTransportMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Transport obj) {
		redPigTransportMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigTransportMapper.deleteById(id);
	}


	public Transport selectByPrimaryKey(Long id) {
		return redPigTransportMapper.selectByPrimaryKey(id);
	}


	public List<Transport> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Transport> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigTransportMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}

}
