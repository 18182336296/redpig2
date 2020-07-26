package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.ExpressCompanyCommon;

public interface ExpressCompanyCommonMapper extends SupperMapper {

	void batchDelete(List<ExpressCompanyCommon> objs);

	List<ExpressCompanyCommon> selectObjByProperty(Map<String, Object> maps);

	ExpressCompanyCommon selectByPrimaryKey(Long id);

	List<ExpressCompanyCommon> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<ExpressCompanyCommon> queryByIds(List<Long> ids);

	List<ExpressCompanyCommon> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(ExpressCompanyCommon obj);

	void updateById(ExpressCompanyCommon obj);

	void deleteById(@Param(value="id")Long id);
	List<ExpressCompanyCommon> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<ExpressCompanyCommon> queryPagesWithNoRelations(Map<String,Object> params);

	List<ExpressCompanyCommon> queryPageListWithNoRelations(Map<String,Object> params);

}
