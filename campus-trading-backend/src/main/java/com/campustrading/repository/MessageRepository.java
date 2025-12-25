package com.campustrading.repository;

import com.campustrading.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    // 查找与某人的相关消息 (用于构建列表)
    // 也可以使用 @Query 自定义查询，这里为了简单，我们查出来后在内存处理
    List<Message> findByFromIdOrToIdOrderByCreateTimeDesc(String fromId, String toId);

    // 查找两人的聊天记录
    // 逻辑：(from = me AND to = other) OR (from = other AND to = me)
    // 这里简单起见，我们获取所有相关消息然后在 Service/Controller 层过滤
}