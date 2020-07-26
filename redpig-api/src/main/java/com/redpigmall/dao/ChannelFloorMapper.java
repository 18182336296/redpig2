package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.ChannelFloor;

public interface ChannelFloorMapper extends SupperMapper {

	void batchDelete(List<ChannelFloor> objs);

	List<ChannelFloor> selectObjByProperty(Map<String, Object> maps);

	ChannelFloor selectByPrimaryKey(Long id);

	List<ChannelFloor> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<ChannelFloor> queryByIds(List<Long> ids);

	List<ChannelFloor> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(ChannelFloor obj);

	void updateById(ChannelFloor obj);

	void deleteById(@Param(value="id")Long id);
	List<ChannelFloor> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<ChannelFloor> queryPagesWithNoRelations(Map<String,Object> params);

	List<ChannelFloor> queryPageListWithNoRelations(Map<String,Object> params);

}
