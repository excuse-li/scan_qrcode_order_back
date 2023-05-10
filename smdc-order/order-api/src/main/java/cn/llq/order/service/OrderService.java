package cn.llq.order.service;

import cn.llq.order.*;
import cn.llq.order.buyCarDao.BuyCarDao;
import cn.llq.order.client.MemberClient;
import cn.llq.order.client.StoreClient;
import cn.llq.order.client.TableClient;
import cn.llq.order.client.WsClient;
import cn.llq.order.dao.*;
import cn.llq.order.vo.OrderVo;
import cn.llq.shop.model.pojo.ShopStorePo;
import cn.llq.shop.model.pojo.StoreTable;
import cn.llq.utils.DistributedLockHandler;
import cn.llq.utils.MD5Util;
import cn.llq.utils.XmlUtil;
import cn.llq.utils.opt.Lock;
import cn.llq.utils.response.exception.APIException;
import cn.llq.utils.status.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Example;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    OrderGoodsMapper orderGoodsMapper;

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    OrderMemberMapper orderMemberMapper;
    @Autowired
    BuyCarDao buyCarDao;
    @Autowired
    StoreClient storeClient;
    @Autowired
    MemberClient memberClient;
    @Value("${wx.payUrl}")
    String payUrl;
    @Value("${wx.mchId}")
    String mchId;
    @Value("${wx.appId}")
    String appId;
    @Value("${wx.notifyUrl}")
    String notifyUrl;
    @Value("${wx.payKey}")
    String wxKey;


    @Autowired
    BalanceInfoMapper balanceInfoMapper;

    @Autowired
    DistributedLockHandler distributedLockHandler;
    @Autowired
    PayOrderMapper payOrderMapper;

    RestTemplate restTemplate = new RestTemplate();
    @Autowired
    TableClient tableClient;
    @Autowired
    WsClient wsClient;

    @GlobalTransactional
    public void createOrder(Long tableId,Long storeId){

        OrderInfo byTableId = this.getByNotTableId(tableId);
        OrderVo orderVo = new OrderVo();
        if (byTableId!=null){
            // TODO 已下单处理
            BigDecimal orderAmount = byTableId.getOrderAmount();

            BuyCarInfo buyCarInfo = buyCarDao.getBuyCarInfo(tableId);
            List<OrderGoods> list = buyCarInfo.getList();
            BigDecimal bigDecimal = new BigDecimal(0);
            if (buyCarInfo==null||buyCarInfo.getList().size()==0){
                throw new APIException(500,"该桌还未添加菜品！");
            }
            orderVo.setCreateDate(new Date());
            orderVo.setOrderSrc(OrderSrc.SCANER);
            orderVo.setPayStatus(OrderPayStatus.NOT_PAY);
            orderVo.setStoreId(storeId);
            orderVo.setTableId(tableId);
            orderVo.setId(byTableId.getId());
            orderVo.setCreateMemberId(buyCarInfo.getCreateUserId());
            orderVo.setUserStatus(OrderUserStatus.NORMAL);


            for (int i = 0; i < list.size(); i++) {
                OrderGoods orderGoods = list.get(i);
                orderGoods.setOrderId(byTableId.getId());
                orderAmount = orderAmount.add(orderGoods.getGoodsDue());
                orderGoods.setOrderId(byTableId.getId());
                orderGoodsMapper.insert(orderGoods);
                bigDecimal = bigDecimal.add(orderGoods.getGoodsDue());
            }
            orderVo.setOrderAmount(bigDecimal);
            orderVo.setList(list);
            this.printOrder(orderVo);
            StoreTable storeTable = new StoreTable();
            storeTable.setId(tableId);
            storeTable.setUseStatus(TableUseStatus.USEING);

            buyCarDao.deleteBuyCarGoods(tableId);


            tableClient.updateTable(storeTable);

            byTableId.setOrderAmount(orderAmount);
            orderInfoMapper.updateByPrimaryKeySelective(byTableId);
            wsClient.emitOrder(tableId,storeId);
            return ;
        }
        StoreTable storeTable = new StoreTable();
        storeTable.setId(tableId);
        storeTable.setUseStatus(TableUseStatus.USEING);
        tableClient.updateTable(storeTable);
        BuyCarInfo buyCarInfo = buyCarDao.getBuyCarInfo(tableId);
        if (buyCarInfo==null||buyCarInfo.getList().size()==0){
            throw new APIException(500,"该桌还未添加菜品！");
        }
        List<OrderGoods> list = buyCarInfo.getList();
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setOrderSrc(OrderSrc.SCANER);
        orderInfo.setPayStatus(OrderPayStatus.NOT_PAY);
        orderInfo.setStoreId(storeId);
        orderInfo.setTableId(tableId);
        orderInfo.setCreateMemberId(buyCarInfo.getCreateUserId());
        orderInfo.setUserStatus(OrderUserStatus.NORMAL);

        BigDecimal due = new BigDecimal("0");
        for (int i = 0; i < list.size(); i++) {
            OrderGoods orderGoods = list.get(i);
            orderGoods.setOrderId(orderInfo.getId());
            due = due.add(orderGoods.getGoodsDue());
            orderGoodsMapper.insert(orderGoods);
        }
        List<String> buyCarUsers = buyCarDao.getBuyCarUsers(tableId);
        for (int i = 0; i < buyCarUsers.size(); i++) {
            OrderMember orderMember = new OrderMember();
            orderMember.setOrderId(orderInfo.getId());
            orderMember.setMemberId(buyCarUsers.get(i));
            orderMember.setType("一般用户");
            orderMemberMapper.insert(orderMember);
        }
        orderInfo.setOrderAmount(due);
        BeanUtils.copyProperties(orderInfo,orderVo);
        orderVo.setList(list);
        this.printOrder(orderVo);
        buyCarDao.deleteBuyCarGoods(tableId);
        wsClient.emitOrder(tableId,storeId);
        orderInfoMapper.insertSelective(orderInfo);
    }

    public JSONObject getByTableId(Long tableId) {
        OrderInfo info = OrderInfo.newInstance();
        info.setTableId(tableId);
        info.setPayStatus(OrderPayStatus.NOT_PAY);
        OrderInfo orderInfo = orderInfoMapper.selectOne(info);
        if (orderInfo==null){
            return null;
        }
        JSONObject o =(JSONObject) JSONObject.toJSON(orderInfo);
        OrderGoods orderGoods = OrderGoods.newInstance();
        orderGoods.setOrderId(orderInfo.getId());
        o.put("list",orderGoodsMapper.select(orderGoods));
        o.put("table",tableClient.getTableById(orderInfo.getTableId()).getBody());
        o.put("storeInfo",storeClient.getShopStoreById(orderInfo.getStoreId()).getBody());

        return o;
    }

    public OrderInfo getByNotTableId(Long tableId) {
        OrderInfo info = OrderInfo.newInstance();
        info.setTableId(tableId);
        info.setPayStatus(OrderPayStatus.NOT_PAY);
        OrderInfo orderInfo = orderInfoMapper.selectOne(info);

        return orderInfo;
    }

    public OrderVo getById(String id) {
        OrderInfo orderInfo = orderInfoMapper.selectByPrimaryKey(id);
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(orderInfo,orderVo);
        OrderGoods orderGoods = OrderGoods.newInstance();
        orderGoods.setOrderId(id);
        List<OrderGoods> select = orderGoodsMapper.select(orderGoods);
        orderVo.setList(select);
        return orderVo;
    }

    public Object getOrderListByParam(String token, String orderNo, Long storeId, String startTime, String endTime, Integer page, Integer size) {
        PageHelper.startPage(page,size);
        List<Long> stores = new ArrayList<>();
        if (storeId!=0L){
            stores.add(storeId);
        }else{
            stores = storeClient.getStoreList(1,99999,token).getBody().getList().stream().map(ShopStorePo::getId).collect(Collectors.toList());
        }
        Example example = new Example(OrderInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("id","%"+orderNo+"%");
        criteria.andIn("storeId",stores);
        criteria.andLessThan("createDate",endTime);
        criteria.andGreaterThan("createDate",startTime);
        return new PageInfo<>(orderInfoMapper.selectByExample(example));
    }

    public Object userList(Integer page,Integer size) {

        String userId = memberClient.getUserInfoByToken().getBody().getId();
        OrderMember orderMember = OrderMember.newInstance();
        orderMember.setMemberId(userId);
        List<OrderMember> select = orderMemberMapper.select(orderMember);
        List<String> collect = select.stream().map(OrderMember::getOrderId).collect(Collectors.toList());
        if(collect.size()==0){
            return new PageInfo<>();
        }
        PageHelper.startPage(page,size);
        Example example = new Example(OrderInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",collect);
        criteria.andNotEqualTo("userStatus",OrderUserStatus.DELETE);
        example.orderBy("createDate").desc();
        PageInfo<OrderInfo> pageInfo = new PageInfo<>(orderInfoMapper.selectByExample(example));
        List<OrderInfo> list = pageInfo.getList();
        PageInfo<Object> objectPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo,objectPageInfo);
        JSONArray array = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            JSONObject o = (JSONObject)JSONObject.toJSON(list.get(i));
            try {

                o.put("tableInfo",storeClient.getTableById(list.get(i).getTableId()).getBody());
            } catch (Exception e) {
                StoreTable storeTable = new StoreTable();
                storeTable.setTableName("该桌台已删除");
                o.put("tableInfo",storeTable);
            }

            o.put("storeInfo",storeClient.getShopStoreById(list.get(i).getStoreId()).getBody());
            OrderGoods orderGoods = OrderGoods.newInstance();
            orderGoods.setOrderId(list.get(i).getId());
            orderGoods.setGoodsStatus(OrderGoodsStatus.NORMAL);
            o.put("list",orderGoodsMapper.select(orderGoods));
            array.add(o);
        }
        objectPageInfo.setList(array);
        return objectPageInfo;
    }

    public void rejectedGood(String orderGoodsId, Integer num) {
        //TODO
        OrderGoods orderGoods = orderGoodsMapper.selectByPrimaryKey(orderGoodsId);
        OrderGoods goods = new OrderGoods();
        BeanUtils.copyProperties(orderGoods,goods);
        orderGoods.setGoodsStatus(GoodsStatus.DELETE);
        goods.setNum(goods.getNum()-num);
        goods.setId(UUID.randomUUID().toString());
        goods.setGoodsDue(goods.getGoodsDue().subtract(goods.getGoodsSkuPrice().multiply(new BigDecimal(num))));
        OrderInfo orderInfo = orderInfoMapper.selectByPrimaryKey(orderGoods.getOrderId());
        orderInfo.setOrderAmount( orderInfo.getOrderAmount().subtract(goods.getGoodsSkuPrice().multiply(new BigDecimal(num))));
        orderInfoMapper.updateByPrimaryKey(orderInfo);
        orderGoodsMapper.insert(goods);
        orderGoodsMapper.updateByPrimaryKey(orderGoods);
    }

    @GlobalTransactional
    public Object generaPayOrder(String id,String token)  {
        Lock lock = new Lock("orderIdPay" + id, id);
        distributedLockHandler.tryLock(lock);
        String openId = memberClient.getUserInfoByToken().getBody().getOpenId();
        OrderInfo orderInfo = orderInfoMapper.selectByPrimaryKey(id);
        if (orderInfo.getPayStatus().equals(OrderPayStatus.ALREADY_PAY)){
            throw new APIException(400,"订单已支付");
        }
        PayOrder payOrder = new PayOrder();
        String str = getStr(20);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appid",appId);
        jsonObject.put("mch_id",mchId);
        jsonObject.put("device_info","WEB");
        jsonObject.put("nonce_str", str);
        try {
            jsonObject.put("body", URLEncoder.encode("极哆客-扫码点餐","utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        jsonObject.put("attach", id);
        jsonObject.put("sign_type", "MD5");
        jsonObject.put("out_trade_no", payOrder.getId());
        jsonObject.put("total_fee", orderInfo.getOrderAmount().multiply(new BigDecimal(100)).intValue()+"");
//        jsonObject.put("total_fee", 1);
        try {
            jsonObject.put("spbill_create_ip", InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        jsonObject.put("notify_url", notifyUrl);
        jsonObject.put("trade_type", "JSAPI");
        jsonObject.put("openid", openId);
        jsonObject.put("sign",createSign(new TreeMap(jsonObject)));
        payOrder.setStatus(OrderPayStatus.NOT_PAY);
        payOrder.setOrderId(id);
        payOrder.setCreateTime(new Date());

        LinkedMultiValueMap<Object, Object> header = new LinkedMultiValueMap<>();
        header.set("Content-Type","text/xml;charset=utf-8");
        HttpEntity<Object> request = new HttpEntity(jsonObject,header);
        JSONObject object = new JSONObject();
        String post = null;
        try {
            post = restTemplate.postForObject(payUrl, XmlUtil.mapToXml(jsonObject).replaceAll("<\\?xml version=\"1\\.0\" encoding=\"UTF-8\" standalone=\"no\"\\?>", ""), String.class);
            post = new String(post.getBytes("ISO-8859-1"),"utf-8");
            Map<String, String> stringStringMap = XmlUtil.xmlToMap(post);
            object.put("package","prepay_id="+stringStringMap.get("prepay_id"));
            if(!stringStringMap.get("return_code").equals("SUCCESS")){
                throw new APIException(403,"生成支付单失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new APIException(403,"生成支付单失败");
        }

        object.put("appId",appId);
        object.put("timeStamp",new Date().getTime());
        object.put("nonceStr",str);
//        object.put("package",null);
        object.put("signType","MD5");
        //
        object.put("paySign",createSign(new TreeMap(object)));
        payOrderMapper.insertSelective(payOrder);
        return object;
    }

    public Object offLinePay(String orderId,BigDecimal amount,Integer payType){
        Lock lock = new Lock("orderIdPay" + orderId, orderId);
        distributedLockHandler.tryLock(lock);
        OrderInfo orderInfo1 = orderInfoMapper.selectByPrimaryKey(orderId);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId( orderId);
        orderInfo.setPaymentAmount(amount);
        orderInfo.setPaymentTime(new Date());
        orderInfo.setPaymentType(payType);
        orderInfo.setPayStatus(OrderPayStatus.ALREADY_PAY);
        StoreTable storeTable = new StoreTable();
        storeTable.setId(orderInfo1.getTableId());
        storeTable.setUseStatus(TableUseStatus.NOMARL);
        wsClient.emitOrder(orderInfo1.getTableId(),orderInfo1.getStoreId());
        tableClient.updateTable(storeTable);
        orderInfoMapper.updateByPrimaryKeySelective(orderInfo);
        distributedLockHandler.releaseLock(lock);
        return null;
    }

    @Transactional
    public Object payResult(String msg) throws Exception {
        Map<String, String> stringStringMap = XmlUtil.xmlToMap(msg);
        String out_trade_no = stringStringMap.get("out_trade_no");
        PayOrder payOrder = new PayOrder();
        payOrder.setStatus(OrderPayStatus.ALREADY_PAY);
        payOrder.setId(out_trade_no);

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId( stringStringMap.get("attach"));
        orderInfo.setPaymentAmount(new BigDecimal(stringStringMap.get("total_fee")).divide(new BigDecimal(100)));
        orderInfo.setPaymentTime(new Date());
        orderInfo.setPaymentType(1);
        orderInfo.setPayStatus(OrderPayStatus.ALREADY_PAY);
        OrderInfo orderInfo1 = orderInfoMapper.selectByPrimaryKey(orderInfo.getId());
        if (stringStringMap.get("result_code")!=null && stringStringMap.get("result_code").equals("SUCCESS")){
            StoreTable storeTable = new StoreTable();
            storeTable.setId(orderInfo1.getTableId());
            storeTable.setUseStatus(TableUseStatus.NOMARL);
            tableClient.updateTable(storeTable);
            orderInfoMapper.updateByPrimaryKeySelective(orderInfo);
            payOrderMapper.updateByPrimaryKeySelective(payOrder);
            OrderInfo orderInfo2 = orderInfoMapper.selectByPrimaryKey(orderInfo.getId());
            Long merchantId = storeClient.getShopStoreById(orderInfo2.getStoreId()).getBody().getMerchantId();

            BalanceInfo balanceInfo = new BalanceInfo();
            balanceInfo.setMerchantId(merchantId);
            BalanceInfo balanceInfo1 = balanceInfoMapper.selectOne(balanceInfo);
            Lock lock = new Lock("payLock" + merchantId, merchantId.toString());
            distributedLockHandler.tryLock(lock);
            if (balanceInfo1==null){
                balanceInfo.setBalance(orderInfo.getPaymentAmount());
                balanceInfo.setUpdateTime(new Date());
                balanceInfoMapper.insert(balanceInfo);
            }else{
                balanceInfo1.setUpdateTime(new Date());
                balanceInfo1.setBalance(balanceInfo1.getBalance().add(orderInfo.getPaymentAmount().subtract(orderInfo.getPaymentAmount().multiply(new BigDecimal("0.006")).setScale(2,BigDecimal.ROUND_UP))));
                balanceInfoMapper.updateByPrimaryKeySelective(balanceInfo1);
            }
            distributedLockHandler.releaseLock(lock);

        }




        JSONObject jsonObject = new JSONObject();
        jsonObject.put("return_code","SUCCESS");
        Lock lock = new Lock("orderIdPay" + orderInfo.getId(), orderInfo.getId());
        distributedLockHandler.releaseLock(lock);
        return XmlUtil.mapToXml(jsonObject).replaceAll("<\\?xml version=\"1\\.0\" encoding=\"UTF-8\" standalone=\"no\"\\?>", "");

    }


    public static String getStr(int n) { // 定义需要生成字符串的位数
        String s = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random ran = new Random();
        char[] ca = new char[n]; // 定义一个字符数组，用于String创建的构造子
        for (int i = 0; i < ca.length; i++) {
            ca[i] = s.charAt(ran.nextInt(62));
        }
        return new String(ca);
    }
    public static String getSign(Map param){

        String str= "";
        TreeMap<String, String> stringStringTreeMap = new TreeMap<>(param);
        ArrayList<String> strings = new ArrayList<>(stringStringTreeMap.keySet());
//        String[] array = (String[])strings.toArray();
        Collections.sort(strings);
        for (int i = 0; i < strings.size(); i++) {
            str+=strings.get(i)+"="+stringStringTreeMap.get(strings.get(i))+"&";
        }
        return str.substring(0,str.length()-1);
    }

    public void printOrder(OrderVo byId){



        HashMap<String, Object> param = new HashMap<>();
        param.put("storeId",byId.getStoreId());

        String msg = "<CB>"+storeClient.getShopStoreById(byId.getStoreId()).getBody().getStoreName()+"</CB><BR>";
        msg+="订单编号<BR>"+byId.getId()+"<BR>";
        msg+="--------------------------------<BR>";
        List<OrderGoods> list = byId.getList();
        for (int i = 0; i < list.size(); i++) {
            msg+= list.get(i).getGoodsName()+"";
            if (list.get(i).getGoodsSkuProps()!=null){
                JSONArray parse = (JSONArray)JSONObject.parse(list.get(i).getGoodsSkuProps());
                if(parse!=null){
                    for (int j = 0; j < parse.size(); j++) {
                        JSONObject o = (JSONObject)parse.get(j);
                        msg+="["+o.getString("propValue")+"]";
                    }
                }
            }

            msg+="  "+list.get(i).getGoodsSkuPrice().toString()+"  "+list.get(i).getNum()+"  "+list.get(i).getGoodsDue()+"<BR>";
        }
        msg+="--------------------------------<BR>";

        msg+="合计金额："+byId.getOrderAmount();
        msg+="<CUT>";
        param.put("msg",msg);
        storeClient.printMsg(param);

    }
    /**
     * 微信支付签名算法sign
     * @param parameters
     * @return
     */
    public  String createSign(SortedMap<String,String> parameters){
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }

        sb.append("key=" + wxKey);
        String sign = md5Password(sb.toString()).toUpperCase();
        return sign;
    }


    public static String md5Password(String key) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }


}
