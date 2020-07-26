package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GoldRecord;

public interface GoldRecordMapper extends SupperMapper {

	void batchDelete(List<GoldRecord> objs);

	List<GoldRecord> selectObjByProperty(Map<String, Object> maps);

	GoldRecord selectByPrimaryKey(Long id);

	List<GoldRecord> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GoldRecord> queryByIds(List<Long> ids);

	List<GoldRecord> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(GoldRecord obj);

	void updateById(GoldRecord obj);

	void deleteById(@Param(value="id")Long id);

	List<GoldRecord> queryPages(Map<String, Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GoldRecord> queryPagesWithNoRelations(Map<String,Object> params);

	List<GoldRecord> queryPageListWithNoRelations(Map<String,Object> params);

}
