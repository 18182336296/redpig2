package com.redpigmall.dao;

import com.redpigmall.domain.LuckydrawRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface LuckydrawRecordMapper extends SupperMapper {

	void batchDelete(List<LuckydrawRecord> objs);

	List<LuckydrawRecord> selectObjByProperty(Map<String, Object> maps);

	LuckydrawRecord selectByPrimaryKey(Long id);

	List<LuckydrawRecord> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<LuckydrawRecord> queryByIds(List<Long> ids);

	List<LuckydrawRecord> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(LuckydrawRecord obj);

	void updateById(LuckydrawRecord obj);

	void deleteById(@Param(value = "id") Long id);

	List<LuckydrawRecord> queryPages(Map<String, Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<LuckydrawRecord> queryPagesWithNoRelations(Map<String, Object> params);

	List<LuckydrawRecord> queryPageListWithNoRelations(Map<String, Object> params);

}
