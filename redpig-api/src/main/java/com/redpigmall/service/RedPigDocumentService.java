package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.DocumentMapper;
import com.redpigmall.service.RedPigDocumentService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigDocumentService extends BaseService<Document>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Document> objs) {
		if (objs != null && objs.size() > 0) {
			redPigDocumentMapper.batchDelete(objs);
		}
	}


	public Document getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Document> objs = redPigDocumentMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Document> selectObjByProperty(Map<String, Object> maps) {
		return redPigDocumentMapper.selectObjByProperty(maps);
	}


	public List<Document> queryPages(Map<String, Object> params) {
		return redPigDocumentMapper.queryPages(params);
	}


	public List<Document> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigDocumentMapper.queryPageListWithNoRelations(param);
	}


	public List<Document> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigDocumentMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private DocumentMapper redPigDocumentMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigDocumentMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Document obj) {
		redPigDocumentMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Document obj) {
		redPigDocumentMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigDocumentMapper.deleteById(id);
	}


	public Document selectByPrimaryKey(Long id) {
		return redPigDocumentMapper.selectByPrimaryKey(id);
	}


	public List<Document> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Document> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigDocumentMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
