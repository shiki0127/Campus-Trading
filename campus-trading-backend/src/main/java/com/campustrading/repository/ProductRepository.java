package com.campustrading.repository;

import com.campustrading.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByStatusOrderByCreateTimeDesc(Integer status);
    List<Product> findByCategoryAndStatus(String category, Integer status);
    List<Product> findBySellerIdOrderByCreateTimeDesc(String sellerId);

    // 新增：根据标题模糊查询 (包含 keyword 且状态为在售)
    List<Product> findByTitleContainingAndStatus(String title, Integer status);
}