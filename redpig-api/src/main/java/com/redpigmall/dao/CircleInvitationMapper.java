package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.CircleInvitation;

public interface CircleInvitationMapper extends SupperMapper {

	void batchDelete(List<CircleInvitation> objs);

	List<CircleInvitation> selectObjByProperty(Map<String, Object> maps);

	CircleInvitation selectByPrimaryKey(Long id);

	List<CircleInvitation> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<CircleInvitation> queryByIds(List<Long> ids);

	List<CircleInvitation> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(CircleInvitation obj);

	void updateById(CircleInvitation obj);

	void deleteById(@Param(value="id")Long id);
	List<CircleInvitation> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<CircleInvitation> queryPagesWithNoRelations(Map<String,Object> params);

	List<CircleInvitation> queryPageListWithNoRelations(Map<String,Object> params);

}
