package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.ChattingConfig;

public interface ChattingConfigMapper extends SupperMapper {

	void batchDelete(List<ChattingConfig> objs);

	List<ChattingConfig> selectObjByProperty(Map<String, Object> maps);

	ChattingConfig selectByPrimaryKey(Long id);

	List<ChattingConfig> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<ChattingConfig> queryByIds(List<Long> ids);

	List<ChattingConfig> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(ChattingConfig obj);

	void updateById(ChattingConfig obj);

	void deleteById(@Param(value="id")Long id);
	List<ChattingConfig> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<ChattingConfig> queryPagesWithNoRelations(Map<String,Object> params);

	List<ChattingConfig> queryPageListWithNoRelations(Map<String,Object> params);

}
