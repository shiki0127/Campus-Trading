package com.campustrading.repository;

import com.campustrading.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // 自动生成查询方法：根据用户名查找用户
    User findByUsername(String username);
}