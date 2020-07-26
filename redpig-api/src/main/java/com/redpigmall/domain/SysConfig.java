package com.redpigmall.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.redpigmall.api.annotation.Lock;
import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: SysConfig.java
 * </p>
 * 
 * <p>
 * Description: 系统配置管理类,包括系统基础信息、系统邮箱发送配置、手机短息发送配置、殴飞充值接口配置等所有系统基础相关内容
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "sysconfig")
public class SysConfig extends IdEntity {
	private static final long serialVersionUID = -1665701817869956309L;
	
	private String title;// 商城标题
	private String keywords;// 商城SEO关键字
	private String description;// 商城SEO描述
	private String address;// 商城访问地址，填写商城网址
	private String poweredby;// 前端poweredby
	private String copyRight;// 版权信息
	private String meta_generator;// meta的generator信息
	private String meta_author;// meta的author信息
	private String company_name;// 公司简称，用在超级管理平台的左下角显示
	private String uploadFilePath;// 用户上传文件路径
	private String sysLanguage;// 后台系统语言
	private String sysPcLanguage;// 买家PC端系统语言	
	private String sysStoreLanguage;// 卖家PC端系统语言	
	private int integralRate;// 充值积分兑换比率
	private boolean smsEnbale;// 短信平台开启
	private String smsURL;// 短信平台发送地址
	private String smsUserName;// 短信平台用户名
	private String smsPassword;// 短信平台用户密码
	private String smsTest;// 短信测试发送账号
	private boolean emailEnable;// 邮件是否开启
	private String emailHost;// stmp服务器
	private int emailPort;// stmp端口
	private String emailUser;// 发件人
	private String emailUserName;// 邮箱用户名
	private String emailPws;// 邮箱密码
	private String emailTest;// 邮件发送测试
	private String websiteName;// 网站名称
	private String hotSearch;// 热门搜索
	@Column(columnDefinition = "varchar(255) default 'blue' ")
	private String websiteCss;// 当前网站平台样式，默认为蓝色
	@OneToOne(fetch = FetchType.EAGER)
	private Accessory websiteLogo;// 网站logo
	@Column(columnDefinition = "LongText")
	private String codeStat;// 三方代码统计
	private boolean websiteState;// 网站状态(开/关)
	private boolean visitorConsult;// 游客咨询
	@Column(columnDefinition = "LongText")
	private String closeReason;// 网站关闭原因
	private String securityCodeType;// 验证码类型
	private boolean securityCodeRegister;// 前台注册验证
	private boolean securityCodeLogin;// 前台登陆验证
	private boolean securityCodeConsult;// 商品咨询验证
	private String imageSuffix;// 图片的后缀名
	private String imageWebServer;// 图片服务器地址
	private String cdnServer;// 静态文件存放路径
	private int imageFilesize;// 允许图片上传的最大值
	private int smallWidth;// 最小尺寸像素宽
	private int smallHeight;
	private int middleWidth;// 中尺寸像素宽
	private int middleHeight;
	private int bigWidth;// 大尺寸像素高
	private int bigHeight;
	private boolean integral;// 积分
	private boolean integralStore;// 开启积分商城
	private boolean voucher;// 代金券
	private boolean deposit;// 预存款
	private boolean groupBuy;// 团购
	@Column(columnDefinition = "int default 300")
	private int group_meal_gold;// 团购套餐价格 按每30天多少金币算,默认是300个金币每30天
	private boolean gold;// 金币
	@Column(columnDefinition = "int default 1")
	private int goldMarketValue;// 金币市值，默认是一个金币抵制1元
	private int memberRegister;// 会员注册(赠送积分)
	private int memberDayLogin;// 会员每日登陆(赠送积分)
	private int indentComment;// 订单评论(赠送积分)
	private int consumptionRatio;// 消费比例(赠送积分)
	private int everyIndentLimit;// 每个订单(赠送积分)
	private String imageSaveType;// 图片保存类型
	private int complaint_time;// 举报失效，以订单完成时间开始计算，单位为天
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Accessory storeImage;// 默认店铺标志
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Accessory goodsImage;// 默认商品图片
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Accessory memberIcon;// 默认用户图标
	private boolean store_allow;// 允许店铺申请
	@Column(columnDefinition = "LongText")
	private String user_level;// 会员等级数据，使用json管理
	@Column(columnDefinition = "LongText")
	private String templates;// 店铺样式管理使用字符串管理
	@Column(columnDefinition = "LongText")
	private String store_payment;// 平台支付方式启用情况，使用json管理，如{"alipay":true,"99bill":false}
	@Column(columnDefinition = "LongText")
	private String share_code;// 百度分享代码
	private boolean ztc_status;// 直通车状态
	@Column(columnDefinition = "int default 0")
	private int ztc_goods_view;// 直通车商品显示方式，0为没有任何限制，1为按照商品分类显示，在商品分类搜索页中是否按照该页中分类进行显示，
	private int ztc_price;// 直通车起价，用户可以任意设定价格，必须大于该价格，价格越高排序也靠前
	@Column(columnDefinition = "bit default 0")
	private boolean second_domain_open;// 是否开通二级域名
	@Column(columnDefinition = "int default 0")
	@Lock
	private int domain_allow_count;// 店铺二级域名运行修改次数，0为无限制
	@Column(columnDefinition = "LongText")
	@Lock
	private String sys_domain;// 系统保留二级域名
	@Column(columnDefinition = "bit default 0")
	private boolean qq_login;// 是否允许QQ登录
	private String qq_login_id;// QQ登录Id
	private String qq_login_key;// QQ登录key
	@Column(columnDefinition = "LongText")
	private String qq_domain_code;// QQ域名验证信息
	@Column(columnDefinition = "bit default 0")
	private boolean sina_login;// 是否允许新浪微博登录
	private String sina_login_id;// 新浪微博Id
	private String sina_login_key;// 新浪微博key
	@Column(columnDefinition = "LongText")
	private String sina_domain_code;// 新浪微博域名验证信息
	private Date lucene_update;// 全文索引更新时间
	@Column(columnDefinition = "int default 0")
	private int combin_status;// 组合销套开启状态
	@Column(columnDefinition = "int default 50")
	private int combin_amount;// 组合销套餐费用，单位为金币/30天
	@Column(columnDefinition = "int default 5")
	private int combin_count;// 组合销售中的最大商品数量
	@Column(columnDefinition = "int default 1")
	private int combin_scheme_count;// 组合销售方案数量
	
	@OneToMany(mappedBy = "config")
	private List<Accessory> login_imgs = new ArrayList<Accessory>();// 登录页面的左侧图片
	
