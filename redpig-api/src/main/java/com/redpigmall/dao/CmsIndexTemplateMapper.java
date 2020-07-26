package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.CmsIndexTemplate;

public interface CmsIndexTemplateMapper extends SupperMapper {

	void batchDelete(List<CmsIndexTemplate> objs);

	List<CmsIndexTemplate> selectObjByProperty(Map<String, Object> maps);

	CmsIndexTemplate selectByPrimaryKey(Long id);

	List<CmsIndexTemplate> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<CmsIndexTemplate> queryByIds(List<Long> ids);

	List<CmsIndexTemplate> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(CmsIndexTemplate obj);

	void updateById(CmsIndexTemplate obj);

	void deleteById(@Param(value="id")Long id);
	List<CmsIndexTemplate> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<CmsIndexTemplate> queryPagesWithNoRelations(Map<String,Object> params);

	List<CmsIndexTemplate> queryPageListWithNoRelations(Map<String,Object> params);

}
