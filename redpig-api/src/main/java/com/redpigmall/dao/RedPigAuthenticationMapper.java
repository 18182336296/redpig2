package com.redpigmall.dao;

import com.redpigmall.domain.Authentication;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Auther: Administrator
 * @Date: 2018/9/15 17:21
 * @Description:认证信息
 */
public interface RedPigAuthenticationMapper extends SupperMapper{
    void batchDelete(List<Authentication> objs);

    List<Authentication> selectObjByProperty(Map<String, Object> maps);

    Authentication selectByPrimaryKey(Long id);

    List<Authentication> queryPageList(Map<String, Object> maps);

    Integer selectCount(Map<String, Object> maps);

    List<Authentication> queryByIds(List<Long> ids);

    List<Authentication> queryPageListByParentIsNull(Map<String, Object> params);

    void saveEntity(Authentication obj);

    void updateById(Authentication obj);

    void deleteById(@Param(value="id")Long id);

    List<Authentication> queryPages(Map<String,Object> params);

    void batchDeleteByIds(List<Long> ids);

    List<Authentication> queryPagesWithNoRelations(Map<String,Object> params);

    List<Authentication> queryPageListWithNoRelations(Map<String,Object> params);
}
