/**   
 * Copyright © 2018 eSunny Info. Tech Ltd. All rights reserved.
 * @Package: com.redpigmall.service 
 * @author: zxq@yihexinda.com  
 * @date: 2018年9月12日 下午5:59:50 
 */
package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.redpigmall.dao.WithdrawalSettingMapper;
import com.redpigmall.domain.WithdrawalSetting;
import com.redpigmall.service.base.BaseService;

/**
 * 提现管理业务实现层
 * @ClassName RedPigQithDrawalManageService
 * @author zxq@yihexinda.com
 * @date 2018年9月12日 下午5:59:50
 * @version 1.0
 * <p>Company: http://www.yihexinda.com</p>
 */
@Service
@Transactional(readOnly = true)
public class RedPigWithDrawalManageService extends BaseService<WithdrawalSetting>{
	@Autowired
	private WithdrawalSettingMapper withdrawalSettingMapper;
	
	@Override
	public WithdrawalSetting selectByPrimaryKey(Long id) {
		return withdrawalSettingMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<WithdrawalSetting> queryPages(Map<String, Object> params) {
		return withdrawalSettingMapper.queryPages(params);
	}

	@Override
	public List<WithdrawalSetting> queryPageListWithNoRelations(Map<String, Object> params) {
		return withdrawalSettingMapper.queryPageListWithNoRelations(params);
	}

	@Override
	public int selectCount(Map<String, Object> params) {
		Integer count = withdrawalSettingMapper.selectCount(params);
		if (count == null) {
			return 0;
		}
		return count;
	}
	/**
	 * 提现设置插入（使用mybatis-generator自动生成）
	 * @Title: insert
	 * @param record
	 * @return 返回插入条数
	 * @author zxq@yihexinda.com
	 * @date 2018年9月12日 下午8:13:07
	 * @return int
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insert(WithdrawalSetting record) {
		return withdrawalSettingMapper.insert(record);
	}
	
	/**
	 * 根据ID更新表字段
	 * @Title: updateByPrimaryKey
	 * @param entity
	 * @author zxq@yihexinda.com
	 * @date 2018年9月12日 下午8:15:40
	 * @return int
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateByPrimaryKey(WithdrawalSetting entity) {
		return withdrawalSettingMapper.updateByPrimaryKey(entity);
	}
	
	/**
	 * 查询一条记录
	 * @Title: selectOne
	 * @author zxq@yihexinda.com
	 * @date 2018年9月12日 下午8:41:23
	 * @return WithdrawalSetting
	 */
	public WithdrawalSetting selectOne() {
		return withdrawalSettingMapper.selectOne();
	}
}
