package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GoodsReturn;

public interface GoodsReturnMapper extends SupperMapper {

	void batchDelete(List<GoodsReturn> objs);

	List<GoodsReturn> selectObjByProperty(Map<String, Object> maps);

	GoodsReturn selectByPrimaryKey(Long id);

	List<GoodsReturn> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GoodsReturn> queryByIds(List<Long> ids);

	List<GoodsReturn> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(GoodsReturn obj);

	void updateById(GoodsReturn obj);

	void deleteById(@Param(value="id")Long id);
	List<GoodsReturn> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GoodsReturn> queryPagesWithNoRelations(Map<String,Object> params);

	List<GoodsReturn> queryPageListWithNoRelations(Map<String,Object> params);

}
