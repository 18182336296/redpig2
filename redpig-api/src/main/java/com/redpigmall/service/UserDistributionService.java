package com.redpigmall.service;

import com.redpigmall.dao.UserDistributionMapper;
import com.redpigmall.domain.UserDistribution;
import com.redpigmall.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Auther: csh
 * @Date: 2018/9/3 15:05
 * @Description:
 */
@Service
@Transactional(readOnly = true)
public class UserDistributionService  extends BaseService<UserDistribution> {

    @Autowired
    private UserDistributionMapper mapper;

    @Override
    public UserDistribution selectByPrimaryKey(Long id) {
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    public List<UserDistribution> queryPages(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<UserDistribution> queryPageListWithNoRelations(Map<String, Object> params) {
        return null;
    }

    @Override
    public int selectCount(Map<String, Object> params) {
        return mapper.selectCount(params);
    }

    public List<UserDistribution> selectAll(){
        return mapper.selectAll();
    }
    public List<UserDistribution> queryTwoData(Long id){
        return mapper.queryTwoData(id);
    }
    public List<UserDistribution> queryTwoAndThreeData(Long id){
        return mapper.queryTwoAndThreeData(id);
    }

    public List<UserDistribution> selectObjByProperty(Map<String, Object> params){
        return mapper.selectObjByProperty(params);
    };

}
