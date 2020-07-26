package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GoldLog;

public interface GoldLogMapper extends SupperMapper {

	void batchDelete(List<GoldLog> objs);

	List<GoldLog> selectObjByProperty(Map<String, Object> maps);

	GoldLog selectByPrimaryKey(Long id);

	List<GoldLog> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GoldLog> queryByIds(List<Long> ids);

	List<GoldLog> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(GoldLog obj);

	void updateById(GoldLog obj);

	void deleteById(@Param(value="id")Long id);
	List<GoldLog> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GoldLog> queryPagesWithNoRelations(Map<String,Object> params);

	List<GoldLog> queryPageListWithNoRelations(Map<String,Object> params);

}
