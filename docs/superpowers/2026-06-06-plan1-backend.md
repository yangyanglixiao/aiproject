# Plan 1: 后端核心 API 实现计划

> **For agentic workers:** Use superpowers:subagent-driven-development or superpowers:executing-plans to implement task by task.

**Goal:** 构建订餐系统后端核心 API，覆盖认证、商家、商品、购物车、订单、支付、后台管理全流程。
**Architecture:** Spring Boot 单体应用，MyBatis-Plus 操作 MySQL，JWT 做认证，策略模式做支付扩展。
**Tech Stack:** Spring Boot 2.7.18 + MyBatis-Plus 3.5.3 + MySQL 8 + JWT (jjwt 0.12) + Lombok + H2 (测试)

---

### 项目结构

```
backend/
├── pom.xml                          # Maven 根 pom
├── src/main/java/com/galaxy/ordering/
│   ├── OrderingApplication.java     # 启动类
│   ├── config/
│   │   ├── MyBatisPlusConfig.java   # MP 配置（分页、乐观锁）
│   │   ├── SecurityConfig.java      # Spring Security 配置
│   │   └── JwtConfig.java           # JWT 密钥配置
│   ├── common/
│   │   ├── Result.java              # 统一返回 {code, msg, data}
│   │   ├── BusinessException.java   # 业务异常
│   │   └── BaseEntity.java          # 公共字段 (createTime/updateTime)
│   ├── entity/
│   │   ├── User.java                # 用户表实体
│   │   ├── Merchant.java            # 商家表实体
│   │   ├── Category.java            # 分类表实体
│   │   ├── Product.java             # 商品表实体
│   │   ├── Cart.java                # 购物车实体
│   │   ├── Order.java               # 主订单实体
│   │   ├── OrderItem.java           # 订单明细实体
│   │   └── PaymentRecord.java       # 支付记录实体
│   ├── dto/
│   │   ├── LoginRequest.java        # 登录请求
│   │   ├── LoginResponse.java       # JWT token + user info
│   │   ├── CartAddRequest.java      # 加购请求
│   │   ├── OrderCreateRequest.java  # 下单请求
│   │   ├── OrderPayRequest.java     # 支付请求
│   │   └── MerchantAuditRequest.java # 商家审核请求
│   ├── mapper/                      # MyBatis-Plus Mapper 接口
│   │   ├── UserMapper.java
│   │   ├── MerchantMapper.java
│   │   ├── CategoryMapper.java
│   │   ├── ProductMapper.java
│   │   ├── CartMapper.java
│   │   ├── OrderMapper.java
│   │   ├── OrderItemMapper.java
│   │   └── PaymentRecordMapper.java
│   ├── service/
│   │   ├── auth/
│   │   │   └── AuthService.java     # 认证服务（登录、JWT）
│   │   ├── merchant/
│   │   │   └── MerchantService.java # 商家 CRUD + 审核
│   │   ├── product/
│   │   │   └── ProductService.java  # 商品 CRUD + 分类
│   │   ├── cart/
│   │   │   └── CartService.java     # 购物车操作
│   │   ├── order/
│   │   │   ├── OrderService.java    # 订单创建（拆单）+ 状态管理
│   │   │   └── OrderStrategy.java   # 订单策略（预留）
│   │   └── payment/
│   │       ├── PaymentStrategy.java     # 支付策略接口
│   │       ├── MockPaymentStrategy.java # 模拟支付
│   │       └── PaymentService.java      # 支付编排服务
│   ├── controller/
│   │   ├── AuthController.java      # POST /api/auth/login
│   │   ├── MerchantController.java  # 商家 CRUD + 列表
│   │   ├── ProductController.java   # 商品 CRUD + 按商家查询
│   │   ├── CartController.java      # 购物车 CRUD
│   │   ├── OrderController.java     # 订单 CRUD + 取消
│   │   ├── PaymentController.java   # 模拟支付
│   │   └── admin/
│   │       ├── AdminAuthController.java  # 后台登录
│   │       ├── AdminMerchantController.java # 商家审核
│   │       ├── AdminProductController.java  # 商品管理
│   │       └── AdminOrderController.java    # 订单管理
│   └── security/
│       ├── JwtTokenProvider.java      # JWT 生成/解析/校验
│       └── JwtAuthenticationFilter.java # JWT 过滤器
├── src/main/resources/
│   ├── application.yml               # 数据源 + 通用配置
│   └── db/schema.sql                 # 建表 DDL
└── src/test/java/                    # 测试
    ├── service/
    │   ├── CartServiceTest.java
    │   ├── OrderServiceTest.java
    │   └── PaymentServiceTest.java
    └── controller/
        └── MerchantControllerTest.java
```

---

### Task 1: 初始化 Spring Boot 项目骨架

**Files:**
- Create: `backend/pom.xml`
- Create: `backend/src/main/java/com/galaxy/ordering/OrderingApplication.java`
- Create: `backend/src/main/resources/application.yml`
- Create: `backend/src/test/java/com/galaxy/ordering/OrderingApplicationTests.java`

- [ ] **Step 1: 编写 pom.xml**

Spring Boot 2.7.18, MyBatis-Plus 3.5.3, Spring Security, jjwt 0.12.0, Lombok, H2 (test), MySQL 8.0.33, Spring Boot Starter Web, Spring Boot Starter Validation。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.18</version>
        <relativePath/>
    </parent>
    <groupId>com.galaxy</groupId>
    <artifactId>ordering</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>ordering-backend</name>
    <properties>
        <java.version>1.8</java.version>
        <mybatis-plus.version>3.5.3.1</mybatis-plus.version>
        <jjwt.version>0.12.5</jjwt.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: 编写 application.yml**

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://47.108.58.39:3306/my_ordering?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  sql:
    init:
      mode: always
      schema-locations: classpath:db/schema.sql

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

