package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.LogType;

public interface LogTypeMapper extends SupperMapper {

	LogType selectByPrimaryKey(Long id);
	
	List<LogType> queryPageList(Map<String, Object> maps);

}