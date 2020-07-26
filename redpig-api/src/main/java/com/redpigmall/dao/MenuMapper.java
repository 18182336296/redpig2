package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Menu;

public interface MenuMapper extends SupperMapper {

	void batchDelete(List<Menu> objs);

	List<Menu> selectObjByProperty(Map<String, Object> maps);

	Menu selectByPrimaryKey(Long id);
	
	List<Menu> queryPageList(Map<String, Object> maps);
	
	Integer selectCount(Map<String, Object> maps);

	List<Menu> queryByIds(List<Long> ids);

	List<Menu> queryPageListByParentIsNull(Map<String, Object> params);

	void save(Menu photo);
	
	void update(Menu photo);

	void delete(@Param(value="id")Long id);

	void saveEntity(Menu obj);

	void updateById(Menu obj);

	void deleteById(@Param(value="id")Long id);

	List<Menu> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Menu> queryPagesWithNoRelations(Map<String,Object> params);

	List<Menu> queryPageListWithNoRelations(Map<String,Object> params);
	
	List<Menu> getUserMenus(Map<String, Object> maps);
	
}
