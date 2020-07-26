package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.VMenu;

public interface VMenuMapper extends SupperMapper {

	void batchDelete(List<VMenu> objs);

	List<VMenu> selectObjByProperty(Map<String, Object> maps);

	VMenu selectByPrimaryKey(Long id);

	List<VMenu> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<VMenu> queryByIds(List<Long> ids);

	List<VMenu> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(VMenu obj);

	void updateById(VMenu obj);

	void deleteById(@Param(value="id")Long id);
	List<VMenu> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<VMenu> queryPagesWithNoRelations(Map<String,Object> params);

	List<VMenu> queryPageListWithNoRelations(Map<String,Object> params);

}
