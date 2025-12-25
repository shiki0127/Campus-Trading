package com.campustrading.controller;

import com.campustrading.common.Result;
import com.campustrading.entity.Product;
import com.campustrading.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // 1. 发布商品
    @PostMapping("/publish")
    public Result<Product> publish(@RequestBody Product product) {
        product.setStatus(0); // 默认在售
        product.setCreateTime(new Date());
        // 注意：实际开发中 sellerName 和 avatar 应该从 Token 或 数据库 查，这里前端传过来

        // 如果这里报错 "S not within bound"，请检查 ProductRepository 是否 import 了正确的 com.campustrading.entity.Product
        Product savedProduct = productRepository.save(product);
        return Result.success(savedProduct);
    }

    // 2. 首页获取商品列表 (只看在售的)
    @GetMapping("/list")
    public Result<List<Product>> getList(@RequestParam(required = false) String category) {
        List<Product> productList;
        if (category != null && !category.isEmpty() && !"全部".equals(category)) {
            // 确保 productRepository.findByCategoryAndStatus 返回的是 List<com.campustrading.entity.Product>
            productList = productRepository.findByCategoryAndStatus(category, 0);
        } else {
            productList = productRepository.findByStatusOrderByCreateTimeDesc(0);
        }
        return Result.success(productList);
    }

    // 3. 商品详情
    @GetMapping("/{id}")
    public Result<Product> getDetail(@PathVariable String id) {
        Product product = productRepository.findById(id).orElse(null);
        return Result.success(product);
    }

    // 4. 我的发布列表
    @GetMapping("/my/{sellerId}")
    public Result<List<Product>> getMyPublishes(@PathVariable String sellerId) {
        List<Product> myProducts = productRepository.findBySellerIdOrderByCreateTimeDesc(sellerId);
        return Result.success(myProducts);
    }
}