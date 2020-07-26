package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GroupGoods;

public interface GroupGoodsMapper extends SupperMapper {

	void batchDelete(List<GroupGoods> objs);

	List<GroupGoods> selectObjByProperty(Map<String, Object> maps);

	GroupGoods selectByPrimaryKey(Long id);

	List<GroupGoods> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GroupGoods> queryByIds(List<Long> ids);

	List<GroupGoods> queryPageListByParentIsNull(Map<String, Object> params);

	List<GroupGoods> queryPageListOrderByGgSelledCount(Map<String, Object> params);

	void saveEntity(GroupGoods obj);

	void updateById(GroupGoods obj);

	void deleteById(@Param(value="id")Long id);

	List<GroupGoods> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GroupGoods> queryPagesWithNoRelations(Map<String,Object> params);

	List<GroupGoods> queryPageListWithNoRelations(Map<String,Object> params);

}
