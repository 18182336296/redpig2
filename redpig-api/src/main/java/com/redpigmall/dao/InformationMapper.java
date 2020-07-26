package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Information;

public interface InformationMapper extends SupperMapper {

	void batchDelete(List<Information> objs);

	List<Information> selectObjByProperty(Map<String, Object> maps);

	Information selectByPrimaryKey(Long id);

	List<Information> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Information> queryByIds(List<Long> ids);

	List<Information> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Information obj);

	void updateById(Information obj);

	void deleteById(@Param(value="id")Long id);
	List<Information> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Information> queryPagesWithNoRelations(Map<String,Object> params);

	List<Information> queryPageListWithNoRelations(Map<String,Object> params);

}
