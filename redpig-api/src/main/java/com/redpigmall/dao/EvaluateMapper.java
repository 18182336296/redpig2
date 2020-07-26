package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Evaluate;

public interface EvaluateMapper extends SupperMapper {

	void batchDelete(List<Evaluate> objs);

	List<Evaluate> selectObjByProperty(Map<String, Object> maps);

	Evaluate selectByPrimaryKey(Long id);

	List<Evaluate> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Evaluate> queryByIds(List<Long> ids);

	List<Evaluate> queryPageListByParentIsNull(Map<String, Object> params);

	void delete(Evaluate evaluate);

	void batchDeleteEvaluates(List<Evaluate> evaluates);

	
	void saveEntity(Evaluate obj);

	void updateById(Evaluate obj);

	void deleteById(@Param(value="id")Long id);
	List<Evaluate> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Evaluate> queryPagesWithNoRelations(Map<String,Object> params);

	List<Evaluate> queryPageListWithNoRelations(Map<String,Object> params);

}
