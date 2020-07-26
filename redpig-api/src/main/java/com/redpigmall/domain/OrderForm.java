package com.redpigmall.domain;

import com.redpigmall.api.annotation.Lock;
import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * Title: OrderForm.java
 * </p>
 * 
 * <p>
 * Description: 系统订单管理类，包括购物订单、手机充值订单、团购订单等等，该类对应的数据表是商城系统中最大也是最重要的数据库。
 * 系統接入殴飞充值，充值按照以下流程完成：1、运营商向殴飞申请开通接口并在b2b2c系统中配置对应的用户名 、密码；2、
 * 运营商在殴飞平台充值一定款项；3、运营商配置充值金额，比如充值100元，殴飞收取98.3，运营商配置为98.5，一单赚2毛；4、用户从平台充值，
 * 首先查询殴飞账户余额，余额足够则跳转到付款界面，向运营商账户付款(目前只支持预存款付款)，付款成功后调用殴飞接口完成充值；
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
 * @date 2014-4-25
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "orderform")
public class OrderForm extends IdEntity implements Serializable{
	private Long receiver_addr_id;//收货地址
	
	@Column(columnDefinition = "LongText")
	private String special_invoice;//发票
	
	private Long ship_storehouse_id;//发货仓库
	private String ship_storehouse_name;//仓库名称
	@Column(columnDefinition = "LongText")
	private String order_sign;//订单标签
	private String order_special;//订单指定
	
	private String trade_no;// 交易流水号,在线支付时每次随机生成唯一的号码，重复提交时替换当前订单的交易流水号
	private String order_id;// 订单号
	@Column(columnDefinition = "int default 0")
	private Integer order_main;// 是否为主订单，1为主订单，主订单用在买家用户中心显示订单内容
	@Column(columnDefinition = "LongText")
	private String child_order_detail;// 子订单详情，如果该订单为主订单，则记录
	private String out_order_id;// 外部单号
	private String order_type;// 订单类型，分为web:PC网页订单，weixin:手机网页订单,android:android手机客户端订单，ios:iOS手机客户端订单
	@Column(columnDefinition = "int default 0")
	private Integer order_cat;// 订单分类，0为购物订单，1为手机充值订单 2为生活类团购订单,3为秒杀订单,4为拼团订单
	/**
	 * 使用json管理"
	 * 	[{  "goods_id":1,
	 * 		"goods_name":"佐丹奴男装翻领T恤 条纹修身商务男POLO纯棉短袖POLO衫",
	 *      "goods_type":"group",
	 *      "goods_choice_type":1,
	 *      "goods_cat":0,
	 *      "goods_commission_rate":"0.8",
	 *      "goods_commission_price":"16.00",
	 *      "goods_payoff_price":"234",
	 *      "goods_type":"combin",
	 *      "goods_count":2,
	 *      "goods_price":100,
	 *      "goods_all_price":200,
	 *      "goods_gsp_ids":"/1/3",
	 *      "goods_gsp_val":"尺码：XXL",
	 *      "goods_mainphoto_path":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg",
	 *      "goods_return_status":"商品退货状态 当有此字段为“”时可退货状态 5为已申请退货，6商家已同意退货，7用户填写退货物流，8商家已确认，提交平台已退款 -1已经超出可退货时间",
	 *      "goods_complaint_status" 没有此字段时可投诉 投诉后的状态为1不可投诉
	 *  }]"
	 */
	@Column(columnDefinition = "LongText")
	private String goods_info;// 使用json管理"[{"goods_id":1,"goods_name":"佐丹奴男装翻领T恤 条纹修身商务男POLO纯棉短袖POLO衫","goods_type":"group","goods_choice_type":1,"goods_cat":0,"goods_commission_rate":"0.8","goods_commission_price":"16.00","goods_payoff_price":"234""goods_type":"combin","goods_count":2,"goods_price":100,"goods_all_price":200,"goods_gsp_ids":"/1/3","goods_gsp_val":"尺码：XXL","goods_mainphoto_path":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg","goods_return_status", 商品退货状态 当有此字段为“”时可退货状态 5为已申请退货，6商家已同意退货，7用户填写退货物流，8商家已确认，提交平台已退款 -1已经超出可退货时间,"goods_complaint_status" 没有此字段时可投诉 投诉后的状态为1不可投诉}]"
	/**
	 * 退货商品详细，
	 * return_goods_id:退货商品id,
	 * return_goods_count：退货数量，
	 * return_goods_commission_rate：退货商品佣金率，
	 * return_goods_price：退货商品单价，
	 * return_goods_content:退货商品说明。
	 * 使用json管理
	 * 格式如下:
	 * "[
	 * 		{"return_goods_id":1,"return_goods_count":3,"return_goods_commission_rate":"0.8","return_goods_price":100},
	 * 		{"return_goods_id":1,"return_goods_count":3,"return_goods_commission_rate":"0.8","return_goods_price":100}
	 *  ]"
	 */
	@Column(columnDefinition = "LongText")
	private String return_goods_info;// 退货商品详细，return_goods_id:退货商品id,return_goods_count：退货数量，return_goods_commission_rate：退货商品佣金率，return_goods_price：退货商品单价，return_goods_content:退货商品说明。使用json管理"[{"return_goods_id":1,"return_goods_count":3,"return_goods_commission_rate":"0.8","return_goods_price":100},{"return_goods_id":1,"return_goods_count":3,"return_goods_commission_rate":"0.8","return_goods_price":100}]"
	@Column(columnDefinition = "LongText")
	private String group_info;// 生活类团购 团购详情 GroupLifeGoods
								// 使用json管理"{"goods_id":1,"goods_name":"最新电影票xx影视城
	// 团购优惠","goods_type":"0为经销商发布
	// 1为自营发布","goods_price","10
	// 单价","goods_count","1数量","goods_total_price","10
	// 总价"，"goods_mainphoto_path":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg"}"
	@Column(columnDefinition = "LongText")
	private String nuke_goods_info;// 秒杀的商品信息

	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal totalPrice;// 订单总价格
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal goods_amount;// 商品总价格
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal commission_amount;// 该订单佣金总费用，
	@Column(columnDefinition = "LongText")
	private String msg;// 订单附言
	private String payType;// 支付类型，手机端支付时选设置订单支付类型，根据支付类型再进行支付,online:在线支付，payafter:货到付款
	private Long payment_id;// 支付方式
	private String payment_name;//支付名称
	private String payment_mark;//支付标记

