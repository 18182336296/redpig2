package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.ShowClass;

public interface ShowClassMapper extends SupperMapper {

	void batchDelete(List<ShowClass> objs);

	List<ShowClass> selectObjByProperty(Map<String, Object> maps);

	ShowClass selectByPrimaryKey(Long id);

	List<ShowClass> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<ShowClass> queryByIds(List<Long> ids);

	List<ShowClass> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(ShowClass obj);

	void updateById(ShowClass obj);

	void deleteById(@Param(value="id")Long id);
	List<ShowClass> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<ShowClass> queryPagesWithNoRelations(Map<String,Object> params);

	List<ShowClass> queryPageListWithNoRelations(Map<String,Object> params);

	List<ShowClass> queryListWithNoRelations(Map<String, Object> params);

	List<Map<String, Object>> queryListWithNoRelations4fir(Map<String, Object> params);

}
