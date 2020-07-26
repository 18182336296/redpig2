package com.redpigmall.dao;

import com.redpigmall.domain.UserNukeRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserNukeRecordMapper extends SupperMapper {

	void batchDelete(List<UserNukeRecord> objs);

	List<UserNukeRecord> selectObjByProperty(Map<String, Object> maps);

	UserNukeRecord selectByPrimaryKey(Long id);

	List<UserNukeRecord> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<UserNukeRecord> queryByIds(List<Long> ids);

	List<UserNukeRecord> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(UserNukeRecord obj);

	void updateById(UserNukeRecord obj);

	void deleteById(@Param(value = "id") Long id);

	List<UserNukeRecord> queryPages(Map<String, Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<UserNukeRecord> queryPagesWithNoRelations(Map<String, Object> params);

	List<UserNukeRecord> queryPageListWithNoRelations(Map<String, Object> params);

}
