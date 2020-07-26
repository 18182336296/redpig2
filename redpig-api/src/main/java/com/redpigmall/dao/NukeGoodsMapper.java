package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.NukeGoods;

public interface NukeGoodsMapper extends SupperMapper {

	void batchDelete(List<NukeGoods> objs);

	List<NukeGoods> selectObjByProperty(Map<String, Object> maps);

	NukeGoods selectByPrimaryKey(Long id);

	List<NukeGoods> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<NukeGoods> queryByIds(List<Long> ids);

	List<NukeGoods> queryPageListByParentIsNull(Map<String, Object> params);

	List<NukeGoods> queryPageListOrderByGgSelledCount(Map<String, Object> params);

	void saveEntity(NukeGoods obj);

	void updateById(NukeGoods obj);

	void deleteById(@Param(value="id")Long id);

	List<NukeGoods> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<NukeGoods> queryPagesWithNoRelations(Map<String,Object> params);

	List<NukeGoods> queryPageListWithNoRelations(Map<String,Object> params);

}
