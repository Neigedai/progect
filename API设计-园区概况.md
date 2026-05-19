# 园区概况展示 API 接口定义

## 1. 获取园区列表

| 项目 | 内容 |
|------|------|
| **路径** | `/api/parks` |
| **Method** | `GET` |
| **鉴权** | 否（公开） |

### 请求参数
无

### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "parkName": "数字科技园",
      "logo": "/static/images/parks/logo-digital-tech.png",
      "isDefault": 1,
      "address": "某市高新区创新大道 168 号",
      "description": "数字科技园是国家级高新技术产业开发区...",
      "planImage": "/static/images/parks/plan-digital-tech.png",
      "planDescription": "园区总规划面积 500 亩...",
      "transportInfo": "地铁 2 号线直达...",
      "createTime": "2026-05-18T10:00:00",
      "updateTime": "2026-05-18T10:00:00",
      "deleted": 0
    }
  ]
}
```

---

## 2. 获取园区概况

| 项目 | 内容 |
|------|------|
| **路径** | `/api/parks/{id}/overview` |
| **Method** | `GET` |
| **鉴权** | 否（公开） |

### 路径参数
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `id` | Long | 是 | 园区 ID |

### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "parkInfo": {
      "id": 1,
      "parkName": "数字科技园",
      "logo": "/static/images/parks/logo-digital-tech.png",
      "isDefault": 1,
      "address": "某市高新区创新大道 168 号",
      "description": "数字科技园是国家级高新技术产业开发区...",
      "planImage": "/static/images/parks/plan-digital-tech.png",
      "planDescription": "园区总规划面积 500 亩...",
      "transportInfo": "地铁 2 号线直达..."
    },
    "facilities": [
      {
        "id": 1,
        "parkId": 1,
        "facilityName": "智慧食堂",
        "icon": "/static/images/icons/canteen.png",
        "description": "支持扫码点餐、人脸支付",
        "image": "/static/images/facilities/canteen-1.png",
        "sortOrder": 1
      }
    ],
    "honors": [
      {
        "id": 1,
        "parkId": 1,
        "honorName": "国家级高新技术产业开发区",
        "image": "/static/images/honors/national-tech.png",
        "awardYear": "2023",
        "sortOrder": 1
      }
    ]
  }
}
```

### 错误响应
| code | 说明 |
|------|------|
| 404 | 园区不存在 `{"code":404,"message":"园区不存在","data":null}` |
| 500 | 服务器内部错误 |

---

## 6. 联调步骤

1. 执行 `park_tables.sql` 初始化表结构和测试数据
2. 启动后端 `mvn spring-boot:run`（确认 profile=dev）
3. 验证 API：`curl http://localhost:8080/api/parks` / `curl http://localhost:8080/api/parks/1/overview`
4. 启动前端 `npm run dev`，访问 `http://localhost:5173/park-overview`
5. 验证园区切换：页面加载默认园区 → 下拉切换 → URL 参数同步更新 → 设施/荣誉数据刷新

## 7. 后续扩展建议

| 扩展点 | 当前状态 | 改造方式 |
|--------|----------|----------|
| **地图 SDK 接入** | `map-placeholder` 区域占位 | 替换为高德/百度地图组件，parkInfo 中增加经纬度字段 |
| **图片上传 MinIO** | 使用静态路径 `/static/images/...` | 抽象图片路径生成逻辑为 `FileService`，前端调用上传接口返回 MinIO URL |
| **荣誉轮播动画** | Element Plus 默认 card 模式 | 可替换为 Swiper.js 增强动画效果 |
| **缓存优化** | 每次请求实时查询 | 添加 `@Cacheable` 注解，参考需求文档配置 Redis 缓存 |
