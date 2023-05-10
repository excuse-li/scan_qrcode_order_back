package cn.llq.shop.model.pojo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Table(name = "shop_store")
public class ShopStorePo {
    @Id
    @Column
    Long id;

    @Column(name = "store_name")
    String storeName;

    @Column(name = "location_y")
    String locationY;

    @Column(name = "location_x")
    String locationX;

    @Column(name = "reset_time")
    String resetTime;

    @Column
    String img;

    @Column(name = "location_info")
    String locationInfo;


    @Column(name = "`desc`")
    String desc;

    @Column(name = "link_phone")
    String linkPhone;

    @Column(name = "merchant_id")
    Long merchantId;

    @Column
    int status=1;

}
