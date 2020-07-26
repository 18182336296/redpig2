package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.BuyGift;

public interface BuyGiftMapper extends SupperMapper {

	void batchDelete(List<BuyGift> objs);

	List<BuyGift> selectObjByProperty(Map<String, Object> maps);

	BuyGift selectByPrimaryKey(Long id);

	List<BuyGift> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<BuyGift> queryByIds(List<Long> ids);

	List<BuyGift> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(BuyGift obj);

	void updateById(BuyGift obj);

	void deleteById(@Param(value="id")Long id);
	List<BuyGift> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<BuyGift> queryPagesWithNoRelations(Map<String,Object> params);

	List<BuyGift> queryPageListWithNoRelations(Map<String,Object> params);

}
