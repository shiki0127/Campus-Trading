package com.campustrading.controller;

import com.campustrading.common.Result;
import com.campustrading.entity.Order;
import com.campustrading.entity.Product;
import com.campustrading.repository.OrderRepository;
import com.campustrading.repository.ProductRepository;
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

    // 1. 创建订单
    @PostMapping("/create")
    public Result<Order> createOrder(@RequestBody Order order) {
        // 查询商品信息
        Optional<Product> productOpt = productRepository.findById(order.getProductId());
        if (!productOpt.isPresent()) {
            return Result.error("商品不存在");
        }
        Product product = productOpt.get();
        if (product.getStatus() == 1) {
            return Result.error("商品已售出，手慢了！");
        }

        // 补全订单信息
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

    // 2. 支付订单 (模拟支付成功)
    @PostMapping("/pay/{orderId}")
    public Result<String> payOrder(@PathVariable String orderId, @RequestParam String payMethod) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) return Result.error("订单不存在");

        Order order = orderOpt.get();
        if(order.getStatus() == 1) return Result.success("订单已支付");

        // 1. 更新订单状态
        order.setStatus(1); // 已支付
        order.setPayMethod(payMethod);
        order.setPayTime(new Date());
        orderRepository.save(order);

        // 2. 更新商品状态为已售出
        Optional<Product> productOpt = productRepository.findById(order.getProductId());
        if(productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setStatus(1); // 标记已售出
            productRepository.save(product);
        }

        return Result.success("支付成功");
    }

    // 3. 我的订单列表
    @GetMapping("/my/{userId}")
    public Result<List<Order>> myOrders(@PathVariable String userId) {
        return Result.success(orderRepository.findByBuyerIdOrderByCreateTimeDesc(userId));
    }

    // 4. 我的卖出列表 (卖家视角)
    @GetMapping("/sales/{sellerId}")
    public Result<List<Order>> mySales(@PathVariable String sellerId) {
        return Result.success(orderRepository.findBySellerIdOrderByCreateTimeDesc(sellerId));
    }

    // 5. 卖家发货
    @PostMapping("/deliver/{orderId}")
    public Result<String> deliverOrder(@PathVariable String orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) return Result.error("订单不存在");

        Order order = orderOpt.get();
        // 状态流转：1(已支付) -> 2(已发货/交易完成)
        if (order.getStatus() != 1) {
            return Result.error("订单状态不正确，无法发货");
        }

        order.setStatus(2); // 2 代表已发货
        orderRepository.save(order);
        return Result.success("发货成功");
    }
}