	private String transport;// 配送方式
	private String shipCode;// 物流单号
	private Date return_shipTime;// 买家退货发货截止时间
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal ship_price;// 配送价格
	/** 
	 * 订单状态，
	 * 0为订单取消，
	 * 10为已提交待付款，
	 * 15为线下付款提交申请(已经取消该付款方式)，
	 * 16为货到付款，
	 * 20为已付款待发货，
	 * 21申请退款中，
	 * 30为已发货待收货，
	 * 35,自提点已经收货，
	 * 40为已收货, 
	 * 50买家评价完毕 ,
	 * 60:已结束订单,
	 * 65订单不可评价，到达设定时间，系统自动关闭订单相互评价功能
	 * */
	@Lock
	private Integer order_status;// 订单状态，0为订单取消，10为已提交待付款，15为线下付款提交申请(已经取消该付款方式)，16为货到付款，20为已付款待发货，21申请退款中，30为已发货待收货，35,自提点已经收货，40为已收货, 50买家评价完毕 ,60:已结束订单,65订单不可评价，到达设定时间，系统自动关闭订单相互评价功能

	@Column(columnDefinition = "int default 0")
	private Integer distribution_status;// 分销结算：0为未结算，1为已计算

	private String user_id;// 买家用户id
	private String user_name;// 买家用户姓名
	private String store_id;// 订单对应的卖家店铺
	private String store_name;// 订单对应的卖家店铺名称
	private Date payTime;// 付款时间
	private Date shipTime;// 发货时间
	private Date confirmTime;// 确认收货时间
	private Date finishTime;// 完成时间
	/**
	 * 物流公司信息
	 * 格式:
	 * json{
	 * 	"express_company_id":1,
	 * 	"express_company_name":"顺丰快递",
	 * 	"express_company_mark":"shunfeng",
	 * 	"express_company_type":"EXPRESS"
	 * }
	 */
	@Column(columnDefinition = "LongText")
	private String express_info;// 物流公司信息json{"express_company_id":1,"express_company_name":"顺丰快递","express_company_mark":"shunfeng","express_company_type":"EXPRESS"}
	private String receiver_Name;// 收货人姓名,确认订单后，将买家的收货地址所有信息添加到订单中，该订单与买家收货地址没有任何关联
	private String receiver_area;// 收货人地区,例如：福建省厦门市海沧区
	private String receiver_area_info;// 收货人详细地址，例如：凌空二街56-1号，4单元2楼1号
	private String receiver_zip;// 收货人邮政编码
	private String receiver_telephone;// 收货人联系电话
	private String receiver_mobile;// 收货人手机号码

