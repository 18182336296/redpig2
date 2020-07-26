package com.redpigmall.api.tools.cache;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * <p>
 * Title: GenerateVelocityStatisHTML.java
 * </p>
 * 
 * <p>
 * Description:生成静态文件
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2017-3-21
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
public class GenerateVelocityStatisHTML {
//	static {
//
//		try {
//			Properties prop = new Properties();
//			
//			prop.load(Globals.class.getResourceAsStream("/velocity.properties"));
//			// 指定模板文件存放位置
//			prop.put("file.resource.loader.path",Globals.static_folder+  File.separator+"vm");
//			
//			Velocity.init(prop);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * 
	 * @param context 
	 * 		velocity上下文环境
	 * @param vm 
	 * 		模板:绝对路径
	 * @param filePath 
	 * 		生成的目标html文件,绝对路径
	 */
	public static void generateHTML(VelocityContext context, String vm,String filePath) {
		try {
			// 取得给定位置的模板
			Template template = Velocity.getTemplate(vm);
			File file = new File(filePath);
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			BufferedWriter bw = new BufferedWriter(osw);
			// 模板与context里的属性相结合写入输出流中
			template.merge(context, bw);
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param velocityParams 
	 * 		模板中需要用到的数据
	 * @param vm 
	 * 		模板:绝对路径
	 * @param filePath 
	 * 		生成的目标html文件,绝对路径
	 */
	public static void generate(Map<String, Object> velocityParams,String vm, String filePath) {
		try {

			// VelocityContext这个类按照我的理解与request的作用是一样的
			VelocityContext context = new VelocityContext();

			Set<String> keys = velocityParams.keySet();

			for (String key : keys) {
				context.put(key, velocityParams.get(key));
			}

			GenerateVelocityStatisHTML.generateHTML(context, vm, filePath);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

}
