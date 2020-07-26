package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.CircleInvitationReply;

public interface CircleInvitationReplyMapper extends SupperMapper {

	void batchDelete(List<CircleInvitationReply> objs);

	List<CircleInvitationReply> selectObjByProperty(Map<String, Object> maps);

	CircleInvitationReply selectByPrimaryKey(Long id);

	List<CircleInvitationReply> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<CircleInvitationReply> queryByIds(List<Long> ids);

	List<CircleInvitationReply> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(CircleInvitationReply obj);

	void updateById(CircleInvitationReply obj);

	void deleteById(@Param(value="id")Long id);
	List<CircleInvitationReply> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<CircleInvitationReply> queryPagesWithNoRelations(Map<String,Object> params);

	List<CircleInvitationReply> queryPageListWithNoRelations(Map<String,Object> params);

}