	@Column(columnDefinition = "LongText")
	private String service_telphone_list;// 平台客服电话，一行一个
	@Column(columnDefinition = "LongText")
	private String service_qq_list;// 平台客服QQ，一行一个
	@OneToOne
	private Accessory admin_login_logo;// 平台登录页的左上角Logo
	@OneToOne
	private Accessory admin_manage_logo;// 平台管理中心左上角的Logo
	@Column(columnDefinition = "int default 3")
	@Lock
	private int auto_order_notice;// 卖家发货后达到该时长，给买家发送即将自动确认收货的短信、邮件提醒
	@Column(columnDefinition = "int default 7")
	@Lock
	private int auto_order_confirm;// 卖家发货后，达到该时间系统自动确认收货

	@Column(columnDefinition = "int default 7")
	@Lock
	private int auto_order_return;// 买家申请退货，到达该时间未能输入退货单号及物流公司，退货失败并且订单变为待评价，订单状态为49

	@Column(columnDefinition = "int default 7")
	@Lock
	private int auto_order_evaluate;// 订单确认收货后到达该时间，系统自动管理订单评价功能
	@Column(columnDefinition = "int default 7")
	@Lock
	private int grouplife_order_return;// 生活类团购退货时效，如电影票，购买付款后，在该天数内可以申请退款
	@Column(columnDefinition = "LongText")
	private String kuaidi_id;// kuaidi100快递查询Id，卖家需自行向http://www.kuaidi100.com申请接口id，下个版本公司内部出版接口查询
	@Column(columnDefinition = "LongText")
	private String kuaidi_id2;// 快递100收费推送接口，能够快速查询系统快递信息
	@Column(columnDefinition = "int default 0")
	private int kuaidi_type;// 快递100类型，0为免费版快递查询，1为收费版快递查询，默认为免费版快递查询
	@Column(columnDefinition = "varchar(255)")
	private String currency_code;// 货币符号，默认为人民币¥
	@Lock
	@Column(columnDefinition = "int default 0")
	private int weixin_store;// 微信商城的状态，0为为开启，1为开启状态
	@OneToOne
	private Accessory weixin_qr_img;// 微信二维码图片
	@Lock
	private String weixin_account;// 微信账号
	@Lock
	private String weixin_token;// 微信token，申请开发者时自己填写的token
	@Lock
	private String weixin_appId;// 微信App Id，申请开发者成功后微信生成的AppId
	@Lock
	private String weixin_appSecret;// 微信AppSecret，申请开发者成功后微信生成的AppSecret
	@Lock
	@Column(columnDefinition = "LongText")
	private String weixin_welecome_content;// 关注微信时候的欢迎词
	@Lock
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory store_weixin_logo;// 平台微信商城ogo
	@Column(columnDefinition = "int default 1")
	private int payoff_count;// 月结算次数，可以设置为1次、2次、3次、4次
	private Date payoff_date;// 下次结算日期，每天0:00计算系统下次结算时间
	@Column(columnDefinition = "int default 0")
	private int payoff_mag_type;// 结算通知消息类型，0为系统默认消息，1为自定义消息
	@Column(columnDefinition = "LongText")
	private String payoff_mag_default;// 系统默认结算通知
	@Column(columnDefinition = "LongText")
	private String payoff_mag_auto;// 自定义结算通知
	@Column(precision = 12, scale = 2)
	private BigDecimal payoff_all_sale;// 系统总销售金额
	@Column(precision = 12, scale = 2)
	private BigDecimal payoff_all_commission;// 系统总销售佣金
	@Column(precision = 12, scale = 2)
	private BigDecimal payoff_all_amount;// 系统总结算金额(应结算)
	@Column(precision = 12, scale = 2)
	private BigDecimal payoff_all_amount_reality;// 系统总结算金额(实际结算)
	@Lock
	private boolean ofcard;// 是否开通殴飞充值接口
	@Lock
	private String ofcard_userid;// 殴飞充值接口用户名
	@Lock
	private String ofcard_userpws;// 殴飞充值接口密码
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal ofcard_mobile_profit;// 手机充值的利润值，用户充值手机时候，系统会查询殴飞接口实际收费，根据殴飞的收费加上该利润值，作为用户缴纳的最终金额
	@Lock
	@Column(columnDefinition = "int default 0")
	private int app_download;// 是否启用App下载，默认为0不启用，1为启用
	@Lock
	private String android_download;// 安卓客户端下载地址
	private String android_version;// 安卓客户端版本号
	@Lock
	private String ios_download;// Iphone客户端下载地址
	private String ios_version;// Iphone客户端版本号
	@Lock
	@Column(columnDefinition = "int default 0")
	private int app_seller_download;// 是否启用商家App下载，默认为0不启用，1为启用
	@Lock
	private String android_seller_download;// 安卓商家客户端下载地址
	private String android_seller_version;// 安卓商家客户端版本号
	@Lock
	private String ios_seller_download;// Iphone商家客户端下载地址
	private String ios_seller_version;// Iphone商家客户端版本号
	@Lock
	@Column(columnDefinition = "int default 0")
	private int buygift_status;// 满就送开启状态 0为关闭 1为开启
	@Column(columnDefinition = "int default 300")
	private int buygift_meal_gold;// 满就送促销价格 按每30天多少金币算,默认是300个金币每30天
	@Lock
	@Column(columnDefinition = "int default 0")
	private int enoughreduce_status;// 满就减开启状态 0为关闭 1为开启
	@Column(columnDefinition = "int default 300")
	private int enoughreduce_meal_gold;// 满就送促销价格 按每30天多少金币算,默认是300个金币每30天
	@Column(columnDefinition = "int default 300")
	private int enoughreduce_max_count;// 第三方店铺最大满就送数量，默认1个
	@Column(columnDefinition = "int default 0")
	private int email_buy;// 邮件购买状态，0为商家不可购买邮件,1为商家可购买邮件
	@Column(columnDefinition = "int default 0")
	private int email_buy_cost;// 邮件购买金额，以100封邮件为单位
	@Column(columnDefinition = "int default 0")
	private int sms_buy;// 短信购买状态，0为商家不可购买短信,1为商家可购买短信
	@Column(columnDefinition = "int default 0")
	private int sms_buy_cost;// 短信购买金额，以100条短信为单位
	@Column(columnDefinition = "int default 0")
	private int whether_free;// 商城是否开启了0元试用0元试用 0为否 1为是
	@Column(columnDefinition = "int default 0")
	private int lucenen_queue;// lucene写索引是否启用队列，默认是发布商品后即刻写索引，效率较低，启用队列后效率高,但是会滞后写入,0：不启用，1：启用
	
