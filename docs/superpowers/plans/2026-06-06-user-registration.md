# User Registration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Add user registration feature so new users can create accounts via the frontend and authenticate with JWT.

**Architecture:** Backend adds register endpoint in AuthService with BCrypt password hashing. Frontend adds Register.vue page with form validation. Login page gets a "注册" link to /register. Role is fixed to USER.

**Tech Stack:** Spring Boot + MyBatis-Plus (backend), Vue 3 + Element Plus + Pinia (frontend), BCrypt (password hashing), JWT (authentication).

---

### Task 1: Backend — Create RegisterRequest DTO

**Files:**
- Create: `backend/src/main/java/com/galaxy/ordering/dto/RegisterRequest.java`

- [ ] **Step 1: Write the DTO**

```java
package com.galaxy.ordering.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度为3-50个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度至少6个字符")
    private String password;

    @Size(max = 20, message = "手机号最多20个字符")
    private String phone;
}
```

- [ ] **Step 2: Verify the DTO compiles**

Run: `cd backend && mvn compile -q`
Expected: No errors

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/galaxy/ordering/dto/RegisterRequest.java
git commit -m "feat: add RegisterRequest DTO"
```

---

### Task 2: Backend — Add register method to AuthService

**Files:**
- Modify: `backend/src/main/java/com/galaxy/ordering/service/auth/AuthService.java`

- [ ] **Step 1: Add register method to AuthService**

Add the following method to `AuthService` class (after the `login` method):

```java
public void register(RegisterRequest request) {
    // Check if username already exists
    User existing = userMapper.selectOne(
        new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
    if (existing != null) {
        throw new BusinessException("用户名已存在");
    }

    // Create new user with role USER
    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setPhone(request.getPhone());
    user.setRole("USER");
    userMapper.insert(user);
}
```

- [ ] **Step 2: Add import for RegisterRequest**

In `AuthService.java`, add: `import com.galaxy.ordering.dto.RegisterRequest;`

- [ ] **Step 3: Verify compilation**

Run: `cd backend && mvn compile -q`
Expected: No errors

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/galaxy/ordering/service/auth/AuthService.java
git commit -m "feat: add register method to AuthService"
```

---

### Task 3: Backend — Add register endpoint to AuthController

**Files:**
- Modify: `backend/src/main/java/com/galaxy/ordering/controller/AuthController.java`

- [ ] **Step 1: Add register endpoint**

Replace the entire file with:

```java
package com.galaxy.ordering.controller;

import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.dto.LoginRequest;
import com.galaxy.ordering.dto.LoginResponse;
import com.galaxy.ordering.dto.RegisterRequest;
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

    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.ok("注册成功");
    }
}
```

- [ ] **Step 2: Verify compilation**

Run: `cd backend && mvn compile -q`
Expected: No errors

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/galaxy/ordering/controller/AuthController.java
git commit -m "feat: add register endpoint to AuthController"
```

---

### Task 4: Frontend — Add /register route

**Files:**
- Modify: `frontend/src/router/index.js`

- [ ] **Step 1: Add register route**

Add a new route entry in the routes array:

```js
{ path: '/register', component: () => import('../views/Register.vue') },
```

Insert it right after the `/login` route (line 10).

The modified routes array should look like:
```js
const routes = [
  { path: '/', component: () => import('../views/Home.vue') },
  { path: '/merchant/:id', component: () => import('../views/MerchantDetail.vue') },
  { path: '/cart', component: () => import('../views/Cart.vue') },
  { path: '/order/confirm', component: () => import('../views/OrderConfirm.vue') },
  { path: '/order/pay/:orderId', component: () => import('../views/OrderPay.vue') },
  { path: '/my-orders', component: () => import('../views/MyOrders.vue') },
  { path: '/login', component: () => import('../views/Login.vue') },
  { path: '/register', component: () => import('../views/Register.vue') },
  // ... admin routes follow
]
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/router/index.js
git commit -m "feat: add /register route"
```

---

### Task 5: Frontend — Add register link on Login page

**Files:**
- Modify: `frontend/src/views/Login.vue`

- [ ] **Step 1: Add register link**

After the `</el-form>` closing tag (before `</el-card>`), add:

```vue
      <div style="text-align: right; margin-top: 12px;">
        <router-link to="/register" style="color: #409eff; text-decoration: none; font-size: 14px;">还没有账号？立即注册</router-link>
      </div>
```

- [ ] **Step 2: Verify the link renders**

Run: `cd frontend && npm run dev` (or verify in browser)
Expected: Login page shows "还没有账号？立即注册" link in bottom-right of form

- [ ] **Step 3: Commit**

```bash
git add frontend/src/views/Login.vue
git commit -m "feat: add register link on login page"
```

---

### Task 6: Frontend — Create Register page

**Files:**
- Create: `frontend/src/views/Register.vue`

- [ ] **Step 1: Create the Register page component**

```vue
<template>
  <div class="login-page">
    <el-card class="login-card">
      <h2>用户注册</h2>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" @keyup.enter="handleRegister" />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" prefix-icon="Lock" @keyup.enter="handleRegister" />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号（选填）" prefix-icon="Phone" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading" style="width: 100%">注册</el-button>
        </el-form-item>
      </el-form>
      <div style="text-align: center; margin-top: 12px;">
        <router-link to="/login" style="color: #409eff; text-decoration: none; font-size: 14px;">已有账号？去登录</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../api/request'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

// Custom validator: password must match
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次密码输入不一致'))
  } else {
    callback()
  }
}

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度为3-50个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await request.post('/api/auth/register', {
      username: form.username,
      password: form.password,
      phone: form.phone || undefined
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    // error already handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 56px);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
  padding: 20px;
}
.login-card h2 {
  text-align: center;
  margin-bottom: 24px;
  color: #333;
}
</style>
```

- [ ] **Step 2: Verify the page renders**

Run: `cd frontend && npm run dev`
Expected: Navigate to /register, form renders with 4 fields + register button + login link

- [ ] **Step 3: Commit**

```bash
git add frontend/src/views/Register.vue
git commit -m "feat: add register page with form validation"
```

---

### Task 7: Integration Test — Verify register flow end-to-end

- [ ] **Step 1: Restart backend, then test register API**

```bash
# Kill 8080 if needed
lsof -ti:8080 | xargs kill -9
# Start backend (run in terminal or background)
cd backend && mvn spring-boot:run
```

Then test:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456","phone":"13800138000"}'
```
Expected: `{"code":200,"msg":"注册成功","data":null}`

Test duplicate username:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'
```
Expected: `{"code":500,"msg":"用户名已存在",...}`

- [ ] **Step 2: Test login with the newly registered user**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'
```
Expected: Returns JWT token with `userId`, `username`, `role: "USER"`

- [ ] **Step 3: Test frontend register page**

Visit `http://localhost:3000/register`, register a new user, verify redirect to /login after success.

- [ ] **Step 4: Final commit**

```bash
git add -u
git commit -m "feat: verify register flow end-to-end"
```
