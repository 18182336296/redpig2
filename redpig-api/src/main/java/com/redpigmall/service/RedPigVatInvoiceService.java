package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.VatInvoice;
import com.redpigmall.dao.VatInvoiceMapper;
import com.redpigmall.service.RedPigVatInvoiceService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigVatInvoiceService extends BaseService<VatInvoice> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<VatInvoice> objs) {
		if (objs != null && objs.size() > 0) {
			redPigVatInvoiceMapper.batchDelete(objs);
		}
	}


	public VatInvoice getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<VatInvoice> objs = redPigVatInvoiceMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<VatInvoice> selectObjByProperty(Map<String, Object> maps) {
		return redPigVatInvoiceMapper.selectObjByProperty(maps);
	}


	public List<VatInvoice> queryPages(Map<String, Object> params) {
		return redPigVatInvoiceMapper.queryPages(params);
	}


	public List<VatInvoice> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigVatInvoiceMapper.queryPageListWithNoRelations(param);
	}


	public List<VatInvoice> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigVatInvoiceMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private VatInvoiceMapper redPigVatInvoiceMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigVatInvoiceMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(VatInvoice obj) {
		redPigVatInvoiceMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(VatInvoice obj) {
		redPigVatInvoiceMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigVatInvoiceMapper.deleteById(id);
	}


	public VatInvoice selectByPrimaryKey(Long id) {
		return redPigVatInvoiceMapper.selectByPrimaryKey(id);
	}


	public List<VatInvoice> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<VatInvoice> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigVatInvoiceMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
