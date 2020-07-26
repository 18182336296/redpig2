package com.redpigmall.domain;

import java.util.Date;

public class AppVersion {
    private Long id;

    private Date addTime;

    private String app_version; // app版本号

    private String update_info; // 更新内容

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

	public String getApp_version() {
		return app_version;
	}

	public void setApp_version(String app_version) {
		this.app_version = app_version;
	}

	public String getUpdate_info() {
		return update_info;
	}

	public void setUpdate_info(String update_info) {
		this.update_info = update_info;
	}

}