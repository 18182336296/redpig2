package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.FTPServer;

public interface FTPServerMapper extends SupperMapper {

	void batchDelete(List<FTPServer> objs);

	List<FTPServer> selectObjByProperty(Map<String, Object> maps);

	FTPServer selectByPrimaryKey(Long id);

	List<FTPServer> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<FTPServer> queryByIds(List<Long> ids);

	List<FTPServer> queryPageListByParentIsNull(Map<String, Object> params);

	List<FTPServer> queryList(Map<String, Object> params);

	void save(FTPServer ftpServer);
	
	void insert(FTPServer ftpServer);

	void update(FTPServer ftpServer);

	List<FTPServer> queryPageListNotId(Map<String,Object> map);

	void delete(Long id);

	List<FTPServer> queryFtpServerUserTrans(Map<String, Object> params);

	void saveEntity(FTPServer obj);

	void updateById(FTPServer obj);

	void deleteById(@Param(value="id")Long id);
	List<FTPServer> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<FTPServer> queryPagesWithNoRelations(Map<String,Object> params);

	List<FTPServer> queryPageListWithNoRelations(Map<String,Object> params);

}
