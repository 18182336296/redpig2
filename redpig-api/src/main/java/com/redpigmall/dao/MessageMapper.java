package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Message;

public interface MessageMapper extends SupperMapper {

	void batchDelete(List<Message> objs);

	List<Message> selectObjByProperty(Map<String, Object> maps);

	Message selectByPrimaryKey(Long id);

	List<Message> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Message> queryByIds(List<Long> ids);

	List<Message> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Message obj);

	void updateById(Message obj);

	void deleteById(@Param(value="id")Long id);
	List<Message> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Message> queryPagesWithNoRelations(Map<String,Object> params);

	List<Message> queryPageListWithNoRelations(Map<String,Object> params);

}
