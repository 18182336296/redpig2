package com.redpigmall.dao;

import com.redpigmall.domain.CollageBuy;
import com.redpigmall.domain.CollageBuy;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CollageBuyMapper extends SupperMapper {

	void batchDelete(List<CollageBuy> objs);

	List<CollageBuy> selectObjByProperty(Map<String, Object> maps);

	CollageBuy selectByPrimaryKey(Long id);

	List<CollageBuy> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<CollageBuy> queryByIds(List<Long> ids);

	List<CollageBuy> queryPageListByParentIsNull(Map<String, Object> params);

	List<CollageBuy> queryPageListOrderByGgSelledCount(Map<String, Object> params);

	void saveEntity(CollageBuy obj);

	void updateById(CollageBuy obj);

	void deleteById(@Param(value = "id") Long id);

	List<CollageBuy> queryPages(Map<String, Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<CollageBuy> queryPagesWithNoRelations(Map<String, Object> params);

	List<CollageBuy> queryPageListWithNoRelations(Map<String, Object> params);

}
