package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.FTPServer;
import com.redpigmall.domain.User;
import com.redpigmall.domain.UserRole;

public interface UserMapper extends SupperMapper {

	void batchDelete(List<User> objs);

	List<User> selectObjByProperty(Map<String, Object> maps);

	User selectByPrimaryKey(Long id);
	
	User selectByPrimaryKeyEagerStore(Long id);
	
	List<User> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<User> queryByIds(List<Long> ids);

	List<User> queryPageListByParentIsNull(Map<String, Object> params);

	Long save(User user);
	
	void saveUserRole(List<UserRole> urs);

	void update(User user);

	void deleteUserRoleById(List<UserRole> urs);
	
	List<User> queryPages(Map<String, Object> params);

	void saveFTPServer(FTPServer ftpServer);
	
	void saveEntity(User obj);

	void updateById(User obj);

	void deleteById(@Param(value="id")Long id);
	
	User getUserByStoreId(@Param(value="id")Long id);
	void batchDeleteByIds(List<Long> ids);

	List<User> queryPagesWithNoRelations(Map<String,Object> params);

	List<User> queryPageListWithNoRelations(Map<String,Object> params);

	List<User> verityUserNamePasswordStatus(Map<String, Object> map);
	
	List<User> verityUserNamePassword(Map<String, Object> map);

	List<User> verityUserNamePasswordEmail(Map<String, Object> params);
	
	void deleteUserMenu(Long userId);
	
	void saveUserMenu(Map<String,Object> maps);

	List<User> verityUserNamePasswordAndRole(Map<String, Object> map);
	
}
