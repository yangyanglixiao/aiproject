# 在线订餐系统设计

## 一、系统架构

```
                    ┌──────────────────────────┐
                    │     /Users/yang/           │
                    │   Desktop/aiproject/       │
                    │                            │
                    │  ┌──────────┐ ┌──────────┐ │
  Frontend ──────►  │  │ backend/ │ │frontend/ │ │ ◄─ Browser
  (Vue 3 SPA)      │  └────┬─────┘ └────┬─────┘ │
                    │       │ Spring Boot │       │
                    │       │  + MyBatis-Plus │  │
                    │       └──────┬───────┘    │
                    │              │            │
                    │       ┌──────▼───────┐    │
                    │       │   MySQL      │    │
                    │       │  (my_order)  │    │
                    │       └──────────────┘    │
                    └──────────────────────────┘
```

**核心栈:** Spring Boot 2.7 + MyBatis-Plus 3.5 + MySQL 8 + Vue 3 + Vite + Pinia + Element Plus

## 二、数据模型

- `user` — id, username, password, phone, role[USER/MERCHANT/ADMIN]
- `merchant` — id, name, address, phone, logo, status[REVIEWING/APPROVED/REJECTED], description
- `category` — id, merchant_id, name, sort
- `product` — id, merchant_id, category_id, name, description, price, image, status[ON_OFF_SHELF]
- `cart` — id, user_id, merchant_id, product_id, quantity
- `order` — id, user_id, total_amount, status, pay_status, pay_method, created_at
- `order_item` — id, order_id, merchant_id, product_id, product_name, product_image, price, quantity, subtotal
- `payment_record` — id, order_id, amount, pay_channel[mock/alipay/wechat], status, pay_time

## 三、核心 API

**用户端:**
- `POST /api/auth/login` — 登录
- `GET /api/merchants` — 商家列表（筛选/搜索）
- `GET /api/merchants/{id}` — 商家详情
- `GET /api/merchants/{id}/products` — 商家商品
- `POST /api/cart/add` — 加购
- `GET /api/cart` — 购物车
- `PUT/DELETE /api/cart/item/{id}` — 修改/删除
- `POST /api/orders` — 创建订单（自动拆单）
- `GET /api/orders` — 我的订单列表
- `GET /api/orders/{id}` — 订单详情
- `POST /api/orders/{id}/cancel` — 取消订单
- `POST /api/pay/{orderId}/mock` — 模拟支付

**管理后台:**
- `POST /api/admin/login` — 后台登录
- `GET /api/admin/merchants` — 商家列表
- `PUT /api/admin/merchants/{id}/audit` — 审核商家
- `GET/POST/PUT/DELETE /api/admin/products` — 商品 CRUD
- `GET /api/admin/orders` — 订单列表
- `PUT /api/admin/orders/{id}/status` — 修改订单状态

## 四、支付策略

策略模式: `PaymentStrategy` 接口 → `MockPaymentStrategy`(MVP) / `AlipayPaymentStrategy` / `WechatPaymentStrategy`(预留)

## 五、关键流程

- 下单: 购物车按 merchant_id 分组 → 创建主订单 + 多个 order_item → 清空购物车
- 支付: MockPaymentStrategy 模拟延迟 → 更新状态
- 取消: 仅 PREPARING 之前可取消
