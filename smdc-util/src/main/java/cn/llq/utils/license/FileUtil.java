package cn.llq.utils.license;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @ClassName FileUtil
 * @Description
 * @Author huhui
 * @Date 2019/10/10 10:17
 */
public class FileUtil {

    /**
     * 获得类的基路径，打成jar包也可以正确获得路径
     *
     * @return
     */
    public static String getBasePath() throws UnsupportedEncodingException {
        //String filePath = FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        String filePath = "E:\\license";
        if (filePath.endsWith(".jar")) {
            filePath = filePath.substring(0, filePath.lastIndexOf("/"));
            filePath = URLDecoder.decode(filePath, "UTF-8"); //解决路径中有空格%20的问题
        }
        File file = new File(filePath);
        filePath = file.getAbsolutePath();
        return filePath;
    }

    /**
     * 二进制数据转文件
     *
     * @param bytes 二进制数据
     */
    public static File byteArrayToFile2(byte[] bytes, String outputFile) throws Exception {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * @Description 获取文件内容
     * @Param filePath 文件路径
     */
    public static byte[] getFileContent(String filePath) {
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            byte[] data = new byte[is.available()];//把所有的数据读取到这个字节当中
            //完整的读取一个文件
            is.read(data);
            return data;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
