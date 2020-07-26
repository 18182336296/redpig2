package com.redpigmall.view.web.action.base;

import org.springframework.beans.factory.annotation.Autowired;

import com.redpigmall.api.sec.SessionRegistry;
import com.redpigmall.api.tools.database.RedPigDatabaseTools;
import com.redpigmall.logic.service.RedPigHandleOrderFormService;
import com.redpigmall.lucene.RedPigLuceneUtil;
import com.redpigmall.manage.admin.tools.RedPigCartTools;
import com.redpigmall.manage.admin.tools.RedPigChannelTools;
import com.redpigmall.manage.admin.tools.RedPigFreeTools;
import com.redpigmall.manage.admin.tools.RedPigGoodsTools;
import com.redpigmall.manage.admin.tools.RedPigImageTools;
import com.redpigmall.manage.admin.tools.RedPigInventoryTools;
import com.redpigmall.manage.admin.tools.RedPigOrderFormTools;
import com.redpigmall.manage.admin.tools.RedPigPaymentTools;
import com.redpigmall.manage.admin.tools.RedPigQueryTools;
import com.redpigmall.manage.admin.tools.RedPigStatTools;
import com.redpigmall.manage.admin.tools.RedPigStoreTools;
import com.redpigmall.manage.admin.tools.RedPigSubjectTools;
import com.redpigmall.manage.admin.tools.RedPigUserTools;
import com.redpigmall.manage.buyer.tools.RedPigFootPointTools;
import com.redpigmall.manage.delivery.tools.RedPigDeliveryAddressTools;
import com.redpigmall.manage.seller.tools.RedPigCombinTools;
import com.redpigmall.manage.seller.tools.RedPigTransportTools;
import com.redpigmall.module.weixin.view.tools.RedPigWeixinIndexTools;
import com.redpigmall.msg.RedPigMsgTools;
import com.redpigmall.service.*;
import com.redpigmall.pay.tools.RedPigPayTools;
import com.redpigmall.view.web.tools.RedPigActivityViewTools;
import com.redpigmall.view.web.tools.RedPigAreaViewTools;
import com.redpigmall.view.web.tools.RedPigArticleViewTools;
import com.redpigmall.view.web.tools.RedPigConsultViewTools;
import com.redpigmall.view.web.tools.RedPigCouponViewTools;
import com.redpigmall.view.web.tools.RedPigEvaluateViewTools;
import com.redpigmall.view.web.tools.RedPigGoodsCaseViewTools;
import com.redpigmall.view.web.tools.RedPigGoodsClassViewTools;
import com.redpigmall.view.web.tools.RedPigGoodsFloorViewTools;
import com.redpigmall.view.web.tools.RedPigGoodsViewTools;
import com.redpigmall.view.web.tools.RedPigGroupViewTools;
import com.redpigmall.view.web.tools.RedPigImageViewTools;
import com.redpigmall.view.web.tools.RedPigIntegralViewTools;
import com.redpigmall.view.web.tools.RedPigNavViewTools;
import com.redpigmall.view.web.tools.RedPigQueryUtils;
import com.redpigmall.view.web.tools.RedPigShowClassTools;
import com.redpigmall.view.web.tools.RedPigStoreViewTools;

/**
 * 
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
 * @date 2014-11-11
 * 
 * @version redpigmall_b2b2c 8.0
 */
public class BaseAction {

	@Autowired
	protected RedPigStoreSlideService storeSlideService;
	
	@Autowired
	protected RedPigStorePartnerService storepartnerService;
	
	@Autowired
	protected RedPigInformationService iInformationService;
	
	@Autowired
	protected RedPigUserService userSerivce;
	
	@Autowired
	protected RedPigStoreGradeService storegradeService;
	
	@Autowired
	protected RedPigStorePointService storePointService;
	
	@Autowired
	protected RedPigMerchantServicesService merchantService;

	@Autowired
	protected RedPigLuceneUtil lucene;
	@Autowired
	protected RedPigGoldRecordService goldRecordService;
	@Autowired
	protected RedPigGoldLogService goldLogService;
	
	@Autowired
	protected RedPigTemplateService templateService;
	
	@Autowired
	protected RedPigGroupInfoService groupInfoService;
	
	@Autowired
	protected RedPigPayoffLogService payoffservice;
	
	@Autowired
	protected RedPigHandleOrderFormService handelOrderFormService;
	@Autowired
	protected RedPigCloudPurchaseOrderService cloudPurchaseOrderService;
	@Autowired
	protected RedPigVerifyCodeService verifyCodeService;
	@Autowired
	protected RedPigQRLoginService qRLoginService;
	@Autowired
	protected RedPigImageViewTools imageViewTools;
	
	@Autowired
	protected RedPigVerifyCodeService mobileverifycodeService;
	@Autowired
	protected RedPigIntegralGoodsService integralGoodsService;

	@Autowired
	protected RedPigIntegralGoodsCartService integralGoodsCartService;

	@Autowired
	protected RedPigPredepositLogService predepositLogService;
	@Autowired
	protected RedPigPartnerService partnerService;

