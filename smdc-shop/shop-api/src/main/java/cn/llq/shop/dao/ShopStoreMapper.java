package cn.llq.shop.dao;

import cn.llq.shop.model.pojo.ShopStorePo;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

public interface ShopStoreMapper extends Mapper<ShopStorePo>, InsertUseGeneratedKeysMapper<ShopStorePo> {
}
