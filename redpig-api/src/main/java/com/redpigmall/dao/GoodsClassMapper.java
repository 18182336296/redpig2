package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GoodsClass;

public interface GoodsClassMapper extends SupperMapper {

	void batchDelete(List<GoodsClass> objs);

	List<GoodsClass> selectObjByProperty(Map<String, Object> maps);

	GoodsClass selectByPrimaryKey(Long id);

	List<GoodsClass> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GoodsClass> queryByIds(List<Long> ids);

	List<GoodsClass> queryPageListByParentIsNull(Map<String, Object> params);

	List<GoodsClass> queryPages(Map<String, Object> params);

	void update(GoodsClass gc);

	void save(GoodsClass goodsClass);

	List<GoodsClass> queryGoodsClassByIds(Map<String, Object> params);

	void removeGoodsType(Map<String, Object> maps);

	void removeGoodsSpecification(Map<String, Object> maps);
	
	void removeClilds(Map<String, Object> maps);

	void delete(@Param(value="id")Long id);

	void saveEntity(GoodsClass obj);

	void updateById(GoodsClass obj);

	void deleteById(@Param(value="id")Long id);

	void batchDeleteByIds(List<Long> ids);

	List<GoodsClass> queryPagesWithNoRelations(Map<String,Object> params);

	List<GoodsClass> queryPageListWithNoRelations(Map<String,Object> params);

	List<Map<String, Object>> queryListWithNoRelations(Map<String, Object> params);

}
