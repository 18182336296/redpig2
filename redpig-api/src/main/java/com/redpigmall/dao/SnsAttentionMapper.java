package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.SnsAttention;

public interface SnsAttentionMapper extends SupperMapper {

	void batchDelete(List<SnsAttention> objs);

	List<SnsAttention> selectObjByProperty(Map<String, Object> maps);

	SnsAttention selectByPrimaryKey(Long id);

	List<SnsAttention> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<SnsAttention> queryByIds(List<Long> ids);

	List<SnsAttention> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(SnsAttention obj);

	void updateById(SnsAttention obj);

	void deleteById(@Param(value="id")Long id);
	List<SnsAttention> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<SnsAttention> queryPagesWithNoRelations(Map<String,Object> params);

	List<SnsAttention> queryPageListWithNoRelations(Map<String,Object> params);

}
