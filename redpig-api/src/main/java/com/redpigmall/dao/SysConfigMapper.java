package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.SysConfig;

public interface SysConfigMapper extends SupperMapper {

	void batchDelete(List<SysConfig> objs);

	List<SysConfig> selectObjByProperty(Map<String, Object> maps);

	SysConfig selectByPrimaryKey(Long id);

	List<SysConfig> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<SysConfig> queryByIds(List<Long> ids);

	List<SysConfig> queryPageListByParentIsNull(Map<String, Object> params);

	void save(SysConfig config);

	void update(SysConfig config);

	void saveEntity(SysConfig obj);

	void updateById(SysConfig obj);

	void deleteById(@Param(value="id")Long id);
	List<SysConfig> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<SysConfig> queryPagesWithNoRelations(Map<String,Object> params);

	List<SysConfig> queryPageListWithNoRelations(Map<String,Object> params);

}
