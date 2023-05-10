package cn.llq.good.menuList.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "store_goods_down")
public class StoreGoodsDown {
    @Column(name = "store_id")
    @Id
    Long storeId;

    @Id
    @Column(name = "goods_id")
    String goodsId;
}
