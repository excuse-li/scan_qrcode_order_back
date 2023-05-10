package cn.llq.shop.model.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "user_info")
@Data
public class UserInfoPo {
    @Id
    @Column
    Long id;

    @Column(name = "user_name")
    String userName;

    @Column(name = "password")
    String password;

    @Column
    String type;

    @Column(name = "real_name")
    String realName;

    @Column(name = "id_card")
    String idCard;

    @Column
    String sex;

    @Column(name = "join_time")
    Date joinTime;

    @Column(name = "update_time")
    Date updateTime;

    @Column(name = "store_id")
    Long storeId;

    @Column(name = "merchant_id")
    Long merchantId;

    @Column(name = "link_phone")
    String linkPhone;

    @Column
    Integer status;
}
