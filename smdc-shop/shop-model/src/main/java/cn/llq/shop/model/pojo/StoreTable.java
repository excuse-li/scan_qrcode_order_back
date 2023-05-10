package cn.llq.shop.model.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "store_table")
@Data
public class StoreTable {
    @Id
    @Column
    Long id;

    @Column(name = "table_name")
    String tableName;

    @Column
    Integer status=1;

    @Column(name = "store_id")
    Long storeId;

    @Column
    String url;

    @Column(name = "use_status")
    Integer useStatus=0;

    @Column(name="create_time")
    Date createTime;

    @Column(name="qr_code")
    String qrCode;
}
