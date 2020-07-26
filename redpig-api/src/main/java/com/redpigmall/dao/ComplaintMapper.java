package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Complaint;

public interface ComplaintMapper extends SupperMapper {

	void batchDelete(List<Complaint> objs);

	List<Complaint> selectObjByProperty(Map<String, Object> maps);

	Complaint selectByPrimaryKey(Long id);

	List<Complaint> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Complaint> queryByIds(List<Long> ids);

	List<Complaint> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Complaint obj);

	void updateById(Complaint obj);

	void deleteById(@Param(value="id")Long id);
	List<Complaint> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Complaint> queryPagesWithNoRelations(Map<String,Object> params);

	List<Complaint> queryPageListWithNoRelations(Map<String,Object> params);
	
	
}
