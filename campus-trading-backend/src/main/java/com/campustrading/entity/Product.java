package com.campustrading.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String title;       // 标题
    private String description; // 描述
    private Double price;       // 价格
    private String category;    // 分类
    private String campus;      // 校区
    private String imageUrl;    // 图片地址 (简化，先存一张)

    private String sellerId;    // 卖家ID
    private String sellerName;  // 卖家昵称 (冗余存储，方便查询)
    private String sellerAvatar;// 卖家头像

    private Integer status;     // 0:在售, 1:已售出
    private Date createTime;    // 发布时间
}