package cn.llq.shop.service;

import cn.llq.shop.dao.StorePrintMapper;
import cn.llq.shop.model.pojo.ShopStorePo;
import cn.llq.shop.model.pojo.StorePrint;
import cn.llq.utils.SHA1;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Example;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StorePrintService {
    @Autowired
    StorePrintMapper storePrintMapper;
    @Autowired
    ShopStoreService shopStoreService;
    RestTemplate restTemplate = new RestTemplate();

    @Value("${print.user}")
    String printUser;
    @Value("${print.ukey}")
    String ukey;

    @Value("${xy-print.user}")
    String xyPrintUser;
    @Value("${xy-print.ukey}")
    String xyUkey;

    @GlobalTransactional
    public void addPrint(StorePrint storePrint){
        storePrint.setCreateTime(new Date());
        storePrintMapper.insertSelective(storePrint);
    }

    @GlobalTransactional
    public void deletePrint(Long id){
        storePrintMapper.deleteByPrimaryKey(id);
    }

    public Object getPrintList(String printName, Long storeId, Integer page, Integer size,String token) {
        PageHelper.startPage(page,size);
        Example example = new Example(StorePrint.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("printName","%"+printName+"%");
        List<Long> longs = new ArrayList<>();
        if (storeId==0L){
            longs = shopStoreService.listPageByShopStore(new ShopStorePo(),1,99999,token).getList().stream().map(ShopStorePo::getId).collect(Collectors.toList());
            longs.add(-1L);
        }else{
            longs.add(storeId);
        }
        criteria.andIn("storeId",longs);
        return new PageInfo<>(storePrintMapper.selectByExample(example));
    }

    public void printInfo(Long storeId,String msg){
        System.out.println("storeId = [" + storeId + "], msg = [" + msg + "]");
        LinkedMultiValueMap<String, String> object = new LinkedMultiValueMap<>();
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        long stime = new Date().getTime() / 1000;
//        JSONObject object = new JSONObject();
        object.set("user",printUser);
        object.set("stime",String.valueOf(stime));
        object.set("sig", SHA1.encode(printUser+ukey+stime));
        object.set("apiname", "Open_printMsg");
        StorePrint storePrint = new StorePrint();
        storePrint.setStoreId(storeId);
        List<StorePrint> select = storePrintMapper.select(storePrint);
        object.set("content",msg);
        header.set("Content-Type","application/x-www-form-urlencoded");




        for (int i = 0; i < select.size(); i++) {
            object.set("sn",select.get(i).getPrintSN());
            HttpEntity<Object> entity = new HttpEntity<>(object,header);
            System.out.println(JSONObject.toJSON(object));
            String jsonObject = restTemplate.postForObject("http://api.feieyun.cn/Api/Open/", entity, String.class);
            System.out.println(jsonObject);
        }
    }
}
