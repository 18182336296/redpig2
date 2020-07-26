package com.redpigmall.corework.log;

import java.lang.reflect.Method;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.ip.IPSeeker;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.LogFieldType;
import com.redpigmall.domain.SysLog;
import com.redpigmall.domain.User;
import com.redpigmall.service.RedPigSysLogService;
import com.redpigmall.service.RedPigUserService;

/**
 * 
 * <p>
 * Title: SystemLogAdvice.java
 * </p>
 * 
 * <p>
 * Description: 系统日志管理类，这里使用Spring环绕通知和异常通知进行动态管理,系统只记录管理员操作记录，对访问列表不进行记录
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
 * @date 2016-9-16
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Aspect
@Component
public class SystemLogAdvice {
	
	@Autowired
	private RedPigSysLogService sysLogService;
	
	@Autowired
	private RedPigUserService userSerivce;
	
	/**
	 * 记录管理员操作日志
	 * @param joinPoint
	 * @param annotation
	 * @param request
	 * @throws Exception
	 */
	@After("execution(* com.redpigmall.manage.admin..*.*(..)) && @annotation(annotation)&&args(request,..)")
	public void admin_op_log(JoinPoint joinPoint, SecurityMapping annotation,
			HttpServletRequest request) throws Exception {
		saveLog(joinPoint, annotation, request);
	}
	
	/**
	 * 微信操作日志记录
	 * @param joinPoint
	 * @param annotation
	 * @param request
	 * @throws Exception
	 */
	@After("execution(* com.redpigmall.module.weixin.manage..*.*(..)) && @annotation(annotation)&&args(request,..)")
	public void admin_weixin_log(JoinPoint joinPoint,
			SecurityMapping annotation, HttpServletRequest request)
			throws Exception {
		saveLog(joinPoint, annotation, request);
	}
	
	/**
	 * 记录app操作日志
	 * @param joinPoint
	 * @param annotation
	 * @param request
	 * @throws Exception
	 */
	
	@After("execution(* com.redpigmall.module.app.manage..*.*(..)) && @annotation(annotation)&&args(request,..)")
	public void admin_app_log(JoinPoint joinPoint, SecurityMapping annotation,
			HttpServletRequest request) throws Exception {
		saveLog(joinPoint, annotation, request);
	}
	
	/**
	 * 保存cms日志
	 * @param joinPoint
	 * @param annotation
	 * @param request
	 * @throws Exception
	 */
	@After("execution(* com.redpigmall.module.cms.manage..*.*(..)) && @annotation(annotation)&&args(request,..)")
	public void admin_cms_log(JoinPoint joinPoint, SecurityMapping annotation,
			HttpServletRequest request) throws Exception {
		saveLog(joinPoint, annotation, request);
	}

	/**
	 * 保存圈子日志
	 * @param joinPoint
	 * @param annotation
	 * @param request
	 * @throws Exception
	 */
	@After("execution(* com.redpigmall.module.circle.manage..*.*(..)) && @annotation(annotation)&&args(request,..)")
	public void admin_circle_log(JoinPoint joinPoint,
			SecurityMapping annotation, HttpServletRequest request)
			throws Exception {
		saveLog(joinPoint, annotation, request);
	}
	
	/**
	 * 保存sns日志
	 * @param joinPoint
	 * @param annotation
	 * @param request
	 * @throws Exception
	 */
	@After("execution(* com.redpigmall.module.sns.manage..*.*(..)) && @annotation(annotation)&&args(request,..)")
	public void admin_sns_log(JoinPoint joinPoint, SecurityMapping annotation,
			HttpServletRequest request) throws Exception {
		saveLog(joinPoint, annotation, request);
	}
	/**
	 * 保存日志
	 * @param joinPoint
	 * @param annotation
	 * @param request
	 * @throws Exception
	 */
	private void saveLog(JoinPoint joinPoint, SecurityMapping annotation,
			HttpServletRequest request) throws Exception {
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userSerivce.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());

			String id = request.getParameter("id");
			String mulitId = request.getParameter("mulitId");
			String userName = SecurityUserHolder.getCurrentUser().getUserName();
			if (SecurityUserHolder.getCurrentUser().getTrueName() != null) {
				userName =

				userName + "（"
						+ SecurityUserHolder.getCurrentUser().getTrueName()
						+ "）";
			}
			String description = userName + "于"
					+ CommUtil.formatTime("yyyy-MM-dd HH:mm:ss", new Date());
			if (annotation.value().indexOf("index") > 0) {
				description = description + "管理员登录";
			} else if (annotation.value().indexOf("admin_pws_save") > 0) {
				String pws = request.getParameter("password");
				description = description + "修改密码为" + pws.substring(0, 1)
						+ "*****";

				String current_ip = CommUtil.getIpAddr(request);
				String ip_city = "未知地区";
				if (CommUtil.isIp(current_ip)) {
					IPSeeker ip = new IPSeeker(null, null);
					ip_city = ip.getIPLocation(current_ip).getCountry();
				}
				SysLog log = new SysLog();
				log.setTitle("管理员操作");
				log.setType(0);
				log.setAddTime(new Date());
				log.setUser_name(user.getUserName());
				log.setContent(description);
				log.setIp(current_ip);
				log.setIp_city(ip_city);
				this.sysLogService.saveEntity(log);
			} else if ((id != null) || (mulitId != null)
					|| (annotation.value().indexOf("save") >= 0)
					|| (annotation.value().indexOf("edit") >= 0)
					|| (annotation.value().indexOf("update") >= 0)) {
				String option1 = "执行";
				String option2 = "操作";
				description = description + option1 + annotation.title()
						+ option2;
				if (((request.getParameter("mulitId") != null ? 1 : 0) & (""
						.equals(request.getParameter("mulitId")) ? 0 : 1)) != 0) {
					description = description + "。操作数据id为："
							+ request.getParameter("mulitId");
				} else if ((request.getParameter("id") != null)
						&& (!"".equals(request.getParameter("id")))) {
					description = description + "。操作数据id为："
							+ request.getParameter("id");
				}
				String current_ip = CommUtil.getIpAddr(request);
				String ip_city = "未知地区";
				if (CommUtil.isIp(current_ip)) {
					IPSeeker ip = new IPSeeker(null, null);
					ip_city = ip.getIPLocation(current_ip).getCountry();
				}
				SysLog log = new SysLog();
				log.setTitle("管理员操作");
				log.setType(0);
				log.setAddTime(new Date());
				log.setUser_name(user.getUserName());
				log.setContent(description);
				log.setIp(current_ip);
				log.setIp_city(ip_city);
				System.out.println("保存管理平台操作日志");
				this.sysLogService.saveEntity(log);
			}
		}
	}
	
	/**
	 * 保存异常日志
	 * @param request
	 * @param exception
	 */
	@AfterThrowing(value = "execution(* com.redpigmall.manage.admin..*.*(..))&&args(request,..) ", throwing = "exception")
	public void exceptionLog(HttpServletRequest request, Throwable exception) {
		if (SecurityUserHolder.getCurrentUser() != null) {
			try {
				User user = this.userSerivce.selectByPrimaryKey(SecurityUserHolder
						.getCurrentUser().getId());
				String current_ip = CommUtil.getIpAddr(request);
				String ip_city = "未知地区";
				if (CommUtil.isIp(current_ip)) {
					IPSeeker ip = new IPSeeker(null, null);
					ip_city = ip.getIPLocation(current_ip).getCountry();
				}
				SysLog log = new SysLog();
				log.setTitle("系统异常");
				log.setType(1);
				log.setAddTime(new Date());
				log.setUser_name(user.getUserName());
				log.setContent(request.getRequestURI() + "时出现异常，异常代码为:"
						+ exception.getMessage());
				log.setIp(current_ip);
				log.setIp_city(ip_city);
				this.sysLogService.saveEntity(log);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("保存异常日志出错!");
			}
		}
	}

	public void loginLog() {
		System.out.println("用户登录");
	}

	@SuppressWarnings({ "unused", "rawtypes" })
	private Method getMethod(ProceedingJoinPoint joinPoint) {
		MethodSignature joinPointObject = (MethodSignature) joinPoint
				.getSignature();

		Method method = joinPointObject.getMethod();

		String name = method.getName();
		Class[] parameterTypes = method.getParameterTypes();

		Object target = joinPoint.getTarget();
		try {
			method = target.getClass().getMethod(name, parameterTypes);
		} catch (SecurityException e) {
			method = null;
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return method;
	}

	public String adminOptionContent(Object[] args, String mName)
			throws Exception {
		if (args == null) {
			return null;
		}
		StringBuffer rs = new StringBuffer();
		rs.append(mName);
		String className = null;
		int index = 1;
		for (Object info : args) {
			
			className = info.getClass().getName();
			className = className.substring(className.lastIndexOf(".") + 1);
			boolean cal = false;
			LogFieldType[] types = LogFieldType.values();
			
			
			for (LogFieldType type : types) {
				
				if (type.toString().equals(className)) {
					cal = true;
				}
			}
			if (cal) {
				rs.append("[参数" + index + "，类型：" + className + "，值：");

				Method[] methods = info.getClass().getDeclaredMethods();
				
				for (Method method : methods) {
					String methodName = method.getName();
					if (methodName.indexOf("get") != -1) {
						Object rsValue = null;
						try {
							rsValue = method.invoke(info, new Object[0]);
							if (rsValue != null) {
								rs.append("(" + methodName + " : "
										+ rsValue.toString() + ")");
							}
						} catch (Exception e) {
						}
					}
				}
				rs.append("]");
				index++;
			}
		}
		return rs.toString();
	}
}
