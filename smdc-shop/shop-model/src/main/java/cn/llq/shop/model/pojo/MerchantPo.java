package cn.llq.shop.model.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "merchant")
public class MerchantPo {
    @Id
    @Column
    Long id;

    @Column(name = "conpany_name")
    String conpanyName;

    @Column
    String license;

    @Column(name = "license_pic")
    String licensePic;

    @Column(name = "position_code")
    Long positionCode;

    @Column
    String location;

    @Column(name = "link_name")
    String linkName;

    @Column(name = "link_phone")
    String linkPhone;

    @Column(name = "wechart_id")
    String wechartId;

    @Column
    String payee;

    @Column
    String bank;

    @Column(name = "bank_card")
    String bankCard;

    @Column(name = "limit_time")
    Date limitTime;

    @Column(name = "update_time")
    Date updateTime = new Date();

    @Column(name = "use_status")
    int useStatus;
}
