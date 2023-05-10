package cn.llq.file.web;

import cn.llq.file.config.AliyunOssConfig;
import cn.llq.utils.auth.HasPermition;
import cn.llq.utils.response.ResultVO;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Controller
public class FileInfoWeb {

    @Autowired
    AliyunOssConfig aliyunOssConfig;

    @RequestMapping(value = "file", method = RequestMethod.POST)
    @ResponseBody
    public Object upLoadFile(MultipartFile file){
        OSS oss = new OSSClientBuilder().build(aliyunOssConfig.getEntryPoint(), aliyunOssConfig.getKey(), aliyunOssConfig.getSecret());

        String s = UUID.randomUUID().toString();
        try{
            oss.putObject(aliyunOssConfig.getBucket(),s,file.getInputStream());
            return ResultVO.success("上传成功","https://"+aliyunOssConfig.getBucket()+"."+aliyunOssConfig.getEntryPoint()+"/"+s);
        }catch (IOException e){
            e.printStackTrace();
            return ResultVO.fail("上传失败", e.getMessage());
        }finally {
            oss.shutdown();
        }
    }
}
