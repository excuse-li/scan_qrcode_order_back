package cn.llq.member.web;

import cn.llq.member.client.StoreClient;
import cn.llq.shop.model.pojo.ShopStorePo;
import cn.llq.shop.model.pojo.StoreTable;
import cn.llq.utils.SHA1;
import cn.llq.utils.XmlUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.DocumentHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("token")
public class CheckToken {
    @Value("${wx.appId}")
    String appId;

    @Value("${wx.appsecret}")
    String appSecret;

    @Value("${wx.token}")
    String token;
    RestTemplate restTemplate;

    @Autowired
    StoreClient storeClient;
    @GetMapping("")
    public Object getToken(@RequestParam("signature")String signature,@RequestParam("timestamp")String timestamp,@RequestParam("nonce")String nonce,@RequestParam("echostr")String echostr){
        System.out.println("signature = [" + signature + "], timestamp = [" + timestamp + "], nonce = [" + nonce + "], echostr = [" + echostr + "]");
        HashMap<String, String> map = new HashMap<>();
        map.put("token",token);
        map.put("timestamp",timestamp);
        map.put("nonce",nonce);
        System.out.println(gengeSign(map));
        if (signature.equals(gengeSign(map))){
            return echostr;
        }

        return false;
    }

    @PostMapping()
    public Object msg(@RequestBody String msg){
        try {
            JSONObject o = (JSONObject)JSONObject.toJSON(XmlUtil.xmlToMap(msg));
            JSONObject object = new JSONObject();
            System.out.println(o);
            object.put("ToUserName",o.getString("FromUserName"));
            object.put("FromUserName",o.getString("ToUserName"));
            object.put("CreateTime",new Date().getTime());
            object.put("MsgType","news");
            object.put("ArticleCount","1");

            /*object.put("Content","你好呀");
            Map hashMap = object.toJavaObject(Map.class);
            System.out.println(XmlUtil.mapToXml(hashMap).replaceAll("<\\?xml version=\"1\\.0\" encoding=\"UTF-8\" standalone=\"no\"\\?>",""));*/
            if (o.getString("MsgType").equals("event")&&o.getString("Event").equals("SCAN")){
                String[] eventKeys = o.getString("EventKey").split(",");
                String tableId = eventKeys[0];
                String storeId = eventKeys[1];

                JSONObject articles = new JSONObject();
                JSONObject aticla = new JSONObject();

                StoreTable table = storeClient.getTableById(Long.valueOf(tableId)).getBody();

                ShopStorePo body = storeClient.getShopStoreById(Long.valueOf(storeId)).getBody();
                aticla.put("Title","欢迎光临"+body.getStoreName()+",~");
                aticla.put("Description","您的桌号是:"+table.getTableName()+"~");
                aticla.put("PicUrl",body.getImg());
                aticla.put("Url","https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appId+"&redirect_uri=http%3a%2f%2fwww.jiduoke.shop%2f&response_type=code&scope=snsapi_userinfo&state="+o.getString("EventKey")+","+new Date().getTime()+"#wechat_redirect");
                articles.put("item",aticla);
                object.put("Articles",articles);
                System.out.println(XmlUtil.createXmlByMap(object,"xml"));
                return XmlUtil.createXmlByMap(object,"xml");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String gengeSign(Map<String, String> params) {
        SortedMap<String, String> sortedMap = new TreeMap<>(params);
        StringBuilder toSign = new StringBuilder();
        ArrayList<String> strings = new ArrayList<>(sortedMap.values());

        Collections.sort(strings);
        toSign.append(strings.get(0)+strings.get(1)+strings.get(2));
        String val = toSign.toString();
        System.out.println(val);
        String digest = SHA1.encode(val);
        return digest;
    }

}
