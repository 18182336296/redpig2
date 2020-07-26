package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.SystemTip;

public interface SystemTipMapper extends SupperMapper {

	void batchDelete(List<SystemTip> objs);

	List<SystemTip> selectObjByProperty(Map<String, Object> maps);

	SystemTip selectByPrimaryKey(Long id);

	List<SystemTip> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<SystemTip> queryByIds(List<Long> ids);

	List<SystemTip> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(SystemTip obj);

	void updateById(SystemTip obj);

	void deleteById(@Param(value="id")Long id);
	List<SystemTip> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<SystemTip> queryPagesWithNoRelations(Map<String,Object> params);

	List<SystemTip> queryPageListWithNoRelations(Map<String,Object> params);

}
