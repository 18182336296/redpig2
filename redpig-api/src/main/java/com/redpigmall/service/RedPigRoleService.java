package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.Res;
import com.redpigmall.dao.RoleMapper;
import com.redpigmall.service.RedPigRoleService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigRoleService extends BaseService<Role>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Role> objs) {
		if (objs != null && objs.size() > 0) {
			redPigRoleMapper.batchDelete(objs);
		}
	}


	public Role getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Role> objs = redPigRoleMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Role> selectObjByProperty(Map<String, Object> maps) {
		return redPigRoleMapper.selectObjByProperty(maps);
	}


	public List<Role> queryPages(Map<String, Object> params) {
		return redPigRoleMapper.queryPages(params);
	}


	public List<Role> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigRoleMapper.queryPageListWithNoRelations(param);
	}


	public List<Role> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigRoleMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private RoleMapper redPigRoleMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigRoleMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Role obj) {
		redPigRoleMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Role obj) {
		redPigRoleMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigRoleMapper.deleteById(id);
	}


	public Role selectByPrimaryKey(Long id) {
		return redPigRoleMapper.selectByPrimaryKey(id);
	}


	public List<Role> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Role> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigRoleMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<Role> queryPageListByDisplayAndType(Map<String, Object> params) {

		return redPigRoleMapper.queryPageListByDisplayAndType(params);
	}

	@Transactional(readOnly = false)

	public void deleteRoleRes(Long id, List<Res> reses) {
		Map<String, Object> params = Maps.newHashMap();
		if (id != null && reses != null && reses.size() > 0) {
			for (Res res : reses) {
				params.put("role_id", res.getId());
			}
			redPigRoleMapper.deleteRoleRes(params);
		}
	}

	@Transactional(readOnly = false)

	public void saveRoleRes(Long id, List<Res> reses) {
		Map<String, Object> params = Maps.newHashMap();
		if (id != null && reses != null && reses.size() > 0) {
			for (Res res : reses) {
				params.put("role_id", res.getId());
			}
			redPigRoleMapper.saveRoleRes(params);
		}
	}
}
