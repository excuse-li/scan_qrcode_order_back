package cn.llq.utils.license;

import org.apache.commons.lang.ArrayUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.security.rsa.RSAPrivateKeyImpl;
import sun.security.rsa.RSAPublicKeyImpl;
import sun.security.util.DerValue;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

/**
 * @ClassName LicenseUtil
 * @Description
 * @Author huhui
 * @Date 2019/10/31 16:39
 */
public class LicenseUtil {

    public static final int ENCODE_MAX = 117;
    public static final int DECODE_MAX = 128;

    /**
     * @Author huhui
     * @Date 2019/10/31 16:47
     * @Description 获取加密字符串
     * @Param
     * @Return
     */
    public static String getEncodedData(String dataStr) throws Exception {
        Map<String, Object> keyMap = RSAUtil.initKey();
        String publicKey = RSAUtil.getPublicKey(keyMap);
        System.out.println("publicKey：" + publicKey);

        //公钥加密，私钥解密
        System.out.println("原文字：" + dataStr);
        byte[] data = dataStr.getBytes();
        byte[] encodedData = RSAUtil.encryptByPublicKey(data, publicKey);
        String encodedDataStr = new String(encodedData);
        System.out.println("加密后：\n" + encodedDataStr); //加密后乱码是正常的

        return encodedDataStr;
    }

    /**
     * @Author huhui
     * @Date 2019/11/1 11:00
     * @Description 获取解密字符串
     * @Param
     * @Return
     */
    public static String getDecodedData(String encodedDataStr) throws Exception {
        Map<String, Object> keyMap = RSAUtil.initKey();
        String privateKey = RSAUtil.getPrivateKey(keyMap);
        System.out.println("privateKey：" + privateKey);
        //解密
        byte[] encodedDate = encodedDataStr.getBytes();
        byte[] decodedData = RSAUtil.decryptByPrivateKey(encodedDate, privateKey);
        String decodedDataStr = new String(decodedData);
        System.out.println("解密后: " + decodedDataStr);
        return decodedDataStr;
    }

    public static String decode(String encBase64String, String privateKeyString) throws Exception {
        BASE64Decoder base64Decoder = new BASE64Decoder();
        byte[] desEncodeRead = base64Decoder.decodeBuffer(privateKeyString);//
        DerValue d = new DerValue(desEncodeRead);
        RSAPrivateKey privateKey = (RSAPrivateKey) RSAPrivateKeyImpl.parseKey(d);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] src = base64Decoder.decodeBuffer(encBase64String);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < src.length; i += DECODE_MAX) {
            byte[] toDecodeSegment = ArrayUtils.subarray(src, i, i + DECODE_MAX);
            byte[] destByte = cipher.doFinal(toDecodeSegment);
            out.write(destByte);
        }
        byte[] decode = out.toByteArray();
        return new String(decode, "UTF-8");
    }


    public static String encode(String src, String publicKey) throws Exception {
        BASE64Decoder base64Decoder = new BASE64Decoder();
        BASE64Encoder base64Encoder = new BASE64Encoder();
        byte[] desEncodeRead = base64Decoder.decodeBuffer(publicKey);//
        DerValue d = new DerValue(desEncodeRead);
        RSAPublicKey p = (RSAPublicKey) RSAPublicKeyImpl.parse(d);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, p);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] toEncode = src.getBytes();
        for (int i = 0; i < toEncode.length; i += ENCODE_MAX) {
            byte[] toEncodeSegment = ArrayUtils.subarray(toEncode, i, i + ENCODE_MAX);
            byte[] ecodeSegemnt = cipher.doFinal(toEncodeSegment);
            out.write(ecodeSegemnt);
        }
        byte[] encode = out.toByteArray();
        return base64Encoder.encode(encode);
    }

}
