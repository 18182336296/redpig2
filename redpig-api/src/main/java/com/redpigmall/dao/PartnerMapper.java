package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Partner;

public interface PartnerMapper extends SupperMapper {

	void batchDelete(List<Partner> objs);

	List<Partner> selectObjByProperty(Map<String, Object> maps);

	Partner selectByPrimaryKey(Long id);

	List<Partner> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Partner> queryByIds(List<Long> ids);

	List<Partner> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Partner obj);

	void updateById(Partner obj);

	void deleteById(@Param(value="id")Long id);
	List<Partner> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Partner> queryPagesWithNoRelations(Map<String,Object> params);

	List<Partner> queryPageListWithNoRelations(Map<String,Object> params);

}
