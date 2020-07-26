package com.redpigmall.dao;

import com.redpigmall.api.enums.Lang;
import com.redpigmall.domain.UserCollageBuyInfo;
import com.redpigmall.domain.UserCollageList;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface CollageListMapper extends SupperMapper {
	
	/**
	 * 团拼接口
	 * @param maps
	 * @return
	 */
	List<UserCollageList> selectObjByCollage(Map<String, Object> maps);

	/**
	 * 拼团团接口
	 * @param maps
	 * @return
	 */
	List<UserCollageBuyInfo> selectUserCollageBuyInfo(Map<String, Object> maps);


    /**
     * 拼团人数统计
     *
     * @param maps
     * @return
     */
    Integer selectCounts(Map<String, Integer> maps);

	/**
	 * 得到团列表
	 * @param id
	 * @return
	 */
	UserCollageList selectByPrimaryKey(Long id);

	/**
	 * 得到团长列表----------------
	 * @param id
	 * @return
	 */
	UserCollageBuyInfo selectByGroupHostPrimdaryKey(Integer id);


	/**
	 * 还差多少人拼成团统计
	 *
	 * @param maps
	 * @return
	 */
	Integer selectCountGroup(Long maps);






}
