package cn.llq.member.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "member")
@Data
public class Member {
    @Id
    @Column
    String id;

    @Column(name = "open_id")
    String openId;

    @Column
    String nickname;

    @Column
    String province;

    @Column
    String city;

    @Column
    String country;

    @Column
    String headimg;

    @Column
    String unionid;
}
