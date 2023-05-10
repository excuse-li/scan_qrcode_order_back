package cn.llq.shop.service;

import cn.llq.shop.client.FileClient;
import cn.llq.shop.client.MemberClient;
import cn.llq.shop.dao.TableMapper;
import cn.llq.shop.model.pojo.ShopStorePo;
import cn.llq.shop.model.pojo.StoreTable;
import cn.llq.utils.response.ResultVO;
import cn.llq.utils.response.exception.APIException;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TableService {
    @Autowired
    TableMapper tableMapper;
    @Autowired
    MemberClient memberClient;
    RestTemplate restTemplate = new RestTemplate();
    @Autowired
    FileClient fileClient;

    @Autowired
    ShopStoreService shopStoreService;


    @GlobalTransactional
    public void addTable(StoreTable storeTable){
        storeTable.setCreateTime(new Date());
        tableMapper.insert(storeTable);
    }

    @GlobalTransactional
    public void updateTable(StoreTable storeTable) {
        tableMapper.updateByPrimaryKeySelective(storeTable);
    }

    public Object getTableById(Long id) {

        try{
            StoreTable storeTable = tableMapper.selectByPrimaryKey(id);
            if (!storeTable.getStatus().equals(3)){
                return  storeTable;
            }
        }catch (NullPointerException e){
            throw new APIException(400,"该桌台不存在或已删除");
        }
        throw new APIException(400,"该桌台不存在或已删除");
    }

    @GlobalTransactional
    public void deleteTable(Long id) {
        StoreTable storeTable = new StoreTable();
        storeTable.setId(id);
        storeTable.setStatus(3);
        tableMapper.updateByPrimaryKey(storeTable);
    }

    public Object getTableList(String name, Integer page, Integer size,Long storeId,String token) {

        PageHelper.startPage(page,size);
        Example example = new Example(StoreTable.class);
        Example.Criteria criteria = example.createCriteria().andLike("tableName", "%" + name + "%");
        if (storeId!=null&&!storeId.equals(0L)){
            criteria.andEqualTo("storeId",storeId);
        }else{
            List<ShopStorePo> list = shopStoreService.listPageByShopStore(new ShopStorePo(), 1, 999, token).getList();
            List<Long> collect = list.stream().map(ShopStorePo::getId).collect(Collectors.toList());
            collect.add(-1L);
            criteria.andIn("storeId",collect);
        }
        criteria.andNotEqualTo("status",3);
        return new PageInfo<>(tableMapper.selectByExample(example));
    }

    public String getQrCode(Long id){
        StoreTable table = tableMapper.selectByPrimaryKey(id);
        if (table==null){
            throw new APIException(400,"餐桌不存在");
        }
        if(table.getQrCode()!=null){
           return table.getQrCode();
        }
        String body = memberClient.getAccessToken().getBody();
        JSONObject param = new JSONObject();
//        param.put("expire_seconds","20000");
        param.put("action_name", "QR_LIMIT_STR_SCENE");
        JSONObject actionInfo = new JSONObject();
        JSONObject scene = new JSONObject();
        scene.put("scene_str",table.getId()+","+table.getStoreId());
        actionInfo.put("scene",scene);
        param.put("action_info",actionInfo);
//        actionInfo.put("scene")
//        param.put("action_name", "QR_SCENE");
        System.out.println("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + body);
        String s = restTemplate.postForObject("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + body, param, String.class);
        System.out.println(s);
        JSONObject parse =(JSONObject) JSONObject.parse(s);
        String ticket = parse.getString("ticket");
        if (ticket!=null){
            byte[] forObject = restTemplate.getForObject("https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket, byte[].class);

            File file = new File(UUID.randomUUID().toString()+".png");
            try {
                System.out.println(file.getAbsolutePath());
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(forObject);
                fileOutputStream.flush();
                fileOutputStream.close();
                DiskFileItem fileItem = (DiskFileItem) new DiskFileItemFactory().createItem("file",
                        MediaType.TEXT_PLAIN_VALUE, true, file.getName());

                try (InputStream input = new FileInputStream(file); OutputStream os = fileItem.getOutputStream()) {
                    IOUtils.copy(input, os);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid file: " + e, e);
                }

                MultipartFile multi = new CommonsMultipartFile(fileItem);

                ResultVO<String> o = fileClient.upLoadFile(multi);
                file.delete();
                table.setQrCode(o.getBody());
                tableMapper.updateByPrimaryKeySelective(table);
                return table.getQrCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
