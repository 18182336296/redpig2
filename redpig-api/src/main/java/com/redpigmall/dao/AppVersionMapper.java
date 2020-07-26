package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.AppVersion;

public interface AppVersionMapper extends SupperMapper{
    int deleteByPrimaryKey(Long id);

    int insert(AppVersion record);

    int insertSelective(AppVersion record);

    AppVersion selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AppVersion record);

    int updateByPrimaryKey(AppVersion record);

	List<AppVersion> queryPages(Map<String, Object> params);

	List<AppVersion> queryPageListWithNoRelations(Map<String, Object> params);

}