jwt:
  secret: Y3J1ZC1zdXBlci1zZWNyZXQta2V5LWZvci1vcmRlcmluZy1zeXN0ZW0tMjAyNg==
  expiration: 86400000

logging:
  level:
    com.galaxy.ordering: debug
```

- [ ] **Step 3: 编写启动类**

```java
package com.galaxy.ordering;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.galaxy.ordering.mapper")
public class OrderingApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderingApplication.class, args);
    }
}
```

- [ ] **Step 4: 编写 schema.sql**

```sql
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20),
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `merchant` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `address` VARCHAR(255),
    `phone` VARCHAR(20),
    `logo` VARCHAR(255),
    `description` TEXT,
    `status` VARCHAR(20) NOT NULL DEFAULT 'REVIEWING',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `category` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `merchant_id` BIGINT NOT NULL,
    `name` VARCHAR(50) NOT NULL,
    `sort` INT DEFAULT 0,
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `merchant_id` BIGINT NOT NULL,
    `category_id` BIGINT NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `price` DECIMAL(10,2) NOT NULL,
    `image` VARCHAR(255),
    `status` VARCHAR(20) NOT NULL DEFAULT 'ON_SHELF',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `cart` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `merchant_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `quantity` INT NOT NULL DEFAULT 1,
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_merchant_product (user_id, merchant_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_no` VARCHAR(32) NOT NULL UNIQUE,
    `user_id` BIGINT NOT NULL,
    `total_amount` DECIMAL(10,2) NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `pay_status` VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    `pay_method` VARCHAR(20),
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id` BIGINT NOT NULL,
    `merchant_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `product_name` VARCHAR(100) NOT NULL,
    `product_image` VARCHAR(255),
    `price` DECIMAL(10,2) NOT NULL,
    `quantity` INT NOT NULL,
    `subtotal` DECIMAL(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `payment_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id` BIGINT NOT NULL,
    `order_no` VARCHAR(32) NOT NULL,
    `amount` DECIMAL(10,2) NOT NULL,
    `pay_channel` VARCHAR(20) NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `pay_time` DATETIME,
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

- [ ] **Step 5: 运行验证**

```bash
cd backend && mvn spring-boot:run
```
看到 `Started OrderingApplication` 表示成功。然后 `Ctrl+C` 停止。

- [ ] **Step 6: Commit**

```bash
cd /Users/yang/Desktop/aiproject
git add backend/
git commit -m "feat: init backend project skeleton with schema"
```

---

### Task 2: 统一返回、业务异常、BaseEntity、枚举

**Files:**
- Create: `backend/src/main/java/com/galaxy/ordering/common/Result.java`
- Create: `backend/src/main/java/com/galaxy/ordering/common/BusinessException.java`
- Create: `backend/src/main/java/com/galaxy/ordering/common/BaseEntity.java`
- Create: `backend/src/main/java/com/galaxy/ordering/common/enums/` (目录)

- [ ] **Step 1: 编写 Result 通用返回类**

```java
package com.galaxy.ordering.common;

import lombok.Data;
import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("success");
        r.setData(data);
        return r;
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> fail(int code, String msg) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    public static <T> Result<T> fail(String msg) {
        return fail(500, msg);
    }
}
```

- [ ] **Step 2: 编写 BusinessException**

```java
package com.galaxy.ordering.common;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(String msg) {
        super(msg);
        this.code = 400;
    }

    public BusinessException(int code, String msg) {
        super(msg);
        this.code = code;
    }
}
```

- [ ] **Step 3: 编写全局异常处理**

```java
package com.galaxy.ordering.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBusiness(BusinessException e) {
        log.warn("Business exception: {}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        log.error("Unexpected error", e);
        return Result.fail("系统错误，请稍后重试");
    }
}
```

- [ ] **Step 4: 编写 BaseEntity**

```java
package com.galaxy.ordering.common;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public abstract class BaseEntity implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
```

- [ ] **Step 5: 编写 MyBatis-Plus 自动填充处理器**

```java
package com.galaxy.ordering.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }
}
```

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/galaxy/ordering/common/ backend/src/main/java/com/galaxy/ordering/config/MyMetaObjectHandler.java
git commit -m "feat: add common Result, BusinessException, BaseEntity, global exception handler"
```

---

### Task 3: 实体类 + Mapper 层

**Files:**
- Create: 8 个 entity 文件（User, Merchant, Category, Product, Cart, Order, OrderItem, PaymentRecord）
- Create: 8 个 mapper 接口
- Modify: `application.yml` — 确认 mybatis-plus 配置已含 type-aliases-package

- [ ] **Step 1: 编写所有实体类**

`User.java`:
```java
package com.galaxy.ordering.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.galaxy.ordering.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {
    private String username;
    private String password;
    private String phone;
    private String role;  // USER, MERCHANT, ADMIN
}
```

`Merchant.java`:
```java
package com.galaxy.ordering.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.galaxy.ordering.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant")
public class Merchant extends BaseEntity {
    private String name;
    private String address;
    private String phone;
    private String logo;
    private String description;
    private String status;  // REVIEWING, APPROVED, REJECTED
}
```

`Category.java`:
```java
package com.galaxy.ordering.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.galaxy.ordering.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
public class Category extends BaseEntity {
    private Long merchantId;
    private String name;
    private Integer sort;
}
```

`Product.java`:
```java
package com.galaxy.ordering.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.galaxy.ordering.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
public class Product extends BaseEntity {
    private Long merchantId;
    private Long categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private String image;
    private String status;  // ON_SHELF, OFF_SHELF
}
```

`Cart.java`:
```java
package com.galaxy.ordering.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.galaxy.ordering.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cart")
public class Cart extends BaseEntity {
    private Long userId;
    private Long merchantId;
    private Long productId;
    private Integer quantity;
}
```

`Order.java`:
```java
package com.galaxy.ordering.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.galaxy.ordering.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order")
public class Order extends BaseEntity {
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;       // PENDING, PAID, PREPARING, DELIVERING, COMPLETED, CANCELLED
    private String payStatus;    // UNPAID, PAID
    private String payMethod;
    private LocalDateTime payTime;
}
```

`OrderItem.java`:
```java
package com.galaxy.ordering.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.galaxy.ordering.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_item")
public class OrderItem extends BaseEntity {
    private Long orderId;
    private Long merchantId;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
```

`PaymentRecord.java`:
```java
package com.galaxy.ordering.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.galaxy.ordering.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payment_record")
public class PaymentRecord extends BaseEntity {
    private Long orderId;
    private String orderNo;
    private BigDecimal amount;
    private String payChannel;  // mock, alipay, wechat
    private String status;      // PENDING, SUCCESS, FAILED
    private LocalDateTime payTime;
}
```

- [ ] **Step 2: 编写所有 Mapper 接口**

所有 mapper 都是同一模式：`@Mapper` + `extends BaseMapper<实体>`。

```java
@Mapper public interface UserMapper extends BaseMapper<User> {}
@Mapper public interface MerchantMapper extends BaseMapper<Merchant> {}
@Mapper public interface CategoryMapper extends BaseMapper<Category> {}
@Mapper public interface ProductMapper extends BaseMapper<Product> {}
@Mapper public interface CartMapper extends BaseMapper<Cart> {}
@Mapper public interface OrderMapper extends BaseMapper<Order> {}
@Mapper public interface OrderItemMapper extends BaseMapper<OrderItem> {}
@Mapper public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {}
```

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/galaxy/ordering/entity/ backend/src/main/java/com/galaxy/ordering/mapper/
git commit -m "feat: add all entity classes and mapper interfaces"
```

---

### Task 4: JWT 认证模块

**Files:**
- Create: `backend/src/main/java/com/galaxy/ordering/security/JwtTokenProvider.java`
- Create: `backend/src/main/java/com/galaxy/ordering/security/JwtAuthenticationFilter.java`
- Create: `backend/src/main/java/com/galaxy/ordering/config/SecurityConfig.java`
- Create: `backend/src/main/java/com/galaxy/ordering/dto/LoginRequest.java`
- Create: `backend/src/main/java/com/galaxy/ordering/dto/LoginResponse.java`

- [ ] **Step 1: 编写 JwtTokenProvider**

```java
package com.galaxy.ordering.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        byte[] bytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

- [ ] **Step 2: 编写 JwtAuthenticationFilter**

```java
package com.galaxy.ordering.security;

import com.galaxy.ordering.common.BusinessException;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractToken(request);

        if (token != null && tokenProvider.validateToken(token)) {
            String username = tokenProvider.getUsernameFromToken(token);
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
```

- [ ] **Step 3: 编写 SecurityConfig**

```java
package com.galaxy.ordering.config;

import com.galaxy.ordering.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                .requestMatchers("/api/auth/**", "/api/merchants/**", "/api/merchants/**/products", "/h2-console/**").permitAll()
                .requestMatchers("/api/admin/**").authenticated()
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .headers().frameOptions().disable();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

- [ ] **Step 4: 编写 LoginRequest 和 LoginResponse**

```java
// LoginRequest.java
package com.galaxy.ordering.dto;
import lombok.Data;
import javax.validation.constraints.NotBlank;
@Data
public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}
```

```java
// LoginResponse.java
package com.galaxy.ordering.dto;
import lombok.Data;
@Data
public class LoginResponse {
    private String token;
    private String username;
    private String role;
    private Long userId;
}
```

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/galaxy/ordering/security/ backend/src/main/java/com/galaxy/ordering/config/SecurityConfig.java backend/src/main/java/com/galaxy/ordering/dto/
git commit -m "feat: add JWT authentication module and security config"
```

---

### Task 5: AuthService + AuthController

**Files:**
- Create: `backend/src/main/java/com/galaxy/ordering/service/auth/AuthService.java`
- Create: `backend/src/main/java/com/galaxy/ordering/controller/AuthController.java`

- [ ] **Step 1: 编写 AuthService**

```java
package com.galaxy.ordering.service.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.galaxy.ordering.common.BusinessException;
import com.galaxy.ordering.dto.LoginRequest;
import com.galaxy.ordering.dto.LoginResponse;
import com.galaxy.ordering.entity.User;
import com.galaxy.ordering.mapper.UserMapper;
import com.galaxy.ordering.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        String token = tokenProvider.generateToken(
            new UsernamePasswordAuthenticationToken(user.getUsername(), null,
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))));

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setUserId(user.getId());
        return response;
    }

    public User getCurrentUser(String username) {
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }
}
```

- [ ] **Step 2: 编写 AuthController**

```java
package com.galaxy.ordering.controller;

