package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GoodsSpecProperty;

public interface GoodsSpecPropertyMapper extends SupperMapper {

	void batchDelete(List<GoodsSpecProperty> objs);

	List<GoodsSpecProperty> selectObjByProperty(Map<String, Object> maps);

	GoodsSpecProperty selectByPrimaryKey(Long id);

	List<GoodsSpecProperty> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GoodsSpecProperty> queryByIds(List<Long> ids);

	List<GoodsSpecProperty> queryPageListByParentIsNull(Map<String, Object> params);

	int save(GoodsSpecProperty property);

	void update(GoodsSpecProperty property);

	int delete(Long id);

	void saveEntity(GoodsSpecProperty obj);

	void updateById(GoodsSpecProperty obj);

	void deleteById(@Param(value="id")Long id);
	List<GoodsSpecProperty> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GoodsSpecProperty> queryPagesWithNoRelations(Map<String,Object> params);

	List<GoodsSpecProperty> queryPageListWithNoRelations(Map<String,Object> params);
	
	void deleteGoodsCartAndGoodsSpecProperty(Map<String, Object> params);

}
