package cn.llq.goods.dao;

import cn.llq.good.menuList.pojo.GoodsType;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

public interface GoodsTypeMapper extends Mapper<GoodsType>, InsertUseGeneratedKeysMapper<GoodsType> {
}