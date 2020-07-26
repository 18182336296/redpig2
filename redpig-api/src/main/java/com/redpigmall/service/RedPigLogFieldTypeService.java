package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.LogFieldType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.redpigmall.dao.LogFieldTypeMapper;
import com.redpigmall.service.RedPigLogFieldTypeService;

@Service
@Transactional(readOnly = true)
public class RedPigLogFieldTypeService  {

	@Autowired
	private LogFieldTypeMapper redPigLogFieldTypeMapper;


	public LogFieldType selectByPrimaryKey(Long id) {
		return redPigLogFieldTypeMapper.selectByPrimaryKey(id);
	}


	public List<LogFieldType> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return redPigLogFieldTypeMapper.queryPageList(params);
	}


	public List<LogFieldType> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		return redPigLogFieldTypeMapper.selectCount(params);
	}
}
