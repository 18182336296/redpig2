package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.Complaint;
import com.redpigmall.dao.ComplaintMapper;
import com.redpigmall.service.RedPigComplaintService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigComplaintService extends BaseService<Complaint> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Complaint> objs) {
		if (objs != null && objs.size() > 0) {
			redPigComplaintMapper.batchDelete(objs);
		}
	}


	public Complaint getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Complaint> objs = redPigComplaintMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Complaint> selectObjByProperty(Map<String, Object> maps) {
		return redPigComplaintMapper.selectObjByProperty(maps);
	}


	public List<Complaint> queryPages(Map<String, Object> params) {
		return redPigComplaintMapper.queryPages(params);
	}


	public List<Complaint> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigComplaintMapper.queryPageListWithNoRelations(param);
	}


	public List<Complaint> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigComplaintMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private ComplaintMapper redPigComplaintMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigComplaintMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Complaint obj) {
		redPigComplaintMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Complaint obj) {
		redPigComplaintMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigComplaintMapper.deleteById(id);
	}


	public Complaint selectByPrimaryKey(Long id) {
		return redPigComplaintMapper.selectByPrimaryKey(id);
	}


	public List<Complaint> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Complaint> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigComplaintMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public void saveComplaintAndComplaintGoods(Map<String, Object> params) {
	}

}
