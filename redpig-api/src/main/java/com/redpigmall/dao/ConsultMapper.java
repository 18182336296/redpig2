package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Consult;

public interface ConsultMapper extends SupperMapper {

	void batchDelete(List<Consult> objs);

	List<Consult> selectObjByProperty(Map<String, Object> maps);

	Consult selectByPrimaryKey(Long id);

	List<Consult> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Consult> queryByIds(List<Long> ids);

	List<Consult> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Consult obj);

	void updateById(Consult obj);

	void deleteById(@Param(value="id")Long id);
	List<Consult> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Consult> queryPagesWithNoRelations(Map<String,Object> params);

	List<Consult> queryPageListWithNoRelations(Map<String,Object> params);

}
