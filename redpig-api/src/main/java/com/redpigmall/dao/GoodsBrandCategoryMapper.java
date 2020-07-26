package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GoodsBrandCategory;

public interface GoodsBrandCategoryMapper extends SupperMapper {

	void batchDelete(List<GoodsBrandCategory> objs);

	List<GoodsBrandCategory> selectObjByProperty(Map<String, Object> maps);

	GoodsBrandCategory selectByPrimaryKey(Long id);

	
	List<GoodsBrandCategory> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GoodsBrandCategory> queryByIds(List<Long> ids);

	List<GoodsBrandCategory> queryPageListByParentIsNull(Map<String, Object> params);

	void save(GoodsBrandCategory cat);

	void saveEntity(GoodsBrandCategory obj);

	void updateById(GoodsBrandCategory obj);

	void deleteById(@Param(value="id")Long id);
	List<GoodsBrandCategory> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GoodsBrandCategory> queryPagesWithNoRelations(Map<String,Object> params);

	List<GoodsBrandCategory> queryPageListWithNoRelations(Map<String,Object> params);

}
