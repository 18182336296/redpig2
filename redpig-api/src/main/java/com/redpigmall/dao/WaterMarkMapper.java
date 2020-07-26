package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.WaterMark;

public interface WaterMarkMapper extends SupperMapper {

	void batchDelete(List<WaterMark> objs);

	List<WaterMark> selectObjByProperty(Map<String, Object> maps);

	WaterMark selectByPrimaryKey(Long id);

	List<WaterMark> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<WaterMark> queryByIds(List<Long> ids);

	List<WaterMark> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(WaterMark obj);

	void updateById(WaterMark obj);

	void deleteById(@Param(value="id")Long id);
	List<WaterMark> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<WaterMark> queryPagesWithNoRelations(Map<String,Object> params);

	List<WaterMark> queryPageListWithNoRelations(Map<String,Object> params);

}
