package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.CircleClass;

public interface CircleClassMapper extends SupperMapper {

	void batchDelete(List<CircleClass> objs);

	List<CircleClass> selectObjByProperty(Map<String, Object> maps);

	CircleClass selectByPrimaryKey(Long id);

	List<CircleClass> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<CircleClass> queryByIds(List<Long> ids);

	List<CircleClass> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(CircleClass obj);

	void updateById(CircleClass obj);

	void deleteById(@Param(value="id")Long id);
	List<CircleClass> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<CircleClass> queryPagesWithNoRelations(Map<String,Object> params);

	List<CircleClass> queryPageListWithNoRelations(Map<String,Object> params);

}
