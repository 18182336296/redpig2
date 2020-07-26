package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Res;

public interface ResMapper extends SupperMapper {

	void batchDelete(List<Res> objs);

	List<Res> selectObjByProperty(Map<String, Object> maps);

	Res selectByPrimaryKey(Long id);

	List<Res> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Res> queryByIds(List<Long> ids);

	List<Res> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Res obj);

	void updateById(Res obj);

	void deleteById(@Param(value="id")Long id);
	List<Res> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Res> queryPagesWithNoRelations(Map<String,Object> params);

	List<Res> queryPageListWithNoRelations(Map<String,Object> params);

	List<String> queryResLists(Long id);

}