	@Autowired
	protected RedPigGroupService groupService;
	@Autowired
	protected RedPigGoodsFloorService goodsFloorService;

	@Autowired
	protected RedPigGoodsFloorViewTools gf_tools;

	@Autowired
	protected RedPigGoodsCaseService goodsCaseService;
	@Autowired
	protected RedPigGoodsCaseViewTools goodsCaseViewTools;

	@Autowired
	protected RedPigArticleViewTools article_Tools;
	@Autowired
	protected RedPigShowClassService showclassService;
	@Autowired
	protected RedPigShowClassTools showClassTools;

	@Autowired
	protected RedPigWeixinFloorService weixinfloorService;
	@Autowired
	protected RedPigWeixinIndexTools weixinIndexTools;

	@Autowired
	protected RedPigGroupAreaService groupAreaService;
	@Autowired
	protected RedPigGroupPriceRangeService groupPriceRangeService;
	@Autowired
	protected RedPigGroupClassService groupClassService;
	@Autowired
	protected RedPigGroupGoodsService groupGoodsService;
	@Autowired
	protected RedPigGroupLifeGoodsService groupLifeGoodsService;

	@Autowired
	protected RedPigGroupViewTools groupViewTools;

	@Autowired
	protected RedPigNavViewTools navTools;

	@Autowired
	protected RedPigGoodsClassViewTools gcViewTools;

	@Autowired
	protected RedPigAreaService AreaService;
	@Autowired
	protected RedPigGroupFloorService groupFloorService;
	@Autowired
	protected RedPigGroupAreaInfoService groupAreaInfoService;
	@Autowired
	protected RedPigGroupIndexService groupindexService;
	@Autowired
	protected RedPigUserGoodsClassService userGoodsClassService;

	@Autowired
	protected RedPigConsultService consultService;

	@Autowired
	protected RedPigConsultViewTools consultViewTools;
	@Autowired
	protected RedPigEvaluateViewTools evaluateViewTools;

	@Autowired
	protected RedPigStoreNavigationService storenavigationService;
	@Autowired
	protected RedPigConsultSatisService consultsatisService;

	@Autowired
	protected RedPigActivityGoodsService actgoodsService;
	@Autowired
	protected RedPigActivityViewTools activityViewTools;

	@Autowired
	protected RedPigFreeApplyLogService freeApplyLogService;

	@Autowired
	protected RedPigStoreHouseService storehouseService;
	@Autowired
	protected RedPigQueryTools QueryTools;

	@Autowired
	protected RedPigMerchantServicesService merchantServicesService;

	@Autowired
	protected RedPigGoodsBrandService brandService;

	@Autowired
	protected RedPigGoodsTypePropertyService goodsTypePropertyService;

	@Autowired
	protected RedPigQueryTools queryTools;
	@Autowired
	protected RedPigActivityViewTools activityviewTools;

	@Autowired
	protected RedPigShowClassService showClassService;
	@Autowired
	protected RedPigFootPointService footPointService;

	@Autowired
	protected RedPigFootPointTools footPointTools;

	@Autowired
	protected RedPigFreeGoodsService freegoodsService;
	@Autowired
	protected RedPigFreeClassService freeClassService;

	@Autowired
	protected RedPigFreeTools freeTools;
	@Autowired
	protected RedPigFreeApplyLogService freeapplylogService;

	@Autowired
	protected RedPigGoodsLogService goodsLogService;
	@Autowired
	protected RedPigDocumentService documentService;

	@Autowired
	protected RedPigCouponViewTools couponViewTools;
	@Autowired
	protected RedPigIntegralViewTools integralViewTools;
	@Autowired
	protected RedPigCouponService couponService;

	@Autowired
	protected RedPigChannelService channelService;

	@Autowired
	protected RedPigChannelFloorService channelfloorService;
	@Autowired
	protected RedPigChannelTools channelTools;

	@Autowired
	protected RedPigGoodsSpecPropertyService goodsSpecPropertyService;
	@Autowired
	protected RedPigAddressService addressService;
	@Autowired
	protected RedPigAreaService areaService;
	@Autowired
	protected RedPigPaymentService paymentService;

	@Autowired
	protected RedPigPaymentTools paymentTools;
	@Autowired
	protected RedPigPayTools payTools;

	@Autowired
	protected RedPigOrderFormTools orderFormTools;
	@Autowired
	protected RedPigCartTools cartTools;

	@Autowired
	protected RedPigEnoughReduceService enoughReduceService;
	@Autowired
	protected RedPigBuyGiftService buyGiftService;
	@Autowired
	protected RedPigCombinPlanService combinplanService;
	@Autowired
	protected RedPigActivityViewTools activityTools;
	@Autowired
	protected RedPigDeliveryAddressService deliveryaddrService;
	@Autowired
	protected RedPigDeliveryAddressTools DeliveryAddressTools;
	@Autowired
	protected RedPigGoodsClassService goodsClassService;
	@Autowired
	protected RedPigInventoryService inventoryService;
	@Autowired
	protected RedPigStoreHouseService storeHouseService;

