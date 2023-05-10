package cn.llq.order;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BuyCarInfo {
    List<OrderGoods> list = new ArrayList<>();
    String createUserId;
}
