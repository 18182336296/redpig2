package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.PredepositCash;

public interface PredepositCashMapper extends SupperMapper {

	void batchDelete(List<PredepositCash> objs);

	List<PredepositCash> selectObjByProperty(Map<String, Object> maps);

	PredepositCash selectByPrimaryKey(Long id);

	List<PredepositCash> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<PredepositCash> queryByIds(List<Long> ids);

	List<PredepositCash> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(PredepositCash obj);

	void updateById(PredepositCash obj);

	void deleteById(@Param(value="id")Long id);
	List<PredepositCash> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<PredepositCash> queryPagesWithNoRelations(Map<String,Object> params);

	List<PredepositCash> queryPageListWithNoRelations(Map<String,Object> params);

}
