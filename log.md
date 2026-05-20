# 会话操作日志

> 项目：园区服务运营系统
> 日期：2026-05-18

---

## 模块一：园区概况展示（3.2）

### 数据库
- 创建 `park_info`、`park_facility`、`park_honor` 三张表（DDL + 索引 + UTF8MB4）
- 插入 2 个园区（数字科技园、生物医药产业园）及配套 9 条设施、7 条荣誉数据
- 文件：[db/park_tables.sql](backend/src/main/resources/db/park_tables.sql)

### 后端（Spring Boot 3.2 + MyBatis-Plus）
- 新增 Entity：[ParkInfo.java](backend/src/main/java/com/park/entity/ParkInfo.java)、[ParkFacility.java](backend/src/main/java/com/park/entity/ParkFacility.java)、[ParkHonor.java](backend/src/main/java/com/park/entity/ParkHonor.java)
- 新增 Mapper：ParkInfoMapper、ParkFacilityMapper、ParkHonorMapper
- 新增 VO：[ParkOverviewVO.java](backend/src/main/java/com/park/dto/ParkOverviewVO.java)
- 新增 Service：[ParkService.java](backend/src/main/java/com/park/service/ParkService.java) + [ParkServiceImpl.java](backend/src/main/java/com/park/service/impl/ParkServiceImpl.java)
- 新增 Controller：[ParkController.java](backend/src/main/java/com/park/controller/ParkController.java)
  - `GET /api/parks` — 园区列表
  - `GET /api/parks/{id}/overview` — 园区概况（含设施、荣誉）
- 修改 [SecurityConfig.java](backend/src/main/java/com/park/config/SecurityConfig.java)：添加 `/api/parks/**` 白名单

### 前端（Vue 3 + Element Plus + Vite）
- 新增 API 层：[src/api/park.js](frontend/src/api/park.js)
- 新增组件：[ParkSwitcher.vue](frontend/src/components/ParkSwitcher.vue)（园区下拉切换，同步 URL query）
- 新增组件：[FacilityList.vue](frontend/src/components/FacilityList.vue)（配套设施卡片网格，P0）
- 新增组件：[HonorCarousel.vue](frontend/src/components/HonorCarousel.vue)（荣誉资质轮播，P1）
- 新增页面：[ParkOverview.vue](frontend/src/views/ParkOverview.vue)（主页面，含园区 Hero、交通、规划、设施、荣誉）
- 修改路由：添加 `/park-overview`

### Bug 修复
- 修复登录失败页面刷新问题：[request.js](frontend/src/utils/request.js)：401 拦截增加路径判断 `window.location.pathname !== '/login'`
- 修复登录跳转：[Login.vue](frontend/src/views/Login.vue)：改为 `router.push('/park-overview')`
- 移除地图 SDK 占位区域及相关 CSS/图标导入
- 修复详情页"一直转圈"：[ServiceDetail.vue](frontend/src/views/ServiceDetail.vue)：`detail.getSteps()` → `detail.steps`，`detail.getPriceInfo()` → `detail.priceInfo`
- 新增侧边栏导航：[MainLayout.vue](frontend/src/layout/MainLayout.vue) 添加 el-menu（园区概况 + 企业服务大厅）

---

## 模块二：企业服务大厅（3.3）

### 数据库
- 创建 `service_category`、`service_item`、`service_provider` 三张表
- `service_item` 使用 JSON 字段存储流程步骤（`steps`）和定价信息（`price_info`）
- `service_provider` 仅建表占位（P1）
- 插入 5 条分类 + 5 条服务（高新技术企业认定、科技型中小企业评价、代理记账、人才招聘、商标注册代理）
- 文件：[db/service_tables.sql](backend/src/main/resources/db/service_tables.sql)

### 后端
- 新增 Entity：[ServiceCategory.java](backend/src/main/java/com/park/entity/ServiceCategory.java)、[ServiceItem.java](backend/src/main/java/com/park/entity/ServiceItem.java)、[ServiceProvider.java](backend/src/main/java/com/park/entity/ServiceProvider.java)
- 新增 Mapper：ServiceCategoryMapper、ServiceItemMapper、ServiceProviderMapper
- 新增 DTO：[ServiceItemQueryDTO.java](backend/src/main/java/com/park/dto/ServiceItemQueryDTO.java)（分页筛选参数）
- 新增 VO：[ServiceItemVO.java](backend/src/main/java/com/park/dto/ServiceItemVO.java)、[ServiceDetailVO.java](backend/src/main/java/com/park/dto/ServiceDetailVO.java)（含 JSON→List/Map 解析）
- 新增 Service：[ServiceCatalogService.java](backend/src/main/java/com/park/service/ServiceCatalogService.java) + [ServiceCatalogServiceImpl.java](backend/src/main/java/com/park/service/impl/ServiceCatalogServiceImpl.java)（分页+关键词搜索+分类筛选）
- 新增 Controller：[ServiceController.java](backend/src/main/java/com/park/controller/ServiceController.java)
  - `GET /api/services/categories` — 分类列表
  - `GET /api/services/items` — 服务分页列表（支持 categoryId/keyword/page/size）
  - `GET /api/services/items/{id}` — 服务详情
