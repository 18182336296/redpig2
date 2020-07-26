package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.ReturnGoodsLog;

public interface ReturnGoodsLogMapper extends SupperMapper {

	void batchDelete(List<ReturnGoodsLog> objs);

	List<ReturnGoodsLog> selectObjByProperty(Map<String, Object> maps);

	ReturnGoodsLog selectByPrimaryKey(Long id);

	List<ReturnGoodsLog> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<ReturnGoodsLog> queryByIds(List<Long> ids);

	List<ReturnGoodsLog> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(ReturnGoodsLog obj);

	void updateById(ReturnGoodsLog obj);

	void deleteById(@Param(value="id")Long id);
	List<ReturnGoodsLog> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<ReturnGoodsLog> queryPagesWithNoRelations(Map<String,Object> params);

	List<ReturnGoodsLog> queryPageListWithNoRelations(Map<String,Object> params);

}
