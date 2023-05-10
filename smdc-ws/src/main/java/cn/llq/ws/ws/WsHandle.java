package cn.llq.ws.ws;

import cn.llq.ws.client.MemberClient;
import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.timeout.IdleStateEvent;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.yeauty.annotation.*;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ServerEndpoint(path = "/{storeId}/{tableId}/{token}",port = "65526")
@Component
public class WsHandle {

    public static final Map<Long, List<String>> rooms = new HashMap<>();
    public static final Map<String, Session> sessions = new HashMap<>();
    public static final Map<Long, Long> tableStore = new HashMap<>();
    public static final Map<Long, Session> storeAdmin = new HashMap<>();
    @Autowired
    MemberClient memberClient;

    @BeforeHandshake
    public void handshake(Session session ,@PathVariable("tableId")Long tableId,@PathVariable("storeId")Long storeId,@PathVariable("token")String token){

    }

    public static void addGoods(Long tableId,Long storeId){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type","addGood");
        jsonObject.put("table",tableId);
        if (storeAdmin.get(storeId)!=null){
            storeAdmin.get(storeId).sendText(jsonObject.toJSONString());
        }
        if (rooms.get(tableId)!=null){
            for (int i = 0; i < rooms.get(tableId).size(); i++) {
                sessions.get(rooms.get(tableId).get(i)).sendText(jsonObject.toJSONString());
            }
        }

    }

    public static void emitOrder(Long tableId,Long storeId){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type","emitOrder");
        jsonObject.put("table",tableId);
        if ( rooms.get(tableId)!=null){
            for (int i = 0; i < rooms.get(tableId).size(); i++) {
                sessions.get(rooms.get(tableId).get(i)).sendText(jsonObject.toJSONString());
            }
        }

//            Session session = storeAdmin.get(tableStore.get(tableId));
        Session session = storeAdmin.get(storeId);
        if (session!=null){
            session.sendText(jsonObject.toJSONString());
        }
    }

    @OnOpen
    public void onOpen(Session session,@PathVariable("tableId")Long tableId,@PathVariable("storeId")Long storeId,@PathVariable("token")String token){

        if (tableId==0L&&token.equals("0")){
            storeAdmin.put(storeId,session);
        }else{
            if (rooms.get(tableId)==null){
                rooms.put(tableId,new ArrayList());
            }
            tableStore.put(tableId,storeId);
            String id = memberClient.getUserInfoByToken(token).getBody().getId();
            sessions.put(id,session);
            rooms.get(tableId).add(id);
        }

    }

    @OnClose
    public void onClose(@PathVariable("tableId")Long tableId,@PathVariable("storeId")Long storeId,@PathVariable("token")String token) {
        if (tableId==0L&&token.equals("0")){
            storeAdmin.remove(storeId);
        }else{
            String id = memberClient.getUserInfoByToken(token).getBody().getId();
            sessions.remove(id);
            if (rooms.get(tableId)==null){
                rooms.put(tableId,new ArrayList());
            }
            rooms.get(tableId).remove(id);
            tableStore.remove(tableId);
        }

    }

    @OnError
    public void onError(Session session, Throwable throwable) {

    }

    @OnMessage
    public void onMessage(@PathVariable("tableId")Long tableId,@PathVariable("storeId")Long storeId,@PathVariable("token")String token,Session session, String message) {

//        {   //购物车
//           type:'buycar',
//           table: 1,
//        }
//        {
//          type:'order',
//          table:1
//        }
//        {
//            type: 'heart'
//        }


        JSONObject msg = (JSONObject)JSONObject.parse(message);

        if (tableId==0L&&token.equals("0")){

        }else{
            if (msg.getString("type").equals("heart")){
                session.sendText(message);
            }
        }
    }

    @OnBinary
    public void onBinary(Session session, byte[] bytes) {
        for (byte b : bytes) {
            System.out.println(b);
        }
        session.sendBinary(bytes); 
    }

    @OnEvent
    public void onEvent(Session session, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    System.out.println("read idle");
                    break;
                case WRITER_IDLE:
                    System.out.println("write idle");
                    break;
                case ALL_IDLE:
                    System.out.println("all idle");
                    break;
                default:
                    break;
            }
        }
    }

}