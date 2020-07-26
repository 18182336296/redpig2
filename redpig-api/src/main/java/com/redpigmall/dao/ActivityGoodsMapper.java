package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.ActivityGoods;

public interface ActivityGoodsMapper extends SupperMapper {

	void batchDelete(List<ActivityGoods> objs);

	List<ActivityGoods> selectObjByProperty(Map<String, Object> maps);

	ActivityGoods selectByPrimaryKey(Long id);

	List<ActivityGoods> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<ActivityGoods> queryByIds(List<Long> ids);

	List<ActivityGoods> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(ActivityGoods obj);

	void updateById(ActivityGoods obj);

	void deleteById(@Param(value="id")Long id);

	List<ActivityGoods> queryPages(Map<String, Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<ActivityGoods> queryPagesWithNoRelations(Map<String,Object> params);

	List<ActivityGoods> queryPageListWithNoRelations(Map<String,Object> params);

}
