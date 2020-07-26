package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Goods;

public interface GoodsMapper extends SupperMapper {

	void batchDelete(List<Goods> objs);

	List<Goods> selectObjByProperty(Map<String, Object> maps);

	Goods selectByPrimaryKey(Long id);

	List<Goods> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Goods> queryByIds(List<Long> ids);

	List<Goods> queryPageListByParentIsNull(Map<String, Object> params);

	void batchDeleteGoodsPhotos(Map<String, Object> params);

	void batchDeleteUserGoodsClass(Map<String, Object> params);

	void batchDeleteGoodsSpecProperty(Map<String, Object> params);
	
	void deleteGoodsMainPhoto(@Param(value="id")Long id);

	void delete(@Param(value="id")Long id);

	void update(Goods obj);

	List<Goods> queryGoodsByGoodsClassIds(Map<String, Object> params);

	void removeGoodsBrandByGoodsBrandId(Map<String, Object> params);
	
	void saveEntity(Goods obj);

	void updateById(Goods obj);

	void deleteById(@Param(value="id")Long id);

	void batchInsertGoodsPhotos(Map<String, Object> params);

	void batchInsertGoodsSpecs(Map<String, Object> params);

	List<Goods> queryPages(Map<String,Object> params);
	//和queryPages一直，只不过不加在关联关系,这样可以加速访问
	List<Goods> queryPages2(Map<String,Object> params);
	
	void batchDeleteByIds(List<Long> ids);

	List<Goods> queryPagesWithNoRelations(Map<String,Object> params);

	List<Goods> queryPageListWithNoRelations(Map<String,Object> params);
	
	void batchInsertGoodsSpecs2(Map<String, Object> params);

	void saveGoodsUserGoodsClass(Map<String, Object> params);

	List<Map<String, Object>> queryGoodsList(Map<String, Object> params);

	Map<String, Object> selectGoodsById(Long id);
}