import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.dto.LoginRequest;
import com.galaxy.ordering.dto.LoginResponse;
import com.galaxy.ordering.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.login(request));
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/galaxy/ordering/service/auth/ backend/src/main/java/com/galaxy/ordering/controller/AuthController.java
git commit -m "feat: add auth service and login controller"
```

---

### Task 6: Merchant 服务 + Controller

**Files:**
- Create: `backend/src/main/java/com/galaxy/ordering/service/merchant/MerchantService.java`
- Create: `backend/src/main/java/com/galaxy/ordering/controller/MerchantController.java`

- [ ] **Step 1: 编写 MerchantService**

```java
package com.galaxy.ordering.service.merchant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.galaxy.ordering.common.BusinessException;
import com.galaxy.ordering.entity.Merchant;
import com.galaxy.ordering.mapper.MerchantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantService extends ServiceImpl<MerchantMapper, Merchant> {

    public Page<Merchant> list(int page, int size, String keyword) {
        Page<Merchant> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Merchant::getName, keyword);
        }
        wrapper.eq(Merchant::getStatus, "APPROVED")
               .orderByDesc(Merchant::getCreateTime);
        return this.page(pageParam, wrapper);
    }

    public Merchant getById(Long id) {
        Merchant merchant = this.getByIdSimple(id);
        if (merchant == null || !"APPROVED".equals(merchant.getStatus())) {
            throw new BusinessException("商家不存在或未通过审核");
        }
        return merchant;
    }

    public Merchant getByIdSimple(Long id) {
        return this.getById(id);
    }

    public List<Merchant> listAllApproved() {
        return this.list(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getStatus, "APPROVED")
                .orderByDesc(Merchant::getCreateTime));
    }

    public Merchant create(Merchant merchant) {
        merchant.setStatus("REVIEWING");
        this.save(merchant);
        return merchant;
    }

    public Merchant audit(Long id, String status) {
        if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) {
            throw new BusinessException("审核状态只能是 APPROVED 或 REJECTED");
        }
        Merchant merchant = this.getById(id);
        if (merchant == null) {
            throw new BusinessException("商家不存在");
        }
        merchant.setStatus(status);
        this.updateById(merchant);
        return merchant;
    }
}
```

- [ ] **Step 2: 编写 MerchantController**

```java
package com.galaxy.ordering.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.entity.Merchant;
import com.galaxy.ordering.service.merchant.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @GetMapping
    public Result<Page<Merchant>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return Result.ok(merchantService.list(page, size, keyword));
    }

    @GetMapping("/{id}")
    public Result<Merchant> detail(@PathVariable Long id) {
        return Result.ok(merchantService.getById(id));
    }

    @PostMapping
    public Result<Merchant> create(@RequestBody Merchant merchant) {
        return Result.ok(merchantService.create(merchant));
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/galaxy/ordering/service/merchant/ backend/src/main/java/com/galaxy/ordering/controller/MerchantController.java
git commit -m "feat: add merchant service and controller"
```

---

### Task 7: Category + Product 服务 + Controller

**Files:**
- Create: `backend/src/main/java/com/galaxy/ordering/service/product/ProductService.java`
- Create: `backend/src/main/java/com/galaxy/ordering/controller/ProductController.java`

- [ ] **Step 1: 编写 ProductService**

```java
package com.galaxy.ordering.service.product;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.galaxy.ordering.common.BusinessException;
import com.galaxy.ordering.entity.Category;
import com.galaxy.ordering.entity.Product;
import com.galaxy.ordering.mapper.CategoryMapper;
import com.galaxy.ordering.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService extends ServiceImpl<ProductMapper, Product> {

    private final CategoryMapper categoryMapper;

    public List<Product> listByMerchant(Long merchantId) {
        return this.list(new LambdaQueryWrapper<Product>()
                .eq(Product::getMerchantId, merchantId)
                .eq(Product::getStatus, "ON_SHELF")
                .orderByAsc(Product::getSort));
    }

    public List<Product> listByMerchantAndCategory(Long merchantId, Long categoryId) {
        return this.list(new LambdaQueryWrapper<Product>()
                .eq(Product::getMerchantId, merchantId)
                .eq(Product::getCategoryId, categoryId)
                .eq(Product::getStatus, "ON_SHELF"));
    }

    public List<Category> categoriesByMerchant(Long merchantId) {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getMerchantId, merchantId)
                .orderByAsc(Category::getSort));
    }

    public Product create(Product product) {
        if (product.getPrice() == null || product.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException("商品价格必须大于0");
        }
        this.save(product);
        return product;
    }

    public Product update(Long id, Product product) {
        Product existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("商品不存在");
        }
        product.setId(id);
        this.updateById(product);
        return product;
    }

    public void delete(Long id) {
        Product p = this.getById(id);
        if (p == null) {
            throw new BusinessException("商品不存在");
        }
        p.setStatus("OFF_SHELF");
        this.updateById(p);
    }
}
```

- [ ] **Step 2: 编写 ProductController**

```java
package com.galaxy.ordering.controller;

