package com.redpigmall.service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.dao.SysConfigMapper;
import com.redpigmall.service.RedPigSysConfigService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigSysConfigService extends BaseService<SysConfig>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<SysConfig> objs) {
		if (objs != null && objs.size() > 0) {
			redPigSysConfigMapper.batchDelete(objs);
		}
	}


	public SysConfig getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<SysConfig> objs = redPigSysConfigMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<SysConfig> selectObjByProperty(Map<String, Object> maps) {
		return redPigSysConfigMapper.selectObjByProperty(maps);
	}


	public List<SysConfig> queryPages(Map<String, Object> params) {
		return redPigSysConfigMapper.queryPages(params);
	}


	public List<SysConfig> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigSysConfigMapper.queryPageListWithNoRelations(param);
	}


	public List<SysConfig> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigSysConfigMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private SysConfigMapper redPigSysConfigMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigSysConfigMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(SysConfig obj) {
		redPigSysConfigMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(SysConfig obj) {
		redPigSysConfigMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigSysConfigMapper.deleteById(id);
	}


	public SysConfig selectByPrimaryKey(Long id) {
		return redPigSysConfigMapper.selectByPrimaryKey(id);
	}


	public List<SysConfig> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}

	@Transactional(readOnly = false)

	public SysConfig getSysConfig() {

		Map<String, Object> maps = Maps.newHashMap();
		maps.put("orderBy", "addTime");
		maps.put("orderType", "DESC");
		
		List<SysConfig> configs = Lists.newArrayList();
		try {

			configs = this.redPigSysConfigMapper.queryPageList(maps);
		} catch (Exception e) {
			e.printStackTrace(); 
		}

		if ((configs != null) && (configs.size() > 0)) {
			SysConfig sc = (SysConfig) configs.get(0);

			if (sc.getUploadFilePath() == null) {
				sc.setUploadFilePath("upload");
			}
			if (sc.getSysLanguage() == null) {
				sc.setSysLanguage("zh_cn");
			}
			if (sc.getSysPcLanguage() == null) {
				sc.setSysPcLanguage("zh_cn");
			}
			if (sc.getSysStoreLanguage() == null) {
				sc.setSysStoreLanguage("zh_cn");
			}
			if ((sc.getWebsiteName() == null) || (sc.getWebsiteName().equals(""))) {
				sc.setWebsiteName("RedPigMall");
			}
			if ((sc.getCloseReason() == null) || (sc.getCloseReason().equals(""))) {
				sc.setCloseReason("系统维护中...");
			}
			if ((sc.getTitle() == null) || (sc.getTitle().equals(""))) {
				sc.setTitle("RedPigMall B2B2C商城系统");
			}
			if ((sc.getImageSaveType() == null) || (sc.getImageSaveType().equals(""))) {
				sc.setImageSaveType("sidImg");
			}
			if (sc.getImageFilesize() == 0) {
				sc.setImageFilesize(1024);
			}
			if (sc.getSmallWidth() == 0) {
				sc.setSmallWidth(160);
			}
			if (sc.getSmallHeight() == 0) {
				sc.setSmallHeight(160);
			}
			if (sc.getMiddleWidth() == 0) {
				sc.setMiddleWidth(300);
			}
			if (sc.getMiddleHeight() == 0) {
				sc.setMiddleHeight(300);
			}
			if (sc.getBigHeight() == 0) {
				sc.setBigHeight(1024);
			}
			if (sc.getBigWidth() == 0) {
				sc.setBigWidth(1024);
			}
			if ((sc.getImageSuffix() == null) || (sc.getImageSuffix().equals(""))) {
				sc.setImageSuffix("gif|jpg|jpeg|bmp|png|tbi");
			}
			if (sc.getStoreImage() == null) {
				Accessory storeImage = new Accessory();
				storeImage.setPath("resources/style/common/images");
				storeImage.setName("store.jpg");
				sc.setStoreImage(storeImage);
			}
			if (sc.getGoodsImage() == null) {
				Accessory goodsImage = new Accessory();
				goodsImage.setPath("resources/style/common/images");
				goodsImage.setName("good.jpg");
				sc.setGoodsImage(goodsImage);
			}
			if (sc.getMemberIcon() == null) {
				Accessory memberIcon = new Accessory();
				memberIcon.setPath("resources/style/common/images");
				memberIcon.setName("member.jpg");
				sc.setMemberIcon(memberIcon);
			}
			if (sc.getCurrency_code() == null) {
				sc.setCurrency_code("¥");
			}
			if ((sc.getSecurityCodeType() == null) || (sc.getSecurityCodeType().equals(""))) {
				sc.setSecurityCodeType("normal");
			}
			if ((sc.getWebsiteCss() == null) || (sc.getWebsiteCss().equals(""))) {
				sc.setWebsiteCss("blue");
			}
			if (sc.getPayoff_date() == null) {
				Calendar cale = Calendar.getInstance();
				cale.set(5, cale.getActualMaximum(5));
				sc.setPayoff_date(cale.getTime());
			}
			if ((sc.getSmsURL() == null) || (sc.getSmsURL().equals(""))) {
				sc.setSmsURL("http://service.winic.org/sys_port/gateway/");
			}
			if (sc.getAuto_order_notice() == 0) {
				sc.setAuto_order_notice(3);
			}
			if (sc.getAuto_order_evaluate() == 0) {
				sc.setAuto_order_evaluate(7);
			}
			if (sc.getAuto_order_return() == 0) {
				sc.setAuto_order_return(7);
			}
			if (sc.getAuto_order_confirm() == 0) {
				sc.setAuto_order_confirm(7);
			}
			if (sc.getGrouplife_order_return() == 0) {
				sc.setGrouplife_order_return(7);
			}
			return sc;
		}

		SysConfig sc = new SysConfig();
		sc.setUploadFilePath("upload");
		sc.setWebsiteName("RedPigMall");
		sc.setSysLanguage("zh_cn");
		sc.setTitle("RedPigMall B2B2C商城系统");
		sc.setSecurityCodeType("normal");
		sc.setEmailEnable(true);
		sc.setCloseReason("系统维护中...");
		sc.setImageSaveType("sidImg");
		sc.setImageFilesize(1024);
		sc.setSmallWidth(160);
		sc.setSmallHeight(160);
		sc.setMiddleHeight(300);
		sc.setMiddleWidth(300);
		sc.setBigHeight(1024);
		sc.setBigWidth(1024);
		sc.setImageSuffix("gif|jpg|jpeg|bmp|png|tbi");
		sc.setComplaint_time(30);
		sc.setWebsiteCss("blue");
		sc.setSmsURL("http://service.winic.org/sys_port/gateway/");

		Accessory goodsImage = new Accessory();
		goodsImage.setPath("resources/style/common/images");
		goodsImage.setName("good.jpg");
		sc.setGoodsImage(goodsImage);

		Accessory storeImage = new Accessory();
		storeImage.setPath("resources/style/common/images");
		storeImage.setName("store.jpg");
		sc.setStoreImage(storeImage);

		Accessory memberIcon = new Accessory();
		memberIcon.setPath("resources/style/common/images");
		memberIcon.setName("member.jpg");
		sc.setMemberIcon(memberIcon);
		Calendar cale = Calendar.getInstance();
		cale.set(5, cale.getActualMaximum(5));
		sc.setCurrency_code("¥");
		sc.setPayoff_date(cale.getTime());
		sc.setAuto_order_notice(3);
		sc.setAuto_order_evaluate(7);
		sc.setAuto_order_return(7);
		sc.setAuto_order_confirm(7);
		sc.setGrouplife_order_return(7);
		return sc;

	}

	@SuppressWarnings("rawtypes")

	public List queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);

	}


	@Transactional(readOnly = false)
	public void save(SysConfig config) {
		redPigSysConfigMapper.save(config);
	}


	@Transactional(readOnly = false)
	public void update(SysConfig config) {
		redPigSysConfigMapper.update(config);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigSysConfigMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}

}
