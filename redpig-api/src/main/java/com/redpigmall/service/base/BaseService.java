package com.redpigmall.service.base;

import java.util.List;
import java.util.Map;

import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.query.support.IQuery;

@SuppressWarnings({"rawtypes","serial"})
public abstract class BaseService<T> {
	
	public T queryByProperty(Map<String,Object> params){
		return null;
	}
	
	public abstract T selectByPrimaryKey(Long id);
	
	public List<T> queryPageList(Map<String, Object> params,Integer begin,Integer max){
		
		if(begin !=null && max !=null){
			params.put("currentPage", begin);
			params.put("pageSize", max);
		}
		
		return this.queryPages(params);
		
	}
	
	public  List<T> queryPageListWithNoRelations(Map<String, Object> params,Integer begin,Integer max){
		
		if(begin !=null && max !=null){
			params.put("currentPage", begin);
			params.put("pageSize", max);
		}
		
		return this.queryPageListWithNoRelations(params);
		
	}
	
	public abstract  List<T> queryPages(Map<String, Object> params);
	
	public abstract  List<T> queryPageListWithNoRelations(Map<String, Object> params);
	
	public  List<T> listByNoRelation(Map<String, Object> params,Integer begin,Integer max){
		return null;
	}
	
	public abstract int selectCount(Map<String, Object> params);
	
	@Deprecated
	public List<T> queryList(Map<String, Object> params,Integer begin,Integer max){
		return null;
	}
	
	public IPageList list2(final Map<String, Object> params) {
		
		return new IPageList() {
			

			public void setQuery(IQuery paramIQuery) {
				
			}
			
			/**
			 * 返回查询总记录数
			 */

			public int getRowCount() {
				return selectCount(params);
			}
			
			/**
			 * 数据
			 */

			public List getResult() {
				return queryList(params,Integer.valueOf(getCurrentPage()),getPageSize());
			}
			
			/**
			 * 返回查询结果总页数
			 */

			public int getPages() {
				
				int rowCount = getRowCount();
				int page = 0;
				if(rowCount % getPageSize() > 0 ){
					page = rowCount / getPageSize() + 1;
				}else{
					page = rowCount / getPageSize() ;
				}
				
				return page;
			}
			
			/**
			 * 每页记录数
			 */

			public int getPageSize() {
				return (int) params.get("pageSize");
			}
			
			/**
			 * 当前页码
			 */

			public int getCurrentPage() {
				String current = String.valueOf(params.get("src_currentPage"));
				return Integer.valueOf(current) * getPageSize();
			}
			

			public void doList(int pageSize, int pageNo, String totalSQL,String construct, String queryHQL, Map params) {
			}
			

			public void doList(int pageSize, int pageNo, String totalSQL,String construct, String queryHQL) {
			}
		};
	}
	
	
	public IPageList list(final Map<String, Object> params) {
		
		return new IPageList() {
			

			public void setQuery(IQuery paramIQuery) {
				
			}
			
			/**
			 * 返回查询总记录数
			 */

			public int getRowCount() {
				return selectCount(params);
			}
			
			/**
			 * 数据
			 */

			public List getResult() {
				return queryPageList(params,Integer.valueOf(getCurrentPage()),getPageSize());
			}
			
			/**
			 * 返回查询结果总页数
			 */

			public int getPages() {
				
				int rowCount = getRowCount();
				int page = 0;
				if(rowCount % getPageSize() > 0 ){
					page = rowCount / getPageSize() + 1;
				}else{
					page = rowCount / getPageSize() ;
				}
				
				return page;
			}
			
			/**
			 * 每页记录数
			 */

			public int getPageSize() {
				return (int) params.get("pageSize");
			}
			
			/**
			 * 当前页码
			 */

			public int getCurrentPage() {
				String current = String.valueOf(params.get("src_currentPage"));
				return Integer.valueOf(current) * getPageSize();
			}
			

			public void doList(int pageSize, int pageNo, String totalSQL,String construct, String queryHQL, Map params) {
			}
			

			public void doList(int pageSize, int pageNo, String totalSQL,String construct, String queryHQL) {
			}
		};
	}
	
	
	