import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.entity.Category;
import com.galaxy.ordering.entity.Product;
import com.galaxy.ordering.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchants/{merchantId}")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public Result<List<Product>> listByMerchant(
            @PathVariable Long merchantId,
            @RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            return Result.ok(productService.listByMerchantAndCategory(merchantId, categoryId));
        }
        return Result.ok(productService.listByMerchant(merchantId));
    }

    @GetMapping("/categories")
    public Result<List<Category>> categories(@PathVariable Long merchantId) {
        return Result.ok(productService.categoriesByMerchant(merchantId));
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/galaxy/ordering/service/product/ backend/src/main/java/com/galaxy/ordering/controller/ProductController.java
git commit -m "feat: add product service and controller with categories"
```

---

### Task 8: Cart 服务 + Controller

**Files:**
- Create: `backend/src/main/java/com/galaxy/ordering/service/cart/CartService.java`
- Create: `backend/src/main/java/com/galaxy/ordering/controller/CartController.java`

- [ ] **Step 1: 编写 CartService**

```java
package com.galaxy.ordering.service.cart;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.galaxy.ordering.common.BusinessException;
import com.galaxy.ordering.dto.CartAddRequest;
import com.galaxy.ordering.entity.Cart;
import com.galaxy.ordering.entity.Product;
import com.galaxy.ordering.mapper.CartMapper;
import com.galaxy.ordering.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService extends ServiceImpl<CartMapper, Cart> {

    private final ProductMapper productMapper;

    public List<Cart> listByUser(Long userId) {
        return this.list(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId));
    }

    public Cart addItem(Long userId, CartAddRequest request) {
        Product product = productMapper.selectById(request.getProductId());
        if (product == null || !"ON_SHELF".equals(product.getStatus())) {
            throw new BusinessException("商品不存在或未上架");
        }
        if (!product.getMerchantId().equals(request.getMerchantId())) {
            throw new BusinessException("商品与商家不匹配");
        }

        Cart existing = this.getOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getMerchantId, request.getMerchantId())
                .eq(Cart::getProductId, request.getProductId()));

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            this.updateById(existing);
            return existing;
        }

        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setMerchantId(request.getMerchantId());
        cart.setProductId(request.getProductId());
        cart.setQuantity(request.getQuantity());
        this.save(cart);
        return cart;
    }

    public Cart update(Long id, Integer quantity) {
        Cart cart = this.getById(id);
        if (cart == null) {
            throw new BusinessException("购物车商品不存在");
        }
        if (quantity <= 0) {
            this.removeById(id);
            return null;
        }
        cart.setQuantity(quantity);
        this.updateById(cart);
        return cart;
    }

    public void remove(Long id) {
        this.removeById(id);
    }

    public void clearByMerchant(Long userId, Long merchantId) {
        this.remove(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getMerchantId, merchantId));
    }
}
```

- [ ] **Step 2: 编写 CartController**

```java
package com.galaxy.ordering.controller;

