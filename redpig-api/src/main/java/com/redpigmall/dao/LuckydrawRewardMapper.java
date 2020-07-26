package com.redpigmall.dao;

import com.redpigmall.domain.LuckydrawReward;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface LuckydrawRewardMapper extends SupperMapper {

	void batchDelete(List<LuckydrawReward> objs);

	List<LuckydrawReward> selectObjByProperty(Map<String, Object> maps);

	LuckydrawReward selectByPrimaryKey(Long id);

	List<LuckydrawReward> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<LuckydrawReward> queryByIds(List<Long> ids);

	List<LuckydrawReward> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(LuckydrawReward obj);

	void updateById(LuckydrawReward obj);

	void deleteById(@Param(value = "id") Long id);

	List<LuckydrawReward> queryPages(Map<String, Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<LuckydrawReward> queryPagesWithNoRelations(Map<String, Object> params);

	List<LuckydrawReward> queryPageListWithNoRelations(Map<String, Object> params);

}
