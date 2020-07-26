package com.redpigmall.service;


import com.redpigmall.api.enums.Lang;
import com.redpigmall.dao.CollageListMapper;
import com.redpigmall.domain.CollageBuy;
import com.redpigmall.domain.UserCollageBuyInfo;
import com.redpigmall.domain.UserCollageList;
import com.redpigmall.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class RedPigCollageListService extends BaseService<CollageBuy>  {
	
	
	@Autowired
	private CollageListMapper collageListMapper;
	private Long id;


	/**
	 * 团拼实现
	 * @param maps
	 * @return
	 */
	public List<UserCollageList> selectObjByCollage(Map<String, Object> maps) {
		return collageListMapper.selectObjByCollage(maps);
	}


	/**
	 * 团拼列表
	 * @param maps
	 * @return
	 */
	public List<UserCollageBuyInfo> selectUserCollageBuyInfo(Map<String, Object> maps) {
		return collageListMapper.selectUserCollageBuyInfo(maps);
	}

	/**
	 * 拼团人数统计
	 * @param id
	 * @param o
     * @param w
     * @return
	 */
	public Integer selectCounts(Map<String, Integer> maps) {

        return collageListMapper.selectCounts(maps);
	}

	/**
	 *  得到拼团对象
	 * @param id
	 * @return
	 */

	public UserCollageList selectByPrimaryKeys(Long id){

		return collageListMapper.selectByPrimaryKey(id);
	}
	/**
	 * 得到团长对象
	 */
	/*
	public UserCollageBuyInfo selectByGroupHostPrimdaryKey(Integer id){
		return collageListMapper.selectByGroupHostPrimdaryKey(id);
	}*/

	/**
	 * 还差多少人拼成团统计
	 * @param id
	 * @return
	 */
	public Integer selectCountGroup(Long maps){
		return collageListMapper.selectCountGroup(maps);
	}

	@Override
	public CollageBuy selectByPrimaryKey(Long id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<CollageBuy> queryPages(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<CollageBuy> queryPageListWithNoRelations(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int selectCount(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	

}
