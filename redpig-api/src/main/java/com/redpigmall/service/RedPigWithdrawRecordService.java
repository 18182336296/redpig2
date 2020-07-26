/**   
 * Copyright © 2018 eSunny Info. Tech Ltd. All rights reserved.
 * @Package: com.redpigmall.service 
 * @author: zxq@yihexinda.com  
 * @date: 2018年9月13日 下午3:54:22 
 */
package com.redpigmall.service;

import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.WithdrawRecordMapper;
import com.redpigmall.domain.WithawalRecord;
import com.redpigmall.domain.virtual.WithdrawRecordView;
import com.redpigmall.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName RedPigWithdrawRecordService
 * @author zxq@yihexinda.com
 * @date 2018年9月13日 下午3:54:22
 * @version 1.0
 * <p>Company: http://www.yihexinda.com</p>
 */
@Service
@Transactional(readOnly=true)
public class RedPigWithdrawRecordService extends BaseService<WithdrawRecordView>{
	@Autowired
	private WithdrawRecordMapper withdrawRecordMapper;
	
	@Override
	public WithdrawRecordView selectByPrimaryKey(Long id) {
		return withdrawRecordMapper.selectByPrimaryKey(id);
	}
	
	@Override
	public List<WithdrawRecordView> queryPages(Map<String, Object> params) {
		return withdrawRecordMapper.queryPages(params);
	}

	@Override
	public List<WithdrawRecordView> queryPageListWithNoRelations(Map<String, Object> params) {
		return withdrawRecordMapper.queryPageListWithNoRelations(params);
	}

	@Override
	public int selectCount(Map<String, Object> params) {
		return withdrawRecordMapper.selectCount(params);
	}
	
	@Override
	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}
	
	/**
	 * 更新多个选择字段
	 * @Title: updateByPrimaryKeySelective
	 * @param record
	 * @author zxq@yihexinda.com
	 * @date 2018年9月13日 下午9:01:17
	 * @return int
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateByPrimaryKeySelective(WithawalRecord record) {
		return withdrawRecordMapper.updateByPrimaryKeySelective(record);
	}
	
	/**
	 * 更新ID查询提现记录
	 * @Title: selectById
	 * @param id 主键
	 * @author zxq@yihexinda.com
	 * @date 2018年9月13日 下午9:11:11
	 * @return WithawalRecord
	 */
	public WithawalRecord selectById(Long id) {
		return withdrawRecordMapper.selectById(id);
	}

	/**
	 * 保存提现申请
	 * @param obj
	 */
	@Transactional(readOnly = false)
	public void insertSelective(WithawalRecord obj) {
		withdrawRecordMapper.insertSelective(obj);
	}
}
