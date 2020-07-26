package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GoodsBrand;

public interface GoodsBrandMapper extends SupperMapper {

	void batchDelete(List<GoodsBrand> objs);

	List<GoodsBrand> selectObjByProperty(Map<String, Object> maps);

	GoodsBrand selectByPrimaryKey(Long id);

	List<GoodsBrand> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GoodsBrand> queryByIds(List<Long> ids);

	List<GoodsBrand> queryPageListByParentIsNull(Map<String, Object> params);

	List<GoodsBrand> queryPages(Map<String, Object> params);

	void update(GoodsBrand goodsBrand);

	void save(GoodsBrand goodsBrand);

	void deleteGoodsBrandAndGoodsType(List<Map<String, Long>> gbtIds);

	int delete(Long id);

	void saveGoodsBrandAndGoodsType(List<Map<String, Long>> gbtIds);
	
	void saveEntity(GoodsBrand obj);

	void updateById(GoodsBrand obj);

	void deleteById(@Param(value="id")Long id);

	void batchDeleteByIds(List<Long> ids);

	List<GoodsBrand> queryPagesWithNoRelations(Map<String,Object> params);

	List<GoodsBrand> queryPageListWithNoRelations(Map<String,Object> params);

	List<Map<String, Object>> queryListWithNoRelations(Map<String, Object> params);

}
