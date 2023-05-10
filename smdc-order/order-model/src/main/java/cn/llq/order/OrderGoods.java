package cn.llq.order;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Table(name = "order_goods")
@Data
public class OrderGoods {
    @Id
    @Column
    String id = UUID.randomUUID().toString();

    @Column(name = "sku_id")
    String skuId;

    @Column(name = "goods_name")
    String goodsName;

    @Column(name = "goods_sku_props")
    String goodsSkuProps;

    @Column(name = "goods_sku_price")
    BigDecimal goodsSkuPrice;

    @Column
    Integer num;

    @Column(name="goods_due")
    BigDecimal goodsDue;

    @Column(name="goods_img")
    String goodsImg;

    @Column(name = "order_id")
    String orderId;

    @Column(name = "add_time")
    Date addTime;

    @Column(name = "goods_status")
    Integer goodsStatus;

    public static OrderGoods newInstance() {
        OrderGoods orderGoods = new OrderGoods();
        orderGoods.setId(null);
        return orderGoods;
    }

}
