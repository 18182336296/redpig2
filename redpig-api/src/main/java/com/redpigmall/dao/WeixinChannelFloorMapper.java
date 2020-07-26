package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.WeixinChannelFloor;

public interface WeixinChannelFloorMapper extends SupperMapper {

	void batchDelete(List<WeixinChannelFloor> objs);

	List<WeixinChannelFloor> selectObjByProperty(Map<String, Object> maps);

	WeixinChannelFloor selectByPrimaryKey(Long id);

	List<WeixinChannelFloor> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<WeixinChannelFloor> queryByIds(List<Long> ids);

	List<WeixinChannelFloor> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(WeixinChannelFloor obj);

	void updateById(WeixinChannelFloor obj);

	void deleteById(@Param(value="id")Long id);
	List<WeixinChannelFloor> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<WeixinChannelFloor> queryPagesWithNoRelations(Map<String,Object> params);

	List<WeixinChannelFloor> queryPageListWithNoRelations(Map<String,Object> params);

}
