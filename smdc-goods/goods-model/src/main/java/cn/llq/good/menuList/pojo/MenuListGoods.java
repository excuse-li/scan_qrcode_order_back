package cn.llq.good.menuList.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "menu_list_goods")
public class MenuListGoods {
    @Column(name = "menu_list_id")
    Long menuListId;

    @Column(name = "goods_id")
    String goodsId;

    @Column
    @Id
    Long id;
}
