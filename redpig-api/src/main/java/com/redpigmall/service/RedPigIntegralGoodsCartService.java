package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.IntegralGoodsCart;
import com.redpigmall.dao.IntegralGoodsCartMapper;
import com.redpigmall.service.RedPigIntegralGoodsCartService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigIntegralGoodsCartService extends BaseService<IntegralGoodsCart>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<IntegralGoodsCart> objs) {
		if (objs != null && objs.size() > 0) {
			redPigIntegralGoodsCartMapper.batchDelete(objs);
		}
	}


	public IntegralGoodsCart getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<IntegralGoodsCart> objs = redPigIntegralGoodsCartMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<IntegralGoodsCart> selectObjByProperty(Map<String, Object> maps) {
		return redPigIntegralGoodsCartMapper.selectObjByProperty(maps);
	}


	public List<IntegralGoodsCart> queryPages(Map<String, Object> params) {
		return redPigIntegralGoodsCartMapper.queryPages(params);
	}


	public List<IntegralGoodsCart> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigIntegralGoodsCartMapper.queryPageListWithNoRelations(param);
	}


	public List<IntegralGoodsCart> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigIntegralGoodsCartMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private IntegralGoodsCartMapper redPigIntegralGoodsCartMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigIntegralGoodsCartMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(IntegralGoodsCart obj) {
		redPigIntegralGoodsCartMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(IntegralGoodsCart obj) {
		redPigIntegralGoodsCartMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigIntegralGoodsCartMapper.deleteById(id);
	}


	public IntegralGoodsCart selectByPrimaryKey(Long id) {
		return redPigIntegralGoodsCartMapper.selectByPrimaryKey(id);
	}


	public List<IntegralGoodsCart> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<IntegralGoodsCart> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigIntegralGoodsCartMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
