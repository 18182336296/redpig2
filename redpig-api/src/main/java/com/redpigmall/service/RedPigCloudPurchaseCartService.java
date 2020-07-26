package com.redpigmall.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.CloudPurchaseCart;
import com.redpigmall.domain.CloudPurchaseGoods;
import com.redpigmall.domain.CloudPurchaseLottery;
import com.redpigmall.dao.CloudPurchaseCartMapper;
import com.redpigmall.dao.CloudPurchaseGoodsMapper;
import com.redpigmall.dao.CloudPurchaseLotteryMapper;
import com.redpigmall.service.RedPigCloudPurchaseCartService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigCloudPurchaseCartService extends BaseService<CloudPurchaseCart> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<CloudPurchaseCart> objs) {
		if (objs != null && objs.size() > 0) {
			redPigCloudPurchaseCartMapper.batchDelete(objs);
		}
	}


	public CloudPurchaseCart getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<CloudPurchaseCart> objs = redPigCloudPurchaseCartMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<CloudPurchaseCart> selectObjByProperty(Map<String, Object> maps) {
		return redPigCloudPurchaseCartMapper.selectObjByProperty(maps);
	}


	public List<CloudPurchaseCart> queryPages(Map<String, Object> params) {
		return redPigCloudPurchaseCartMapper.queryPages(params);
	}


	public List<CloudPurchaseCart> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigCloudPurchaseCartMapper.queryPageListWithNoRelations(param);
	}


	public List<CloudPurchaseCart> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigCloudPurchaseCartMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private CloudPurchaseCartMapper redPigCloudPurchaseCartMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigCloudPurchaseCartMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(CloudPurchaseCart obj) {
		redPigCloudPurchaseCartMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(CloudPurchaseCart obj) {
		redPigCloudPurchaseCartMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigCloudPurchaseCartMapper.deleteById(id);
	}


	public CloudPurchaseCart selectByPrimaryKey(Long id) {
		return redPigCloudPurchaseCartMapper.selectByPrimaryKey(id);
	}


	public List<CloudPurchaseCart> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<CloudPurchaseCart> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigCloudPurchaseCartMapper.selectCount(params);
		if (c == null) {
			return 0;
		}
		return c;
	}

	@Transactional(readOnly = false)
	public void delete_aftet_pay(Long user_id, Long lottery_id) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", user_id);
		params.put("cloudPurchaseLottery_id", lottery_id);
		List<CloudPurchaseCart> list = redPigCloudPurchaseCartMapper.queryPageList(params);

		batchDelObjs(list);
	}

	@Autowired
	private CloudPurchaseLotteryMapper cloudPurchaseLotteryMapper;

	@Transactional(readOnly = false)
	public int addCloudsCart(Long user_id, Long lottery_id, int count) {
		int flag = 0;

		CloudPurchaseLottery cloudPurchaseLottery = this.cloudPurchaseLotteryMapper.selectByPrimaryKey(lottery_id);

		if ((count <= 0) || (cloudPurchaseLottery.getPurchased_left_times() == 0)) {
			return flag;
		}

		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", user_id);
		params.put("cloudPurchaseLottery_id", lottery_id);

		List<CloudPurchaseCart> cart_list = this.redPigCloudPurchaseCartMapper.queryPageList(params);

		if (cart_list.size() > 0) {
			CloudPurchaseCart cloudPurchaseCart = (CloudPurchaseCart) cart_list.get(0);

			flag = updateCloudGoodsNum(cloudPurchaseCart, cloudPurchaseCart.getPurchased_times() + count);

		} else {
			CloudPurchaseGoods goods = cloudPurchaseLottery.getCloudPurchaseGoods();
			if ((goods.getLeast_rmb() == 10) && (count % 10 != 0)) {
				count = 10 * (count / 10 + 1);
			}
			CloudPurchaseCart cloudPurchaseCart = new CloudPurchaseCart();
			cloudPurchaseCart.setAddTime(new Date());
			cloudPurchaseCart.setCloudPurchaseLottery(cloudPurchaseLottery);
			cloudPurchaseCart.setUser_id(user_id);

			if (check_goods_inventory(cloudPurchaseLottery, count)) {
				cloudPurchaseCart.setPurchased_times(count);
			} else {
				cloudPurchaseCart.setPurchased_times(cloudPurchaseLottery.getPurchased_left_times());
			}
			saveEntity(cloudPurchaseCart);
			flag = 1;
		}
		return flag;
	}

	public boolean check_goods_inventory(CloudPurchaseLottery lottery, int count) {
		return lottery.getPurchased_left_times() >= count;
	}

	public int updateCloudGoodsNum(Long cart_id, int count) {
		CloudPurchaseCart cloudPurchaseCart = selectByPrimaryKey(cart_id);
		return updateCloudGoodsNum(cloudPurchaseCart, count);
	}

	@Autowired
	private CloudPurchaseGoodsMapper cloudPurchaseGoodsMapper;

	@Transactional(readOnly = false)
	public int updateCloudGoodsNum(CloudPurchaseCart cloudPurchaseCart, int count) {
		int flag = 0;
		CloudPurchaseLottery cloudPurchaseLottery = cloudPurchaseCart.getCloudPurchaseLottery();
		if ((count <= 0) || (cloudPurchaseLottery.getPurchased_left_times() < count)) {
			return flag;
		}
		CloudPurchaseGoods goods = (CloudPurchaseGoods) this.cloudPurchaseGoodsMapper
				.selectByPrimaryKey(cloudPurchaseLottery.getGoods_id());

		if ((goods.getLeast_rmb() == 10) && (count % 10 != 0)) {
			count = 10 * (count / 10 + 1);
		}
		cloudPurchaseCart.setPurchased_times(count);

		updateById(cloudPurchaseCart);
		flag = 1;
		return flag;
	}

	public List<CloudPurchaseCart> queryCloudsCart(Long user_id) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", user_id);
		params.put("cloudPurchaseLottery_status_no", 5);

		List<CloudPurchaseCart> carts = this.redPigCloudPurchaseCartMapper.queryPageList(params);

		if (carts.size() > 0) {
			for (CloudPurchaseCart cart : carts) {
				this.redPigCloudPurchaseCartMapper.deleteById(cart.getId());
			}
		}
		params.clear();
		params.put("user_id", user_id);
		List<CloudPurchaseCart> cart_list = this.redPigCloudPurchaseCartMapper.queryPageList(params);

		return cart_list;
	}
}
