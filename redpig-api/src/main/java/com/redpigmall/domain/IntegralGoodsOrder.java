package com.redpigmall.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: IntegralGoodsOrder.java
 * </p>
 * 
 * <p>
 * Description: 积分兑换订单
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
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "integral_goodsorder")
public class IntegralGoodsOrder extends IdEntity {
	private String igo_order_sn;// 订单编号，以igo开头
	@ManyToOne(fetch = FetchType.LAZY)
	private User igo_user;// 订单用户
	@Column(columnDefinition = "LongText")
	private String goods_info;// 订单中商品信息，使用json管理[{id,5,goods_name,"adsfsd"},{},{}]
	@Column(precision = 12, scale = 2)
	private BigDecimal igo_trans_fee;// 购物车运费
	private int igo_status;// 订单状态，0为已提交未付款，20为付款成功，30为已发货，40为已收货完成,-1为已经取消，此时不归还用户积分
	private int igo_total_integral;// 总共消费积分
	@Column(columnDefinition = "LongText")
	private String igo_msg;// 兑换附言
	private String igo_payment;// 支付方式，使用mark标识
	@Column(columnDefinition = "LongText")
	private String igo_pay_msg;// 支付说明
	private Date igo_pay_time;// 支付时间
	@Column(columnDefinition = "LongText")
	private String igo_express_info;// 物流公司信息json{"express_company_id":1,"express_company_name":"顺丰快递","express_company_mark":"shunfeng","express_company_type":"EXPRESS"}
	private String igo_ship_code;// 物流单号
	@Temporal(TemporalType.DATE)
	private Date igo_ship_time;// 发货时间
	@Column(columnDefinition = "LongText")
	private String igo_ship_content;// 发货说明
	private String receiver_Name;// 收货人姓名,确认订单后，将买家的收货地址所有信息添加到订单中，该订单与买家收货地址没有任何关联
	private String receiver_area;// 收货人地区,例如：福建省厦门市海沧区
	private String receiver_area_info;// 收货人详细地址，例如：凌空二街56-1号，4单元2楼1号
	private String receiver_zip;// 收货人邮政编码
	private String receiver_telephone;// 收货人联系电话
	private String receiver_mobile;// 收货人手机号码
	@Column(columnDefinition = "LongText")
	private String order_sign;//订单标签

	public String getOrder_sign() {
		return this.order_sign;
	}

	public void setOrder_sign(String order_sign) {
		this.order_sign = order_sign;
	}

	public IntegralGoodsOrder(Long id, Date addTime) {
		super(id, addTime);
	}

	public IntegralGoodsOrder() {
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

	public String getIgo_ship_content() {
		return this.igo_ship_content;
	}

	public void setIgo_ship_content(String igo_ship_content) {
		this.igo_ship_content = igo_ship_content;
	}

	public String getIgo_ship_code() {
		return this.igo_ship_code;
	}

	public void setIgo_ship_code(String igo_ship_code) {
		this.igo_ship_code = igo_ship_code;
	}

	public Date getIgo_ship_time() {
		return this.igo_ship_time;
	}

	public void setIgo_ship_time(Date igo_ship_time) {
		this.igo_ship_time = igo_ship_time;
	}

	public Date getIgo_pay_time() {
		return this.igo_pay_time;
	}

	public void setIgo_pay_time(Date igo_pay_time) {
		this.igo_pay_time = igo_pay_time;
	}

	public String getIgo_order_sn() {
		return this.igo_order_sn;
	}

	public void setIgo_order_sn(String igo_order_sn) {
		this.igo_order_sn = igo_order_sn;
	}

	public User getIgo_user() {
		return this.igo_user;
	}

	public void setIgo_user(User igo_user) {
		this.igo_user = igo_user;
	}

	public String getGoods_info() {
		return this.goods_info;
	}

	public void setGoods_info(String goods_info) {
		this.goods_info = goods_info;
	}

	public BigDecimal getIgo_trans_fee() {
		return this.igo_trans_fee;
	}

	public void setIgo_trans_fee(BigDecimal igo_trans_fee) {
		this.igo_trans_fee = igo_trans_fee;
	}

	public int getIgo_status() {
		return this.igo_status;
	}

	public void setIgo_status(int igo_status) {
		this.igo_status = igo_status;
	}

	public int getIgo_total_integral() {
		return this.igo_total_integral;
	}

	public void setIgo_total_integral(int igo_total_integral) {
		this.igo_total_integral = igo_total_integral;
	}

	public String getIgo_msg() {
		return this.igo_msg;
	}

	public void setIgo_msg(String igo_msg) {
		this.igo_msg = igo_msg;
	}

	public String getIgo_payment() {
		return this.igo_payment;
	}

	public void setIgo_payment(String igo_payment) {
		this.igo_payment = igo_payment;
	}

	public String getIgo_pay_msg() {
		return this.igo_pay_msg;
	}

	public void setIgo_pay_msg(String igo_pay_msg) {
		this.igo_pay_msg = igo_pay_msg;
	}

	public String getIgo_express_info() {
		return this.igo_express_info;
	}

	public void setIgo_express_info(String igo_express_info) {
		this.igo_express_info = igo_express_info;
	}
}
