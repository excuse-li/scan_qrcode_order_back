package cn.llq.shop.dao;

import cn.llq.shop.model.pojo.StoreTable;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

public interface TableMapper extends Mapper<StoreTable>, InsertUseGeneratedKeysMapper<StoreTable> {
}
