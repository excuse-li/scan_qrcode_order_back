package cn.llq.order;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

@Table(name = "pay_order")
@Data
public class PayOrder {
    @Id
    @Column
    String id = UUID.randomUUID().toString().replace("-","");

    @Column
    Integer status;

    @Column(name = "update_time")
    Date updateTime;

    @Column(name="create_time")
    Date createTime;

    @Column(name = "order_id")
    String orderId;

    public static PayOrder newInstance() {

        PayOrder payOrder = new PayOrder();
        payOrder.setId(null);
        return payOrder;
    }
}
