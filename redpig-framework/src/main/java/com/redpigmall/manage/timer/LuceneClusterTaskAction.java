package com.redpigmall.manage.timer;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.redpigmall.logic.service.RedPigLuceneClusterService;

//import com.redpigmall.logic.service.RedPigLuceneClusterService;

/**
 * <p>
 * Title: LuceneClusterTaskAction.java
 * </p>
 * 
 * <p>
 * Description:LuceneClusterTaskAction
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
 * @date 2014-5-30
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Component("luceneClusterTaskAction")
public class LuceneClusterTaskAction {
	
	@Autowired
	private RedPigLuceneClusterService quartzService;
	
	public void execute() throws Exception {
		System.out.println("开始执行lucene cluster");
		try {
			quartzService.updateLuceneCluster();
			System.out.println("LuceneClusterTaskAction");
		} catch (Exception e) {
			
		}
	}
}
