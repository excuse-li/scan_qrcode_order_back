package cn.llq.good.menuList.pojo;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "menu_list")
@Data
public class MenuList {
    @Column
    @Id
    Long id;

    @Column
    String name;

    @Column
    int status;

    @Column(name = "update_time")
    Date updateTime = new Date();

    @Column(name = "merchant_id")
    Long merchantId;
}
