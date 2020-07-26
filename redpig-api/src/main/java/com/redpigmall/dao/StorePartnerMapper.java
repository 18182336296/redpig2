package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.StorePartner;

public interface StorePartnerMapper extends SupperMapper {

	void batchDelete(List<StorePartner> objs);

	List<StorePartner> selectObjByProperty(Map<String, Object> maps);

	StorePartner selectByPrimaryKey(Long id);

	List<StorePartner> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<StorePartner> queryByIds(List<Long> ids);

	List<StorePartner> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(StorePartner obj);

	void updateById(StorePartner obj);

	void deleteById(@Param(value="id")Long id);
	List<StorePartner> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<StorePartner> queryPagesWithNoRelations(Map<String,Object> params);

	List<StorePartner> queryPageListWithNoRelations(Map<String,Object> params);

}
