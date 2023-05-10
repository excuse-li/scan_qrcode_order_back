package cn.llq.good.menuList.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "goods_basic")
@Data
public class GoodsBasic {
    @Id
    @Column
    String id = UUID.randomUUID().toString();

    @Column(name = "goods_name")
    String goodsName;

    @Column(name = "head_img")
    String headImg;

    @Column(name = "`desc`")
    String desc;

    @Column(name="start_num")
    BigDecimal startNum=new BigDecimal(1);

    @Column(name = "goods_type")
    Long goodsType;

    @Column(name = "merchant_id")
    Long merchantId;

    @Column(name = "`status`")
    Integer status;
}