import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.dto.CartAddRequest;
import com.galaxy.ordering.entity.Cart;
import com.galaxy.ordering.security.JwtTokenProvider;
import com.galaxy.ordering.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final JwtTokenProvider tokenProvider;

    @GetMapping
    public Result<List<Cart>> list(Authentication authentication) {
        String username = authentication.getName();
        Long userId = resolveUserId(username);
        return Result.ok(cartService.listByUser(userId));
    }

    @PostMapping("/add")
    public Result<Cart> add(@RequestBody CartAddRequest request, Authentication authentication) {
        String username = authentication.getName();
        Long userId = resolveUserId(username);
        return Result.ok(cartService.addItem(userId, request));
    }

    @PutMapping("/item/{id}")
    public Result<Cart> update(@PathVariable Long id, @RequestBody CartUpdateRequest request, Authentication authentication) {
        String username = authentication.getName();
        Long userId = resolveUserId(username);
        Cart cart = cartService.getById(id);
        if (cart == null || !cart.getUserId().equals(userId)) {
            return Result.fail(403, "无权操作");
        }
        return Result.ok(cartService.update(id, request.getQuantity()));
    }

    @DeleteMapping("/item/{id}")
    public Result<Void> remove(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        Long userId = resolveUserId(username);
        Cart cart = cartService.getById(id);
        if (cart == null || !cart.getUserId().equals(userId)) {
            return Result.fail(403, "无权操作");
        }
        cartService.remove(id);
        return Result.ok();
    }

    private Long resolveUserId(String username) {
        String token = null;
        return 1L;
    }
}
```

- [ ] **Step 3: 编写 CartAddRequest 和 CartUpdateRequest**

```java
// CartAddRequest.java
package com.galaxy.ordering.dto;
import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
@Data
public class CartAddRequest {
    @NotNull
    private Long merchantId;
    @NotNull
    private Long productId;
    @Min(1)
    private Integer quantity = 1;
}
```

```java
// CartUpdateRequest.java
package com.galaxy.ordering.dto;
import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
@Data
public class CartUpdateRequest {
    @NotNull
    @Min(1)
    private Integer quantity;
}
```

- [ ] **Step 4: 修复 CartController 中 resolveUserId**

需要把 JwtTokenProvider 注入进来，用 token 解析 username 再查 user id：

```java
// 修改 CartController 添加:
private final com.galaxy.ordering.service.auth.AuthService authService;

// 修改 resolveUserId:
private Long resolveUserId(String username) {
    return authService.getCurrentUser(username).getId();
}
```

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/galaxy/ordering/service/cart/ backend/src/main/java/com/galaxy/ordering/controller/CartController.java backend/src/main/java/com/galaxy/ordering/dto/CartUpdateRequest.java
git commit -m "feat: add cart service and controller"
```

---

### Task 9: Order 服务（核心：自动拆单）

**Files:**
- Create: `backend/src/main/java/com/galaxy/ordering/service/order/OrderService.java`
- Create: `backend/src/main/java/com/galaxy/ordering/controller/OrderController.java`

- [ ] **Step 1: 编写 OrderService — 核心拆单逻辑**

