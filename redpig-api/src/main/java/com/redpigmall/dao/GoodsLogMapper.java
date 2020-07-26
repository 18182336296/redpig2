package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GoodsLog;

public interface GoodsLogMapper extends SupperMapper {

	void batchDelete(List<GoodsLog> objs);

	List<GoodsLog> selectObjByProperty(Map<String, Object> maps);

	GoodsLog selectByPrimaryKey(Long id);

	List<GoodsLog> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GoodsLog> queryByIds(List<Long> ids);

	List<GoodsLog> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(GoodsLog obj);

	void updateById(GoodsLog obj);

	void deleteById(@Param(value="id")Long id);
	List<GoodsLog> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GoodsLog> queryPagesWithNoRelations(Map<String,Object> params);

	List<GoodsLog> queryPageListWithNoRelations(Map<String,Object> params);
	
	List<Map<String,Object>> queryByGroup(Map<String, Object> params);

}
