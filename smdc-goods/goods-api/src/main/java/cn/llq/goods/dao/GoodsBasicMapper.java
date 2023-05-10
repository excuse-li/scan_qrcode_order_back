package cn.llq.goods.dao;


import cn.llq.good.menuList.pojo.GoodsBasic;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

public interface GoodsBasicMapper extends Mapper<GoodsBasic>, InsertUseGeneratedKeysMapper<GoodsBasic> {
}
