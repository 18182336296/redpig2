package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.WithdrawalSetting;

public interface WithdrawalSettingMapper {
    int deleteByPrimaryKey(Long id);

    int insert(WithdrawalSetting record);

    int insertSelective(WithdrawalSetting record);

    WithdrawalSetting selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(WithdrawalSetting record);

    int updateByPrimaryKey(WithdrawalSetting record);

	/**
	 * @Title: queryPageListWithNoRelations
	 * @param params
	 * @author zxq@yihexinda.com
	 * @date 2018年9月12日 下午6:51:12
	 * @return List<WithdrawalSetting>
	 */
	List<WithdrawalSetting> queryPageListWithNoRelations(Map<String, Object> params);

	/**
	 * 
	 * @Title: queryPages
	 * @param params
	 * @author zxq@yihexinda.com
	 * @date 2018年9月12日 下午6:51:20
	 * @return List<WithdrawalSetting>
	 */
	List<WithdrawalSetting> queryPages(Map<String, Object> params);

	/**
	 * 
	 * @Title: selectCount
	 * @param params
	 * @author zxq@yihexinda.com
	 * @date 2018年9月12日 下午6:52:10
	 * @return Integer
	 */
	Integer selectCount(Map<String, Object> params);
	/**
	 * 查询一条记录
	 * @Title: selectOne
	 * @author zxq@yihexinda.com
	 * @date 2018年9月12日 下午8:40:39
	 * @return WithdrawalSetting
	 */
	WithdrawalSetting selectOne();
}