	private Integer invoiceType;// 发票类型，0为个人，1为单位
	private String invoice;// 发票信息
	@OneToMany(mappedBy = "of", cascade = CascadeType.REMOVE)
	private List<OrderFormLog> ofls = new ArrayList<OrderFormLog>();// 订单日志
	@Column(columnDefinition = "LongText")
	private String pay_msg;// 支付相关说明，比如汇款账号、时间等
	@Column(columnDefinition = "bit default 0")
	private boolean auto_confirm_email;// 自动收款的邮件提示
	@Column(columnDefinition = "bit default 0")
	private boolean auto_confirm_sms;// 自动收款的短信提示
	@OneToMany(mappedBy = "of", cascade = CascadeType.REMOVE)
	private List<Evaluate> evas = new ArrayList<Evaluate>();// 订单对应的评价
	@OneToMany(mappedBy = "of", cascade = CascadeType.REMOVE)
	private List<Complaint> complaints = new ArrayList<Complaint>();// 投诉管理类
	@Column(columnDefinition = "LongText")
	private String coupon_info;// 订单使用的优惠券信息json{"couponinfo_id":1,"couponinfo_sn":"ljsa-123l-8weo-s28s","coupon_amount":123,"coupon_goods_rate":0.98},coupon_goods_rate对于当前订单商品价格的比例
	@Column(columnDefinition = "LongText")
	private String order_seller_intro;// 订单卖家给予的说明，用在虚拟商品信息，比如购买充值卡，卖家发货时在这里给出对应的卡号和密钥
	@Column(columnDefinition = "int default 0")
	private Integer order_form;// 订单种类，0为商家商品订单，1为平台自营商品订单
	private Long eva_user_id;// 保存点击确认发货的管理员id，
	// 以下为手机充值订单信息
	private String rc_mobile;// 充值手机号
	@Column(columnDefinition = "int default 0")
	private Integer rc_amount;// 充值金额,只能为整数
	@Column(precision = 12, scale = 2)
	private BigDecimal rc_price;// 充值rc_amount金额需要缴纳的费用
	@Column(precision = 12, scale = 2)
	private BigDecimal out_price;// 充值rc_amount金额第三方平台收取的价格，通过接口自动获取
	@Column(columnDefinition = "varchar(255) default 'mobile'")
	private String rc_type;// 充值类型，默认为mobile，手机直充，目前仅支持该类型
	@Column(columnDefinition = "int default 0")
	private Integer operation_price_count;// 商家手动调整费用次数，0为未调整过，用于显示调整过订单费用的账单显示
	private String delivery_time;// 快递配送时间，给商家发货时候使用
	@Column(columnDefinition = "int default 0")
	private Integer delivery_type;// 配送方式，0为快递配送，1为自提点自提
	private Long delivery_address_id;// 自提点id
	@Column(columnDefinition = "LongText")
	private String delivery_info;// 自提点信息，使用json管理{"id":1,"da_name":"兴华南街自提点","":""},用于在订单中显示
	@Column(columnDefinition = "LongText")
	private String enough_reduce_info;// 订单中参加的满就减的json
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal enough_reduce_amount;// 满就减的金额
	@Column(columnDefinition = "LongText")
	private String gift_infos;// 满就送赠送商品信息
								// [{"goods_id":"1"
								// ,"goods_name":"鳄鱼褐色纯皮裤腰带",store_domainPath这个是店铺二级域名路径
								// goods_domainPath商品二级域名路径
								// "goods_main_photo":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg","goods_price":"54.30","buyGify_id":1}]
								// 参加的商品
	@Column(columnDefinition = "int default 0")
	private Integer whether_gift;// 订单是否有满就送活动0为没有 1为有

