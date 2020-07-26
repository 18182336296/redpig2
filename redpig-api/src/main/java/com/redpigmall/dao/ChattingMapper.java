package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Chatting;

public interface ChattingMapper extends SupperMapper {

	void batchDelete(List<Chatting> objs);

	List<Chatting> selectObjByProperty(Map<String, Object> maps);

	Chatting selectByPrimaryKey(Long id);
	
	List<Chatting> queryPageList(Map<String, Object> maps);
	
	Integer selectCount(Map<String, Object> maps);

	void save(Chatting photo);
	
	void saveEntity(Chatting obj);
	
	void updateById(Chatting obj);

	void deleteById(@Param(value="id")Long id);

	List<Chatting> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Chatting> queryPagesWithNoRelations(Map<String,Object> params);

	List<Chatting> queryPageListWithNoRelations(Map<String,Object> params);

}
