package com.redpigmall.logic.service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.logic.service.RedPigLuceneClusterService;
import com.redpigmall.lucene.LuceneThread;
import com.redpigmall.lucene.LuceneUtil;
import com.redpigmall.lucene.LuceneVo;
import com.redpigmall.lucene.tools.RedPigLuceneVoTools;
import com.redpigmall.manage.admin.tools.RedPigFreeTools;
import com.redpigmall.manage.admin.tools.RedPigGoodsFloorTools;
import com.redpigmall.manage.admin.tools.RedPigGoodsTools;
import com.redpigmall.manage.admin.tools.RedPigImageTools;
import com.redpigmall.manage.admin.tools.RedPigInventoryTools;
import com.redpigmall.manage.admin.tools.RedPigOrderFormTools;
import com.redpigmall.manage.admin.tools.RedPigStoreTools;
import com.redpigmall.manage.delivery.tools.RedPigDeliveryAddressTools;
import com.redpigmall.manage.seller.tools.RedPigCombinTools;
import com.redpigmall.manage.seller.tools.RedPigTransportTools;
import com.redpigmall.module.circle.view.tools.RedPigCircleViewTools;
import com.redpigmall.module.weixin.view.tools.Base64Tools;
import com.redpigmall.module.weixin.view.tools.RedPigWeixinChannelFloorTools;
import com.redpigmall.module.weixin.view.tools.RedPigWeixinTools;
import com.redpigmall.domain.Article;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GroupGoods;
import com.redpigmall.domain.GroupLifeGoods;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.service.RedPigAccessoryService;
import com.redpigmall.service.RedPigActivityGoodsService;
import com.redpigmall.service.RedPigActivityService;
import com.redpigmall.service.RedPigAdvertPositionService;
import com.redpigmall.service.RedPigAdvertService;
import com.redpigmall.service.RedPigAlbumService;
import com.redpigmall.service.RedPigAreaService;
import com.redpigmall.service.RedPigArticleClassService;
import com.redpigmall.service.RedPigArticleService;
import com.redpigmall.service.RedPigBuyGiftService;
import com.redpigmall.service.RedPigCircleClassService;
import com.redpigmall.service.RedPigCircleInvitationReplyService;
import com.redpigmall.service.RedPigCircleInvitationService;
import com.redpigmall.service.RedPigCircleService;
import com.redpigmall.service.RedPigCloudPurchaseClassService;
import com.redpigmall.service.RedPigCloudPurchaseGoodsService;
import com.redpigmall.service.RedPigCloudPurchaseLotteryService;
import com.redpigmall.service.RedPigCmsIndexTemplateService;
import com.redpigmall.service.RedPigCombinPlanService;
import com.redpigmall.service.RedPigComplaintGoodsService;
import com.redpigmall.service.RedPigComplaintService;
import com.redpigmall.service.RedPigConsultService;
import com.redpigmall.service.RedPigCouponInfoService;
import com.redpigmall.service.RedPigDeliveryAddressService;
import com.redpigmall.service.RedPigDocumentService;
import com.redpigmall.service.RedPigEnoughReduceService;
import com.redpigmall.service.RedPigEvaluateService;
import com.redpigmall.service.RedPigExpressCompanyService;
import com.redpigmall.service.RedPigFreeApplyLogService;
import com.redpigmall.service.RedPigFreeClassService;
import com.redpigmall.service.RedPigFreeGoodsService;
import com.redpigmall.service.RedPigGoldLogService;
import com.redpigmall.service.RedPigGoldRecordService;
import com.redpigmall.service.RedPigGoodsBrandService;
import com.redpigmall.service.RedPigGoodsCartService;
import com.redpigmall.service.RedPigGoodsClassService;
import com.redpigmall.service.RedPigGoodsFormatService;
import com.redpigmall.service.RedPigGoodsService;
import com.redpigmall.service.RedPigGoodsSpecPropertyService;
import com.redpigmall.service.RedPigGoodsSpecificationService;
import com.redpigmall.service.RedPigGoodsTagService;
import com.redpigmall.service.RedPigGoodsTypePropertyService;
import com.redpigmall.service.RedPigGoodsTypeService;
import com.redpigmall.service.RedPigGroupClassService;
import com.redpigmall.service.RedPigGroupFloorService;
import com.redpigmall.service.RedPigGroupGoodsService;
import com.redpigmall.service.RedPigGroupInfoService;
import com.redpigmall.service.RedPigGroupLifeGoodsService;
import com.redpigmall.service.RedPigGroupService;
import com.redpigmall.service.RedPigInformationClassService;
import com.redpigmall.service.RedPigInformationReplyService;
import com.redpigmall.service.RedPigInformationService;
import com.redpigmall.service.RedPigIntegralGoodsOrderService;
import com.redpigmall.service.RedPigIntegralGoodsService;
import com.redpigmall.service.RedPigIntegralLogService;
import com.redpigmall.service.RedPigInventoryOperationService;
import com.redpigmall.service.RedPigInventoryService;
import com.redpigmall.service.RedPigMenuService;
import com.redpigmall.service.RedPigMerchantServicesService;
import com.redpigmall.service.RedPigMessageService;
import com.redpigmall.service.RedPigNavigationService;
import com.redpigmall.service.RedPigOrderFormLogService;
import com.redpigmall.service.RedPigOrderFormService;
import com.redpigmall.service.RedPigPartnerService;
import com.redpigmall.service.RedPigPaymentService;
import com.redpigmall.service.RedPigPayoffLogService;
import com.redpigmall.service.RedPigPredepositCashService;
import com.redpigmall.service.RedPigPredepositLogService;
import com.redpigmall.service.RedPigPredepositService;
import com.redpigmall.service.RedPigRechargeCardService;
import com.redpigmall.service.RedPigRefundLogService;
import com.redpigmall.service.RedPigReplyContentService;
import com.redpigmall.service.RedPigReturnGoodsLogService;
import com.redpigmall.service.RedPigRoleGroupService;
import com.redpigmall.service.RedPigRoleService;
import com.redpigmall.service.RedPigShipAddressService;
import com.redpigmall.service.RedPigSmsGoldLogService;
import com.redpigmall.service.RedPigSnsAttentionService;
import com.redpigmall.service.RedPigStoreGradeService;
import com.redpigmall.service.RedPigStoreHouseService;
import com.redpigmall.service.RedPigStorePointService;
import com.redpigmall.service.RedPigStoreService;
import com.redpigmall.service.RedPigSubjectService;
import com.redpigmall.service.RedPigSysConfigService;
import com.redpigmall.service.RedPigSysLogService;
import com.redpigmall.service.RedPigTemplateService;
import com.redpigmall.service.RedPigTransportService;
import com.redpigmall.service.RedPigUserConfigService;
import com.redpigmall.service.RedPigUserGoodsClassService;
import com.redpigmall.service.RedPigUserService;
import com.redpigmall.service.RedPigVMenuService;
import com.redpigmall.service.RedPigVatInvoiceService;
import com.redpigmall.service.RedPigWaterMarkService;
import com.redpigmall.service.RedPigWeixinChannelFloorService;
import com.redpigmall.service.RedPigWeixinChannelService;
import com.redpigmall.view.web.tools.RedPigArticleViewTools;
import com.redpigmall.view.web.tools.RedPigCmsTools;
import com.redpigmall.view.web.tools.RedPigEmojiTools;
import com.redpigmall.view.web.tools.RedPigEvaluateViewTools;
import com.redpigmall.view.web.tools.RedPigGoodsViewTools;
import com.redpigmall.view.web.tools.RedPigIntegralViewTools;
import com.redpigmall.view.web.tools.RedPigQueryUtils;
import com.redpigmall.view.web.tools.RedPigRoleTools;
import com.redpigmall.view.web.tools.RedPigStoreViewTools;

