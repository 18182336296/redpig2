package com.redpigmall.manage.seller.action.base;

import org.springframework.beans.factory.annotation.Autowired;

import com.redpigmall.api.tools.database.RedPigDatabaseTools;
import com.redpigmall.logic.service.RedPigHandleOrderFormService;
import com.redpigmall.manage.admin.tools.RedPigFreeTools;
import com.redpigmall.manage.admin.tools.RedPigGoodsTools;
import com.redpigmall.manage.admin.tools.RedPigImageTools;
import com.redpigmall.manage.admin.tools.RedPigOrderFormTools;
import com.redpigmall.manage.admin.tools.RedPigPayoffLogTools;
import com.redpigmall.manage.admin.tools.RedPigShipTools;
import com.redpigmall.manage.admin.tools.RedPigStoreTools;
import com.redpigmall.manage.admin.tools.RedPigUserTools;
import com.redpigmall.manage.delivery.tools.RedPigDeliveryAddressTools;
import com.redpigmall.manage.seller.tools.RedPigCombinTools;
import com.redpigmall.manage.seller.tools.RedPigOrderTools;
import com.redpigmall.manage.seller.tools.RedPigTransportTools;
import com.redpigmall.module.chatting.view.tools.RedPigChattingViewTools;
import com.redpigmall.module.weixin.view.tools.RedPigWeixinStoreViewTools;
import com.redpigmall.msg.RedPigMsgTools;
import com.redpigmall.service.*;
import com.redpigmall.pay.tools.RedPigPayTools;
import com.redpigmall.view.web.tools.RedPigActivityViewTools;
import com.redpigmall.view.web.tools.RedPigAlbumViewTools;
import com.redpigmall.view.web.tools.RedPigAreaViewTools;
import com.redpigmall.view.web.tools.RedPigArticleViewTools;
import com.redpigmall.view.web.tools.RedPigCmsTools;
import com.redpigmall.view.web.tools.RedPigEvaluateViewTools;
import com.redpigmall.view.web.tools.RedPigGoodsViewTools;
import com.redpigmall.view.web.tools.RedPigIntegralViewTools;
import com.redpigmall.view.web.tools.RedPigOrderViewTools;
import com.redpigmall.view.web.tools.RedPigQueryUtils;
import com.redpigmall.view.web.tools.RedPigRoleTools;
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
	protected RedPigActivityService activityService;

	@Autowired
	protected RedPigActivityGoodsService activityGoodsService;

	@Autowired
	protected RedPigGoodsService goodsService;

	@Autowired
	protected RedPigQueryUtils queryTools;

	@Autowired
	protected RedPigActivityViewTools activityViewTools;

	@Autowired
	protected RedPigAdvertService advertService;

	@Autowired
	protected RedPigAdvertPositionService advertPositionService;

	@Autowired
	protected RedPigAccessoryService accessoryService;

	@Autowired
	protected RedPigGoldLogService goldLogService;

	@Autowired
	protected RedPigAlbumService albumService;

	@Autowired
	protected RedPigWaterMarkService waterMarkService;

	@Autowired
	protected RedPigGoodsService goodsSerivce;

	@Autowired
	protected RedPigAlbumViewTools albumViewTools;

	@Autowired
	protected RedPigStoreTools storeTools;

	@Autowired
	protected RedPigStoreViewTools storeViewTools;

	@Autowired
	protected RedPigImageTools ImageTools;

	@Autowired
	protected RedPigBuyGiftService buygiftService;

	@Autowired
	protected RedPigGoodsClassService goodsClassService;

	@Autowired
	protected RedPigStoreService storeService;

	@Autowired
	protected RedPigStoreTools StoreTools;

	@Autowired
	protected RedPigSalesLogService salesLogService;

	@Autowired
	protected RedPigOrderFormTools orderFormTools;

	@Autowired
	protected RedPigSalesLogService combinlogService;

	@Autowired
	protected RedPigSalesLogService saleslogService;

	@Autowired
	protected RedPigCombinPlanService combinplanService;

	@Autowired
	protected RedPigCombinTools combinTools;

	@Autowired
	protected RedPigGoodsTools goodsTools;

	@Autowired
	protected RedPigComplaintService complaintService;

	@Autowired
	protected RedPigComplaintSubjectService complaintSubjectService;

	@Autowired
	protected RedPigConsultService consultService;

	@Autowired
	protected RedPigMsgTools msgTools;

	@Autowired
	protected RedPigGoodsViewTools GoodsViewTools;

	@Autowired
	protected RedPigCouponService couponService;

	@Autowired
	protected RedPigCouponInfoService couponinfoService;

	@Autowired
	protected RedPigEnoughReduceService enoughreduceService;

	@Autowired
	protected RedPigGoodsViewTools goodsViewTools;

	@Autowired
	protected RedPigEvaluateService evaluateService;

	@Autowired
	protected RedPigImageTools imageTools;

	@Autowired
	protected RedPigExpressCompanyService expressCompanyService;

	@Autowired
	protected RedPigExpressCompanyCommonService expressCompanyCommonService;

	@Autowired
	protected RedPigTransportTools transportTools;

	@Autowired
	protected RedPigFreeGoodsService freegoodsService;

	@Autowired
	protected RedPigFreeClassService freeClassService;

	@Autowired
	protected RedPigFreeTools freeTools;

	@Autowired
	protected RedPigFreeApplyLogService freeapplylogService;

	@Autowired
	protected RedPigExpressCompanyService expressCompayService;

	@Autowired
	protected RedPigShipAddressService shipAddressService;

	@Autowired
	protected RedPigAreaService areaService;

	@Autowired
	protected RedPigMessageService messageService;

	@Autowired
	protected RedPigPaymentService paymentService;

	@Autowired
	protected RedPigGoldRecordService goldRecordService;

	@Autowired
	protected RedPigPredepositLogService predepositLogService;

	@Autowired
	protected RedPigPayTools payTools;

	@Autowired
	protected RedPigGoodsBrandService goodsBrandService;

	@Autowired
	protected RedPigGoodsBrandCategoryService goodsBrandCategoryService;

	@Autowired
	protected RedPigUserGoodsClassService usergoodsclassService;

	@Autowired
	protected RedPigGoodsFormatService goodsFormatService;

	@Autowired
	protected RedPigUserGoodsClassService userGoodsClassService;

	@Autowired
	protected RedPigGoodsSpecPropertyService specPropertyService;

	@Autowired
	protected RedPigGoodsTypePropertyService goodsTypePropertyService;

	@Autowired
	protected RedPigGoodsCartService goodsCartService;

	@Autowired
	protected RedPigOrderFormLogService orderFormLogService;

	@Autowired
	protected RedPigTransportService transportService;

	@Autowired
	protected RedPigDatabaseTools databaseTools;

	@Autowired
	protected RedPigGoodsSpecificationService goodsSpecificationService;

	@Autowired
	protected RedPigComplaintGoodsService complaintGoodsService;

	@Autowired
	protected RedPigGroupGoodsService groupGoodsService;

	@Autowired
	protected RedPigZTCGoldLogService iztcGoldLogService;

	@Autowired
	protected RedPigGoodsCartService cartService;

	@Autowired
	protected RedPigGoodsTypePropertyService gtpService;

	@Autowired
	protected RedPigGoodsTypeService goodsTypeService;

	@Autowired
	protected RedPigGoodsSpecificationService goodsSpecService;

	@Autowired
	protected RedPigGoodsSpecPropertyService goodsSpecPropertyService;

	@Autowired
	protected RedPigStoreTools shopTools;

	@Autowired
	protected RedPigGoodsClassService goodsclassService;

	@Autowired
	protected RedPigGroupAreaInfoService groupareainfoService;

	@Autowired
	protected RedPigGroupService groupService;

	@Autowired
	protected RedPigGroupAreaService groupAreaService;

	@Autowired
	protected RedPigGroupClassService groupClassService;

	@Autowired
	protected RedPigGroupLifeGoodsService groupLifeGoodsService;

	@Autowired
	protected RedPigGroupInfoService groupInfoService;

	@Autowired
	protected RedPigGroupAreaInfoService groupAreaInfoService;

	@Autowired
	protected RedPigGoodsClassService GoodsClassService;

	@Autowired
	protected RedPigInformationService informationService;

	@Autowired
	protected RedPigInformationClassService informationClassService;

	@Autowired
	protected RedPigCmsTools cmsTools;

	@Autowired
	protected RedPigExpressInfoService expressInfoService;

	@Autowired
	protected RedPigOrderTools orderTools;

	@Autowired
	protected RedPigShipTools ShipTools;

	@Autowired
	protected RedPigCouponInfoService couponInfoService;

	@Autowired
	protected RedPigHandleOrderFormService handelOrderFormService;

	@Autowired
	protected RedPigPayoffLogService payoffLogService;

	@Autowired
	protected RedPigPayoffLogTools payofflogTools;

	@Autowired
	protected RedPigOrderFormService orderFormServer;

	@Autowired
	protected RedPigOrderFormService orderformService;

	@Autowired
	protected RedPigRefundApplyFormService refundapplyformService;

	@Autowired
	protected RedPigReturnGoodsLogService returngoodslogService;

	@Autowired
	protected RedPigCustomerRelManaService customerRelManaService;

	@Autowired
	protected RedPigSmsGoldLogService SmsGoldLogService;

	@Autowired
	protected RedPigTemplateService templateService;

	@Autowired
	protected RedPigStoreGradeService storeGradeService;

	@Autowired
	protected RedPigStoreAdjustInfoService adjustInfoService;

	@Autowired
	protected RedPigStoreNavigationService storenavigationService;

	@Autowired
	protected RedPigStoreSlideService storeSlideService;

	@Autowired
	protected RedPigUserTools userTools;

	@Autowired
	protected RedPigArticleService articleService;

	@Autowired
	protected RedPigAreaViewTools areaViewTools;

	@Autowired
	protected RedPigSysConfigService configService;

	@Autowired
	protected RedPigUserConfigService userConfigService;

	@Autowired
	protected RedPigGoodsLogService goodsLogService;

	@Autowired
	protected RedPigUserService userService;

	@Autowired
	protected RedPigOrderFormService orderFormService;

	@Autowired
	protected RedPigFavoriteService favoriteService;

	@Autowired
	protected RedPigRoleGroupService roleGroupService;

	@Autowired
	protected RedPigRoleService roleService;

	@Autowired
	protected RedPigMenuService menuService;

	@Autowired
	protected RedPigUserMenuTools menuTools;

	@Autowired
	protected RedPigTransAreaService transAreaService;

	@Autowired
	protected RedPigWeixinStoreViewTools weixinstoreViewTools;

	@Autowired
	protected RedPigWaterMarkService watermarkService;

	@Autowired
	protected RedPigOrderViewTools orderViewTools;

	@Autowired
	protected RedPigArticleClassService articleClassService;

	@Autowired
	protected RedPigArticleViewTools article_Tools;

	@Autowired
	protected RedPigChattingLogService chattinglogService;

	@Autowired
	protected RedPigChattingViewTools ChattingViewTools;

	@Autowired
	protected RedPigSysConfigService redPigSysConfigService;

	@Autowired
	protected RedPigUserConfigService redPigUserConfigService;

	@Autowired
	protected RedPigRoleTools roleTools;

	@Autowired
	protected RedPigUserMenuTools userMenuTools;
	
}
