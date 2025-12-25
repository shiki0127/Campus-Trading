package com.campustrading.controller;

import com.campustrading.common.Result;
import com.campustrading.entity.User;
import com.campustrading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}