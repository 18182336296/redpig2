package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Navigation;

public interface NavigationMapper extends SupperMapper {

	void batchDelete(List<Navigation> objs);

	List<Navigation> selectObjByProperty(Map<String, Object> maps);

	Navigation selectByPrimaryKey(Long id);

	List<Navigation> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Navigation> queryByIds(List<Long> ids);

	List<Navigation> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Navigation obj);

	void updateById(Navigation obj);

	void deleteById(@Param(value="id")Long id);
	List<Navigation> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Navigation> queryPagesWithNoRelations(Map<String,Object> params);

	List<Navigation> queryPageListWithNoRelations(Map<String,Object> params);

}
