package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.CombinPlan;

public interface CombinPlanMapper extends SupperMapper {

	void batchDelete(List<CombinPlan> objs);

	List<CombinPlan> selectObjByProperty(Map<String, Object> maps);

	CombinPlan selectByPrimaryKey(Long id);

	List<CombinPlan> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<CombinPlan> queryByIds(List<Long> ids);

	List<CombinPlan> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(CombinPlan obj);

	void updateById(CombinPlan obj);

	void deleteById(@Param(value="id")Long id);

	List<CombinPlan> queryPages(Map<String, Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<CombinPlan> queryPagesWithNoRelations(Map<String,Object> params);

	List<CombinPlan> queryPageListWithNoRelations(Map<String,Object> params);

}
