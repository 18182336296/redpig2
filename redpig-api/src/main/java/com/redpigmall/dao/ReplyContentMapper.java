package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.ReplyContent;

public interface ReplyContentMapper extends SupperMapper {

	void batchDelete(List<ReplyContent> objs);

	List<ReplyContent> selectObjByProperty(Map<String, Object> maps);

	ReplyContent selectByPrimaryKey(Long id);

	List<ReplyContent> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<ReplyContent> queryByIds(List<Long> ids);

	List<ReplyContent> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(ReplyContent obj);

	void updateById(ReplyContent obj);

	void deleteById(@Param(value="id")Long id);
	List<ReplyContent> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<ReplyContent> queryPagesWithNoRelations(Map<String,Object> params);

	List<ReplyContent> queryPageListWithNoRelations(Map<String,Object> params);

}
