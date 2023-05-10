package cn.llq.order.service;

import cn.llq.good.menuList.bo.GoodsBo;
import cn.llq.order.OrderGoods;
import cn.llq.order.buyCarDao.BuyCarDao;
import cn.llq.order.client.GoodsClient;
import cn.llq.order.client.MemberClient;
import cn.llq.order.client.TableClient;
import cn.llq.order.client.WsClient;
import cn.llq.shop.model.pojo.StoreTable;
import cn.llq.utils.response.ResultVO;
import cn.llq.utils.response.exception.APIException;
import cn.llq.utils.status.GoodsStatus;
import cn.llq.utils.status.TableUseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class BuyCarService {

    @Autowired
    BuyCarDao buyCarDao;
    @Autowired
    GoodsClient goodsClient;
    @Autowired
    MemberClient memberClient;
    @Autowired
    TableClient tableClient;
    @Autowired
    WsClient wsClient;


    public void joinBuyCar(Long tableId,String token){

        StoreTable storeTable = new StoreTable();
        storeTable.setId(tableId);
        storeTable.setUseStatus(TableUseStatus.JOINING);
        tableClient.updateTable(storeTable);



        buyCarDao.join(token==null?"0":memberClient.getUserInfoByToken().getBody().getId(),tableId);
    }

    public void addGoods(Long tableId,String skuId,Integer num){

        ResultVO<GoodsBo> bySkuId = goodsClient.getBySkuId(skuId);
        GoodsBo goodsBo = bySkuId.getBody();
        if (goodsBo == null){
            throw new APIException(400,"商品不存在或已下架");
        }
        OrderGoods orderGoods = new OrderGoods();
        orderGoods.setNum(1);
        orderGoods.setAddTime(new Date());
        orderGoods.setGoodsDue(goodsBo.getSkuList().get(0).getPrice());
        orderGoods.setGoodsSkuProps(goodsBo.getSkuList().get(0).getProps());
        orderGoods.setSkuId(skuId);
        orderGoods.setGoodsSkuPrice(goodsBo.getSkuList().get(0).getPrice());
        orderGoods.setGoodsImg(goodsBo.getHeadImg());
        orderGoods.setGoodsName(goodsBo.getGoodsName());
        orderGoods.setGoodsStatus(GoodsStatus.START);
        orderGoods.setNum(num);

        wsClient.addGoods(tableId,tableClient.getTableById(tableId).getBody().getStoreId());
        buyCarDao.addGoods(tableId,orderGoods);
    }

    public void deleteBuyCar(Long tableId){
        buyCarDao.deleteBuyCarInfo(tableId);
    }

    public Object getBuyCarInfo(Long tableId){
        return buyCarDao.getBuyCarInfo(tableId);
    }
}
