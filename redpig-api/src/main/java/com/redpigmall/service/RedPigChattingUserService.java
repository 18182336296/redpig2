package com.redpigmall.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.dao.UserMapper;
import com.redpigmall.domain.ChattingLog;
import com.redpigmall.domain.ChattingUser;
import com.redpigmall.dao.ChattingLogMapper;
import com.redpigmall.dao.ChattingUserMapper;
import com.redpigmall.dao.StoreMapper;
import com.redpigmall.service.RedPigChattingUserService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigChattingUserService extends BaseService<ChattingUser>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<ChattingUser> objs) {
		if (objs != null && objs.size() > 0) {
			redPigChattingUserMapper.batchDelete(objs);
		}
	}


	public ChattingUser getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<ChattingUser> objs = redPigChattingUserMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<ChattingUser> selectObjByProperty(Map<String, Object> maps) {
		return redPigChattingUserMapper.selectObjByProperty(maps);
	}


	public List<ChattingUser> queryPages(Map<String, Object> params) {
		return redPigChattingUserMapper.queryPages(params);
	}


	public List<ChattingUser> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigChattingUserMapper.queryPageListWithNoRelations(param);
	}


	public List<ChattingUser> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigChattingUserMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private ChattingUserMapper redPigChattingUserMapper;
	@Autowired
	private ChattingLogMapper redPigChattingLogMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigChattingUserMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(ChattingUser obj) {
		redPigChattingUserMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(ChattingUser obj) {
		redPigChattingUserMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigChattingUserMapper.deleteById(id);
	}


	public ChattingUser selectByPrimaryKey(Long id) {
		return redPigChattingUserMapper.selectByPrimaryKey(id);
	}


	public List<ChattingUser> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<ChattingUser> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigChattingUserMapper.selectCount(params);
		if (c == null) {
			return 0;
		}
		return c;
	}

	@Autowired
	private StoreMapper storeMapper;

	@Autowired
	private UserMapper userMapper;

	@SuppressWarnings("unused")
	private int getServiceCount(Set<Long> ids) {
		int count = 0;
		if (ids.size() > 0) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("ids", ids);
			return this.redPigChattingUserMapper.selectCount(params);
		}
		return count;
	}

	@Transactional(readOnly = false)
	public Long getService_id(Set<Long> ids, int service_type) {
		Long service_id = null;
		if (ids.size() > 0) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("chatting_user_ids", ids);
			params.put("chatting_status", Integer.valueOf(1));
			params.put("service_type", Integer.valueOf(service_type));
			params.put("orderBy", "chatting_count");
			params.put("orderType", "asc");

			List<ChattingUser> cusers = this.redPigChattingUserMapper.queryPageList(params);

			if (cusers.size() > 0) {
				service_id = ((ChattingUser) cusers.get(0)).getId();
				ChattingUser obj = (ChattingUser) cusers.get(0);
				obj.setChatting_count(obj.getChatting_count() + 1);
				this.redPigChattingUserMapper.updateById(obj);

			} else {
				params.clear();
				params.put("chatting_user_ids", ids);
				params.put("chatting_status", Integer.valueOf(1));
				params.put("service_type", Integer.valueOf(service_type));
				params.put("orderBy", "chatting_count");
				params.put("orderType", "asc");
				List<ChattingUser> cusers2 = this.redPigChattingUserMapper.queryPageList(params);

				if (cusers2.size() > 0) {
					service_id = ((ChattingUser) cusers2.get(0)).getId();
					ChattingUser obj = (ChattingUser) cusers2.get(0);
					obj.setChatting_count(obj.getChatting_count() + 1);
					this.redPigChattingUserMapper.updateById(obj);
				} else {
					params.clear();
					params.put("ids", ids);
					params.put("chatting_status", Integer.valueOf(0));
					params.put("service_type", Integer.valueOf(service_type));
					params.put("orderBy", "chatting_count");
					params.put("orderType", "asc");
					List<ChattingUser> cusers3 = this.redPigChattingUserMapper.queryPageList(params);

					if (cusers3.size() > 0) {
						service_id = ((ChattingUser) cusers3.get(0)).getId();
						ChattingUser obj = (ChattingUser) cusers3.get(0);
						obj.setChatting_count(obj.getChatting_count() + 1);
						this.redPigChattingUserMapper.updateById(obj);
					} else {
						params.clear();
						params.put("ids", ids);
						params.put("chatting_status", Integer.valueOf(0));
						List<ChattingUser> cusers4 = this.redPigChattingUserMapper.queryPageList(params);

						if (cusers4.size() > 0) {
							service_id = ((ChattingUser) cusers4.get(0)).getId();
							ChattingUser obj = (ChattingUser) cusers4.get(0);
							obj.setChatting_count(obj.getChatting_count() + 1);
							this.redPigChattingUserMapper.updateById(obj);
						}
					}
				}
			}
		}
		return service_id;
	}

	@SuppressWarnings("unused")
	@Transactional(readOnly = false)
	public Long distribute_service(String store_id, int service_type) {
		Long service_id = null;
		String service_name = "";
		String user_name = "";
		ChattingUser cu;
		if ((store_id != null) && (!store_id.equals(""))) {
			Store store = this.storeMapper.selectByPrimaryKey(CommUtil.null2Long(store_id));
			User seller = store.getUser();
			List<User> seller_childs = seller.getChilds();
			Set<Long> ids = Sets.newHashSet();

			for (User temp : seller_childs) {
				ids.add(temp.getId());
			}
			ids.add(seller.getId());
			// if (getServiceCount(ids) > 0) {
			// service_id = getService_id(ids, service_type);
			// } else {
			cu = new ChattingUser();
			cu.setAddTime(new Date());
			cu.setChatting_name(seller.getUserName());
			cu.setChatting_user_id(seller.getId());
			cu.setChatting_user_name(seller.getUserName());
			cu.setChatting_user_form("seller");
			cu.setChatting_status(1);
			this.redPigChattingUserMapper.saveEntity(cu);
			service_id = seller.getId();
			service_name = cu.getChatting_name();
			user_name = cu.getChatting_user_name();
			// }
		} else {
			Map<String, Object> params = Maps.newHashMap();
			params.put("userRole", "ADMIN");
			List<User> users = this.userMapper.queryPageList(params);

			Set<Long> ids = Sets.newHashSet();

			for (User temp : users) {
				ids.add(temp.getId());
			}

			// if (getServiceCount(ids) > 0) {
			// service_id = getService_id(ids, service_type);
			// } else {
			params.clear();
			params.put("userName", "admin");
			params.put("userRole", "ADMIN");
			List<User> users2 = this.userMapper.queryPageList(params);

			cu = new ChattingUser();
			cu.setAddTime(new Date());
			cu.setChatting_name(users2.get(0).getUserName());
			cu.setChatting_user_name(users2.get(0).getUserName());
			cu.setChatting_user_id(users2.get(0).getId());
			cu.setChatting_user_form("plat");
			cu.setChatting_status(1);
			this.redPigChattingUserMapper.saveEntity(cu);
			service_id = cu.getChatting_user_id();
			service_name = cu.getChatting_name();
			user_name = cu.getChatting_user_name();
			// }
		}
		System.out.println("=====客服分配成功=====客服id：" + service_id);
		return service_id;
	}

	@SuppressWarnings("rawtypes")
	public List<Map> queryUsers() {
		List<Map> maps = Lists.newArrayList();
		User user = SecurityUserHolder.getCurrentUser();
		Map<String, Object> params = Maps.newHashMap();
		params.put("chatting_user_id", user.getId());
		params.put("currentPage", 0);
		params.put("pageSize", 1);

		List<ChattingUser> cusers = this.redPigChattingUserMapper.queryPageList(params);
		if (cusers.size() > 0) {
			params.clear();
			params.put("service_read", 0);
			params.put("service_id", cusers.get(0).getChatting_user_id());

			List<ChattingLog> logs = this.redPigChattingLogMapper.queryPageList(params);

			for (int i = 0; i < logs.size(); i++) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", logs.get(i).getUser_id());
				map.put("userName", logs.get(i).getUser_name());
				map.put("service_read", Integer.valueOf(logs.get(i).getService_read()));
				maps.add(map);
			}
		}
		return maps;
	}


	public List<Long> queryChattingUserIds() {
		List<ChattingUser> cus = this.redPigChattingUserMapper.queryPageList(RedPigMaps.newMap());
		List<Long> ids = Lists.newArrayList();
		for (ChattingUser cu : cus) {
			ids.add(cu.getId());
		}
		return ids;
	}

}
