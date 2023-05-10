package cn.llq.goods.service;

import cn.llq.good.menuList.bo.GoodsBo;
import cn.llq.good.menuList.pojo.*;
import cn.llq.goods.dao.*;
import cn.llq.shop.model.pojo.UserInfoPo;
import cn.llq.utils.RedisUtil;
import cn.llq.utils.response.exception.APIException;
import cn.llq.utils.status.GoodsStatus;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GoodsService {
    @Autowired
    GoodsBasicMapper goodsBasicMapper;

    @Autowired
    GoodsPropsMapper goodsPropsMapper;

    @Autowired
    GoodsSkuMapper goodsSkuMapper;

    @Autowired
    GoodsTypeMapper goodsTypeMapper;
    @Autowired
    StoreMenuMapper storeMenuMapper;
    @Autowired
    MenuListMapper menuListMapper;
    @Autowired
    MenuListGoodsMapper menuListGoodsMapper;
    @Autowired
    StoreGoodsDownMapper storeGoodsDownMapper;
    
    @Autowired
    RedisUtil redisUtil;

    @GlobalTransactional
    public void addGoodsType(GoodsType goodsType,String token){
        goodsType.setMerchantId(getMerchantId(token));
        goodsTypeMapper.insertSelective(goodsType);
    }

    @GlobalTransactional
    public void updateGoodsType(GoodsType goodsType,String token){
        goodsType.setMerchantId(getMerchantId(token));
        goodsTypeMapper.updateByPrimaryKeySelective(goodsType);
    }



    @GlobalTransactional
    public GoodsType getGoodsTypeById(Long id){
        return goodsTypeMapper.selectByPrimaryKey(id);
    }
    public Object listGoodsType(String token){
        Long merchantId = getMerchantId(token);
        GoodsType goodsType = new GoodsType();
        goodsType.setMerchantId(merchantId);
        List<GoodsType> select = goodsTypeMapper.select(goodsType);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < select.size(); i++) {
            JSONObject o =(JSONObject) JSONObject.toJSON(select.get(i));
            Example example = new Example(GoodsBasic.class);
            example.createCriteria().andNotEqualTo("status",GoodsStatus.DELETE).andEqualTo("goodsType",select.get(i).getId());
            o.put("goodsCount",goodsBasicMapper.selectCountByExample(example));
            jsonArray.add(o);
        }

        return jsonArray;
    }

    @GlobalTransactional
    public void deleteType(Long id){

        Example example = new Example(GoodsBasic.class);
        example.createCriteria().andNotEqualTo("status",GoodsStatus.DELETE).andEqualTo("goodsType",id);
        int i = goodsBasicMapper.selectCountByExample(example);

        if (i>0){
            throw new APIException(400,"分类存在商品，不能删除");
        }

        goodsTypeMapper.deleteByPrimaryKey(id);

    }

    @GlobalTransactional
    public void addGoods(GoodsBo goodsBo,String token){
        goodsBo.setStatus(GoodsStatus.START);
        goodsBo.setMerchantId(getMerchantId(token));
        GoodsBasic goodsBasic = new GoodsBasic();
        BeanUtils.copyProperties(goodsBo,goodsBasic);
        goodsBasicMapper.insertSelective(goodsBasic);
        List<GoodsProps> propsList = goodsBo.getPropsList();
        List<GoodsSku> skuList = goodsBo.getSkuList();
        for (int i = 0; i < propsList.size(); i++) {
            propsList.get(i).setGoodsId(goodsBasic.getId());
            goodsPropsMapper.insert(propsList.get(i));
        }

        for (int i = 0; i < skuList.size(); i++) {
            skuList.get(i).setGoodsId(goodsBasic.getId());
            goodsSkuMapper.insert(skuList.get(i));
        }
    }

    @GlobalTransactional
    public void updateGoods(GoodsBo goodsBo,String token){
        goodsBo.setMerchantId(getMerchantId(token));
        GoodsBasic goodsBasic = new GoodsBasic();

        BeanUtils.copyProperties(goodsBo,goodsBasic);
        goodsBasicMapper.updateByPrimaryKeySelective(goodsBasic);
        List<GoodsProps> propsList = goodsBo.getPropsList();
        GoodsProps goodsProps = new GoodsProps();
        goodsProps.setGoodsId(goodsBo.getId());
        goodsProps.setId(null);
        goodsPropsMapper.delete(goodsProps);
        for (int i = 0; i < propsList.size(); i++) {
            propsList.get(i).setGoodsId(goodsBasic.getId());
            goodsPropsMapper.insert(propsList.get(i));
        }

        List<GoodsSku> skuList = goodsBo.getSkuList();
        GoodsSku goodsSku = new GoodsSku();
        goodsSku.setId(null);
        goodsSku.setGoodsId(goodsBo.getId());

        goodsSkuMapper.delete(goodsSku);

        for (int i = 0; i < skuList.size(); i++) {
            skuList.get(i).setGoodsId(goodsBasic.getId());
            goodsSkuMapper.insert(skuList.get(i));
        }
    }

    @GlobalTransactional
    public void deleteGoods(String id){
        GoodsBasic goodsBasic = new GoodsBasic();
        goodsBasic.setId(id);

        goodsBasic.setStatus(GoodsStatus.DELETE);
        goodsBasicMapper.updateByPrimaryKeySelective(goodsBasic);
    }

    @GlobalTransactional
    public void startGoods(String id){
        GoodsBasic goodsBasic = new GoodsBasic();
        goodsBasic.setId(id);

        goodsBasic.setStatus(GoodsStatus.START);
        goodsBasicMapper.updateByPrimaryKeySelective(goodsBasic);
    }

    @GlobalTransactional
    public void stopGoods(String id){
        GoodsBasic goodsBasic = new GoodsBasic();
        goodsBasic.setId(id);

        goodsBasic.setStatus(GoodsStatus.START);
        goodsBasicMapper.updateByPrimaryKeySelective(goodsBasic);
    }

    public GoodsBo getGoodsById(String id){
        GoodsBasic goodsBasic = goodsBasicMapper.selectByPrimaryKey(id);
        if (goodsBasic == null){
            throw new APIException(400,"商品不存在！");
        }
        GoodsBo goodsBo = new GoodsBo();
        BeanUtils.copyProperties(goodsBasic,goodsBo);
        GoodsProps goodsProps = new GoodsProps();
        goodsProps.setId(null);
        goodsProps.setGoodsId(id);
        goodsBo.setPropsList(goodsPropsMapper.select(goodsProps));

        GoodsSku goodsSku = new GoodsSku();
        goodsSku.setGoodsId(id);
        goodsSku.setId(null);
        goodsBo.setSkuList(goodsSkuMapper.select(goodsSku));
        return goodsBo;
    }

    public Long getMerchantId(String token){
        Map<String,Object> map = (HashMap<String,Object>)redisUtil.get(token);
        if(map==null){
            throw new APIException(403,"登录已失效");
        }
        UserInfoPo info =(UserInfoPo) map.get("info");
        return info.getMerchantId();
    }


    public Object listByStore(Long id, Integer showDown) {
        StoreMenu storeMenu = new StoreMenu();
        storeMenu.setShopStoreId(id);
        storeMenu = storeMenuMapper.selectOne(storeMenu);
        Long menuListId = storeMenu.getMenuListId();

        return listByMenuList(menuListId,showDown);
    }

    public JSONArray listByMenuList(Long id,Integer showDown){
        MenuListGoods menuListGoods = new MenuListGoods();
        menuListGoods.setMenuListId(id);
        List<MenuListGoods> select = menuListGoodsMapper.select(menuListGoods);
        List<String> stringStream = select.stream().map(MenuListGoods::getGoodsId).collect(Collectors.toList());


        GoodsType goodsType = new GoodsType();
//        goodsType.setMerchantId(getMerchantId(token));
        Example example = new Example(GoodsBasic.class);
        Example.Criteria criteria = example.createCriteria();
        if (stringStream.size()==0){
            stringStream = new ArrayList<>();

            stringStream.add("-1");
        }
        criteria.andIn("id", stringStream);
        if (showDown<=0){

            StoreGoodsDown storeGoodsDown = new StoreGoodsDown();
            storeGoodsDown.setStoreId(id);
            List<StoreGoodsDown> select2 = storeGoodsDownMapper.select(storeGoodsDown);
            List<String> collect = select2.stream().map(StoreGoodsDown::getGoodsId).collect(Collectors.toList());
            if (collect.size()>0){
                criteria.andNotIn("id",collect);
            }
        }


        criteria.andEqualTo("status",GoodsStatus.START);

        List<GoodsBasic> goodsBasics = goodsBasicMapper.selectByExample(example);
        List<GoodsType> select1 = goodsTypeMapper.select(goodsType);

        JSONArray arr = new JSONArray();

        for (int i = 0; i < select1.size(); i++) {
            JSONObject o = (JSONObject)JSONObject.toJSON(select1.get(i));
            ArrayList<GoodsBo> basics = new ArrayList<>();
            for (int j = 0; j < goodsBasics.size(); j++) {
                if (select1.get(i).getId().equals(goodsBasics.get(j).getGoodsType())){
                    GoodsBo goodsBo = new GoodsBo();
                    BeanUtils.copyProperties(goodsBasics.get(j),goodsBo);
                    GoodsProps goodsProps = new GoodsProps();
                    goodsProps.setId(null);
                    goodsProps.setGoodsId(goodsBasics.get(j).getId());
                    goodsBo.setPropsList(goodsPropsMapper.select(goodsProps));

                    GoodsSku goodsSku = new GoodsSku();
                    goodsSku.setGoodsId(goodsBasics.get(j).getId());
                    goodsSku.setId(null);
                    goodsBo.setSkuList(goodsSkuMapper.select(goodsSku));
                    basics.add(goodsBo);
                }
            }
            if (basics.size()>0){
                o.put("list",basics);
                arr.add(o);
            }
        }
       return arr;
    }

    @GlobalTransactional
    public void downGoodsStore(StoreGoodsDown storeGoodsDown) {
        storeGoodsDownMapper.insert(storeGoodsDown);
    }

    @GlobalTransactional
    public void upGoodsStore(StoreGoodsDown storeGoodsDown) {
        storeGoodsDownMapper.delete(storeGoodsDown);
    }

    public Object listDownGoodsStore(Long id) {
        StoreGoodsDown storeGoodsDown = new StoreGoodsDown();
        storeGoodsDown.setStoreId(id);
        return storeGoodsDownMapper.select(storeGoodsDown);
    }

    public Object listByType(Long id, String name, int page, int size) {
        PageHelper.startPage(page,size);

        Example example = new Example(GoodsBasic.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("goodsType",id);
        criteria.andLike("goodsName","%"+name+"%");
        criteria.andNotEqualTo("status",GoodsStatus.DELETE);
        List<GoodsBasic> select = goodsBasicMapper.selectByExample(example);
        PageInfo<GoodsBasic> goodsBasicPageInfo = new PageInfo<>(select);
        List<GoodsBasic> list = goodsBasicPageInfo.getList();
        ArrayList<GoodsBasic> goodsBos = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            goodsBos.add(getGoodsById(list.get(i).getId()));

        }
        goodsBasicPageInfo.setList(goodsBos);
        return goodsBasicPageInfo;
    }

    public Object getBySkuId(String id) {

        GoodsSku goodsSku = goodsSkuMapper.selectByPrimaryKey(id);
        if (goodsSku==null){
            throw new APIException("商品id不正确");
        }
        ArrayList<GoodsSku> goodsSkus = new ArrayList<>();
        goodsSkus.add(goodsSku);
        GoodsBo goodsBo = new GoodsBo();
        GoodsBasic goodsBasic = goodsBasicMapper.selectByPrimaryKey(goodsSku.getGoodsId());
        if (goodsBasic==null||goodsBasic.getStatus().equals(GoodsStatus.DELETE)){
            throw new APIException("商品存在或已删除");
        }
        BeanUtils.copyProperties(goodsBasic,goodsBo);

        goodsBo.setSkuList(goodsSkus);
        return goodsBo;
    }
}
