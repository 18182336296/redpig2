package com.redpigmall.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.google.common.collect.Lists;
import com.redpigmall.api.annotation.Lock;
import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
import com.redpigmall.api.sec.SecurityUserHolder;

/**
 * 
 * <p>
 * Title: User.java
 * </p>
 * 
 * <p>
 * Description: 用户类，所有用户均使用该类进行管理，包括普通用户、管理员、商家等
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
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "serial" })
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "user")
public class User extends IdEntity   {

	private String application_seller_login_token;

	private String pay_password;

	@Column(columnDefinition = "LongText")
	private String buy_goods_limit_info;//购买的限购商品信息
	@Column(columnDefinition = "int default 0")
	private Integer day_msg_count = 0;
	private String validateCode;
	private String weixin_unionID;
	private Date integral_signDate;
	private Date seller_application_loginTime;
	
	@Lock
	@Column(unique = true)
	private String userName;// 用户名
	private String nickName;// 昵称
	private String trueName;// 真实姓名
	@Lock
	private String password;// 密码
	private String userRole;// 用户角色，登录时根据不同用户角色导向不同的管理页面ADMIN、BUYER、SELLER
	private Date birthday;// 出生日期
	private String telephone;// 电话号码
	private String QQ;// 用户QQ
	private String WW;// 用户阿里旺旺
	private int month_income;// 月收入 0为无收入 1为2000元以下 2为2000-3999 3为4000-5999
								// 4为6000-7999 5为8000以上
	@Column(columnDefinition = "int default 0")
	private int years;// 用户年龄
	private String MSN;// 用户MSN
	private String address;// 用户地址
	private int sex;// 性别 1为男、0为女、-1为保密
	private String email;// 邮箱地址
	private String mobile;// 手机号码
	private String card; // 身份证号
	@OneToOne(mappedBy = "photo")
	private Accessory photo;// 用户照片
	@OneToOne(mappedBy = "area")
	private Area area;// 用户家乡地区信息
	private int status;// 用户状态，-1表示已经删除，删除时在用户前增加下划线
	@ManyToMany(targetEntity = Role.class, fetch = FetchType.LAZY)
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private List<Role> roles = Lists.newArrayList();
	@Transient
	private Map<String, List<Res>> roleResources;
	
	@Transient
	private List<String> res ;
	
	@ManyToMany(targetEntity = Menu.class, fetch = FetchType.LAZY)
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "user_menu", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "menu_id"))
	private List<Menu> menus = Lists.newArrayList();
	
	@Transient
	private List<String> roleRes;
	
	private Date lastLoginDate;// 上次登陆时间
	private Date loginDate;// 登陆时间
	private String lastLoginIp;// 上次登录IP
	private String loginIp;// 登陆Ip
	private int loginCount;// 登录次数
	private int report;// 是否允许举报商品,0为允许，-1为不允许
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal availableBalance;// 可用余额
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal freezeBlance;// 冻结余额
	@Lock
	private int integral;// 用户积分
	@Lock
	private int gold;// 用户金币
	@OneToOne(mappedBy = "user")
	private UserConfig config;
	@OneToMany(mappedBy = "user")
	private List<Accessory> files = new ArrayList<Accessory>();// 用户上传的文件
	@OneToMany(mappedBy = "user")
	private List<GoodsCart> goodscarts = new ArrayList<GoodsCart>();// 用户对应的购物车
	@OneToOne(mappedBy = "store",cascade = CascadeType.REMOVE)
	private Store store;// 用户对应的店铺
	@ManyToOne
	private User parent;// 如果为卖家子账号，则该属性不为空，通过该属性获取卖家子账号对应的店铺信息
	@OneToMany(mappedBy = "parent")
	private List<User> childs = new ArrayList<User>();
	
	@ManyToOne
	private User directSellingParent;//直销父类
	
	@OneToMany(mappedBy = "directSellingParent")
	private List<User> directSellingChilds = Lists.newArrayList();//直销子类
	
	private String direct_selling_qr_path;//直销二维码
	
	private String qq_openid;// qq互联
	private String sina_openid;// 新浪微博id
	@Column(columnDefinition = "int default 0")
	private int user_type;// 用户类别，默认为0个人用户，1为企业用户
	private String contact_user;// 企业联系人
	private String department;// 企业联系人所在部门
	private String company_name;// 企业名称
	private String company_address;// 企业地址
	private String company_purpose;// 购买类型
	private String company_url;// 企业网址
	private String company_person_num;// 企业员工人数
	private String company_trade;// 公司行业
	private String company_nature;// 企业性质
	@Column(columnDefinition = "int default 0")
	private int store_apply_step;// 店铺申请进行的步骤，默认为0,总共分为0、1、2、3、4、5、6、7、8

	@OneToMany(mappedBy = "pd_user", cascade = CascadeType.REMOVE)
	private List<Predeposit> posits = new ArrayList<Predeposit>();// 用户的的预存款充值记录
	@OneToMany(mappedBy = "pd_log_user", cascade = CascadeType.REMOVE)
	private List<PredepositLog> user_predepositlogs = new ArrayList<PredepositLog>();// 用户的预存款日志
	@OneToMany(mappedBy = "pd_log_admin", cascade = CascadeType.REMOVE)
	private List<PredepositLog> admin_predepositlogs = new ArrayList<PredepositLog>();// 管理的预存款操作f日志
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private List<Address> addrs = new ArrayList<Address>();// 用户的配送地址信息
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private List<Album> albums = new ArrayList<Album>();// 用户的相册
	@OneToMany(mappedBy = "fromUser", cascade = CascadeType.REMOVE)
	private List<Message> from_msgs = new ArrayList<Message>();// 用户发送的邮件
	@OneToMany(mappedBy = "toUser", cascade = CascadeType.REMOVE)
	private List<Message> to_msgs = new ArrayList<Message>();// 用户收到的邮件
	@OneToMany(mappedBy = "gold_user", cascade = CascadeType.REMOVE)
	private List<GoldRecord> gold_record = new ArrayList<GoldRecord>();// 用户的金币记录
	@OneToMany(mappedBy = "gold_admin", cascade = CascadeType.REMOVE)
	private List<GoldRecord> gold_record_admin = new ArrayList<GoldRecord>();// 管理员操作的金币记录
	@OneToMany(mappedBy = "integral_user", cascade = CascadeType.REMOVE)
	private List<IntegralLog> integral_logs = new ArrayList<IntegralLog>();// 用户积分日志
	@OneToMany(mappedBy = "operate_user", cascade = CascadeType.REMOVE)
	private List<IntegralLog> integral_admin_logs = new ArrayList<IntegralLog>();// 管理员操作积分日志
	@OneToMany(mappedBy = "evaluate_user", cascade = CascadeType.REMOVE)
	private List<Evaluate> user_evaluate = new ArrayList<Evaluate>();// 买家评价
	
	@OneToMany(mappedBy = "refund_user", cascade = CascadeType.REMOVE)
	private List<RefundLog> rls = new ArrayList<RefundLog>();// 订单退款日志
	@OneToMany(mappedBy = "igo_user", cascade = CascadeType.REMOVE)
	private List<IntegralGoodsOrder> integralorders = new ArrayList<IntegralGoodsOrder>();// 用户对应的积分商品订单
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private List<GroupLifeGoods> grouplifegoods = new ArrayList<GroupLifeGoods>();// 用户发放的生活类团购
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private List<CouponInfo> couponinfos = new ArrayList<CouponInfo>();// 用户发放的生活类团购
	@OneToMany(mappedBy = "seller", cascade = CascadeType.REMOVE)
	private List<PayoffLog> paylogs = new ArrayList<PayoffLog>();// 商家对应的结算日志
	@Column(precision = 12, scale = 2)
	private BigDecimal user_goods_fee;// 该用户总商品消费金额，1、用于计算用户等级，消费越高，等级越高。2、平台发放优惠券时（如：限制人数100人），按照用户消费金额排序，前100人可以得到该优惠券
	@OneToOne(fetch = FetchType.LAZY)
	private StorePoint admin_sp;// 管理员评价统计类
	@Column(columnDefinition = "LongText")
	private String staple_gc;// 用户店铺常用分类，使用json管理[{"id",1"name":"女装"},{"id",3"name":"男装"}]这里只记录最底层分类的id
	private String app_login_token;// 用户手机app登录产生的token
	private String app_seller_login_token;// 商家手机app登录产生的token
	private String user_form;// 用户类型，分为pc，android,ios，
	
	private String mobile_pay_password;// 手机支付密码，用户手机端使用预存款支付时需要输入支付密码，如果用户没有设置支付密码需要输入登录密码
	private String invoice;// 用户发票信息
	@Column(columnDefinition = "int default 0")
	private int invoiceType;// 发票类型
	private Long delivery_id;// 用户所申请的自提点id
	@Column(columnDefinition = "int default 1")
	private int whether_attention;// 是否允许关注 关闭后其他人无法访问您的个人主页 且无法对您进行关注
									// 0为不允许,1为允许
	@Column(columnDefinition = "LongText")
	private String circle_create_info;// 用户所创建圈子id,可以多个，使用json管理[{"id":1,"name":"搞笑一家人"},{"id":1,"name":"搞笑一家人"},{"id":1,"name":"搞笑一家人"}]
	@Column(columnDefinition = "LongText")
	private String circle_attention_info;// 用户关注的圈子信息，使用json管理[{"id":1,"name":"搞笑一家人"},{"id":1,"name":"搞笑一家人"},{"id":1,"name":"搞笑一家人"}]
	private String openId;// 微信公众平台使用的openid
	@Column(columnDefinition = "LongText")
	private String userMark; // 用户保密
	
	@ManyToOne(fetch = FetchType.LAZY)
	private FTPServer ftp;// 创建用户时系统随机为用户分配的ftp服务器
	
	@Version
	private int user_version;// User数据版本控制，不允许多线程同时修改User
	
	@Column(columnDefinition = "LongText  comment '用户标签'")
	private String user_tags;
	
	public FTPServer getFtp() {
		return ftp;
	}

	public void setFtp(FTPServer ftp) {
		this.ftp = ftp;
	}
	
	public Date getSeller_application_loginTime() {
		return this.seller_application_loginTime;
	}

	public void setSeller_application_loginTime(
			Date seller_application_loginTime) {
		this.seller_application_loginTime = seller_application_loginTime;
	}

	public String getApplication_seller_login_token() {
		return this.application_seller_login_token;
	}

	public void setApplication_seller_login_token(
			String application_seller_login_token) {
		this.application_seller_login_token = application_seller_login_token;
	}

	public Date getIntegral_signDate() {
		return this.integral_signDate;
	}

	public void setIntegral_signDate(Date integral_signDate) {
		this.integral_signDate = integral_signDate;
	}

	public String getValidateCode() {
		return this.validateCode;
	}

	public void setValidateCode(String validateCode) {
		this.validateCode = validateCode;
	}

	public Integer getDay_msg_count() {
		return this.day_msg_count;
	}

	public void setDay_msg_count(Integer day_msg_count) {
		this.day_msg_count = day_msg_count;
	}

	public String getWeixin_unionID() {
		return this.weixin_unionID;
	}

	public void setWeixin_unionID(String weixin_unionID) {
		this.weixin_unionID = weixin_unionID;
	}

	public String getBuy_goods_limit_info() {
		return this.buy_goods_limit_info;
	}

	public void setBuy_goods_limit_info(String buy_goods_limit_info) {
		this.buy_goods_limit_info = buy_goods_limit_info;
	}

	public String getApp_seller_login_token() {
		return this.app_seller_login_token;
	}

	public void setApp_seller_login_token(String app_seller_login_token) {
		this.app_seller_login_token = app_seller_login_token;
	}

	public Integer getUser_version() {
		return this.user_version;
	}

	public void setUser_version(Integer user_version) {
		this.user_version = user_version;
	}

	public String getCompany_purpose() {
		return this.company_purpose;
	}

	public void setCompany_purpose(String company_purpose) {
		this.company_purpose = company_purpose;
	}

	public String getCompany_trade() {
		return this.company_trade;
	}

	public void setCompany_trade(String company_trade) {
		this.company_trade = company_trade;
	}

	public String getUserMark() {
		return this.userMark;
	}

	public void setUserMark(String userMark) {
		this.userMark = userMark;
	}

	public String getOpenId() {
		return this.openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getCircle_create_info() {
		return this.circle_create_info;
	}

	public void setCircle_create_info(String circle_create_info) {
		this.circle_create_info = circle_create_info;
	}

	public User() {
	}

	public User(Long id, Date addTime) {
		super(id, addTime);
	}

	public User(Long id, Date addTime, String userName) {
		super(id, addTime);
		this.userName = userName;
	}

	public User(Long id, Date addTime, String userName, String trueName,
			String email, String mobile, String QQ, String WW, String MSN,
			BigDecimal availableBalance, BigDecimal freezeBlance,
			Integer integral, Integer gold, Integer loginCount,
			Date lastLoginDate, String lastLoginIp, BigDecimal user_goods_fee) {
		super(id, addTime);
		super.setAddTime(addTime);
		this.userName = userName;
		this.trueName = trueName;
		this.email = email;
		this.mobile = mobile;
		this.QQ = QQ;
		this.WW = WW;
		this.MSN = MSN;
		this.availableBalance = availableBalance;
		this.freezeBlance = freezeBlance;
		this.integral = integral;
		this.gold = gold;
		this.loginCount = loginCount;
		this.lastLoginDate = lastLoginDate;
		this.lastLoginIp = lastLoginIp;
		this.user_goods_fee = user_goods_fee;
	}

	public String getCircle_attention_info() {
		return this.circle_attention_info;
	}

	public void setCircle_attention_info(String circle_attention_info) {
		this.circle_attention_info = circle_attention_info;
	}

	public Integer getWhether_attention() {
		return this.whether_attention;
	}

	public void setWhether_attention(Integer whether_attention) {
		this.whether_attention = whether_attention;
	}

	public Long getDelivery_id() {
		return this.delivery_id;
	}

	public void setDelivery_id(Long delivery_id) {
		this.delivery_id = delivery_id;
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

	public String getPay_password() {
		return this.pay_password;
	}

	public void setPay_password(String pay_password) {
		this.pay_password = pay_password;
	}

	public String getUser_form() {
		return this.user_form;
	}

	public void setUser_form(String user_form) {
		this.user_form = user_form;
	}

	public String getApp_login_token() {
		return this.app_login_token;
	}

	public void setApp_login_token(String app_login_token) {
		this.app_login_token = app_login_token;
	}

	public List<PayoffLog> getPaylogs() {
		return this.paylogs;
	}

	public void setPaylogs(List<PayoffLog> paylogs) {
		this.paylogs = paylogs;
	}

	public List<CouponInfo> getCouponinfos() {
		return this.couponinfos;
	}

	public void setCouponinfos(List<CouponInfo> couponinfos) {
		this.couponinfos = couponinfos;
	}

	public List<GroupLifeGoods> getGrouplifegoods() {
		return this.grouplifegoods;
	}

	public void setGrouplifegoods(List<GroupLifeGoods> grouplifegoods) {
		this.grouplifegoods = grouplifegoods;
	}

	public List<IntegralGoodsOrder> getIntegralorders() {
		return this.integralorders;
	}

	public void setIntegralorders(List<IntegralGoodsOrder> integralorders) {
		this.integralorders = integralorders;
	}

	public List<GoodsCart> getGoodscarts() {
		return this.goodscarts;
	}

	public void setGoodscarts(List<GoodsCart> goodscarts) {
		this.goodscarts = goodscarts;
	}

	public BigDecimal getUser_goods_fee() {
		return this.user_goods_fee;
	}

	public void setUser_goods_fee(BigDecimal user_goods_fee) {
		this.user_goods_fee = user_goods_fee;
	}

	public String getStaple_gc() {
		return this.staple_gc;
	}

	public void setStaple_gc(String staple_gc) {
		this.staple_gc = staple_gc;
	}

	public StorePoint getAdmin_sp() {
		return this.admin_sp;
	}

	public void setAdmin_sp(StorePoint admin_sp) {
		this.admin_sp = admin_sp;
	}

	public Integer getStore_apply_step() {
		return this.store_apply_step;
	}

	public void setStore_apply_step(Integer store_apply_step) {
		this.store_apply_step = store_apply_step;
	}

	public String getNickName() {
		return this.nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getCard() {
		return this.card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public Integer getUser_type() {
		return this.user_type;
	}

	public void setUser_type(Integer user_type) {
		this.user_type = user_type;
	}

	public String getContact_user() {
		return this.contact_user;
	}

	public void setContact_user(String contact_user) {
		this.contact_user = contact_user;
	}

	public String getDepartment() {
		return this.department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getCompany_name() {
		return this.company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public String getCompany_address() {
		return this.company_address;
	}

	public void setCompany_address(String company_address) {
		this.company_address = company_address;
	}

	public String getCompany_url() {
		return this.company_url;
	}

	public void setCompany_url(String company_url) {
		this.company_url = company_url;
	}

	public String getCompany_person_num() {
		return this.company_person_num;
	}

	public void setCompany_person_num(String company_person_num) {
		this.company_person_num = company_person_num;
	}

	public String getCompany_nature() {
		return this.company_nature;
	}

	public void setCompany_nature(String company_nature) {
		this.company_nature = company_nature;
	}

	public static long getSerialversionuid() {
		return 8026813053768023527L;
	}

	public Integer getYears() {
		return this.years;
	}

	public void setYears(Integer years) {
		this.years = years;
	}

	public Area getArea() {
		return this.area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public List<RefundLog> getRls() {
		return this.rls;
	}

	public void setRls(List<RefundLog> rls) {
		this.rls = rls;
	}

	public List<Evaluate> getUser_evaluate() {
		return this.user_evaluate;
	}

	public void setUser_evaluate(List<Evaluate> user_evaluate) {
		this.user_evaluate = user_evaluate;
	}

	public List<IntegralLog> getIntegral_logs() {
		return this.integral_logs;
	}

	public void setIntegral_logs(List<IntegralLog> integral_logs) {
		this.integral_logs = integral_logs;
	}

	public List<IntegralLog> getIntegral_admin_logs() {
		return this.integral_admin_logs;
	}

	public void setIntegral_admin_logs(List<IntegralLog> integral_admin_logs) {
		this.integral_admin_logs = integral_admin_logs;
	}

	public List<GoldRecord> getGold_record() {
		return this.gold_record;
	}

	public void setGold_record(List<GoldRecord> gold_record) {
		this.gold_record = gold_record;
	}

	public List<GoldRecord> getGold_record_admin() {
		return this.gold_record_admin;
	}

	public void setGold_record_admin(List<GoldRecord> gold_record_admin) {
		this.gold_record_admin = gold_record_admin;
	}

	public List<Message> getFrom_msgs() {
		return this.from_msgs;
	}

	public void setFrom_msgs(List<Message> from_msgs) {
		this.from_msgs = from_msgs;
	}

	public List<Message> getTo_msgs() {
		return this.to_msgs;
	}

	public void setTo_msgs(List<Message> to_msgs) {
		this.to_msgs = to_msgs;
	}

	public List<Album> getAlbums() {
		return this.albums;
	}

	public void setAlbums(List<Album> albums) {
		this.albums = albums;
	}

	public List<Address> getAddrs() {
		return this.addrs;
	}

	public void setAddrs(List<Address> addrs) {
		this.addrs = addrs;
	}

	public List<Predeposit> getPosits() {
		return this.posits;
	}

	public void setPosits(List<Predeposit> posits) {
		this.posits = posits;
	}

	public String getSina_openid() {
		return this.sina_openid;
	}

	public void setSina_openid(String sina_openid) {
		this.sina_openid = sina_openid;
	}

	public String getQq_openid() {
		return this.qq_openid;
	}

	public void setQq_openid(String qq_openid) {
		this.qq_openid = qq_openid;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public Date getLoginDate() {
		return this.loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public String getLastLoginIp() {
		return this.lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	public String getLoginIp() {
		return this.loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	public Date getLastLoginDate() {
		return this.lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public Date getBirthday() {
		return this.birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Integer getSex() {
		return this.sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public String getPassword() {
		return this.password;
	}

	public String getUsername() {
		return this.userName;
	}

	public String getUserName() {
		if (SecurityUserHolder.getCurrentUser() != null) {
			if (SecurityUserHolder.getCurrentUser().getUserRole()
					.indexOf("ADMIN") >= 0) {
				return this.userName;
			}
		}
		if (this.nickName != null) {
			return this.nickName;
		}
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public boolean isAccountNonExpired() {
		return true;
	}
	
	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	
	public Map<String, List<Res>> getRoleResources() {
		if (this.roleResources == null) {
			this.roleResources = new HashMap<String, List<Res>>();
			
			for (Role role : this.roles) {
				String roleCode = role.getRoleCode();
				List<Res> ress = role.getReses();
				for (Res res : ress) {
					String key = roleCode + "_" + res.getType();
					if (!this.roleResources.containsKey(key)) {
						this.roleResources.put(key, new ArrayList<Res>());
					}
					this.roleResources.get(key).add(res);
				}
			}
		}
		
		return this.roleResources;
	}
	
	public List<String> getRes() {
		
		if (this.res == null || this.res.size() == 0) {
			this.res = Lists.newArrayList();
			List<Role> roles = this.getRoles();
			for (Role role : roles) {
				List<Res> ress = role.getReses();
				
				for (Res res : ress) {
					this.res.add(res.getValue());
					
				}
			}
		}
		
		return this.res;
	}
	
	public List<String> getRoleRes() {
		
		if (this.res == null || this.res.size() == 0) {
			this.res = Lists.newArrayList();
			List<Role> roles = this.getRoles();
			for (Role role : roles) {
				if(!role.getType().equals("SELLER")){
					continue;
				}
				List<Res> ress = role.getReses();
				
				for (Res res : ress) {
						this.res.add(res.getValue());
				}
			}
		}
		
		return this.res;
	}
	
	public List<Menu> getMenus() {
		return menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public List<Role> getRoles() {
		return this.roles;
	}
	
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	public static long getSerialVersionUID() {
		return 8026813053768023527L;
	}

	public void setRoleResources(Map<String, List<Res>> roleResources) {
		this.roleResources = roleResources;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public boolean isEnabled() {
		return true;
	}

	public String getTrueName() {
		return this.trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public String getUserRole() {
		return this.userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getQQ() {
		return this.QQ;
	}

	public void setQQ(String qq) {
		this.QQ = qq;
	}

	public String getMSN() {
		return this.MSN;
	}

	public void setMSN(String msn) {
		this.MSN = msn;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Accessory getPhoto() {
		return this.photo;
	}

	public void setPhoto(Accessory photo) {
		this.photo = photo;
	}

	public BigDecimal getAvailableBalance() {
		if (this.availableBalance == null) {
			return BigDecimal.valueOf(0.0D);
		}
		return this.availableBalance;
	}

	public void setAvailableBalance(BigDecimal availableBalance) {
		this.availableBalance = availableBalance;
	}

	public BigDecimal getFreezeBlance() {
		return this.freezeBlance;
	}

	public void setFreezeBlance(BigDecimal freezeBlance) {
		this.freezeBlance = freezeBlance;
	}

	public Integer getMonth_income() {
		return this.month_income;
	}

	public void setMonth_income(Integer month_income) {
		this.month_income = month_income;
	}

	public UserConfig getConfig() {
		return this.config;
	}

	public void setConfig(UserConfig config) {
		this.config = config;
	}

	public List<Accessory> getFiles() {
		return this.files;
	}

	public void setFiles(List<Accessory> files) {
		this.files = files;
	}

	public Integer getIntegral() {
		return this.integral;
	}

	public void setIntegral(Integer integral) {
		this.integral = integral;
	}

	public Integer getLoginCount() {
		return this.loginCount;
	}

	public void setLoginCount(Integer loginCount) {
		this.loginCount = loginCount;
	}

	public String getWW() {
		return this.WW;
	}

	public void setWW(String ww) {
		this.WW = ww;
	}

	public Integer getGold() {
		return this.gold;
	}

	public void setGold(Integer gold) {
		this.gold = gold;
	}

	public Integer getReport() {
		return this.report;
	}

	public void setReport(Integer report) {
		this.report = report;
	}

	public List<PredepositLog> getUser_predepositlogs() {
		return this.user_predepositlogs;
	}

	public void setUser_predepositlogs(List<PredepositLog> user_predepositlogs) {
		this.user_predepositlogs = user_predepositlogs;
	}

	public List<PredepositLog> getAdmin_predepositlogs() {
		return this.admin_predepositlogs;
	}

	public void setAdmin_predepositlogs(List<PredepositLog> admin_predepositlogs) {
		this.admin_predepositlogs = admin_predepositlogs;
	}

	public void setParent(User parent) {
		this.parent = parent;
	}

	public List<User> getChilds() {
		return this.childs;
	}

	public void setChilds(List<User> childs) {
		this.childs = childs;
	}

	public Store getStore() {
		return this.store;
	}

	public User getParent() {
		return this.parent;
	}

	public String getUser_tags() {
		return this.user_tags;
	}

	public void setUser_tags(String user_tags) {
		this.user_tags = user_tags;
	}



	public String getMobile_pay_password() {
		return mobile_pay_password;
	}

	public void setMobile_pay_password(String mobile_pay_password) {
		this.mobile_pay_password = mobile_pay_password;
	}

	public void setMonth_income(int month_income) {
		this.month_income = month_income;
	}

	public void setYears(int years) {
		this.years = years;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setLoginCount(int loginCount) {
		this.loginCount = loginCount;
	}

	public void setReport(int report) {
		this.report = report;
	}

	public void setIntegral(int integral) {
		this.integral = integral;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public void setUser_type(int user_type) {
		this.user_type = user_type;
	}

	public void setStore_apply_step(int store_apply_step) {
		this.store_apply_step = store_apply_step;
	}

	public void setInvoiceType(int invoiceType) {
		this.invoiceType = invoiceType;
	}

	public void setWhether_attention(int whether_attention) {
		this.whether_attention = whether_attention;
	}

	public void setUser_version(int user_version) {
		this.user_version = user_version;
	}

	public User getDirectSellingParent() {
		return directSellingParent;
	}

	public void setDirectSellingParent(User directSellingParent) {
		this.directSellingParent = directSellingParent;
	}

	public List<User> getDirectSellingChilds() {
		return directSellingChilds;
	}

	public void setDirectSellingChilds(List<User> directSellingChilds) {
		this.directSellingChilds = directSellingChilds;
	}

	public String getDirect_selling_qr_path() {
		return direct_selling_qr_path;
	}

	public void setDirect_selling_qr_path(String direct_selling_qr_path) {
		this.direct_selling_qr_path = direct_selling_qr_path;
	}

}
