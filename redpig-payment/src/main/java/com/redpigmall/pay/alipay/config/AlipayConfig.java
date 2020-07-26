package com.redpigmall.pay.alipay.config;
/**
 * 类名：AlipayConfig 功能：基础配置类 详细：设置帐户有关信息及返回路径 版本：3.2 日期：2011-03-17 说明：
 * 以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 * 该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 * 
 * 提示：如何获取安全校验码和合作身份者ID 1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 * 2.点击“商家服务”(https://b.alipay.com/order/myOrder.htm)
 * 3.点击“查询合作者身份(PID)”、“查询安全校验码(Key)”
 * 
 * 安全校验码查看时，输入支付密码后，页面呈灰色的现象，怎么办？ 解决方法： 1、检查浏览器配置，不让浏览器做弹框屏蔽设置
 * 2、更换浏览器或电脑，重新登录查询。
 */

public class AlipayConfig {

	// ↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	private String partner = "2088102002733949";
	// 交易安全检验码，由数字和字母组成的32位字符串
	private String key = "o8y6rlckp4q8w5et893squl4pkludvhe";

	// 签约支付宝账号或卖家收款支付宝帐户
	private String seller_email = "zzw888888@tom.com";

	// 支付宝服务器通知的页面 要用 http://格式的完整路径，不允许加?id=123这类自定义参数
	// 必须保证其地址能够在互联网中访问的到
	private String notify_url = "";
	// 当前页面跳转后的页面 要用 http://格式的完整路径，不允许加?id=123这类自定义参数
	// 域名不能写成http://localhost/create_direct_pay_by_user_jsp_utf8/return_url.jsp
	// ，否则会导致return_url执行无效
	private String return_url = "";
	// ↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	// 调试用，创建TXT日志路径
	private String log_path = "D:\\alipay_log_" + System.currentTimeMillis() + ".txt";
	// 字符编码格式 目前支持 gbk 或 utf-8
	private String input_charset = "utf-8";
	// 签名方式 不需修改,无线的产品中，签名方式为rsa时，sign_type需赋值为0001而不是RSA
	private String sign_type = "MD5";
	// 访问模式,根据自己的服务器是否支持ssl访问，若支持请选择https；若不支持请选择http
	private String transport = "http";
	// 商户的私钥
		// 如果签名方式设置为“0001”时，请设置该参数
	public static String private_key = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC/324XuPng16ihc6t8vu0WfPJOf70Y9nBwCEdJ8xVGx1mVnpQwE4THa1atavSPcJoh/TXAIRmZ0OA7bEuyUkOgYbYk1M1eRjYPFmhyFxfp791M2SFY0ALKKZzGTBQ9Ef3X25Clo9Gvwit7cU+zWZO87HOueeH9ELgYif3x97L80q/Uk1LXfbpGbbf/k2VUcimg0GoYkKiNd5SH1JZqIx/arybEcI5hn6pJqW4RNqTODOnd8L/hu0mR3M98RVO6zUJSHmy9WJJLiwfSQD3sgjt+8CZLL/1PPC5MVXoNjXRYOf2ceT12CNSI0MtFJjaM7r4xhChRYA102Uuraf+4woE7AgMBAAECggEAe1aU3or5lZ8Ltz0zryzMGviWif9y/ujrvhiUOuUXeDgNTjBx8bWaVNabET7/lnU7nz21n5unaUjr20byId7fuekVvPdOTJu1hF1TJoPRy391MkEhPoX4RRwaux2MpPU7x9ZMVS3JTMH0UZW1NgUPAxK8ChwvOQ3WSW/q3e/sDrgCt+0zTFzJ2pGG917h+YmDjelxcrNpMWgMSmc78XC9IgfiOx4g7aX+5h4B/O9LqaPNbIevMzNOboPPzVfai1U3eXtFm2n0rmnrd9uUBU2l9Q1sTQYYuWa/dgprjR4iZZUPAK8M0nzeFMbQGNt2ATtRHZa/NT39Zyz6hj5pFlFSAQKBgQDuoza3RhkcybrX+LykqhchTvP/m8bqt4UVhu27UO91d2BVVWvr24/t/dwcGSg2WGeXtdQWiQjxD2JskhjDyuDAu2VEi6reUAMh1tZm180QtP/xjCc5IzrcDr17tb9C9tqoc2H3NT7+cCbyfz12PbrCeAlXWBhIWK622OWe3dnESwKBgQDN1TDJf5mQ/02m5m00N8B1XShawpFf4ntoCv31GkqetCuxbXXU6cZZiV2nXY3YMFNpliR3NUpmTg8OtW4klCR9/887sjdHheTikX1obj57LfcA9SzmZiaBalsFr7zKyGNjawXix65qKfCyLP9QnXaR4wubheNqRKWpOHRXAaPA0QKBgQDBzIy4BFaKmY2CrX9N3tBP/ZWMRQ8jOQz8cYqJb+44IE18n5W4gqP9rAdgTlHo6JygiUsHThyT4GGXOIKiJxWlj8prA14tkx8oERgnhyQDaGre2GpWLbU0V0gsumnsURs8aA/sOjLBYYVPtGPOrc0I94DmjSWTQ2oIFldMUHD0xwKBgQCtFNlahx7WC6j7cIZhw4Pl7PkTIMtn6qXE/oFAyuP2giC/qQDNkesFvXnwU74EDdcGUYfGahxu3LLStoOw5JWvEh3r39eWICQw1RC0cQe/Qw6SUph4AWvdpVZ2+Kjzh//zSLqBUVH72dqZqTbkThS3ZkmLdNGfuavoGR3TUACoEQKBgQDVTZwbvE7yC4HJ0MfwM1mAHG1sw+HpltMUsTwX4SZTmbFFnTrPp8CEMEltv3m0RYxHkVu/B01nGCNsKcq92DXQ6/bvZw59Vy492+xMEOgpNyO+p3tiBaegtyC+vg8wptv/hRr/i+/XZucWauUtZDdZQZrPeTuc/lWBC6oRKiJKfg==";
	
