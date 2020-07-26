package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.UserGoodsClass;

public interface UserGoodsClassMapper extends SupperMapper {

	void batchDelete(List<UserGoodsClass> objs);

	List<UserGoodsClass> selectObjByProperty(Map<String, Object> maps);

	UserGoodsClass selectByPrimaryKey(Long id);

	List<UserGoodsClass> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<UserGoodsClass> queryByIds(List<Long> ids);

	List<UserGoodsClass> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(UserGoodsClass obj);

	void updateById(UserGoodsClass obj);

	void deleteById(@Param(value="id")Long id);
	List<UserGoodsClass> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<UserGoodsClass> queryPagesWithNoRelations(Map<String,Object> params);

	List<UserGoodsClass> queryPageListWithNoRelations(Map<String,Object> params);

}
