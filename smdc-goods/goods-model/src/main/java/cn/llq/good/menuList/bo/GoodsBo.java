package cn.llq.good.menuList.bo;

import cn.llq.good.menuList.pojo.GoodsBasic;
import cn.llq.good.menuList.pojo.GoodsProps;
import cn.llq.good.menuList.pojo.GoodsSku;
import lombok.Data;

import java.util.List;

@Data
public class GoodsBo extends GoodsBasic {
    List<GoodsSku> skuList;
    List<GoodsProps> propsList;
}
