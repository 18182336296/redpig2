package com.redpigmall.manage.timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.redpigmall.logic.service.RedPigQuartzService;

@Component("shop_job")
public class JobManageAction {
	@Autowired
	private RedPigQuartzService quartzService;

	public void execute() {
		try {
			this.quartzService.oneDay_1();
		} catch (Exception localException) {
		}
		try {
			this.quartzService.oneDay_2();
		} catch (Exception localException1) {
		}
		try {
			this.quartzService.oneDay_3();
		} catch (Exception localException2) {
		}
		try {
			this.quartzService.oneDay_4();
		} catch (Exception localException3) {
		}
		try {
			this.quartzService.oneDay_5();
		} catch (Exception localException4) {
		}
		try {
			this.quartzService.oneDay_6();
		} catch (Exception localException5) {
		}
		try {
			this.quartzService.oneDay_7();
		} catch (Exception localException6) {
		}
		try {
			this.quartzService.oneDay_8();
		} catch (Exception localException7) {
		}
		try {
			this.quartzService.oneDay_9();
		} catch (Exception localException8) {
		}
	}
}
