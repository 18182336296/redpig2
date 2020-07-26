package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.LimitSelling;

public interface LimitSellingMapper extends SupperMapper {

	void batchDelete(List<LimitSelling> objs);

	List<LimitSelling> selectObjByProperty(Map<String, Object> maps);

	LimitSelling selectByPrimaryKey(Long id);

	List<LimitSelling> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<LimitSelling> queryByIds(List<Long> ids);

	List<LimitSelling> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(LimitSelling obj);

	void updateById(LimitSelling obj);

	void deleteById(@Param(value="id")Long id);

	List<LimitSelling> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<LimitSelling> queryPagesWithNoRelations(Map<String,Object> params);

	List<LimitSelling> queryPageListWithNoRelations(Map<String,Object> params);

}
