package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.TransArea;

public interface TransAreaMapper extends SupperMapper {

	void batchDelete(List<TransArea> objs);

	List<TransArea> selectObjByProperty(Map<String, Object> maps);

	TransArea selectByPrimaryKey(Long id);

	List<TransArea> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<TransArea> queryByIds(List<Long> ids);

	List<TransArea> queryPageListByParentIsNull(Map<String, Object> params);

	void update(TransArea area);

	void save(TransArea area);

	void deleteByIds(List<Long> listIds);

	void saveEntity(TransArea obj);

	void updateById(TransArea obj);

	void deleteById(@Param(value="id")Long id);
	List<TransArea> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<TransArea> queryPagesWithNoRelations(Map<String,Object> params);

	List<TransArea> queryPageListWithNoRelations(Map<String,Object> params);

}
