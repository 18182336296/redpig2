package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.InformationReply;

public interface InformationReplyMapper extends SupperMapper {

	void batchDelete(List<InformationReply> objs);

	List<InformationReply> selectObjByProperty(Map<String, Object> maps);

	InformationReply selectByPrimaryKey(Long id);

	List<InformationReply> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<InformationReply> queryByIds(List<Long> ids);

	List<InformationReply> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(InformationReply obj);

	void updateById(InformationReply obj);

	void deleteById(@Param(value="id")Long id);
	List<InformationReply> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<InformationReply> queryPagesWithNoRelations(Map<String,Object> params);

	List<InformationReply> queryPageListWithNoRelations(Map<String,Object> params);

}