	@Column(columnDefinition = "int default 0")
	private int lucenen_cluster;// lucene集群
	
	@Column(columnDefinition = "int default 0")
	private int sms_queue;// 短信发送是否启用队列，默认是不启用队列，相关发送短信的地方都及时发送短信，启用队列后效率高，但是会滞后发送,0：不启用，1：启用
	@Column(columnDefinition = "int default 0")
	private int email_queue;// 邮件发送是否启用队列，默认是不启用队列，相关发送邮件的地方都是及时发送邮件，启用队列后效率高，但是会滞后发送,0：不启用，1：启用
	@Column(columnDefinition = "int default 0")
	private int circle_open;// 系统圈子开启状态，1为开启
	@Column(columnDefinition = "int default 1")
	private int circle_count;// 用户可创建圈子数量，可以在平台中设置
	@Column(columnDefinition = "int default 0")
	private int circle_limit;// 用户创建圈子限制信息，（保存用户等级信息，只有用户等级大于等于该等级时才可以申请创建圈子，0为不限制，1为铜牌，2银牌，3金牌，4超级）
	@Column(columnDefinition = "int default 0")
	private int circle_audit;// 申请圈子是否需要审核，0为不需要审核，1为需要审核
	@Column(columnDefinition = "int default 0")
	private int publish_post_limit;// 用户发帖限制信息（保存用户等级信息，只有用户等级大于等于该等级时才可以发布帖子，0为不限制，1为铜牌，2银牌，3金牌，4超级）
	@Column(columnDefinition = "int default 0")
	private int publish_seller_limit;// 发帖身份限制，0为所有人可发帖，1为只能商家可发帖
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory welcome_img;// 欢迎词图片
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory qr_logo;// 二维码中心Logo图片
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory Website_ico;// 网站ICO图片
	@Column(columnDefinition = "int default 0")
	private int qr_login;// 是否开启二维码登录，默认为0不开启，1为开启，开启后使用app扫描二维码即可完成pc端登录
	@SuppressWarnings("unused")
	private String apiKey;// 手机推送的API key,使用百度的推送服务,系统集成百度推送接口
	@SuppressWarnings("unused")
	private String secretKey;// 手机推送的Secret key,使用百度的推送服务
	@Column(columnDefinition = "int default 30")
	private int evaluate_edit_deadline;// 评价修改期限 默认30天
	@Column(columnDefinition = "int default 180")
	private int evaluate_add_deadline;// 评价追加期限 默认180天
	
	@Column(columnDefinition = "int default 0")
	private Integer smsPlatType = 0;

	private String ymrt_smsURL;
	private String ymrt_smsUserName;
	private String ymrt_smsPassword;
	private String ymrt_smsTest;

	@OneToOne(fetch = FetchType.EAGER)
	private Accessory websiteLogo_wide;

	@Column(columnDefinition = "int default 0")
	private Integer sign_integral = 0;

	@Column(columnDefinition = "int default 24")
	@Lock
	private Integer auto_order_cancel = 24;

	private String app_push_buyer_apiKey;
	private String app_push_buyer_secretKey;
	private String app_push_seller_apiKey;
	private String app_push_seller_secretKey;

	@Column(columnDefinition = "LongText")
	private String app_navigator;
	@Column(columnDefinition = "LongText")
	private String app_launch_ad;
	@Column(columnDefinition = "int default 0")
	private Integer email_register = 0;
	@Column(columnDefinition = "int default 0")
	private Integer phone_register = 0;
	@Column(columnDefinition = "int default 0")
	private Integer weixin_login = 0;
	@Column(columnDefinition = "LongText")
	private String weixin_navigator;
	private String open_weixin_appId;
	private String open_weixin_appSecret;
	@Column(columnDefinition = "int default 0")
	private Integer index_type = 0;
	@Column(columnDefinition = "int default 0")
	private Integer cloudbuy = 0;
	
	//管理平台URL
	@Column(columnDefinition = "LongText")
	private String adminUrl;
	
	//卖家中心URL
	@Column(columnDefinition = "LongText")
	private String sellerUrl;
	
	//前台首页URL
	@Column(columnDefinition = "LongText")
	private String indexUrl;
	
	//H5 URL
	@Column(columnDefinition = "LongText")
	private String h5Url;
	
	private String lucenePath; //lucene更新索引文件存放路径 注意:windows和linux不一样的.
	
	private Integer direct_selling; //0:关闭直销 1:开启直销
	
	private BigDecimal direct_selling_first_level_rate;//分销一级佣金比例
	
	private BigDecimal direct_selling_second_level_rate;//分销二级佣金比例
	
	private String static_folder;//静态文件存放路径
	
	private String vm_folder;//vm模板存放路径
	
	public Integer getCloudbuy() {
		return this.cloudbuy;
	}

	public void setCloudbuy(Integer cloudbuy) {
		this.cloudbuy = cloudbuy;
	}

	public Integer getSmsPlatType() {
		return this.smsPlatType;
	}

	public void setSmsPlatType(Integer smsPlatType) {
		this.smsPlatType = smsPlatType;
	}

	public String getYmrt_smsURL() {
		return this.ymrt_smsURL;
	}

	public void setYmrt_smsURL(String ymrt_smsURL) {
		this.ymrt_smsURL = ymrt_smsURL;
	}

	public String getYmrt_smsUserName() {
		return this.ymrt_smsUserName;
	}

	public void setYmrt_smsUserName(String ymrt_smsUserName) {
		this.ymrt_smsUserName = ymrt_smsUserName;
	}

	public String getYmrt_smsPassword() {
		return this.ymrt_smsPassword;
	}

	public void setYmrt_smsPassword(String ymrt_smsPassword) {
		this.ymrt_smsPassword = ymrt_smsPassword;
	}

	public String getYmrt_smsTest() {
		return this.ymrt_smsTest;
	}

	public void setYmrt_smsTest(String ymrt_smsTest) {
		this.ymrt_smsTest = ymrt_smsTest;
	}

	public Accessory getWebsiteLogo_wide() {
		return this.websiteLogo_wide;
	}

	public void setWebsiteLogo_wide(Accessory websiteLogo_wide) {
		this.websiteLogo_wide = websiteLogo_wide;
	}

	public Integer getAuto_order_cancel() {
		return this.auto_order_cancel;
	}

	public void setAuto_order_cancel(Integer auto_order_cancel) {
		this.auto_order_cancel = auto_order_cancel;
	}

	public String getWeixin_navigator() {
		return this.weixin_navigator;
	}

	public void setWeixin_navigator(String weixin_navigator) {
		this.weixin_navigator = weixin_navigator;
	}

