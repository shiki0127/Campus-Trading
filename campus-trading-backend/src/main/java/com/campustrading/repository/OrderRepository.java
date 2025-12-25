package com.campustrading.repository;

import com.campustrading.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    // 查询买家的所有订单
    List<Order> findByBuyerIdOrderByCreateTimeDesc(String buyerId);

    //查询卖家的所有订单 (我的卖出)
    List<Order> findBySellerIdOrderByCreateTimeDesc(String sellerId);
}