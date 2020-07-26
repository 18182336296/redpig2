package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.LogFieldType;

public interface LogFieldTypeMapper extends SupperMapper {

	LogFieldType selectByPrimaryKey(Long id);

	List<LogFieldType> queryPageList(Map<String, Object> maps);

}