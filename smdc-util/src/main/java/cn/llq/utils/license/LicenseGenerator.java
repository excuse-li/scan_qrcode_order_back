package cn.llq.utils.license;

import java.io.File;
import java.util.Map;

/**
 * @ClassName LicenseGenerator
 * @Description 生成认证文件
 * @Author huhui
 * @Date 2019/10/10 9:47
 */
public class LicenseGenerator {

    /**
     * @Author huhui
     * @Date 2019/10/10 15:26
     * @Description 生成认证文件
     * @Param timeEnd 有效期，用于访问时判断
     * @Return void
     */
    public static void generatorLicense(String dataStr) throws Exception {
        String machineCode = ComputerUtil.getMachineCode();
        Map<String, Object> keyMap = RSAUtil.initKey();
        String publicKey = RSAUtil.getPublicKey(keyMap);
        String privateKey = RSAUtil.getPrivateKey(keyMap);
        System.out.println("machineCode：" + machineCode);
        System.out.println("publicKey：" + publicKey);
        System.out.println("privateKey：" + privateKey);

        //公钥加密，私钥解密
        System.out.println("原文字：" + dataStr);
        byte[] data = dataStr.getBytes();
        byte[] encodedData = RSAUtil.encryptByPublicKey(data, publicKey);
        System.out.println("加密后：\n" + new String(encodedData)); //加密后乱码是正常的

        Base64Util.byteArrayToFile(encodedData, FileUtil.getBasePath() + File.separator + "license.dat");
        System.out.println("license.dat：" + FileUtil.getBasePath() + File.separator + "license.dat");

        //获取生成的.dat文件内容解密
        byte[] encodeByte = FileUtil.getFileContent(FileUtil.getBasePath() + File.separator + "license.dat");
        System.out.println("文件内容：\n" + new String(encodeByte)); //加密后乱码是正常的
        byte[] decodedByte = RSAUtil.decryptByPrivateKey(encodeByte, privateKey);
        System.out.println("文件解密后: " + new String(decodedByte));

        //解密
        byte[] decodedData = RSAUtil.decryptByPrivateKey(encodedData, privateKey);
        String target = new String(decodedData);
        System.out.println("解密后: " + target);
    }

}
