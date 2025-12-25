package com.campustrading.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String orderNo;     // 订单号
    private String productId;   // 商品ID
    private String productTitle;// 商品标题（冗余存储，防止商品删除后订单看不见）
    private String productImage;// 商品图片
    private Double price;       // 成交价格

    private String buyerId;     // 买家ID
    private String sellerId;    // 卖家ID

    private Integer status;     // 0:待支付, 1:已支付, 2:已取消
    private String payMethod;   // wechat, alipay
    private Date createTime;
    private Date payTime;
}