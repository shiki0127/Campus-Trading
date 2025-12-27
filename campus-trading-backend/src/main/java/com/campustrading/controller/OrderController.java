package com.campustrading.controller;

import com.campustrading.common.Result;
import com.campustrading.entity.Order;
import com.campustrading.entity.Product;
import com.campustrading.repository.OrderRepository;
import com.campustrading.repository.ProductRepository;
import com.campustrading.repository.UserRepository; // 1. 引入 UserRepo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository; // 2. 注入

    // 辅助方法：填充买家和卖家昵称
    private void fillOrderUserInfo(Order order) {
        if (order.getBuyerId() != null) {
            userRepository.findById(order.getBuyerId())
                    .ifPresent(u -> order.setBuyerName(u.getNickname()));
        }
        if (order.getSellerId() != null) {
            userRepository.findById(order.getSellerId())
                    .ifPresent(u -> order.setSellerName(u.getNickname()));
        }
    }

    // 1. 创建订单
    @PostMapping("/create")
    public Result<Order> createOrder(@RequestBody Order order) {
        Optional<Product> productOpt = productRepository.findById(order.getProductId());
        if (!productOpt.isPresent()) {
            return Result.error("商品不存在");
        }
        Product product = productOpt.get();
        if (product.getStatus() == 1) {
            return Result.error("商品已售出，手慢了！");
        }

        order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        order.setProductTitle(product.getTitle());
        order.setProductImage(product.getImageUrl());
        order.setPrice(product.getPrice());
        order.setSellerId(product.getSellerId());
        order.setStatus(0); // 待支付
        order.setCreateTime(new Date());

        orderRepository.save(order);
        return Result.success(order);
    }

    // 2. 支付订单
    @PostMapping("/pay/{orderId}")
    public Result<String> payOrder(@PathVariable String orderId, @RequestParam String payMethod) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) return Result.error("订单不存在");

        Order order = orderOpt.get();
        if(order.getStatus() != 0) return Result.success("订单状态已更新"); // 防止重复支付

        // 更新订单
        order.setStatus(1); // 已支付 (待发货)
        order.setPayMethod(payMethod);
        order.setPayTime(new Date());
        orderRepository.save(order);

        // 更新商品状态
        Optional<Product> productOpt = productRepository.findById(order.getProductId());
        if(productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setStatus(1); // 已售出
            productRepository.save(product);
        }

        return Result.success("支付成功");
    }

    // 3. 我的订单列表 (买家)
    @GetMapping("/my/{userId}")
    public Result<List<Order>> myOrders(@PathVariable String userId) {
        List<Order> list = orderRepository.findByBuyerIdOrderByCreateTimeDesc(userId);
        list.forEach(this::fillOrderUserInfo); // 填充信息
        return Result.success(list);
    }

    // 4. 我的卖出列表 (卖家)
    @GetMapping("/sales/{sellerId}")
    public Result<List<Order>> mySales(@PathVariable String sellerId) {
        List<Order> list = orderRepository.findBySellerIdOrderByCreateTimeDesc(sellerId);
        list.forEach(this::fillOrderUserInfo); // 填充信息
        return Result.success(list);
    }

    // 5. 卖家发货
    @PostMapping("/deliver/{orderId}")
    public Result<String> deliverOrder(@PathVariable String orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) return Result.error("订单不存在");

        Order order = orderOpt.get();
        if (order.getStatus() != 1) {
            return Result.error("订单状态不正确，无法发货");
        }

        order.setStatus(2); // 2: 已发货/交易完成
        orderRepository.save(order);
        return Result.success("发货成功");
    }
}