	public Integer getIndex_type() {
		return this.index_type;
	}

	public void setIndex_type(Integer index_type) {
		this.index_type = index_type;
	}

	public Integer getSign_integral() {
		return this.sign_integral;
	}

	public void setSign_integral(Integer sign_integral) {
		this.sign_integral = sign_integral;
	}

	public Integer getEmail_register() {
		return this.email_register;
	}

	public void setEmail_register(Integer email_register) {
		this.email_register = email_register;
	}

	public Integer getPhone_register() {
		return this.phone_register;
	}

	public void setPhone_register(Integer phone_register) {
		this.phone_register = phone_register;
	}

	public Integer getWeixin_login() {
		return this.weixin_login;
	}

	public void setWeixin_login(Integer weixin_login) {
		this.weixin_login = weixin_login;
	}

	public String getOpen_weixin_appId() {
		return this.open_weixin_appId;
	}

	public void setOpen_weixin_appId(String open_weixin_appId) {
		this.open_weixin_appId = open_weixin_appId;
	}

	public String getOpen_weixin_appSecret() {
		return this.open_weixin_appSecret;
	}

	public void setOpen_weixin_appSecret(String open_weixin_appSecret) {
		this.open_weixin_appSecret = open_weixin_appSecret;
	}

	public String getApp_navigator() {
		return this.app_navigator;
	}

	public void setApp_navigator(String app_navigator) {
		this.app_navigator = app_navigator;
	}

	public String getApp_launch_ad() {
		return this.app_launch_ad;
	}

	public void setApp_launch_ad(String app_launch_ad) {
		this.app_launch_ad = app_launch_ad;
	}

	public Accessory getWebsite_ico() {
		return this.Website_ico;
	}

	public void setWebsite_ico(Accessory website_ico) {
		this.Website_ico = website_ico;
	}

	public Integer getEvaluate_add_deadline() {
		return this.evaluate_add_deadline;
	}

	public void setEvaluate_add_deadline(Integer evaluate_add_deadline) {
		this.evaluate_add_deadline = evaluate_add_deadline;
	}

	public Integer getEvaluate_edit_deadline() {
		return this.evaluate_edit_deadline;
	}

	public void setEvaluate_edit_deadline(Integer evaluate_edit_deadline) {
		this.evaluate_edit_deadline = evaluate_edit_deadline;
	}

	public String getApp_push_buyer_apiKey() {
		return this.app_push_buyer_apiKey;
	}

	public void setApp_push_buyer_apiKey(String app_push_buyer_apiKey) {
		this.app_push_buyer_apiKey = app_push_buyer_apiKey;
	}

	public String getApp_push_buyer_secretKey() {
		return this.app_push_buyer_secretKey;
	}

	public void setApp_push_buyer_secretKey(String app_push_buyer_secretKey) {
		this.app_push_buyer_secretKey = app_push_buyer_secretKey;
	}

	public String getApp_push_seller_apiKey() {
		return this.app_push_seller_apiKey;
	}

	public void setApp_push_seller_apiKey(String app_push_seller_apiKey) {
		this.app_push_seller_apiKey = app_push_seller_apiKey;
	}

	public String getApp_push_seller_secretKey() {
		return this.app_push_seller_secretKey;
	}

	public void setApp_push_seller_secretKey(String app_push_seller_secretKey) {
		this.app_push_seller_secretKey = app_push_seller_secretKey;
	}

	public Integer getQr_login() {
		return this.qr_login;
	}

	public void setQr_login(Integer qr_login) {
		this.qr_login = qr_login;
	}

	public Accessory getQr_logo() {
		return this.qr_logo;
	}

	public void setQr_logo(Accessory qr_logo) {
		this.qr_logo = qr_logo;
	}

	public Accessory getWelcome_img() {
		return this.welcome_img;
	}

	public void setWelcome_img(Accessory welcome_img) {
		this.welcome_img = welcome_img;
	}

	public Integer getPublish_seller_limit() {
		return this.publish_seller_limit;
	}

	public void setPublish_seller_limit(Integer publish_seller_limit) {
		this.publish_seller_limit = publish_seller_limit;
	}

	public Integer getPublish_post_limit() {
		return this.publish_post_limit;
	}

	public void setPublish_post_limit(Integer publish_post_limit) {
		this.publish_post_limit = publish_post_limit;
	}

	public Integer getCircle_audit() {
		return this.circle_audit;
	}

	public void setCircle_audit(Integer circle_audit) {
		this.circle_audit = circle_audit;
	}

	public Integer getCircle_limit() {
		return this.circle_limit;
	}

	public void setCircle_limit(Integer circle_limit) {
		this.circle_limit = circle_limit;
	}

	public Integer getCircle_count() {
		return this.circle_count;
	}

	public void setCircle_count(Integer circle_count) {
		this.circle_count = circle_count;
	}

	public Integer getCircle_open() {
		return this.circle_open;
	}

	public void setCircle_open(Integer circle_open) {
		this.circle_open = circle_open;
	}

	public SysConfig() {
	}

	public SysConfig(Long id, Date addTime) {
		super(id, addTime);
	}

	public Integer getLucenen_queue() {
		return this.lucenen_queue;
	}

	public void setLucenen_queue(Integer lucenen_queue) {
		this.lucenen_queue = lucenen_queue;
	}

	public int getLucenen_cluster() {
		return lucenen_cluster;
	}

	public void setLucenen_cluster(int lucenen_cluster) {
		this.lucenen_cluster = lucenen_cluster;
	}
	public Integer getSms_queue() {
		return this.sms_queue;
	}

	public void setSms_queue(Integer sms_queue) {
		this.sms_queue = sms_queue;
	}

	public Integer getEmail_queue() {
		return this.email_queue;
	}

	public void setEmail_queue(Integer email_queue) {
		this.email_queue = email_queue;
	}

	public Integer getWhether_free() {
		return this.whether_free;
	}

	public void setWhether_free(Integer whether_free) {
		this.whether_free = whether_free;
	}

	public Integer getKuaidi_type() {
		return this.kuaidi_type;
	}

	public void setKuaidi_type(Integer kuaidi_type) {
		this.kuaidi_type = kuaidi_type;
	}

	public String getKuaidi_id2() {
		return this.kuaidi_id2;
	}

	public void setKuaidi_id2(String kuaidi_id2) {
		this.kuaidi_id2 = kuaidi_id2;
	}

	public Integer getEmail_buy_cost() {
		return this.email_buy_cost;
	}

	public void setEmail_buy_cost(Integer email_buy_cost) {
		this.email_buy_cost = email_buy_cost;
	}

	public Integer getSms_buy_cost() {
		return this.sms_buy_cost;
	}