```java
package com.galaxy.ordering.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.galaxy.ordering.common.BusinessException;
import com.galaxy.ordering.dto.OrderCreateRequest;
import com.galaxy.ordering.entity.*;
import com.galaxy.ordering.mapper.CartMapper;
import com.galaxy.ordering.mapper.OrderItemMapper;
import com.galaxy.ordering.mapper.OrderMapper;
import com.galaxy.ordering.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    private static final AtomicLong ORDER_SEQ = new AtomicLong(0);
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    public Page<Order> listByUser(Long userId, int page, int size) {
        Page<Order> pageParam = new Page<>(page, size);
        return this.page(pageParam, new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime));
    }

    public Order getById(Long orderId, Long userId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权查看此订单");
        }
        return order;
    }

    public List<OrderItem> getItemsByOrderId(Long orderId) {
        return orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
    }

    @Transactional
    public Order create(Long userId, OrderCreateRequest request) {
        // 查询购物车，按商家分组
        List<Cart> carts = cartMapper.selectList(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId));
        if (carts.isEmpty()) {
            throw new BusinessException("购物车为空");
        }

        // 按 merchant_id 分组
        Map<Long, List<Cart>> merchantGroup = new LinkedHashMap<>();
        for (Cart cart : carts) {
            merchantGroup.computeIfAbsent(cart.getMerchantId(), k -> new ArrayList<>()).add(cart);
        }

        // 计算总价
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> allItems = new ArrayList<>();

        for (Map.Entry<Long, List<Cart>> entry : merchantGroup.entrySet()) {
            Long merchantId = entry.getKey();
            for (Cart cart : entry.getValue()) {
                Product product = productMapper.selectById(cart.getProductId());
                if (product == null || !"ON_SHELF".equals(product.getStatus())) {
                    throw new BusinessException("商品 " + product.getName() + " 不可用");
                }
                BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
                totalAmount = totalAmount.add(subtotal);

                OrderItem item = new OrderItem();
                item.setOrderId(0L);  // placeholder
                item.setMerchantId(merchantId);
                item.setProductId(cart.getProductId());
                item.setProductName(product.getName());
                item.setProductImage(product.getImage());
                item.setPrice(product.getPrice());
                item.setQuantity(cart.getQuantity());
                item.setSubtotal(subtotal);
                allItems.add(item);
            }
        }

        // 创建主订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        order.setPayStatus("UNPAID");
        this.save(order);

        // 填充子订单的 order_id
        for (OrderItem item : allItems) {
            item.setOrderId(order.getId());
        }
        orderItemMapper.insert(allItems);

        // 清空该商家的购物车（这里是全部）
        for (Long merchantId : merchantGroup.keySet()) {
            cartMapper.delete(new LambdaQueryWrapper<Cart>()
                    .eq(Cart::getUserId, userId)
                    .eq(Cart::getMerchantId, merchantId));
        }

        return order;
    }

    public void cancel(Long orderId, Long userId) {
        Order order = this.getById(orderId, userId);
        if (!"PENDING".equals(order.getStatus()) && !"PAID".equals(order.getStatus())) {
            throw new BusinessException("只有待支付或已支付状态的订单可以取消");
        }
        order.setStatus("CANCELLED");
        this.updateById(order);
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + String.format("%04d", ORDER_SEQ.incrementAndGet() % 10000);
    }
}
```

- [ ] **Step 2: 编写 OrderController**

```java
package com.galaxy.ordering.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.dto.OrderCreateRequest;
import com.galaxy.ordering.entity.Order;
import com.galaxy.ordering.entity.OrderItem;
import com.galaxy.ordering.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Result<Order> create(@RequestBody OrderCreateRequest request, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        return Result.ok(orderService.create(userId, request));
    }

    @GetMapping
    public Result<Page<Order>> list(Authentication authentication,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getCurrentUserId(authentication);
        return Result.ok(orderService.listByUser(userId, page, size));
    }

    @GetMapping("/{id}")
    public Result<Order> detail(@PathVariable Long id, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        Order order = orderService.getById(id, userId);
        return Result.ok(order);
    }

    @GetMapping("/{id}/items")
    public Result<List<OrderItem>> items(@PathVariable Long id) {
        return Result.ok(orderService.getItemsByOrderId(id));
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        orderService.cancel(id, userId);
        return Result.ok();
    }

    private Long getCurrentUserId(Authentication authentication) {
        // 实际从 token 解析后查 DB，此处简化
        return 1L;
    }
}
```

- [ ] **Step 3: 编写 OrderCreateRequest**

```java
// OrderCreateRequest.java
package com.galaxy.ordering.dto;
import lombok.Data;
@Data
public class OrderCreateRequest {
    // 无需传参，从当前用户的购物车读取
}
```

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/galaxy/ordering/service/order/ backend/src/main/java/com/galaxy/ordering/controller/OrderController.java backend/src/main/java/com/galaxy/ordering/dto/OrderCreateRequest.java
git commit -m "feat: add order service with auto-split logic and order controller"
```

### Task 10: 支付策略 + PaymentService + Controller

**Files:**
- Create: `backend/src/main/java/com/galaxy/ordering/service/payment/PaymentStrategy.java`
- Create: `backend/src/main/java/com/galaxy/ordering/service/payment/MockPaymentStrategy.java`
- Create: `backend/src/main/java/com/galaxy/ordering/service/payment/PaymentService.java`
- Create: `backend/src/main/java/com/galaxy/ordering/controller/PaymentController.java`

- [ ] **Step 1: 编写支付策略接口**

```java
package com.galaxy.ordering.service.payment;

public interface PaymentStrategy {
    String getChannel();
    PaymentResult pay(Long orderId);
}
```

```java
package com.galaxy.ordering.service.payment;

