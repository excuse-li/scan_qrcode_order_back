package cn.llq.order;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Data
@Table(name = "order_member")
public class OrderMember {

    @Column
    @Id
    String id= UUID.randomUUID().toString();

    @Column(name = "member_id")
    String memberId;

    @Column(name = "order_id")
    String orderId;

    @Column
    String type;

    public static OrderMember newInstance() {

        OrderMember orderMember = new OrderMember();
        orderMember.setId(null);
        return orderMember;
    }

}
