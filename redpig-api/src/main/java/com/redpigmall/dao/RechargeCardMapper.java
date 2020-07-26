package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.RechargeCard;

public interface RechargeCardMapper extends SupperMapper {

	void batchDelete(List<RechargeCard> objs);

	List<RechargeCard> selectObjByProperty(Map<String, Object> maps);

	RechargeCard selectByPrimaryKey(Long id);

	List<RechargeCard> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<RechargeCard> queryByIds(List<Long> ids);

	List<RechargeCard> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(RechargeCard obj);

	void updateById(RechargeCard obj);

	void deleteById(@Param(value="id")Long id);

	List<RechargeCard> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<RechargeCard> queryPagesWithNoRelations(Map<String,Object> params);

	List<RechargeCard> queryPageListWithNoRelations(Map<String,Object> params);

}
