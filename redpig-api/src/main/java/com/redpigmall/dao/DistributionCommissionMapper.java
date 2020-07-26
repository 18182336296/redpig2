package com.redpigmall.dao;

import com.redpigmall.domain.DistributionCommission;

import java.util.List;
import java.util.Map;

/**
 * @Auther: csh
 * @Date: 2018/8/27 11:24
 * @Description:分销拥金
 */
public interface DistributionCommissionMapper extends SupperMapper {
    /**通过主键查询*/
    DistributionCommission selectByPrimaryKey(Long id);
    /**通过用户id查询*/
    DistributionCommission selectByUserId(Long id);
    /**通过条件查询*/
    List<DistributionCommission> queryPageList(Map<String, Object> maps);
    /**添加数据*/
    void saveEntity(DistributionCommission distributionGrade);
    /**参数查询*/
    List<DistributionCommission> queryPages(Map<String, Object> params);
    /**统计条数*/
    Integer selectCount(Map<String, Object> maps);
    /**通过id更新*/
    void updateById(DistributionCommission obj);
}
