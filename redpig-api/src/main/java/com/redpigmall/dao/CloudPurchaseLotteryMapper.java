package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.CloudPurchaseLottery;

public interface CloudPurchaseLotteryMapper extends SupperMapper {

	void batchDelete(List<CloudPurchaseLottery> objs);

	List<CloudPurchaseLottery> selectObjByProperty(Map<String, Object> maps);

	CloudPurchaseLottery selectByPrimaryKey(Long id);

	List<CloudPurchaseLottery> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<CloudPurchaseLottery> queryByIds(List<Long> ids);

	List<CloudPurchaseLottery> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(CloudPurchaseLottery obj);

	void updateById(CloudPurchaseLottery obj);

	void deleteById(@Param(value="id")Long id);
	List<CloudPurchaseLottery> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<CloudPurchaseLottery> queryPagesWithNoRelations(Map<String,Object> params);

	List<CloudPurchaseLottery> queryPageListWithNoRelations(Map<String,Object> params);

}
