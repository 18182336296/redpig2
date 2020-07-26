package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.ConsultSatis;

public interface ConsultSatisMapper extends SupperMapper {

	void batchDelete(List<ConsultSatis> objs);

	List<ConsultSatis> selectObjByProperty(Map<String, Object> maps);

	ConsultSatis selectByPrimaryKey(Long id);

	List<ConsultSatis> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<ConsultSatis> queryByIds(List<Long> ids);

	List<ConsultSatis> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(ConsultSatis obj);

	void updateById(ConsultSatis obj);

	void deleteById(@Param(value="id")Long id);
	List<ConsultSatis> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<ConsultSatis> queryPagesWithNoRelations(Map<String,Object> params);

	List<ConsultSatis> queryPageListWithNoRelations(Map<String,Object> params);

}
