package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GoodsFloor;

public interface GoodsFloorMapper extends SupperMapper {

	void batchDelete(List<GoodsFloor> objs);

	List<GoodsFloor> selectObjByProperty(Map<String, Object> maps);

	GoodsFloor selectByPrimaryKey(Long id);

	List<GoodsFloor> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GoodsFloor> queryByIds(List<Long> ids);

	List<GoodsFloor> queryPageListByParentIsNull(Map<String, Object> params);

	List<GoodsFloor> queryAll();

	void save(GoodsFloor goodsfloor);

	void update(GoodsFloor goodsfloor);

	void delete(@Param(value="id")Long id);

	void saveEntity(GoodsFloor obj);

	void updateById(GoodsFloor obj);

	void deleteById(@Param(value="id")Long id);
	List<GoodsFloor> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GoodsFloor> queryPagesWithNoRelations(Map<String,Object> params);

	List<GoodsFloor> queryPageListWithNoRelations(Map<String,Object> params);

}
