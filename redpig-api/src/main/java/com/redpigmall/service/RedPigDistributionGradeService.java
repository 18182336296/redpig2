package com.redpigmall.service;

import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.DistributionGradeMapper;
import com.redpigmall.domain.DistributionGrade;
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
public class RedPigDistributionGradeService extends BaseService<DistributionGrade> {

    @Autowired
    private DistributionGradeMapper mapper;

    @Override
    public DistributionGrade selectByPrimaryKey(Long id) {
        return mapper.selectByPrimaryKey(id) ;
    }

    @Transactional(readOnly = false)
    public void saveEntity(DistributionGrade obj) {
        mapper.saveEntity(obj);
    }

    @Override
    public List<DistributionGrade> queryPages(Map<String, Object> params) {
        return mapper.queryPages(params);
    }

    @Override
    public List<DistributionGrade> queryPageListWithNoRelations(Map<String, Object> params) {
        return null;
    }


    @Override
    public List<DistributionGrade> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
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
    public void updateById(DistributionGrade obj) {
        mapper.updateById(obj);
    }

    public DistributionGrade queryByCondition(Map<String, Object> params){
        return mapper.queryByCondition(params);
    }
}
