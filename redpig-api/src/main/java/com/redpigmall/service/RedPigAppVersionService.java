package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.AppVersion;
import com.redpigmall.dao.AppVersionMapper;
import com.redpigmall.service.RedPigAppVersionService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigAppVersionService extends BaseService<AppVersion>  {

	@Autowired
	private AppVersionMapper appVersionMapper;

	public AppVersion selectByPrimaryKey(Long id) {
		// TODO Auto-generated method stub
		return appVersionMapper.selectByPrimaryKey(id);
	}


	public List<AppVersion> queryPages(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return appVersionMapper.queryPages(params);
	}


	public List<AppVersion> queryPageListWithNoRelations(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return appVersionMapper.queryPageListWithNoRelations(params);
	}


	public int selectCount(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return appVersionMapper.selectCount(params);
	}
	

	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}


	@Transactional(readOnly = false)
	public int insertSelective(AppVersion version) {
		// TODO Auto-generated method stub
		return appVersionMapper.insertSelective(version);
	}


	@Transactional(readOnly = false)
	public int updateByPrimaryKeySelective(AppVersion version) {
		// TODO Auto-generated method stub
		return appVersionMapper.updateByPrimaryKeySelective(version);
	}

}
