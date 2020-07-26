package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GoodsType;

public interface GoodsTypeMapper extends SupperMapper {

	void batchDelete(List<GoodsType> objs);

	List<GoodsType> selectObjByProperty(Map<String, Object> maps);

	GoodsType selectByPrimaryKey(Long id);

	List<GoodsType> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GoodsType> queryByIds(List<Long> ids);

	List<GoodsType> queryPageListByParentIsNull(Map<String, Object> params);

	void update(GoodsType type);

	void save(GoodsType goodsType);

	void deleteGoodsTypeAndGoodsBrand(List<Long> listIds);

	void delete(List<Long> listIds);
	void saveEntity(GoodsType obj);

	void updateById(GoodsType obj);

	void deleteById(@Param(value="id")Long id);
	List<GoodsType> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GoodsType> queryPagesWithNoRelations(Map<String,Object> params);

	List<GoodsType> queryPageListWithNoRelations(Map<String,Object> params);

}
