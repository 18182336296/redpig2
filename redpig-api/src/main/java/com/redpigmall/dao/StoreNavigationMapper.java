package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.StoreNavigation;

public interface StoreNavigationMapper extends SupperMapper {

	void batchDelete(List<StoreNavigation> objs);

	List<StoreNavigation> selectObjByProperty(Map<String, Object> maps);

	StoreNavigation selectByPrimaryKey(Long id);

	List<StoreNavigation> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<StoreNavigation> queryByIds(List<Long> ids);

	List<StoreNavigation> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(StoreNavigation obj);

	void updateById(StoreNavigation obj);

	void deleteById(@Param(value="id")Long id);
	List<StoreNavigation> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<StoreNavigation> queryPagesWithNoRelations(Map<String,Object> params);

	List<StoreNavigation> queryPageListWithNoRelations(Map<String,Object> params);

}
