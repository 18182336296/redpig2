package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.VatInvoice;

public interface VatInvoiceMapper extends SupperMapper {

	void batchDelete(List<VatInvoice> objs);

	List<VatInvoice> selectObjByProperty(Map<String, Object> maps);

	VatInvoice selectByPrimaryKey(Long id);

	List<VatInvoice> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<VatInvoice> queryByIds(List<Long> ids);

	List<VatInvoice> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(VatInvoice obj);

	void updateById(VatInvoice obj);

	void deleteById(@Param(value="id")Long id);
	List<VatInvoice> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<VatInvoice> queryPagesWithNoRelations(Map<String,Object> params);

	List<VatInvoice> queryPageListWithNoRelations(Map<String,Object> params);

}
