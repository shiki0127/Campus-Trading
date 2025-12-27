package com.campustrading.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient; // 引入 Transient
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String orderNo;     // 订单号
    private String productId;   // 商品ID
    private String productTitle;// 商品标题
    private String productImage;// 商品图片
    private Double price;       // 成交价格

    private String buyerId;     // 买家ID
    private String sellerId;    // 卖家ID

    // 新增：用于前端展示的字段，加 @Transient 表示不存入数据库，只在返回时填充
    // 或者如果不介意冗余存储，也可以去掉 @Transient
    @Transient
    private String buyerName;
    @Transient
    private String sellerName;

    private Integer status;     // 0:待支付, 1:已支付/待发货, 2:已发货/已完成, 3:已取消
    private String payMethod;   // wechat, alipay
    private Date createTime;
    private Date payTime;
}