package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.AppOperativeVersion;

public interface AppOperativeVersionMapper extends SupperMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AppOperativeVersion record);

    int insertSelective(AppOperativeVersion record);

    AppOperativeVersion selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AppOperativeVersion record);

    int updateByPrimaryKey(AppOperativeVersion record);

	List<AppOperativeVersion> queryPages(Map<String, Object> params);

	List<AppOperativeVersion> queryPageListWithNoRelations(Map<String, Object> params);

	AppOperativeVersion getLatestVersion();

}