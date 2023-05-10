package cn.llq.order.vo;

import cn.llq.order.OrderGoods;
import cn.llq.order.OrderInfo;
import lombok.Data;

import java.util.List;

@Data
public class OrderVo extends OrderInfo {
    List<OrderGoods> list;
}
