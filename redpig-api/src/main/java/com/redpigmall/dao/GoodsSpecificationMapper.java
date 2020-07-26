package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GoodsSpecification;

public interface GoodsSpecificationMapper extends SupperMapper {

	void batchDelete(List<GoodsSpecification> objs);

	List<GoodsSpecification> selectObjByProperty(Map<String, Object> maps);

	GoodsSpecification selectByPrimaryKey(Long id);

	List<GoodsSpecification> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GoodsSpecification> queryByIds(List<Long> ids);

	List<GoodsSpecification> queryPageListByParentIsNull(Map<String, Object> params);

	int save(GoodsSpecification goodsSpecification);

	void update(GoodsSpecification goodsSpecification);

	void delete(Long id);

	void saveGoodsSpecificationGoodsClassDetail(List<Map<String, Object>> gspgcIds);

	void saveEntity(GoodsSpecification obj);

	void updateById(GoodsSpecification obj);

	void deleteById(@Param(value="id")Long id);
	List<GoodsSpecification> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GoodsSpecification> queryPagesWithNoRelations(Map<String,Object> params);

	List<GoodsSpecification> queryPageListWithNoRelations(Map<String,Object> params);

	void deleteGoodsSpecificationGoodsClassDetail(List<Map<String, Object>> gsgcds);

}
