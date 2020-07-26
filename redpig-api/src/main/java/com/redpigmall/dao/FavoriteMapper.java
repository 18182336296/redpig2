package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Favorite;

public interface FavoriteMapper extends SupperMapper {

	void batchDelete(List<Favorite> objs);

	List<Favorite> selectObjByProperty(Map<String, Object> maps);

	Favorite selectByPrimaryKey(Long id);

	List<Favorite> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Favorite> queryByIds(List<Long> ids);

	List<Favorite> queryPageListByParentIsNull(Map<String, Object> params);

	void delete(Favorite obj);

	void deleteByGoodsId(@Param(value="id") Long id);

	void saveEntity(Favorite obj);

	void updateById(Favorite obj);

	void deleteById(@Param(value="id")Long id);
	List<Favorite> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Favorite> queryPagesWithNoRelations(Map<String,Object> params);

	List<Favorite> queryPageListWithNoRelations(Map<String,Object> params);
	
	List<Map<String,Object>> PersonaCollectionList(Map<String,Object> params);
	
	List<Map<String,Object>> storeCollectionList(Map<String,Object> params);
	
	

}