- 修改 [SecurityConfig.java](backend/src/main/java/com/park/config/SecurityConfig.java)：添加 `/api/services/**` 白名单

### 前端
- 新增 API 层：[src/api/service.js](frontend/src/api/service.js)
- 新增页面：[ServiceList.vue](frontend/src/views/ServiceList.vue)（分类标签切换 + 搜索框 + 卡片网格 + 分页，筛选参数同步 URL Query）
- 新增页面：[ServiceDetail.vue](frontend/src/views/ServiceDetail.vue)（面包屑 + 基础信息 + el-steps 流程 + 费用表格 + 立即办理按钮）
- 新增占位页：[ApplyPlaceholder.vue](frontend/src/views/ApplyPlaceholder.vue)（申请办理占位）
- 修改路由：添加 `/services`、`/services/:id`、`/apply/:serviceId`

---

## 服务运行信息
- 后端：`http://localhost:8080`（Spring Boot，进程 PID 约 10964 → 重启后变化）
- 前端：`http://localhost:3002`（Vite，因 3000/3001 被占用自动顺延）
- 数据库：MySQL 9.7 @ localhost:3306，库名 `park_service`
- 测试账号：admin / admin123

## API 测试结果
| 接口 | 状态 | 说明 |
|------|------|------|
| `GET /api/parks` | 200 | 返回 2 园区 |
| `GET /api/parks/1/overview` | 200 | 含 5 设施 + 4 荣誉 |
| `GET /api/parks/999/overview` | 404 | 园区不存在 |
| `POST /api/auth/login` (正确) | 200 | 返回 token |
| `POST /api/auth/login` (错误) | 401 | 用户名或密码错误 |
| `GET /api/services/categories` | 200 | 返回 5 分类 |
| `GET /api/services/items` | 200 | 分页 5 条 |
| `GET /api/services/items?categoryId=4` | 200 | 按分类筛选 1 条 |
| `GET /api/services/items?keyword=高新` | 200 | 关键词搜索 1 条 |
| `GET /api/services/items/1` | 200 | 详情含 steps+priceInfo |
| `GET /api/services/items/999` | 404 | 服务不存在 |

## 前端页面路由
| 路径 | 页面 | 组件 |
|------|------|------|
| `/login` | 登录页 | Login.vue |
| `/park-overview` | 园区概况展示 | ParkOverview.vue |
| `/services` | 企业服务大厅列表 | ServiceList.vue |
| `/services/:id` | 服务详情 | ServiceDetail.vue |
| `/apply/:serviceId` | 申请办理（占位） | ApplyPlaceholder.vue |

---

## 模块三：后台内容管理（3.9）

### 数据库
- `park_info` 扩展字段：`status`、`sort_order`、`video_url`、`create_by`、`update_by`
- 创建 `cms_article`（资讯文章表）含 type/status/publish_time/view_count 等字段
- 创建 `cms_banner`（轮播图表）含 image_url/link_url/begin_time/end_time 等字段
- 更新 2 个园区数据，插入 3 篇示例文章（含动态/政策/草稿）、4 条轮播图
- 文件：[db/cms_tables.sql](backend/src/main/resources/db/cms_tables.sql)

### 后端
- 新增 Entity：[CmsArticle.java](backend/src/main/java/com/park/entity/CmsArticle.java)、[CmsBanner.java](backend/src/main/java/com/park/entity/CmsBanner.java)
- 修改 [ParkInfo.java](backend/src/main/java/com/park/entity/ParkInfo.java)：新增 status/sortOrder/videoUrl/createBy/updateBy 字段
- 新增 Mapper：CmsArticleMapper、CmsBannerMapper
- 新增 DTO：[ArticleQueryDTO.java](backend/src/main/java/com/park/dto/ArticleQueryDTO.java)、[BannerQueryDTO.java](backend/src/main/java/com/park/dto/BannerQueryDTO.java)、[ParkQueryDTO.java](backend/src/main/java/com/park/dto/ParkQueryDTO.java)
- 新增通用分页：[PageResult.java](backend/src/main/java/com/park/common/PageResult.java)
- 新增 Service：[AdminParkService.java](backend/src/main/java/com/park/service/AdminParkService.java) + [AdminParkServiceImpl.java](backend/src/main/java/com/park/service/impl/AdminParkServiceImpl.java)
- 新增 Service：[ArticleService.java](backend/src/main/java/com/park/service/ArticleService.java) + [ArticleServiceImpl.java](backend/src/main/java/com/park/service/impl/ArticleServiceImpl.java)（含发布时自动填充发布时间）
- 新增 Service：[BannerService.java](backend/src/main/java/com/park/service/BannerService.java) + [BannerServiceImpl.java](backend/src/main/java/com/park/service/impl/BannerServiceImpl.java)
- 新增 Service：[FileService.java](backend/src/main/java/com/park/service/FileService.java) + [FileServiceImpl.java](backend/src/main/java/com/park/service/impl/FileServiceImpl.java)（本地存储占位，5MB限制，类型白名单）
- 新增 Controller：[AdminParkController.java](backend/src/main/java/com/park/controller/AdminParkController.java)（`/api/admin/parks` CRUD+状态切换）
- 新增 Controller：[ArticleController.java](backend/src/main/java/com/park/controller/ArticleController.java)（`/api/admin/articles` CRUD+状态切换）
- 新增 Controller：[BannerController.java](backend/src/main/java/com/park/controller/BannerController.java)（`/api/admin/banners` CRUD+状态切换）
- 新增 Controller：[UploadController.java](backend/src/main/java/com/park/controller/UploadController.java)（`/api/admin/upload/image`）
- 新增 [WebMvcConfig.java](backend/src/main/java/com/park/config/WebMvcConfig.java)：静态资源映射 `/uploads/**` → `file:uploads/`
- 修改 [SecurityConfig.java](backend/src/main/java/com/park/config/SecurityConfig.java)：添加 `/uploads/**` 白名单
- 修改 [CorsConfig.java](backend/src/main/java/com/park/config/CorsConfig.java)：添加 PATCH 方法支持

