package cn.llq.order.dao;

import cn.llq.order.OrderInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface OrderInfoMapper extends Mapper<OrderInfo> {

    @Select("<script>" +
            "SELECT store_id AS storeId,COUNT(id) AS num\n" +
            "FROM order_info WHERE store_id in<foreach collection=\"list\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">\n" +
            "               #{item}\n" +
            "       </foreach> and to_days(create_date) =  to_days(now()) GROUP BY store_id" +
            "</script>")
    List<Map<String,Object>> getTodayStoreNum(@Param("list") List<Long> list);

    @Select("<script>" +
            "SELECT store_id AS storeId,sum(order_amount) AS amount\n" +
            "FROM order_info WHERE store_id in<foreach collection=\"list\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">\n" +
            "               #{item}\n" +
            "       </foreach> and to_days(create_date) =  to_days(now()) GROUP BY store_id" +
            "</script>")
    List<Map<String,Object>> getTodayStoreDue(@Param("list") List<Long> list);

    @Select("<script>" +
            "SELECT COUNT(id) as `value`,DATE_FORMAT(create_date,'%Y-%m-%d') as `name` FROM order_info  WHERE store_id IN" +
            "<foreach collection=\"list\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">\n" +
            "               #{item}\n" +
            "       </foreach>\n" +
            "  " +
            "GROUP BY `name`  ORDER BY `name` DESC limit 7 " +
            "</script>")
    List<Map<String,Object>> getWeekNumData(@Param("list") List<Long> list);

    @Select("<script>" +
            "SELECT sum(order_amount) as `value`,DATE_FORMAT(create_date,'%Y-%m-%d') as `name` FROM order_info  WHERE store_id IN" +
            "<foreach collection=\"list\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">\n" +
            "               #{item}\n" +
            "       </foreach>\n" +
            "GROUP BY `name`  ORDER BY `name` DESC limit 7 " +
            "</script>")
    List<Map<String,Object>> getWeekAmountData(@Param("list") List<Long> list);
}
