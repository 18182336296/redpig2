package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.WeixinFloor;

public interface WeixinFloorMapper extends SupperMapper {

	void batchDelete(List<WeixinFloor> objs);

	List<WeixinFloor> selectObjByProperty(Map<String, Object> maps);

	WeixinFloor selectByPrimaryKey(Long id);

	List<WeixinFloor> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<WeixinFloor> queryByIds(List<Long> ids);

	List<WeixinFloor> queryPageListByParentIsNull(Map<String, Object> params);

	void save(WeixinFloor obj);

	void updateById(WeixinFloor obj);

	void deleteById(@Param(value="id")Long id);
	
	List<WeixinFloor> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<WeixinFloor> queryPagesWithNoRelations(Map<String,Object> params);

	List<WeixinFloor> queryPageListWithNoRelations(Map<String,Object> params);

}