### 前端
- 新增 API 层：[src/api/admin.js](frontend/src/api/admin.js)（园区/文章/轮播图/上传全部API）
- 新增组件：[UploadBtn.vue](frontend/src/components/UploadBtn.vue)（图片上传按钮，含前端 5MB + 类型校验）
- 新增页面：[ParkManage.vue](frontend/src/views/ParkManage.vue)（园区管理：表格+搜索+弹窗编辑+状态开关）
- 新增页面：[ArticleManage.vue](frontend/src/views/ArticleManage.vue)（资讯管理：表格+类型/状态筛选+Markdown编辑+发布状态切换）
- 新增页面：[BannerManage.vue](frontend/src/views/BannerManage.vue)（轮播图管理：表格+缩略图预览+时间范围选择器+状态开关）
- 修改路由：添加 `/admin/parks`、`/admin/articles`、`/admin/banners`
- 修改 [MainLayout.vue](frontend/src/layout/MainLayout.vue)：侧边栏新增"内容管理"子菜单组

### Bug 修复
- 修复 PATCH 请求 CORS 报错：[CorsConfig.java](backend/src/main/java/com/park/config/CorsConfig.java) 缺少 PATCH 方法
- 修复状态栏展示不全：`el-switch` 内文字移除，改为 switch 后跟文字标签，宽度调整为 100px
- 修复上传按钮无效：[UploadBtn.vue](frontend/src/components/UploadBtn.vue) button-in-label → ref.click() + emit
- 修复上传后图片加载失败：[vite.config.js](frontend/vite.config.js) 添加 `/uploads`、`/static` 代理
- 修复 multipart 5MB 限制：[application-dev.yml](backend/src/main/resources/application-dev.yml) 添加 `max-file-size: 5MB`
- 新增 [GlobalExceptionHandler.java](backend/src/main/java/com/park/common/GlobalExceptionHandler.java)：`MaxUploadSizeExceededException` 处理

---

## 模块四：前后台数据对应修复

### 问题
- `park_facility` / `park_honor` 前台有展示（ParkOverview.vue）但后台无管理
- `cms_article` 后台有管理（ArticleManage.vue）但前台无展示
- `cms_banner` 后台有管理（BannerManage.vue）但首页无轮播图

### 修复
- [AdminParkController.java](backend/src/main/java/com/park/controller/AdminParkController.java)：新增设施/荣誉子资源CRUD（`/api/admin/parks/{parkId}/facilities|honors`）
- 新增 [PublicArticleController.java](backend/src/main/java/com/park/controller/PublicArticleController.java)（`/api/articles` 公开接口）
- 新增 [PublicBannerController.java](backend/src/main/java/com/park/controller/PublicBannerController.java)（`/api/banners` 公开接口，按时间范围过滤）
- 修改 [SecurityConfig.java](backend/src/main/java/com/park/config/SecurityConfig.java)：添加 `/api/articles/**`、`/api/banners/**` 白名单
- 新增 [src/api/public.js](frontend/src/api/public.js)（前台公开API）
- 重写 [ParkManage.vue](frontend/src/views/ParkManage.vue)：三Tab（基础信息/配套设施/荣誉资质）
- 新增 [ArticleList.vue](frontend/src/views/ArticleList.vue)（前台资讯列表+类型筛选+搜索+分页）
- 新增 [ArticleDetail.vue](frontend/src/views/ArticleDetail.vue)（前台文章详情+简易Markdown渲染）
- 修改 [Home.vue](frontend/src/views/Home.vue)：新增轮播图组件
- 修改路由：添加 `/articles`、`/articles/:id`
- 修改 [MainLayout.vue](frontend/src/layout/MainLayout.vue)：新增"资讯与政策"菜单

---

## 模块五：服务管理后台（3.10 部分）

### 后端
- 新增 Service：[AdminServiceService.java](backend/src/main/java/com/park/service/AdminServiceService.java) + [AdminServiceServiceImpl.java](backend/src/main/java/com/park/service/impl/AdminServiceServiceImpl.java)
- 新增 Controller：[AdminServiceController.java](backend/src/main/java/com/park/controller/AdminServiceController.java)
  - `GET/POST/PUT/DELETE /api/admin/services/categories` — 分类CRUD
  - `GET/POST/PUT/DELETE /api/admin/services/items` — 项目CRUD（含分页筛选）
  - `PATCH /api/admin/services/items/{id}/status` — 上下架切换

