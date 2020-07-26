package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GroupPriceRange;

public interface GroupPriceRangeMapper extends SupperMapper {

	void batchDelete(List<GroupPriceRange> objs);

	List<GroupPriceRange> selectObjByProperty(Map<String, Object> maps);

	GroupPriceRange selectByPrimaryKey(Long id);

	List<GroupPriceRange> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GroupPriceRange> queryByIds(List<Long> ids);

	List<GroupPriceRange> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(GroupPriceRange obj);

	void updateById(GroupPriceRange obj);

	void deleteById(@Param(value="id")Long id);
	List<GroupPriceRange> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GroupPriceRange> queryPagesWithNoRelations(Map<String,Object> params);

	List<GroupPriceRange> queryPageListWithNoRelations(Map<String,Object> params);

}
