package cn.llq.order.service;

import cn.llq.order.BalanceInfo;
import cn.llq.order.OrderInfo;
import cn.llq.order.client.StoreClient;
import cn.llq.order.dao.BalanceInfoMapper;
import cn.llq.order.dao.OrderInfoMapper;
import cn.llq.shop.model.pojo.ShopStorePo;
import cn.llq.utils.response.ResultVO;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CountService {

    @Autowired
    StoreClient storeClient;
    @Autowired
    OrderInfoMapper orderInfoMapper;
    @Autowired
    BalanceInfoMapper balanceInfoMapper;

    /**
     * 今日订单数量
     * @param token
     * @return
     */
    public Object getTodayOrderNum(String token){

        return orderInfoMapper.selectCountByExample(getOrderExample(token));
    }

    /**
     * 今日成交额
     * @param token
     * @return
     */
    public BigDecimal getTodayOrderDue(String token){

        List<OrderInfo> orderInfos = orderInfoMapper.selectByExample(getOrderExample(token));

        BigDecimal bigDecimal = new BigDecimal("0");
        for (int i = 0; i < orderInfos.size(); i++) {
            bigDecimal = bigDecimal.add(orderInfos.get(i).getOrderAmount());
        }
        return bigDecimal;
    }

    /**
     * 订单平均成交额
     * @param token
     * @return
     */
    public Object getTodayOrderAvg(String token){
        List<OrderInfo> orderInfos = orderInfoMapper.selectByExample(getOrderExample(token));

        BigDecimal bigDecimal = new BigDecimal("0");
        if (orderInfos.size()==0){

            return bigDecimal;
        }
        for (int i = 0; i < orderInfos.size(); i++) {
            bigDecimal = bigDecimal.add(orderInfos.get(i).getOrderAmount());
        }
        return bigDecimal.divide(new BigDecimal(orderInfos.size()),BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 门店平均订单量
     * @param token
     * @return
     */
    public Object getStoreAvgNum(String token){
        List<ShopStorePo> storeList = storeClient.getStoreList(1, 9999, token).getBody().getList();

        if (storeList.size()==0){
            return new BigDecimal(0);
        }
        return  new BigDecimal(orderInfoMapper.selectCountByExample(getOrderExample(token))).divide(new BigDecimal(storeList.size()),BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 首页卡片数据
     * @return
     */
    public Object getCardCount(String token){
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("todayDue",getTodayOrderDue(token));
        jsonObject.put("todayOrderAvg",getTodayOrderAvg(token));
        jsonObject.put("todayOrderNum",getTodayOrderNum(token));
        jsonObject.put("storeAvgNum",getStoreAvgNum(token));

        return jsonObject;
    }


    /**
     * 获取饼状图数据
     * @param token
     * @return
     */
    public Object getPieData(String token){
        JSONObject jsonObject = new JSONObject();
        List<ShopStorePo> storeList = storeClient.getStoreList(1, 9999, token).getBody().getList();
        List<Long> collect = storeList.stream().map(ShopStorePo::getId).collect(Collectors.toList());
        collect.add(-1L);

        List<Map<String, Object>> todayStoreDue = orderInfoMapper.getTodayStoreDue(collect);
        JSONArray odayStoreDue = new JSONArray();
        for (int i = 0; i < todayStoreDue.size(); i++) {
            JSONObject object = new JSONObject();
            object.put("name",storeClient.getShopStoreById(Long.valueOf(todayStoreDue.get(i).get("storeId").toString())).getBody().getStoreName());
            object.put("value",new BigDecimal(todayStoreDue.get(i).get("amount").toString()));
            odayStoreDue.add(object);
        }
        jsonObject.put("todayStoreDue",odayStoreDue);

        List<Map<String, Object>> todayStoreNum = orderInfoMapper.getTodayStoreNum(collect);
        JSONArray odayStoreNum = new JSONArray();
        for (int i = 0; i < todayStoreNum.size(); i++) {
            JSONObject object = new JSONObject();
            object.put("name",storeClient.getShopStoreById(Long.valueOf(todayStoreNum.get(i).get("storeId").toString())).getBody().getStoreName());
            object.put("value",new BigDecimal(todayStoreNum.get(i).get("num").toString()));
            odayStoreNum.add(object);
        }
        jsonObject.put("todayStoreNum",odayStoreNum);

        return jsonObject;
    }

    public Object getLineData(String token) {
        JSONObject jsonObject = new JSONObject();

        List<ShopStorePo> storeList = storeClient.getStoreList(1, 9999, token).getBody().getList();
        List<Long> collect = storeList.stream().map(ShopStorePo::getId).collect(Collectors.toList());
        collect.add(-1L);
        List<Map<String, Object>> weekAmountData = orderInfoMapper.getWeekAmountData(collect);

        List<Map<String, Object>> weekNumData = orderInfoMapper.getWeekNumData(collect);

        jsonObject.put("weekNumData",weekNumData);
        jsonObject.put("weekAmountData",weekAmountData);

        return jsonObject;
    }



    public Object getBlanceInfo(){
        BalanceInfo balanceInfo = new BalanceInfo();
        JSONObject o = (JSONObject)JSONObject.toJSON(storeClient.getUserInfo().getBody());
        JSONObject info = o.getJSONObject("info");
        Long aLong = info.getLong("merchantId");
        balanceInfo.setMerchantId(aLong);
        return balanceInfoMapper.selectOne(balanceInfo);
    }







    Example getOrderExample(String token){
        List<ShopStorePo> storeList = storeClient.getStoreList(1, 9999, token).getBody().getList();
        List<Long> collect = storeList.stream().map(ShopStorePo::getId).collect(Collectors.toList());
        collect.add(-1L);
        Example example = new Example(OrderInfo.class);

        Example.Criteria criteria = example.createCriteria();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(new Date());
        criteria.andBetween("createDate", format+" 00:00:00", format+" 23:59:59");
        criteria.andIn("storeId",collect);
        return example;
    }





}