### 前端
- 新增页面：[ServiceManage.vue](frontend/src/views/ServiceManage.vue)（两Tab：分类管理+项目管理，含动态步骤编辑器+费用三字段JSON序列化）
- 修改路由：添加 `/admin/services`
- 修改 [MainLayout.vue](frontend/src/layout/MainLayout.vue)：内容管理新增"服务管理"

---

## 模块六：用户注册登录 + 用户管理

### 数据库
- `sys_user` 新增字段：`verification_code VARCHAR(10)`、`verification_code_time DATETIME`、`last_login_time DATETIME`
- 新增索引：`uk_phone`（手机号唯一索引）

### 后端
- 修改 [User.java](backend/src/main/java/com/park/entity/User.java)：新增 verificationCode/verificationCodeTime/lastLoginTime 属性
- 新增 DTO：[PhoneLoginRequest.java](backend/src/main/java/com/park/dto/PhoneLoginRequest.java)、[SendCodeRequest.java](backend/src/main/java/com/park/dto/SendCodeRequest.java)、[UserQueryDTO.java](backend/src/main/java/com/park/dto/UserQueryDTO.java)
- 修改 [UserService.java](backend/src/main/java/com/park/service/UserService.java)：新增 sendCode/phoneLogin 方法
- 修改 [UserServiceImpl.java](backend/src/main/java/com/park/service/impl/UserServiceImpl.java)：实现验证码生成（6位随机数）、5分钟过期校验、手机号自动注册、最后登录时间记录
- 新增 Service：[AdminUserService.java](backend/src/main/java/com/park/service/AdminUserService.java) + [AdminUserServiceImpl.java](backend/src/main/java/com/park/service/impl/AdminUserServiceImpl.java)（含用户名/手机号唯一性校验）
- 修改 [AuthController.java](backend/src/main/java/com/park/controller/AuthController.java)：新增 `POST /api/auth/send-code`、`POST /api/auth/phone-login`
- 新增 Controller：[AdminUserController.java](backend/src/main/java/com/park/controller/AdminUserController.java)
  - `GET/POST/PUT/DELETE /api/admin/users` — 用户CRUD
  - `PATCH /api/admin/users/{id}/status` — 封禁/解封
  - `PATCH /api/admin/users/{id}/password` — 重置密码

### 前端
- 重写 [Login.vue](frontend/src/views/Login.vue)：双Tab（密码登录 + 手机号登录/注册，含60秒验证码倒计时）
- 新增页面：[UserManage.vue](frontend/src/views/UserManage.vue)（表格+搜索+新增/编辑/改密/删除/封禁解封）
- 修改路由：添加 `/admin/users`
- 修改 [MainLayout.vue](frontend/src/layout/MainLayout.vue)：内容管理新增"用户管理"

---

## 模块七：全局 UI 样式调整

### 设计系统
- 新建 [src/styles/global.css](frontend/src/styles/global.css)：
  - 设计Token：`--primary: #165DFF`、`--sidebar-bg: #1D2129`、`--bg-page: #F2F3F5`、`--radius: 8px`、`--shadow: 0 2px 8px rgba(0,0,0,0.08)` 等
  - Element Plus 全局覆盖：按钮/卡片/表格/输入框/对话框/分页/标签/开关
  - 通用布局类：`.page-header`、`.search-card`、`.pagination`、`.section-title`

### 修改文件
- [main.js](frontend/src/main.js)：引入 global.css
- [MainLayout.vue](frontend/src/layout/MainLayout.vue)：暗色侧边栏+Logo图标+面包屑+响应式折叠(768px)
- [Login.vue](frontend/src/views/Login.vue)：渐变背景+卡片悬浮阴影+移动端适配
- [Home.vue](frontend/src/views/Home.vue)：轮播图+欢迎区+三列快捷入口卡片（图标+悬浮动效）
- [ParkOverview.vue](frontend/src/views/ParkOverview.vue)：Hero蓝色渐变+统一section标题样式+响应式
- [ServiceList.vue](frontend/src/views/ServiceList.vue)：卡片悬浮动效+排版
- [ServiceDetail.vue](frontend/src/views/ServiceDetail.vue)：排版间距+颜色变量+响应式
- [ArticleList.vue](frontend/src/views/ArticleList.vue)：卡片交互+响应式折叠
- [ArticleDetail.vue](frontend/src/views/ArticleDetail.vue)：内容区排版（h3/h4/li/strong）
- 5个Admin管理页：移除冗余scoped样式，统一由global.css管理
- [FacilityList.vue](frontend/src/components/FacilityList.vue)、[HonorCarousel.vue](frontend/src/components/HonorCarousel.vue)：统一颜色变量

---

## 当前完整 API 清单

