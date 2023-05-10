package cn.llq.goods.dao;

import cn.llq.good.menuList.pojo.GoodsSku;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

public interface GoodsSkuMapper extends Mapper<GoodsSku>, InsertUseGeneratedKeysMapper<GoodsSku> {
}
