package cn.llq.good.menuList.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "goods_type")
public class GoodsType {
    @Id
    @Column
    Long id;

    @Column
    String name;

    @Column
    String headimg;

    @Column(name = "merchant_id")
    Long merchantId;
}
