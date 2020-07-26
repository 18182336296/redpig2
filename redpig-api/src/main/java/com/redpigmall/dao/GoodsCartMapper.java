package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GoodsCart;

public interface GoodsCartMapper extends SupperMapper {

	void batchDelete(List<GoodsCart> objs);

	List<GoodsCart> selectObjByProperty(Map<String, Object> maps);

	GoodsCart selectByPrimaryKey(Long id);

	List<GoodsCart> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GoodsCart> queryByIds(List<Long> ids);

	List<GoodsCart> queryPageListByParentIsNull(Map<String, Object> params);
	
	List<GoodsCart> findGoodsCartByIds(Map<String,Object> params);
	
	void deletelById(Long id);
	
	void saveEntity(GoodsCart obj);

	void updateById(GoodsCart obj);

	void deleteById(@Param(value="id")Long id);
	List<GoodsCart> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GoodsCart> queryPagesWithNoRelations(Map<String,Object> params);
	
	List<GoodsCart> queryPageListWithNoRelations(Map<String,Object> params);
	
	void saveGoodsCartAndGsps(Map<String,Object> params);

}