	private Long ship_addr_id;// 发货地址Id
	@Column(columnDefinition = "LongText")
	private String ship_addr;// 发货详细地址
	@Column(columnDefinition = "int default 0")
	private Integer order_confirm_delay;// 收货时间延长

	private Nuke nuke;//当前订单对应的活动信息
	private CollageBuy collage;//当前订单对应的拼团活动信息
	
	public String getOrder_special() {
		return this.order_special;
	}

	public void setOrder_special(String order_special) {
		this.order_special = order_special;
	}

	public String getSpecial_invoice() {
		return this.special_invoice;
	}

	public void setSpecial_invoice(String special_invoice) {
		this.special_invoice = special_invoice;
	}

	public String getOrder_sign() {
		return this.order_sign;
	}

	public void setOrder_sign(String order_sign) {
		this.order_sign = order_sign;
	}

	public String getShip_storehouse_name() {
		return this.ship_storehouse_name;
	}

	public void setShip_storehouse_name(String ship_storehouse_name) {
		this.ship_storehouse_name = ship_storehouse_name;
	}

	public Long getReceiver_addr_id() {
		return this.receiver_addr_id;
	}

	public void setReceiver_addr_id(Long receiver_addr_id) {
		this.receiver_addr_id = receiver_addr_id;
	}

	public Long getShip_storehouse_id() {
		return this.ship_storehouse_id;
	}

	public void setShip_storehouse_id(Long ship_storehouse_id) {
		this.ship_storehouse_id = ship_storehouse_id;
	}

	public OrderForm() {
	}

	public OrderForm(Long id, Date addTime) {
		super(id, addTime);
	}

	public String getPayment_mark() {
		return this.payment_mark;
	}

	public void setPayment_mark(String payment_mark) {
		this.payment_mark = payment_mark;
	}

	public String getPayment_name() {
		if ((this.payment_name == null) || ("".equals(this.payment_name))) {
			this.payment_name = "未支付";
		}
		return this.payment_name;
	}

	public void setPayment_name(String payment_name) {
		this.payment_name = payment_name;
	}

	public Long getDelivery_address_id() {
		return this.delivery_address_id;
	}

	public void setDelivery_address_id(Long delivery_address_id) {
		this.delivery_address_id = delivery_address_id;
	}

	public Integer getOrder_confirm_delay() {
		return this.order_confirm_delay;
	}

	public void setOrder_confirm_delay(Integer order_confirm_delay) {
		if(order_confirm_delay == null) {
			this.order_confirm_delay = 0;
		}
		this.order_confirm_delay = order_confirm_delay;
	}

	public Long getShip_addr_id() {
		return this.ship_addr_id;
	}

	public void setShip_addr_id(Long ship_addr_id) {
		this.ship_addr_id = ship_addr_id;
	}

	public String getShip_addr() {
		return this.ship_addr;
	}

	public void setShip_addr(String ship_addr) {
		this.ship_addr = ship_addr;
	}

	public Integer getWhether_gift() {
		return this.whether_gift;
	}

	public void setWhether_gift(Integer whether_gift) {
		this.whether_gift = whether_gift;
	}

	public String getGift_infos() {
		return this.gift_infos;
	}

	public void setGift_infos(String gift_infos) {
		this.gift_infos = gift_infos;
	}

	public BigDecimal getEnough_reduce_amount() {
		return this.enough_reduce_amount;
	}

	public void setEnough_reduce_amount(BigDecimal enough_reduce_amount) {
		this.enough_reduce_amount = enough_reduce_amount;
	}

	public String getEnough_reduce_info() {
		return this.enough_reduce_info;
	}

	public void setEnough_reduce_info(String enough_reduce_info) {
		this.enough_reduce_info = enough_reduce_info;
	}

	public String getDelivery_info() {
		return this.delivery_info;
	}

	public void setDelivery_info(String delivery_info) {
		this.delivery_info = delivery_info;
	}

	public String getDelivery_time() {
		return this.delivery_time;
	}

	public void setDelivery_time(String delivery_time) {
		this.delivery_time = delivery_time;
	}

	public Integer getDelivery_type() {
		return this.delivery_type;
	}

