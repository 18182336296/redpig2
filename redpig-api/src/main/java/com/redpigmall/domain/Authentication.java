package com.redpigmall.domain;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

import javax.persistence.Table;
import java.util.Date;

/**
 * @Auther: Administrator
 * @Date: 2018/9/15 16:32
 * @Description: 认证信息
 */
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "authentication")
public class Authentication extends IdEntity {
    //认证ID
    private Long id;
    //认证用户姓名
    private String name;
    //认证用户电话号码
    private String phone;
    //认证用户身份证号码
    private String card;
    //认证用户信息创建时间
    private Date addTime;
    //认证用户信息修改时间
    private Date update_time;
    //认证状态：0禁止、1启用
    private int state;
    //审核状态：0审核通过、1审核未通过
    private int examine_state;
    //认证用户公司名称
    private String company;
    //认证用户公司部门
    private String department;
    //认证用户公司职位
    private String position;
    //备注
    private String remark;


    //关联User的id
    private Long user_id;


    @Override
    public Date getAddTime() {
        return addTime;
    }

    @Override
    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }



    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getExamine_state() {
        return examine_state;
    }

    public void setExamine_state(int examine_state) {
        this.examine_state = examine_state;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
