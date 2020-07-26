package com.redpigmall.domain;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * <p>
 * Title: Goods.java
 * </p>
 * 
 * <p>
 * Description:商品实体类,用来描述系统商品信息，系统核心实体类
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
 * @date 2018-4-25
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goods")
public class Goods extends IdEntity {
	private String seo_keywords;// 关键字
	@Column(columnDefinition = "LongText")
	private String seo_description;// 描述
	private String goods_name;// 商品名称
	@Column(precision = 12, scale = 2)
	private BigDecimal goods_price;// 商品原价
	@Column(precision = 12, scale = 2)
	private BigDecimal store_price;// 店铺价格
	@Column(columnDefinition = "int default 0")
	private int goods_inventory;// 库存数量
	private String inventory_type;// 库存方式，分为all全局库存，spec按规格库存
	@Column(columnDefinition = "int default 0")
	private int goods_salenum;// 商品售出数量
	private String goods_serial;// 商品货号
	@Column(precision = 12, scale = 2)
	private BigDecimal goods_weight;// 商品重量
	@Column(precision = 12, scale = 2)
	private BigDecimal goods_volume;// 商品体积
	private String goods_fee;// 运费
	@Column(columnDefinition = "LongText")
	private String goods_details;// 详细说明pc网页
	@Column(columnDefinition = "LongText")
	private String goods_details_mobile;// 详细说明app
	@Column(columnDefinition = "LongText")
	private String pack_details;// 商品包装清单
	@Column(columnDefinition = "LongText")
	private String goods_service;// 商品售后保障
	private boolean store_recommend;// 商城推荐
	private Date store_recommend_time;// 商品商城推荐时间
	private boolean goods_recommend;// 是否店铺推荐，推荐后在店铺首页推荐位显示
	private int goods_click;// 商品浏览次数
	@Column(columnDefinition = "int default 0")
	private int goods_collect;// 商品收藏次数
	@Column(columnDefinition = "int default 0")
	private int goods_cod;// 是否支持货到付款，默认为0：支持货到付款，-1为不支持货到付款
	@ManyToOne(fetch = FetchType.LAZY)
	private Store goods_store;// 所属店铺
	@Column(columnDefinition = "int default 0")
	private int goods_status;// 商品当前状态，-5为平台未审核， 0为上架，1为在仓库中，2为定时自动上架，3为店铺过期自动下架，-1为手动下架状态，-2为违规下架状态
	@Column(columnDefinition = "int default 0")
	private int publish_goods_status;// 商品发布审核后状态， 0为发布后上架，1为发布后在仓库中，2为发布后定时自动上架，平台审核商品后根据该字段设定商品当前状态
	private Date goods_seller_time;// 商品上架时间，系统根据商品上架时间倒序排列
	@Column(columnDefinition = "int default 1")
	private int goods_transfee;// 商品运费承担方式，0为买家承担，1为卖家承担
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsClass gc;// 商品对应的大分类
	@ManyToOne(fetch = FetchType.LAZY)
	private Accessory goods_main_photo;// 商品主图片
	