### 公开接口（无需认证）
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/login` | 账号+密码登录 |
| POST | `/api/auth/register` | 账号+密码注册 |
| POST | `/api/auth/send-code` | 发送手机验证码 |
| POST | `/api/auth/phone-login` | 手机号+验证码登录 |
| GET | `/api/health` | 健康检查 |
| GET | `/api/parks` | 园区列表 |
| GET | `/api/parks/{id}/overview` | 园区概况（含设施、荣誉） |
| GET | `/api/services/categories` | 服务分类列表 |
| GET | `/api/services/items` | 服务项目分页列表 |
| GET | `/api/services/items/{id}` | 服务项目详情 |
| GET | `/api/articles` | 资讯文章分页列表 |
| GET | `/api/articles/{id}` | 资讯文章详情 |
| GET | `/api/banners` | 当前有效轮播图列表 |

### 后台管理接口（需认证）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET/POST/PUT/DELETE | `/api/admin/parks` | 园区CRUD |
| PATCH | `/api/admin/parks/{id}/status` | 园区启停 |
| GET/POST/PUT/DELETE | `/api/admin/parks/{parkId}/facilities` | 设施CRUD |
| GET/POST/PUT/DELETE | `/api/admin/parks/{parkId}/facilities/{id}` | 设施单条操作 |
| GET/POST/PUT/DELETE | `/api/admin/parks/{parkId}/honors` | 荣誉CRUD |
| GET/POST/PUT/DELETE | `/api/admin/parks/{parkId}/honors/{id}` | 荣誉单条操作 |
| GET/POST/PUT/DELETE | `/api/admin/articles` | 文章CRUD |
| PATCH | `/api/admin/articles/{id}/status` | 文章发布/草稿切换 |
| GET/POST/PUT/DELETE | `/api/admin/banners` | 轮播图CRUD |
| PATCH | `/api/admin/banners/{id}/status` | 轮播图启停 |
| POST | `/api/admin/upload/image` | 图片上传（5MB限制） |
| GET/POST/PUT/DELETE | `/api/admin/services/categories` | 服务分类CRUD |
| GET/POST/PUT/DELETE | `/api/admin/services/items` | 服务项目CRUD |
| PATCH | `/api/admin/services/items/{id}/status` | 服务上下架 |
| GET/POST/PUT/DELETE | `/api/admin/users` | 用户CRUD |
| PATCH | `/api/admin/users/{id}/status` | 用户封禁/解封 |
| PATCH | `/api/admin/users/{id}/password` | 重置密码 |

---

## 前端完整路由
| 路径 | 页面 | 组件 |
|------|------|------|
| `/login` | 登录页 | Login.vue |
| `/` | 首页（轮播图+快捷入口） | Home.vue |
| `/park-overview` | 园区概况展示 | ParkOverview.vue |
| `/services` | 企业服务大厅列表 | ServiceList.vue |
| `/services/:id` | 服务详情 | ServiceDetail.vue |
| `/apply/:serviceId` | 申请办理（占位） | ApplyPlaceholder.vue |
| `/articles` | 资讯政策列表 | ArticleList.vue |
| `/articles/:id` | 文章详情 | ArticleDetail.vue |
| `/admin/parks` | 园区管理（含设施/荣誉） | ParkManage.vue |
| `/admin/articles` | 资讯管理 | ArticleManage.vue |
| `/admin/banners` | 轮播图管理 | BannerManage.vue |
| `/admin/services` | 服务管理（分类+项目） | ServiceManage.vue |
| `/admin/users` | 用户管理 | UserManage.vue |

---

## 服务运行信息
- 后端：`http://localhost:8080`（Spring Boot 3.2.5）
- 前端：`http://localhost:3000`（Vite 5.x）
- 数据库：MySQL 8.0 @ localhost:3306，库名 `park_service`
- 测试账号：admin / admin123

---

## 模块十：RBAC 权限系统

### 数据库
- 创建 `sys_permission` 表（id, permission_name, permission_code, permission_type, parent_id, path, sort_order, create_time, update_time, is_deleted）
- 创建 `sys_role_permission` 表（id, role_id, permission_id）
- 插入 4 角色：super_admin / admin / reviewer / user
- 插入 47 条权限数据（14 菜单 + 33 按钮/API 权限）
- 分配角色权限映射 92 条
- admin 用户分配 super_admin 角色
- 文件：[db/rbac.sql](backend/src/main/resources/db/rbac.sql)

### 后端
- 新增 Entity：[Permission.java](backend/src/main/java/com/park/entity/Permission.java)、[RolePermission.java](backend/src/main/java/com/park/entity/RolePermission.java)
- 新增 Mapper：[PermissionMapper.java](backend/src/main/java/com/park/mapper/PermissionMapper.java)、[RolePermissionMapper.java](backend/src/main/java/com/park/mapper/RolePermissionMapper.java)
- 新增 Service：[AuthService.java](backend/src/main/java/com/park/service/AuthService.java) + [AuthServiceImpl.java](backend/src/main/java/com/park/service/impl/AuthServiceImpl.java)
  - `getUserPermissionCodes(userId)` — 获取用户所有权限编码
  - `getUserRoles(userId)` — 获取用户所有角色
  - `assignRolePermissions(roleId, permIds)` — 角色分配权限
  - `assignUserRole(userId, roleId)` — 用户分配角色
  - `hasPermission(userId, permCode)` — 权限校验
