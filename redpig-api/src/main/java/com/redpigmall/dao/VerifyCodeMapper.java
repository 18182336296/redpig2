package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.VerifyCode;

public interface VerifyCodeMapper extends SupperMapper {

	void batchDelete(List<VerifyCode> objs);

	List<VerifyCode> selectObjByProperty(Map<String, Object> maps);

	VerifyCode selectByPrimaryKey(Long id);

	List<VerifyCode> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<VerifyCode> queryByIds(List<Long> ids);

	List<VerifyCode> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(VerifyCode obj);

	void updateById(VerifyCode obj);

	void deleteById(@Param(value="id")Long id);
	List<VerifyCode> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<VerifyCode> queryPagesWithNoRelations(Map<String,Object> params);

	List<VerifyCode> queryPageListWithNoRelations(Map<String,Object> params);

}
