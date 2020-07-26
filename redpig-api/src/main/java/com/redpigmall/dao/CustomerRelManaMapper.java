package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.CustomerRelMana;

public interface CustomerRelManaMapper extends SupperMapper {

	void batchDelete(List<CustomerRelMana> objs);

	List<CustomerRelMana> selectObjByProperty(Map<String, Object> maps);

	CustomerRelMana selectByPrimaryKey(Long id);

	List<CustomerRelMana> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<CustomerRelMana> queryByIds(List<Long> ids);

	List<CustomerRelMana> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(CustomerRelMana obj);

	void updateById(CustomerRelMana obj);

	void deleteById(@Param(value="id")Long id);
	List<CustomerRelMana> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<CustomerRelMana> queryPagesWithNoRelations(Map<String,Object> params);

	List<CustomerRelMana> queryPageListWithNoRelations(Map<String,Object> params);

}
