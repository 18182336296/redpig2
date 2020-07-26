package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.StoreGrade;

public interface StoreGradeMapper extends SupperMapper {

	void batchDelete(List<StoreGrade> objs);

	List<StoreGrade> selectObjByProperty(Map<String, Object> maps);

	StoreGrade selectByPrimaryKey(Long id);

	List<StoreGrade> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<StoreGrade> queryByIds(List<Long> ids);

	List<StoreGrade> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(StoreGrade obj);

	void updateById(StoreGrade obj);

	void deleteById(@Param(value="id")Long id);
	List<StoreGrade> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<StoreGrade> queryPagesWithNoRelations(Map<String,Object> params);

	List<StoreGrade> queryPageListWithNoRelations(Map<String,Object> params);

}
