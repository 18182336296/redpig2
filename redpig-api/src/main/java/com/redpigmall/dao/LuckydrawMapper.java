package com.redpigmall.dao;

import com.redpigmall.domain.Luckydraw;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface LuckydrawMapper extends SupperMapper {

	void batchDelete(List<Luckydraw> objs);

	List<Luckydraw> selectObjByProperty(Map<String, Object> maps);

	Luckydraw selectByPrimaryKey(Long id);

	List<Luckydraw> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Luckydraw> queryByIds(List<Long> ids);

	List<Luckydraw> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Luckydraw obj);

	void updateById(Luckydraw obj);

	void deleteById(@Param(value = "id") Long id);

	List<Luckydraw> queryPages(Map<String, Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Luckydraw> queryPagesWithNoRelations(Map<String, Object> params);

	List<Luckydraw> queryPageListWithNoRelations(Map<String, Object> params);

}
