package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GroupLifeGoods;

public interface GroupLifeGoodsMapper extends SupperMapper {

	void batchDelete(List<GroupLifeGoods> objs);

	List<GroupLifeGoods> selectObjByProperty(Map<String, Object> maps);

	GroupLifeGoods selectByPrimaryKey(Long id);

	List<GroupLifeGoods> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GroupLifeGoods> queryByIds(List<Long> ids);

	List<GroupLifeGoods> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(GroupLifeGoods obj);

	void updateById(GroupLifeGoods obj);

	void deleteById(@Param(value="id")Long id);

	List<GroupLifeGoods> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GroupLifeGoods> queryPagesWithNoRelations(Map<String,Object> params);

	List<GroupLifeGoods> queryPageListWithNoRelations(Map<String,Object> params);

}
