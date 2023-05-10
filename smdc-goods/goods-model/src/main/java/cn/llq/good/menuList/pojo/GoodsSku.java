package cn.llq.good.menuList.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "goods_sku")
@Data
public class GoodsSku {
    @Id
    @Column
    String id= UUID.randomUUID().toString();

    @Column
    String props;

    @Column
    BigDecimal price;

    @Column(name = "goods_id")
    String goodsId;
}
