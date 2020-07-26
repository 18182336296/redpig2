package com.redpigmall.manage.admin.action.base;

import org.springframework.beans.factory.annotation.Autowired;

import com.redpigmall.api.sec.SessionRegistry;
import com.redpigmall.api.tools.database.RedPigDatabaseTools;
import com.redpigmall.logic.service.RedPigHandleOrderFormService;
import com.redpigmall.lucene.tools.RedPigLuceneVoTools;
import com.redpigmall.manage.admin.tools.RedPigAreaManageTools;
import com.redpigmall.manage.admin.tools.RedPigCartTools;
import com.redpigmall.manage.admin.tools.RedPigChannelTools;
import com.redpigmall.manage.admin.tools.RedPigFreeTools;
import com.redpigmall.manage.admin.tools.RedPigGoodsFloorTools;
import com.redpigmall.manage.admin.tools.RedPigGoodsTools;
import com.redpigmall.manage.admin.tools.RedPigGroupTools;
import com.redpigmall.manage.admin.tools.RedPigImageTools;
import com.redpigmall.manage.admin.tools.RedPigInventoryTools;
import com.redpigmall.manage.admin.tools.RedPigOrderFormTools;
import com.redpigmall.manage.admin.tools.RedPigPaymentTools;
import com.redpigmall.manage.admin.tools.RedPigPayoffLogTools;
import com.redpigmall.manage.admin.tools.RedPigQueryTools;
import com.redpigmall.manage.admin.tools.RedPigShipTools;
import com.redpigmall.manage.admin.tools.RedPigStatTools;
import com.redpigmall.manage.admin.tools.RedPigStoreTools;
import com.redpigmall.manage.admin.tools.RedPigSubjectTools;
import com.redpigmall.manage.delivery.tools.RedPigDeliveryAddressTools;
import com.redpigmall.manage.seller.tools.RedPigCombinTools;
import com.redpigmall.manage.seller.tools.RedPigOrderTools;
import com.redpigmall.manage.seller.tools.RedPigTransportTools;
import com.redpigmall.module.chatting.view.tools.RedPigChattingViewTools;
import com.redpigmall.module.circle.view.tools.RedPigCircleViewTools;
import com.redpigmall.module.weixin.view.tools.Base64Tools;
import com.redpigmall.module.weixin.view.tools.RedPigWeixinChannelFloorTools;
import com.redpigmall.module.weixin.view.tools.RedPigWeixinIndexTools;
import com.redpigmall.module.weixin.view.tools.RedPigWeixinTools;
import com.redpigmall.msg.RedPigMsgTools;
import com.redpigmall.service.*;
import com.redpigmall.view.web.tools.RedPigAlbumViewTools;
import com.redpigmall.view.web.tools.RedPigArticleViewTools;
import com.redpigmall.view.web.tools.RedPigCmsTools;
import com.redpigmall.view.web.tools.RedPigEmojiTools;
import com.redpigmall.view.web.tools.RedPigEvaluateViewTools;
import com.redpigmall.view.web.tools.RedPigGoodsViewTools;
import com.redpigmall.view.web.tools.RedPigIntegralViewTools;
import com.redpigmall.view.web.tools.RedPigQueryUtils;
import com.redpigmall.view.web.tools.RedPigRoleTools;
import com.redpigmall.view.web.tools.RedPigShowClassTools;
import com.redpigmall.view.web.tools.RedPigStoreViewTools;
import com.redpigmall.view.web.tools.RedPigUserMenuTools;

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
	protected RedPigLuckydrawService luckydrawService;

	@Autowired
	protected RedPigLuckydrawRewardService luckydrawRewardService;

	@Autowired
	protected RedPigLuckydrawRecordService luckydrawRecordService;

	@Autowired
	protected RedPigNukeService nukeService;

	@Autowired
	protected RedPigCollageBuyService collageBuyService;
	
	@Autowired
	protected RedPigLimitSellingService limitSellingService;
	
	@Autowired
	protected RedPigChattingService chattingService;
	
	@Autowired
	protected RedPigPredepositLogService predepositlogService;
	@Autowired
	protected RedPigPredepositCashService predepositcashService;
	@Autowired
	protected RedPigIntegralLogService integrallogService;
	@Autowired
	protected RedPigTransAreaService redPigTransAreaService;
	@Autowired
	protected RedPigStoreGradeService storegradeService;
	@Autowired
	protected RedPigVatInvoiceService vatinvoiceService;
	@Autowired
	protected RedPigFTPServerService redPigFTPServerService;
	@Autowired
	protected RedPigSubjectService subjectService;
	
	@Autowired
	protected RedPigSubjectTools SubjectTools;
	
	@Autowired
	protected RedPigStoreAdjustInfoService storeadjustinfoService;
	@Autowired
	protected RedPigMerchantServicesService merchantservicesService;
	
	@Autowired
	protected RedPigGoodsFloorService redPigGoodsfloorService;


	@Autowired
	protected RedPigGoodsFloorTools gf_tools;
	@Autowired
	protected RedPigChannelTools ch_tools;
	
	@Autowired
	protected RedPigChannelFloorService channelfloorService;
	@Autowired
	protected RedPigChannelTools channelTools;
	@Autowired
	protected RedPigGoodsCaseService goodscaseService;
	@Autowired
	protected RedPigArticleService articleService;
	
	@Autowired
	protected RedPigLuceneVoTools luceneVoTools;
	
	@Autowired
	protected RedPigRoleGroupService redPigRoleGroupService;
	
	@Autowired
	protected RedPigRoleTools roleTools;

	@Autowired
	protected RedPigInformationReplyService redPigInformationReplyService;

	@Autowired
	protected RedPigInformationService redPigInformationService;

	@Autowired
	protected RedPigCmsTools cmsTools;

	@Autowired
	protected RedPigInformationClassService redPigInformationClassService;

	@Autowired
	protected RedPigCircleInvitationReplyService invitationReplyService;

	@Autowired
	protected RedPigCircleInvitationService invitationService;

	@Autowired
	protected RedPigCmsIndexTemplateService redPigCmsIndexTemplateService;

	@Autowired
	protected RedPigCircleViewTools redPigCircleViewTools;

	@Autowired
	protected RedPigGoodsFloorTools redPigGf_tools;

	@Autowired
	protected RedPigFreeGoodsService redPigFreeGoodsService;

	@Autowired
	protected RedPigCircleClassService redPigCircleClassService;

	@Autowired
	protected RedPigCircleService redPigCircleService;

	@Autowired
	protected RedPigInformationReplyService redPigInformationreplyService;

	@Autowired
	protected RedPigCircleClassService circleclassService;

	@Autowired
	protected RedPigCircleService circleService;

	@Autowired
	protected RedPigNavigationService navigationService;

	@Autowired
	protected RedPigIntegralViewTools redpigIntegralViewTools;

	@Autowired
	protected RedPigCloudPurchaseLotteryService redPigCloudPurchaseLotteryService;

	@Autowired
	protected RedPigCloudPurchaseClassService redPigCloudPurchaseClassService;

	@Autowired
	protected RedPigCloudPurchaseGoodsService redPigCloudpurchasegoodsService;

	@Autowired
	protected RedPigCloudPurchaseGoodsService redPigCloudpurchaseGoodsService;

	@Autowired
	protected RedPigCloudPurchaseClassService redPigCloudpurchaseclassService;

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
	protected RedPigStoreGradeService redPigStoreGradeService;

	@Autowired
	protected RedPigMessageService redPigMessageService;

	@Autowired
	protected RedPigOrderFormLogService redPigOrderFormLogService;

	@Autowired
	protected RedPigUserGoodsClassService redPigUserGoodsClassService;

	@Autowired
	protected RedPigWaterMarkService redPigWaterMarkService;

	@Autowired
	protected RedPigTransportService redPigTransportService;

	@Autowired
	protected RedPigGoodsFormatService redPigGoodsFormatService;

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
	protected RedPigMerchantServicesService redPigMerchantServicesService;

	@Autowired
	protected RedPigGoodsViewTools redPigGoodsViewTools;

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
	protected RedPigAreaManageTools redPigAreaManageTools;

	@Autowired
	protected RedPigIntegralGoodsService redPigIntegralgoodsService;

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

	@Autowired
	protected RedPigStoreStatService redPigStoreStatService;

	@Autowired
	protected RedPigSystemTipService redPigSystemTipService;

	@Autowired
	protected RedPigComplaintSubjectService complaintsubjectService;
	@Autowired
	protected RedPigReturnGoodsLogService returngoodslogService;
	@Autowired
	protected RedPigExpressCompanyService expressCompayService;
	@Autowired
	protected RedPigPredepositService predepositService;
	@Autowired
	protected RedPigPredepositLogService predepositLogService;
	@Autowired
	protected RedPigReturnGoodsLogService returnGoodsLogService;
	@Autowired
	protected RedPigRefundLogService refundLogService;
	@Autowired
	protected RedPigPayoffLogService payoffLogService;
	@Autowired
	protected RedPigOrderFormTools orderFormTools;
	@Autowired
	protected RedPigGroupInfoService groupinfoService;
	@Autowired
	protected RedPigStoreService storeService;
	@Autowired
	protected RedPigGoodsBrandService redPigGoodsBrandService;
	@Autowired
	protected RedPigGoodsBrandCategoryService redPigGoodsBrandCategoryService;
	@Autowired
	protected RedPigAccessoryService redPigAccessoryService;
	@Autowired
	protected RedPigGoodsService redPigGoodsService;
	@Autowired
	protected RedPigQueryUtils redPigQueryTools;
	@Autowired
	protected RedPigGoodsClassService redPigGoodsClassService;
	@Autowired
	protected RedPigShowClassTools redPigShowClassTools;
	@Autowired
	protected RedPigGoodsTypeService redPigGgoodsTypeService;
	@Autowired
	protected RedPigShowClassService redPigShowClassService;
	@Autowired
	protected RedPigAdvertPositionService redPigAdvertPositionService;
	@Autowired
	protected RedPigGoodsTypeService redPigGoodsTypeService;
	@Autowired
	protected RedPigGoodsBrandService redPigGoodsbrandService;
	@Autowired
	protected RedPigUserService redPigUserService;
	@Autowired
	protected RedPigEvaluateService redPigEvaluateService;
	@Autowired
	protected RedPigFavoriteService redPigFavoriteService;
	@Autowired
	protected RedPigZTCGoldLogService redPigZTCGoldLogService;
	@Autowired
	protected RedPigGoodsTools redPigGoodsTools;
	@Autowired
	protected RedPigTemplateService redPigTemplateService;
	@Autowired
	protected RedPigMsgTools redPigMsgTools;
	@Autowired
	protected RedPigStoreTools shopTools;
	@Autowired
	protected RedPigGoodsTypePropertyService redPigGoodsTypePropertyService;
	@Autowired
	protected RedPigGoodsSpecificationService redPigGoodsSpecificationService;
	@Autowired
	protected RedPigGoodsClassService redPigGoodsclassService;
	@Autowired
	protected RedPigGoodsSpecPropertyService redPigGoodsSpecPropertyService;
	@Autowired
	protected RedPigShowClassService showclassService;
	@Autowired
	protected RedPigGoodsClassService goodsclassService;
	@Autowired
	protected RedPigChannelService channelService;
	@Autowired
	protected RedPigShowClassTools showClassTools;
	@Autowired
	protected RedPigAdvertPositionService advertPositionService;
	@Autowired
	protected RedPigGoodsBrandService goodsbrandService;
	@Autowired
	protected RedPigWeixinChannelService weiXinChannelService;
	@Autowired
	protected RedPigZTCGoldLogService ztcGoldLogService;
	@Autowired
	protected RedPigGoldLogService goldLogService;
	@Autowired
	protected RedPigPayoffLogService payofflogService;
	@Autowired
	protected RedPigPayoffLogTools payofflogTools;
	@Autowired
	protected RedPigActivityService activityService;
	@Autowired
	protected RedPigActivityGoodsService activityGoodsService;
	@Autowired
	protected RedPigGoodsService goodService;
	@Autowired
	protected RedPigAlbumViewTools albumViewTools;
	@Autowired
	protected RedPigWaterMarkService watermarkService;
	@Autowired
	protected RedPigBuyGiftService buygiftService;
	@Autowired
	protected RedPigCombinPlanService combinplanService;
	@Autowired
	protected RedPigCombinTools combinTools;
	@Autowired
	protected RedPigConsultService consultService;
	@Autowired
	protected RedPigCouponService couponService;
	@Autowired
	protected RedPigCouponInfoService couponinfoService;
	@Autowired
	protected RedPigCustomerRelManaService customerRelManaService;
	@Autowired
	protected RedPigEnoughReduceService enoughreduceService;
	@Autowired
	protected RedPigImageTools imageTools;
	@Autowired
	protected RedPigExpressCompanyService expressCompanyService;
	@Autowired
	protected RedPigExpressCompanyCommonService expressCompanyCommonService;
	@Autowired
	protected RedPigFreeGoodsService freegoodsService;
	@Autowired
	protected RedPigFreeClassService freeClassService;
	@Autowired
	protected RedPigFreeTools freeTools;
	@Autowired
	protected RedPigFreeApplyLogService freeapplylogService;
	@Autowired
	protected RedPigSysConfigService configService;
	@Autowired
	protected RedPigGoodsFloorService goodsFloorService;
	
	@Autowired
	protected RedPigUserConfigService userConfigService;
	@Autowired
	protected RedPigGoodsService goodsService;
	@Autowired
	protected RedPigGoodsBrandService goodsBrandService;
	@Autowired
	protected RedPigGoodsClassService goodsClassService;
	@Autowired
	protected RedPigTemplateService templateService;
	@Autowired
	protected RedPigMessageService messageService;
	@Autowired
	protected RedPigMsgTools msgTools;
	@Autowired
	protected RedPigDatabaseTools databaseTools;
	@Autowired
	protected RedPigCartTools cartTools;
	@Autowired
	protected RedPigEvaluateService evaluateService;
	@Autowired
	protected RedPigGoodsCartService goodsCartService;
	@Autowired
	protected RedPigOrderFormService orderFormService;
	@Autowired
	protected RedPigOrderFormLogService orderFormLogService;
	@Autowired
	protected RedPigAccessoryService accessoryService;
	@Autowired
	protected RedPigUserGoodsClassService userGoodsClassService;
	@Autowired
	protected RedPigGoodsSpecPropertyService specPropertyService;
	@Autowired
	protected RedPigGoodsTypePropertyService goodsTypePropertyService;
	@Autowired
	protected RedPigWaterMarkService waterMarkService;
	@Autowired
	protected RedPigAlbumService albumService;
	@Autowired
	protected RedPigTransportService transportService;
	@Autowired
	protected RedPigPaymentService paymentService;
	@Autowired
	protected RedPigTransportTools transportTools;
	@Autowired
	protected RedPigStoreTools storeTools;
	@Autowired
	protected RedPigStoreViewTools storeViewTools;
	@Autowired
	protected RedPigGoodsViewTools goodsViewTools;
	@Autowired
	protected RedPigGoodsSpecificationService goodsSpecificationService;
	@Autowired
	protected RedPigGoodsFormatService goodsFormatService;
	@Autowired
	protected RedPigAreaService areaService;
	@Autowired
	protected RedPigGoodsTools goodsTools;
	@Autowired
	protected RedPigImageTools ImageTools;
	@Autowired
	protected RedPigShipAddressService shipAddressService;
	@Autowired
	protected RedPigGoodsTagService goodsTagService;
	@Autowired
	protected RedPigInventoryService inventoryService;
	@Autowired
	protected RedPigStoreHouseService storeHouseService;
	@Autowired
	protected RedPigInventoryTools InventoryTools;
	@Autowired
	protected RedPigInventoryOperationService inventoryOperationService;
	@Autowired
	protected RedPigGoodsTypePropertyService gtpService;
	@Autowired
	protected RedPigGoodsTypeService goodsTypeService;
	@Autowired
	protected RedPigMerchantServicesService merchantServicesService;
	@Autowired
	protected RedPigQueryTools queryTools;
	@Autowired
	protected RedPigGoodsLogService goodsLogService;
	@Autowired
	protected RedPigGroupAreaInfoService groupareainfoService;
	@Autowired
	protected RedPigGroupAreaService groupareaService;
	@Autowired
	protected RedPigGroupGoodsService groupGoodsService;
	@Autowired
	protected RedPigGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	protected RedPigNukeGoodsService nukeGoodsService;
	@Autowired
	protected RedPigGroupClassService groupclassService;
	@Autowired
	protected RedPigNukeClassService nukeClassService;
	
	@Autowired
	protected RedPigGroupGoodsService groupgoodsService;
	@Autowired
	protected RedPigGroupIndexService groupIndexService;
	@Autowired
	protected RedPigGroupTools groupTools;
	@Autowired
	protected RedPigGroupClassService groupClassService;
	@Autowired
	protected RedPigGroupLifeGoodsService grouplifeGoodsService;
	@Autowired
	protected RedPigGroupPriceRangeService grouppricerangeService;
	@Autowired
	protected RedPigGroupService groupService;
	@Autowired
	protected RedPigGroupAreaService groupAreaService;
	@Autowired
	protected RedPigGoodsClassService GoodsClassService;
	@Autowired
	protected RedPigGroupLifeGoodsService grouplifegoodsService;
	@Autowired
	protected RedPigGroupAreaInfoService groupAreaInfoService;
	@Autowired
	protected RedPigInventoryOperationService inventoryoperationService;
	@Autowired
	protected RedPigExpressInfoService expressInfoService;
	@Autowired
	protected RedPigOrderTools orderTools;
	@Autowired
	protected RedPigShipTools ShipTools;
	@Autowired
	protected RedPigCouponInfoService couponInfoService;
	@Autowired
	protected RedPigHandleOrderFormService handleOrderFormService;
	@Autowired
	protected RedPigRefundApplyFormService refundapplyformService;
	@Autowired
	protected RedPigOrderFormService orderformService;
	@Autowired
	protected RedPigStoreHouseService storehouseService;
	@Autowired
	protected RedPigInventoryTools inventoryTools;
	@Autowired
	protected RedPigTransAreaService transAreaService;
	@Autowired
	protected RedPigAreaManageTools areaManageTools;
	@Autowired
	protected RedPigStorePointService storePointService;
	@Autowired
	protected RedPigFavoriteService favoriteService;
	@Autowired
	protected RedPigComplaintGoodsService complaintGoodsService;
	@Autowired
	protected RedPigPayoffLogService paylogService;
	@Autowired
	protected RedPigGoodsSpecPropertyService specpropertyService;
	@Autowired
	protected RedPigGoldRecordService grService;
	@Autowired
	protected RedPigZTCGoldLogService ztcglService;
	@Autowired
	protected RedPigGoldLogService glService;
	@Autowired
	protected RedPigMerchantServicesService merchantServices;
	@Autowired
	protected RedPigMenuService menuService;
	@Autowired
	protected RedPigAreaService redPigAreaService;
	@Autowired
	protected RedPigExpressCompanyService redPigExpressCompanyService;
	@Autowired
	protected RedPigOrderFormTools redPigOrderFormTools;
	@Autowired
	protected RedPigSysConfigService redPigSysConfigService;
	@Autowired
	protected RedPigUserConfigService redPigUserConfigService;
	@Autowired
	protected RedPigPaymentTools redPigPaymentTools;
	@Autowired
	protected RedPigPaymentService redPigPaymentService;
	@Autowired
	protected RedPigUserLevelService userlevelService;
	@Autowired
	protected RedPigChattingUserService chattingUserService;
	@Autowired
	protected RedPigChattingLogService chattinglogService;
	@Autowired
	protected RedPigChattingViewTools chattingViewTools;
	@Autowired
	protected RedPigChattingConfigService chattingconfigService;
	@Autowired
	protected RedPigChattingViewTools ChattTools;
	@Autowired
	protected RedPigCloudPurchaseLotteryService cloudpurchaselotteryService;
	@Autowired
	protected RedPigCloudPurchaseRecordService cloudPurchaseRecordService;
	@Autowired
	protected RedPigWeixinChannelFloorService weixinchannelfloorService;
	@Autowired
	protected RedPigWeixinChannelFloorTools weixinChannelFloorTools;
	@Autowired
	protected RedPigWeixinTools weixinTools;
	@Autowired
	protected RedPigVMenuService vMenuService;
	@Autowired
	protected Base64Tools base64Tools;
	@Autowired
	protected RedPigEmojiTools emojiTools;
	@Autowired
	protected RedPigFreeGoodsService freeGoodsService;
	@Autowired
	protected RedPigReplyContentService replycontentService;
	@Autowired
	protected RedPigVMenuService vmenuService;
	@Autowired
	protected RedPigPraiseService praiseService;
	@Autowired
	protected RedPigWeixinFloorService weixinfloorService;
	@Autowired
	protected RedPigWeixinIndexTools weixinIndexTools;
	@Autowired
	protected RedPigWeixinChannelService weixinchannelService;
	@Autowired
	protected RedPigWeixinChannelFloorService weixinChannelFloorService;
	@Autowired
	protected RedPigUserService userService;
	@Autowired
	protected RedPigRoleService roleService;
	@Autowired
	protected RedPigRoleGroupService roleGroupService;
	@Autowired
	protected RedPigResService resService;
	@Autowired
	protected RedPigStoreGradeService storeGradeService;
	@Autowired
	protected RedPigAdvertService advertService;
	@Autowired
	protected RedPigUserGoodsClassService ugcService;
	@Autowired
	protected RedPigSysLogService syslogService;
	@Autowired
	protected RedPigGoodsSpecificationService specService;
	@Autowired
	protected RedPigGoldLogService goldlogService;
	@Autowired
	protected RedPigStorePointService storepointService;
	@Autowired
	protected RedPigPredepositCashService redepositcashService;
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
	protected RedPigStatTools statTools;
	@Autowired
	protected RedPigUserMenuTools menuTools;
	@Autowired
	protected RedPigSysLogService redPigSysLogService;
	@Autowired
	protected RedPigAppVersionService redPigVersionService;
	@Autowired
	protected RedPigAppOperativeVersionService redPigOperativeVersionService;
	@Autowired
	protected RedPigDistributionGradeService redPigDistributionGradeService;
	@Autowired
	protected RedPigDistributionAgreementService redPigDistributionAgreementService;
	@Autowired
	protected RedPigDistributionCommissionService redPigDistributionCommissionService;

	@Autowired
	protected RedPigAuthenticationService redPigAuthenticationService;


}