/**
 * <p>
 * Title: RedPigLuceneClusterServiceImpl.java
 * </p>
 * 
 * <p>
 * Description: lucene集群类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2014-12-9
 * 
 * @version redpigmall_b2b2c 2016
 */
@SuppressWarnings({ "unused"})
@Service
@Transactional
public class RedPigLuceneClusterService {
	
	@Autowired
	protected RedPigGoodsService goodsService;
	
	@Autowired
	protected RedPigGroupLifeGoodsService groupLifeGoodsService;
	
	@Autowired
	protected RedPigGroupGoodsService groupGoodsService;
	
	@Autowired
	protected RedPigArticleService articleService;
	
	@Autowired
	protected RedPigLuceneVoTools luceneVoTools;
	
	@Autowired
	protected RedPigSysConfigService configService;
	
	/**
	 * lucene更新
	 */
	@Transactional(readOnly=false)

	public void updateLuceneCluster() {
		
		LuceneUtil.deleteAllLuceneIndex(new File(ClusterSyncTools.getClusterRoot()+File.separator+"luence"+File.separator));
		
		try {
			
			Map<String, Object> params = Maps.newHashMap();
			params.put("goods_status", 0);
			List<Goods> goods_list = this.goodsService.queryPageList(params);
			params.clear();
			params.put("group_status", 1);
			List<GroupLifeGoods> lifeGoods_list = this.groupLifeGoodsService.queryPageList(params);
			params.clear();
			params.put("gg_status", 1);
			List<GroupGoods> groupGoods_list = this.groupGoodsService.queryPageList(params);
			params.clear();
			params.put("display", true);
			List<Article> article_list = this.articleService.queryPageList(params);
			String goods_lucene_path = ClusterSyncTools.getClusterRoot() + File.separator + "luence" + File.separator
					+ "goods";
			String grouplifegoods_lucene_path = ClusterSyncTools.getClusterRoot() + File.separator + "luence"
					+ File.separator + "lifegoods";
			String groupgoods_lucene_path = ClusterSyncTools.getClusterRoot() + File.separator + "luence"
					+ File.separator + "groupgoods";
			File file = new File(goods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(goods_lucene_path);
			}
			file = new File(grouplifegoods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(grouplifegoods_lucene_path);
			}
			file = new File(groupgoods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(groupgoods_lucene_path);
			}
			List<LuceneVo> goods_vo_list = Lists.newArrayList();
			LuceneVo vo;
			for (Goods goods : goods_list) {
				vo = this.luceneVoTools.setGoodsVo(goods);
				goods_vo_list.add(vo);
			}
			List<LuceneVo> lifegoods_vo_list = Lists.newArrayList();
			for (GroupLifeGoods goods : lifeGoods_list) {
				vo = this.luceneVoTools.setLifeGoodsVo(goods);
				lifegoods_vo_list.add(vo);
			}
			List<LuceneVo> groupgoods_vo_list = Lists.newArrayList();
			for (GroupGoods goods : groupGoods_list) {
				vo = this.luceneVoTools.setGroupGoodsVo(goods);
				groupgoods_vo_list.add(vo);
			}
			LuceneThread goods_thread = new LuceneThread(goods_lucene_path, goods_vo_list);
			LuceneThread lifegoods_thread = new LuceneThread(grouplifegoods_lucene_path, lifegoods_vo_list);
			LuceneThread groupgoods_thread = new LuceneThread(groupgoods_lucene_path, groupgoods_vo_list);
			Date d1 = new Date();
			
			goods_thread.run();
			lifegoods_thread.run();
			groupgoods_thread.run();
			Date d2 = new Date();
			String path = ClusterSyncTools.getClusterRoot() + File.separator + "luence";
			Map<String, Object> map = Maps.newHashMap();
			map.put("run_time", Long.valueOf(d2.getTime() - d1.getTime()));
			map.put("file_size", CommUtil.fileSize(new File(path)));
			map.put("update_time", CommUtil.formatLongDate(new Date()));
			SysConfig config = this.configService.getSysConfig();
			config.setLucene_update(new Date());
			this.configService.updateById(config);
		} catch (Exception e) {
			
		}
		
	}
	
}
