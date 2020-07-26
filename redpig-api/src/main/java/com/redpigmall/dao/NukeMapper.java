package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Nuke;

public interface NukeMapper extends SupperMapper {

	void batchDelete(List<Nuke> objs);

	List<Nuke> selectObjByProperty(Map<String, Object> maps);

	Nuke selectByPrimaryKey(Long id);

	List<Nuke> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Nuke> queryByIds(List<Long> ids);

	List<Nuke> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Nuke obj);

	void updateById(Nuke obj);

	void deleteById(@Param(value="id")Long id);

	List<Nuke> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Nuke> queryPagesWithNoRelations(Map<String,Object> params);

	List<Nuke> queryPageListWithNoRelations(Map<String,Object> params);

}
