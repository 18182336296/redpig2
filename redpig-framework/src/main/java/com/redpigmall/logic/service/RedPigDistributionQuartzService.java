package com.redpigmall.logic.service;

import com.redpigmall.domain.DistributionCommission;
import com.redpigmall.domain.DistributionGrade;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.UserDistribution;
import com.redpigmall.service.RedPigDistributionCommissionService;
import com.redpigmall.service.RedPigDistributionGradeService;
import com.redpigmall.service.RedPigOrderFormService;
import com.redpigmall.service.UserDistributionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Auther: csh
 * @Date: 2018/9/3 15:00
 * @Description:分销拥金统计
 */
@Service
@Transactional
public class RedPigDistributionQuartzService {
    private Logger logger = LoggerFactory.getLogger(RedPigDistributionQuartzService.class);

    @Autowired
    private UserDistributionService userDistributionDao;

    @Autowired
    private RedPigDistributionCommissionService distributionCommissionDao;

    @Autowired
    private RedPigOrderFormService orderFormDao;

    @Autowired
    private RedPigDistributionGradeService distributionGradeDao;


    /**
     * 每月一号统计分销金额
     */
    @Transactional(readOnly=false)
    public void countDistribution(){
        DistributionCommission commission;
        /**获取全部分销商*/
        List<UserDistribution> userDistributions = userDistributionDao.selectAll();
        for (UserDistribution userDistribution:userDistributions) {
            logger.info("开始："+ Arrays.asList(userDistribution)+"的数据获取！");
            /**获取记录*/
            commission =distributionCommissionDao.selectByUserId(userDistribution.getUser().getId());
            logger.info("用户"+userDistribution.getUser().getId()+"获取为:"+Arrays.asList(commission));
            if(null==commission || null==commission.getId()){
                commission = new DistributionCommission();
                BigDecimal zero = new BigDecimal(0.00);
                commission.setSum_deal_price(zero);
                commission.setDown_sum_price(zero);
                commission.setBalance_price(zero);
                commission.setSum_price(zero);
                commission.setSum_order(0);
                commission.setSum_user(0);
            }
            /*获取分销商一级,注意：userDistribution.getId()改为userDistribution.getUser().getId()*/
            List<UserDistribution> oneDistribution = userDistributionDao.queryTwoData(userDistribution.getUser().getId());
            logger.info("用户"+userDistribution.getId()+"获取到的一级分销列表:"+Arrays.asList(oneDistribution));
            /*一级分销客户数*/
            commission.setSum_user(oneDistribution.size());
            BigDecimal sumDealPrice=new BigDecimal(0.00).setScale(2,BigDecimal.ROUND_DOWN);
            int count = 0;
            if(oneDistribution.size()<=0){
                continue;
            }
            for (int i = 0;i<oneDistribution.size();i++){
                Long id = oneDistribution.get(i).getUser().getId();
                List<OrderForm> orderForms = orderFormDao.queryOrderAllById(id);
                logger.info("一级分销用户:"+id+"获取的数据"+ Arrays.asList(orderForms));
                for (int j=0;j<orderForms.size();j++){
                    OrderForm orderForm = orderForms.get(j);
                    //累计金额
                    sumDealPrice= sumDealPrice.add(orderForm.getTotalPrice());
                    //设置为已结算
                    orderForm.setDistribution_status(1);
                    orderFormDao.updateDistributionStatus(orderForm);
                }
                //累计笔数
                count=count+orderForms.size();
            }
            sumDealPrice = sumDealPrice.add(commission.getSum_deal_price()==null?new BigDecimal(0.00):commission.getSum_deal_price()).setScale(2,BigDecimal.ROUND_DOWN);
            commission.setSum_deal_price(sumDealPrice);
            commission.setSum_order((commission.getSum_order()+count));
            /*获取分销商一二级,注意：userDistribution.getId()改为userDistribution.getUser().getId()*/
            List<UserDistribution> list = userDistributionDao.queryTwoAndThreeData(userDistribution.getUser().getId());
            BigDecimal balance = new BigDecimal(0).setScale(2,BigDecimal.ROUND_DOWN);
            BigDecimal totalPrice = new BigDecimal(0);
            for (int i =0;i<list.size();i++){
                List<OrderForm> orderForms = orderFormDao.queryOrderById(list.get(i).getUser().getId());
                for (OrderForm order:orderForms){
                    //可提现金额
                    balance=   balance.add(order.getTotalPrice());
                    //下线累计成交金额
                    totalPrice=   totalPrice.add(order.getTotalPrice());
                    order.setDistribution_status(1);
                    orderFormDao.updateDistributionStatus(order);
                }
            }
            // 在上述计算的基础上+原有的下线订单金额数目
            totalPrice = totalPrice.add(sumDealPrice).add(commission.getDown_sum_price());
            // 更新总的下线订单金额总数
            commission.setDown_sum_price(totalPrice);
            /*匹配本人的分销等级*/
            Map map = new HashMap<>();
            map.put("count_user",commission.getSum_user());
            map.put("count_price",commission.getSum_price());
            map.put("down_count_price",commission.getDown_sum_price());
            DistributionGrade distributionGrade = distributionGradeDao.queryByCondition(map);
            if(null==distributionGrade){
                logger.error("出现异常,分销商"+userDistribution.getUser().getId()+"找不到会员等级!");
                continue;
            }
            /**1为内部 2为外部*/
            BigDecimal per = new BigDecimal(0).setScale(2,BigDecimal.ROUND_DOWN);
            if(userDistribution.getIs_inner_staff()==1){
                BigDecimal val = new BigDecimal(distributionGrade.getInner_rebate()).divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_DOWN);
                per = val.setScale(2,BigDecimal.ROUND_DOWN);
            }else{
                BigDecimal val = new BigDecimal(distributionGrade.getOut_rebate()).divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_DOWN);
                per = val.setScale(2,BigDecimal.ROUND_DOWN);
            }
            logger.info("原有的可提现金额为:"+commission.getBalance_price());
            balance = sumDealPrice.add(balance).multiply(per);
            balance = commission.getBalance_price().add(balance);
            logger.info("新结余为:"+balance);
            commission.setBalance_price(balance);
            //更新表
            if(null==commission.getId()){
                commission.setUser(userDistribution.getUser());
                commission.setUser_name(userDistribution.getUser().getUserName());
                commission.setNickName(userDistribution.getUser().getNickName());
                commission.setAddTime(new Date());
                commission.setGrade(distributionGrade.getGrade());
                commission.setGrade_id(distributionGrade.getId());
                commission.setSum_price(new BigDecimal(0));
                distributionCommissionDao.saveEntity(commission);
            }else{
                commission.setUpdate_time(new Date());
                distributionCommissionDao.updateById(commission);
            }
        }
    }


}
