package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GoodsTag;

public interface GoodsTagMapper extends SupperMapper {

	void batchDelete(List<GoodsTag> objs);

	List<GoodsTag> selectObjByProperty(Map<String, Object> maps);

	GoodsTag selectByPrimaryKey(Long id);

	List<GoodsTag> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GoodsTag> queryByIds(List<Long> ids);

	List<GoodsTag> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(GoodsTag obj);

	void updateById(GoodsTag obj);

	void deleteById(@Param(value="id")Long id);
	List<GoodsTag> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GoodsTag> queryPagesWithNoRelations(Map<String,Object> params);

	List<GoodsTag> queryPageListWithNoRelations(Map<String,Object> params);

}
