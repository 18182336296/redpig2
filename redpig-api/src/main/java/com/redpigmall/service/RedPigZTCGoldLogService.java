package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.ZTCGoldLog;
import com.redpigmall.dao.ZTCGoldLogMapper;
import com.redpigmall.service.RedPigZTCGoldLogService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigZTCGoldLogService extends BaseService<ZTCGoldLog> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<ZTCGoldLog> objs) {
		if (objs != null && objs.size() > 0) {
			redPigZTCGoldLogMapper.batchDelete(objs);
		}
	}


	public ZTCGoldLog getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<ZTCGoldLog> objs = redPigZTCGoldLogMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<ZTCGoldLog> selectObjByProperty(Map<String, Object> maps) {
		return redPigZTCGoldLogMapper.selectObjByProperty(maps);
	}


	public List<ZTCGoldLog> queryPages(Map<String, Object> params) {
		return redPigZTCGoldLogMapper.queryPages(params);
	}


	public List<ZTCGoldLog> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigZTCGoldLogMapper.queryPageListWithNoRelations(param);
	}


	public List<ZTCGoldLog> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigZTCGoldLogMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private ZTCGoldLogMapper redPigZTCGoldLogMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigZTCGoldLogMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(ZTCGoldLog obj) {
		redPigZTCGoldLogMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(ZTCGoldLog obj) {
		redPigZTCGoldLogMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigZTCGoldLogMapper.deleteById(id);
	}


	public ZTCGoldLog selectByPrimaryKey(Long id) {
		return redPigZTCGoldLogMapper.selectByPrimaryKey(id);
	}


	public List<ZTCGoldLog> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<ZTCGoldLog> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigZTCGoldLogMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}

	@Transactional(readOnly = false)

	public void deleteByGoodsId(Long id) {
		if (id != null && id > 0) {
			redPigZTCGoldLogMapper.deleteByGoodsId(id);
		}
	}
}
