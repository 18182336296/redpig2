package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.OrderFormLog;

public interface OrderFormLogMapper extends SupperMapper {

	void batchDelete(List<OrderFormLog> objs);

	List<OrderFormLog> selectObjByProperty(Map<String, Object> maps);

	OrderFormLog selectByPrimaryKey(Long id);

	List<OrderFormLog> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<OrderFormLog> queryByIds(List<Long> ids);

	List<OrderFormLog> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(OrderFormLog obj);

	void updateById(OrderFormLog obj);

	void deleteById(@Param(value="id")Long id);
	List<OrderFormLog> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<OrderFormLog> queryPagesWithNoRelations(Map<String,Object> params);

	List<OrderFormLog> queryPageListWithNoRelations(Map<String,Object> params);

}
