package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.FTPServer;
import com.redpigmall.dao.FTPServerMapper;
import com.redpigmall.service.RedPigFTPServerService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigFTPServerService extends BaseService<FTPServer>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<FTPServer> objs) {
		if (objs != null && objs.size() > 0) {
			redPigFTPServerMapper.batchDelete(objs);
		}
	}


	public FTPServer getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<FTPServer> objs = redPigFTPServerMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<FTPServer> selectObjByProperty(Map<String, Object> maps) {
		return redPigFTPServerMapper.selectObjByProperty(maps);
	}


	public List<FTPServer> queryPages(Map<String, Object> params) {
		return redPigFTPServerMapper.queryPages(params);
	}


	public List<FTPServer> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigFTPServerMapper.queryPageListWithNoRelations(param);
	}


	public List<FTPServer> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigFTPServerMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}

	@Autowired
	private FTPServerMapper redPigFTPServerMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigFTPServerMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(FTPServer obj) {
		redPigFTPServerMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(FTPServer obj) {
		redPigFTPServerMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigFTPServerMapper.deleteById(id);
	}


	public FTPServer selectByPrimaryKey(Long id) {
		return redPigFTPServerMapper.selectByPrimaryKey(id);
	}


	public List<FTPServer> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<FTPServer> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigFTPServerMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<FTPServer> queryList(Map<String, Object> params, Integer begin, Integer max) {

		return redPigFTPServerMapper.queryList(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.list2(params);
	}


	@Transactional(readOnly = false)
	public void save(FTPServer ftpServer) {
		redPigFTPServerMapper.insert(ftpServer);
	}


	@Transactional(readOnly = false)
	public void update(FTPServer ftpServer) {
		redPigFTPServerMapper.update(ftpServer);
	}


	public List<FTPServer> queryPageListNotId(Map<String, Object> map) {
		return redPigFTPServerMapper.queryPageListNotId(map);
	}


	@Transactional(readOnly = false)
	public void delete(Long id) {
		redPigFTPServerMapper.delete(id);
	}


	public List<FTPServer> queryFtpServerUserTrans(Map<String, Object> params) {
		return redPigFTPServerMapper.queryFtpServerUserTrans(params);
	}
}
