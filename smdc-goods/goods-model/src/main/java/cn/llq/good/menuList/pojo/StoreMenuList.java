package cn.llq.good.menuList.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "menu_list")
public class StoreMenuList {
    @Id
    @Column(name = "shop_store_id")
    Long shopStoreId;

    @Column(name = "menu_list_id")
    Long menuListId;

}
