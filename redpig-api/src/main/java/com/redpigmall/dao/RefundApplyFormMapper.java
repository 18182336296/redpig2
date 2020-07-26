package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.RefundApplyForm;

public interface RefundApplyFormMapper extends SupperMapper {

	void batchDelete(List<RefundApplyForm> objs);

	List<RefundApplyForm> selectObjByProperty(Map<String, Object> maps);

	RefundApplyForm selectByPrimaryKey(Long id);

	List<RefundApplyForm> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<RefundApplyForm> queryByIds(List<Long> ids);

	List<RefundApplyForm> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(RefundApplyForm obj);

	void updateById(RefundApplyForm obj);

	void deleteById(@Param(value="id")Long id);
	List<RefundApplyForm> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<RefundApplyForm> queryPagesWithNoRelations(Map<String,Object> params);

	List<RefundApplyForm> queryPageListWithNoRelations(Map<String,Object> params);

}
