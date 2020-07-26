package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.StoreSlide;

public interface StoreSlideMapper extends SupperMapper {

	void batchDelete(List<StoreSlide> objs);

	List<StoreSlide> selectObjByProperty(Map<String, Object> maps);

	StoreSlide selectByPrimaryKey(Long id);

	List<StoreSlide> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<StoreSlide> queryByIds(List<Long> ids);

	List<StoreSlide> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(StoreSlide obj);

	void updateById(StoreSlide obj);

	void deleteById(@Param(value="id")Long id);
	List<StoreSlide> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<StoreSlide> queryPagesWithNoRelations(Map<String,Object> params);

	List<StoreSlide> queryPageListWithNoRelations(Map<String,Object> params);

}
