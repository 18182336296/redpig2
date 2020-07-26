package com.redpigmall.dao;

import com.redpigmall.domain.DistributionAgreement;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Auther: csh
 * @Date: 2018/8/27 11:24
 * @Description:分销协议
 */
public interface DistributionAgreementMapper extends SupperMapper {

    void batchDelete(List<DistributionAgreement> objs);

    List<DistributionAgreement> selectObjByProperty(Map<String, Object> maps);

    DistributionAgreement selectByPrimaryKey(Long id);

    List<DistributionAgreement> queryPageList(Map<String, Object> maps);

    Integer selectCount(Map<String, Object> maps);

    List<DistributionAgreement> queryByIds(List<Long> ids);

    List<DistributionAgreement> queryPageListByParentIsNull(Map<String, Object> params);

    void saveEntity(DistributionAgreement obj);

    void updateById(DistributionAgreement obj);

    void deleteById(@Param(value="id")Long id);

    List<DistributionAgreement> queryPages(Map<String,Object> params);

    void batchDeleteByIds(List<Long> ids);

    List<DistributionAgreement> queryPagesWithNoRelations(Map<String,Object> params);

    List<DistributionAgreement> queryPageListWithNoRelations(Map<String,Object> params);


}
