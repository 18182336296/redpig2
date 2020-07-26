package com.redpigmall.module.weixin.view.action.base;

import com.redpigmall.service.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.redpigmall.logic.service.RedPigHandleOrderFormService;
import com.redpigmall.lucene.tools.RedPigLuceneVoTools;
import com.redpigmall.manage.admin.tools.RedPigFreeTools;
import com.redpigmall.manage.admin.tools.RedPigGoodsTools;
import com.redpigmall.manage.admin.tools.RedPigImageTools;
import com.redpigmall.manage.admin.tools.RedPigOrderFormTools;
import com.redpigmall.manage.admin.tools.RedPigPaymentTools;
import com.redpigmall.manage.admin.tools.RedPigQueryTools;
import com.redpigmall.manage.admin.tools.RedPigShipTools;
import com.redpigmall.manage.admin.tools.RedPigStoreTools;
import com.redpigmall.manage.admin.tools.RedPigUserTools;
import com.redpigmall.manage.seller.tools.RedPigTransportTools;
import com.redpigmall.module.weixin.view.tools.Base64Tools;
import com.redpigmall.module.weixin.view.tools.RedPigWeixinChannelFloorTools;
import com.redpigmall.module.weixin.view.tools.RedPigWeixinFootPointTools;
import com.redpigmall.module.weixin.view.tools.RedPigWeixinIndexTools;
import com.redpigmall.module.weixin.view.tools.RedPigWeixinStoreViewTools;
import com.redpigmall.module.weixin.view.tools.RedPigWeixinTools;
import com.redpigmall.msg.RedPigMsgTools;
import com.redpigmall.pay.tools.RedPigPayTools;
import com.redpigmall.view.web.tools.RedPigActivityViewTools;
import com.redpigmall.view.web.tools.RedPigAreaViewTools;
import com.redpigmall.view.web.tools.RedPigConsultViewTools;
import com.redpigmall.view.web.tools.RedPigCouponViewTools;
import com.redpigmall.view.web.tools.RedPigEmojiTools;
import com.redpigmall.view.web.tools.RedPigEvaluateViewTools;
import com.redpigmall.view.web.tools.RedPigGoodsClassViewTools;
import com.redpigmall.view.web.tools.RedPigGoodsViewTools;
import com.redpigmall.view.web.tools.RedPigGroupViewTools;
import com.redpigmall.view.web.tools.RedPigIntegralViewTools;
import com.redpigmall.view.web.tools.RedPigNavViewTools;
import com.redpigmall.view.web.tools.RedPigQueryUtils;
import com.redpigmall.view.web.tools.RedPigShowClassTools;
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
	protected RedPigExpressInfoService expressInfoService;

	@Autowired
	protected RedPigRefundApplyFormService refundApplyFormService;

	@Autowired
	protected RedPigLuceneVoTools luceneVoTools;
	@Autowired
	protected RedPigStorePointService storePointService;

	@Autowired
	protected RedPigExpressCompanyService expressCompayService;
	@Autowired
	protected RedPigReturnGoodsLogService returnGoodsLogService;
	@Autowired
	protected RedPigPayoffLogService payoffLogservice;
	@Autowired
	protected RedPigGroupGoodsService ggService;
	@Autowired
	protected RedPigShipTools ShipTools;
	@Autowired
	protected RedPigGroupInfoService groupinfoService;
	@Autowired
	protected RedPigWeixinFootPointTools footPointTools;

	@Autowired
	protected RedPigComplaintService complaintService;
	@Autowired
	protected RedPigComplaintSubjectService complaintSubjectService;
	@Autowired
	protected RedPigWeixinFootPointTools weixinFootPointTools;

	@Autowired
	protected RedPigOrderFormService orderformService;
	@Autowired
	protected RedPigFootPointService footPointService;

	@Autowired
	protected RedPigVerifyCodeService mobileverifycodeService;
	@Autowired
	protected RedPigWeixinStoreViewTools weixinStoreViewTools;
	@Autowired
	protected RedPigQueryUtils redPigQueryTools;
	@Autowired
	protected RedPigAccessoryService accessoryService;
	@Autowired
	protected RedPigWeixinTools weixinTools;
	@Autowired
	protected RedPigVMenuService vMenuService;
	@Autowired
	protected Base64Tools base64Tools;
	@Autowired
	protected RedPigEmojiTools emojiTools;
	@Autowired
	protected RedPigGroupGoodsService groupgoodsService;
	@Autowired
	protected RedPigGroupLifeGoodsService grouplifeGoodsService;

	@Autowired
	protected RedPigVMenuService vmenuService;

	@Autowired
	protected RedPigStoreTools storeTools;
	@Autowired
	protected RedPigWeixinChannelService weixinchannelService;
	@Autowired
	protected RedPigActivityService activityService;

	@Autowired
	protected RedPigActivityGoodsService activityGoodsService;

	@Autowired
	protected RedPigBuyGiftService buyGiftService;

	@Autowired
	protected RedPigEnoughReduceService enoughReduceService;

	@Autowired
	protected RedPigGoodsBrandService goodsBrandService;

	@Autowired
	protected RedPigWeixinChannelService weixinChannelService;

	@Autowired
	protected RedPigWeixinChannelFloorService weixinChannelFloorService;

	@Autowired
	protected RedPigWeixinChannelFloorTools weixinChannelFloorTools;

	@Autowired
	protected RedPigShowClassService showClassService;

	@Autowired
	protected RedPigAdvertService advertService;

	@Autowired
	protected RedPigAreaViewTools areaViewTools;

	@Autowired
	protected RedPigQueryTools QueryTools;

	@Autowired
	protected RedPigShowClassTools showClassTools;

	@Autowired
	protected RedPigImageTools imageTools;

	@Autowired
	protected RedPigGoodsSpecPropertyService goodsSpecPropertyService;

	@Autowired
	protected RedPigGoodsTypePropertyService goodsTypePropertyService;

	@Autowired
	protected RedPigGoodsBrandService brandService;

	@Autowired
	protected RedPigIntegralViewTools integralViewTools;

	@Autowired
	protected RedPigCouponService couponService;

	@Autowired
	protected RedPigCouponInfoService couponInfoService;

	@Autowired
	protected RedPigCouponViewTools couponViewTools;

	@Autowired
	protected RedPigFreeGoodsService freegoodsService;

	@Autowired
	protected RedPigFreeClassService freeClassService;

	@Autowired
	protected RedPigFreeTools freeTools;

	@Autowired
	protected RedPigFreeApplyLogService freeapplylogService;

	@Autowired
	protected RedPigAddressService addressService;

	@Autowired
	protected RedPigEvaluateService evaluateService;

	@Autowired
	protected RedPigConsultService consultService;

	@Autowired
	protected RedPigTransportTools transportTools;

	@Autowired
	protected RedPigConsultViewTools consultViewTools;

	@Autowired
	protected RedPigEvaluateViewTools evaluateViewTools;

	@Autowired
	protected RedPigActivityViewTools activityViewTools;

	@Autowired
	protected RedPigCombinPlanService combinplanService;

	@Autowired
	protected RedPigFavoriteService favoriteService;

	@Autowired
	protected RedPigFreeApplyLogService freeApplyLogService;

	@Autowired
	protected RedPigFreeGoodsService freeGoodsService;

	@Autowired
	protected RedPigStoreHouseService storeHouseService;

	@Autowired
	protected RedPigInventoryService inventoryService;

	@Autowired
	protected RedPigGoodsTools goodsTools;

	@Autowired
	protected RedPigMerchantServicesService merchantServicesService;

	@Autowired
	protected RedPigActivityGoodsService actgoodsService;

	@Autowired
	protected RedPigGroupService groupService;

	@Autowired
	protected RedPigNukeGoodsService nukeGoodsService;

	@Autowired
	protected RedPigNukeService nukeService;

	@Autowired
	protected RedPigCollageBuyService collageBuyService;

	@Autowired
	protected RedPigCollageListService collageListService;

	@Autowired
	protected RedPigGroupAreaService groupAreaService;

	@Autowired
	protected RedPigGroupPriceRangeService groupPriceRangeService;

	@Autowired
	protected RedPigGroupClassService groupClassService;

	@Autowired
	protected RedPigGroupViewTools groupViewTools;

	@Autowired
	protected RedPigNavViewTools navTools;

	@Autowired
	protected RedPigStoreViewTools storeViewTools;

	@Autowired
	protected RedPigPaymentTools paymentTools;

	@Autowired
	protected RedPigGoodsClassViewTools gcViewTools;

	@Autowired
	protected RedPigGroupIndexService groupindexService;

	@Autowired
	protected RedPigGoodsClassService goodsClassService;

	@Autowired
	protected RedPigGoodsCartService goodsCartService;

	@Autowired
	protected RedPigUserTools userTools;

	@Autowired
	protected RedPigWeixinIndexTools weixinIndexTools;

	@Autowired
	protected RedPigWeixinFloorService weixinfloorService;

	@Autowired
	protected RedPigAdvertPositionService advertPositionService;

	@Autowired
	protected RedPigIntegralGoodsCartService integralGoodsCartService;

	@Autowired
	protected RedPigPayTools payTools;

	@Autowired
	protected RedPigRoleService roleService;

	@Autowired
	protected RedPigIntegralLogService integralLogService;

	@Autowired
	protected RedPigAlbumService albumService;

	@Autowired
	protected RedPigDocumentService documentService;

	@Autowired
	protected RedPigVerifyCodeService verifyCodeService;

	@Autowired
	protected RedPigAreaService areaService;

	@Autowired
	protected RedPigOrderFormService orderFormService;

	@Autowired
	protected RedPigOrderFormLogService orderFormLogService;

	@Autowired
	protected RedPigPredepositService predepositService;

	@Autowired
	protected RedPigPredepositLogService predepositLogService;

	@Autowired
	protected RedPigGoldRecordService goldRecordService;

	@Autowired
	protected RedPigGoldLogService goldLogService;

	@Autowired
	protected RedPigUserService userService;

	@Autowired
	protected RedPigPaymentService paymentService;

	@Autowired
	protected RedPigIntegralGoodsOrderService integralGoodsOrderService;

	@Autowired
	protected RedPigIntegralGoodsService integralGoodsService;

	@Autowired
	protected RedPigGroupGoodsService groupGoodsService;

	@Autowired
	protected RedPigGoodsService goodsService;
	
	@Autowired
	protected RedPigComplaintGoodsService complaintGoodsService;
	
	@Autowired
	protected RedPigTemplateService templateService;

	@Autowired
	protected RedPigMsgTools msgTools;

	@Autowired
	protected RedPigOrderFormTools orderFormTools;

	@Autowired
	protected RedPigStoreService storeService;

	@Autowired
	protected RedPigGroupLifeGoodsService groupLifeGoodsService;

	@Autowired
	protected RedPigGroupInfoService groupInfoService;

	@Autowired
	protected RedPigMessageService messageService;

	@Autowired
	protected RedPigPayoffLogService payoffservice;

	@Autowired
	protected RedPigGoodsLogService goodsLogService;

	@Autowired
	protected RedPigGoodsViewTools goodsViewTools;

	@Autowired
	protected RedPigHandleOrderFormService handelOrderFormService;

	@Autowired
	protected RedPigCloudPurchaseOrderService cloudPurchaseOrderService;

	@Autowired
	protected RedPigSysConfigService configService;

	@Autowired
	protected RedPigUserConfigService userConfigService;

	@Autowired
	protected RedPigReplyContentService replycontentService;

	@Autowired
	protected RedPigPraiseService praiseService;

	@Autowired
	protected RedPigUserGoodsClassService userGoodsClassService;

	@Autowired
	protected RedPigWeixinStoreViewTools weixinstoreViewTools;
	
	@Autowired
	protected RedPigAppOperativeVersionService appOperativeVersionService;
	
	@Autowired
	protected RedPigFTPServerService ftpServerService;
	
	

}