- 新增注解：[RequirePermission.java](backend/src/main/java/com/park/annotation/RequirePermission.java)（`@RequirePermission("code")`）
- 新增 AOP：[PermissionAspect.java](backend/src/main/java/com/park/security/PermissionAspect.java)（拦截 @RequirePermission，从 SecurityContext 获取用户并校验权限，无权限返回 403）
- 新增 Controller：[PermissionController.java](backend/src/main/java/com/park/controller/PermissionController.java)
  - `GET/POST/PUT/DELETE /api/admin/perm/roles` — 角色 CRUD
  - `GET/PUT /api/admin/perm/roles/{id}/permissions` — 角色权限查询/分配
  - `GET /api/admin/perm/permissions` — 权限树列表
  - `GET/POST/DELETE /api/admin/perm/users/{userId}/roles` — 用户角色分配
- 修改 [AuthController.java](backend/src/main/java/com/park/controller/AuthController.java)：新增 `GET /api/auth/me`（返回 user + roles + permissions）
- 修改 [SecurityConfig.java](backend/src/main/java/com/park/config/SecurityConfig.java)：`/api/auth/me` 改为需认证访问
- 修改 [pom.xml](backend/pom.xml)：新增 `spring-boot-starter-aop` 依赖
- 添加 @RequirePermission 注解至以下控制器：
  - [AdminUserController.java](backend/src/main/java/com/park/controller/AdminUserController.java)（user:view/add/edit/delete/status）
  - [ResidencyApplicationController.java](backend/src/main/java/com/park/controller/ResidencyApplicationController.java)（residency:view/approve/edit）
  - [FloatingMenuController.java](backend/src/main/java/com/park/controller/FloatingMenuController.java)（floating:view/add/edit/delete）

### 前端
- 新增 [src/utils/auth.js](frontend/src/utils/auth.js)：useAuth() 组合式函数（loadAuth/clearAuth/hasPermission/hasRole/state）
- 新增 [src/directives/permission.js](frontend/src/directives/permission.js)：`v-permission` 指令（无权限隐藏元素）
- 修改 [src/main.js](frontend/src/main.js)：注册 v-permission 全局指令
- 修改 [MainLayout.vue](frontend/src/layout/MainLayout.vue)：
  - 登录后调用 /api/auth/me 加载权限
  - 内容管理子菜单按权限动态显示
  - 无任何后台权限的用户不显示"内容管理"菜单
  - 显示当前登录用户名
- 新增页面：[RoleManage.vue](frontend/src/views/RoleManage.vue)（角色管理：CRUD + 权限树分配）
- 新增路由：`/admin/roles`

---

## 模块八：企业服务大厅悬浮菜单

### 需求
- 企业服务大厅页面右侧固定悬浮菜单
- 鼠标移入按钮时左侧弹出对应图片
- 弹出图片后台可配置

### 数据库
- 创建 `floating_menu_item` 表（id, title, image_url, sort_order, status, create_time, update_time, is_deleted）
- 插入 4 条示例数据
- 文件：[db/floating_menu.sql](backend/src/main/resources/db/floating_menu.sql)

### 后端
- 新增 Entity：[FloatingMenuItem.java](backend/src/main/java/com/park/entity/FloatingMenuItem.java)
- 新增 Mapper：[FloatingMenuItemMapper.java](backend/src/main/java/com/park/mapper/FloatingMenuItemMapper.java)
- 新增 Service：[FloatingMenuService.java](backend/src/main/java/com/park/service/FloatingMenuService.java) + [FloatingMenuServiceImpl.java](backend/src/main/java/com/park/service/impl/FloatingMenuServiceImpl.java)
- 新增 Controller：[FloatingMenuController.java](backend/src/main/java/com/park/controller/FloatingMenuController.java)
  - `GET /api/floating-menu/items` — 公开接口，获取启用的菜单项（按 sort_order 排序）
  - `GET /api/floating-menu/admin/items` — 管理端列表
  - `GET /api/floating-menu/admin/items/{id}` — 管理端详情
  - `POST /api/floating-menu/admin/items` — 新增菜单项
  - `PUT /api/floating-menu/admin/items/{id}` — 编辑菜单项
  - `DELETE /api/floating-menu/admin/items/{id}` — 删除菜单项
- 修改 [SecurityConfig.java](backend/src/main/java/com/park/config/SecurityConfig.java)：添加 `/api/floating-menu/items` 白名单

### 前端
- 修改 [src/api/service.js](frontend/src/api/service.js)：新增 `getFloatingMenuItems()` 方法
- 新增组件：[FloatingMenu.vue](frontend/src/components/FloatingMenu.vue)（右侧固定悬浮，竖向按钮，hover 左侧弹出图片，768px 响应式缩放）
- 修改 [ServiceList.vue](frontend/src/views/ServiceList.vue)：集成 FloatingMenu 组件

### 后台配置方式
1. 通过 Admin API 管理菜单项（`/api/floating-menu/admin/items`）
2. 每个菜单项可配置：标题（title）、图片URL（image_url）、排序（sort_order）、启用状态（status）
3. 图片URL 可使用上传接口 `/api/admin/upload/image` 上传后获得的路径

