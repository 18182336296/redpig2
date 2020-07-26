package com.redpigmall.msg;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.PopupAuthenticator;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.Template;
import com.redpigmall.domain.User;
import com.redpigmall.service.RedPigAppPushLogService;
import com.redpigmall.service.RedPigAppPushUserService;
import com.redpigmall.service.RedPigGoodsService;
import com.redpigmall.service.RedPigOrderFormService;
import com.redpigmall.service.RedPigStoreService;
import com.redpigmall.service.RedPigSysConfigService;
import com.redpigmall.service.RedPigTemplateService;
import com.redpigmall.service.RedPigUserService;

/**
 * 
 * <p>
 * Title: RedPigMsgTools.java<／p>
 * 
 * <p>
 * Description: 系统手机短信、邮件发送工具类，手机短信发送需要运营商购买短信平台提供的相关接口信息，邮件发送需要正确配置邮件服务器，
 * 运营商管理后台均有相关配置及发送测试（redpigg） <／p>
 * <p>
 * 发送短信邮件工具类 参数json数据 buyer_id:如果有买家，则买家user.id seller_id:如果有卖家,卖家的user.id
 * sender_id:发送者的user.id receiver_id:接收者的user.id order_id:如果有订单 订单order.id
 * childorder_id：如果有子订单id goods_id:商品的id self_goods: 如果是自营商品 则在邮件或者短信显示 平台名称
 * SysConfig.title,（redpig）
 * 
 * 其中收费工具类只作为商家和用户在交易中的发送工具类，发送的短信邮件均收费，需要商家在商家中心购买相应数量的短信和邮件，
 * 在短信和邮件数量允许的情况下才能发送（hezeng）
 * 
 * 2015年7月16日修改，将异步去掉，有时异步发送不能获取对应中的对应关系，导致发送短信失败
 * </p>
 * 
 * 
 * <p>
 * Copyright: Copyright (c) 2015<／p>
 * 
 * <p>
 * Company: www.redpigmall.net<／p>
 * 
 * @author redpig
 * 
 * @date 2016-4-24
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
@Component
public class RedPigMsgTools {

	@Autowired
	private RedPigSysConfigService redPigSysConfigService;
	
	@Autowired
	private RedPigSysConfigService configService;
	@Autowired
	private RedPigUserService userService;
	@Autowired
	private RedPigTemplateService templateService;
	@Autowired
	private RedPigOrderFormService orderFormService;
	@Autowired
	private RedPigGoodsService goodsService;
	@Autowired
	private RedPigStoreService storeService;
	
	@Autowired
	private RedPigAppPushUserService appPushUserService;
	@Autowired
	private RedPigAppPushLogService appPushLogService;
	
	/**
	 * 发送短信底层工具
	 * 
	 * @param mobile
	 * @param content
	 * @return
	 */
	public boolean sendSMS(String mobile, String content) {
		boolean result = true;
		if (this.redPigSysConfigService.getSysConfig().getSmsEnbale()) {
			String url = this.redPigSysConfigService.getSysConfig().getSmsURL();
			String userName = this.redPigSysConfigService.getSysConfig()
					.getSmsUserName();
			String password = this.redPigSysConfigService.getSysConfig()
					.getSmsPassword();
			RedPigSmsBase sb = new RedPigSmsBase(url, userName, password);
			String ret = sb.SendSms(mobile, content);
			if (!ret.substring(0, 3).equals("000")) {
				result = false;
			}
		} else {
			result = false;
			System.out.println("系统关闭了短信发送功能");
		}
		return result;
	}

	

	/**
	 * 收费短信发送方法，商家购买短信或者邮件后，当商家有交易订单需要发送短信提醒商家或者订单用户时使用该收费工具
	 * 
	 * @param request
	 * @param mark
	 * @param mobile
	 * @param json
	 *            :参数json，发送非订单信息的参数
	 * @param order_id
	 *            ：订单id，
	 * @throws Exception
	 */
	@Async
	@Transactional
	public void sendSmsCharge(String web, String mark, String mobile,
			String json, String order_id, String store_id) throws Exception {
		if (this.configService.getSysConfig().getSmsEnbale()) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("mark", mark);

			List<Template> templates = this.templateService
					.queryPageList(params);

			Template template = templates != null && templates.size() > 0 ? templates
					.get(0) : null;

			Store store = null;
			boolean flag = false;
			Map function_map = Maps.newHashMap();
			List<Map> function_maps = Lists.newArrayList();
			if ((store_id != null) && (!store_id.equals(""))) {
				store = this.storeService.selectByPrimaryKey(CommUtil
						.null2Long(store_id));
				if (store.getStore_sms_count() > 0) {
					function_maps = JSON.parseArray(store.getSms_email_info(),
							Map.class);
					if(function_maps != null && function_maps.size()>0){
						for (Map temp_map2 : function_maps) {
							if (template != null) {
								if (CommUtil.null2String(temp_map2.get("type"))
										.equals(CommUtil.null2String(template
												.getType()))) {
									if (CommUtil.null2String(temp_map2.get("mark"))
											.equals(template.getMark())) {
										function_map = temp_map2;
										if (CommUtil.null2Int(function_map
												.get("sms_open")) == 1) {
											flag = true;
											break;
										}
										System.out.println("商家已关闭该短信发送功能");
									}
								}
							}
						}
					}
				}
			}
			if ((flag) && (template != null) && (template.getOpen())) {
				ExpressionParser exp = new SpelExpressionParser();
				Object context = new StandardEvaluationContext();
				Map<String, Object> map = queryJson(json);
				if ((mobile != null) && (!mobile.equals(""))) {
					if (order_id != null) {
						OrderForm order = this.orderFormService
								.selectByPrimaryKey(CommUtil
										.null2Long(order_id));
						User buyer = this.userService
								.selectByPrimaryKey(CommUtil.null2Long(order
										.getUser_id()));
						((EvaluationContext) context).setVariable("buyer",
								buyer);
						if (store != null) {
							((EvaluationContext) context).setVariable("seller",
									store.getUser());
						}
						((EvaluationContext) context).setVariable("order",
								order);
					}
					if (map.get("receiver_id") != null) {
						Long receiver_id = CommUtil.null2Long(map
								.get("receiver_id"));
						User receiver = this.userService
								.selectByPrimaryKey(receiver_id);
						((EvaluationContext) context).setVariable("receiver",
								receiver);
					}
					if (map.get("sender_id") != null) {
						Long sender_id = CommUtil.null2Long(map
								.get("sender_id"));
						User sender = this.userService
								.selectByPrimaryKey(sender_id);
						((EvaluationContext) context).setVariable("sender",
								sender);
					}
					if (map.get("buyer_id") != null) {
						Long buyer_id = CommUtil.null2Long(map.get("buyer_id"));
						User buyer = this.userService
								.selectByPrimaryKey(buyer_id);
						((EvaluationContext) context).setVariable("buyer",
								buyer);
					}
					if (map.get("seller_id") != null) {
						Long seller_id = CommUtil.null2Long(map
								.get("seller_id"));
						User seller = this.userService
								.selectByPrimaryKey(seller_id);
						((EvaluationContext) context).setVariable("seller",
								seller);
					}
					if (map.get("order_id") != null) {
						Long temp_order_id = CommUtil.null2Long(map
								.get("order_id"));
						OrderForm orderForm = this.orderFormService
								.selectByPrimaryKey(temp_order_id);
						((EvaluationContext) context).setVariable("orderForm",
								orderForm);
					}
					if (map.get("childorder_id") != null) {
						Long childorder_id = CommUtil.null2Long(map
								.get("childorder_id"));
						OrderForm orderForm = this.orderFormService
								.selectByPrimaryKey(childorder_id);
						((EvaluationContext) context).setVariable(
								"child_orderForm", orderForm);
					}
					if (map.get("goods_id") != null) {
						Long goods_id = CommUtil.null2Long(map.get("goods_id"));
						Goods goods = this.goodsService
								.selectByPrimaryKey(goods_id);
						((EvaluationContext) context).setVariable("goods",
								goods);
					}
					if (map.get("self_goods") != null) {
						((EvaluationContext) context).setVariable("seller", map
								.get("self_goods").toString());
					}
					((EvaluationContext) context).setVariable("config",
							this.configService.getSysConfig());
					((EvaluationContext) context).setVariable("send_time",
							CommUtil.formatLongDate(new Date()));
					if (web != null) {
						((EvaluationContext) context).setVariable("webPath",
								web);
					}
					Expression ex = exp.parseExpression(template.getContent(),
							new SpelTemplate());
					String content = (String) ex.getValue(
							(EvaluationContext) context, String.class);
					boolean result = sendSMS(mobile, content);
					if (result) {
						System.out.println("发送短信成功");
						if (store != null) {
							store.setStore_sms_count(store.getStore_sms_count() - 1);
							function_map.put("sms_count", Integer
									.valueOf(CommUtil.null2Int(function_map
											.get("sms_count")) + 1));
							String sms_email_json = JSON
									.toJSONString(function_maps);
							store.setSend_sms_count(store.getSend_sms_count() + 1);
							store.setSms_email_info(sms_email_json);
							this.storeService.updateById(store);
						}
					}
				}
			} else {
				System.out.println("未找到模板信息：" + mark);
			}
		} else {
			System.out.println("系统关闭了短信发送功能！");
		}
	}

	/**
	 * 收费邮件发送方法，商家购买短信或者邮件后，当商家有交易订单需要发送短信提醒商家或者订单用户时使用该收费工具
	 * 
	 * @param request
	 * @param mark
	 * @param mobile
	 * @param json
	 * @param order_id
	 *            :订单操作时发送邮件
	 * @param store_id
	 *            :发送邮件的店铺id
	 * @throws Exception
	 */
	@Async
	@Transactional
	public void sendEmailCharge(String web, String mark, String email,
			String json, String order_id, String store_id) throws Exception {
		System.out.println("email:" + email);
		if (this.configService.getSysConfig().getEmailEnable()) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("mark", mark);
			List<Template> templates = this.templateService
					.queryPageList(params);

			Template template = templates != null && templates.size() > 0 ? templates
					.get(0) : null;

			Store store = null;
			boolean flag = false;
			Map function_map = Maps.newHashMap();
			List<Map> function_maps = Lists.newArrayList();
			if ((store_id != null) && (!store_id.equals(""))) {
				store = this.storeService.selectByPrimaryKey(CommUtil
						.null2Long(store_id));
				if ((store != null) && (store.getStore_email_count() > 0)) {
					function_maps = JSON.parseArray(store.getSms_email_info(),
							Map.class);
					for (Map temp_map2 : function_maps) {
						if (template != null) {
							if (CommUtil.null2String(temp_map2.get("type"))
									.equals(CommUtil.null2String(template
											.getType()))) {
								if (CommUtil.null2String(temp_map2.get("mark"))
										.equals(template.getMark())) {
									function_map = temp_map2;
									if (CommUtil.null2Int(function_map
											.get("email_open")) == 1) {
										flag = true;
										break;
									}
									flag = false;
									System.out.println("商家已关闭该邮件发送功能");
								}
							}
						}
					}
				} else {
					System.out.println("商家没有购买邮件流量");
				}
			}
			if ((flag) && (template != null) && (template.getOpen())) {
				ExpressionParser exp = new SpelExpressionParser();
				Object context = new StandardEvaluationContext();
				Map<String, Object> map = queryJson(json);
				String subject = template.getTitle();
				if (order_id != null) {
					OrderForm order = this.orderFormService
							.selectByPrimaryKey(CommUtil.null2Long(order_id));
					User buyer = this.userService.selectByPrimaryKey(CommUtil
							.null2Long(order.getUser_id()));
					((EvaluationContext) context).setVariable("buyer", buyer);
					if (store != null) {
						((EvaluationContext) context).setVariable("seller",
								store.getUser());
					}
					((EvaluationContext) context).setVariable("order", order);
				}
				if (map.get("receiver_id") != null) {
					Long receiver_id = CommUtil.null2Long(map
							.get("receiver_id"));
					User receiver = this.userService
							.selectByPrimaryKey(receiver_id);
					((EvaluationContext) context).setVariable("receiver",
							receiver);
				}
				if (map.get("sender_id") != null) {
					Long sender_id = CommUtil.null2Long(map.get("sender_id"));
					User sender = this.userService
							.selectByPrimaryKey(sender_id);
					((EvaluationContext) context).setVariable("sender", sender);
				}
				if (map.get("buyer_id") != null) {
					Long buyer_id = CommUtil.null2Long(map.get("buyer_id"));
					User buyer = this.userService.selectByPrimaryKey(buyer_id);
					((EvaluationContext) context).setVariable("buyer", buyer);
				}
				if (map.get("seller_id") != null) {
					Long seller_id = CommUtil.null2Long(map.get("seller_id"));
					User seller = this.userService
							.selectByPrimaryKey(seller_id);
					((EvaluationContext) context).setVariable("seller", seller);
				}
				if (map.get("order_id") != null) {
					Long temp_order_id = CommUtil
							.null2Long(map.get("order_id"));
					OrderForm orderForm = this.orderFormService
							.selectByPrimaryKey(temp_order_id);
					((EvaluationContext) context).setVariable("orderForm",
							orderForm);
				}
				if (map.get("childorder_id") != null) {
					Long childorder_id = CommUtil.null2Long(map
							.get("childorder_id"));
					OrderForm orderForm = this.orderFormService
							.selectByPrimaryKey(childorder_id);
					((EvaluationContext) context).setVariable(
							"child_orderForm", orderForm);
				}
				if (map.get("goods_id") != null) {
					Long goods_id = CommUtil.null2Long(map.get("goods_id"));
					Goods goods = this.goodsService
							.selectByPrimaryKey(goods_id);
					((EvaluationContext) context).setVariable("goods", goods);
				}
				if (map.get("self_goods") != null) {
					((EvaluationContext) context).setVariable("seller", map
							.get("self_goods").toString());
				}
				((EvaluationContext) context).setVariable("config",
						this.configService.getSysConfig());
				((EvaluationContext) context).setVariable("send_time",
						CommUtil.formatLongDate(new Date()));
				if (web != null) {
					((EvaluationContext) context).setVariable("webPath", web);
				}
				Expression ex = exp.parseExpression(template.getContent(),
						new SpelTemplate());
				String content = (String) ex.getValue(
						(EvaluationContext) context, String.class);
				boolean result = sendEmail(email, subject, content);
				if (result) {
					System.out.println("发送邮件成功");
					if (store != null) {
						store.setStore_email_count(store.getStore_email_count() - 1);
						function_map.put("email_count", Integer
								.valueOf(CommUtil.null2Int(function_map
										.get("email_count")) + 1));
						String sms_email_json = JSON
								.toJSONString(function_maps);
						store.setSms_email_info(sms_email_json);
						store.setSend_email_count(store.getSend_email_count() + 1);
						this.storeService.updateById(store);
					}
				}
			}
		} else {
			System.out.println("系统关闭了邮件发送功能！");
		}
	}

	/**
	 * 免费短信发送方法，系统给用户发送的短信工具，
	 * 
	 * @param request
	 * @param mark
	 * @param mobile
	 * @param json
	 * @throws Exception
	 */
	@Async
	@Transactional
	public void sendSmsFree(String web, String mark, String mobile,
			String json, String order_id)  {
		if (this.configService.getSysConfig().getSmsEnbale()) {

			Map<String, Object> params = Maps.newHashMap();
			params.put("mark", mark);

			List<Template> templates = this.templateService
					.queryPageList(params);

			Template template = templates != null && templates.size() > 0 ? templates
					.get(0) : null;

			if ((template != null) && (template.getOpen())) {
				Map<String, Object> map = queryJson(json);
				if ((mobile != null) && (!mobile.equals(""))) {
					ExpressionParser exp = new SpelExpressionParser();
					EvaluationContext context = new StandardEvaluationContext();
					if (order_id != null) {
						OrderForm order = this.orderFormService
								.selectByPrimaryKey(CommUtil
										.null2Long(order_id));
						User buyer = this.userService
								.selectByPrimaryKey(CommUtil.null2Long(order
										.getUser_id()));
						context.setVariable("buyer", buyer);
						context.setVariable("order", order);
					}
					if (map.get("receiver_id") != null) {
						Long receiver_id = CommUtil.null2Long(map
								.get("receiver_id"));
						User receiver = this.userService
								.selectByPrimaryKey(receiver_id);
						context.setVariable("receiver", receiver);
					}
					if (map.get("sender_id") != null) {
						Long sender_id = CommUtil.null2Long(map
								.get("sender_id"));
						User sender = this.userService
								.selectByPrimaryKey(sender_id);
						context.setVariable("sender", sender);
					}
					if (map.get("buyer_id") != null) {
						Long buyer_id = CommUtil.null2Long(map.get("buyer_id"));
						User buyer = this.userService
								.selectByPrimaryKey(buyer_id);
						context.setVariable("buyer", buyer);
					}
					if (map.get("seller_id") != null) {
						Long seller_id = CommUtil.null2Long(map
								.get("seller_id"));
						User seller = this.userService
								.selectByPrimaryKey(seller_id);
						context.setVariable("seller", seller);
					}
					if (map.get("order_id") != null) {
						Long order_id_temp = CommUtil.null2Long(map
								.get("order_id"));
						OrderForm orderForm = this.orderFormService
								.selectByPrimaryKey(order_id_temp);
						context.setVariable("orderForm", orderForm);
					}
					if (map.get("childorder_id") != null) {
						Long childorder_id = CommUtil.null2Long(map
								.get("childorder_id"));
						OrderForm orderForm = this.orderFormService
								.selectByPrimaryKey(childorder_id);
						context.setVariable("child_orderForm", orderForm);
					}
					if (map.get("goods_id") != null) {
						Long goods_id = CommUtil.null2Long(map.get("goods_id"));
						Goods goods = this.goodsService
								.selectByPrimaryKey(goods_id);
						context.setVariable("goods", goods);
					}
					if (map.get("self_goods") != null) {
						context.setVariable("seller", map.get("self_goods")
								.toString());
					}
					context.setVariable("config",
							this.configService.getSysConfig());
					context.setVariable("send_time",
							CommUtil.formatLongDate(new Date()));
					if (web != null) {
						context.setVariable("webPath", web);
					}
					Expression ex = exp.parseExpression(template.getContent(),
							new SpelTemplate());
					boolean ret;
					try {
						String content = (String) ex.getValue(context, String.class);
						ret = sendSMS(mobile, content);
						if (ret) {
							System.out.println("发送短信成功");
						} else {
							System.out.println("发送短信失败");
						}
					} catch (Exception e) {
						System.out.println("未开启短信服务");
					}
					
				}
			}
		} else {
			System.out.println("系统关闭了短信发送功能！");
		}
	}

	/**
	 * 免费邮件发送方法， 系统给用户发送的邮件工具，
	 * 
	 * @param request
	 * @param mark
	 * @param mobile
	 * @param json
	 * @throws Exception
	 */
	@Async
	@Transactional
	public void sendEmailFree(String web, String mark, String email,
			String json, String order_id) throws Exception {
		if (this.configService.getSysConfig().getEmailEnable()) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("mark", mark);

			List<Template> templates = this.templateService
					.queryPageList(params);

			Template template = templates != null && templates.size() > 0 ? templates
					.get(0) : null;
			if ((template != null) && (template.getOpen())) {
				Map<String, Object> map = queryJson(json);
				String subject = template.getTitle();
				ExpressionParser exp = new SpelExpressionParser();
				EvaluationContext context = new StandardEvaluationContext();
				if (order_id != null) {
					OrderForm order = this.orderFormService
							.selectByPrimaryKey(CommUtil.null2Long(order_id));
					User buyer = this.userService.selectByPrimaryKey(CommUtil
							.null2Long(order.getUser_id()));
					context.setVariable("buyer", buyer);
					context.setVariable("order", order);
				}
				if (map.get("receiver_id") != null) {
					Long receiver_id = CommUtil.null2Long(map
							.get("receiver_id"));
					User receiver = this.userService
							.selectByPrimaryKey(receiver_id);
					context.setVariable("receiver", receiver);
				}
				if (map.get("sender_id") != null) {
					Long sender_id = CommUtil.null2Long(map.get("sender_id"));
					User sender = this.userService
							.selectByPrimaryKey(sender_id);
					context.setVariable("sender", sender);
				}
				if (map.get("buyer_id") != null) {
					Long buyer_id = CommUtil.null2Long(map.get("buyer_id"));
					User buyer = this.userService.selectByPrimaryKey(buyer_id);
					context.setVariable("buyer", buyer);
				}
				if (map.get("seller_id") != null) {
					Long seller_id = CommUtil.null2Long(map.get("seller_id"));
					User seller = this.userService
							.selectByPrimaryKey(seller_id);
					context.setVariable("seller", seller);
				}
				if (map.get("order_id") != null) {
					Long order_id_temp = CommUtil
							.null2Long(map.get("order_id"));
					OrderForm orderForm = this.orderFormService
							.selectByPrimaryKey(order_id_temp);
					context.setVariable("orderForm", orderForm);
				}
				if (map.get("childorder_id") != null) {
					Long childorder_id = CommUtil.null2Long(map
							.get("childorder_id"));
					OrderForm orderForm = this.orderFormService
							.selectByPrimaryKey(childorder_id);
					context.setVariable("child_orderForm", orderForm);
				}
				if (map.get("goods_id") != null) {
					Long goods_id = CommUtil.null2Long(map.get("goods_id"));
					Goods goods = this.goodsService
							.selectByPrimaryKey(goods_id);
					context.setVariable("goods", goods);
				}
				if (map.get("self_goods") != null) {
					context.setVariable("seller", map.get("self_goods")
							.toString());
				}
				context.setVariable("config", this.configService.getSysConfig());
				context.setVariable("send_time",
						CommUtil.formatLongDate(new Date()));
				if (web != null) {
					context.setVariable("webPath", web);
				}
				Expression ex = exp.parseExpression(template.getContent(),
						new SpelTemplate());
				String content = (String) ex.getValue(context, String.class);
				sendEmail(email, subject, content);
				System.out.println("发送邮件成功");
			} else {
				System.out.println("系统关闭了邮件发送功能");
			}
		}
	}

	/**
	 * 发送邮件底层工具
	 * 
	 * @param mobile
	 * @param content
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public boolean sendEmail(String email, String subject, String content) {
		boolean ret = true;
		if (this.configService.getSysConfig().getEmailEnable()) {
			String username = "";
			String password = "";
			String smtp_server = "";
			String from_mail_address = "";
			username = this.configService.getSysConfig().getEmailUserName();
			password = this.configService.getSysConfig().getEmailPws();
			smtp_server = this.configService.getSysConfig().getEmailHost();
			from_mail_address = this.configService.getSysConfig().getEmailUser();
			String to_mail_address = email;
			if ((username != null) && (password != null)
					&& (!username.equals("")) && (!password.equals(""))
					&& (smtp_server != null) && (!smtp_server.equals(""))
					&& (to_mail_address != null)
					&& (!to_mail_address.trim().equals(""))) {
				Authenticator auth = new PopupAuthenticator(username, password);
				Properties mailProps = new Properties();
				mailProps.put("mail.smtp.auth", "true");
				mailProps.put("username", username);
				mailProps.put("password", password);
				mailProps.put("mail.smtp.host", smtp_server);
				Session mailSession = Session.getInstance(mailProps, auth);
				MimeMessage message = new MimeMessage(mailSession);
				try {
					message.setFrom(new InternetAddress(from_mail_address));
					message.setRecipient(Message.RecipientType.TO,
							new InternetAddress(to_mail_address));
					message.setSubject(subject);
					MimeMultipart multi = new MimeMultipart("related");
					BodyPart bodyPart = new MimeBodyPart();
					bodyPart.setDataHandler(new DataHandler(content,
							"text/html;charset=UTF-8"));

					multi.addBodyPart(bodyPart);
					message.setContent(multi);
					message.saveChanges();
					Transport.send(message);
					ret = true;
				} catch (AddressException e) {
					ret = false;
					e.printStackTrace();
				} catch (MessagingException e) {
					ret = false;
					e.printStackTrace();
				}
			} else {
				ret = false;
			}
		} else {
			ret = false;
			System.out.println("系统关闭了邮件发送功能");
		}
		System.out.println("ret:" + ret);
		return ret;
	}

	/**
	 * 解析json工具
	 * 
	 * @param json
	 * @return
	 */
	@Async
	@Transactional
	public void sendAppPush(String web, String mark, String user_id,
			String json, String order_id) throws Exception {
		Map<String, Object> params = Maps.newHashMap();
		params.put("mark", mark);

		List<Template> templates = this.templateService.queryPageList(params);

		Template template = templates != null && templates.size() > 0 ? templates
				.get(0) : null;
		if ((template != null) && (template.getOpen())) {
			Map<String, Object> map = queryJson(json);
			Map json_map = Maps.newHashMap();
			String subject = template.getTitle();
			ExpressionParser exp = new SpelExpressionParser();
			EvaluationContext context = new StandardEvaluationContext();
			if (order_id != null) {
				OrderForm order = this.orderFormService
						.selectByPrimaryKey(CommUtil.null2Long(order_id));
				User buyer = this.userService.selectByPrimaryKey(CommUtil
						.null2Long(order.getUser_id()));
				context.setVariable("buyer", buyer);
				context.setVariable("config", this.configService.getSysConfig());
				context.setVariable("send_time",
						CommUtil.formatLongDate(new Date()));
				context.setVariable("webPath", web);
				context.setVariable("order", order);
				json_map.put("type", "order");
				json_map.put("value", order_id);
			}
			if (map.get("receiver_id") != null) {
				Long receiver_id = CommUtil.null2Long(map.get("receiver_id"));
				User receiver = this.userService
						.selectByPrimaryKey(receiver_id);
				context.setVariable("receiver", receiver);
			}
			if (map.get("sender_id") != null) {
				Long sender_id = CommUtil.null2Long(map.get("sender_id"));
				User sender = this.userService.selectByPrimaryKey(sender_id);
				context.setVariable("sender", sender);
			}
			if (map.get("buyer_id") != null) {
				Long buyer_id = CommUtil.null2Long(map.get("buyer_id"));
				User buyer = this.userService.selectByPrimaryKey(buyer_id);
				context.setVariable("buyer", buyer);
			}
			if (map.get("seller_id") != null) {
				Long seller_id = CommUtil.null2Long(map.get("seller_id"));
				User seller = this.userService.selectByPrimaryKey(seller_id);
				context.setVariable("seller", seller);
			}
			if (map.get("order_id") != null) {
				Long order_id_temp = CommUtil.null2Long(map.get("order_id"));
				OrderForm orderForm = this.orderFormService
						.selectByPrimaryKey(order_id_temp);
				context.setVariable("orderForm", orderForm);
				json_map.put("type", "order");
				json_map.put("value", map.get("order_id"));
			}
			if (map.get("childorder_id") != null) {
				Long childorder_id = CommUtil.null2Long(map
						.get("childorder_id"));
				OrderForm orderForm = this.orderFormService
						.selectByPrimaryKey(childorder_id);
				context.setVariable("child_orderForm", orderForm);
			}
			if (map.get("goods_id") != null) {
				Long goods_id = CommUtil.null2Long(map.get("goods_id"));
				Goods goods = this.goodsService.selectByPrimaryKey(goods_id);
				context.setVariable("goods", goods);
			}
			if (map.get("self_goods") != null) {
				context.setVariable("seller", map.get("self_goods").toString());
			}
			context.setVariable("config", this.configService.getSysConfig());
			context.setVariable("send_time",
					CommUtil.formatLongDate(new Date()));
			context.setVariable("webPath", web);
			Expression ex = exp.parseExpression(template.getContent(),
					new SpelTemplate());
			String content = (String) ex.getValue(context, String.class);
			params.clear();
			params = Maps.newHashMap();

			params.put("user_id", user_id);

		}
	}

	private Map queryJson(String json) {
		Map<String, Object> map = Maps.newHashMap();
		if ((json != null) && (!json.equals(""))) {
			map = JSON.parseObject(json);
		}
		return map;
	}

}
