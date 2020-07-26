package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Article;

public interface ArticleMapper extends SupperMapper {

	void batchDelete(List<Article> objs);

	List<Article> selectObjByProperty(Map<String, Object> maps);

	Article selectByPrimaryKey(Long id);

	List<Article> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Article> queryByIds(List<Long> ids);

	List<Article> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Article obj);

	void updateById(Article obj);

	void deleteById(@Param(value="id")Long id);
	List<Article> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Article> queryPagesWithNoRelations(Map<String,Object> params);

	List<Article> queryPageListWithNoRelations(Map<String,Object> params);

}