	public void setSms_buy_cost(Integer sms_buy_cost) {
		this.sms_buy_cost = sms_buy_cost;
	}

	public Integer getEmail_buy() {
		return this.email_buy;
	}

	public void setEmail_buy(Integer email_buy) {
		this.email_buy = email_buy;
	}

	public Integer getSms_buy() {
		return this.sms_buy;
	}

	public void setSms_buy(Integer sms_buy) {
		this.sms_buy = sms_buy;
	}

	public Integer getEnoughreduce_max_count() {
		return this.enoughreduce_max_count;
	}

	public void setEnoughreduce_max_count(Integer enoughreduce_max_count) {
		this.enoughreduce_max_count = enoughreduce_max_count;
	}

	public Integer getCombin_status() {
		return this.combin_status;
	}

	public void setCombin_status(Integer combin_status) {
		this.combin_status = combin_status;
	}

	public Integer getEnoughreduce_status() {
		return this.enoughreduce_status;
	}

	public void setEnoughreduce_status(Integer enoughreduce_status) {
		this.enoughreduce_status = enoughreduce_status;
	}

	public Integer getEnoughreduce_meal_gold() {
		return this.enoughreduce_meal_gold;
	}

	public void setEnoughreduce_meal_gold(Integer enoughreduce_meal_gold) {
		this.enoughreduce_meal_gold = enoughreduce_meal_gold;
	}

	public Integer getBuygift_meal_gold() {
		return this.buygift_meal_gold;
	}

	public void setBuygift_meal_gold(Integer buygift_meal_gold) {
		this.buygift_meal_gold = buygift_meal_gold;
	}

	public Integer getBuygift_status() {
		return this.buygift_status;
	}


	public void setBuygift_status(Integer buygift_status) {
		this.buygift_status = buygift_status;
	}

	public Integer getCombin_scheme_count() {
		return this.combin_scheme_count;
	}

	public void setCombin_scheme_count(Integer combin_scheme_count) {
		this.combin_scheme_count = combin_scheme_count;
	}

	public Accessory getAdmin_login_logo() {
		return this.admin_login_logo;
	}

	public void setAdmin_login_logo(Accessory admin_login_logo) {
		this.admin_login_logo = admin_login_logo;
	}

	public Accessory getAdmin_manage_logo() {
		return this.admin_manage_logo;
	}

	public void setAdmin_manage_logo(Accessory admin_manage_logo) {
		this.admin_manage_logo = admin_manage_logo;
	}

