package com.redpigmall.domain;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * csh
 * 分销协议
 */
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "distribution_agreement")
public class DistributionAgreement extends IdEntity {
    /**分销协议*/
    @Column(columnDefinition = "LongText")
    private String agreement;
    /**更新时间*/
    private Date update_time;
    /**id*/
    private Long id;
    /**保存时间*/
    private Date addTime;
    /**协议版本*/
    private String agreement_version;
    /**协议状态*/
    private String agreement_state;
    /**协议类型*/
    private String agreement_type;
    /**协议名称*/
    private String agreement_name;

    public String getAgreement() {
        return agreement;
    }

    public void setAgreement(String agreement) {
        this.agreement = agreement;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getAddTime() {
        return addTime;
    }

    @Override
    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getAgreement_version() {
        return agreement_version;
    }

    public void setAgreement_version(String agreement_version) {
        this.agreement_version = agreement_version;
    }

    public String getAgreement_state() {
        return agreement_state;
    }

    public void setAgreement_state(String agreement_state) {
        this.agreement_state = agreement_state;
    }

    public String getAgreement_type() {
        return agreement_type;
    }

    public void setAgreement_type(String agreement_type) {
        this.agreement_type = agreement_type;
    }

    public String getAgreement_name() {
        return agreement_name;
    }

    public void setAgreement_name(String agreement_name) {
        this.agreement_name = agreement_name;
    }
}
