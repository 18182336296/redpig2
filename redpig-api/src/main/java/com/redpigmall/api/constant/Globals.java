package com.redpigmall.api.constant;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import com.google.common.collect.Lists;

/**
 * 
 * <p>
 * Title: Globals.java
 * </p>
 * 
 * <p>
 * Description: 系统常量类，这里的常量是系统默认值，可以在系统中进行用户定制
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.com
 * </p>
 * 
 * @author redpigmall
 * 
 * @date 2016-4-24
 * 
 * @version redpigmall_b2b2c 8.0
 */
public class Globals {
	/** 系统默认名称 */
	public static final String DEFAULT_SYSTEM_TITLE = "RedPigMall B2B2C商城系统";
	
	/** 是否允许单点登录，店铺二级域名开启*/
	public static final boolean SSO_SIGN = false;
	
	/** 软件发布小版本号*/
	public static final int DEFAULT_SHOP_VERSION = 20170404;
	
	/** 软件大版本号*/
	public static final String DEFAULT_SHOP_OUT_VERSION = "V8.0";
	
	/** 软件名称*/
	public static final String DEFAULT_WBESITE_NAME = "RedPigMall";
	
	/** 系统关闭原因默认值*/
	public static final String DEFAULT_CLOSE_REASON = "系统维护中...";
	
	/** 系统管理后台默认风格*/
	public static final String DEFAULT_THEME = "blue";
	
	/** 用户各种报表模板*/
	public static final String DERAULT_USER_TEMPLATE = "user_templates";
	
	/** 默认文件上传路径*/
	public static final String UPLOAD_FILE_PATH = "upload";
	
	/** 默认系统语言为简体中文*/
	public static final String DEFAULT_SYSTEM_LANGUAGE = "zh_cn";
	
	/** 默认系统页面根路径*/
	public static final String DEFAULT_SYSTEM_PAGE_ROOT = "WEB-INF/templates/";
	
	/** 默认系统后台页面路径为*/
	public static final String SYSTEM_MANAGE_PAGE_PATH = "WEB-INF/templates/zh_cn/system/";
	
	/** 默认系统页面前台路径*/
	public static final String SYSTEM_FORNT_PAGE_PATH = "WEB-INF/templates/zh_cn/shop/";
	
	/** 系统数据备份默认路径*/
	public static final String SYSTEM_DATA_BACKUP_PATH = "data";
	
	/** 系统是否需要升级,预留字段*/
	public static final Boolean SYSTEM_UPDATE = Boolean.valueOf(true);
	
	/** 是否记录日志*/
	public static final boolean SAVE_LOG = true;
	
	/** 默认验证码类型*/
	public static final String SECURITY_CODE_TYPE = "normal";
	
	/** 是否可以申请开店*/
	public static final boolean STORE_ALLOW = true;
	
	/** 邮箱默认开启*/
	public static final boolean EAMIL_ENABLE = true;
	
	/** 默认图片存储路径格式*/
	public static final String DEFAULT_IMAGESAVETYPE = "sidImg";
	
	/** 默认上传图片最大尺寸,单位为KB*/
	public static final int DEFAULT_IMAGE_SIZE = 1024;
	
	/** 默认上传图片扩展名*/
	public static final String DEFAULT_IMAGE_SUFFIX = "gif|jpg|jpeg|bmp|png|tbi";
	
	/** 默认商城小图片宽度*/
	public static final int DEFAULT_IMAGE_SMALL_WIDTH = 160;
	
	/** 默认商城小图片高度*/
	public static final int DEFAULT_IMAGE_SMALL_HEIGH = 160;
	
	/** 默认商城中图片宽度*/
	public static final int DEFAULT_IMAGE_MIDDLE_WIDTH = 300;
	
	/** 默认商城中图片高度*/
	public static final int DEFAULT_IMAGE_MIDDLE_HEIGH = 300;
	
	/** 默认商城大图片宽度*/
	public static final int DEFAULT_IMAGE_BIG_WIDTH = 1024;
	
	/** 默认商城大图片高度*/
	public static final int DEFAULT_IMAGE_BIG_HEIGH = 1024;
	
	/** 默认投诉时效时间*/
	public static final int DEFAULT_COMPLAINT_TIME = 30;
	
	/** 默认表前缀名*/
	public static final String DEFAULT_TABLE_SUFFIX = "redpigmall_";
	
	/** 第三方账号登录的前缀*/
	public static final String THIRD_ACCOUNT_LOGIN = "redpigmall_thid_login_";
	
	/** 暂时使用第三方，以后公司会接入自己的接口*/
	public static final String DEFAULT_SMS_URL = "http://service.winic.org/sys_port/gateway/";
	
	public static final String DEFAULT_BIND_DOMAIN_CODE = "12321321";
	
	public static  String USER_LOGIN ;
	
	public static final String USER_RES = "USER_RES";//用户资源
	
	public static final String USER_ROLE_GROUP = "USER_ROLE_GROUP";//系统权限组
	
	public static  String static_folder = "";
	
	//=========================================集群相关配置================================================
	//此目录下有图片路径：upload，lucene路径:lucene，QQWry.dat路径：resources/data
	//可以通过nginx、apache等任意配置，之后需要在管理平台配置nginx、apache的访问路径
	
	public static boolean COPY_IMAGE;
	
	//web应用名称:web、admin、store、wap,如果需要拆分可以继续添加
	public static String WEB_NAME;
	//web名字
	public static String WEB = "web";
	//admin名字
	public static String ADMIN = "admin";
	//store名字
	public static String STORE = "store";
	//wap名字
	public static String WAP = "wap";
	
	private static Properties pps = new Properties();
	
	/**不用Filter去处理的url*/
	public static List<String> urls = Lists.newArrayList();
	
	public static List<String> authUrls = Lists.newArrayList();
	
	/** 必须是登陆才能访问的url 比如购物车结算等*/
	public static List<String> filterUrls = Lists.newArrayList();

	static{
			
			try {
				pps.load(Globals.class.getClassLoader().getResourceAsStream("redpigmall.properties"));
				WEB_NAME = pps.getProperty("webName");
				USER_LOGIN = WEB_NAME+"USER";
				static_folder = pps.getProperty("static_folder");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			COPY_IMAGE = false;
			
			/**
				添加不需要Filter过滤的URL
			*/
			
			urls.add("/login");
			
			urls.add("/redpigmall_login");
			
			urls.add("/authority");
			
			urls.add("/verify");
			
			urls.add("/verity_password");
			
			urls.add("/domain_error");
			
			urls.add("/register_mobile");
			
			urls.add("/verity_user");
			
			
			
			urls.add(".css");
			urls.add(".gif");
			urls.add(".jpg");
			urls.add(".jpeg");
			urls.add(".png");
			
			urls.add(".bmp");
			urls.add(".html");
			urls.add(".js");
			urls.add(".ico");
			
			urls.add("/login_error");
			
			authUrls.add("/index*");
			authUrls.add("/redpigmall_login*");
			authUrls.add("/redpigmall_logout*");
			authUrls.add("/login_success*");
			authUrls.add("/login_error*");
			
			authUrls.add("/service_chatting_new*");
			authUrls.add("/welcome*");
			
			filterUrls.add("/goods_cart1");
			filterUrls.add("/goods_cart2");
			
	}
	
	//=========================================集群相关配置================================================
}
