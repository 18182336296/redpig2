package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.WeixinChannel;
import com.redpigmall.dao.WeixinChannelMapper;
import com.redpigmall.service.RedPigWeixinChannelService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigWeixinChannelService extends BaseService<WeixinChannel> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<WeixinChannel> objs) {
		if (objs != null && objs.size() > 0) {
			redPigWeixinChannelMapper.batchDelete(objs);
		}
	}


	public WeixinChannel getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<WeixinChannel> objs = redPigWeixinChannelMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<WeixinChannel> selectObjByProperty(Map<String, Object> maps) {
		return redPigWeixinChannelMapper.selectObjByProperty(maps);
	}


	public List<WeixinChannel> queryPages(Map<String, Object> params) {
		return redPigWeixinChannelMapper.queryPages(params);
	}


	public List<WeixinChannel> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigWeixinChannelMapper.queryPageListWithNoRelations(param);
	}


	public List<WeixinChannel> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigWeixinChannelMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private WeixinChannelMapper redPigWeixinChannelMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigWeixinChannelMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(WeixinChannel obj) {
		redPigWeixinChannelMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(WeixinChannel obj) {
		redPigWeixinChannelMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigWeixinChannelMapper.deleteById(id);
	}


	public WeixinChannel selectByPrimaryKey(Long id) {
		return redPigWeixinChannelMapper.selectByPrimaryKey(id);
	}


	public List<WeixinChannel> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<WeixinChannel> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigWeixinChannelMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
