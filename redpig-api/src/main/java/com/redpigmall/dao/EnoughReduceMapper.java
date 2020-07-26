package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.EnoughReduce;

public interface EnoughReduceMapper extends SupperMapper {

	void batchDelete(List<EnoughReduce> objs);

	List<EnoughReduce> selectObjByProperty(Map<String, Object> maps);

	EnoughReduce selectByPrimaryKey(Long id);

	List<EnoughReduce> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<EnoughReduce> queryByIds(List<Long> ids);

	List<EnoughReduce> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(EnoughReduce obj);

	void updateById(EnoughReduce obj);

	void deleteById(@Param(value="id")Long id);
	List<EnoughReduce> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<EnoughReduce> queryPagesWithNoRelations(Map<String,Object> params);

	List<EnoughReduce> queryPageListWithNoRelations(Map<String,Object> params);

}
