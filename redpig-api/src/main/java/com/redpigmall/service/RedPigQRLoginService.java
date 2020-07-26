package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.QRLogin;
import com.redpigmall.dao.QRLoginMapper;
import com.redpigmall.service.RedPigQRLoginService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigQRLoginService extends BaseService<QRLogin> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<QRLogin> objs) {
		if (objs != null && objs.size() > 0) {
			redPigQRLoginMapper.batchDelete(objs);
		}
	}


	public QRLogin getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<QRLogin> objs = redPigQRLoginMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<QRLogin> selectObjByProperty(Map<String, Object> maps) {
		return redPigQRLoginMapper.selectObjByProperty(maps);
	}


	public List<QRLogin> queryPages(Map<String, Object> params) {
		return redPigQRLoginMapper.queryPages(params);
	}


	public List<QRLogin> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigQRLoginMapper.queryPageListWithNoRelations(param);
	}


	public List<QRLogin> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigQRLoginMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private QRLoginMapper redPigQRLoginMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigQRLoginMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(QRLogin obj) {
		redPigQRLoginMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(QRLogin obj) {
		redPigQRLoginMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigQRLoginMapper.deleteById(id);
	}


	public QRLogin selectByPrimaryKey(Long id) {
		return redPigQRLoginMapper.selectByPrimaryKey(id);
	}


	public List<QRLogin> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<QRLogin> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigQRLoginMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
