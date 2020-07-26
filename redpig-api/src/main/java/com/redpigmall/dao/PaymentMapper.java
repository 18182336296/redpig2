package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Payment;

public interface PaymentMapper extends SupperMapper {

	void batchDelete(List<Payment> objs);

	List<Payment> selectObjByProperty(Map<String, Object> maps);

	Payment selectByPrimaryKey(Long id);

	List<Payment> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Payment> queryByIds(List<Long> ids);

	List<Payment> queryPageListByParentIsNull(Map<String, Object> params);

	void update(Payment obj);

	void save(Payment obj);

	void saveEntity(Payment obj);

	void updateById(Payment obj);

	void deleteById(@Param(value="id")Long id);
	List<Payment> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Payment> queryPagesWithNoRelations(Map<String,Object> params);

	List<Payment> queryPageListWithNoRelations(Map<String,Object> params);

}
