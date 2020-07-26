package com.redpigmall.dao;

import com.redpigmall.domain.DistributionGrade;

import java.util.List;
import java.util.Map;

/**
 * @Auther: csh
 * @Date: 2018/8/27 11:24
 * @Description:会员等级
 */
public interface DistributionGradeMapper extends SupperMapper {
    /**通过主键查询*/
    DistributionGrade selectByPrimaryKey(Long id);
    /**通过条件查询*/
    List<DistributionGrade> queryPageList(Map<String,Object> maps);
    /**添加数据*/
    void saveEntity(DistributionGrade distributionGrade);
    /**参数查询*/
    List<DistributionGrade> queryPages(Map<String,Object> params);
    /**统计条数*/
    Integer selectCount(Map<String, Object> maps);
    /**通过id更新*/
    void updateById(DistributionGrade obj);
    /**通过条件查询*/
    DistributionGrade queryByCondition(Map<String, Object> maps);
}