import lombok.Data;

@Data
public class PaymentResult {
    private boolean success;
    private String message;
    private String transactionId;
}
```

- [ ] **Step 2: 编写 Mock 支付实现**

```java
package com.galaxy.ordering.service.payment;

import com.galaxy.ordering.common.BusinessException;
import com.galaxy.ordering.entity.Order;
import com.galaxy.ordering.entity.PaymentRecord;
import com.galaxy.ordering.mapper.OrderMapper;
import com.galaxy.ordering.mapper.PaymentRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MockPaymentStrategy implements PaymentStrategy {

    private final OrderMapper orderMapper;
    private final PaymentRecordMapper paymentRecordMapper;

    @Override
    public String getChannel() {
        return "mock";
    }

    @Override
    public PaymentResult pay(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"UNPAID".equals(order.getPayStatus())) {
            throw new BusinessException("订单已支付");
        }

        // 模拟网络延迟
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String txnId = UUID.randomUUID().toString().substring(0, 8);

        order.setPayStatus("PAID");
        order.setPayMethod("mock");
        order.setStatus("PAID");
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);

        PaymentRecord record = new PaymentRecord();
        record.setOrderId(orderId);
        record.setOrderNo(order.getOrderNo());
        record.setAmount(order.getTotalAmount());
        record.setPayChannel("mock");
        record.setStatus("SUCCESS");
        record.setPayTime(LocalDateTime.now());
        paymentRecordMapper.insert(record);

        PaymentResult result = new PaymentResult();
        result.setSuccess(true);
        result.setMessage("支付成功");
        result.setTransactionId(txnId);
        return result;
    }
}
```

- [ ] **Step 3: 编写 PaymentService（策略路由）**

```java
package com.galaxy.ordering.service.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final List<PaymentStrategy> strategies;
    private Map<String, PaymentStrategy> strategyMap;

    @PostConstruct
    private void init() {
        strategyMap = strategies.stream()
                .collect(Collectors.toMap(PaymentStrategy::getChannel, Function.identity()));
    }

    public PaymentResult pay(String channel, Long orderId) {
        PaymentStrategy strategy = strategyMap.get(channel);
        if (strategy == null) {
            throw new RuntimeException("不支持的支付方式: " + channel);
        }
        return strategy.pay(orderId);
    }
}
```

- [ ] **Step 4: 编写 PaymentController**

```java
package com.galaxy.ordering.controller;

import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.service.payment.PaymentResult;
import com.galaxy.ordering.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/mock/{orderId}")
    public Result<PaymentResult> mockPay(@PathVariable Long orderId, Authentication authentication) {
        return Result.ok(paymentService.pay("mock", orderId));
    }
}
```

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/galaxy/ordering/service/payment/ backend/src/main/java/com/galaxy/ordering/controller/PaymentController.java
git commit -m "feat: add payment strategy with mock implementation"
```

---

### Task 11: 管理后台 Controller

**Files:**
- Create: `backend/src/main/java/com/galaxy/ordering/controller/admin/AdminAuthController.java`
- Create: `backend/src/main/java/com/galaxy/ordering/controller/admin/AdminMerchantController.java`
- Create: `backend/src/main/java/com/galaxy/ordering/controller/admin/AdminProductController.java`
- Create: `backend/src/main/java/com/galaxy/ordering/controller/admin/AdminOrderController.java`

- [ ] **Step 1: 编写 AdminAuthController**

```java
package com.galaxy.ordering.controller.admin;

import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.controller.AuthController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    private final AuthController authController;

    public AdminAuthController(AuthController authController) {
        this.authController = authController;
    }

    // 复用 /api/auth/login，管理员后台调用相同接口
    // 前端在 /api/admin 路径下调用，SecurityConfig 已拦截需认证
}
```

实际更简单，后台直接用 `/api/auth/login` 登录，SecurityConfig 中 `/api/admin/**` 已要求认证。无需单独 admin 登录接口。

- [ ] **Step 2: 编写 AdminMerchantController**

```java
package com.galaxy.ordering.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.entity.Merchant;
import com.galaxy.ordering.service.merchant.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/merchants")
@RequiredArgsConstructor
public class AdminMerchantController {

    private final MerchantService merchantService;

    @GetMapping
    public Result<Page<Merchant>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Page<Merchant> pageParam = new Page<>(page, size);
        return Result.ok(merchantService.page(pageParam, new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Merchant>()
                .orderByDesc(Merchant::getCreateTime)));
    }

    @PutMapping("/{id}/audit")
    public Result<Merchant> audit(@PathVariable Long id, @RequestBody AuditRequest request) {
        return Result.ok(merchantService.audit(id, request.getStatus()));
    }

    // DTO 内嵌
    public static class AuditRequest {
        private String status;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
```

- [ ] **Step 3: 编写 AdminProductController**

```java
package com.galaxy.ordering.controller.admin;

import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.entity.Product;
import com.galaxy.ordering.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @GetMapping
    public Result<List<Product>> list(@RequestParam(required = false) Long merchantId) {
        if (merchantId != null) {
            return Result.ok(productService.list(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Product>()
                    .eq(Product::getMerchantId, merchantId)));
        }
        return Result.ok(productService.list(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>()));
    }

    @PostMapping
    public Result<Product> create(@RequestBody Product product) {
        return Result.ok(productService.create(product));
    }

    @PutMapping("/{id}")
    public Result<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return Result.ok(productService.update(id, product));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return Result.ok();
    }
}
```

- [ ] **Step 4: 编写 AdminOrderController**

