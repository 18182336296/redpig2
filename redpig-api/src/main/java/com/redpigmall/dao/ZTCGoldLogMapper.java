package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.ZTCGoldLog;

public interface ZTCGoldLogMapper extends SupperMapper {

	void batchDelete(List<ZTCGoldLog> objs);

	List<ZTCGoldLog> selectObjByProperty(Map<String, Object> maps);

	ZTCGoldLog selectByPrimaryKey(Long id);

	List<ZTCGoldLog> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<ZTCGoldLog> queryByIds(List<Long> ids);

	List<ZTCGoldLog> queryPageListByParentIsNull(Map<String, Object> params);
	
	void deleteByGoodsId(@Param(value="id")Long id);

	void saveEntity(ZTCGoldLog obj);

	void updateById(ZTCGoldLog obj);

	void deleteById(@Param(value="id")Long id);
	List<ZTCGoldLog> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<ZTCGoldLog> queryPagesWithNoRelations(Map<String,Object> params);

	List<ZTCGoldLog> queryPageListWithNoRelations(Map<String,Object> params);

}
