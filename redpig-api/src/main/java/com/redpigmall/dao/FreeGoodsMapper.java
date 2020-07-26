package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.FreeGoods;

public interface FreeGoodsMapper extends SupperMapper {

	void batchDelete(List<FreeGoods> objs);

	List<FreeGoods> selectObjByProperty(Map<String, Object> maps);

	FreeGoods selectByPrimaryKey(Long id);

	List<FreeGoods> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<FreeGoods> queryByIds(List<Long> ids);

	List<FreeGoods> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(FreeGoods obj);

	void updateById(FreeGoods obj);

	void deleteById(@Param(value="id")Long id);
	List<FreeGoods> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<FreeGoods> queryPagesWithNoRelations(Map<String,Object> params);

	List<FreeGoods> queryPageListWithNoRelations(Map<String,Object> params);

}
