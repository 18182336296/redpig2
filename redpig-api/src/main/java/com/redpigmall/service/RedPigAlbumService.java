package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.UserMapper;
import com.redpigmall.domain.Album;
import com.redpigmall.dao.AlbumMapper;
import com.redpigmall.service.RedPigAlbumService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigAlbumService extends BaseService<Album>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Album> objs) {
		if (objs != null && objs.size() > 0) {
			redPigAlbumMapper.batchDelete(objs);
		}
	}


	public Album getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Album> objs = redPigAlbumMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Album> selectObjByProperty(Map<String, Object> maps) {
		return redPigAlbumMapper.selectObjByProperty(maps);
	}

	@Autowired
	private AlbumMapper redPigAlbumMapper;

	@Autowired
	private UserMapper redPigUserMapper;


	public List<Album> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigAlbumMapper.queryPageListWithNoRelations(param);
	}


	public List<Album> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigAlbumMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigAlbumMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Album obj) {
		redPigAlbumMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Album obj) {
		redPigAlbumMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigAlbumMapper.deleteById(id);
	}


	public Album selectByPrimaryKey(Long id) {
		return redPigAlbumMapper.selectByPrimaryKey(id);
	}


	public List<Album> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Album> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigAlbumMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<Album> queryPages(Map<String, Object> params) {
		return redPigAlbumMapper.queryPages(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	public Album getDefaultAlbum(Long id) {
		User user = this.redPigUserMapper.selectByPrimaryKey(id);
		if (user.getParent() == null) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("user_id", id);
			params.put("album_default", Boolean.valueOf(true));
			List<Album> list = this.redPigAlbumMapper.queryPageList(params);

			if (list.size() > 0) {
				return (Album) list.get(0);
			}
			return null;
		}

		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", user.getParent().getId());
		params.put("album_default", Boolean.valueOf(true));
		List<Album> list = this.redPigAlbumMapper.queryPageList(params);

		if (list.size() > 0) {
			return (Album) list.get(0);
		}
		return null;
	}
}
