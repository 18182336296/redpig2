package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GoodsCase;

public interface GoodsCaseMapper extends SupperMapper {

	void batchDelete(List<GoodsCase> objs);

	List<GoodsCase> selectObjByProperty(Map<String, Object> maps);

	GoodsCase selectByPrimaryKey(Long id);

	List<GoodsCase> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GoodsCase> queryByIds(List<Long> ids);

	List<GoodsCase> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(GoodsCase obj);

	void updateById(GoodsCase obj);

	void deleteById(@Param(value="id")Long id);
	List<GoodsCase> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GoodsCase> queryPagesWithNoRelations(Map<String,Object> params);

	List<GoodsCase> queryPageListWithNoRelations(Map<String,Object> params);

}
