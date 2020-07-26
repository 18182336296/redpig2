package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.ChattingLog;

public interface ChattingLogMapper extends SupperMapper {

	void batchDelete(List<ChattingLog> objs);

	List<ChattingLog> selectObjByProperty(Map<String, Object> maps);

	ChattingLog selectByPrimaryKey(Long id);

	List<ChattingLog> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<ChattingLog> queryByIds(List<Long> ids);

	List<ChattingLog> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(ChattingLog obj);

	void updateById(ChattingLog obj);

	void deleteById(@Param(value="id")Long id);
	
	List<ChattingLog> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<ChattingLog> queryPagesWithNoRelations(Map<String,Object> params);

	List<ChattingLog> queryPageListWithNoRelations(Map<String,Object> params);

	List<ChattingLog> queryPageListUnique(Map<String, Object> params);

}
