package com.redpigmall.dao;

import com.redpigmall.domain.FTPServer;
import com.redpigmall.domain.UserDistribution;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserDistributionMapper extends SupperMapper {

	void batchDelete(List<UserDistribution> objs);

	List<UserDistribution> selectObjByProperty(Map<String, Object> maps);

	UserDistribution selectByPrimaryKey(Long id);

	UserDistribution selectByPrimaryKeyEagerStore(Long id);

	List<UserDistribution> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<UserDistribution> queryByIds(List<Long> ids);

	List<UserDistribution> queryPageListByParentIsNull(Map<String, Object> params);

	Long save(UserDistribution user);


	void update(UserDistribution user);


	List<UserDistribution> queryPages(Map<String, Object> params);

	void saveFTPServer(FTPServer ftpServer);

	void saveEntity(UserDistribution obj);

	void updateById(UserDistribution obj);

	void deleteById(@Param(value = "id") Long id);

	UserDistribution getUserByStoreId(@Param(value = "id") Long id);
	void batchDeleteByIds(List<Long> ids);

	List<UserDistribution> queryPagesWithNoRelations(Map<String, Object> params);

	List<UserDistribution> queryPageListWithNoRelations(Map<String, Object> params);


	/**查询全部列表*/
	List<UserDistribution> selectAll();
	/**查询一级和二级分销商*/
	List<UserDistribution> queryTwoAndThreeData(Long id);
	/**查询一级分销商*/
	public List<UserDistribution> queryTwoData(Long id);

}
