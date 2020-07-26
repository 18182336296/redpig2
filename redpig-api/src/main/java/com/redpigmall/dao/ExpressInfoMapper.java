package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.ExpressInfo;

public interface ExpressInfoMapper extends SupperMapper {

	void batchDelete(List<ExpressInfo> objs);

	List<ExpressInfo> selectObjByProperty(Map<String, Object> maps);

	ExpressInfo selectByPrimaryKey(Long id);

	List<ExpressInfo> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<ExpressInfo> queryByIds(List<Long> ids);

	List<ExpressInfo> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(ExpressInfo obj);

	void updateById(ExpressInfo obj);

	void deleteById(@Param(value="id")Long id);
	List<ExpressInfo> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<ExpressInfo> queryPagesWithNoRelations(Map<String,Object> params);

	List<ExpressInfo> queryPageListWithNoRelations(Map<String,Object> params);

}
