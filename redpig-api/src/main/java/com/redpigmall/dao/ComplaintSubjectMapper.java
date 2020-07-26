package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.ComplaintSubject;

public interface ComplaintSubjectMapper extends SupperMapper {

	void batchDelete(List<ComplaintSubject> objs);

	List<ComplaintSubject> selectObjByProperty(Map<String, Object> maps);

	ComplaintSubject selectByPrimaryKey(Long id);

	List<ComplaintSubject> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<ComplaintSubject> queryByIds(List<Long> ids);

	List<ComplaintSubject> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(ComplaintSubject obj);

	void updateById(ComplaintSubject obj);

	void deleteById(@Param(value="id")Long id);
	List<ComplaintSubject> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<ComplaintSubject> queryPagesWithNoRelations(Map<String,Object> params);

	List<ComplaintSubject> queryPageListWithNoRelations(Map<String,Object> params);

}
