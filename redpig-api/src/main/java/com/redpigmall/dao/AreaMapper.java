package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Area;

public interface AreaMapper extends SupperMapper {

	void batchDelete(List<Area> objs);

	List<Area> selectObjByProperty(Map<String, Object> maps);

	Area selectByPrimaryKey(Long id);

	List<Area> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Area> queryByIds(List<Long> ids);

	List<Area> queryPageListByParentIsNull(Map<String, Object> params);

	List<Area> queryPages(Map<String, Object> params);

	void update(Area obj);

	void delete(Area obj);

	List<Area> queryChilds(Area obj);

	void deleteBatch(List<Long> listIds);

	void save(Area area);
	void saveEntity(Area obj);

	void updateById(Area obj);

	void deleteById(@Param(value="id")Long id);

	void batchDeleteByIds(List<Long> ids);

	List<Area> queryPagesWithNoRelations(Map<String,Object> params);

	List<Area> queryPageListWithNoRelations(Map<String,Object> params);

}
