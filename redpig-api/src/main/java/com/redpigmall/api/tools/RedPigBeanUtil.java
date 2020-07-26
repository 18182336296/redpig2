//package com.redpigmall.api.tools;
//
//import com.redpigmall.dao.SupperMapper;
//
///**
// * Spring Bean工具类
// * @author RedPigMall
// *
// */
//public class RedPigBeanUtil {
//	/**
//	 * 根据domain类名来解析得到对应的mapper
//	 * @param clazz
//	 * @return
//	 */
//	@SuppressWarnings("rawtypes")
//	public static SupperMapper getMapper(Class clazz){
//	    String relMapperName = 
//	    		clazz.getSimpleName().substring(0,1).toLowerCase()+
//	    		clazz.getSimpleName().substring(1)+"Mapper";
//	    
//	    return  (SupperMapper) SpringUtil.getBean(relMapperName);
//	}
//	
//	
//	
// }
