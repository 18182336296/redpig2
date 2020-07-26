package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.FootPoint;

public interface FootPointMapper extends SupperMapper {

	void batchDelete(List<FootPoint> objs);

	List<FootPoint> selectObjByProperty(Map<String, Object> maps);

	FootPoint selectByPrimaryKey(Long id);

	List<FootPoint> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<FootPoint> queryByIds(List<Long> ids);

	List<FootPoint> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(FootPoint obj);

	void updateById(FootPoint obj);

	void deleteById(@Param(value="id")Long id);
	List<FootPoint> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<FootPoint> queryPagesWithNoRelations(Map<String,Object> params);

	List<FootPoint> queryPageListWithNoRelations(Map<String,Object> params);

}
