package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.domain.ComplaintSubject;
import com.redpigmall.dao.ComplaintSubjectMapper;
import com.redpigmall.service.RedPigComplaintSubjectService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigComplaintSubjectService extends BaseService<ComplaintSubject> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<ComplaintSubject> objs) {
		if (objs != null && objs.size() > 0) {
			redPigComplaintSubjectMapper.batchDelete(objs);
		}
	}


	public ComplaintSubject getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<ComplaintSubject> objs = redPigComplaintSubjectMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<ComplaintSubject> selectObjByProperty(Map<String, Object> maps) {
		return redPigComplaintSubjectMapper.selectObjByProperty(maps);
	}


	public List<ComplaintSubject> queryPages(Map<String, Object> params) {
		return redPigComplaintSubjectMapper.queryPages(params);
	}


	public List<ComplaintSubject> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigComplaintSubjectMapper.queryPageListWithNoRelations(param);
	}


	public List<ComplaintSubject> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigComplaintSubjectMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private ComplaintSubjectMapper redPigComplaintSubjectMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		if (RedPigCommonUtil.isNotNull(ids)) {
			redPigComplaintSubjectMapper.batchDeleteByIds(ids);
		}
	}


	@Transactional(readOnly = false)
	public void saveEntity(ComplaintSubject obj) {
		redPigComplaintSubjectMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(ComplaintSubject obj) {
		redPigComplaintSubjectMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigComplaintSubjectMapper.deleteById(id);
	}


	public ComplaintSubject selectByPrimaryKey(Long id) {
		return redPigComplaintSubjectMapper.selectByPrimaryKey(id);
	}


	public List<ComplaintSubject> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<ComplaintSubject> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigComplaintSubjectMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
