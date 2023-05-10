package cn.llq.order;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Table(name="order_info")
@Data
public class OrderInfo {
    @Id
    @Column
    String id= UUID.randomUUID().toString();

    @Column(name="store_id")
    Long storeId;

    @Column(name = "order_src")
    Integer orderSrc;

    @Column(name = "create_date")
    Date createDate;

    @Column(name = "order_amount")
    BigDecimal orderAmount;

    @Column(name = "payment_amount")
    BigDecimal paymentAmount;

    @Column(name = "payment_type")
    Integer paymentType;

    @Column(name = "payment_time")
    Date paymentTime;

    @Column(name = "payment_member_id")
    String paymentMemberId;

    @Column(name = "create_member_id")
    String createMemberId;

    @Column(name = "table_id")
    Long tableId;

    @Column(name = "user_status")
    Integer userStatus;

    @Column(name = "pay_status")
    Integer payStatus;

    public static OrderInfo newInstance() {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(null);
        return orderInfo;
    }
}
