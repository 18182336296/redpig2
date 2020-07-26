package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.redpigmall.dao.LogTypeMapper;
import com.redpigmall.service.RedPigLogTypeService;

@Service
@Transactional(readOnly = true)
public class RedPigLogTypeService  {

	@Autowired
	private LogTypeMapper redPigLogTypeMapper;


	public LogType selectByPrimaryKey(Long id) {
		return redPigLogTypeMapper.selectByPrimaryKey(id);
	}


	public List<LogType> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return redPigLogTypeMapper.queryPageList(params);
	}


	public List<LogType> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		return redPigLogTypeMapper.selectCount(params);
	}
}
