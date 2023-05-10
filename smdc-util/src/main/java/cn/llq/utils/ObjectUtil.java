package cn.llq.utils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

public class ObjectUtil {

    private static final char UNDERLINE = '_';

    /**
     * 将POJO对象转换成http参数字符串
     *
     * @param o
     * @return
     */
    public static String ObjectToParam(Object o) {
        Class<?> aClass = o.getClass();
        Field[] fields = aClass.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        StringBuffer stringBuffer = new StringBuffer("?");
        for (Field field : fields) {

            try {
                stringBuffer.append(camelToUnderline(field.getName()) + "=" + field.get(o) + "&");
            } catch (Exception e) {

            }

        }
        AccessibleObject.setAccessible(fields, false);
        return stringBuffer.toString().substring(0, stringBuffer.toString().length() - 1);
    }

    /**
     * 下划线转驼峰命名
     *
     * @param param
     * @return
     */
    public static String underlineToCamel(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = Character.toLowerCase(param.charAt(i));
            if (c == UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰转下划线
     *
     * @param param
     * @return
     */
    public static String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
