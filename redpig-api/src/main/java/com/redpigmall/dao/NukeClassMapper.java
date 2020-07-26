package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.NukeClass;

public interface NukeClassMapper extends SupperMapper {

	void batchDelete(List<NukeClass> objs);

	List<NukeClass> selectObjByProperty(Map<String, Object> maps);

	NukeClass selectByPrimaryKey(Long id);

	List<NukeClass> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<NukeClass> queryByIds(List<Long> ids);

	List<NukeClass> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(NukeClass obj);

	void updateById(NukeClass obj);

	void deleteById(@Param(value="id")Long id);
	List<NukeClass> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<NukeClass> queryPagesWithNoRelations(Map<String,Object> params);

	List<NukeClass> queryPageListWithNoRelations(Map<String,Object> params);

}
