package com.redpigmall.module.weixin.manage.base;

import org.springframework.beans.factory.annotation.Autowired;

import com.redpigmall.logic.service.RedPigHandleOrderFormService;
import com.redpigmall.lucene.tools.RedPigLuceneVoTools;
import com.redpigmall.manage.admin.tools.RedPigCartTools;
import com.redpigmall.manage.admin.tools.RedPigFreeTools;
import com.redpigmall.manage.admin.tools.RedPigGoodsTools;
import com.redpigmall.manage.admin.tools.RedPigImageTools;
import com.redpigmall.manage.admin.tools.RedPigOrderFormTools;
import com.redpigmall.manage.admin.tools.RedPigPaymentTools;
import com.redpigmall.manage.admin.tools.RedPigStoreTools;
import com.redpigmall.manage.admin.tools.RedPigUserTools;
import com.redpigmall.manage.delivery.tools.RedPigDeliveryAddressTools;
import com.redpigmall.manage.seller.tools.RedPigCombinTools;
import com.redpigmall.manage.seller.tools.RedPigTransportTools;
import com.redpigmall.module.weixin.view.tools.Base64Tools;
import com.redpigmall.module.weixin.view.tools.RedPigWeixinCartTools;
import com.redpigmall.module.weixin.view.tools.RedPigWeixinChannelFloorTools;
import com.redpigmall.module.weixin.view.tools.RedPigWeixinIndexTools;
import com.redpigmall.module.weixin.view.tools.RedPigWeixinTools;
import com.redpigmall.msg.RedPigMsgTools;
import com.redpigmall.service.RedPigAccessoryService;
import com.redpigmall.service.RedPigActivityGoodsService;
import com.redpigmall.service.RedPigAddressService;
import com.redpigmall.service.RedPigAdvertPositionService;
import com.redpigmall.service.RedPigAlbumService;
import com.redpigmall.service.RedPigAreaService;
import com.redpigmall.service.RedPigBuyGiftService;
import com.redpigmall.service.RedPigCloudPurchaseOrderService;
import com.redpigmall.service.RedPigCombinPlanService;
import com.redpigmall.service.RedPigCouponInfoService;
import com.redpigmall.service.RedPigCouponService;
import com.redpigmall.service.RedPigDeliveryAddressService;
import com.redpigmall.service.RedPigEnoughReduceService;
import com.redpigmall.service.RedPigFreeApplyLogService;
import com.redpigmall.service.RedPigFreeGoodsService;
import com.redpigmall.service.RedPigGoodsBrandService;
import com.redpigmall.service.RedPigGoodsCartService;
import com.redpigmall.service.RedPigGoodsClassService;
import com.redpigmall.service.RedPigGoodsLogService;
import com.redpigmall.service.RedPigGoodsService;
import com.redpigmall.service.RedPigGoodsSpecPropertyService;
import com.redpigmall.service.RedPigGroupGoodsService;
import com.redpigmall.service.RedPigGroupInfoService;
import com.redpigmall.service.RedPigGroupLifeGoodsService;
import com.redpigmall.service.RedPigGroupService;
import com.redpigmall.service.RedPigIntegralGoodsOrderService;
import com.redpigmall.service.RedPigMessageService;
import com.redpigmall.service.RedPigOrderFormLogService;
import com.redpigmall.service.RedPigOrderFormService;
import com.redpigmall.service.RedPigPaymentService;
import com.redpigmall.service.RedPigPayoffLogService;
import com.redpigmall.service.RedPigPraiseService;
import com.redpigmall.service.RedPigPredepositLogService;
import com.redpigmall.service.RedPigReplyContentService;
import com.redpigmall.service.RedPigRoleService;
import com.redpigmall.service.RedPigStoreHouseService;
import com.redpigmall.service.RedPigStoreService;
import com.redpigmall.service.RedPigSysConfigService;
import com.redpigmall.service.RedPigTemplateService;
import com.redpigmall.service.RedPigUserConfigService;
import com.redpigmall.service.RedPigUserService;
import com.redpigmall.service.RedPigVMenuService;
import com.redpigmall.service.RedPigVatInvoiceService;
import com.redpigmall.service.RedPigVerifyCodeService;
import com.redpigmall.service.RedPigWeixinChannelFloorService;
import com.redpigmall.service.RedPigWeixinChannelService;
import com.redpigmall.service.RedPigWeixinFloorService;
import com.redpigmall.pay.tools.RedPigPayTools;
import com.redpigmall.view.web.tools.RedPigActivityViewTools;
import com.redpigmall.view.web.tools.RedPigAreaViewTools;
import com.redpigmall.view.web.tools.RedPigCouponViewTools;
import com.redpigmall.view.web.tools.RedPigEmojiTools;
import com.redpigmall.view.web.tools.RedPigGoodsViewTools;
import com.redpigmall.view.web.tools.RedPigGroupViewTools;
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
	protected RedPigFreeApplyLogService applyLogService;
	
	@Autowired
	protected RedPigFreeTools freeTools;
	
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
	protected RedPigGoodsService redPigGoodsService;
	@Autowired
	protected RedPigEmojiTools emojiTools;
	@Autowired
	protected RedPigImageTools imageTools;
	@Autowired
	protected RedPigGoodsClassService goodsClassService;
	@Autowired
	protected RedPigGroupService groupService;
	@Autowired
	protected RedPigGroupGoodsService groupgoodsService;
	@Autowired
	protected RedPigActivityGoodsService activityGoodsService;
	@Autowired
	protected RedPigGoodsBrandService goodsBrandService;
	@Autowired
	protected RedPigFreeGoodsService freeGoodsService;
	@Autowired
	protected RedPigRoleService roleService;
	@Autowired
	protected RedPigReplyContentService replycontentService;
	@Autowired
	protected RedPigGroupLifeGoodsService grouplifeGoodsService;
	
	@Autowired
	protected RedPigVMenuService vmenuService;
	
	@Autowired
	protected RedPigPraiseService praiseService;
	
	@Autowired
	protected RedPigWeixinFloorService weixinfloorService;
	@Autowired
	protected RedPigStoreTools storeTools;
	@Autowired
	protected RedPigAlbumService albumService;
	@Autowired
	protected RedPigWeixinIndexTools weixinIndexTools;
	
	@Autowired
	protected RedPigWeixinChannelService weixinchannelService;
	@Autowired
	protected RedPigWeixinChannelFloorService weixinChannelFloorService;
	@Autowired
	protected RedPigAdvertPositionService advertPositionService;
	@Autowired
	protected RedPigWeixinChannelFloorTools weixinChannelFloorTools;

	@Autowired
	protected RedPigGoodsService goodsService;

	@Autowired
	protected RedPigGoodsSpecPropertyService goodsSpecPropertyService;

	@Autowired
	protected RedPigAddressService addressService;

	@Autowired
	protected RedPigAreaService areaService;

	@Autowired
	protected RedPigPaymentService paymentService;

	@Autowired
	protected RedPigOrderFormService orderFormService;

	@Autowired
	protected RedPigGoodsCartService goodsCartService;

	@Autowired
	protected RedPigStoreService storeService;

	@Autowired
	protected RedPigOrderFormLogService orderFormLogService;

	@Autowired
	protected RedPigUserService userService;

	@Autowired
	protected RedPigTemplateService templateService;

	@Autowired
	protected RedPigPredepositLogService predepositLogService;

	@Autowired
	protected RedPigGroupGoodsService groupGoodsService;

	@Autowired
	protected RedPigCouponInfoService couponInfoService;

	@Autowired
	protected RedPigPaymentTools paymentTools;

	@Autowired
	protected RedPigPayTools payTools;

	@Autowired
	protected RedPigTransportTools transportTools;

	@Autowired
	protected RedPigGoodsViewTools goodsViewTools;

	@Autowired
	protected RedPigStoreViewTools storeViewTools;

	@Autowired
	protected RedPigOrderFormTools orderFormTools;

	@Autowired
	protected RedPigCartTools cartTools;

	@Autowired
	protected RedPigGroupLifeGoodsService groupLifeGoodsService;

	@Autowired
	protected RedPigGroupInfoService groupInfoService;

	@Autowired
	protected RedPigMessageService messageService;

	@Autowired
	protected RedPigGroupViewTools groupViewTools;

	@Autowired
	protected RedPigLuceneVoTools luceneVoTools;

	@Autowired
	protected RedPigUserTools userTools;

	@Autowired
	protected RedPigPayoffLogService payoffLogService;

	@Autowired
	protected RedPigIntegralGoodsOrderService igorderService;

	@Autowired
	protected RedPigEnoughReduceService enoughReduceService;

	@Autowired
	protected RedPigBuyGiftService buyGiftService;

	@Autowired
	protected RedPigCombinPlanService combinplanService;

	@Autowired
	protected RedPigCombinTools combinTools;

	@Autowired
	protected RedPigIntegralViewTools integralViewTools;

	@Autowired
	protected RedPigActivityGoodsService actgoodsService;

	@Autowired
	protected RedPigActivityViewTools activityTools;

	@Autowired
	protected RedPigDeliveryAddressService deliveryaddrService;

	@Autowired
	protected RedPigGoodsLogService goodsLogService;

	@Autowired
	protected RedPigDeliveryAddressTools DeliveryAddressTools;

	@Autowired
	protected RedPigHandleOrderFormService HandleOrderFormService;

	@Autowired
	protected RedPigAreaViewTools areaViewTools;

	@Autowired
	protected RedPigVerifyCodeService verifyCodeService;

	@Autowired
	protected RedPigCouponService couponService;

	@Autowired
	protected RedPigCouponViewTools couponViewTools;

	@Autowired
	protected RedPigWeixinCartTools weixinCartTools;

	@Autowired
	protected RedPigMsgTools msgTools;

	@Autowired
	protected RedPigVerifyCodeService mobileverifycodeService;

	@Autowired
	protected RedPigVatInvoiceService vatinvoiceService;

	@Autowired
	protected RedPigGoodsTools goodsTools;

	@Autowired
	protected RedPigStoreHouseService storeHouseService;

	@Autowired
	protected RedPigCloudPurchaseOrderService cloudPurchaseOrderService;

	@Autowired
	protected RedPigSysConfigService configService;

	@Autowired
	protected RedPigUserConfigService userConfigService;
	
}
