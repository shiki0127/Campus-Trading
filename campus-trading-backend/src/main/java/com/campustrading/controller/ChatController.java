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
        // 这里的 fromName/Avatar 实际上应该由后端根据 token/id 查出来填充，防止造假
        // 但为了演示方便，允许前端传，或者我们在这里补全
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
        // 简单暴力法：获取所有 userId1 参与的消息，然后过滤出 userId2
        List<Message> allMessages = messageRepository.findByFromIdOrToIdOrderByCreateTimeDesc(userId1, userId1);

        List<Message> history = allMessages.stream()
                .filter(m -> (m.getFromId().equals(userId1) && m.getToId().equals(userId2)) ||
                        (m.getFromId().equals(userId2) && m.getToId().equals(userId1)))
                .sorted(Comparator.comparing(Message::getCreateTime)) // 按时间正序
                .collect(Collectors.toList());

        return Result.success(history);
    }

    // 3. 获取消息列表 (最近联系人)
    @GetMapping("/list/{userId}")
    public Result<List<Map<String, Object>>> getChatList(@PathVariable String userId) {
        // 1. 找出所有我参与的消息
        List<Message> allMessages = messageRepository.findByFromIdOrToIdOrderByCreateTimeDesc(userId, userId);

        // 2. 按“对方ID”分组，保留最新的一条
        Map<String, Message> latestMsgMap = new LinkedHashMap<>();
        for (Message msg : allMessages) {
            String otherId = msg.getFromId().equals(userId) ? msg.getToId() : msg.getFromId();
            if (!latestMsgMap.containsKey(otherId)) {
                latestMsgMap.put(otherId, msg);
            }
        }

        // 3. 组装返回结果
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map.Entry<String, Message> entry : latestMsgMap.entrySet()) {
            String otherId = entry.getKey();
            Message msg = entry.getValue();

            // 查对方的详细信息 (头像/昵称)
            // 如果 msg 里存了对方信息稍微快点，但这里如果是别人发给我的，msg里有from信息；如果是我发给别人的，msg里没存to的详情
            // 简单起见，查一下库
            User otherUser = userRepository.findById(otherId).orElse(new User());

            Map<String, Object> chatItem = new HashMap<>();
            chatItem.put("id", otherId); // 聊天对象的ID
            chatItem.put("name", otherUser.getNickname() == null ? "未知用户" : otherUser.getNickname());
            chatItem.put("avatar", otherUser.getAvatar());
            chatItem.put("lastMsg", msg.getContent());
            chatItem.put("time", msg.getCreateTime());

            resultList.add(chatItem);
        }

        return Result.success(resultList);
    }
}