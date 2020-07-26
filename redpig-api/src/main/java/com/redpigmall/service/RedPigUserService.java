package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import com.redpigmall.dao.UserMapper;
import com.redpigmall.domain.Menu;
import com.redpigmall.domain.Role;
import com.redpigmall.domain.User;
import com.redpigmall.domain.UserRole;
import com.redpigmall.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;


@Service
@Transactional(readOnly = true)
public class RedPigUserService extends BaseService<User> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<User> objs) {
		if (objs != null && objs.size() > 0) {
			redPigUserMapper.batchDelete(objs);
		}
	}


	public User getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);

		List<User> objs = redPigUserMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<User> selectObjByProperty(Map<String, Object> maps) {
		return redPigUserMapper.selectObjByProperty(maps);
	}


	public List<User> queryPages(Map<String, Object> params) {
		return redPigUserMapper.queryPages(params);
	}


	public List<User> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigUserMapper.queryPageListWithNoRelations(param);
	}


	public List<User> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigUserMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private UserMapper redPigUserMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigUserMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(User obj) {
		redPigUserMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(User obj) {
		redPigUserMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigUserMapper.deleteById(id);
	}


	public User selectByPrimaryKey(Long id) {
		return redPigUserMapper.selectByPrimaryKey(id);
	}


	public User selectByPrimaryKeyEagerStore(Long id) {
		return redPigUserMapper.selectByPrimaryKeyEagerStore(id);
	}


	public List<User> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<User> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigUserMapper.selectCount(params);
		if (c == null) {
			return 0;
		}
		return c;
	}


	public List<User> queryPageListByParentIsNull(Map<String, Object> params, int begin, int max) {
		return redPigUserMapper.queryPageListByParentIsNull(params);
	}


	@Transactional(readOnly = false)
	public Long save(User user) {
		return redPigUserMapper.save(user);
	}

	@Transactional(readOnly = false)

	public void saveUserRole(Long userId, List<Role> roles) {
		if(userId== null || (roles==null || roles.size()==0)) {
			return ;
		}
		List<UserRole> urs = Lists.newArrayList();
		for (Role role : roles) {
			UserRole ur = new UserRole();
			ur.setUser_id(userId);
			ur.setRole_id(role.getId());
			urs.add(ur);
		}

		redPigUserMapper.saveUserRole(urs);
	}

	@Transactional(readOnly = false)

	public void deleteUserRole(Long userId, List<Role> rs) {

		List<UserRole> urs = Lists.newArrayList();

		for (Role role : rs) {
			UserRole ur = new UserRole();
			ur.setUser_id(userId);
			ur.setRole_id(role.getId());
			urs.add(ur);
		}
		if (userId != null && urs.size() > 0) {
			redPigUserMapper.deleteUserRoleById(urs);
		}
	}


	public User queryByProperty(Map<String, Object> params) {
		List<User> users = this.queryPageList(params);

		if (users != null && users.size() > 0) {
			return users.get(0);
		}
		return null;
	}


	public List<User> verityUserNamePasswordStatus(Map<String, Object> map) {
		return redPigUserMapper.verityUserNamePasswordStatus(map);
	}


	public List<User> verityUserNamePassword(Map<String, Object> map) {
		return redPigUserMapper.verityUserNamePassword(map);
	}


	public List<User> verityUserNamePasswordAndRole(Map<String, Object> map) {
		return redPigUserMapper.verityUserNamePasswordAndRole(map);
	}


	public List<User> verityUserNamePasswordEmail(Map<String, Object> params) {
		return redPigUserMapper.verityUserNamePasswordEmail(params);
	}

	@Transactional(readOnly = false)

	public void deleteUserMenu(Long userId) {
		redPigUserMapper.deleteUserMenu(userId);
	}

	@Transactional(readOnly = false)

	public void saveUserMenu(Long userId, List<Menu> menus) {

		Map<String,Object> maps = Maps.newHashMap();
		maps.put("userId", userId);
		maps.put("menus", menus);

		if(userId!=null && menus!=null && menus.size()>0){
			redPigUserMapper.saveUserMenu(maps);
		}
	}

}
