package cn.llq.shop.dao;

import cn.llq.shop.model.pojo.MerchantPo;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

public interface MerchantMapper extends Mapper<MerchantPo>, InsertUseGeneratedKeysMapper<MerchantPo> {
}
