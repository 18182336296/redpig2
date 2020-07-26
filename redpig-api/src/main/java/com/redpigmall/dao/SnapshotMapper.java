package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Snapshot;

public interface SnapshotMapper extends SupperMapper {

	void batchDelete(List<Snapshot> objs);

	List<Snapshot> selectObjByProperty(Map<String, Object> maps);

	Snapshot selectByPrimaryKey(Long id);

	List<Snapshot> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Snapshot> queryByIds(List<Long> ids);

	List<Snapshot> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Snapshot obj);

	void updateById(Snapshot obj);

	void deleteById(@Param(value="id")Long id);
	List<Snapshot> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Snapshot> queryPagesWithNoRelations(Map<String,Object> params);

	List<Snapshot> queryPageListWithNoRelations(Map<String,Object> params);

}
