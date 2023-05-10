package cn.llq.order.buyCarDao;

import cn.llq.good.menuList.bo.GoodsBo;
import cn.llq.order.BuyCarInfo;
import cn.llq.order.OrderGoods;
import cn.llq.utils.DistributedLockHandler;
import cn.llq.utils.RedisUtil;
import cn.llq.utils.opt.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class BuyCarDao {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    DistributedLockHandler distributedLockHandler;

    public void createBuyCar(String userId,Long tableId){
        ArrayList<String> strings = new ArrayList<>();
        strings.add(userId);
        redisUtil.set("table_"+tableId,strings);
        BuyCarInfo buyCarInfo = new BuyCarInfo();
        buyCarInfo.setCreateUserId(userId);
        redisUtil.set("buyCar_"+tableId,buyCarInfo);
    }

    public void join(String userId,Long tableId){
        Lock lock = new Lock("table__" + tableId, "table__" + tableId);
        distributedLockHandler.tryLock(lock);
        System.out.println(redisUtil.hasKey("table_" + tableId));
        if (!redisUtil.hasKey("table_" + tableId)){
            createBuyCar(userId,tableId);
        }
        System.out.println(redisUtil.hasKey("table_" + tableId));
        ArrayList<String> strings ;
        strings = (ArrayList<String>)redisUtil.get("table_" + tableId);
        System.out.println(strings);
        if (!strings.contains(userId)){
            strings.add(userId);
            System.out.println("添加了用户");
        }else{
            System.out.println("已经有了用户");
            distributedLockHandler.releaseLock(lock);
            return;
        }
        redisUtil.set("table_"+tableId,strings);
        distributedLockHandler.releaseLock(lock);
    }

    public void addGoods(Long tableId, OrderGoods orderGoods){
        Lock lock = new Lock("table__" + tableId, "table__" + tableId);
        distributedLockHandler.tryLock(lock);
        BuyCarInfo buyCarInfo = (BuyCarInfo)redisUtil.get("buyCar_"+tableId);
        List<OrderGoods> list = buyCarInfo.getList();
        boolean flag = true;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getSkuId().equals(orderGoods.getSkuId())){
                list.get(i).setNum(list.get(i).getNum()+orderGoods.getNum());
                list.get(i).setGoodsDue(list.get(i).getGoodsSkuPrice().multiply(new BigDecimal(Double.valueOf(list.get(i).getNum()))));
                if (list.get(i).getNum()==0){
                    list.remove(i);
                }
                flag = false;
                break;
            }
        }
        if (flag){
            list.add(orderGoods);
        }
        buyCarInfo.setList(list);
        redisUtil.set("buyCar_"+tableId,buyCarInfo);
        distributedLockHandler.releaseLock(lock);
    }

    public void deleteBuyCarInfo(Long tableId){
        Lock lock = new Lock("table__" + tableId, "table__" + tableId);
        distributedLockHandler.tryLock(lock);
        redisUtil.del("buyCar_"+tableId);
        redisUtil.del("table_"+tableId);
        distributedLockHandler.releaseLock(lock);
    }
    public void deleteBuyCarGoods(Long tableId){
        Lock lock = new Lock("table__" + tableId, "table__" + tableId);
        distributedLockHandler.tryLock(lock);
        BuyCarInfo buyCarInfo = (BuyCarInfo)redisUtil.get("buyCar_"+tableId);
        List<OrderGoods> list = new ArrayList<>();

        buyCarInfo.setList(list);
        redisUtil.set("buyCar_"+tableId,buyCarInfo);
        distributedLockHandler.releaseLock(lock);
    }
    public BuyCarInfo getBuyCarInfo(Long tableId){
        return (BuyCarInfo)redisUtil.get("buyCar_"+tableId);
    }

    public List<String> getBuyCarUsers(Long tableId){
        return (List<String>)redisUtil.get("table_"+tableId);
    }


}
