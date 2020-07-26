package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Album;

public interface AlbumMapper extends SupperMapper {

	void batchDelete(List<Album> objs);

	List<Album> selectObjByProperty(Map<String, Object> maps);

	Album selectByPrimaryKey(Long id);

	List<Album> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Album> queryByIds(List<Long> ids);

	List<Album> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Album obj);

	void updateById(Album obj);

	void deleteById(@Param(value="id")Long id);

	List<Album> queryPages(Map<String, Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Album> queryPagesWithNoRelations(Map<String,Object> params);

	List<Album> queryPageListWithNoRelations(Map<String,Object> params);

}
