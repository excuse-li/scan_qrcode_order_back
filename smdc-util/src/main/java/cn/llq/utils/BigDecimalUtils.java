package cn.llq.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * 算术工具类
 *
 * @author jm
 */
public class BigDecimalUtils {

    /**
     * 环比计算
     *
     * @param count     现在数量
     * @param lastCount 之前数量
     * @return 环比值
     */
    public static String ratioCalculation(Long count, Long lastCount) {
        // 算出环比
        BigDecimal c = BigDecimal.valueOf(count);
        String ratio;
        if (lastCount == 0) {
            ratio = "0";
        } else {
            BigDecimal b = BigDecimal.valueOf(lastCount);
//            BigDecimal bigDecimal = c.subtract(b).divide(b);
            BigDecimal bigDecimal = c.subtract(b).divide(b, 4, BigDecimal.ROUND_UP);
            NumberFormat percent = NumberFormat.getPercentInstance();
            percent.setMaximumFractionDigits(2);
            ratio = percent.format(bigDecimal.doubleValue());
        }
        return ratio;
    }

    /**
     * 判断 b1 是否 >= b2
     */
    public static boolean greaterOrEqualTo(BigDecimal b1, BigDecimal b2) {
        return b1.compareTo(b2) != -1;
    }

    /**
     * 判断 b1 是否 <= b2
     */
    public static boolean lessThanOrEqualTo(BigDecimal b1, BigDecimal b2) {
        return b1.compareTo(b2) != 1;
    }

    /**
     * 判断值是否小于0
     */
    public static boolean isLessThanZero(BigDecimal b) {
        return b.compareTo(BigDecimal.ZERO) == -1;
    }

}