### 后续更新（2026-05-19）
- 新增 PATCH 端点 `PATCH /api/floating-menu/admin/items/{id}/status` — 切换启用状态
- 修改 [FloatingMenu.vue](frontend/src/components/FloatingMenu.vue)：优化样式，竖向文字+图标布局，弹出图片带三角箭头，淡入过渡动画
- 新增管理页面：[FloatingMenuManage.vue](frontend/src/views/FloatingMenuManage.vue)（表格+图片预览+新增/编辑/删除+状态开关+上传集成）
- 新增管理 API：[src/api/admin.js](frontend/src/api/admin.js) 新增 `updateFloatingMenuItemStatus`
- 新增路由：`/admin/floating-menu` → FloatingMenuManage.vue
- 修改 [MainLayout.vue](frontend/src/layout/MainLayout.vue)：内容管理子菜单新增"悬浮菜单"

### 样式与图标更新（2026-05-19）
- 数据库 `floating_menu_item` 表新增 `icon` 字段（VARCHAR 100），Element Plus 图标名
- 修改 [FloatingMenuItem.java](backend/src/main/java/com/park/entity/FloatingMenuItem.java)：新增 `icon` 属性
- 修改 [floating_menu.sql](backend/src/main/resources/db/floating_menu.sql)：DDL 和示例数据新增 icon 字段
- 修改 [FloatingMenu.vue](frontend/src/components/FloatingMenu.vue)：
  - 未选中：白色背景 + #151515 文字/图标
  - 选中(hover)：#2568f1 背景 + #fff 文字/图标，向左突出 6px 并放大
  - 弹窗图片最大宽高限制 140px
  - 圆角边框 + 阴影，平滑过渡动画
- 修改 [FloatingMenuManage.vue](frontend/src/views/FloatingMenuManage.vue)：表格增加图标列，表单增加图标输入

### 图标改为图片上传（2026-05-19）
- 修改 [FloatingMenu.vue](frontend/src/components/FloatingMenu.vue)：icon 改为 `<img>` 渲染图片/SVG，有 icon 显示图片否则显示默认图标
- 修改 [FloatingMenuManage.vue](frontend/src/views/FloatingMenuManage.vue)：图标列表列显示缩略图预览，表单改用 UploadBtn 上传

---

## 模块九：入驻申请与审批

### 数据库
- 创建 `residency_application` 表（id, user_id, contact_name, contact_phone, area, industry_type, expected_entry_date, additional_info, status, reviewed_by, review_comment, create_time, update_time, is_deleted）
- 文件：[db/residency_application.sql](backend/src/main/resources/db/residency_application.sql)

### 后端
- 新增 Entity：[ResidencyApplication.java](backend/src/main/java/com/park/entity/ResidencyApplication.java)
- 新增 Mapper：[ResidencyApplicationMapper.java](backend/src/main/java/com/park/mapper/ResidencyApplicationMapper.java)
- 新增 Service：[ResidencyApplicationService.java](backend/src/main/java/com/park/service/ResidencyApplicationService.java) + [ResidencyApplicationServiceImpl.java](backend/src/main/java/com/park/service/impl/ResidencyApplicationServiceImpl.java)
- 新增 Controller：[ResidencyApplicationController.java](backend/src/main/java/com/park/controller/ResidencyApplicationController.java)
  - `POST /api/residency/applications` — 用户提交入驻申请（需认证）
  - `GET /api/residency/applications/my` — 用户查看自己的申请
  - `GET /api/admin/residency/applications` — 管理员分页列表（支持 status 筛选）
  - `GET /api/admin/residency/applications/{id}` — 管理员查看详情
  - `PUT /api/admin/residency/applications/{id}` — 管理员编辑
  - `PATCH /api/admin/residency/applications/{id}/approve` — 审批（通过/拒绝，含审批意见）

### 前端
- 新增 API 层：[src/api/residency.js](frontend/src/api/residency.js)
- 新增页面：[ResidencyApply.vue](frontend/src/views/ResidencyApply.vue)（入驻申请表：联系人、电话、面积、行业、预计时间、补充信息，手机号校验，提交成功跳转首页）
- 新增管理页：[ResidencyManage.vue](frontend/src/views/ResidencyManage.vue)（状态筛选+表格+详情弹窗+通过/拒绝审批+审批意见输入）
- 修改 [Home.vue](frontend/src/views/Home.vue)：首页新增"在线入驻申请"快捷入口卡片
- 新增路由：`/apply/residency`、`/admin/residency`
- 修改 [MainLayout.vue](frontend/src/layout/MainLayout.vue)：内容管理子菜单新增"入驻审批"

---

## 模块十一：企业认证

### 数据库
- 创建 `enterprise_auth` 表（id, user_id, company_name, credit_code, license_url, legal_person_id_url, legal_face_verified, auth_status, review_comment, reviewed_by, auth_submitted_at, auth_reviewed_at, create_time, update_time, is_deleted）
- 唯一索引：`uk_enterprise_auth_user`（user_id）、`uk_enterprise_auth_credit_code`（credit_code）
- 普通索引：`idx_enterprise_auth_status`（auth_status）
- 文件：[db/enterprise_auth.sql](backend/src/main/resources/db/enterprise_auth.sql)
- 新增 RBAC 权限：`admin:enterprise`（菜单）、`enterprise:view`（查看）、`enterprise:approve`（审批）
- 超级管理员和管理员角色分配企业认证权限

