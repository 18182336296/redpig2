package com.redpigmall.module.circle.manage.admin.action.base;

import org.springframework.beans.factory.annotation.Autowired;

import com.redpigmall.manage.admin.tools.RedPigFreeTools;
import com.redpigmall.manage.admin.tools.RedPigImageTools;
import com.redpigmall.manage.admin.tools.RedPigStoreTools;
import com.redpigmall.manage.delivery.tools.RedPigDeliveryAddressTools;
import com.redpigmall.manage.seller.tools.RedPigCombinTools;
import com.redpigmall.manage.seller.tools.RedPigTransportTools;
import com.redpigmall.module.circle.view.tools.RedPigCircleViewTools;
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
import com.redpigmall.service.RedPigCombinPlanService;
import com.redpigmall.service.RedPigComplaintGoodsService;
import com.redpigmall.service.RedPigCouponInfoService;
import com.redpigmall.service.RedPigDeliveryAddressService;
import com.redpigmall.service.RedPigDocumentService;
import com.redpigmall.service.RedPigEnoughReduceService;
import com.redpigmall.service.RedPigEvaluateService;
import com.redpigmall.service.RedPigExpressCompanyService;
import com.redpigmall.service.RedPigFavoriteService;
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
import com.redpigmall.service.RedPigIntegralGoodsOrderService;
import com.redpigmall.service.RedPigIntegralGoodsService;
import com.redpigmall.service.RedPigIntegralLogService;
import com.redpigmall.service.RedPigInventoryOperationService;
import com.redpigmall.service.RedPigInventoryService;
import com.redpigmall.service.RedPigMerchantServicesService;
import com.redpigmall.service.RedPigMessageService;
import com.redpigmall.service.RedPigNavigationService;
import com.redpigmall.service.RedPigOrderFormLogService;
import com.redpigmall.service.RedPigOrderFormService;
import com.redpigmall.service.RedPigPartnerService;
import com.redpigmall.service.RedPigPaymentService;
import com.redpigmall.service.RedPigPayoffLogService;
import com.redpigmall.service.RedPigPredepositCashService;
import com.redpigmall.service.RedPigRechargeCardService;
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
import com.redpigmall.service.RedPigWaterMarkService;
import com.redpigmall.view.web.tools.RedPigArticleViewTools;
import com.redpigmall.view.web.tools.RedPigGoodsViewTools;
import com.redpigmall.view.web.tools.RedPigIntegralViewTools;
import com.redpigmall.view.web.tools.RedPigQueryUtils;
import com.redpigmall.view.web.tools.RedPigStoreViewTools;
/**
 * <p>
 * Title: BaseAction.java
 * </p>
 * 
 * <p>
 * Description: 统一注入需要使用的实体类,不会有任何影响,这些bean已经在spring容器中被构建出来,这里只是做一个注入
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
 * @date 2017-4-11
 * 
 * @version redpigmall_b2b2c 8.0
 */
public class BaseAction {
	@Autowired
	protected RedPigGoodsService goodsService;
	@Autowired
	protected RedPigStoreService storeService;

	@Autowired
	protected RedPigFavoriteService favoriteService;
	@Autowired
	protected RedPigIntegralViewTools IntegralViewTools;
	@Autowired
	protected RedPigCircleViewTools circleViewTools;
	@Autowired
	protected RedPigSysConfigService configService;
	@Autowired
	protected RedPigUserConfigService userConfigService;
	@Autowired
	protected RedPigUserService userService;
	@Autowired
	protected RedPigAccessoryService accessoryService;
	@Autowired
	protected RedPigCircleService circleService;
	@Autowired
	protected RedPigCircleClassService circleclassService;
	@Autowired
	protected RedPigCircleInvitationService invitationService;
	@Autowired
	protected RedPigMessageService messageService;
	@Autowired
	protected RedPigCircleInvitationReplyService invitationReplyService;
	@Autowired
	protected RedPigNavigationService navigationService;
	@Autowired
	protected RedPigIntegralViewTools redpigIntegralViewTools;
	
	@Autowired
	protected RedPigSysConfigService redPigSysConfigService;

	@Autowired
	protected RedPigUserConfigService redPigUserConfigService;

	@Autowired
	protected RedPigQueryUtils redPigQueryTools;

	@Autowired
	protected RedPigUserService redPigUserService;

	@Autowired
	protected RedPigStoreTools redPigStoreTools;

	@Autowired
	protected RedPigIntegralViewTools redPigIntegralViewTools;

	@Autowired
	protected RedPigRoleService redPigRoleService;

	@Autowired
	protected RedPigAlbumService redPigAlbumService;

	@Autowired
	protected RedPigOrderFormService redPigOrderFormService;

	@Autowired
	protected RedPigCouponInfoService redPigCouponInfoService;

	@Autowired
	protected RedPigAccessoryService redPigAccessoryService;

	@Autowired
	protected RedPigGoodsCartService redPigGoodsCartService;

	@Autowired
	protected RedPigPredepositCashService redPigPredepositcashService;

	@Autowired
	protected RedPigIntegralGoodsOrderService redPigIntegralGoodsOrderService;

	@Autowired
	protected RedPigIntegralLogService redPigIntegralLogService;

	@Autowired
	protected RedPigGoldLogService redPigGoldlogService;

	@Autowired
	protected RedPigStorePointService redPigStorepointService;

	@Autowired
	protected RedPigAdvertService redPigAdvertService;

	@Autowired
	protected RedPigDeliveryAddressService redPigDeliveryAddressService;

