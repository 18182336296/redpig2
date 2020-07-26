package com.redpigmall.service;


import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.RedPigAuthenticationMapper;
import com.redpigmall.domain.Authentication;
import com.redpigmall.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Auther: Administrator
 * @Date: 2018/9/15 17:02
 * @Description:
 */
@Service
@Transactional(readOnly = true)
public class RedPigAuthenticationService extends BaseService<Authentication> {

    @Autowired
    private RedPigAuthenticationMapper redPigAuthenticationMapper;

    @Transactional(readOnly = false)
    public void batchDelObjs(List<Authentication> objs) {
        if (objs != null && objs.size() > 0) {
            redPigAuthenticationMapper.batchDelete(objs);
        }
    }

    public Authentication getObjByProperty(String key, String operation_symbol, Object value) {
        Map<String, Object> maps = Maps.newHashMap();
        maps.put("operation_property", key);
        maps.put("operation_symbol", operation_symbol);
        maps.put("operation_value", value);
        List<Authentication> objs = redPigAuthenticationMapper.selectObjByProperty(maps);
        if (objs != null && objs.size() > 0) {
            return objs.get(0);
        }
        return null;
    }


    public List<Authentication> selectObjByProperty(Map<String, Object> maps) {
        return redPigAuthenticationMapper.selectObjByProperty(maps);
    }


    public List<Authentication> queryPages(Map<String, Object> params) {
        return redPigAuthenticationMapper.queryPages(params);
    }


    public List<Authentication> queryPageListWithNoRelations(Map<String, Object> param) {
        return redPigAuthenticationMapper.queryPageListWithNoRelations(param);
    }


    public List<Authentication> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
                                                                 Integer pageSize) {
        return redPigAuthenticationMapper.queryPagesWithNoRelations(params);
    }


    public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
        return super.queryPagesWithNoRelations(params);
    }


    public IPageList list(Map<String, Object> params) {
        return super.listPages(params);
    }

    @Transactional(readOnly = false)
    public void batchDeleteByIds(List<Long> ids) {
        redPigAuthenticationMapper.batchDeleteByIds(ids);
    }


    @Transactional(readOnly = false)
    public void saveEntity(Authentication obj) {
        redPigAuthenticationMapper.saveEntity(obj);
    }


    @Transactional(readOnly = false)
    public void updateById(Authentication obj) {
        redPigAuthenticationMapper.updateById(obj);
    }


    @Transactional(readOnly = false)
    public void deleteById(Long id) {
        redPigAuthenticationMapper.deleteById(id);
    }


    public Authentication selectByPrimaryKey(Long id) {
        return redPigAuthenticationMapper.selectByPrimaryKey(id);
    }


    public List<Authentication> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
        return super.queryPageList(params, begin, max);
    }


    public List<Authentication> queryPageList(Map<String, Object> params) {
        return this.queryPageList(params, null, null);
    }


    public int selectCount(Map<String, Object> params) {
        Integer c = redPigAuthenticationMapper.selectCount(params);
        if (c == null) {
            return 0;
        }

        return c;

    }
}
