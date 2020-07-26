package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Advert;

public interface AdvertMapper extends SupperMapper {

	void batchDelete(List<Advert> objs);

	List<Advert> selectObjByProperty(Map<String, Object> maps);

	Advert selectByPrimaryKey(Long id);

	List<Advert> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Advert> queryByIds(List<Long> ids);

	List<Advert> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Advert obj);

	void updateById(Advert obj);

	void deleteById(@Param(value="id")Long id);

	List<Advert> queryPages(Map<String, Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Advert> queryPagesWithNoRelations(Map<String,Object> params);

	List<Advert> queryPageListWithNoRelations(Map<String,Object> params);

	@SuppressWarnings("rawtypes")
	List<Map> getAdvertImg(Map<String, Object> map);

}
