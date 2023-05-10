package cn.llq.shop.model.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "role")
public class Role {
    @Id
    @Column(name = "id")
    Long id;

    @Column(name = "role_name")
    String roleName;

    @Column(name = "`desc`")
    String desc;
}
