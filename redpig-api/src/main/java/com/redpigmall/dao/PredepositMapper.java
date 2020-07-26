package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Predeposit;

public interface PredepositMapper extends SupperMapper {

	void batchDelete(List<Predeposit> objs);

	List<Predeposit> selectObjByProperty(Map<String, Object> maps);

	Predeposit selectByPrimaryKey(Long id);

	List<Predeposit> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Predeposit> queryByIds(List<Long> ids);

	List<Predeposit> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Predeposit obj);

	void updateById(Predeposit obj);

	void deleteById(@Param(value="id")Long id);
	List<Predeposit> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Predeposit> queryPagesWithNoRelations(Map<String,Object> params);

	List<Predeposit> queryPageListWithNoRelations(Map<String,Object> params);

}
