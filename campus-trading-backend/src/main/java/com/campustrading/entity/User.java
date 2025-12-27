package com.campustrading.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users") // 对应 MongoDB 的 users 集合
public class User {
    @Id
    private String id;       // 主键
    private String username; // 学号或手机号
    private String password; // 密码
    private String nickname; // 昵称
    private String avatar;   // 头像URL
    private String campus;   // 校区 (呈贡/东陆)
    private Double balance;  // 余额 (模拟支付用)
    private String gender;   // 性别
}