package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Praise;

public interface PraiseMapper extends SupperMapper {

	void batchDelete(List<Praise> objs);

	List<Praise> selectObjByProperty(Map<String, Object> maps);

	Praise selectByPrimaryKey(Long id);

	List<Praise> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Praise> queryByIds(List<Long> ids);

	List<Praise> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Praise obj);

	void updateById(Praise obj);

	void deleteById(@Param(value="id")Long id);
	List<Praise> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Praise> queryPagesWithNoRelations(Map<String,Object> params);

	List<Praise> queryPageListWithNoRelations(Map<String,Object> params);

}
