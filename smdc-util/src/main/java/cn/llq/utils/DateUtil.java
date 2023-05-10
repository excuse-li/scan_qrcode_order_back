package cn.llq.utils;


//import org.apache.http.util.TextUtils;

import com.google.common.collect.Lists;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 日期工具类
 *
 * @author jm
 */
public class DateUtil {

    /**
     * 根据开始日期,结束日期算出环比前的开始日期
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 环比前的日期
     */
    public static LocalDate getLastDate(LocalDate startDate, LocalDate endDate) {
        Long day = (endDate.toEpochDay() - startDate.toEpochDay());
        if (day <= 0) {
            throw new RuntimeException("选择的日期范围错误!");
        }
        return startDate.minusDays(day);
    }

    /**
     * 时间戳 转LocalDateTime
     */
    public static LocalDateTime getLocalDateTime(Long time) {
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.ofHours(8));
        return localDateTime;
    }


    /**
     * 获取本月的开始时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getBeginDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        return getDayStartTime(calendar.getTime());
    }

    /**
     * 获取本月的结束时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getEndDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() - 1, day);
        return getDayEndTime(calendar.getTime());
    }

    /**
     * 获取今年是哪一年
     */
    public static Integer getNowYear() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return Integer.valueOf(gc.get(1));
    }

    /**
     * 获取本月是哪一月
     */
    public static Integer getNowMonth() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return Integer.valueOf(gc.get(2) + 1);
    }

    /**
     * 获取是哪一月
     */
    public static int getNowMonth(Date date) {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(2) + 1;
    }

    /**
     * 获取是哪一年
     */
    public static Integer getNowYear(Date date) {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return Integer.valueOf(gc.get(1));
    }

    /**
     * 获取某个日期的开始时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Timestamp getDayStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (null != date) {
            calendar.setTime(date);
        }
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar
                .get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    /**
     * 获取某个日期的结束时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Timestamp getDayEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (null != date) {
            calendar.setTime(date);
        }
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar
                .get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Timestamp(calendar.getTimeInMillis());
    }

    /**
     * 将 Date 转换成LocalDateTime
     * atZone()方法返回在指定时区从此Instant生成的ZonedDateTime。
     *
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    /**
     * 微信时间戳转换为日期,注意微信时间戳为秒
     */
    public static LocalDateTime timestampToTime(Long timestamp) {
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.ofHours(8));
        return localDateTime;
    }

    /**
     * Java将Unix时间戳转换成指定格式日期
     *
     * @param timestampString 时间戳 如："1473048265";
     * @param pattern         要格式化的格式 默认："yyyy-MM-dd HH:mm:ss";
     * @return 返回结果 如："2016-09-05 16:06:42";
     */
    public static Date timeStampToDate(String timestampString, String pattern) {
        //if (TextUtils.isEmpty(pattern)) {
        //    pattern = "yyyy-MM-dd HH:mm:ss";
        //}
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String strDate = new SimpleDateFormat(pattern, Locale.CHINA).format(new Date(timestamp));
        return convertStringToDate(pattern, strDate);
    }

    /**
     * 将时间格式的字符串转为Date类型的标准日期
     *
     * @param pattern 时间格式
     * @param strDate 时间字符串
     * @return 解析日期
     */
    public static Date convertStringToDate(String pattern, String strDate) {
        //if (StringUtils.isBlank(strDate)) {
        //    throw new RuntimeException("时间不能为空");
        //}
        //if (StringUtils.isBlank(pattern)) {
        //    throw new RuntimeException("时间格式不能为空");
        //}
        Date date;
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            date = df.parse(strDate);
        } catch (ParseException pe) {
            throw new RuntimeException(pe);
        }
        return (date);
    }

    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
     *
     * @param dateDate
     * @return
     */
    public static String dateToStr(Date dateDate, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 获取指定格式的时间
     *
     * @param dateDate
     * @return
     */
    public static Date dateByPattern(Date dateDate, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String dateString = formatter.format(dateDate);
        try {
            return formatter.parse(dateString);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return dateDate;
    }

    /**
     * @Author huhui
     * @Date 2019/8/21 13:59
     * @Description 获取指定起始时间内的每一天
     * @Param [startTime, endTime, pattern]
     * @Return java.util.List<java.util.Date>
     */
    public static List<Date> findDates(Date startTime, Date endTime, int amount) {
        List<Date> dateList = new ArrayList();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        dateList.add(startTime);

        while (cal.getTime().compareTo(endTime) < 0) {
            cal.add(Calendar.DAY_OF_MONTH, amount);
            if (cal.getTime().compareTo(endTime) < 0) {
                dateList.add(cal.getTime());
            }
        }
        dateList.add(endTime);
        return dateList;
    }

    /**
     * @Author huhui
     * @Date 2019/8/21 14:43
     * @Description 获取指定时间内的日期间隔
     * @Param [startTime, endTime, pattern, amount]
     * @Return java.util.List<java.lang.String>
     */
    public static List<Date> findHours(Date startTime, Date endTime, int amount) {
        List<Date> dateList = new ArrayList();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);

        dateList.add(startTime);

        while (cal.getTime().compareTo(endTime) < 0) {
            cal.add(Calendar.HOUR_OF_DAY, amount);
            dateList.add(cal.getTime());
        }
        return dateList;
    }

    /**
     * @Author huhui
     * @Date 2019/8/21 14:21
     * @Description 两个日期是否是同一天
     * @Param [date1, date2]
     * @Return boolean
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            return isSameDay(cal1, cal2);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 != null && cal2 != null) {
            return cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(6) == cal2.get(6);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime   当前时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 返回某个日期后几分钟的日期
     */
    public static Date getFrontMinute(Date date, int num) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + num);
        return cal.getTime();
    }

    /**
     * 返回某个日期后几天的日期
     */
    public static Date getFrontDay(Date date, int num) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + num);
        return cal.getTime();
    }

    /**
     * 返回某个日期前几天的日期
     */
    public static Date getOutDay(Date date, int num) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - num);
        return cal.getTime();
    }

    /**
     * 返回某个日期后几个月的日期
     */
    public static Date getFrontMonth(Date date, int num) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + num);
        return cal.getTime();
    }

    /**
     * 返回某个日期后几个月的日期
     */
    public static Date getFrontYear(Date date, int num) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + num);
        return cal.getTime();
    }

    /**
     * 获取当前日期的前一天
     *
     * @param currentDate
     * @return
     */
    public static String getBeforeDate(String currentDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 1);
        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        return dayBefore;
    }


    /*** 
          *  
          * @param date 
      * @param dateFormat : e.g:yyyy-MM-dd HH:mm:ss 
      * @return 
      */

    public static String formatDateByPattern(LocalDateTime localDateTime, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formatTimeStr = null;
        Date date = localDateTimeToDate(localDateTime);
        if (date != null) {
            formatTimeStr = sdf.format(date);
        }
        return formatTimeStr;
    }

    /*** 
          *  
          * @param date 
      * @param dateFormat : e.g:yyyy-MM-dd HH:mm:ss 
      * @return 
      */

    public static String formatDateByPattern(Date date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formatTimeStr = null;
        if (date != null) {
            formatTimeStr = sdf.format(date);
        }
        return formatTimeStr;
    }

    public static long dateDiffHour(Date startTime, Date endTime) {
        //按照传入的格式生成一个simpledateformate对象
        long nd = 1000 * 24 * 60 * 60;//一天的毫秒数
        long nh = 1000 * 60 * 60;//一小时的毫秒数
//        long nm = 1000*60;//一分钟的毫秒数
//        long ns = 1000;//一秒钟的毫秒数
        long diff;
        //获得两个时间的毫秒时间差异
        diff = endTime.getTime() - startTime.getTime();
//        long day = diff/nd;//计算差多少天
        long hour = diff % nd / nh;//计算差多少小时
//        long min = diff%nd%nh/nm;//计算差多少分钟
//        long sec = diff%nd%nh%nm/ns;//计算差多少秒//输出结果
//        System.out.println("时间相差："+day+"天"+hour+"小时"+min+"分钟"+sec+"秒。");
        return hour;
    }

    public static long dateDiffMin(Date startTime, Date endTime) {
        //按照传入的格式生成一个simpledateformate对象
        long nd = 1000 * 24 * 60 * 60;//一天的毫秒数
        long nh = 1000 * 60 * 60;//一小时的毫秒数
        long nm = 1000 * 60;//一分钟的毫秒数
//        long ns = 1000;//一秒钟的毫秒数
        long diff;
        //获得两个时间的毫秒时间差异
        diff = endTime.getTime() - startTime.getTime();
//        long day = diff/nd;//计算差多少天
//        long hour = diff%nd/nh;//计算差多少小时
        long min = diff % nd % nh / nm;//计算差多少分钟
//        long sec = diff%nd%nh%nm/ns;//计算差多少秒//输出结果
//        System.out.println("时间相差："+day+"天"+hour+"小时"+min+"分钟"+sec+"秒。");
        return min;
    }

    /*** 
      * convert Date to cron ,eg.  "0 06 10 15 1 ? 2014" 
      * @param date  : 时间点 
      * @return 
      */
    public static String getCron(Date date) {
        String dateFormat = "ss mm HH dd MM ? yyyy";
        return formatDateByPattern(date, dateFormat);
    }

    /*** 
      *  
      * @param date 
      * @param dateFormat : e.g:yyyy-MM-dd HH:mm:ss 
      * @return 
      */

    public static Date localDateTimeToDate(LocalDateTime date) {
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }


    public static Date beforeHourToNowDate(Date date, int hours) {
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(date);
            c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) - hours);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c.getTime();
    }

    /*
     * 校验是否当月最后一天
     * */
    public static boolean isMonthLastDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (calendar.get(Calendar.DATE) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            return true;
        else
            return false;
    }

    /**
     * 获取当年的最后一天
     *
     * @return
     */
    public static Boolean isYearLastDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (calendar.get(Calendar.DATE) == calendar.getActualMaximum(Calendar.DAY_OF_YEAR))
            return true;
        else
            return false;
    }

    /**
     * 获取当年的最后一天
     *
     * @return
     */
    public static Date getCurrYearLast() {
        Calendar currCal = Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        return getYearLast(currentYear);
    }

    /**
     * 获取某年最后一天日期
     *
     * @param year 年份
     * @return Date
     */
    public static Date getYearLast(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        Date currYearLast = calendar.getTime();
        return currYearLast;
    }

    /**
     * 获取指定日期UTC时间戳（毫秒）
     *
     * @return Date
     */

    public static String getUTCTimeStr(Date date) throws Exception {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone("GMT");
        cal.setTimeZone(tz);
        cal.setTime(date);
        return String.valueOf(cal.getTimeInMillis());// 返回的UTC时间戳
    }

    /**
     * 获取某个日期的开始时间UTC
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Long getDayUTCStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (null != date) {
            calendar.setTime(date);
        }
        TimeZone tz = TimeZone.getTimeZone("GMT");
        calendar.setTimeZone(tz);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar
                .get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取某个日期的结束时间UTC
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Long getDayUTCEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (null != date) {
            calendar.setTime(date);
        }
        TimeZone tz = TimeZone.getTimeZone("GMT");
        calendar.setTimeZone(tz);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar
                .get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }


    /**
     * 从大到小
     */
    public static List<LocalDateTime> getAscDateList(LocalDateTime startDate, LocalDateTime endDate) {
        List<LocalDateTime> result = new ArrayList<>();
        long num = endDate.toLocalDate().toEpochDay() - startDate.toLocalDate().toEpochDay();
        if (num > 0) {
            for (int i = 0; i <= num; i++) {
                result.add(endDate.plusDays(i * (-1)));
            }
        }
        return result;
    }

    /**
     * 从大到小
     */
    public static List<LocalDateTime> getDescDateList(LocalDateTime startDate, LocalDateTime endDate) {
        List<LocalDateTime> result = new ArrayList<>();
        long num = endDate.toLocalDate().toEpochDay() - startDate.toLocalDate().toEpochDay();
        if (num > 0) {
            for (int i = 0; i <= num; i++) {
                result.add(startDate.plusDays(i));
            }
        } else {
            result.add(startDate);
        }
        return result;
    }

    /**
     * 格式化时间，返回字符串集合
     */
    public static List<String> formStringListDate(List<LocalDateTime> list, String format) {
        LinkedList<String> resultList = Lists.newLinkedList();
        for (LocalDateTime localDateTime : list) {
            resultList.add(formatDateByPattern(localDateTime, format));
        }
        return resultList;
    }

    /**
     * 显示几天钱几周前几月前几年前
     *
     * @param d
     * @return
     */
    public static String getFriendlytime(LocalDateTime d) {
        long delta = (LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli() - d.toInstant(ZoneOffset.of("+8")).toEpochMilli()) / 1000;
        if (delta <= 0) return "";
        if (delta / (60 * 60 * 24 * 365) > 0) return delta / (60 * 60 * 24 * 365) + "年前";
        if (delta / (60 * 60 * 24 * 30) > 0) return delta / (60 * 60 * 24 * 30) + "个月前";
        if (delta / (60 * 60 * 24 * 7) > 0) return delta / (60 * 60 * 24 * 7) + "周前";
        if (delta / (60 * 60 * 24) > 0) return delta / (60 * 60 * 24) + "天前";
        if (delta / (60 * 60) > 0) return delta / (60 * 60) + "小时前";
        if (delta / (60) > 0) return delta / (60) + "分钟前";
        return "刚刚";
    }

}
