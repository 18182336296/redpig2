package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.PayoffLog;

public interface PayoffLogMapper extends SupperMapper {

	void batchDelete(List<PayoffLog> objs);

	List<PayoffLog> selectObjByProperty(Map<String, Object> maps);

	PayoffLog selectByPrimaryKey(Long id);

	List<PayoffLog> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<PayoffLog> queryByIds(List<Long> ids);

	List<PayoffLog> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(PayoffLog obj);

	void updateById(PayoffLog obj);

	void deleteById(@Param(value="id")Long id);
	List<PayoffLog> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<PayoffLog> queryPagesWithNoRelations(Map<String,Object> params);

	List<PayoffLog> queryPageListWithNoRelations(Map<String,Object> params);

}
