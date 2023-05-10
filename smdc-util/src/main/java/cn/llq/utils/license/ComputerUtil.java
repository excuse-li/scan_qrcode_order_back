package cn.llq.utils.license;

import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.util.Scanner;

/**
 * @ClassName ComputerUtil
 * @Description
 * @Author huhui
 * @Date 2019/10/10 9:48
 */
public class ComputerUtil {

    /**
     * @Author huhui
     * @Date 2019/10/10 9:48
     * @Description 获取CPU序列号
     * @Param
     * @Return
     */
    public static String getCPUSerial() throws IOException {
        Process process = Runtime.getRuntime().exec(
                new String[]{"wmic", "cpu", "get", "ProcessorId"});
        process.getOutputStream().close();
        Scanner sc = new Scanner(process.getInputStream());
        String property = sc.next();
        String serial = sc.next();
        System.out.println(property + ": " + serial);
        return serial;
    }

    /**
     * @Author huhui
     * @Date 2019/10/10 9:52
     * @Description 获取机器码，CPU序列号md5加密大写
     * @Param
     * @Return
     */
    public static String getMachineCode() throws IOException {
        String cpuSerial = getCPUSerial();
        return DigestUtils.md5DigestAsHex(cpuSerial.getBytes()).toUpperCase();
    }

}
