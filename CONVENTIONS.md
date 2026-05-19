# 开发规范

> 适用：Spring Boot 3.x + Vue 3 + TypeScript + MyBatis-Plus

---

## 一、命名规范

### 1.1 Java 后端

| 类型 | 规则 | 示例 |
|------|------|------|
| 类名 | 大驼峰 (PascalCase) | `UserController`, `OrderService` |
| 方法名 | 小驼峰 (camelCase) | `getUserById()`, `updateOrderStatus()` |
| 变量名 | 小驼峰 | `userId`, `orderList` |
| 常量 | 全大写+下划线 | `MAX_RETRY_COUNT`, `DEFAULT_PAGE_SIZE` |
| 包名 | 全小写 | `com.park.controller`, `com.park.service` |
| 实体类 | 大驼峰，与表名对应 | `SysUser`, `ServiceOrder` |
| Mapper | 实体名 + Mapper | `SysUserMapper` |
| Service 接口 | 首字母 I 或不加前缀 | `IUserService` 或 `UserService` |
| Service 实现 | Service 接口名 + Impl | `UserServiceImpl` |
| Controller | 模块名 + Controller | `UserController`, `ParkController` |
| DTO | 功能名 + DTO | `UserLoginDTO`, `OrderQueryDTO` |
| VO | 实体名 + VO | `UserVO`, `OrderDetailVO` |
| 枚举类 | 大驼峰 | `OrderStatus`, `VerifyState` |

### 1.2 数据库

| 类型 | 规则 | 示例 |
|------|------|------|
| 表名 | 小写+下划线 | `sys_user`, `service_order` |
| 字段名 | 小写+下划线 | `create_time`, `user_name` |
| 主键 | `id` | `id BIGINT AUTO_INCREMENT` |
| 索引 | `idx_表名_字段名` | `idx_sys_user_phone` |
| 唯一索引 | `uk_表名_字段名` | `uk_sys_user_phone` |
| 创建时间 | `create_time` | - |
| 更新时间 | `update_time` | - |
| 逻辑删除 | `is_deleted` (0/1) | - |

### 1.3 Vue3 + TypeScript 前端

| 类型 | 规则 | 示例 |
|------|------|------|
| 组件文件 | 大驼峰 | `UserList.vue`, `OrderDetail.vue` |
| 视图页面 | 大驼峰 | `ParkOverview.vue`, `ServiceHall.vue` |
| 组合式函数 | `use` + 大驼峰 | `useUserStore()`, `useAuth()` |
| 函数/方法 | 小驼峰 | `fetchUserList()`, `handleSubmit()` |
| 变量 | 小驼峰 | `userName`, `orderData` |
| 常量 | 全大写+下划线 | `API_BASE_URL`, `PAGE_SIZE` |
| TypeScript 接口 | `I` 前缀+大驼峰 | `IUser`, `IOrderItem` |
| TypeScript 类型 | 大驼峰 | `UserRole`, `OrderStatus` |
| CSS 类名 | 小写+连字符 | `.user-list`, `.order-card` |

---

## 二、API 规范

### 2.1 RESTful 接口命名

| 操作 | 方法 | URL 示例 |
|------|------|----------|
| 列表查询 | GET | `/api/v1/users` |
| 分页查询 | GET | `/api/v1/users?page=1&size=10` |
| 详情查询 | GET | `/api/v1/users/{id}` |
| 新增 | POST | `/api/v1/users` |
| 修改 | PUT | `/api/v1/users/{id}` |
| 部分修改 | PATCH | `/api/v1/users/{id}/status` |
| 删除 | DELETE | `/api/v1/users/{id}` |
| 批量操作 | POST | `/api/v1/users/batch-delete` |

### 2.2 响应格式

统一返回结构：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

分页返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

### 2.3 状态码约定

| code | 含义 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 三、注释规范

### 3.1 必须写注释的场景

- **复杂业务逻辑**：算法、状态机、多层判断等阅读代码无法一眼看懂的
- **非显而易见的约束**：为什么这个值不能为空、为什么用这个阈值
- **临时方案/坑**：`// FIXME: 临时绕开 xxx 问题，等 xxx 版本修复后移除`
- **对外接口**：Controller 的方法用 Javadoc 标注入参、出参、异常

### 3.2 不需要写注释

- 不言自明的代码（方法名已说清楚意图）
- getter/setter
- `// 新增用户` 这类复读代码的注释
- 自动生成的代码

### 3.3 注释格式

Java:

```java
/**
 * 根据企业标签匹配适用政策
 * 匹配规则：标签交集 >= 1 即视为匹配
 */
public List<Policy> matchPolicies(Long enterpriseId) {
    // ...
}
```

TypeScript:

```typescript
/**
 * 根据企业标签匹配适用政策
 * 匹配规则：标签交集 >= 1 即视为匹配
 */
function matchPolicies(enterpriseId: number): Policy[] {
    // ...
}
```

---

## 四、代码格式

### 4.1 缩进与换行

- 缩进 4 空格（Java）/ 2 空格（Vue/TS），不用 Tab
- 单行不超过 120 字符
- 方法之间空一行
- 逻辑块之间空一行

### 4.2 导入顺序

Java:
1. 标准库
2. 第三方库
3. 项目内部

Vue/TS:
1. 第三方库
2. 项目内部模块
3. 组件导入

### 4.3 空行规则

- import 和代码之间空一行
- 最后一个 import 和代码之间空一行
- 类/组件内方法之间空一行

---

## 五、Git 规范

### 5.1 分支

| 分支 | 用途 |
|------|------|
| `main` / `master` | 生产分支 |
| `dev` | 开发分支 |
| `feature/xxx` | 功能分支，从 dev 拉 |
| `fix/xxx` | 修复分支，从 dev 拉 |
| `hotfix/xxx` | 紧急修复，从 main 拉 |

### 5.2 Commit Message

```
<type>: <简短描述>

- 具体改动点1
- 具体改动点2
```

type 类型：`feat` `fix` `refactor` `docs` `style` `test` `chore`

---

## 六、安全规范

- 所有接口除首页展示外均需鉴权
- 敏感数据（身份证、手机号）加密存储
- 接口参数校验不能仅依赖前端，后端必须二次校验
- 文件上传校验类型白名单、大小限制
- SQL 使用参数化查询，禁止拼接
- 用户输入在输出时做 XSS 转义