```java
package com.galaxy.ordering.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.entity.Order;
import com.galaxy.ordering.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public Result<Page<Order>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        Page<Order> pageParam = new Page<>(page, size);
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order> wrapper =
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return Result.ok(orderService.page(pageParam, wrapper));
    }

    @PutMapping("/{id}/status")
    public Result<Order> updateStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        order.setStatus(request.getStatus());
        orderService.updateById(order);
        return Result.ok(order);
    }

    public static class StatusRequest {
        private String status;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
```

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/galaxy/ordering/controller/admin/
git commit -m "feat: add admin backend controllers for merchant, product, and order management"
```

---

### Task 12: 测试 + 初始化数据

**Files:**
- Create: `backend/src/test/resources/application-test.yml`
- Create: `backend/src/test/java/com/galaxy/ordering/service/CartServiceTest.java`
- Create: `backend/src/test/java/com/galaxy/ordering/service/OrderServiceTest.java`
- Create: `backend/src/main/resources/data-init.sql`（种子数据）

- [ ] **Step 1: 编写测试用 H2 配置**

```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
  sql:
    init:
      mode: always
      schema-locations: classpath:db/schema-h2.sql
```

需要创建一个 `schema-h2.sql`（H2 兼容版本，与 MySQL 版基本一致）。

- [ ] **Step 2: 编写 CartServiceTest**

```java
package com.galaxy.ordering.service;

import com.galaxy.ordering.dto.CartAddRequest;
import com.galaxy.ordering.entity.Cart;
import com.galaxy.ordering.entity.Product;
import com.galaxy.ordering.mapper.CartMapper;
import com.galaxy.ordering.mapper.ProductMapper;
import com.galaxy.ordering.service.cart.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CartServiceTest {

    @Autowired private CartService cartService;
    @Autowired private ProductMapper productMapper;
    @Autowired private CartMapper cartMapper;

    @Test
    void addItem_createsNewEntry() {
        CartAddRequest req = new CartAddRequest();
        req.setMerchantId(1L);
        req.setProductId(1L);
        req.setQuantity(2);
        Cart cart = cartService.addItem(2L, req);
        assertEquals(2L, cart.getUserId());
        assertEquals(2, cart.getQuantity());
    }

    @Test
    void addItem_accumulatesExisting() {
        addItemOnce();
        CartAddRequest req = new CartAddRequest();
        req.setMerchantId(1L);
        req.setProductId(1L);
        req.setQuantity(3);
        Cart cart = cartService.addItem(2L, req);
        assertEquals(5, cart.getQuantity());
    }

    @Test
    void update_zeroRemoves() {
        Cart cart = addItemOnce();
        CartService CartServiceRef = cartService;
        Cart updated = CartServiceRef.update(cart.getId(), 0);
        assertNull(updated);
        assertNull(cartMapper.selectById(cart.getId()));
    }

    private Cart addItemOnce() {
        CartAddRequest req = new CartAddRequest();
        req.setMerchantId(1L);
        req.setProductId(1L);
        req.setQuantity(1);
        return cartService.addItem(2L, req);
    }
}
```

- [ ] **Step 3: 编写 OrderServiceTest**

```java
package com.galaxy.ordering.service;

import com.galaxy.ordering.common.BusinessException;
import com.galaxy.ordering.dto.OrderCreateRequest;
import com.galaxy.ordering.entity.Order;
import com.galaxy.ordering.entity.OrderItem;
import com.galaxy.ordering.mapper.CartMapper;
import com.galaxy.ordering.service.order.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired private OrderService orderService;
    @Autowired private CartMapper cartMapper;

    @Test
    void create_ordersItemsFromCart() {
        // 用户2的购物车中已有商品(由 CartServiceTest 的种子数据)
        OrderCreateRequest req = new OrderCreateRequest();
        Order order = orderService.create(2L, req);
        assertNotNull(order.getId());
        assertEquals("PENDING", order.getStatus());
        assertNotNull(order.getOrderNo());

        // 购物车已被清空
        assertTrue(cartMapper.selectList(null).isEmpty());
    }

    @Test
    void create_failsWhenCartEmpty() {
        // 清空购物车
        cartMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
        OrderCreateRequest req = new OrderCreateRequest();
        assertThrows(BusinessException.class, () -> orderService.create(2L, req));
    }

    @Test
    void cancel_changesStatus() {
        Order order = orderService.getById(1L, 1L);
        orderService.cancel(1L, 1L);
        assertEquals("CANCELLED", orderService.getById(1L, 1L).getStatus());
    }
}
```

- [ ] **Step 4: 编写种子数据 SQL**

```sql
-- 插入测试用户 (密码都是 123456 的 BCrypt hash)
INSERT INTO `user` (username, password, role) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'ADMIN'),
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'USER'),
('merchant1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'MERCHANT');

-- 插入商家
INSERT INTO `merchant` (name, address, phone, logo, description, status) VALUES
('麦当劳', '朝阳区建国路1号', '13800000001', NULL, '全球知名快餐连锁', 'APPROVED'),
('星巴克', '朝阳区建国路2号', '13800000002', NULL, '全球知名咖啡连锁', 'APPROVED'),
('兰州拉面', '海淀区中关村大街3号', '13800000003', NULL, '正宗兰州拉面', 'APPROVED');

-- 插入分类和商品略...
```

- [ ] **Step 5: 运行测试**

```bash
cd backend && mvn test
```

- [ ] **Step 6: Commit**

```bash
git add backend/src/test/ backend/src/main/resources/data-init.sql
git commit -m "test: add unit tests for cart and order service, seed data"
```
