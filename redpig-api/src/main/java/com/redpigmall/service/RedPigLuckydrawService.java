package com.redpigmall.service;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.LuckydrawMapper;
import com.redpigmall.domain.Luckydraw;
import com.redpigmall.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class RedPigLuckydrawService extends BaseService<Luckydraw>  {

	@Transactional(readOnly = false)
	public void batchDelObjs(List<Luckydraw> objs) {
		if (objs != null && objs.size() > 0) {
			luckydrawMapper.batchDelete(objs);
		}
	}

	public Luckydraw getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Luckydraw> objs = luckydrawMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Luckydraw> selectObjByProperty(Map<String, Object> maps) {
		return luckydrawMapper.selectObjByProperty(maps);
	}


	public List<Luckydraw> queryPages(Map<String, Object> params) {
		return luckydrawMapper.queryPages(params);
	}


	public List<Luckydraw> queryPageListWithNoRelations(Map<String, Object> param) {
		return luckydrawMapper.queryPageListWithNoRelations(param);
	}


	public List<Luckydraw> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return luckydrawMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private LuckydrawMapper luckydrawMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		luckydrawMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Luckydraw obj) {
		luckydrawMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Luckydraw obj) {
		luckydrawMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		luckydrawMapper.deleteById(id);
	}


	public Luckydraw selectByPrimaryKey(Long id) {
		return luckydrawMapper.selectByPrimaryKey(id);
	}


	public List<Luckydraw> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Luckydraw> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = luckydrawMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
