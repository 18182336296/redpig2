package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.SmsGoldLog;

public interface SmsGoldLogMapper extends SupperMapper {

	void batchDelete(List<SmsGoldLog> objs);

	List<SmsGoldLog> selectObjByProperty(Map<String, Object> maps);

	SmsGoldLog selectByPrimaryKey(Long id);

	List<SmsGoldLog> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<SmsGoldLog> queryByIds(List<Long> ids);

	List<SmsGoldLog> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(SmsGoldLog obj);

	void updateById(SmsGoldLog obj);

	void deleteById(@Param(value="id")Long id);

	List<SmsGoldLog> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<SmsGoldLog> queryPagesWithNoRelations(Map<String,Object> params);

	List<SmsGoldLog> queryPageListWithNoRelations(Map<String,Object> params);

}
