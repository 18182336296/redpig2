package com.redpigmall.service;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.LuckydrawRewardMapper;
import com.redpigmall.domain.LuckydrawReward;
import com.redpigmall.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class RedPigLuckydrawRewardService extends BaseService<LuckydrawReward>  {

	@Transactional(readOnly = false)
	public void batchDelObjs(List<LuckydrawReward> objs) {
		if (objs != null && objs.size() > 0) {
			luckydrawRewardMapper.batchDelete(objs);
		}
	}

	public LuckydrawReward getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<LuckydrawReward> objs = luckydrawRewardMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<LuckydrawReward> selectObjByProperty(Map<String, Object> maps) {
		return luckydrawRewardMapper.selectObjByProperty(maps);
	}


	public List<LuckydrawReward> queryPages(Map<String, Object> params) {
		return luckydrawRewardMapper.queryPages(params);
	}


	public List<LuckydrawReward> queryPageListWithNoRelations(Map<String, Object> param) {
		return luckydrawRewardMapper.queryPageListWithNoRelations(param);
	}


	public List<LuckydrawReward> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return luckydrawRewardMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private LuckydrawRewardMapper luckydrawRewardMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		luckydrawRewardMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(LuckydrawReward obj) {
		luckydrawRewardMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(LuckydrawReward obj) {
		luckydrawRewardMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		luckydrawRewardMapper.deleteById(id);
	}


	public LuckydrawReward selectByPrimaryKey(Long id) {
		return luckydrawRewardMapper.selectByPrimaryKey(id);
	}


	public List<LuckydrawReward> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<LuckydrawReward> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = luckydrawRewardMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