	public String getCompany_name() {
		return this.company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public String getAndroid_version() {
		return this.android_version;
	}

	public void setAndroid_version(String android_version) {
		this.android_version = android_version;
	}

	public String getIos_version() {
		return this.ios_version;
	}

	public void setIos_version(String ios_version) {
		this.ios_version = ios_version;
	}

	public String getPoweredby() {
		return this.poweredby;
	}

	public void setPoweredby(String poweredby) {
		this.poweredby = poweredby;
	}

	public String getMeta_generator() {
		return this.meta_generator;
	}

	public void setMeta_generator(String meta_generator) {
		this.meta_generator = meta_generator;
	}

	public String getMeta_author() {
		return this.meta_author;
	}

	public void setMeta_author(String meta_author) {
		this.meta_author = meta_author;
	}

	public Integer getApp_download() {
		return this.app_download;
	}

	public void setApp_download(Integer app_download) {
		this.app_download = app_download;
	}

	public String getAndroid_download() {
		return this.android_download;
	}

	public void setAndroid_download(String android_download) {
		this.android_download = android_download;
	}

	public String getIos_download() {
		return this.ios_download;
	}

	public void setIos_download(String ios_download) {
		this.ios_download = ios_download;
	}

	public Integer getGrouplife_order_return() {
		return this.grouplife_order_return;
	}

	public void setGrouplife_order_return(Integer grouplife_order_return) {
		this.grouplife_order_return = grouplife_order_return;
	}

	public BigDecimal getPayoff_all_amount_reality() {
		return this.payoff_all_amount_reality;
	}

	public void setPayoff_all_amount_reality(
			BigDecimal payoff_all_amount_reality) {
		this.payoff_all_amount_reality = payoff_all_amount_reality;
	}

	public BigDecimal getPayoff_all_sale() {
		return this.payoff_all_sale;
	}

	public void setPayoff_all_sale(BigDecimal payoff_all_sale) {
		this.payoff_all_sale = payoff_all_sale;
	}

	public BigDecimal getPayoff_all_commission() {
		return this.payoff_all_commission;
	}

	public void setPayoff_all_commission(BigDecimal payoff_all_commission) {
		this.payoff_all_commission = payoff_all_commission;
	}

	public BigDecimal getPayoff_all_amount() {
		return this.payoff_all_amount;
	}

	public void setPayoff_all_amount(BigDecimal payoff_all_amount) {
		this.payoff_all_amount = payoff_all_amount;
	}

	public BigDecimal getOfcard_mobile_profit() {
		return this.ofcard_mobile_profit;
	}

	public void setOfcard_mobile_profit(BigDecimal ofcard_mobile_profit) {
		this.ofcard_mobile_profit = ofcard_mobile_profit;
	}

	public Boolean getOfcard(){
		return this.ofcard;
	}
	
	public void setOfcard(Boolean ofcard) {
		this.ofcard = ofcard;
	}

	public String getOfcard_userid() {
		return this.ofcard_userid;
	}

	public void setOfcard_userid(String ofcard_userid) {
		this.ofcard_userid = ofcard_userid;
	}

	public String getOfcard_userpws() {
		return this.ofcard_userpws;
	}

	public void setOfcard_userpws(String ofcard_userpws) {
		this.ofcard_userpws = ofcard_userpws;
	}

	public Date getPayoff_date() {
		return this.payoff_date;
	}

	public void setPayoff_date(Date payoff_date) {
		this.payoff_date = payoff_date;
	}

	public Integer getPayoff_mag_type() {
		return this.payoff_mag_type;
	}

	public void setPayoff_mag_type(Integer payoff_mag_type) {
		this.payoff_mag_type = payoff_mag_type;
	}

	public String getPayoff_mag_default() {
		return this.payoff_mag_default;
	}

	public void setPayoff_mag_default(String payoff_mag_default) {
		this.payoff_mag_default = payoff_mag_default;
	}

	public String getPayoff_mag_auto() {
		return this.payoff_mag_auto;
	}

	public void setPayoff_mag_auto(String payoff_mag_auto) {
		this.payoff_mag_auto = payoff_mag_auto;
	}

	public Integer getGroup_meal_gold() {
		return this.group_meal_gold;
	}

	public void setGroup_meal_gold(Integer group_meal_gold) {
		this.group_meal_gold = group_meal_gold;
	}

	public Integer getPayoff_count() {
		return this.payoff_count;
	}

	public void setPayoff_count(Integer payoff_count) {
		this.payoff_count = payoff_count;
	}

	public Accessory getWeixin_qr_img() {
		return this.weixin_qr_img;
	}

	public void setWeixin_qr_img(Accessory weixin_qr_img) {
		this.weixin_qr_img = weixin_qr_img;
	}

	public String getWeixin_account() {
		return this.weixin_account;
	}

	public void setWeixin_account(String weixin_account) {
		this.weixin_account = weixin_account;
	}

	public String getWeixin_token() {
		return this.weixin_token;
	}

	public void setWeixin_token(String weixin_token) {
		this.weixin_token = weixin_token;
	}

	public String getWeixin_appId() {
		return this.weixin_appId;
	}

	public void setWeixin_appId(String weixin_appId) {
		this.weixin_appId = weixin_appId;
	}

	public String getWeixin_appSecret() {
		return this.weixin_appSecret;
	}

	public void setWeixin_appSecret(String weixin_appSecret) {
		this.weixin_appSecret = weixin_appSecret;
	}

	public String getWeixin_welecome_content() {
		return this.weixin_welecome_content;
	}

	public void setWeixin_welecome_content(String weixin_welecome_content) {
		this.weixin_welecome_content = weixin_welecome_content;
	}

	public Accessory getStore_weixin_logo() {
		return this.store_weixin_logo;
	}

	public void setStore_weixin_logo(Accessory store_weixin_logo) {
		this.store_weixin_logo = store_weixin_logo;
	}

	public Integer getWeixin_store() {
		return this.weixin_store;
	}

	public void setWeixin_store(Integer weixin_store) {
		this.weixin_store = weixin_store;
	}

	public Integer getAuto_order_return() {
		return this.auto_order_return;
	}

	public void setAuto_order_return(Integer auto_order_return) {
		this.auto_order_return = auto_order_return;
	}

	public Integer getAuto_order_evaluate() {
		return this.auto_order_evaluate;
	}

	public void setAuto_order_evaluate(Integer auto_order_evaluate) {
		this.auto_order_evaluate = auto_order_evaluate;
	}

	public Integer getZtc_goods_view() {
		return this.ztc_goods_view;
	}

	public void setZtc_goods_view(Integer ztc_goods_view) {
		this.ztc_goods_view = ztc_goods_view;
	}

	public String getWebsiteCss() {
		return this.websiteCss;
	}

	public void setWebsiteCss(String websiteCss) {
		this.websiteCss = websiteCss;
	}

	public String getCurrency_code() {
		return this.currency_code;
	}

	public void setCurrency_code(String currency_code) {
		this.currency_code = currency_code;
	}

	public List<Accessory> getLogin_imgs() {
		return this.login_imgs;
	}

	public void setLogin_imgs(List<Accessory> login_imgs) {
		this.login_imgs = login_imgs;
	}

	public Date getLucene_update() {
		return this.lucene_update;
	}

	public void setLucene_update(Date lucene_update) {
		this.lucene_update = lucene_update;
	}

	public Boolean getSina_login() {
		return this.sina_login;
	}

	public void setSina_login(Boolean sina_login) {
		this.sina_login = sina_login;
	}

	public String getSina_login_id() {
		return this.sina_login_id;
	}

	public void setSina_login_id(String sina_login_id) {
		this.sina_login_id = sina_login_id;
	}

	public String getSina_login_key() {
		return this.sina_login_key;
	}

	public void setSina_login_key(String sina_login_key) {
		this.sina_login_key = sina_login_key;
	}

	public String getSina_domain_code() {
		return this.sina_domain_code;
	}

	public void setSina_domain_code(String sina_domain_code) {
		this.sina_domain_code = sina_domain_code;
	}

	public Boolean getQq_login() {
		return this.qq_login;
	}


	
	public void setQq_login(Boolean qq_login) {
		this.qq_login = qq_login;
	}

	public String getQq_login_id() {
		return this.qq_login_id;
	}

	public void setQq_login_id(String qq_login_id) {
		this.qq_login_id = qq_login_id;
	}

	public String getQq_login_key() {
		return this.qq_login_key;
	}

	public void setQq_login_key(String qq_login_key) {
		this.qq_login_key = qq_login_key;
	}

	public Integer getDomain_allow_count() {
		return this.domain_allow_count;
	}

	public void setDomain_allow_count(Integer domain_allow_count) {
		this.domain_allow_count = domain_allow_count;
	}

	public String getSys_domain() {
		return this.sys_domain;
	}

	public void setSys_domain(String sys_domain) {
		this.sys_domain = sys_domain;
	}

	public Boolean getZtc_status() {
		return this.ztc_status;
	}
	
	public void setZtc_status(Boolean ztc_status) {
		this.ztc_status = ztc_status;
	}

	public Integer getZtc_price() {
		return this.ztc_price;
	}

	public void setZtc_price(Integer ztc_price) {
		this.ztc_price = ztc_price;
	}

	public Boolean getStore_allow() {
		return this.store_allow;
	}
	
	public void setStore_allow(Boolean store_allow) {
		this.store_allow = store_allow;
	}

	public Accessory getStoreImage() {
		return this.storeImage;
	}

	public void setStoreImage(Accessory storeImage) {
		this.storeImage = storeImage;
	}

	public Accessory getGoodsImage() {
		return this.goodsImage;
	}

	public void setGoodsImage(Accessory goodsImage) {
		this.goodsImage = goodsImage;
	}

	public Accessory getMemberIcon() {
		return this.memberIcon;
	}

	public void setMemberIcon(Accessory memberIcon) {
		this.memberIcon = memberIcon;
	}

	public String getEmailHost() {
		return this.emailHost;
	}

	public void setEmailHost(String emailHost) {
		this.emailHost = emailHost;
	}

	public Integer getEmailPort() {
		return this.emailPort;
	}

	public void setEmailPort(Integer emailPort) {
		this.emailPort = emailPort;
	}

	public String getEmailUser() {
		return this.emailUser;
	}

	public void setEmailUser(String emailUser) {
		this.emailUser = emailUser;
	}

	public String getEmailUserName() {
		return this.emailUserName;
	}

	public void setEmailUserName(String emailUserName) {
		this.emailUserName = emailUserName;
	}

	public String getEmailPws() {
		return this.emailPws;
	}

	public void setEmailPws(String emailPws) {
		this.emailPws = emailPws;
	}

	public String getSysLanguage() {
		return this.sysLanguage;
	}

	public void setSysLanguage(String sysLanguage) {
		this.sysLanguage = sysLanguage;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKeywords() {
		return this.keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getSmsURL() {
		return this.smsURL;
	}

	public void setSmsURL(String smsURL) {
		this.smsURL = smsURL;
	}

	public String getSmsUserName() {
		return this.smsUserName;
	}

	public void setSmsUserName(String smsUserName) {
		this.smsUserName = smsUserName;
	}

	public String getSmsPassword() {
		return this.smsPassword;
	}

	public void setSmsPassword(String smsPassword) {
		this.smsPassword = smsPassword;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getIntegralRate() {
		return this.integralRate;
	}

	public void setIntegralRate(Integer integralRate) {
		this.integralRate = integralRate;
	}

	public String getCopyRight() {
		return this.copyRight;
	}

	public void setCopyRight(String copyRight) {
		this.copyRight = copyRight;
	}

	public String getWebsiteName() {
		return this.websiteName;
	}

	public void setWebsiteName(String websiteName) {
		this.websiteName = websiteName;
	}

	public String getHotSearch() {
		return this.hotSearch;
	}

	public void setHotSearch(String hotSearch) {
		this.hotSearch = hotSearch;
	}

	public Accessory getWebsiteLogo() {
		return this.websiteLogo;
	}

	public void setWebsiteLogo(Accessory websiteLogo) {
		this.websiteLogo = websiteLogo;
	}

	public String getCodeStat() {
		return this.codeStat;
	}

	public void setCodeStat(String codeStat) {
		this.codeStat = codeStat;
	}

	public Boolean getWebsiteState() {
		return this.websiteState;
	}

	public void setWebsiteState(Boolean websiteState) {
		this.websiteState = websiteState;
	}

	public String getCloseReason() {
		return this.closeReason;
	}

	public void setCloseReason(String closeReason) {
		this.closeReason = closeReason;
	}

	public Boolean getEmailEnable() {
		return this.emailEnable;
	}
	
	public void setEmailEnable(Boolean emailEnable) {
		this.emailEnable = emailEnable;
	}

	public String getEmailTest() {
		return this.emailTest;
	}

	public void setEmailTest(String emailTest) {
		this.emailTest = emailTest;
	}

	public Boolean getSecurityCodeRegister() {
		return this.securityCodeRegister;
	}

	
	public void setSecurityCodeRegister(Boolean securityCodeRegister) {
		this.securityCodeRegister = securityCodeRegister;
	}

	public Boolean getSecurityCodeLogin() {
		return this.securityCodeLogin;
	}
	
	public void setSecurityCodeLogin(Boolean securityCodeLogin) {
		this.securityCodeLogin = securityCodeLogin;
	}

	public Boolean getSecurityCodeConsult() {
		return this.securityCodeConsult;
	}
	
	public void setSecurityCodeConsult(Boolean securityCodeConsult) {
		this.securityCodeConsult = securityCodeConsult;
	}

	public Boolean getVisitorConsult() {
		return this.visitorConsult;
	}	
	
	public void setVisitorConsult(Boolean visitorConsult) {
		this.visitorConsult = visitorConsult;
	}

	public String getImageSuffix() {
		return this.imageSuffix;
	}

	public void setImageSuffix(String imageSuffix) {
		this.imageSuffix = imageSuffix;
	}

	public Integer getImageFilesize() {
		return this.imageFilesize;
	}

	public void setImageFilesize(Integer imageFilesize) {
		this.imageFilesize = imageFilesize;
	}

	public Integer getSmallWidth() {
		return this.smallWidth;
	}

	public void setSmallWidth(Integer smallWidth) {
		this.smallWidth = smallWidth;
	}

	public Integer getSmallHeight() {
		return this.smallHeight;
	}

	public void setSmallHeight(Integer smallHeight) {
		this.smallHeight = smallHeight;
	}

	public Integer getMiddleWidth() {
		return this.middleWidth;
	}

	public void setMiddleWidth(Integer middleWidth) {
		this.middleWidth = middleWidth;
	}

	public Integer getMiddleHeight() {
		return this.middleHeight;
	}

	public void setMiddleHeight(Integer middleHeight) {
		this.middleHeight = middleHeight;
	}

	public Integer getBigWidth() {
		return this.bigWidth;
	}

	public void setBigWidth(Integer bigWidth) {
		this.bigWidth = bigWidth;
	}

	public Integer getBigHeight() {
		return this.bigHeight;
	}

	public void setBigHeight(Integer bigHeight) {
		this.bigHeight = bigHeight;
	}

	public String getImageSaveType() {
		return this.imageSaveType;
	}

	public void setImageSaveType(String imageSaveType) {
		this.imageSaveType = imageSaveType;
	}

	public String getSecurityCodeType() {
		return this.securityCodeType;
	}

	public void setSecurityCodeType(String securityCodeType) {
		this.securityCodeType = securityCodeType;
	}

	public Boolean getIntegral() {
		return this.integral;
	}

	public void setIntegral(Boolean integral) {
		this.integral = integral;
	}

	public Boolean getIntegralStore() {
		return this.integralStore;
	}

	public void setIntegralStore(Boolean integralStore) {
		this.integralStore = integralStore;
	}

	public Boolean getVoucher() {
		return this.voucher;
	}

	public void setVoucher(Boolean voucher) {
		this.voucher = voucher;
	}

	public Boolean getDeposit() {
		return this.deposit;
	}
	
	public void setDeposit(Boolean deposit) {
		this.deposit = deposit;
	}

	public Boolean getGroupBuy() {
		return this.groupBuy;
	}
	
	public void setGroupBuy(Boolean groupBuy) {
		this.groupBuy = groupBuy;
	}

	public Boolean getGold() {
		return this.gold;
	}
	
	public void setGold(Boolean gold) {
		this.gold = gold;
	}

	public Integer getGoldMarketValue() {
		return this.goldMarketValue;
	}

	public void setGoldMarketValue(Integer goldMarketValue) {
		this.goldMarketValue = goldMarketValue;
	}

	public Integer getMemberRegister() {
		return this.memberRegister;
	}

	public void setMemberRegister(Integer memberRegister) {
		this.memberRegister = memberRegister;
	}

	public Integer getMemberDayLogin() {
		return this.memberDayLogin;
	}

	public void setMemberDayLogin(Integer memberDayLogin) {
		this.memberDayLogin = memberDayLogin;
	}

	public Integer getIndentComment() {
		return this.indentComment;
	}

	public void setIndentComment(Integer indentComment) {
		this.indentComment = indentComment;
	}

	public Integer getConsumptionRatio() {
		return this.consumptionRatio;
	}

	public void setConsumptionRatio(Integer consumptionRatio) {
		this.consumptionRatio = consumptionRatio;
	}

	public Integer getEveryIndentLimit() {
		return this.everyIndentLimit;
	}

	public void setEveryIndentLimit(Integer everyIndentLimit) {
		this.everyIndentLimit = everyIndentLimit;
	}

	public Boolean getSmsEnbale() {
		return this.smsEnbale;
	}
	
	public void setSmsEnbale(Boolean smsEnbale) {
		this.smsEnbale = smsEnbale;
	}

	public String getSmsTest() {
		return this.smsTest;
	}

	public void setSmsTest(String smsTest) {
		this.smsTest = smsTest;
	}

	public String getUploadFilePath() {
		
		return this.uploadFilePath;
	}

	public void setUploadFilePath(String uploadFilePath) {
		this.uploadFilePath = uploadFilePath;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getShare_code() {
		return this.share_code;
	}

	public void setShare_code(String share_code) {
		this.share_code = share_code;
	}

	public String getUser_level() {
		return this.user_level;
	}

	public void setUser_level(String user_level) {
		this.user_level = user_level;
	}

	public Integer getComplaint_time() {
		return this.complaint_time;
	}

	public void setComplaint_time(Integer complaint_time) {
		this.complaint_time = complaint_time;
	}

	public Boolean getSecond_domain_open() {
		return this.second_domain_open;
	}

	public void setSecond_domain_open(Boolean second_domain_open) {
		this.second_domain_open = second_domain_open;
	}

	public String getQq_domain_code() {
		return this.qq_domain_code;
	}

	public void setQq_domain_code(String qq_domain_code) {
		this.qq_domain_code = qq_domain_code;
	}

	public String getImageWebServer() {
		return this.imageWebServer;
	}

	public void setImageWebServer(String imageWebServer) {
		this.imageWebServer = imageWebServer;
	}

	public String getService_telphone_list() {
		return this.service_telphone_list;
	}

	public void setService_telphone_list(String service_telphone_list) {
		this.service_telphone_list = service_telphone_list;
	}

	public String getService_qq_list() {
		return this.service_qq_list;
	}

	public void setService_qq_list(String service_qq_list) {
		this.service_qq_list = service_qq_list;
	}

	public Integer getAuto_order_confirm() {
		return this.auto_order_confirm;
	}

	public void setAuto_order_confirm(Integer auto_order_confirm) {
		this.auto_order_confirm = auto_order_confirm;
	}

	public Integer getAuto_order_notice() {
		return this.auto_order_notice;
	}

	public void setAuto_order_notice(Integer auto_order_notice) {
		this.auto_order_notice = auto_order_notice;
	}

	public String getKuaidi_id() {
		return this.kuaidi_id;
	}

	public void setKuaidi_id(String kuaidi_id) {
		this.kuaidi_id = kuaidi_id;
	}

	public Integer getCombin_amount() {
		return this.combin_amount;
	}

	public void setCombin_amount(Integer combin_amount) {
		this.combin_amount = combin_amount;
	}

	public Integer getCombin_count() {
		return this.combin_count;
	}

	public void setCombin_count(Integer combin_count) {
		this.combin_count = combin_count;
	}

	public Integer getApp_seller_download() {
		return this.app_seller_download;
	}

	public void setApp_seller_download(Integer app_seller_download) {
		this.app_seller_download = app_seller_download;
	}

	public String getAndroid_seller_download() {
		return this.android_seller_download;
	}

	public void setAndroid_seller_download(String android_seller_download) {
		this.android_seller_download = android_seller_download;
	}

	public String getAndroid_seller_version() {
		return this.android_seller_version;
	}

	public void setAndroid_seller_version(String android_seller_version) {
		this.android_seller_version = android_seller_version;
	}

	public String getIos_seller_download() {
		return this.ios_seller_download;
	}

	public void setIos_seller_download(String ios_seller_download) {
		this.ios_seller_download = ios_seller_download;
	}

	public String getIos_seller_version() {
		return this.ios_seller_version;
	}

	public void setIos_seller_version(String ios_seller_version) {
		this.ios_seller_version = ios_seller_version;
	}

	public String getAdminUrl() {
		return adminUrl;
	}

	public void setAdminUrl(String adminUrl) {
		this.adminUrl = adminUrl;
	}

	public String getSellerUrl() {
		return sellerUrl;
	}

	public void setSellerUrl(String sellerUrl) {
		this.sellerUrl = sellerUrl;
	}

	public String getIndexUrl() {
		return indexUrl;
	}

	public void setIndexUrl(String indexUrl) {
		this.indexUrl = indexUrl;
	}

	public String getH5Url() {
		return h5Url;
	}

	public void setH5Url(String h5Url) {
		this.h5Url = h5Url;
	}

	public String getLucenePath() {
		return lucenePath;
	}

	public void setLucenePath(String lucenePath) {
		this.lucenePath = lucenePath;
	}

	public Integer getDirect_selling() {
		return direct_selling;
	}

	public void setDirect_selling(Integer direct_selling) {
		this.direct_selling = direct_selling;
	}

	public BigDecimal getDirect_selling_first_level_rate() {
		return direct_selling_first_level_rate;
	}

	public void setDirect_selling_first_level_rate(BigDecimal direct_selling_first_level_rate) {
		this.direct_selling_first_level_rate = direct_selling_first_level_rate;
	}

	public BigDecimal getDirect_selling_second_level_rate() {
		return direct_selling_second_level_rate;
	}

	public void setDirect_selling_second_level_rate(BigDecimal direct_selling_second_level_rate) {
		this.direct_selling_second_level_rate = direct_selling_second_level_rate;
	}
	public String getSysPcLanguage() {
		return sysPcLanguage;
	}

	public void setSysPcLanguage(String sysPcLanguage) {
		this.sysPcLanguage = sysPcLanguage;
	}
	public String getSysStoreLanguage() {
		return sysStoreLanguage;
	}

	public void setSysStoreLanguage(String sysStoreLanguage) {
		this.sysStoreLanguage = sysStoreLanguage;
	}

	public String getStatic_folder() {
		return static_folder;
	}

	public void setStatic_folder(String static_folder) {
		this.static_folder = static_folder;
	}

	public String getVm_folder() {
		return vm_folder;
	}

	public void setVm_folder(String vm_folder) {
		this.vm_folder = vm_folder;
	}

	public String getCdnServer() {
		return cdnServer;
	}

	public void setCdnServer(String cdnServer) {
		this.cdnServer = cdnServer;
	}

}
