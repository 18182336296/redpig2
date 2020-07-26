package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.VMessage;

public interface VMessageMapper extends SupperMapper {

	void batchDelete(List<VMessage> objs);

	List<VMessage> selectObjByProperty(Map<String, Object> maps);

	VMessage selectByPrimaryKey(Long id);

	List<VMessage> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<VMessage> queryByIds(List<Long> ids);

	List<VMessage> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(VMessage obj);

	void updateById(VMessage obj);

	void deleteById(@Param(value="id")Long id);
	List<VMessage> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<VMessage> queryPagesWithNoRelations(Map<String,Object> params);

	List<VMessage> queryPageListWithNoRelations(Map<String,Object> params);

}