	@Autowired
	protected RedPigHandleOrderFormService HandleOrderFormService;

	@Autowired
	protected RedPigVatInvoiceService vatinvoiceService;
	@Autowired
	protected RedPigGoodsBrandService goodsBrandService;
	@Autowired
	protected RedPigGoodsBrandCategoryService goodsBrandCategorySerivce;

	@Autowired
	protected RedPigGoodsViewTools goodsViewTools;

	@Autowired
	protected RedPigUserTools userTools;
	@Autowired
	protected RedPigAreaViewTools areaViewTools;
	@Autowired
	protected RedPigArticleService articleService;

	@Autowired
	protected RedPigAdvertPositionService advertPositionService;

	@Autowired
	protected RedPigUserService userService;
	@Autowired
	protected RedPigRoleService roleService;
	@Autowired
	protected RedPigStoreGradeService storeGradeService;
	@Autowired
	protected RedPigMessageService messageService;
	@Autowired
	protected RedPigAlbumService albumService;

	@Autowired
	protected RedPigAdvertService advertService;
	@Autowired
	protected RedPigPredepositService predepositService;
	@Autowired
	protected RedPigEvaluateService evaluateService;
	@Autowired
	protected RedPigGoodsCartService goodsCartService;
	@Autowired
	protected RedPigUserGoodsClassService ugcService;
	@Autowired
	protected RedPigSysLogService syslogService;
	@Autowired
	protected RedPigOrderFormService orderFormService;
	@Autowired
	protected RedPigOrderFormLogService orderFormLogService;
	@Autowired
	protected RedPigGroupLifeGoodsService grouplifegoodsService;
	@Autowired
	protected RedPigGoodsService goodsService;
	@Autowired
	protected RedPigNukeGoodsService nukeGoodsService;
	@Autowired
	protected RedPigGroupInfoService groupinfoService;
	@Autowired
	protected RedPigCouponInfoService couponInfoService;
	@Autowired
	protected RedPigPayoffLogService paylogService;
	@Autowired
	protected RedPigGoodsSpecPropertyService specpropertyService;
	@Autowired
	protected RedPigGoodsSpecificationService specService;
	@Autowired
	protected RedPigGoldLogService goldlogService;
	@Autowired
	protected RedPigFavoriteService favoriteService;
	@Autowired
	protected RedPigComplaintGoodsService complaintGoodsService;
	@Autowired
	protected RedPigStoreService storeService;
	@Autowired
	protected RedPigGoldRecordService grService;
	@Autowired
	protected RedPigStorePointService storepointService;
	@Autowired
	protected RedPigGoldLogService glService;
	@Autowired
	protected RedPigPredepositCashService redepositcashService;
	@Autowired
	protected RedPigStoreTools storeTools;
	@Autowired
	protected RedPigIntegralGoodsOrderService integralGoodsOrderService;
	@Autowired
	protected RedPigIntegralLogService integralLogService;
	@Autowired
	protected RedPigSnsAttentionService snsAttentionService;
	@Autowired
	protected RedPigDeliveryAddressService deliveryAddressService;
	@Autowired
	protected SessionRegistry sessionRegistry;
	@Autowired
	protected RedPigStoreStatService storeStatService;
	@Autowired
	protected RedPigSystemTipService systemTipService;
	@Autowired
	protected RedPigChattingUserService chattingUserService;
	@Autowired
	protected RedPigMsgTools msgTools;
	@Autowired
	protected RedPigStatTools statTools;
	@Autowired
	protected RedPigDatabaseTools databaseTools;
	@Autowired
	protected RedPigSysLogService sysLogService;
	@Autowired
	protected RedPigSysConfigService configService;
	@Autowired
	protected RedPigAccessoryService accessoryService;
	@Autowired
	protected RedPigUserConfigService userConfigService;
	@Autowired
	protected RedPigSubjectService subjectService;
	@Autowired
	protected RedPigSubjectTools SubjectTools;

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
	protected RedPigGoodsTools goodsTools;

	@Autowired
	protected RedPigTransportTools transportTools;

	@Autowired
	protected RedPigInventoryTools inventoryTools;

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
	protected RedPigOrderFormTools redPigOrderFormTools;

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

	@Autowired
	protected RedPigVatInvoiceService redPigVatinvoiceService;

	@Autowired
	protected RedPigConsultService redPigConsultService;

	@Autowired
	protected RedPigEvaluateViewTools redPigEvaluateViewTools;

	@Autowired
	protected RedPigComplaintService redPigComplaintService;

	@Autowired
	protected RedPigReturnGoodsLogService redPigReturngoodslogService;

	@Autowired
	protected RedPigExpressCompanyService redPigExpressCompayService;

	@Autowired
	protected RedPigPredepositService redPigPredepositService;

	@Autowired
	protected RedPigPredepositLogService redPigPredepositLogService;

	@Autowired
	protected RedPigReturnGoodsLogService redPigReturnGoodsLogService;

	@Autowired
	protected RedPigRefundLogService redPigRefundLogService;

}
