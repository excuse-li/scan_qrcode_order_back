package cn.llq.shop.service;

import cn.llq.shop.dao.ShopStoreMapper;
import cn.llq.shop.model.pojo.ShopStorePo;
import cn.llq.shop.model.pojo.UserInfoPo;
import cn.llq.utils.RedisUtil;
import cn.llq.utils.response.exception.APIException;
import cn.llq.utils.status.StoreStatus;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShopStoreService {
    @Autowired
    @SuppressWarnings("ALL")
    ShopStoreMapper shopStoreMapper;
    @Autowired
    RedisUtil redisUtil;
    final Logger logger = LoggerFactory.getLogger(ShopStoreService.class);

    @GlobalTransactional(rollbackFor = Exception.class)
    public void addShopStore(ShopStorePo shopStore,String token){
        Map<String,Object> map = (HashMap<String,Object>)redisUtil.get(token);

        UserInfoPo info =(UserInfoPo) map.get("info");
        shopStore.setMerchantId(info.getMerchantId());
        shopStoreMapper.insertUseGeneratedKeys(shopStore);
    }

    @GlobalTransactional
    public void updateShopStore(ShopStorePo shopStore){
        if (shopStore.getMerchantId()!=null){
            shopStore.setMerchantId(null);
        }
        shopStoreMapper.updateByPrimaryKeySelective(shopStore);
    }

    @GlobalTransactional
    public void startShopStore(Long id){
        ShopStorePo shopStorePo = new ShopStorePo();
        shopStorePo.setId(id);
        shopStorePo.setStatus(StoreStatus.RUN);
        System.out.println("id="+id);
        shopStoreMapper.updateByPrimaryKeySelective(shopStorePo);
    }

    @GlobalTransactional
    public void stopShopStore(Long id){
        ShopStorePo shopStorePo = new ShopStorePo();
        shopStorePo.setId(id);
        System.out.println("id="+id);
        shopStorePo.setStatus(StoreStatus.STOP);
        shopStoreMapper.updateByPrimaryKeySelective(shopStorePo);
    }

    @GlobalTransactional
    public void deleteShopStore(Long id){
        ShopStorePo shopStorePo = new ShopStorePo();
        shopStorePo.setStatus(StoreStatus.DELETE);
        shopStoreMapper.deleteByPrimaryKey(id);
    }

    @GlobalTransactional
    public Object getShopStoreById(Long id){
        return shopStoreMapper.selectByPrimaryKey(id);
    }
    @GlobalTransactional
    public PageInfo<ShopStorePo> listPageByShopStore(ShopStorePo shopStore,Integer page,Integer size,String authorization){
        PageHelper.startPage(page,size);
        Map<String,Object> map = (HashMap<String,Object>)redisUtil.get(authorization);

        UserInfoPo info =(UserInfoPo) map.get("info");
        shopStore.setMerchantId(info.getMerchantId());
        Example example = new Example(ShopStorePo.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andLike("storeName","%"+(shopStore.getStoreName()==null?"":shopStore.getStoreName())+"%");

        criteria.andNotEqualTo("status",StoreStatus.DELETE);
        criteria.andEqualTo("merchantId",info.getMerchantId());
        List<ShopStorePo> select = shopStoreMapper.selectByExample(example);

        PageInfo<ShopStorePo> objectPageInfo = new PageInfo<ShopStorePo>(select);
        return objectPageInfo;
    }
}
