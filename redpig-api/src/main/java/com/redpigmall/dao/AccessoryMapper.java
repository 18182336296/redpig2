package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Accessory;

public interface AccessoryMapper extends SupperMapper {

	void batchDelete(List<Accessory> objs);

	List<Accessory> selectObjByProperty(Map<String, Object> maps);

	Accessory selectByPrimaryKey(Long id);
	
	List<Accessory> queryPageList(Map<String, Object> maps);
	
	Integer selectCount(Map<String, Object> maps);

	List<Accessory> queryByIds(List<Long> ids);

	List<Accessory> queryPageListByParentIsNull(Map<String, Object> params);

	void save(Accessory photo);
	
	void update(Accessory photo);

	void delete(@Param(value="id")Long id);

	void saveEntity(Accessory obj);

	void updateById(Accessory obj);

	void deleteById(@Param(value="id")Long id);

	List<Accessory> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Accessory> queryPagesWithNoRelations(Map<String,Object> params);

	List<Accessory> queryPageListWithNoRelations(Map<String,Object> params);

}
