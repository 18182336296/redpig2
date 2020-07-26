package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Channel;

public interface ChannelMapper extends SupperMapper {

	void batchDelete(List<Channel> objs);

	List<Channel> selectObjByProperty(Map<String, Object> maps);

	Channel selectByPrimaryKey(Long id);

	List<Channel> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Channel> queryByIds(List<Long> ids);

	List<Channel> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Channel obj);

	void updateById(Channel obj);

	void deleteById(@Param(value="id")Long id);
	List<Channel> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Channel> queryPagesWithNoRelations(Map<String,Object> params);

	List<Channel> queryPageListWithNoRelations(Map<String,Object> params);

}