### 后端
- 新增 Entity：[EnterpriseAuth.java](backend/src/main/java/com/park/entity/EnterpriseAuth.java)
- 新增 Mapper：[EnterpriseAuthMapper.java](backend/src/main/java/com/park/mapper/EnterpriseAuthMapper.java)
- 新增 Service：[EnterpriseAuthService.java](backend/src/main/java/com/park/service/EnterpriseAuthService.java) + [EnterpriseAuthServiceImpl.java](backend/src/main/java/com/park/service/impl/EnterpriseAuthServiceImpl.java)
- 新增 Controller：[EnterpriseAuthController.java](backend/src/main/java/com/park/controller/EnterpriseAuthController.java)
  - `POST /api/enterprise/submit` — 提交企业认证（需认证）
  - `GET /api/enterprise/status` — 查询当前用户认证状态（需认证）
  - `GET /api/admin/enterprise` — 管理员分页列表（支持 status 筛选，需 enterprise:view 权限）
  - `GET /api/admin/enterprise/{id}` — 管理员查看详情（需 enterprise:view 权限）
  - `PATCH /api/admin/enterprise/{id}/review` — 审核认证（需 enterprise:approve 权限）
- 修改 [rbac.sql](backend/src/main/resources/db/rbac.sql)：新增企业认证权限数据

### 前端
- 新增 API 层：[src/api/enterprise.js](frontend/src/api/enterprise.js)
- 新增页面：[EnterpriseAuth.vue](frontend/src/views/EnterpriseAuth.vue)（四步表单：基本信息→资质上传→法人验证→确认提交，实时显示认证状态）
  - 基本信息校验：企业名称必填、信用代码18位格式校验
  - 资质上传：营业执照扫描件（复用 UploadBtn 组件）
  - 法人验证：上传法人身份证 + 人脸识别模拟验证
  - 状态展示：审核中/已通过/已驳回（含驳回原因和重新提交）
- 新增管理页：[EnterpriseAuthManage.vue](frontend/src/views/EnterpriseAuthManage.vue)（状态筛选+表格+图片预览+详情弹窗+通过/驳回审批）
- 新增路由：`/enterprise-auth`、`/admin/enterprise`
- 修改 [MainLayout.vue](frontend/src/layout/MainLayout.vue)：导航栏新增"企业认证"，内容管理新增"企业认证审核"

---

## 模块十二：系统消息通知（3.7）

> 日期：2026-05-20

### 数据库
- [notification.sql](backend/src/main/resources/db/notification.sql)：新建 `notification` 表（id, user_id, type, title, content, is_read, sender_id, create_time, update_time, is_deleted），新增权限 `notification:view/add/delete`

### 后端
- 新增实体：[Notification.java](backend/src/main/java/com/park/entity/Notification.java)
- 新增 Mapper：[NotificationMapper.java](backend/src/main/java/com/park/mapper/NotificationMapper.java)
- 新增服务接口：[NotificationService.java](backend/src/main/java/com/park/service/NotificationService.java)
- 新增服务实现：[NotificationServiceImpl.java](backend/src/main/java/com/park/service/impl/NotificationServiceImpl.java)
  - `sendToUser` 给指定用户发通知，`sendToAll` 全员广播（查询所有活跃用户逐个插入）
  - `markRead` 标记单条已读，`markAllRead` 全部已读
  - `getUnreadCount` 未读计数，`listByUser` 分页列表
- 新增控制器：[NotificationController.java](backend/src/main/java/com/park/controller/NotificationController.java)
  - 前台接口：`GET /api/notifications`（我的消息列表）、`GET /api/notifications/unread-count`（未读数）、`PUT /api/notifications/read/{id}`（标记已读）、`PUT /api/notifications/read-all`（全部已读）
  - 管理接口：`POST /api/admin/notifications`（发布公告，`@RequirePermission("notification:add")`）、`GET /api/admin/notifications`（管理列表）、`DELETE /api/admin/notifications/{id}`（删除）
- 修改 [EnterpriseAuthController.java](backend/src/main/java/com/park/controller/EnterpriseAuthController.java)：审核通过/驳回后调用 `notificationService.sendToUser` 通知申请人
- 修改 [ResidencyApplicationController.java](backend/src/main/java/com/park/controller/ResidencyApplicationController.java)：审批后发送业务通知

### 前端
- 新增 API 层：[notification.js](frontend/src/api/notification.js)
- 新增组件：[NotificationBell.vue](frontend/src/components/NotificationBell.vue)（铃铛图标 + 未读角标 + 弹窗消息列表，30秒轮询未读数，Tab 切换业务通知/系统公告，点击自动标记已读）
- 新增管理页：[NotificationManage.vue](frontend/src/views/NotificationManage.vue)（发布系统公告/业务通知，支持全员发送或指定用户，搜索筛选，删除）
- 新增路由：`/admin/notifications`
- 修改 [MainLayout.vue](frontend/src/layout/MainLayout.vue)：导航栏右侧插入 NotificationBell 组件，内容管理新增"消息管理"菜单项
