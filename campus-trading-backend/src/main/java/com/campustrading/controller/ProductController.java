package com.campustrading.controller;

import com.campustrading.common.Result;
import com.campustrading.entity.Product;
import com.campustrading.entity.User;
import com.campustrading.repository.ProductRepository;
import com.campustrading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository; // 新增：注入用户仓库

    // 辅助方法：填充最新的卖家信息
    private void fillLatestSellerInfo(Product product) {
        if (product != null && product.getSellerId() != null) {
            // 实时查询用户表
            userRepository.findById(product.getSellerId()).ifPresent(user -> {
                // 用最新的昵称和头像覆盖商品表里的旧数据 (只覆盖返回值，不修改数据库，保证展示最新)
                product.setSellerName(user.getNickname());
                product.setSellerAvatar(user.getAvatar());
            });
        }
    }

    // 1. 发布商品
    @PostMapping("/publish")
    public Result<Product> publish(@RequestBody Product product) {
        product.setStatus(0); // 默认在售
        product.setCreateTime(new Date());

        // 发布时也查一下最新的用户信息填进去，确保初始数据也是准的
        userRepository.findById(product.getSellerId()).ifPresent(user -> {
            product.setSellerName(user.getNickname());
            product.setSellerAvatar(user.getAvatar());
        });

        Product savedProduct = productRepository.save(product);
        return Result.success(savedProduct);
    }

    // 2. 首页获取商品列表
    @GetMapping("/list")
    public Result<List<Product>> getList(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword
    ) {
        List<Product> list;

        if (keyword != null && !keyword.isEmpty()) {
            list = productRepository.findByTitleContainingAndStatus(keyword, 0);
        } else if (category != null && !category.isEmpty() && !"全部".equals(category)) {
            list = productRepository.findByCategoryAndStatus(category, 0);
        } else {
            list = productRepository.findByStatusOrderByCreateTimeDesc(0);
        }

        // 遍历列表，为每个商品填充最新卖家信息
        list.forEach(this::fillLatestSellerInfo);

        return Result.success(list);
    }

    // 3. 商品详情
    @GetMapping("/{id}")
    public Result<Product> getDetail(@PathVariable String id) {
        Product product = productRepository.findById(id).orElse(null);
        // 填充最新卖家信息
        fillLatestSellerInfo(product);
        return Result.success(product);
    }

    // 4. 我的发布列表
    @GetMapping("/my/{sellerId}")
    public Result<List<Product>> getMyPublishes(@PathVariable String sellerId) {
        List<Product> myProducts = productRepository.findBySellerIdOrderByCreateTimeDesc(sellerId);
        // 遍历填充
        myProducts.forEach(this::fillLatestSellerInfo);
        return Result.success(myProducts);
    }

    // 5. 删除商品
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable String id) {
        productRepository.deleteById(id);
        return Result.success("删除成功");
    }
}