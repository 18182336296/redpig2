package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.dao.ChannelMapper;
import com.redpigmall.service.RedPigChannelService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigChannelService extends BaseService<Channel>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Channel> objs) {
		if (objs != null && objs.size() > 0) {
			redPigChannelMapper.batchDelete(objs);
		}
	}


	public Channel getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Channel> objs = redPigChannelMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Channel> selectObjByProperty(Map<String, Object> maps) {
		return redPigChannelMapper.selectObjByProperty(maps);
	}


	public List<Channel> queryPages(Map<String, Object> params) {
		return redPigChannelMapper.queryPages(params);
	}


	public List<Channel> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigChannelMapper.queryPageListWithNoRelations(param);
	}


	public List<Channel> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigChannelMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}

	@Autowired
	private ChannelMapper redPigChannelMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		if (RedPigCommonUtil.isNotNull(ids)) {
			redPigChannelMapper.batchDeleteByIds(ids);
		}
	}


	@Transactional(readOnly = false)
	public void saveEntity(Channel obj) {
		redPigChannelMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Channel obj) {
		redPigChannelMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigChannelMapper.deleteById(id);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}


	public Channel selectByPrimaryKey(Long id) {
		return redPigChannelMapper.selectByPrimaryKey(id);
	}


	public List<Channel> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Channel> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigChannelMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
