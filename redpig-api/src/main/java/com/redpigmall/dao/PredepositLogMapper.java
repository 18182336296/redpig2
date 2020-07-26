package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.PredepositLog;

public interface PredepositLogMapper extends SupperMapper {

	void batchDelete(List<PredepositLog> objs);

	List<PredepositLog> selectObjByProperty(Map<String, Object> maps);

	PredepositLog selectByPrimaryKey(Long id);

	List<PredepositLog> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<PredepositLog> queryByIds(List<Long> ids);

	List<PredepositLog> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(PredepositLog obj);

	void updateById(PredepositLog obj);

	void deleteById(@Param(value="id")Long id);
	List<PredepositLog> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<PredepositLog> queryPagesWithNoRelations(Map<String,Object> params);

	List<PredepositLog> queryPageListWithNoRelations(Map<String,Object> params);

}
