package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.dao.GoodsBrandMapper;
import com.redpigmall.service.RedPigGoodsBrandService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGoodsBrandService extends BaseService<GoodsBrand> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GoodsBrand> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGoodsBrandMapper.batchDelete(objs);
		}
	}


	public GoodsBrand getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GoodsBrand> objs = redPigGoodsBrandMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GoodsBrand> selectObjByProperty(Map<String, Object> maps) {
		return redPigGoodsBrandMapper.selectObjByProperty(maps);
	}


	public List<GoodsBrand> queryPages(Map<String, Object> params) {
		return redPigGoodsBrandMapper.queryPages(params);
	}


	public List<GoodsBrand> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGoodsBrandMapper.queryPageListWithNoRelations(param);
	}


	public List<GoodsBrand> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGoodsBrandMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}

	@Autowired
	private GoodsBrandMapper redPigGoodsBrandMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGoodsBrandMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GoodsBrand obj) {
		redPigGoodsBrandMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GoodsBrand obj) {
		redPigGoodsBrandMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGoodsBrandMapper.deleteById(id);
	}


	public GoodsBrand selectByPrimaryKey(Long id) {
		return redPigGoodsBrandMapper.selectByPrimaryKey(id);
	}


	public List<GoodsBrand> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GoodsBrand> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGoodsBrandMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public IPageList list(Map<String, Object> maps) {
		return super.listPages(maps);
	}


	@Transactional(readOnly = false)
	public void update(GoodsBrand goodsBrand) {
		redPigGoodsBrandMapper.update(goodsBrand);
	}


	@Transactional(readOnly = false)
	public void save(GoodsBrand goodsBrand) {
		redPigGoodsBrandMapper.save(goodsBrand);
	}

	@Transactional(readOnly = false)

	public void deleteGoodsBrandAndGoodsType(List<Map<String, Long>> gbtIds) {
		if (RedPigCommonUtil.isNotNull(gbtIds)) {
			redPigGoodsBrandMapper.deleteGoodsBrandAndGoodsType(gbtIds);
		}
	}


	@Transactional(readOnly = false)
	public int delete(Long id) {
		return redPigGoodsBrandMapper.delete(id);
	}

	@Transactional(readOnly = false)

	public void saveGoodsBrandAndGoodsType(List<Map<String, Long>> gbtIds) {
		redPigGoodsBrandMapper.saveGoodsBrandAndGoodsType(gbtIds);
	}


	public List<GoodsBrand> queryPageListWithNoRelations(Map<String, Object> params, int begin, int max) {

		super.queryPageListWithNoRelations(params, begin, max);

		return null;
	}


	public List<Map<String, Object>> queryListWithNoRelations(Map<String, Object> params) {
		params.put("deleteStatus", "0");
		// TODO Auto-generated method stub
		return redPigGoodsBrandMapper.queryListWithNoRelations(params);
	}

}