	@ManyToMany
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "goods_photo", joinColumns = @JoinColumn(name = "goods_id"), inverseJoinColumns = @JoinColumn(name = "photo_id"))
	private List<Accessory> goods_photos = new ArrayList<Accessory>();// 商品其他图片，目前只允许4张,图片可以重复使用
	
	@ManyToMany
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "goods_ugc", joinColumns = @JoinColumn(name = "goods_id"), inverseJoinColumns = @JoinColumn(name = "class_id"))
	private List<UserGoodsClass> goods_ugcs = new ArrayList<UserGoodsClass>();// 店铺中商品所在分类
	@ManyToMany
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "goods_spec", joinColumns = @JoinColumn(name = "goods_id"), inverseJoinColumns = @JoinColumn(name = "spec_id"))
	@OrderBy(value = "sequence asc")
	private List<GoodsSpecProperty> goods_specs = new ArrayList<GoodsSpecProperty>();// 商品对应的规格值
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsBrand goods_brand;// 商品品牌
	
	@Column(columnDefinition = "LongText")
	private String goods_property;// 使用json管理数据
									// [{"val":"中长款（衣长50-70CM）","id":"32769","name":"衣长"},
									// {"val":"纯棉","id":"32768","name":"材质"},
									// {"val":"短袖","id":"1","name":"款式"}]
	@Column(columnDefinition = "LongText")
	private String goods_inventory_detail;// 商品规格详细库存,使用json管理，[{"id":"131072_131080_","price":"144","count":"106",warn:102}]
	private int ztc_status;// 直通车状态，1为开通申请待审核，2为审核通过,-1为审核失败,3为已经开通
	@Column(columnDefinition = "int default 0")
	private int ztc_pay_status;// 直通车金币支付状态，1为支付成功，0为待支付
	@Column(columnDefinition = "int default 30")
	private int ztc_price;// 直通车价格,按照金币计算，单位为个
	@Column(columnDefinition = "int default 30")
	private int ztc_dredge_price;// 已经开通的直通车价格,和ztc_price值一样，由系统定制器控制该值，在用户设定的开始日期后该值才会存在
	private Date ztc_apply_time;// 直通车申请时间
	@Temporal(TemporalType.DATE)
	private Date ztc_begin_time;// 直通车开始时间
	@Column(columnDefinition = "int default 0")
	private int ztc_gold;// 直通车开通金币，扣除完以后自动取消直通车状态
	@Column(columnDefinition = "int default 0")
	private int ztc_click_num;// 直通车商品浏览数
	@ManyToOne(fetch = FetchType.LAZY)
	private User ztc_admin;// 直通车审核管理员
	@Column(columnDefinition = "LongText")
	private String ztc_admin_content;// 直通车审核信息
	@OneToMany(mappedBy = "gg_goods", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<GroupGoods> group_goods_list = new ArrayList<GroupGoods>();// 商品对应的团购信息，一个商品可以参加多个团购，但是团购同一时间段只能发起一个
	@ManyToOne(fetch = FetchType.LAZY)
	private Group group;// 商品对应的团购,通过该字段判断商品当前参与的团购，团购商品审核通过后，给该字段赋值，同一时间段，一款商品只能保持一个团购活动状态
	
	private LimitSelling limitSelling;// 当前秒杀商品活动信息，同一时间段，一个商品只能参加一个秒杀活动！

	@Column(columnDefinition = "int default 0")
	private int group_buy;// 团购状态，0为无团购，1为待审核，2为审核通过,3为团购商品已经卖完 4为审核通过 但未开始
	@Column(columnDefinition = "int default 0")
	private int nuke_buy;// 秒杀状态，0为无秒杀，1为待审核，2为审核通过且开始了,3为秒杀商品已经卖完 4为审核通过 但未开始
	private Nuke nuke;//当前商品对应的活动信息，同一时间段，一个商品只能参加一个秒杀活动！
	@Column(columnDefinition = "int default 0")
	private int collage_buy;// 拼团状态，0为无秒杀，1为待审核，2为审核通过且开始了,3为拼团商品已经卖完 4为审核通过 但未开始
	private CollageBuy collage;//当前商品对应的拼团活动信息，同一时间段，一个商品只能参加一个拼团活动！
	@OneToMany(mappedBy = "evaluate_goods", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private List<Evaluate> evaluates = new ArrayList<Evaluate>();// 商品对应的用户评价，映射评价实体中的evaluate_goods
	@Column(columnDefinition = "int default 0")
	private int evaluate_count;// 评论数，和evaluates总数对应，前端显示数量时候不需要查询evaluates对象
	@Column(columnDefinition = "int default 0")
	private int goods_choice_type;// 0实体商品，1为虚拟商品
	@Column(columnDefinition = "int default 0")
	private int activity_status;// 活动状态，0为无活动，1为待审核，2为审核通过，3为活动已经过期活结束，审核未通过时状态为0
	private Long activity_goods_id;// 对应商城活动商品id，当活动商品过期时该字段将清空
	@Column(precision = 12, scale = 2)
	private BigDecimal goods_current_price;// 商品当前价格,默认等于store_price，参加团购即为团购价，如团购、活动结束后,改价格自动变为store_price
	@Column(columnDefinition = "int default 0")
	private int combin_status;// 组合销售商品，0为无组合销售，1为有组合销售，当该商品参加组合销售时（无论组合商品是否通过审核），该状态为1，当组合时间到期或者组合方案完全删除后该商品状态变为0
	private Long combin_suit_id;// 组合套装方案id
	private Long combin_parts_id;// 组合配件方案id
	@Column(precision = 12, scale = 2)
	private BigDecimal mail_trans_fee;// 平邮费用
	@Column(precision = 12, scale = 2)
	private BigDecimal express_trans_fee;// 快递费用
	@Column(precision = 12, scale = 2)
	private BigDecimal ems_trans_fee;// EMS费用
	@ManyToOne(fetch = FetchType.LAZY)
	private Transport transport;// 调用的运费模板信息
	@Column(precision = 4, scale = 1, columnDefinition = "Decimal default 5.0")
	private BigDecimal description_evaluate;// 商品描述相符评分，默认为5分

	@Column(precision = 3, scale = 2)
	private BigDecimal well_evaluate;// 商品好评率,例如：该值为0.96，好评率即为96%
	@Column(precision = 3, scale = 2)
	private BigDecimal middle_evaluate;// 商品中评率
	@Column(precision = 3, scale = 2)
	private BigDecimal bad_evaluate;// 商品差评率
	@OneToMany(mappedBy = "ag_goods", cascade = CascadeType.REMOVE)
	private List<ActivityGoods> ag_goods_list = new ArrayList<ActivityGoods>();// 商城活动商品
	@Column(columnDefinition = "int default 0")
	private int goods_type;// 商品类型，0为自营商品，1为第三方经销商
	@ManyToOne(fetch = FetchType.LAZY)
	private User user_admin;// 当商品类型为自营商品时，对应的商品发布管理员
	@OneToMany(mappedBy = "goods")
	private List<GoodsCart> carts = new ArrayList<GoodsCart>();// 商品对应的购物车，反向映射
	@OneToMany(mappedBy = "goods")
	private List<ComplaintGoods> cgs = new ArrayList<ComplaintGoods>();// 商品对应的投诉，反向映射
	@Column(columnDefinition = "int default 0")
	private int mobile_recommend;// 手机客户端推荐， 1为推荐，推荐后在手机客户端首页显示
	@Temporal(TemporalType.DATE)
	private Date mobile_recommendTime;// 手机推荐时间，

	@Column(columnDefinition = "int default 0")
	private int mobile_hot;// 手机客户端热卖， 1为热卖，设置后在手机客户端首页显示
	@Temporal(TemporalType.DATE)
	private Date mobile_hotTime;// 手机热卖时间
	
	@Column(columnDefinition = "int default 0")
	private int enough_reduce;// 0为未参加满就减，1为已参加
	
	private int direct_selling;//是否参加直销  0为未参加满就减，1为已参加
	
	private String order_enough_reduce_id;// 对应的满就减id

	@Column(columnDefinition = "int default 0")
	private int order_enough_give_status;// 满就送状态，0为非满就送商品，1为满就送商品

	@Column(columnDefinition = "int default 0")
	private int order_enough_if_give;// 是否是满就送，赠送的商品。0为不送
										// 1为送。送的商品正常价格出售，只有满足订单金额时才已0元出售。
	@Column(precision = 12, scale = 2)
	private BigDecimal buyGift_amount;// 对应的满就送条件金额
	private Long buyGift_id;// 对应的满就送id
	@Column(columnDefinition = "int default 0")
	private int tax_invoice;// 是否支持增值税发票,默认为0不支持，1为支持
	@Column(columnDefinition = "int default 0")
	private int f_sale_type;// 是否为F码销售商品，0为不是F码销售商品，1为F码销售商品，F码商品不可以参加任何商城活动
	@Column(columnDefinition = "LongText")
	private String goods_f_code;// F码信息，使用json管理[{"code":xxx,"status":0},{"code":xxx,"status":1}]
	@Column(columnDefinition = "int default 0")
	private int advance_sale_type;// 是否为预售商品，0为非预售商品，1为预售商品,预售商品不可以参加任何商城活动
	@Temporal(TemporalType.DATE)
	private Date advance_date;// 预售时间
	private int goods_warn_inventory;// 商品预警数量,库存少于预警数量，
	@Column(columnDefinition = "int default 0")
	private int warn_inventory_status;// 预警状态，0为正常，-1为预警
	private Long goods_top_format_id;// 商品顶部版式id
	@Column(columnDefinition = "LongText")
	private String goods_top_format_content;// 商品顶部版式内容
	private Long goods_bottom_format_id;// 商品底部版式id
	@Column(columnDefinition = "LongText")
	private String goods_bottom_format_content;// 商品底部版式内容
	private String qr_img_path;// 商品二维码，v8.0 2016版新增功能，redpigg
	@Column(precision = 12, scale = 2)
	private BigDecimal price_history;// 记录每次商品价格变动时的价格，用于发送收藏商品降价通知
	private String delivery_area;// 发货地址
	private Long delivery_area_id;// 发货地址Id
	@Column(columnDefinition = "int default 0")
	private int whether_free;// 是否有0元试用 0为无 1为是
	@Column(columnDefinition = "int default 0")
	private int weixin_recommend;// 微信端推荐， 1为推荐，推荐后在微信端首页显示
	@Temporal(TemporalType.DATE)
	private Date weixin_recommendTime;// 微信推荐时间，

	@Column(columnDefinition = "int default 0")
	private int weixin_hot;// 微信热卖， 1为热卖，设置后在微信首页显示
	@Temporal(TemporalType.DATE)
	private Date weixin_hotTime;// 微信热卖时间，

	@Column(columnDefinition = "LongText")
	private String goods_specs_info;// 自定义规格属性名称，发布商品时可修改规格名字，{["id":1，"name":"白色"],[]}，规格id为1的名称改为了白色，没有自定义时不存储任何值

	
	@Column(columnDefinition = "LongText")
	private String advance_sale_info;//存储预售商品信息

	@Column(columnDefinition = "int default 0")
	private int goods_limit;//商品限购
	@Column(columnDefinition = "int default 0")
	private int goods_limit_count;//商品限购数量
	@Column(columnDefinition = "LongText")
	private String goods_ad_words;//广告词
	@Column(columnDefinition = "LongText")
	private String goods_color_json;//颜色规格
	@Column(columnDefinition = "LongText")
	private String goods_tags;//商品标签
	@Column(columnDefinition = "LongText")
	private String recommand_goods;//推荐商品
	@Column(columnDefinition = "int default 0")
	private int inventory_set;//库存更新0:订单生成后 1:支付成功后
	private String refuse_info;//拒绝原因
	@Column(columnDefinition = "LongText")
	private String merchantService_info;//商家信息
	
	public String getMerchantService_info() {
		return this.merchantService_info;
	}

	public void setMerchantService_info(String merchantService_info) {
		this.merchantService_info = merchantService_info;
	}

	public String getRefuse_info() {
		return this.refuse_info;
	}

	public void setRefuse_info(String refuse_info) {
		this.refuse_info = refuse_info;
	}

	public int getInventory_set() {
		return this.inventory_set;
	}

	public void setInventory_set(int inventory_set) {
		this.inventory_set = inventory_set;
	}

	public void setGoods_color_json(String goods_color_json) {
		this.goods_color_json = goods_color_json;
	}

	public String getGoods_color_json() {
		return this.goods_color_json;
	}

	public Goods() {
	}

	public Goods(String goods_name, Store goods_store) {
		this.goods_name = goods_name;
		this.goods_store = goods_store;
	}

	public Goods(Long id, String goods_name, BigDecimal goods_current_price,
			BigDecimal goods_price, Accessory goods_main_photo) {
		super.setId(id);
		this.goods_name = goods_name;
		this.goods_current_price = goods_current_price;
		this.goods_price = goods_price;
		this.goods_main_photo = goods_main_photo;
	}

	public Goods(Long id, String goods_name, BigDecimal goods_current_price,
			int goods_inventory) {
		super.setId(id);
		this.goods_name = goods_name;
		this.goods_current_price = goods_current_price;
		this.goods_inventory = goods_inventory;
	}

	public Goods(Long id, String goods_name, BigDecimal goods_current_price,
			int goods_collect, int goods_salenum, Accessory goods_main_photo) {
		super.setId(id);
		this.goods_name = goods_name;
		this.goods_current_price = goods_current_price;
		this.goods_collect = goods_collect;
		this.goods_salenum = goods_salenum;
		this.goods_main_photo = goods_main_photo;
	}

	public Goods(Long id, Date addTime) {
		super(id, addTime);
	}

	public int getGoods_limit() {
		return this.goods_limit;
	}

	public void setGoods_limit(int goods_limit) {
		this.goods_limit = goods_limit;
	}

	public int getGoods_limit_count() {
		return this.goods_limit_count;
	}

	public void setGoods_limit_count(int goods_limit_count) {
		this.goods_limit_count = goods_limit_count;
	}

	public int getWeixin_recommend() {
		return this.weixin_recommend;
	}

	public void setWeixin_recommend(int weixin_recommend) {
		this.weixin_recommend = weixin_recommend;
	}

	public Date getWeixin_recommendTime() {
		return this.weixin_recommendTime;
	}

	public void setWeixin_recommendTime(Date weixin_recommendTime) {
		this.weixin_recommendTime = weixin_recommendTime;
	}

	public int getWeixin_hot() {
		return this.weixin_hot;
	}

	public void setWeixin_hot(int weixin_hot) {
		this.weixin_hot = weixin_hot;
	}

	public Date getWeixin_hotTime() {
		return this.weixin_hotTime;
	}

	public void setWeixin_hotTime(Date weixin_hotTime) {
		this.weixin_hotTime = weixin_hotTime;
	}

	public int getEvaluate_count() {
		return this.evaluate_count;
	}

	public void setEvaluate_count(int evaluate_count) {
		this.evaluate_count = evaluate_count;
	}

	public String getGoods_details_mobile() {
		return this.goods_details_mobile;
	}

	public void setGoods_details_mobile(String goods_details_mobile) {
		this.goods_details_mobile = goods_details_mobile;
	}

	public int getWhether_free() {
		return this.whether_free;
	}

	public void setWhether_free(int whether_free) {
		this.whether_free = whether_free;
	}

	public String getDelivery_area() {
		return this.delivery_area;
	}

	public void setDelivery_area(String delivery_area) {
		this.delivery_area = delivery_area;
	}

	public Long getDelivery_area_id() {
		return this.delivery_area_id;
	}

	public void setDelivery_area_id(Long delivery_area_id) {
		this.delivery_area_id = delivery_area_id;
	}

	public int getWarn_inventory_status() {
		return this.warn_inventory_status;
	}

	public void setWarn_inventory_status(int warn_inventory_status) {
		this.warn_inventory_status = warn_inventory_status;
	}

	public BigDecimal getPrice_history() {
		return this.price_history;
	}

	public void setPrice_history(BigDecimal price_history) {
		this.price_history = price_history;
	}

	public Long getActivity_goods_id() {
		return this.activity_goods_id;
	}

	public void setActivity_goods_id(Long activity_goods_id) {
		this.activity_goods_id = activity_goods_id;
	}

	public String getGoods_top_format_content() {
		return this.goods_top_format_content;
	}

	public void setGoods_top_format_content(String goods_top_format_content) {
		this.goods_top_format_content = goods_top_format_content;
	}

	public String getGoods_bottom_format_content() {
		return this.goods_bottom_format_content;
	}

	public void setGoods_bottom_format_content(
			String goods_bottom_format_content) {
		this.goods_bottom_format_content = goods_bottom_format_content;
	}

	public Long getGoods_top_format_id() {
		return this.goods_top_format_id;
	}

	public void setGoods_top_format_id(Long goods_top_format_id) {
		this.goods_top_format_id = goods_top_format_id;
	}

	public Long getGoods_bottom_format_id() {
		return this.goods_bottom_format_id;
	}

	public void setGoods_bottom_format_id(Long goods_bottom_format_id) {
		this.goods_bottom_format_id = goods_bottom_format_id;
	}

	public String getGoods_f_code() {
		return this.goods_f_code;
	}

	public int getGoods_warn_inventory() {
		return this.goods_warn_inventory;
	}

	public void setGoods_warn_inventory(int goods_warn_inventory) {
		this.goods_warn_inventory = goods_warn_inventory;
	}

	public void setGoods_f_code(String goods_f_code) {
		this.goods_f_code = goods_f_code;
	}

	public Date getAdvance_date() {
		return this.advance_date;
	}

	public void setAdvance_date(Date advance_date) {
		this.advance_date = advance_date;
	}

	public int getF_sale_type() {
		return this.f_sale_type;
	}

	public void setF_sale_type(int f_sale_type) {
		this.f_sale_type = f_sale_type;
	}

	public int getAdvance_sale_type() {
		return this.advance_sale_type;
	}

	public void setAdvance_sale_type(int advance_sale_type) {
		this.advance_sale_type = advance_sale_type;
	}

	public int getTax_invoice() {
		return this.tax_invoice;
	}

	public void setTax_invoice(int tax_invoice) {
		this.tax_invoice = tax_invoice;
	}

	public Long getCombin_suit_id() {
		return this.combin_suit_id;
	}

	public void setCombin_suit_id(Long combin_suit_id) {
		this.combin_suit_id = combin_suit_id;
	}

	public Long getCombin_parts_id() {
		return this.combin_parts_id;
	}

	public void setCombin_parts_id(Long combin_parts_id) {
		this.combin_parts_id = combin_parts_id;
	}

	public Long getBuyGift_id() {
		return this.buyGift_id;
	}

	public void setBuyGift_id(Long buyGift_id) {
		this.buyGift_id = buyGift_id;
	}

	public String getQr_img_path() {
		return this.qr_img_path;
	}

	public void setQr_img_path(String qr_img_path) {
		this.qr_img_path = qr_img_path;
	}

	public int getEnough_reduce() {
		return this.enough_reduce;
	}

	public void setEnough_reduce(int enough_reduce) {
		this.enough_reduce = enough_reduce;
	}

	public String getOrder_enough_reduce_id() {
		return this.order_enough_reduce_id;
	}

	public void setOrder_enough_reduce_id(String order_enough_reduce_id) {
		this.order_enough_reduce_id = order_enough_reduce_id;
	}

	public String getGoods_service() {
		return this.goods_service;
	}

	public int getGoods_cod() {
		return this.goods_cod;
	}

	public void setGoods_cod(int goods_cod) {
		this.goods_cod = goods_cod;
	}

	public BigDecimal getBuyGift_amount() {
		return this.buyGift_amount;
	}

	public void setBuyGift_amount(BigDecimal buyGift_amount) {
		this.buyGift_amount = buyGift_amount;
	}

	public void setGoods_service(String goods_service) {
		this.goods_service = goods_service;
	}

	public String getPack_details() {
		return this.pack_details;
	}

	public void setPack_details(String pack_details) {
		this.pack_details = pack_details;
	}

	public int getOrder_enough_if_give() {
		return this.order_enough_if_give;
	}

	public void setOrder_enough_if_give(int order_enough_if_give) {
		this.order_enough_if_give = order_enough_if_give;
	}

	public int getOrder_enough_give_status() {
		return this.order_enough_give_status;
	}

	public void setOrder_enough_give_status(int order_enough_give_status) {
		this.order_enough_give_status = order_enough_give_status;
	}

	public int getPublish_goods_status() {
		return this.publish_goods_status;
	}

	public void setPublish_goods_status(int publish_goods_status) {
		this.publish_goods_status = publish_goods_status;
	}

	public List<ComplaintGoods> getCgs() {
		return this.cgs;
	}

	public void setCgs(List<ComplaintGoods> cgs) {
		this.cgs = cgs;
	}

	public List<GoodsCart> getCarts() {
		return this.carts;
	}

	public void setCarts(List<GoodsCart> carts) {
		this.carts = carts;
	}

	public BigDecimal getWell_evaluate() {
		return this.well_evaluate;
	}

	public void setWell_evaluate(BigDecimal well_evaluate) {
		this.well_evaluate = well_evaluate;
	}

	public BigDecimal getMiddle_evaluate() {
		return this.middle_evaluate;
	}

	public void setMiddle_evaluate(BigDecimal middle_evaluate) {
		this.middle_evaluate = middle_evaluate;
	}

	public BigDecimal getBad_evaluate() {
		return this.bad_evaluate;
	}

	public void setBad_evaluate(BigDecimal bad_evaluate) {
		this.bad_evaluate = bad_evaluate;
	}

	public User getUser_admin() {
		return this.user_admin;
	}

	public void setUser_admin(User user_admin) {
		this.user_admin = user_admin;
	}

	public int getGoods_type() {
		return this.goods_type;
	}

	public void setGoods_type(int goods_type) {
		this.goods_type = goods_type;
	}

	public BigDecimal getDescription_evaluate() {
		return this.description_evaluate;
	}

	public void setDescription_evaluate(BigDecimal description_evaluate) {
		this.description_evaluate = description_evaluate;
	}

	public List<ActivityGoods> getAg_goods_list() {
		return this.ag_goods_list;
	}

	public void setAg_goods_list(List<ActivityGoods> ag_goods_list) {
		this.ag_goods_list = ag_goods_list;
	}

	public int getGroup_buy() {
		return this.group_buy;
	}

	public void setGroup_buy(int group_buy) {
		this.group_buy = group_buy;
	}

	public int getZtc_status() {
		return this.ztc_status;
	}

	public void setZtc_status(int ztc_status) {
		this.ztc_status = ztc_status;
	}

	public int getZtc_price() {
		return this.ztc_price;
	}

	public void setZtc_price(int ztc_price) {
		this.ztc_price = ztc_price;
	}

	public Date getZtc_begin_time() {
		return this.ztc_begin_time;
	}

	public void setZtc_begin_time(Date ztc_begin_time) {
		this.ztc_begin_time = ztc_begin_time;
	}

	public int getZtc_gold() {
		return this.ztc_gold;
	}

	public void setZtc_gold(int ztc_gold) {
		this.ztc_gold = ztc_gold;
	}

	public String getSeo_keywords() {
		return this.seo_keywords;
	}

	public void setSeo_keywords(String seo_keywords) {
		this.seo_keywords = seo_keywords;
	}

	public String getSeo_description() {
		return this.seo_description;
	}

	public void setSeo_description(String seo_description) {
		this.seo_description = seo_description;
	}

	public String getGoods_name() {
		return this.goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public int getGoods_inventory() {
		return this.goods_inventory;
	}

	public void setGoods_inventory(int goods_inventory) {
		this.goods_inventory = goods_inventory;
	}

	public String getInventory_type() {
		return this.inventory_type;
	}

	public void setInventory_type(String inventory_type) {
		this.inventory_type = inventory_type;
	}

	public int getGoods_salenum() {
		return this.goods_salenum;
	}

	public void setGoods_salenum(int goods_salenum) {
		this.goods_salenum = goods_salenum;
	}

	public String getGoods_serial() {
		return this.goods_serial;
	}

	public void setGoods_serial(String goods_serial) {
		this.goods_serial = goods_serial;
	}

	public BigDecimal getGoods_price() {
		return this.goods_price;
	}

	public void setGoods_price(BigDecimal goods_price) {
		this.goods_price = goods_price;
	}

	public BigDecimal getStore_price() {
		return this.store_price;
	}

	public void setStore_price(BigDecimal store_price) {
		this.store_price = store_price;
	}

	public BigDecimal getGoods_weight() {
		return this.goods_weight;
	}

	public void setGoods_weight(BigDecimal goods_weight) {
		this.goods_weight = goods_weight;
	}

	public String getGoods_fee() {
		return this.goods_fee;
	}

	public void setGoods_fee(String goods_fee) {
		this.goods_fee = goods_fee;
	}

	public String getGoods_details() {
		return this.goods_details;
	}

	public void setGoods_details(String goods_details) {
		this.goods_details = goods_details;
	}

	public boolean getStore_recommend() {
		return this.store_recommend;
	}

	public void setStore_recommend(boolean store_recommend) {
		this.store_recommend = store_recommend;
	}

	public Date getStore_recommend_time() {
		return this.store_recommend_time;
	}

	public void setStore_recommend_time(Date store_recommend_time) {
		this.store_recommend_time = store_recommend_time;
	}

	public Boolean getGoods_recommend() {
		return this.goods_recommend;
	}

	public void setGoods_recommend(boolean goods_recommend) {
		this.goods_recommend = goods_recommend;
	}

	public int getGoods_click() {
		return this.goods_click;
	}

	public void setGoods_click(int goods_click) {
		this.goods_click = goods_click;
	}

	public Store getGoods_store() {
		return this.goods_store;
	}

	public void setGoods_store(Store goods_store) {
		this.goods_store = goods_store;
	}

	public int getGoods_status() {
		return this.goods_status;
	}

	public void setGoods_status(int goods_status) {
		this.goods_status = goods_status;
	}

	public Date getGoods_seller_time() {
		return this.goods_seller_time;
	}

	public void setGoods_seller_time(Date goods_seller_time) {
		this.goods_seller_time = goods_seller_time;
	}

	public int getGoods_transfee() {
		return this.goods_transfee;
	}

	public void setGoods_transfee(int goods_transfee) {
		this.goods_transfee = goods_transfee;
	}

	public GoodsClass getGc() {
		return this.gc;
	}

	public void setGc(GoodsClass gc) {
		this.gc = gc;
	}

	public Accessory getGoods_main_photo() {
		return this.goods_main_photo;
	}

	public void setGoods_main_photo(Accessory goods_main_photo) {
		this.goods_main_photo = goods_main_photo;
	}

	public List<Accessory> getGoods_photos() {
		return this.goods_photos;
	}

	public void setGoods_photos(List<Accessory> goods_photos) {
		this.goods_photos = goods_photos;
	}

	public List<UserGoodsClass> getGoods_ugcs() {
		return this.goods_ugcs;
	}

	public void setGoods_ugcs(List<UserGoodsClass> goods_ugcs) {
		this.goods_ugcs = goods_ugcs;
	}

	public List<GoodsSpecProperty> getGoods_specs() {
		return this.goods_specs;
	}

	public void setGoods_specs(List<GoodsSpecProperty> goods_specs) {
		this.goods_specs = goods_specs;
	}

	public GoodsBrand getGoods_brand() {
		return this.goods_brand;
	}

	public void setGoods_brand(GoodsBrand goods_brand) {
		this.goods_brand = goods_brand;
	}

	public String getGoods_property() {
		return this.goods_property;
	}

	public void setGoods_property(String goods_property) {
		this.goods_property = goods_property;
	}

	public String getGoods_inventory_detail() {
		return this.goods_inventory_detail;
	}

	public void setGoods_inventory_detail(String goods_inventory_detail) {
		this.goods_inventory_detail = goods_inventory_detail;
	}

	public int getZtc_pay_status() {
		return this.ztc_pay_status;
	}

	public void setZtc_pay_status(int ztc_pay_status) {
		this.ztc_pay_status = ztc_pay_status;
	}

	public User getZtc_admin() {
		return this.ztc_admin;
	}

	public void setZtc_admin(User ztc_admin) {
		this.ztc_admin = ztc_admin;
	}

	public String getZtc_admin_content() {
		return this.ztc_admin_content;
	}

	public void setZtc_admin_content(String ztc_admin_content) {
		this.ztc_admin_content = ztc_admin_content;
	}

	public Date getZtc_apply_time() {
		return this.ztc_apply_time;
	}

	public void setZtc_apply_time(Date ztc_apply_time) {
		this.ztc_apply_time = ztc_apply_time;
	}

	public int getZtc_click_num() {
		return this.ztc_click_num;
	}

	public void setZtc_click_num(int ztc_click_num) {
		this.ztc_click_num = ztc_click_num;
	}

	public int getZtc_dredge_price() {
		return this.ztc_dredge_price;
	}

	public void setZtc_dredge_price(int ztc_dredge_price) {
		this.ztc_dredge_price = ztc_dredge_price;
	}

	public int getGoods_collect() {
		return this.goods_collect;
	}

	public void setGoods_collect(int goods_collect) {
		this.goods_collect = goods_collect;
	}

	public List<GroupGoods> getGroup_goods_list() {
		return this.group_goods_list;
	}

	public void setGroup_goods_list(List<GroupGoods> group_goods_list) {
		this.group_goods_list = group_goods_list;
	}

	public Group getGroup() {
		return this.group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public List<Evaluate> getEvaluates() {
		return this.evaluates;
	}

	public void setEvaluates(List<Evaluate> evaluates) {
		this.evaluates = evaluates;
	}

	public int getGoods_choice_type() {
		return this.goods_choice_type;
	}

	public void setGoods_choice_type(int goods_choice_type) {
		this.goods_choice_type = goods_choice_type;
	}

	public int getActivity_status() {
		return this.activity_status;
	}

	public void setActivity_status(int activity_status) {
		this.activity_status = activity_status;
	}

	public BigDecimal getGoods_current_price() {
		return this.goods_current_price;
	}

	public void setGoods_current_price(BigDecimal goods_current_price) {
		this.goods_current_price = goods_current_price;
	}

	public BigDecimal getGoods_volume() {
		return this.goods_volume;
	}

	public void setGoods_volume(BigDecimal goods_volume) {
		this.goods_volume = goods_volume;
	}

	public BigDecimal getMail_trans_fee() {
		return this.mail_trans_fee;
	}

	public void setMail_trans_fee(BigDecimal mail_trans_fee) {
		this.mail_trans_fee = mail_trans_fee;
	}

	public BigDecimal getExpress_trans_fee() {
		return this.express_trans_fee;
	}

	public void setExpress_trans_fee(BigDecimal express_trans_fee) {
		this.express_trans_fee = express_trans_fee;
	}

	public BigDecimal getEms_trans_fee() {
		return this.ems_trans_fee;
	}

	public void setEms_trans_fee(BigDecimal ems_trans_fee) {
		this.ems_trans_fee = ems_trans_fee;
	}

	public Transport getTransport() {
		return this.transport;
	}

	public void setTransport(Transport transport) {
		this.transport = transport;
	}

	public int getCombin_status() {
		return this.combin_status;
	}

	public void setCombin_status(int combin_status) {
		this.combin_status = combin_status;
	}

	public String getGoods_specs_info() {
		return this.goods_specs_info;
	}

	public void setGoods_specs_info(String goods_specs_info) {
		this.goods_specs_info = goods_specs_info;
	}

	public String getGoods_tags() {
		return this.goods_tags;
	}

	public void setGoods_tags(String goods_tags) {
		this.goods_tags = goods_tags;
	}

	public String getGoods_ad_words() {
		return this.goods_ad_words;
	}

	public void setGoods_ad_words(String goods_ad_words) {
		this.goods_ad_words = goods_ad_words;
	}

	public String getRecommand_goods() {
		return this.recommand_goods;
	}

	public void setRecommand_goods(String recommand_goods) {
		this.recommand_goods = recommand_goods;
	}

	public String getAdvance_sale_info() {
		return this.advance_sale_info;
	}

	public void setAdvance_sale_info(String advance_sale_info) {
		this.advance_sale_info = advance_sale_info;
	}

	public int getDirect_selling() {
		return direct_selling;
	}

	public void setDirect_selling(int direct_selling) {
		this.direct_selling = direct_selling;
	}

	public LimitSelling getLimitSelling() {
		return limitSelling;
	}

	public void setLimitSelling(LimitSelling limitSelling) {
		this.limitSelling = limitSelling;
	}

	public int getNuke_buy() {
		return nuke_buy;
	}

	public void setNuke_buy(int nuke_buy) {
		this.nuke_buy = nuke_buy;
	}

	public Nuke getNuke() {
		return nuke;
	}

	public void setNuke(Nuke nuke) {
		this.nuke = nuke;
	}

	public int getCollage_buy() {
		return collage_buy;
	}

	public void setCollage_buy(int collage_buy) {
		this.collage_buy = collage_buy;
	}

	public CollageBuy getCollage() {
		return collage;
	}

	public void setCollage(CollageBuy collage) {
		this.collage = collage;
	}
}
