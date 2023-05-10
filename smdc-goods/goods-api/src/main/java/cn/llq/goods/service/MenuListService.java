package cn.llq.goods.service;

import cn.llq.good.menuList.pojo.MenuList;
import cn.llq.good.menuList.pojo.MenuListGoods;
import cn.llq.good.menuList.pojo.StoreMenu;
import cn.llq.goods.dao.MenuListGoodsMapper;
import cn.llq.goods.dao.MenuListMapper;
import cn.llq.goods.dao.StoreMenuMapper;
import cn.llq.shop.model.pojo.UserInfoPo;
import cn.llq.utils.RedisUtil;
import cn.llq.utils.status.MenuListStatus;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MenuListService {

    @Autowired
    MenuListMapper menuListMapper;

    @Autowired
    MenuListGoodsMapper menuListGoodsMapper;

    @Autowired
    StoreMenuMapper storeMenuMapper;

    @Autowired
    RedisUtil redisUtil;

    @GlobalTransactional
    public MenuList addMenuList(MenuList menuList,String authorization){
        Map<String,Object> map = (HashMap<String,Object>)redisUtil.get(authorization);
        UserInfoPo info =(UserInfoPo) map.get("info");
        menuList.setMerchantId(info.getMerchantId());
        menuList.setStatus(MenuListStatus.ON);
        menuListMapper.insertUseGeneratedKeys(menuList);
        return menuList;
    }

    @GlobalTransactional
    public MenuList updateMenuList(MenuList menuList){
        menuListMapper.updateByPrimaryKeySelective(menuList);
        return menuList;
    }

    @GlobalTransactional
    public void startMenuList(Long id){
        MenuList menuList = new MenuList();
        menuList.setId(id);
        menuList.setStatus(MenuListStatus.ON);
        menuListMapper.updateByPrimaryKeySelective(menuList);
    }

    @GlobalTransactional
    public void stopMenuList(Long id){
        MenuList menuList = new MenuList();
        menuList.setId(id);
        menuList.setStatus(MenuListStatus.OFF);
        menuListMapper.updateByPrimaryKeySelective(menuList);
    }

    @GlobalTransactional
    public void deleteMenuList(Long id){
        MenuList menuList = new MenuList();
        menuList.setId(id);
        menuList.setStatus(MenuListStatus.DELETE);
        menuListMapper.updateByPrimaryKeySelective(menuList);
    }

    public MenuList getMenuListById(Long id){
        return menuListMapper.selectByPrimaryKey(id);
    }

    public Object listMenuList(String name,Integer page,Integer size,String authorization){
        PageHelper.startPage(page,size);
        Map<String,Object> map = (HashMap<String,Object>)redisUtil.get(authorization);
        UserInfoPo info =(UserInfoPo) map.get("info");
        Example example = new Example(MenuList.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("merchantId", info.getMerchantId());
        criteria.andLike("name","%"+name+"%");
        criteria.andNotEqualTo("status",MenuListStatus.DELETE);
        PageInfo menuListPageInfo = new PageInfo(menuListMapper.selectByExample(example));
        List list = menuListPageInfo.getList();
        JSONArray array = new JSONArray();

        for (int i = 0; i < list.size(); i++) {
            JSONObject o = (JSONObject)JSONObject.toJSON(list.get(i));
            Long id = o.getLong("id");

            MenuListGoods menuListGoods = new MenuListGoods();
            menuListGoods.setMenuListId(id);
            o.put("goodsCount",menuListGoodsMapper.selectCount(menuListGoods));

            StoreMenu storeMenu = new StoreMenu();
            storeMenu.setMenuListId(id);
            o.put("storeCount",storeMenuMapper.selectCount(storeMenu));
            array.add(o);
        }
        menuListPageInfo.setList(array);
        return menuListPageInfo;
    }

    @GlobalTransactional
    public void addGoods(List<MenuListGoods> menuListGoods){
        for (int i = 0; i < menuListGoods.size(); i++) {
            menuListGoodsMapper.insert(menuListGoods.get(i));
        }
    }

    @GlobalTransactional
    public void deleteGoods(MenuListGoods menuListGoods){
        menuListGoodsMapper.delete(menuListGoods);
    }

    @GlobalTransactional
    public void updateMenuList(StoreMenu storeMenu){
        storeMenuMapper.deleteByPrimaryKey(storeMenu.getShopStoreId());
        storeMenuMapper.insertSelective(storeMenu);
    }

    public Object getStoreMenu(Long id) {
        StoreMenu storeMenu = new StoreMenu();
        storeMenu.setShopStoreId(id);
        return storeMenuMapper.selectOne(storeMenu);
    }
}
