package cn.llq.order;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "balance_info")
public class BalanceInfo {
    @Id
    @Column
    Long id;
    @Column(name = "merchant_id")
    Long merchantId;
    @Column(name = "balance")
    BigDecimal balance;
    @Column(name = "update_time")
    Date updateTime;
}
