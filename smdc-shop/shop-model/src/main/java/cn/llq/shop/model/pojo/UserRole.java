package cn.llq.shop.model.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name="user_role")
public class UserRole {
    @Id
    @Column(name = "user_id")
    Long userId;

    @Id
    @Column(name = "role_id")
    Long roleId;

}
