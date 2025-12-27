package com.campustrading.controller;

import com.campustrading.common.Result;
import com.campustrading.entity.User;
import com.campustrading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // 1. 注册接口
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        // 检查用户名是否已存在
        User exist = userRepository.findByUsername(user.getUsername());
        if (exist != null) {
            return Result.error("该账号已被注册");
        }
        // 初始化默认值
        if (user.getNickname() == null) user.setNickname("同学_" + System.currentTimeMillis());
        if (user.getAvatar() == null) user.setAvatar("https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png");
        user.setBalance(1000.00); // 注册送1000元体验金

        userRepository.save(user);
        return Result.success(user);
    }

    // 2. 登录接口
    @PostMapping("/login")
    public Result<User> login(@RequestBody User loginUser) {
        User user = userRepository.findByUsername(loginUser.getUsername());
        if (user == null || !user.getPassword().equals(loginUser.getPassword())) {
            return Result.error("账号或密码错误");
        }
        return Result.success(user);
    }

    // 3. 获取用户详情
    @GetMapping("/{id}")
    public Result<User> getUserInfo(@PathVariable String id) {
        return Result.success(userRepository.findById(id).orElse(null));
    }

    // 4. (新增) 更新个人信息
    @PostMapping("/update")
    public Result<User> updateUserInfo(@RequestBody User user) {
        // 实际场景应从 Token 获取当前用户ID防止越权，这里简单处理直接用 ID
        User existUser = userRepository.findById(user.getId()).orElse(null);
        if (existUser == null) return Result.error("用户不存在");

        // 更新字段 (学号/用户名通常不允许修改)
        if (user.getNickname() != null) existUser.setNickname(user.getNickname());
        if (user.getAvatar() != null) existUser.setAvatar(user.getAvatar());
        if (user.getCampus() != null) existUser.setCampus(user.getCampus());
        // 性别字段 User 实体之前可能没定义，如果报错请去 User.java 加一个 private String gender;
        // existUser.setGender(user.getGender());

        userRepository.save(existUser);
        return Result.success(existUser);
    }

    // 5. (新增) 修改密码
    @PostMapping("/change-password")
    public Result<String> changePassword(@RequestBody Map<String, String> params) {
        String userId = params.get("userId");
        String oldPwd = params.get("oldPassword");
        String newPwd = params.get("newPassword");

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return Result.error("用户不存在");

        if (!user.getPassword().equals(oldPwd)) {
            return Result.error("旧密码错误");
        }

        user.setPassword(newPwd);
        userRepository.save(user);
        return Result.success("密码修改成功");
    }
}