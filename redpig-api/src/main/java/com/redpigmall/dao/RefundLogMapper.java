package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.RefundLog;

public interface RefundLogMapper extends SupperMapper {

	void batchDelete(List<RefundLog> objs);

	List<RefundLog> selectObjByProperty(Map<String, Object> maps);

	RefundLog selectByPrimaryKey(Long id);

	List<RefundLog> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<RefundLog> queryByIds(List<Long> ids);

	List<RefundLog> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(RefundLog obj);

	void updateById(RefundLog obj);

	void deleteById(@Param(value="id")Long id);
	List<RefundLog> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<RefundLog> queryPagesWithNoRelations(Map<String,Object> params);

	List<RefundLog> queryPageListWithNoRelations(Map<String,Object> params);

}
