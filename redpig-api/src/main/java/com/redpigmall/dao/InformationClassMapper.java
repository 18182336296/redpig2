package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.InformationClass;

public interface InformationClassMapper extends SupperMapper {

	void batchDelete(List<InformationClass> objs);

	List<InformationClass> selectObjByProperty(Map<String, Object> maps);

	InformationClass selectByPrimaryKey(Long id);

	List<InformationClass> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<InformationClass> queryByIds(List<Long> ids);

	List<InformationClass> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(InformationClass obj);

	void updateById(InformationClass obj);

	void deleteById(@Param(value="id")Long id);
	List<InformationClass> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<InformationClass> queryPagesWithNoRelations(Map<String,Object> params);

	List<InformationClass> queryPageListWithNoRelations(Map<String,Object> params);

}
