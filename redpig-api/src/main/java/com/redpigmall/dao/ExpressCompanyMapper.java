package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.ExpressCompany;

public interface ExpressCompanyMapper extends SupperMapper {

	void batchDelete(List<ExpressCompany> objs);

	List<ExpressCompany> selectObjByProperty(Map<String, Object> maps);

	ExpressCompany selectByPrimaryKey(Long id);

	List<ExpressCompany> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<ExpressCompany> queryByIds(List<Long> ids);

	List<ExpressCompany> queryPageListByParentIsNull(Map<String, Object> params);
	
	List<ExpressCompany> queryPages(Map<String, Object> params);

	void save(ExpressCompany expresscompany);

	void update(ExpressCompany expresscompany);

	void deleteByIds(List<Long> listIds);

	int selectCountByNotId(Map<String, Object> params);

	void saveEntity(ExpressCompany obj);

	void updateById(ExpressCompany obj);

	void deleteById(@Param(value="id")Long id);

	void batchDeleteByIds(List<Long> ids);

	List<ExpressCompany> queryPagesWithNoRelations(Map<String,Object> params);

	List<ExpressCompany> queryPageListWithNoRelations(Map<String,Object> params);

}
