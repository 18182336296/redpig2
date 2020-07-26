package com.redpigmall.view.web.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.redpigmall.api.tools.CommUtil;

/**
 * 
 * <p>
 * Title: MoneyUtil.java
 * </p>
 * 
 * <p>
 * Description:货币显示处理工具类,包含以下内容： 1、四舍五入求值 2、针对不同的格式化要求：万，百万，亿等
 * 3、会计格式的货币值：添加','符号 4、非科学计数法的货币值
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
 * @date 2014-8-26
 * 
 * @version redpigmall_b2b2c v8.0 2016版 
 */
public class MoneyUtil {
	/**
	 * @title 获取格式化的人民币（四舍五入）
	 * @author chanson
	 * @param money
	 *            待处理的人民币
	 * @param scale
	 *            小数点后保留的位数
	 * @param divisor
	 *            格式化值（万，百万，亿等等）
	 * @return
	 */
	public String getFormatMoney(Object money, int scale, double divisor) {
		if (divisor == 0.0D) {
			return "0.00";
		}
		if (scale < 0) {
			return "0.00";
		}
		BigDecimal moneyBD = new BigDecimal(CommUtil.null2Double(money));
		BigDecimal divisorBD = new BigDecimal(divisor);

		return moneyBD.divide(divisorBD, scale, RoundingMode.HALF_UP)
				.toString();
	}
	/**
	 * @title 获取会计格式的人民币（四舍五入）——添加会计标识：','
	 * @author chanson
	 * @param money
	 *            待处理的人民币
	 * @param scale
	 *            小数点后保留的位数
	 * @param divisor
	 *            格式化值（万，百万，亿等等）
	 * @return
	 */
	public String getAccountantMoney(Object money, int scale, double divisor) {
		String disposeMoneyStr = getFormatMoney(money, scale, divisor);

		int dotPosition = disposeMoneyStr.indexOf(".");
		String exceptDotMoeny = null;
		String dotMeony = null;
		if (dotPosition > 0) {
			exceptDotMoeny = disposeMoneyStr.substring(0, dotPosition);
			dotMeony = disposeMoneyStr.substring(dotPosition);
		} else {
			exceptDotMoeny = disposeMoneyStr;
		}
		int negativePosition = exceptDotMoeny.indexOf("-");
		if (negativePosition == 0) {
			exceptDotMoeny = exceptDotMoeny.substring(1);
		}
		StringBuffer reverseExceptDotMoney = new StringBuffer(exceptDotMoeny);
		reverseExceptDotMoney.reverse();

		char[] moneyChar = reverseExceptDotMoney.toString().toCharArray();
		StringBuffer returnMeony = new StringBuffer();
		for (int i = 0; i < moneyChar.length; i++) {
			if ((i != 0) && (i % 3 == 0)) {
				returnMeony.append(",");
			}
			returnMeony.append(moneyChar[i]);
		}
		returnMeony.reverse();
		if (dotPosition > 0) {
			returnMeony.append(dotMeony);
		}
		if (negativePosition == 0) {
			return "-" + returnMeony.toString();
		}
		return returnMeony.toString();
	}
	/**
	 * @title 字符串倒转方法
	 * @detail 字符串倒转方法
	 * @author chanson
	 * @param oldStr
	 */
	public static void testmai(String[] args) {
		BigDecimal money = BigDecimal.valueOf(8.52145218E7D);
		int scale = 2;
		double divisor = 10000.0D;
		System.out.println("原货币值: " + money);
		MoneyUtil util = new MoneyUtil();

		String formatMeony = util.getFormatMoney(money, scale, divisor);
		System.out.println("格式化货币值: " + formatMeony + "万元");
		String accountantMoney = util.getAccountantMoney(money, scale, divisor);
		System.out.println("会计货币值: " + accountantMoney + "万元");
		System.out.println(CommUtil.null2Float(Double.valueOf(8000.8D)));
	}
}
