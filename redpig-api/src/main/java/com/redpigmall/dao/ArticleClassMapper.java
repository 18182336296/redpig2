package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.ArticleClass;

public interface ArticleClassMapper extends SupperMapper {

	void batchDelete(List<ArticleClass> objs);

	List<ArticleClass> selectObjByProperty(Map<String, Object> maps);

	ArticleClass selectByPrimaryKey(Long id);
	
	List<ArticleClass> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<ArticleClass> queryByIds(List<Long> ids);

	List<ArticleClass> queryPageListByParentIsNull(Map<String, Object> params);

	List<ArticleClass> findArticleClassByMark(List<String> marks);
	
	void update(ArticleClass ac);

	void saveEntity(ArticleClass obj);

	void updateById(ArticleClass obj);

	void deleteById(@Param(value="id")Long id);
	List<ArticleClass> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<ArticleClass> queryPagesWithNoRelations(Map<String,Object> params);

	List<ArticleClass> queryPageListWithNoRelations(Map<String,Object> params);

}
