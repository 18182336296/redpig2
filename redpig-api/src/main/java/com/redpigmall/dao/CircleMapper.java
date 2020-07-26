package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Circle;

public interface CircleMapper extends SupperMapper {

	void batchDelete(List<Circle> objs);

	List<Circle> selectObjByProperty(Map<String, Object> maps);

	Circle selectByPrimaryKey(Long id);

	List<Circle> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Circle> queryByIds(List<Long> ids);

	List<Circle> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Circle obj);

	void updateById(Circle obj);

	void deleteById(@Param(value="id")Long id);
	List<Circle> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Circle> queryPagesWithNoRelations(Map<String,Object> params);

	List<Circle> queryPageListWithNoRelations(Map<String,Object> params);

}
