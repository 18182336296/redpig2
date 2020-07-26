package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.StoreAdjustInfo;

public interface StoreAdjustInfoMapper extends SupperMapper {

	void batchDelete(List<StoreAdjustInfo> objs);

	List<StoreAdjustInfo> selectObjByProperty(Map<String, Object> maps);

	StoreAdjustInfo selectByPrimaryKey(Long id);

	List<StoreAdjustInfo> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<StoreAdjustInfo> queryByIds(List<Long> ids);

	List<StoreAdjustInfo> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(StoreAdjustInfo obj);

	void updateById(StoreAdjustInfo obj);

	void deleteById(@Param(value="id")Long id);
	List<StoreAdjustInfo> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<StoreAdjustInfo> queryPagesWithNoRelations(Map<String,Object> params);

	List<StoreAdjustInfo> queryPageListWithNoRelations(Map<String,Object> params);

}
