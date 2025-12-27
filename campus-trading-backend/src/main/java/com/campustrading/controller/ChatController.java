package com.campustrading.controller;

import com.campustrading.common.Result;
import com.campustrading.entity.Message;
import com.campustrading.entity.User;
import com.campustrading.repository.MessageRepository;
import com.campustrading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. 发送消息
    @PostMapping("/send")
    public Result<Message> sendMessage(@RequestBody Message message) {
        message.setCreateTime(new Date());
        User sender = userRepository.findById(message.getFromId()).orElse(null);
        if (sender != null) {
            message.setFromName(sender.getNickname());
            message.setFromAvatar(sender.getAvatar());
        }
        messageRepository.save(message);
        return Result.success(message);
    }

    // 2. 获取与某人的聊天记录
    @GetMapping("/history")
    public Result<List<Message>> getHistory(@RequestParam String userId1, @RequestParam String userId2) {
        List<Message> allMessages = messageRepository.findByFromIdOrToIdOrderByCreateTimeDesc(userId1, userId1);

        List<Message> history = allMessages.stream()
                .filter(m -> (m.getFromId().equals(userId1) && m.getToId().equals(userId2)) ||
                        (m.getFromId().equals(userId2) && m.getToId().equals(userId1)))
                .sorted(Comparator.comparing(Message::getCreateTime))
                .collect(Collectors.toList());

        return Result.success(history);
    }

    // 3. 获取消息列表 (最近联系人)
    @GetMapping("/list/{userId}")
    public Result<List<Map<String, Object>>> getChatList(@PathVariable String userId) {
        List<Message> allMessages = messageRepository.findByFromIdOrToIdOrderByCreateTimeDesc(userId, userId);

        Map<String, Message> latestMsgMap = new LinkedHashMap<>();
        for (Message msg : allMessages) {
            String otherId = msg.getFromId().equals(userId) ? msg.getToId() : msg.getFromId();
            if (!latestMsgMap.containsKey(otherId)) {
                latestMsgMap.put(otherId, msg);
            }
        }

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map.Entry<String, Message> entry : latestMsgMap.entrySet()) {
            String otherId = entry.getKey();
            Message msg = entry.getValue();

            User otherUser = userRepository.findById(otherId).orElse(new User());

            Map<String, Object> chatItem = new HashMap<>();
            chatItem.put("id", otherId);
            chatItem.put("name", otherUser.getNickname() == null ? "未知用户" : otherUser.getNickname());
            chatItem.put("avatar", otherUser.getAvatar());
            chatItem.put("lastMsg", msg.getContent());
            chatItem.put("time", msg.getCreateTime());

            resultList.add(chatItem);
        }

        return Result.success(resultList);
    }

    // 4. (新增) 删除对话
    // 注意：这里为了简化逻辑，直接物理删除两人之间的所有消息。
    // 实际商业项目中通常是做"软删除"（即只对自己隐藏），但这对于课程设计/MVP项目来说足够了。
    @DeleteMapping("/delete/{targetId}")
    public Result<String> deleteChat(@PathVariable String targetId, @RequestParam String currentUserId) {
        // 1. 找出两人所有相关的消息
        List<Message> allMessages = messageRepository.findByFromIdOrToIdOrderByCreateTimeDesc(currentUserId, currentUserId);

        // 2. 过滤出这两人的交互记录
        List<Message> toDelete = allMessages.stream()
                .filter(m -> (m.getFromId().equals(currentUserId) && m.getToId().equals(targetId)) ||
                        (m.getFromId().equals(targetId) && m.getToId().equals(currentUserId)))
                .collect(Collectors.toList());

        // 3. 批量删除
        messageRepository.deleteAll(toDelete);

        return Result.success("删除成功");
    }
}