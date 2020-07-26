package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GoodsTypeProperty;

public interface GoodsTypePropertyMapper extends SupperMapper {

	void batchDelete(List<GoodsTypeProperty> objs);

	List<GoodsTypeProperty> selectObjByProperty(Map<String, Object> maps);

	GoodsTypeProperty selectByPrimaryKey(Long id);

	List<GoodsTypeProperty> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GoodsTypeProperty> queryByIds(List<Long> ids);

	List<GoodsTypeProperty> queryPageListByParentIsNull(Map<String, Object> params);

	void save(GoodsTypeProperty gtp);

	void update(GoodsTypeProperty gtp);

	int delete(@Param(value="id") Long id);

	void saveEntity(GoodsTypeProperty obj);

	void updateById(GoodsTypeProperty obj);

	void deleteById(@Param(value="id")Long id);
	List<GoodsTypeProperty> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GoodsTypeProperty> queryPagesWithNoRelations(Map<String,Object> params);

	List<GoodsTypeProperty> queryPageListWithNoRelations(Map<String,Object> params);

}
