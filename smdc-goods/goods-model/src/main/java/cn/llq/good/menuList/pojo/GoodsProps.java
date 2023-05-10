package cn.llq.good.menuList.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Data
@Table(name = "goods_props")
public class GoodsProps {
    @Id
    @Column
    String id = UUID.randomUUID().toString();

    @Column(name = "prop_name")
    String propName;

    @Column(name = "prop_values")
    String propValues;

    @Column(name = "goods_id")
    String goodsId;
}