	public void setDelivery_type(Integer delivery_type) {
		this.delivery_type = delivery_type;
	}

	public String getPayType() {
		return this.payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getStore_name() {
		return this.store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public Date getConfirmTime() {
		return this.confirmTime;
	}

	public void setConfirmTime(Date confirmTime) {
		this.confirmTime = confirmTime;
	}

	public String getGroup_info() {
		return this.group_info;
	}

	public void setGroup_info(String group_info) {
		this.group_info = group_info;
	}

	public Integer getOperation_price_count() {
		if(this.operation_price_count==null)
			this.operation_price_count=0;
		return this.operation_price_count;
	}

	public void setOperation_price_count(Integer operation_price_count) {
		this.operation_price_count = operation_price_count;
	}

	public Integer getOrder_main() {
		return this.order_main;
	}

	public void setOrder_main(Integer order_main) {
		this.order_main = order_main;
	}

	public String getChild_order_detail() {
		return this.child_order_detail;
	}

	public void setChild_order_detail(String child_order_detail) {
		this.child_order_detail = child_order_detail;
	}

	public String getTrade_no() {
		return this.trade_no;
	}

	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}

	public String getRc_mobile() {
		return this.rc_mobile;
	}

	public void setRc_mobile(String rc_mobile) {
		this.rc_mobile = rc_mobile;
	}

	public Integer getRc_amount() {
		return this.rc_amount;
	}

	public void setRc_amount(Integer rc_amount) {
		this.rc_amount = rc_amount;
	}

	public BigDecimal getRc_price() {
		return this.rc_price;
	}

	public void setRc_price(BigDecimal rc_price) {
		this.rc_price = rc_price;
	}

	public BigDecimal getOut_price() {
		return this.out_price;
	}

	public void setOut_price(BigDecimal out_price) {
		this.out_price = out_price;
	}

	public String getRc_type() {
		return this.rc_type;
	}

	public void setRc_type(String rc_type) {
		this.rc_type = rc_type;
	}

	public Integer getOrder_cat() {
		if(this.order_cat == null)
			this.order_cat = 0;
		return this.order_cat;
	}

	public void setOrder_cat(Integer order_cat) {
		this.order_cat = order_cat;
	}

	public String getReturn_goods_info() {
		return this.return_goods_info;
	}

	public void setReturn_goods_info(String return_goods_info) {
		this.return_goods_info = return_goods_info;
	}

	public Long getEva_user_id() {
		return this.eva_user_id;
	}

	public void setEva_user_id(Long eva_user_id) {
		this.eva_user_id = eva_user_id;
	}

	public BigDecimal getCommission_amount() {
		return this.commission_amount;
	}

	public void setCommission_amount(BigDecimal commission_amount) {
		this.commission_amount = commission_amount;
	}

	public String getUser_name() {
		return this.user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getCoupon_info() {
		return this.coupon_info;
	}

	public void setCoupon_info(String coupon_info) {
		this.coupon_info = coupon_info;
	}

	public String getExpress_info() {
		return this.express_info;
	}

	public void setExpress_info(String express_info) {
		this.express_info = express_info;
	}

	public String getGoods_info() {
		return this.goods_info;
	}

	public void setGoods_info(String goods_info) {
		this.goods_info = goods_info;
	}

	public String getReceiver_Name() {
		return this.receiver_Name;
	}

	public void setReceiver_Name(String receiver_Name) {
		this.receiver_Name = receiver_Name;
	}

	public String getReceiver_area() {
		return this.receiver_area;
	}

	public void setReceiver_area(String receiver_area) {
		this.receiver_area = receiver_area;
	}

	public String getReceiver_area_info() {
		return this.receiver_area_info;
	}

	public void setReceiver_area_info(String receiver_area_info) {
		this.receiver_area_info = receiver_area_info;
	}

	public String getReceiver_zip() {
		return this.receiver_zip;
	}

	public void setReceiver_zip(String receiver_zip) {
		this.receiver_zip = receiver_zip;
	}

	public String getReceiver_telephone() {
		return this.receiver_telephone;
	}

	public void setReceiver_telephone(String receiver_telephone) {
		this.receiver_telephone = receiver_telephone;
	}

	public String getReceiver_mobile() {
		return this.receiver_mobile;
	}

	public void setReceiver_mobile(String receiver_mobile) {
		this.receiver_mobile = receiver_mobile;
	}

	public String getOrder_type() {
		return this.order_type;
	}

	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}

	public Integer getOrder_form() {
		return this.order_form;
	}

	public void setOrder_form(Integer order_form) {
		this.order_form = order_form;
	}

	public Date getReturn_shipTime() {
		return this.return_shipTime;
	}

	public void setReturn_shipTime(Date return_shipTime) {
		this.return_shipTime = return_shipTime;
	}

	public List<Complaint> getComplaints() {
		return this.complaints;
	}

	public void setComplaints(List<Complaint> complaints) {
		this.complaints = complaints;
	}

	public List<Evaluate> getEvas() {
		return this.evas;
	}

	public void setEvas(List<Evaluate> evas) {
		this.evas = evas;
	}

	public String getOrder_id() {
		return this.order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public BigDecimal getTotalPrice() {
		return this.totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public BigDecimal getShip_price() {
		return this.ship_price;
	}

	public void setShip_price(BigDecimal ship_price) {
		this.ship_price = ship_price;
	}

	public Integer getOrder_status() {
		return this.order_status;
	}

	public void setOrder_status(Integer order_status) {
		this.order_status = order_status;
	}

	public String getMsg() {
		return this.msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Long getPayment_id() {
		return this.payment_id;
	}

	public void setPayment_id(Long payment_id) {
		this.payment_id = payment_id;
	}

	public String getUser_id() {
		return this.user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public Date getPayTime() {
		return this.payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public String getShipCode() {
		return this.shipCode;
	}

	public void setShipCode(String shipCode) {
		this.shipCode = shipCode;
	}

	public Date getShipTime() {
		return this.shipTime;
	}

	public void setShipTime(Date shipTime) {
		this.shipTime = shipTime;
	}

	public Date getFinishTime() {
		return this.finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public Integer getInvoiceType() {
		return this.invoiceType;
	}

	public void setInvoiceType(Integer invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getInvoice() {
		return this.invoice;
	}

	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}

	public String getStore_id() {
		return this.store_id;
	}

	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}

	public List<OrderFormLog> getOfls() {
		return this.ofls;
	}

	public void setOfls(List<OrderFormLog> ofls) {
		this.ofls = ofls;
	}

	public String getPay_msg() {
		return this.pay_msg;
	}

	public void setPay_msg(String pay_msg) {
		this.pay_msg = pay_msg;
	}

	public BigDecimal getGoods_amount() {
		return this.goods_amount;
	}

	public void setGoods_amount(BigDecimal goods_amount) {
		this.goods_amount = goods_amount;
	}

	public Boolean getAuto_confirm_email() {
		return this.auto_confirm_email;
	}
	
	public void setAuto_confirm_email(boolean auto_confirm_email) {
		this.auto_confirm_email = auto_confirm_email;
	}

	public Boolean getAuto_confirm_sms() {
		return this.auto_confirm_sms;
	}

	public void setAuto_confirm_sms(boolean auto_confirm_sms) {
		this.auto_confirm_sms = auto_confirm_sms;
	}

	public String getTransport() {
		return this.transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	public String getOut_order_id() {
		return this.out_order_id;
	}

	public void setOut_order_id(String out_order_id) {
		this.out_order_id = out_order_id;
	}

	public String getOrder_seller_intro() {
		return this.order_seller_intro;
	}

	public void setOrder_seller_intro(String order_seller_intro) {
		this.order_seller_intro = order_seller_intro;
	}

	public String getNuke_goods_info() {
		return nuke_goods_info;
	}

	public void setNuke_goods_info(String nuke_goods_info) {
		this.nuke_goods_info = nuke_goods_info;
	}

	public Integer getDistribution_status() {
		return distribution_status;
	}

	public void setDistribution_status(Integer distribution_status) {
		this.distribution_status = distribution_status;
	}

	public Nuke getNuke() {
		return nuke;
	}

	public void setNuke(Nuke nuke) {
		this.nuke = nuke;
	}

	public CollageBuy getCollage() {
		return collage;
	}

	public void setCollage(CollageBuy collage) {
		this.collage = collage;
	}
}
