package com.redpigmall.service;

import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.DistributionCommissionMapper;
import com.redpigmall.domain.DistributionCommission;
import com.redpigmall.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Auther: csh
 * @Date: 2018/8/27 11:22
 * @Description:分销拥金服务接口实现
 */
@Service
@Transactional(readOnly = true)
public class RedPigDistributionCommissionService extends BaseService<DistributionCommission> {

    @Autowired
    private DistributionCommissionMapper mapper;

    @Override
    public DistributionCommission selectByPrimaryKey(Long id) {
        return mapper.selectByPrimaryKey(id) ;
    }

    public DistributionCommission selectByUserId(Long id) {
        return mapper.selectByUserId(id) ;
    }


    @Transactional(readOnly = false)
    public void saveEntity(DistributionCommission obj) {
        mapper.saveEntity(obj);
    }

    @Override
    public List<DistributionCommission> queryPages(Map<String, Object> params) {
        return mapper.queryPages(params);
    }

    @Override
    public List<DistributionCommission> queryPageListWithNoRelations(Map<String, Object> params) {
        return null;
    }


    @Override
    public List<DistributionCommission> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
        return super.queryPageList(params, begin, max);
    }

    @Override
    public int selectCount(Map<String, Object> params) {
        Integer count = mapper.selectCount(params);
        if(count==null){
            return 0;
        }
        return count;
    }

    @Override
    public IPageList list(Map<String, Object> params) {
        return super.listPages(params);
    }

    @Transactional(readOnly = false)
    public void updateById(DistributionCommission obj) {
        mapper.updateById(obj);
    }
}
