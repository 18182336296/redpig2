package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Template;

public interface TemplateMapper extends SupperMapper {

	void batchDelete(List<Template> objs);

	List<Template> selectObjByProperty(Map<String, Object> maps);

	Template selectByPrimaryKey(Long id);

	List<Template> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Template> queryByIds(List<Long> ids);

	List<Template> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Template obj);

	void updateById(Template obj);

	void deleteById(@Param(value="id")Long id);
	List<Template> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Template> queryPagesWithNoRelations(Map<String,Object> params);

	List<Template> queryPageListWithNoRelations(Map<String,Object> params);

}