	// 支付宝的公钥
	// 如果签名方式设置为“0001”时，请设置该参数
	public static String ali_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAierXgj0Lz6ik3E/kEhsnoPTdERSucEy7dTEWPEDNUtJafiaNfBX8H16rke4sUc5EvAGZVIWVCdCheXJ7ATHXTcU7s/qUB6OpJp2R+pG2vhEYDfbcVhPkerarRk5AuJnCZml0B8kozXy+ahjEt/h+4GqY/mJ/9X/w1s2nj2IpLnhhxbwSHjB5RD03qJ9yxxnhNvtdVvixIXdbAklmm+3xMvKOaBjutxlpaLRtUWn/cRZpiWovtc1fxZNDzuD2900gc+UAZxtbjPt3S3HCoq81T3XiMMaydpi3j79GjXOwa1XLreWWHV+8fbim46DGmoZMPqmAE6dBG8Vn5sBmjGWNoQIDAQAB";

	public static String getPrivate_key() {
		return private_key;
	}

	public static void setPrivate_key(String private_key) {
		AlipayConfig.private_key = private_key;
	}

	public static String getAli_public_key() {
		return ali_public_key;
	}

	public static void setAli_public_key(String ali_public_key) {
		AlipayConfig.ali_public_key = ali_public_key;
	}

	public String getPartner() {
		return this.partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSeller_email() {
		return this.seller_email;
	}

	public void setSeller_email(String seller_email) {
		this.seller_email = seller_email;
	}

	public String getNotify_url() {
		return this.notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getReturn_url() {
		return this.return_url;
	}

	public void setReturn_url(String return_url) {
		this.return_url = return_url;
	}

	public String getLog_path() {
		return this.log_path;
	}

	public void setLog_path(String log_path) {
		this.log_path = log_path;
	}

	public String getInput_charset() {
		return this.input_charset;
	}

	public void setInput_charset(String input_charset) {
		this.input_charset = input_charset;
	}

	public String getSign_type() {
		return this.sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}

	public String getTransport() {
		return this.transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}
}
