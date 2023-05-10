package cn.llq.admin.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "user_info")
@Data
public class UserInfo {
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
}
