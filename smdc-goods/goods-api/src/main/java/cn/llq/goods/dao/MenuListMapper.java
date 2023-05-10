package cn.llq.goods.dao;


import cn.llq.good.menuList.pojo.MenuList;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

public interface MenuListMapper extends Mapper<MenuList>, InsertUseGeneratedKeysMapper<MenuList> {
}
