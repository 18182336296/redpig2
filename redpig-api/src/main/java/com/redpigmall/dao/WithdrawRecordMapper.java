package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.VerifyCode;
import com.redpigmall.domain.WithawalRecord;
import com.redpigmall.domain.virtual.WithdrawRecordView;

public interface WithdrawRecordMapper {
	/**
	 * 根据ID删除表记录
	 * @Title: deleteByPrimaryKey
	 * @param id
	 * @return
	 * @author zxq@yihexinda.com
	 * @date 2018年9月13日 下午3:40:58
	 * @return int
	 */
    int deleteByPrimaryKey(Long id);
    /**
     * 插入所有记录
     * @Title: insert
     * @param record
     * @author zxq@yihexinda.com
     * @date 2018年9月13日 下午3:41:34
     * @return int
     */
    int insert(WithawalRecord record);
    /**
     * 插入不为空的字段
     * @Title: insertSelective
     * @param record
     * @author zxq@yihexinda.com
     * @date 2018年9月13日 下午3:41:52
     * @return int
     */
    int insertSelective(WithawalRecord record);
    /**
     * 根据ID查询提现记录
     * @Title: selectByPrimaryKey
     * @param id
     * @author zxq@yihexinda.com
     * @date 2018年9月13日 下午3:42:14
     * @return WithdrawRecordView
     */
    WithdrawRecordView selectByPrimaryKey(Long id);
    /**
     * 只更新不为空的字段
     * @Title: updateByPrimaryKeySelective
     * @param record
     * @return
     * @author zxq@yihexinda.com
     * @date 2018年9月13日 下午3:42:40
     * @return int
     */
    int updateByPrimaryKeySelective(WithawalRecord record);
    /**
     * 根据ID更新所有值
     * @Title: updateByPrimaryKey
     * @param record
     * @author zxq@yihexinda.com
     * @date 2018年9月13日 下午3:42:59
     * @return int
     */
    int updateByPrimaryKey(WithawalRecord record);
	/**
	 * 查询分页列表
	 * @Title: queryPages
	 * @param params
	 * @author zxq@yihexinda.com
	 * @date 2018年9月13日 下午3:57:23
	 * @return List<WithdrawRecordView>
	 */
	List<WithdrawRecordView> queryPages(Map<String, Object> params);
	/**
	 * 
	 * @Title: queryPageListWithNoRelations
	 * @param params
	 * @return
	 * @author zxq@yihexinda.com
	 * @date 2018年9月13日 下午3:57:27
	 * @return List<WithdrawRecordView>
	 */
	List<WithdrawRecordView> queryPageListWithNoRelations(Map<String, Object> params);
	/**
	 *
	 * 查询记录数
	 * @Title: selectCount
	 * @param params
	 * @author zxq@yihexinda.com
	 * @date 2018年9月13日 下午3:57:31
	 * @return int
	 */
	int selectCount(Map<String, Object> params);
	/**
	 * 根据ID获取提现记录
	 * @Title: selectById
	 * @param id 主键
	 * @author zxq@yihexinda.com
	 * @date 2018年9月13日 下午9:09:31
	 * @return WithawalRecord
	 */
	WithawalRecord selectById(Long id);
}