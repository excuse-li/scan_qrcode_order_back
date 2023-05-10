package cn.llq.utils;


import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 集合工具类
 */
public class CollectionUtil {

    /**
     * Map中的value倒叙排序
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortReversedByValue(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();
        map.entrySet().stream()
                .sorted(Map.Entry.<K, V>comparingByValue()
                        .reversed()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    /**
     * Map中的key倒叙排序
     */
    public static <K extends Comparable<? super K>, V> Map<K, V> sortReversedByKey(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();
        map.entrySet().stream()
                .sorted(Map.Entry.<K, V>comparingByKey()
                        .reversed()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    /**
     * Map中的value顺序排序
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();
        map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue()
                ).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    /**
     * Map中的key顺序排序
     */
    public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();
        map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()
                ).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    /**
     * 构造时间模型
     */
    public static Map<String, Long> getHourModel() {
        HashMap<String, Long> map = new HashMap(12);
        for (int i = 0; i < 12; i++) {
            int hour = 2 * (i + 1);
            map.put((hour + ":00"), 0L);
        }
        return map;
    }

    /**
     * 映射时间模型
     */
    public static Map<String, Long> mapHourModel(Map<String, Long> map) {
        Map<String, Long> model = getHourModel();
        for (String key : map.keySet()) {
            if (model.containsKey(key)) {
                model.put(key, map.get(key));
            }
        }
        return model;
    }


    /**
     * 截取list集合，返回list集合
     *
     * @param tList  (需要截取的集合)
     * @param subNum (每次截取的数量)
     * @return
     */
    public static <T> List<List<T>> subList(List<T> tList, Integer subNum) {
        // 新的截取到的list集合
        List<List<T>> tNewList = Lists.newLinkedList();
        // 要截取的下标上限
        Integer priIndex = 0;
        // 要截取的下标下限
        Integer lastIndex = 0;
        // 每次插入list的数量
        // Integer subNum = 500;
        // 查询出来list的总数目
        Integer totalNum = tList.size();
        // 总共需要插入的次数
        Integer insertTimes = totalNum / subNum;
        List<T> subNewList = Lists.newLinkedList();
        for (int i = 0; i <= insertTimes; i++) {
            // [0--20) [20 --40) [40---60) [60---80) [80---100)
            priIndex = subNum * i;
            lastIndex = priIndex + subNum;
            // 判断是否是最后一次
            if (i == insertTimes) {
                subNewList = tList.subList(priIndex, tList.size());
            } else {
                // 非最后一次
                subNewList = tList.subList(priIndex, lastIndex);
            }
            if (subNewList.size() > 0) {
                tNewList.add(subNewList);
            }
        }
        return tNewList;
    }

    /**
     * 截取list集合，返回map集合
     *
     * @param tList  (需要截取的集合)
     * @param subNum (每次截取的数量)
     * @return
     */
    public static <T> Map<Integer, List<T>> subListToMap(List<T> tList, Integer subNum) {
        // 新的截取到的list集合
        //List<List<T>> tNewList = new ArrayList<List<T>>();
        Map<Integer, List<T>> newTlsMap = new HashMap<Integer, List<T>>();
        // 要截取的下标上限
        Integer priIndex = 0;
        // 要截取的下标下限
        Integer lastIndex = 0;
        // 每次插入list的数量
        // Integer subNum = 500;
        // 查询出来list的总数目
        Integer totalNum = tList.size();
        // 总共需要插入的次数
        Integer insertTimes = totalNum / subNum;
        List<T> subNewList = new LinkedList<T>();
        for (int i = 0; i <= insertTimes; i++) {
            // [0--20) [20 --40) [40---60) [60---80) [80---100)
            priIndex = subNum * i;
            lastIndex = priIndex + subNum;
            // 判断是否是最后一次
            if (i == insertTimes) {
                //logger.info(priIndex + "," + tList.size());
                //logger.info("--------------------------------------");
                subNewList = tList.subList(priIndex, tList.size());
            } else {
                // 非最后一次
                //logger.info("最后一次截取："+priIndex + "," + lastIndex);
                //logger.info("***************************************");
                subNewList = tList.subList(priIndex, lastIndex);

            }
            if (subNewList.size() > 0) {
                //logger.info("开始将截取的list放入新的list中");
                newTlsMap.put(i, subNewList);
            }

        }

        return newTlsMap;

    }

}
