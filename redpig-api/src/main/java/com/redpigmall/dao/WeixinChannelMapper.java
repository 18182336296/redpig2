package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.WeixinChannel;

public interface WeixinChannelMapper extends SupperMapper {

	void batchDelete(List<WeixinChannel> objs);

	List<WeixinChannel> selectObjByProperty(Map<String, Object> maps);

	WeixinChannel selectByPrimaryKey(Long id);

	List<WeixinChannel> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<WeixinChannel> queryByIds(List<Long> ids);

	List<WeixinChannel> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(WeixinChannel obj);

	void updateById(WeixinChannel obj);

	void deleteById(@Param(value="id")Long id);
	List<WeixinChannel> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<WeixinChannel> queryPagesWithNoRelations(Map<String,Object> params);

	List<WeixinChannel> queryPageListWithNoRelations(Map<String,Object> params);

}
