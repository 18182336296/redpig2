package com.redpigmall.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.CloudPurchaseCode;
import com.redpigmall.domain.CloudPurchaseEveryColor;
import com.redpigmall.domain.CloudPurchaseGoods;
import com.redpigmall.domain.CloudPurchaseLottery;
import com.redpigmall.domain.CloudPurchaseRecord;
import com.redpigmall.dao.CloudPurchaseEveryColorMapper;
import com.redpigmall.dao.CloudPurchaseGoodsMapper;
import com.redpigmall.dao.CloudPurchaseLotteryMapper;
import com.redpigmall.service.RedPigCloudPurchaseCodeService;
import com.redpigmall.service.RedPigCloudPurchaseGoodsService;
import com.redpigmall.service.RedPigCloudPurchaseLotteryService;
import com.redpigmall.service.RedPigCloudPurchaseRecordService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigCloudPurchaseLotteryService extends BaseService<CloudPurchaseLottery>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<CloudPurchaseLottery> objs) {
		if (objs != null && objs.size() > 0) {
			redPigCloudPurchaseLotteryMapper.batchDelete(objs);
		}
	}


	public CloudPurchaseLottery getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<CloudPurchaseLottery> objs = redPigCloudPurchaseLotteryMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<CloudPurchaseLottery> selectObjByProperty(Map<String, Object> maps) {
		return redPigCloudPurchaseLotteryMapper.selectObjByProperty(maps);
	}


	public List<CloudPurchaseLottery> queryPages(Map<String, Object> params) {
		return redPigCloudPurchaseLotteryMapper.queryPages(params);
	}


	public List<CloudPurchaseLottery> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigCloudPurchaseLotteryMapper.queryPageListWithNoRelations(param);
	}


	public List<CloudPurchaseLottery> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigCloudPurchaseLotteryMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}

	@Autowired
	private CloudPurchaseLotteryMapper redPigCloudPurchaseLotteryMapper;


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigCloudPurchaseLotteryMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public boolean saveEntity(CloudPurchaseLottery obj) {
		redPigCloudPurchaseLotteryMapper.saveEntity(obj);
		return obj != null && obj.getId() != null;
	}


	@Transactional(readOnly = false)
	public void updateById(CloudPurchaseLottery obj) {
		redPigCloudPurchaseLotteryMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigCloudPurchaseLotteryMapper.deleteById(id);
	}


	public CloudPurchaseLottery selectByPrimaryKey(Long id) {
		return redPigCloudPurchaseLotteryMapper.selectByPrimaryKey(id);
	}


	public List<CloudPurchaseLottery> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<CloudPurchaseLottery> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigCloudPurchaseLotteryMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}

	@Autowired
	private RedPigCloudPurchaseGoodsService redPigCloudPurchaseGoodsService;

	@Autowired
	private RedPigCloudPurchaseCodeService redpigCloudPurchaseCodeService;

	@Transactional(readOnly = false)
	public int newLottery(Long goodsId) {
		int i = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		CloudPurchaseGoods goods = this.redPigCloudPurchaseGoodsService.selectByPrimaryKey(goodsId);
		if (goods == null) {
			return i;
		}
		if (goods.getGoodsNum() != 0) {
			CloudPurchaseLottery lottery = new CloudPurchaseLottery();
			lottery.setAddTime(new Date());
			lottery.setGoods_id(goodsId);
			lottery.setStatus(5);
			lottery.setDelivery_status(-1);
			lottery.setPurchased_times(0);
			lottery.setPurchased_left_times(goods.getGoods_price());
			lottery.setCloudPurchaseGoods(goods);
			if (saveEntity(lottery)) {
				List<Long> arr_list = Lists.newArrayList();
				for (int index = 0; index < lottery.getCloudPurchaseGoods().getGoods_price(); index++) {
					arr_list.add(CommUtil.null2Long(Integer.valueOf(index + 10000001)));
				}
				Collections.shuffle(arr_list);
				for (Long long1 : arr_list) {
					CloudPurchaseCode code = new CloudPurchaseCode();
					code.setAddTime(new Date());
					code.setCode(long1);
					code.setLottery_id(lottery.getId());
					this.redpigCloudPurchaseCodeService.saveEntity(code);
				}
				String str = lottery.getId().toString();
				if (str.length() < 4) {
					str = "0000" + str;
				}
				str = str.substring(str.length() - 4, str.length());
				lottery.setPeriod(goods.getClass_id() + sdf.format(new Date()) + str);
				updateById(lottery);
				i = 1;
			} else {
				i = 0;
			}
		} else {
			i = 2;
		}
		return i;
	}

	public String getExpect(Date date) {
		String expect = "";

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		expect = sdf1.format(date);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int h = calendar.get(11);
		int m = calendar.get(12);

		int time = 0;
		if (h >= 1) {
			time += 12;
		}
		if (h >= 2) {
			time += 12;
		}
		if (h >= 11) {
			time += 6;
		}
		if (h >= 12) {
			time += 6;
		}
		if (h >= 13) {
			time += 6;
		}
		if (h >= 14) {
			time += 6;
		}
		if (h >= 15) {
			time += 6;
		}
		if (h >= 16) {
			time += 6;
		}
		if (h >= 17) {
			time += 6;
		}
		if (h >= 18) {
			time += 6;
		}
		if (h >= 19) {
			time += 6;
		}
		if (h >= 20) {
			time += 6;
		}
		if (h >= 21) {
			time += 6;
		}
		if (h >= 22) {
			time += 6;
		}
		if (h >= 23) {
			time += 12;
		}
		if ((h >= 0) && (h <= 2)) {
			time += m / 5 + 1;
		}
		if ((h >= 10) && (h <= 22)) {
			time += m / 10 + 1;
		}
		if ((h >= 22) && (h <= 24)) {
			time += m / 5 + 1;
		}
		String m_time = time + "";
		while (m_time.length() < 3) {
			m_time = "0" + m_time;
		}
		expect = expect + m_time;

		return expect;
	}

	public Date getAnnounced_date(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int h = calendar.get(11);

		if ((h >= 0) && (h < 2)) {
			int minut = calendar.get(12);
			minut = minut / 5 * 5 + 11;
			minut -= calendar.get(12);
			calendar.add(12, minut);
			calendar.set(13, 0);
		} else if ((h >= 2) && (h < 10)) {
			calendar.set(11, 10);
			calendar.set(12, 6);
			calendar.set(13, 0);
		} else if ((h >= 10) && (h < 22)) {
			int minut = calendar.get(12);
			minut = minut / 10 * 10 + 16;
			minut -= calendar.get(12);
			calendar.add(12, minut);
			calendar.set(13, 0);
		} else if ((h >= 22) && (h <= 24)) {
			int minut = calendar.get(12);
			minut = minut / 5 * 5 + 11;
			minut -= calendar.get(12);
			calendar.add(12, minut);
			calendar.set(13, 0);
		}
		return calendar.getTime();
	}

	@Autowired
	private RedPigCloudPurchaseRecordService cloudPurchaseRecordService;


	@Autowired
	private CloudPurchaseGoodsMapper cloudPurchaseGoodMapper;

	@Transactional(readOnly = false)
	public int salesEnd(Long lotteryId) {
		CloudPurchaseLottery lottery = selectByPrimaryKey(lotteryId);
		lottery.setStatus(10);
		lottery.setSoldout_date(new Date());
		lottery.setExpect(getExpect(lottery.getSoldout_date()));

		lottery.setAnnounced_date(getAnnounced_date(lottery.getSoldout_date()));

		List<CloudPurchaseRecord> record_list = this.cloudPurchaseRecordService.getLatest50(lottery.getSoldout_date());

		int i = 0;
		BigDecimal user_time_num_count = new BigDecimal(0);

		for (CloudPurchaseRecord record : record_list) {
			BigDecimal user_time_num = new BigDecimal(CommUtil.null2Int(record.getPayTimeStamp()));
			user_time_num_count = user_time_num_count.add(user_time_num);
		}
		lottery.setUser_time_num_count(user_time_num_count);
		if (lottery.getCloudPurchaseGoods().getGoodsNum() > 0) {
			lottery.getCloudPurchaseGoods().setGoodsNum(lottery.getCloudPurchaseGoods().getGoodsNum() - 1);
			this.cloudPurchaseGoodMapper.updateById(lottery.getCloudPurchaseGoods());
		}

		updateById(lottery);
		boolean ret = true;
		if (ret) {
			if (lottery.getCloudPurchaseGoods().getGoods_status() == 0) {
				switch (newLottery(lottery.getCloudPurchaseGoods().getId())) {
				case 0:
					i = 2;
					break;
				case 1:
					i = 1;
					break;
				case 2:
					i = 3;
				}
			} else {
				i = 4;
			}
		} else {
			i = 0;
		}
		return i;
	}

	public boolean check_lottery_user(Long lottery_id, Long user_id) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("user_id", user_id);
		map.put("status", 1);

		List<CloudPurchaseRecord> record_list = this.cloudPurchaseRecordService.queryPageList(map);

		if (record_list.size() > 0) {
			return true;
		}
		return false;
	}


	public void delete_aftet_pay(Long id, Long id2) {
	}

	@Transactional(readOnly = false)
	public List<CloudPurchaseLottery> query(String class_id, String orderby, String ordertype, int begin_count,
			int select_count) {

		List<CloudPurchaseLottery> list = Lists.newArrayList();
		class_id = CommUtil.null2String(class_id);
		if ("popularity".equals(orderby)) {
			if (!"".equals(class_id)) {
				Map<String, Object> para = Maps.newHashMap();
				para.put("cloudPurchaseGoods_class_id", CommUtil.null2Long(class_id));
				para.put("status", 5);
//				para.put("orderBy", "cpg1.sell_num");
//				para.put("orderType", "desc");

				list = queryPageList(para, begin_count, select_count);// queryList(para,
																		// begin_count,
																		// select_count);

			} else {
				Map<String, Object> para = Maps.newHashMap();
//				para.put("orderBy", "cpg.sell_num");
//				para.put("orderType", "desc");

				list = queryPageList(para, begin_count, select_count);

			}
		} else if ("purchased_times".equals(orderby)) {
			if (!"".equals(class_id)) {
				Map<String, Object> para = Maps.newHashMap();
				para.put("cloudPurchaseGoods_class_id", CommUtil.null2Long(class_id));
				para.put("status", 5);
				para.put("orderBy", "purchased_left_times");
				para.put("orderType", "asc");

				list = queryPageList(para, begin_count, select_count);

			} else {
				Map<String, Object> para = Maps.newHashMap();
				para.put("status", 5);
				para.put("orderBy", "purchased_left_times");
				para.put("orderType", "asc");

				list = queryPageList(para, begin_count, select_count);

			}
		} else if ("addTime".equals(orderby)) {
			if (!"".equals(class_id)) {
				Map<String, Object> para = Maps.newHashMap();
				para.put("cloudPurchaseGoods_class_id", CommUtil.null2Long(class_id));
				para.put("status", 5);

				list = queryPageList(para, begin_count, select_count);

			} else {
				Map<String, Object> para = Maps.newHashMap();
				para.put("status", 5);

				list = queryPageList(para, begin_count, select_count);

			}
		} else if ("goods_price".equals(orderby)) {
			if (!"".equals(class_id)) {
				Map<String, Object> para = Maps.newHashMap();
				para.put("cloudPurchaseGoods_class_id", CommUtil.null2Long(class_id));
				para.put("orderBy", "cpg.goods_price");
				para.put("orderType", ordertype);

				list = queryPageList(para, begin_count, select_count);

			} else {

				Map<String, Object> para = Maps.newHashMap();
				para.put("orderBy", "cpg.goods_price");

				para.put("orderType", ordertype);

				list = queryPageList(para, begin_count, select_count);

			}
		}
		return list;
	}

	@Autowired
	private CloudPurchaseEveryColorMapper cloudPurchaseEveryColorMapper;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = false)
	public void runALottery() {
		int i = 0;
		Map<String, Object> lo_para = Maps.newHashMap();
		lo_para.put("status", Integer.valueOf(10));
		lo_para.put("announced_date_less_than_equal", new Date());

		List<CloudPurchaseLottery> lottery_list = this.queryPageList(lo_para);

		for (CloudPurchaseLottery lottery : lottery_list) {

			if (lottery.getStatus() == 10) {
				String lo_expect = lottery.getExpect();

				Map<String, Object> params = Maps.newHashMap();
				params.put("expect", lo_expect);

				List<CloudPurchaseEveryColor> list = this.cloudPurchaseEveryColorMapper.queryPageList(params);

				if ((list.size() == 0) && (i == 0)) {
					i++;
					try {
						URL url = new URL("http://f.apiplus.cn/cqssc.json");
						HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
						urlcon.connect();
						InputStream is = urlcon.getInputStream();
						BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
						StringBuffer bs = new StringBuffer();
						String l = null;
						while ((l = buffer.readLine()) != null) {
							bs.append(l);
						}
						Map<String, Object> map = JSON.parseObject(bs.toString());
						List<Map> code_list = (List) map.get("data");
						for (Map object : code_list) {
							Object expect = object.get("expect");
							Map<String, Object> para = Maps.newHashMap();
							para.put("expect", expect);

							if (this.cloudPurchaseEveryColorMapper.selectCount(para) == 0) {

								CloudPurchaseEveryColor color = new CloudPurchaseEveryColor();
								color.setAddTime(new Date());
								color.setExpect((String) expect);
								String opencode = (String) object.get("opencode");
								opencode = opencode.replaceAll(",", "");
								if (opencode.length() == 5) {
									color.setOpencode(opencode);
									String opentime = (String) object.get("opentime");
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									Date opendate = sdf.parse(opentime);
									color.setOpentime(opendate);
									this.cloudPurchaseEveryColorMapper.saveEntity(color);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					list = this.cloudPurchaseEveryColorMapper.queryPageList(params);

				}
				if (list.size() > 0) {
					String opencode = ((CloudPurchaseEveryColor) list.get(0)).getOpencode();

					BigDecimal _50he = lottery.getUser_time_num_count();
					BigDecimal shishicai = new BigDecimal(opencode);
					BigDecimal price = new BigDecimal(lottery.getCloudPurchaseGoods().getGoods_price());
					BigDecimal yushu = _50he.add(shishicai).divideAndRemainder(price)[1];
					BigDecimal luck = new BigDecimal(10000001).add(yushu);
					int asdf = luck.intValue();

					Map<String, Object> para = Maps.newHashMap();
					para.put("purchased_codes_like", CommUtil.null2Long(Integer.valueOf(asdf)));
					para.put("cloudPurchaseLottery_id", lottery.getId());
					List<CloudPurchaseRecord> list2 = this.cloudPurchaseRecordService.queryPageList(para);

					if (list2.size() > 0) {
						CloudPurchaseRecord record = (CloudPurchaseRecord) list2.get(0);
						record.setStatus(1);
						this.cloudPurchaseRecordService.saveEntity(record);
						lottery.setChange_code(yushu.toString());
						lottery.setLottery_num(new BigDecimal(opencode));
						lottery.setLucky_code(CommUtil.null2Long(Integer.valueOf(asdf)));
						lottery.setStatus(15);

						lottery.setLucky_userid(record.getUser_id().toString());
						lottery.setLucky_userbuytime(record.getPayTime());
						lottery.setLucky_username(record.getUser_name());
						lottery.setLucky_userphoto(record.getUser_photo());
						lottery.setLucky_usertimes(record.getPurchased_times() + "");

						this.updateById(lottery);
					}
				}
			}
		}
	}

}
