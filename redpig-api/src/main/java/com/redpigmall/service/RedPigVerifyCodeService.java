package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.VerifyCode;
import com.redpigmall.dao.VerifyCodeMapper;
import com.redpigmall.service.RedPigVerifyCodeService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigVerifyCodeService extends BaseService<VerifyCode> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<VerifyCode> objs) {
		if (objs != null && objs.size() > 0) {
			redPigVerifyCodeMapper.batchDelete(objs);
		}
	}


	public VerifyCode getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<VerifyCode> objs = redPigVerifyCodeMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<VerifyCode> selectObjByProperty(Map<String, Object> maps) {
		return redPigVerifyCodeMapper.selectObjByProperty(maps);
	}


	public List<VerifyCode> queryPages(Map<String, Object> params) {
		return redPigVerifyCodeMapper.queryPages(params);
	}


	public List<VerifyCode> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigVerifyCodeMapper.queryPageListWithNoRelations(param);
	}


	public List<VerifyCode> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigVerifyCodeMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private VerifyCodeMapper redPigVerifyCodeMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigVerifyCodeMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(VerifyCode obj) {
		redPigVerifyCodeMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(VerifyCode obj) {
		if(obj!=null && obj.getId()!=null){
			redPigVerifyCodeMapper.updateById(obj);
		}else if(obj !=null && obj.getId() == null){
			redPigVerifyCodeMapper.saveEntity(obj);
		}
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigVerifyCodeMapper.deleteById(id);
	}


	public VerifyCode selectByPrimaryKey(Long id) {
		return redPigVerifyCodeMapper.selectByPrimaryKey(id);
	}


	public List<VerifyCode> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<VerifyCode> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigVerifyCodeMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
