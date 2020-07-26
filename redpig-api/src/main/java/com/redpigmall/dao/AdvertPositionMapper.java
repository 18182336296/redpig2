package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.AdvertPosition;

public interface AdvertPositionMapper extends SupperMapper {

	void batchDelete(List<AdvertPosition> objs);

	List<AdvertPosition> selectObjByProperty(Map<String, Object> maps);

	AdvertPosition selectByPrimaryKey(Long id);

	List<AdvertPosition> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<AdvertPosition> queryByIds(List<Long> ids);

	List<AdvertPosition> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(AdvertPosition obj);

	void updateById(AdvertPosition obj);

	void deleteById(@Param(value="id")Long id);

	List<AdvertPosition> queryPages(Map<String, Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<AdvertPosition> queryPagesWithNoRelations(Map<String,Object> params);

	List<AdvertPosition> queryPageListWithNoRelations(Map<String,Object> params);

}