	@Autowired
	protected RedPigSnsAttentionService redPigSnsAttentionService;

	@Autowired
	protected RedPigSysLogService redPigSyslogService;

	@Autowired
	protected RedPigStoreService redPigStoreService;

	@Autowired
	protected RedPigGoodsService redPigGoodsService;

	@Autowired
	protected RedPigEvaluateService redPigEvaluateService;

	@Autowired
	protected RedPigComplaintGoodsService redPigComplaintGoodsService;

	@Autowired
	protected RedPigGoldRecordService redPigGoldRecordService;

	@Autowired
	protected RedPigGroupInfoService redPigGroupinfoService;

	@Autowired
	protected RedPigGroupLifeGoodsService redPigGrouplifegoodsService;

	@Autowired
	protected RedPigPayoffLogService redPigPayoffLogService;

	@Autowired
	protected RedPigGoodsSpecPropertyService redPigGoodsSpecPropertyService;

	@Autowired
	protected RedPigStoreGradeService redPigStoreGradeService;

	@Autowired
	protected RedPigMessageService redPigMessageService;

	@Autowired
	protected RedPigGoodsBrandService redPigGoodsBrandService;

	@Autowired
	protected RedPigGoodsClassService redPigGoodsClassService;

	@Autowired
	protected RedPigTemplateService redPigTemplateService;

	@Autowired
	protected RedPigOrderFormLogService redPigOrderFormLogService;

	@Autowired
	protected RedPigUserGoodsClassService redPigUserGoodsClassService;

	@Autowired
	protected RedPigGoodsTypePropertyService redPigGoodsTypePropertyService;

	@Autowired
	protected RedPigWaterMarkService redPigWaterMarkService;

	@Autowired
	protected RedPigTransportService redPigTransportService;

	@Autowired
	protected RedPigPaymentService redPigPaymentService;

	@Autowired
	protected RedPigGoodsSpecificationService redPigGoodsSpecificationService;

	@Autowired
	protected RedPigGoodsFormatService redPigGoodsFormatService;

	@Autowired
	protected RedPigAreaService redPigAreaService;

	@Autowired
	protected RedPigShipAddressService redPigShipAddressService;

	@Autowired
	protected RedPigGoodsTagService redPigGoodsTagService;

	@Autowired
	protected RedPigInventoryService redPigInventoryService;

	@Autowired
	protected RedPigStoreHouseService redPigStoreHouseService;

	@Autowired
	protected RedPigInventoryOperationService redPigInventoryOperationService;

	@Autowired
	protected RedPigGoodsTypeService redPigGoodsTypeService;

	@Autowired
	protected RedPigMerchantServicesService redPigMerchantServicesService;

	@Autowired
	protected RedPigStoreViewTools storeViewTools;

	@Autowired
	protected RedPigTransportTools transportTools;

	@Autowired
	protected RedPigGoodsViewTools redPigGoodsViewTools;

	@Autowired
	protected RedPigImageTools imageTools;

	@Autowired
	protected RedPigNavigationService redPigNavigationService;

	@Autowired
	protected RedPigGoldRecordService redPigGoldrecordService;

	@Autowired
	protected RedPigGoldLogService redPigGoldLogService;

	@Autowired
	protected RedPigRechargeCardService redPigRechargeCardService;

	@Autowired
	protected RedPigSmsGoldLogService redPigSmsGoldLogService;

	@Autowired
	protected RedPigDeliveryAddressTools redPigDeliveryAddressTools;

	@Autowired
	protected RedPigAdvertPositionService redPigAdvertPositionService;

	@Autowired
	protected RedPigIntegralGoodsService redPigIntegralgoodsService;

	@Autowired
	protected RedPigExpressCompanyService redPigExpressCompanyService;


	@Autowired
	protected RedPigActivityService redPigActivityService;

	@Autowired
	protected RedPigActivityGoodsService redPigActivityGoodsService;

	@Autowired
	protected RedPigGroupService redPigGroupService;

	@Autowired
	protected RedPigGroupGoodsService redPigGroupGoodsService;

	@Autowired
	protected RedPigGroupLifeGoodsService redPigGroupLifeGoodsService;

	@Autowired
	protected RedPigGroupFloorService redPigGroupfloorService;

	@Autowired
	protected RedPigGroupClassService redPigGroupClassService;
	
	@Autowired
	protected RedPigCombinTools redPigCombinTools;
	
	@Autowired
	protected RedPigCombinPlanService redPigCombinplanService;
	
	@Autowired
	protected RedPigEnoughReduceService redPigEnoughreduceService;
	
	@Autowired
	protected RedPigBuyGiftService redPigBuygiftService;
	

	@Autowired
	protected RedPigFreeGoodsService redPigFreegoodsService;
	
	@Autowired
	protected RedPigFreeClassService redPigFreeClassService;
	
	@Autowired
	protected RedPigFreeTools redPigFreeTools;
	
	@Autowired
	protected RedPigFreeApplyLogService redPigFreeapplylogService;
	
	@Autowired
	protected RedPigFreeClassService redPigFreeclassService;
	
	@Autowired
	protected RedPigArticleClassService articleClassService;
	
	@Autowired
	protected RedPigArticleViewTools articleTools;
	
	@Autowired
	protected RedPigArticleService redPigArticleService;
	
	@Autowired
	protected RedPigDocumentService redPigDocumentService;
	
	@Autowired
	protected RedPigPartnerService redPigPartnerService;

	@Autowired
	protected RedPigSubjectService redPigSubjectService;

	
}
