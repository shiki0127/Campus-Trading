package com.campustrading.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "messages")
public class Message {
    @Id
    private String id;
    private String fromId;      // 发送者ID
    private String fromName;    // 发送者昵称 (冗余)
    private String fromAvatar;  // 发送者头像 (冗余)

    private String toId;        // 接收者ID
    private String content;     // 消息内容
    private Date createTime;    // 发送时间
}