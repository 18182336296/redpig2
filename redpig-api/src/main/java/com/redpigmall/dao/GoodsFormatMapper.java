package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GoodsFormat;

public interface GoodsFormatMapper extends SupperMapper {

	void batchDelete(List<GoodsFormat> objs);

	List<GoodsFormat> selectObjByProperty(Map<String, Object> maps);

	GoodsFormat selectByPrimaryKey(Long id);

	List<GoodsFormat> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GoodsFormat> queryByIds(List<Long> ids);

	List<GoodsFormat> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(GoodsFormat obj);

	void updateById(GoodsFormat obj);

	void deleteById(@Param(value="id")Long id);
	List<GoodsFormat> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GoodsFormat> queryPagesWithNoRelations(Map<String,Object> params);

	List<GoodsFormat> queryPageListWithNoRelations(Map<String,Object> params);

}
