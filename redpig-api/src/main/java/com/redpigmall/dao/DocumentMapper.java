package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Document;

public interface DocumentMapper extends SupperMapper {

	void batchDelete(List<Document> objs);

	List<Document> selectObjByProperty(Map<String, Object> maps);

	Document selectByPrimaryKey(Long id);

	List<Document> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Document> queryByIds(List<Long> ids);

	List<Document> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Document obj);

	void updateById(Document obj);

	void deleteById(@Param(value="id")Long id);
	List<Document> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Document> queryPagesWithNoRelations(Map<String,Object> params);

	List<Document> queryPageListWithNoRelations(Map<String,Object> params);

}
