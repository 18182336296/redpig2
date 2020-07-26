package com.redpigmall.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.redpigmall.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.dao.UserMapper;
import com.redpigmall.dao.CloudPurchaseLotteryMapper;
import com.redpigmall.dao.CloudPurchaseOrderMapper;
import com.redpigmall.service.RedPigCloudPurchaseCartService;
import com.redpigmall.service.RedPigCloudPurchaseCodeService;
import com.redpigmall.service.RedPigCloudPurchaseLotteryService;
import com.redpigmall.service.RedPigCloudPurchaseOrderService;
import com.redpigmall.service.RedPigCloudPurchaseRecordService;
import com.redpigmall.service.RedPigSysConfigService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigCloudPurchaseOrderService extends BaseService<CloudPurchaseOrder>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<CloudPurchaseOrder> objs) {
		if (objs != null && objs.size() > 0) {
			redPigCloudPurchaseOrderMapper.batchDelete(objs);
		}
	}


	public CloudPurchaseOrder getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<CloudPurchaseOrder> objs = redPigCloudPurchaseOrderMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<CloudPurchaseOrder> selectObjByProperty(Map<String, Object> maps) {
		return redPigCloudPurchaseOrderMapper.selectObjByProperty(maps);
	}


	public List<CloudPurchaseOrder> queryPages(Map<String, Object> params) {
		return redPigCloudPurchaseOrderMapper.queryPages(params);
	}


	public List<CloudPurchaseOrder> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigCloudPurchaseOrderMapper.queryPageListWithNoRelations(param);
	}


	public List<CloudPurchaseOrder> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigCloudPurchaseOrderMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private CloudPurchaseOrderMapper redPigCloudPurchaseOrderMapper;

	@Autowired
	private CloudPurchaseLotteryMapper cloudPurchaseLotteryMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private RedPigSysConfigService configService;

	@Autowired
	private RedPigCloudPurchaseCodeService cloudPurchaseCodeService;

	@Autowired
	private RedPigCloudPurchaseRecordService cloudPurchaseRecordService;

	@Autowired
	private RedPigCloudPurchaseLotteryService cloudPurchaseLotteryService;

	@Autowired
	private RedPigCloudPurchaseCartService cloudPurchaseCartService;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigCloudPurchaseOrderMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(CloudPurchaseOrder obj) {
		redPigCloudPurchaseOrderMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(CloudPurchaseOrder obj) {
		redPigCloudPurchaseOrderMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigCloudPurchaseOrderMapper.deleteById(id);
	}


	public CloudPurchaseOrder selectByPrimaryKey(Long id) {
		return redPigCloudPurchaseOrderMapper.selectByPrimaryKey(id);
	}


	public List<CloudPurchaseOrder> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<CloudPurchaseOrder> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigCloudPurchaseOrderMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}

	@Transactional(readOnly = false)
	public int reduce_inventory(CloudPurchaseOrder order, HttpServletRequest request) {
		Map<String, Object> map = JSON.parseObject(order.getOrderInfo());
		for (Object key : map.keySet()) {
			Long id = CommUtil.null2Long(key);
			int count = CommUtil.null2Int(map.get(key));

			CloudPurchaseLottery lottery = this.cloudPurchaseLotteryMapper.selectByPrimaryKey(id);

			User user = this.userMapper.selectByPrimaryKey(order.getUser_id());

			if (count > lottery.getPurchased_left_times()) {
				int more = count - lottery.getPurchased_left_times();
				user.setAvailableBalance(user.getAvailableBalance().add(new BigDecimal(more)));
				this.userMapper.update(user);
				count = lottery.getPurchased_left_times();
			}
			if (count > 0) {
				CloudPurchaseRecord record = new CloudPurchaseRecord();
				Date now = new Date();
				record.setAddTime(now);
				record.setUser_id(order.getUser_id());

				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				record.setPayTime(sdf1.format(now));
				sdf1 = new SimpleDateFormat("HHmmssSSS");
				record.setPayTimeStamp(sdf1.format(now));
				record.setCloudPurchaseLottery(lottery);
				record.setPurchased_times(count);
				record.setUser_name(user.getUserName());

				String url = CommUtil.getURL(request);
				String goods_main_photo = url + "/" + this.configService.getSysConfig().getMemberIcon().getPath() + "/"
						+ this.configService.getSysConfig().getMemberIcon().getName();

				if (user.getPhoto() != null) {
					goods_main_photo = url + "/" + user.getPhoto().getPath() + "/" + user.getPhoto().getName();
				}
				record.setUser_photo(goods_main_photo);

				Map<String, Object> params = Maps.newHashMap();
				params.put("status", Integer.valueOf(0));
				params.put("lottery", lottery.getId());

				List<CloudPurchaseCode> list = this.cloudPurchaseCodeService.queryPageList(params, 0, count);

				List<Long> code_list = Lists.newArrayList();
				for (CloudPurchaseCode code : list) {
					code.setUser_id(order.getUser_id());
					code.setStatus(1);
					this.cloudPurchaseCodeService.updateById(code);
					code_list.add(code.getCode());
				}

				record.setPurchased_codes(JSON.toJSONString(code_list));
				this.cloudPurchaseRecordService.saveEntity(record);
				boolean ret = true;
				if (ret) {
					lottery.setPurchased_times(lottery.getPurchased_times() + count);
					lottery.setPurchased_left_times(lottery.getPurchased_left_times() - count);
					this.cloudPurchaseLotteryMapper.updateById(lottery);

					if (lottery.getPurchased_left_times() == 0) {
						this.cloudPurchaseLotteryService.salesEnd(lottery.getId());
					}
					this.cloudPurchaseCartService.delete_aftet_pay(user.getId(), lottery.getId());
				}
			}
		}
		return 0;
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = false)
	public CloudPurchaseOrder submitOrder(Long user_id, Map orderInfo) {
		int price = 0;
		for (Object key : orderInfo.keySet()) {
			CloudPurchaseLottery cloudPurchaseLottery = this.cloudPurchaseLotteryMapper
					.selectByPrimaryKey(CommUtil.null2Long(key));
			int count = CommUtil.null2Int(orderInfo.get(key));
			if (cloudPurchaseLottery.getStatus() != 5) {
				return null;
			}
			if ((cloudPurchaseLottery.getCloudPurchaseGoods().getLeast_rmb() == 10) && (count % 10 != 0)) {
				return null;
			}
			if (cloudPurchaseLottery.getPurchased_left_times() < count) {
				return null;
			}
			price += count;
		}
		String order_suffix = CommUtil.formatTime("yyyyMMddHHmmss", new Date());

		CloudPurchaseOrder order = new CloudPurchaseOrder();
		order.setAddTime(new Date());
		order.setOdrdersn("cp" + user_id + order_suffix);
		order.setUser_id(user_id);
		order.setOrderInfo(JSON.toJSONString(orderInfo));
		order.setPrice(price);
		order.setStatus(0);
		saveEntity(order);

		return order;
	}

}
