package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.ComplaintGoods;

public interface ComplaintGoodsMapper extends SupperMapper {

	void batchDelete(List<ComplaintGoods> objs);

	List<ComplaintGoods> selectObjByProperty(Map<String, Object> maps);

	ComplaintGoods selectByPrimaryKey(Long id);

	List<ComplaintGoods> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<ComplaintGoods> queryByIds(List<Long> ids);

	List<ComplaintGoods> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(ComplaintGoods obj);

	void updateById(ComplaintGoods obj);

	void deleteById(@Param(value="id")Long id);
	List<ComplaintGoods> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<ComplaintGoods> queryPagesWithNoRelations(Map<String,Object> params);

	List<ComplaintGoods> queryPageListWithNoRelations(Map<String,Object> params);


}