	public IPageList listPages(final Map<String, Object> params) {
		
		return new IPageList() {
			

			public void setQuery(IQuery paramIQuery) {
				
			}
			
			/**
			 * 返回查询总记录数
			 */

			public int getRowCount() {
				return selectCount(params);
			}
			
			/**
			 * 数据
			 */

			public List getResult() {
				return queryPages(params);
			}
			
			/**
			 * 返回查询结果总页数
			 */

			public int getPages() {
				
				int rowCount = getRowCount();
				int page = 0;
				if(rowCount % getPageSize() > 0 ){
					page = rowCount / getPageSize() + 1;
				}else{
					page = rowCount / getPageSize() ;
				}
				
				return page;
			}
			
			/**
			 * 每页记录数
			 */

			public int getPageSize() {
				return (int) params.get("pageSize");
			}
			
			/**
			 * 当前页码
			 */

			public int getCurrentPage() {
				String current = String.valueOf(params.get("src_currentPage"));
				
				return Integer.valueOf(current).intValue() ;
			}
			

			public void doList(int pageSize, int pageNo, String totalSQL,String construct, String queryHQL, Map params) {
			}
			

			public void doList(int pageSize, int pageNo, String totalSQL,String construct, String queryHQL) {
			}
		};
	}
	
	/**
	 * 不加载实体类中的关联对象
	 * @param params
	 * @return
	 */
	public IPageList listByNoRelation(final Map<String, Object> params) {
		
		return new IPageList() {
			

			public void setQuery(IQuery paramIQuery) {
				
			}
			
			/**
			 * 返回查询总记录数
			 */

			public int getRowCount() {
				return selectCount(params);
			}
			
			/**
			 * 数据
			 */

			public List getResult() {
				return listByNoRelation(params,Integer.valueOf(getCurrentPage()),getPageSize());
			}
			
			/**
			 * 返回查询结果总页数
			 */

			public int getPages() {
				
				int rowCount = getRowCount();
				int page = 0;
				if(rowCount % getPageSize() > 0 ){
					page = rowCount / getPageSize() + 1;
				}else{
					page = rowCount / getPageSize() ;
				}
				
				return page;
			}
			
			/**
			 * 每页记录数
			 */

			public int getPageSize() {
				return (int) params.get("pageSize");
			}
			
			/**
			 * 当前页码
			 */

			public int getCurrentPage() {
				String current = String.valueOf(params.get("src_currentPage"));
				return Integer.valueOf(current) * getPageSize();
			}
			

			public void doList(int pageSize, int pageNo, String totalSQL,String construct, String queryHQL, Map params) {
			}
			

			public void doList(int pageSize, int pageNo, String totalSQL,String construct, String queryHQL) {
			}
		};
	}
	
	
	public IPageList queryPagesWithNoRelations(final Map<String, Object> params) {
		
		return new IPageList() {
			

			public void setQuery(IQuery paramIQuery) {
				
			}
			
			/**
			 * 返回查询总记录数
			 */

			public int getRowCount() {
				return selectCount(params);
			}
			
			/**
			 * 数据
			 */

			public List getResult() {
				return queryPagesWithNoRelations(params, null, null);
			}
			
			/**
			 * 返回查询结果总页数
			 */

			public int getPages() {
				
				int rowCount = getRowCount();
				int page = 0;
				if(rowCount % getPageSize() > 0 ){
					page = rowCount / getPageSize() + 1;
				}else{
					page = rowCount / getPageSize() ;
				}
				
				return page;
			}
			
			/**
			 * 每页记录数
			 */

			public int getPageSize() {
				return (int) params.get("pageSize");
			}
			
			/**
			 * 当前页码
			 */

			public int getCurrentPage() {
				String current = String.valueOf(params.get("src_currentPage"));
				return Integer.valueOf(current).intValue() ;
			}
			

			public void doList(int pageSize, int pageNo, String totalSQL,String construct, String queryHQL, Map params) {
			}
			

			public void doList(int pageSize, int pageNo, String totalSQL,String construct, String queryHQL) {
			}
		};
	}

	public  List queryPagesWithNoRelations(Map<String, Object> params,Integer currentPage, Integer pageSize) {
		return null;
	}

}
