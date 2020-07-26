package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Transport;

public interface TransportMapper extends SupperMapper {

	void batchDelete(List<Transport> objs);

	List<Transport> selectObjByProperty(Map<String, Object> maps);

	Transport selectByPrimaryKey(Long id);

	List<Transport> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Transport> queryByIds(List<Long> ids);

	List<Transport> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Transport obj);

	void updateById(Transport obj);

	void deleteById(@Param(value="id")Long id);
	
	List<Transport> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Transport> queryPagesWithNoRelations(Map<String,Object> params);

	List<Transport> queryPageListWithNoRelations(Map<String,Object> params);

}
