package com.redpigmall.service;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.LuckydrawRecordMapper;
import com.redpigmall.domain.LuckydrawRecord;
import com.redpigmall.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class RedPigLuckydrawRecordService extends BaseService<LuckydrawRecord>  {

	@Transactional(readOnly = false)
	public void batchDelObjs(List<LuckydrawRecord> objs) {
		if (objs != null && objs.size() > 0) {
			luckydrawRecordMapper.batchDelete(objs);
		}
	}

	public LuckydrawRecord getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<LuckydrawRecord> objs = luckydrawRecordMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<LuckydrawRecord> selectObjByProperty(Map<String, Object> maps) {
		return luckydrawRecordMapper.selectObjByProperty(maps);
	}


	public List<LuckydrawRecord> queryPages(Map<String, Object> params) {
		return luckydrawRecordMapper.queryPages(params);
	}


	public List<LuckydrawRecord> queryPageListWithNoRelations(Map<String, Object> param) {
		return luckydrawRecordMapper.queryPageListWithNoRelations(param);
	}


	public List<LuckydrawRecord> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return luckydrawRecordMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private LuckydrawRecordMapper luckydrawRecordMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		luckydrawRecordMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(LuckydrawRecord obj) {
		luckydrawRecordMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(LuckydrawRecord obj) {
		luckydrawRecordMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		luckydrawRecordMapper.deleteById(id);
	}


	public LuckydrawRecord selectByPrimaryKey(Long id) {
		return luckydrawRecordMapper.selectByPrimaryKey(id);
	}


	public List<LuckydrawRecord> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<LuckydrawRecord> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = luckydrawRecordMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
