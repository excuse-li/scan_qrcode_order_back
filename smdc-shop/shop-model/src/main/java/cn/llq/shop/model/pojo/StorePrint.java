package cn.llq.shop.model.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "store_print")
public class StorePrint {
    @Id
    @Column
    Long id;

    @Column(name = "print_name")
    String printName;

    @Column(name = "print_sn")
    String printSN;

    @Column(name = "print_key" )
    String printKey;

    @Column(name = "create_time")
    Date createTime;

    @Column(name = "store_id")
    Long storeId;
}
