package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.ChattingUser;

public interface ChattingUserMapper extends SupperMapper {

	void batchDelete(List<ChattingUser> objs);

	List<ChattingUser> selectObjByProperty(Map<String, Object> maps);

	ChattingUser selectByPrimaryKey(Long id);

	List<ChattingUser> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<ChattingUser> queryByIds(List<Long> ids);

	List<ChattingUser> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(ChattingUser obj);

	void updateById(ChattingUser obj);

	void deleteById(@Param(value="id")Long id);
	List<ChattingUser> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<ChattingUser> queryPagesWithNoRelations(Map<String,Object> params);

	List<ChattingUser> queryPageListWithNoRelations(Map<String,Object> params);

}
