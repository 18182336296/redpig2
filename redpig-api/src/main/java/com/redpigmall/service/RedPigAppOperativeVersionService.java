package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.AppOperativeVersion;
import com.redpigmall.dao.AppOperativeVersionMapper;
import com.redpigmall.service.RedPigAppOperativeVersionService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigAppOperativeVersionService extends BaseService<AppOperativeVersion> {

	@Autowired
	private AppOperativeVersionMapper appOperativeVersionMapper;

	@SuppressWarnings("rawtypes")
	public List queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public AppOperativeVersion selectByPrimaryKey(Long id) {
		return appOperativeVersionMapper.selectByPrimaryKey(id);
	}


	public List<AppOperativeVersion> queryPages(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return appOperativeVersionMapper.queryPages(params);
	}


	public List<AppOperativeVersion> queryPageListWithNoRelations(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return appOperativeVersionMapper.queryPageListWithNoRelations(params);
	}


	public int selectCount(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return appOperativeVersionMapper.selectCount(params);
	}

	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}


	@Transactional(readOnly = false)
	public int updateByPrimaryKeySelective(AppOperativeVersion version) {
		// TODO Auto-generated method stub
		return appOperativeVersionMapper.updateByPrimaryKeySelective(version);
	}


	@Transactional(readOnly = false)
	public int insertSelective(AppOperativeVersion version) {
		// TODO Auto-generated method stub
		return appOperativeVersionMapper.insertSelective(version);
	}


	public AppOperativeVersion getLatestVersion() {
		// TODO Auto-generated method stub
		return appOperativeVersionMapper.getLatestVersion();
	}

}
