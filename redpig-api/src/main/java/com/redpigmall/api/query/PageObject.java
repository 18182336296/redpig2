package com.redpigmall.api.query;

/**
 * 
 * <p>
 * Title: PageObject.java
 * </p>
 * 
 * <p>
 * Description:包装分页信息
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
 * @date 2014-4-24
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
public class PageObject {
	private Integer currentPage = Integer.valueOf(-1);
	private Integer pageSize = Integer.valueOf(-1);

	public Integer getCurrentPage() {
		if (this.currentPage == null) {
			this.currentPage = Integer.valueOf(-1);
		}
		return this.currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		if (currentPage == null) {
			currentPage = Integer.valueOf(-1);
		}
		this.currentPage = currentPage;
	}

	public Integer getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}
