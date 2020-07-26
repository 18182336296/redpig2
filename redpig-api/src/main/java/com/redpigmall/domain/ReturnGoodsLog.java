package com.redpigmall.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: ReturnGoodsLog.java
 * </p>
 * 
 * <p>
 * Description: 退货商品日志类,用来记录所有退货操作
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
 * @date 2014-5-12
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "returngoods_log")
public class ReturnGoodsLog extends IdEntity {

	private Long return_order_num; //退款订单订单号

	private String goods_gsp_val;//商品规格值
	private String goods_gsp_ids;//商品规格ID主键
	/**
	 * 套装退款详情，使用json管理，
	 * {
	 * 	"suit_count":2,
	 * 	"plan_goods_price":"429",
	 * 	"all_goods_price":"236.00",
	 * 	"goods_list":
	 * 		[{
	 * 			"id":92,
	 * 			"price":25.0,
	 * 			"inventory":465765,
	 * 			"store_price":25.0,
	 * 			"name":"RAYLI 韩版时尚潮帽 百变女帽子围脖两用 可爱球球保暖毛线 HA048",
	 * 			"img":"upload/system/self_goods/a7c137ef-0933-4c72-8be6-e7eb7fdfb3c7.jpg_small.jpg",
	 * 			"url":"http://localhost/goods_92.htm"
	 * 		}],
	 * 	"suit_all_price":"429.00"}
	 */
	@Column(columnDefinition = "LongText")
	private String return_combin_info;//
	private String return_service_id; // 退货服务单号
	private Long goods_id;// 退货的商品id
	private String goods_name;// 商品的名称
	private String goods_count;// 商品数量
	private String goods_price;// 商品价格
	private String goods_all_price;// 商品总价格
	private String goods_mainphoto_path;// 商品图片路径
	@Column(precision = 12, scale = 2)
	private BigDecimal goods_commission_rate;// 退货商品的佣金比例
	/**
	 * 退货商品状态 
	 * -2为超过退货时间未能输入退货物流 
	 * -1为申请被拒绝  
	 *  1为可以退货 
	 *  5为退货申请中 
	 *  6为审核通过可进行退货 
	 *  7为退货中 
	 *  10为退货完成,等待退款，
	 *  11为平台退款完成
	 */
	private String goods_return_status;// 退货商品状态 -2为超过退货时间未能输入退货物流 -1为申请被拒绝  1为可以退货 5为退货申请中 6为审核通过可进行退货 7为退货中  10为退货完成,等待退款，11为平台退款完成
	private long return_order_id;// 商品对应的订单id主键
	private int goods_type;// 商品的类型 0为自营 1为第三方经销商
	private String return_content;// 退货描述
	private String user_name;// 申请退货的用户姓名
	private Long store_id; // 商品对应的店铺id
	private Long user_id;// 所属用户id
	private String self_address;// 收货时向买家发送的收货地址，买家通过此将货物发送给卖家
	@Column(columnDefinition = "LongText")
	private String return_express_info;// 退货物流公司信息json{"express_company_id":1,"express_company_name":"顺丰快递","express_company_mark":"shunfeng","express_company_type":"EXPRESS"}
	private String express_code;// 快递号
	@Column(columnDefinition = "int default 0")
	private int refund_status;// 退款状态 0为未退款 1为退款完成
	public Long getReturn_order_num() {
		return this.return_order_num;
	}

	public void setReturn_order_num(Long return_order_num) {
		this.return_order_num = return_order_num;
	}

	public String getReturn_combin_info() {
		return this.return_combin_info;
	}

	public void setReturn_combin_info(String return_combin_info) {
		this.return_combin_info = return_combin_info;
	}

	public ReturnGoodsLog() {
	}

	public ReturnGoodsLog(Long id, Date addTime) {
		super(id, addTime);
	}

	public String getGoods_gsp_ids() {
		return this.goods_gsp_ids;
	}

	public void setGoods_gsp_ids(String goods_gsp_ids) {
		this.goods_gsp_ids = goods_gsp_ids;
	}

	public Long getStore_id() {
		return this.store_id;
	}

	public String getGoods_gsp_val() {
		return this.goods_gsp_val;
	}

	public void setGoods_gsp_val(String goods_gsp_val) {
		this.goods_gsp_val = goods_gsp_val;
	}

	public void setStore_id(Long store_id) {
		this.store_id = store_id;
	}

	public BigDecimal getGoods_commission_rate() {
		return this.goods_commission_rate;
	}

	public void setGoods_commission_rate(BigDecimal goods_commission_rate) {
		this.goods_commission_rate = goods_commission_rate;
	}

	public int getRefund_status() {
		return this.refund_status;
	}

	public void setRefund_status(int refund_status) {
		this.refund_status = refund_status;
	}

	public String getReturn_express_info() {
		return this.return_express_info;
	}

	public void setReturn_express_info(String return_express_info) {
		this.return_express_info = return_express_info;
	}

	public String getReturn_service_id() {
		return this.return_service_id;
	}

	public void setReturn_service_id(String return_service_id) {
		this.return_service_id = return_service_id;
	}

	public Long getUser_id() {
		return this.user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getExpress_code() {
		return this.express_code;
	}

	public void setExpress_code(String express_code) {
		this.express_code = express_code;
	}

	public String getSelf_address() {
		return this.self_address;
	}

	public void setSelf_address(String self_address) {
		this.self_address = self_address;
	}

	public String getUser_name() {
		return this.user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getReturn_content() {
		return this.return_content;
	}

	public void setReturn_content(String return_content) {
		this.return_content = return_content;
	}

	public int getGoods_type() {
		return this.goods_type;
	}

	public void setGoods_type(int goods_type) {
		this.goods_type = goods_type;
	}

	public long getReturn_order_id() {
		return this.return_order_id;
	}

	public void setReturn_order_id(long return_order_id) {
		this.return_order_id = return_order_id;
	}

	public Long getGoods_id() {
		return this.goods_id;
	}

	public void setGoods_id(Long goods_id) {
		this.goods_id = goods_id;
	}

	public String getGoods_name() {
		return this.goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getGoods_count() {
		return this.goods_count;
	}

	public void setGoods_count(String goods_count) {
		this.goods_count = goods_count;
	}

	public String getGoods_price() {
		return this.goods_price;
	}

	public void setGoods_price(String goods_price) {
		this.goods_price = goods_price;
	}

	public String getGoods_all_price() {
		return this.goods_all_price;
	}

	public void setGoods_all_price(String goods_all_price) {
		this.goods_all_price = goods_all_price;
	}

	public String getGoods_mainphoto_path() {
		return this.goods_mainphoto_path;
	}

	public void setGoods_mainphoto_path(String goods_mainphoto_path) {
		this.goods_mainphoto_path = goods_mainphoto_path;
	}

	public String getGoods_return_status() {
		return this.goods_return_status;
	}

	public void setGoods_return_status(String goods_return_status) {
		this.goods_return_status = goods_return_status;
	}
}
