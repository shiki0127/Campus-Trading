# 校园二手交易平台 (Campus Second-hand Trading Platform)

## 📖 项目简介

这是一个基于 **Spring Boot** 和 **Vue 3** 前后端分离架构开发的校园二手交易平台。旨在为在校学生提供一个安全、便捷的闲置物品交易环境。系统集成了用户管理、商品发布与搜索、订单交易流程以及实时聊天沟通等核心功能，实现了从浏览到交易闭环的完整体验。

## ✨ 核心功能

* **用户体系**：注册、登录、个人信息修改（头像、昵称、密码）、校区选择（呈贡/东陆）。
* **商品系统**：
    * 商品发布：支持上传本地图片、填写价格、分类及描述。
    * 商品展示：首页轮播图、分类筛选、关键词搜索（支持模糊查询）。
    * 商品管理：卖家可删除已发布的商品。
* **交易闭环**：
    * 购买流程：商品详情 -> 立即购买 -> 模拟支付 -> 生成订单。
    * 订单管理：买家查看“我买到的”，卖家查看“我卖出的”。
    * 状态流转：待支付 -> 待发货 -> 已完成。
* **即时通讯**：
    * 上下文关联：从商品详情页发起聊天，自动携带商品卡片，方便买卖双方确认意向。
    * 身份识别：聊天窗口自动识别买家/卖家身份，提供“发链接”或“核对订单”快捷按钮。
    * 消息管理：支持查看最近联系人列表及删除对话。

## 🛠 技术栈

### 前端 (Frontend)
* **核心框架**：Vue 3 (Composition API)
* **构建工具**：Vite
* **UI 组件库**：Element Plus
* **路由管理**：Vue Router 4
* **状态管理**：Pinia
* **HTTP 请求**：Axios

### 后端 (Backend)
* **核心框架**：Spring Boot 2.6.13
* **开发语言**：Java (JDK 11+)
* **数据库**：MongoDB
* **ORM 框架**：Spring Data MongoDB
* **工具库**：Lombok, Fastjson

## 📂 项目结构

### 后端结构
```text
com.campustrading
├── common          // 通用结果封装 (Result)
├── config          // 配置类 (WebMvc, Mongo)
├── controller      // 接口控制器 (User, Product, Order, Chat, File)
├── entity          // 数据库实体 (User, Product, Order, Message)
├── repository      // 数据访问层 (MongoRepository)
└── CampusTradingApplication.java // 启动类
```

### 前端结构
``` test

src
├── api             // Axios 封装与接口定义
├── assets          // 静态资源 (Images, CSS)
├── components      // 公共组件
├── router          // 路由配置
├── stores          // Pinia 状态管理
├── views           // 页面视图
│   ├── Home        // 首页
│   ├── User        // 用户相关 (登录, 注册, 个人中心, 订单)
│   ├── Product     // 商品相关 (详情, 发布)
│   ├── Message     // 消息相关 (列表, 聊天详情)
│   └── Pay         // 支付相关
└── App.vue
```

## 🚀 快速开始
1. 环境准备

    JDK 11 或更高版本

    Node.js 16+

    MongoDB 服务 (运行在默认端口 27017)

2. 启动后端

    1. 使用 IDEA 打开后端项目。

    2. 确认 application.yml 中的 MongoDB 配置正确。

    3. 运行 CampusTradingApplication 主类。

    4. 服务将启动在 http://localhost:8080。

3. 启动前端

    1. 进入前端目录：
    ```Bash

    cd campus-trading-frontend

    ```

    2. 安装依赖：
    ``` Bash

    npm install

    ```

    3. 启动开发服务器：
    ``` Bash

    npm run dev
    ```

4. 访问 http://localhost:5173 即可体验。

	
## 🤝 贡献与支持

如果你喜欢这个项目，欢迎点个 Star ⭐️！ 如有问题，请提交 Issue

---

Created by shiki
