package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.MerchantServices;

public interface MerchantServicesMapper extends SupperMapper {

	void batchDelete(List<MerchantServices> objs);

	List<MerchantServices> selectObjByProperty(Map<String, Object> maps);

	MerchantServices selectByPrimaryKey(Long id);

	List<MerchantServices> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<MerchantServices> queryByIds(List<Long> ids);

	List<MerchantServices> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(MerchantServices obj);

	void updateById(MerchantServices obj);

	void deleteById(@Param(value="id")Long id);
	List<MerchantServices> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<MerchantServices> queryPagesWithNoRelations(Map<String,Object> params);

	List<MerchantServices> queryPageListWithNoRelations(Map<String,Object> params);

}
