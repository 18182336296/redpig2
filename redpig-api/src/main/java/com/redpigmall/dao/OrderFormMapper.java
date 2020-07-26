package com.redpigmall.dao;

import com.redpigmall.domain.OrderForm;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrderFormMapper extends SupperMapper {

	void batchDelete(List<OrderForm> objs);

	List<OrderForm> selectObjByProperty(Map<String, Object> maps);

	OrderForm selectByPrimaryKey(Long id);

	List<OrderForm> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<OrderForm> queryByIds(List<Long> ids);
	
	List<OrderForm> queryPageListByParentIsNull(Map<String, Object> params);
	
	void saveEntity(OrderForm obj);
	
	void updateById(OrderForm obj);
	/**通过状态查询*/
	void updateDistributionStatus(OrderForm obj);
	
	void deleteById(@Param(value="id")Long id);
	
	List<OrderForm> queryPages(Map<String,Object> params);
	
	void batchDeleteByIds(List<Long> ids);
	
	List<Map<String, Object>> getCountByArea(Map<String, Object> params);
	
	List<OrderForm> queryPagesWithNoRelations(Map<String,Object> params);
	
	List<OrderForm> queryPageListWithNoRelations(Map<String,Object> params);
	
	List<Map<String,Object>> querySum(Map<String, Object> params);
	
	List<Map<String, Object>> countByArea(Map<String, Object> params);
	
	List<Map<String, Object>> selectOrderList(Map<String, Object> params);
	/**通过user_id查询*/
	List<OrderForm> queryOrderById(@Param(value ="user_id")Long id);
	/**通过user_id查询*/
	List<OrderForm> queryOrderAllById(@Param(value ="user_id")Long id);

}
