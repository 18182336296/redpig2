package com.redpigmall.service;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.DistributionAgreementMapper;
import com.redpigmall.domain.DistributionAgreement;
import com.redpigmall.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Auther: csh
 * @Date: 2018/8/27 11:22
 * @Description:
 */
@Service
@Transactional(readOnly = true)
public class RedPigDistributionAgreementService extends BaseService<DistributionAgreement> {

    @Autowired
    DistributionAgreementMapper agreementMapper;

    @Transactional(readOnly = false)
    public void batchDelObjs(List<DistributionAgreement> objs) {
        if (objs != null && objs.size() > 0) {
            agreementMapper.batchDelete(objs);
        }
    }

    public DistributionAgreement getObjByProperty(String key, String operation_symbol, Object value) {
        Map<String, Object> maps = Maps.newHashMap();
        maps.put("operation_property", key);
        maps.put("operation_symbol", operation_symbol);
        maps.put("operation_value", value);
        List<DistributionAgreement> objs = agreementMapper.selectObjByProperty(maps);
        if (objs != null && objs.size() > 0) {
            return objs.get(0);
        }
        return null;
    }


    public List<DistributionAgreement> selectObjByProperty(Map<String, Object> maps) {
        return agreementMapper.selectObjByProperty(maps);
    }


    public List<DistributionAgreement> queryPages(Map<String, Object> params) {
        return agreementMapper.queryPages(params);
    }


    public List<DistributionAgreement> queryPageListWithNoRelations(Map<String, Object> param) {
        return agreementMapper.queryPageListWithNoRelations(param);
    }


    public List<DistributionAgreement> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
                                                     Integer pageSize) {
        return agreementMapper.queryPagesWithNoRelations(params);
    }


    public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
        return super.queryPagesWithNoRelations(params);
    }


    public IPageList list(Map<String, Object> params) {
        return super.listPages(params);
    }

    @Transactional(readOnly = false)
    public void batchDeleteByIds(List<Long> ids) {
        agreementMapper.batchDeleteByIds(ids);
    }


    @Transactional(readOnly = false)
    public void saveEntity(DistributionAgreement obj) {
        agreementMapper.saveEntity(obj);
    }


    @Transactional(readOnly = false)
    public void updateById(DistributionAgreement obj) {
        agreementMapper.updateById(obj);
    }


    @Transactional(readOnly = false)
    public void deleteById(Long id) {
        agreementMapper.deleteById(id);
    }


    public DistributionAgreement selectByPrimaryKey(Long id) {
        return agreementMapper.selectByPrimaryKey(id);
    }


    public List<DistributionAgreement> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
        return super.queryPageList(params, begin, max);
    }


    public List<DistributionAgreement> queryPageList(Map<String, Object> params) {
        return this.queryPageList(params, null, null);
    }


    public int selectCount(Map<String, Object> params) {
        Integer c = agreementMapper.selectCount(params);
        if (c == null) {
            return 0;
        }

        return c;

    }
}
