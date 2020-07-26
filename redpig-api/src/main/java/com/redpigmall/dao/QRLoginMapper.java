package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.QRLogin;

public interface QRLoginMapper extends SupperMapper {

	void batchDelete(List<QRLogin> objs);

	List<QRLogin> selectObjByProperty(Map<String, Object> maps);

	QRLogin selectByPrimaryKey(Long id);

	List<QRLogin> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<QRLogin> queryByIds(List<Long> ids);

	List<QRLogin> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(QRLogin obj);

	void updateById(QRLogin obj);

	void deleteById(@Param(value="id")Long id);
	List<QRLogin> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<QRLogin> queryPagesWithNoRelations(Map<String,Object> params);

	List<QRLogin> queryPageListWithNoRelations(Map<String,Object> params);

}
