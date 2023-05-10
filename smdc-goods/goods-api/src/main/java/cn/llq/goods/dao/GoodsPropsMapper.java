package cn.llq.goods.dao;

import cn.llq.good.menuList.pojo.GoodsProps;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

public interface GoodsPropsMapper extends Mapper<GoodsProps>, InsertUseGeneratedKeysMapper<GoodsProps> {
}
