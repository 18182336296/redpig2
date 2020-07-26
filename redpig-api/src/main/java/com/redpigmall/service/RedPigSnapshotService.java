package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.Snapshot;
import com.redpigmall.dao.SnapshotMapper;
import com.redpigmall.service.RedPigSnapshotService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigSnapshotService extends BaseService<Snapshot>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Snapshot> objs) {
		if (objs != null && objs.size() > 0) {
			redPigSnapshotMapper.batchDelete(objs);
		}
	}


	public Snapshot getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Snapshot> objs = redPigSnapshotMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Snapshot> selectObjByProperty(Map<String, Object> maps) {
		return redPigSnapshotMapper.selectObjByProperty(maps);
	}


	public List<Snapshot> queryPages(Map<String, Object> params) {
		return redPigSnapshotMapper.queryPages(params);
	}


	public List<Snapshot> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigSnapshotMapper.queryPageListWithNoRelations(param);
	}


	public List<Snapshot> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigSnapshotMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private SnapshotMapper redPigSnapshotMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigSnapshotMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Snapshot obj) {
		redPigSnapshotMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Snapshot obj) {
		redPigSnapshotMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigSnapshotMapper.deleteById(id);
	}


	public Snapshot selectByPrimaryKey(Long id) {
		return redPigSnapshotMapper.selectByPrimaryKey(id);
	}


	public List<Snapshot> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Snapshot> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigSnapshotMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
