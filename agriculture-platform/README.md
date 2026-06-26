# 智慧农业综合服务平台

基于 Spring Boot 3.2、Vue 3、MyBatis-Plus 和 Element Plus 的全栈农业信息化管理系统，覆盖农场管理、产品溯源、农技知识推广、天气预测、产量预测、YOLO 病虫害识别、AI 智能问答等场景。

## 技术栈

| 层级 | 技术 |
| --- | --- |
| 后端 | Java 17, Spring Boot 3.2.5, Spring Security, JWT |
| ORM | MyBatis-Plus 3.5.6 |
| 数据库 | MySQL 8.0 |
| 前端 | Vue 3, Vite 5, Pinia |
| UI/图表 | Element Plus 2.7.3, ECharts 5.5 |
| 文档 | SpringDoc OpenAPI |
| 病虫害识别 | Roboflow API (YOLOv8) |
| AI 诊断 | Agent 微服务 (Python FastAPI) |

## 项目结构

```text
agriculture-platform/
├─ backend/                 # Spring Boot 后端
│  ├─ src/main/java/com/agriculture/
│  │  ├─ common/            # 通用配置、认证、响应、异常处理
│  │  ├─ system/            # 用户、角色、菜单
│  │  ├─ farm/              # 农场、地块、作物、设备、环境数据
│  │  ├─ trace/             # 产品、批次、全链路溯源记录
│  │  ├─ knowledge/         # 文章、问答、讲座、视频、病虫害、RAG、知识图谱
│  │  ├─ recommend/         # 个性化推荐
│  │  ├─ yolo/              # YOLO 病虫害检测 (Roboflow API)
│  │  ├─ agent/             # AI 诊断服务
│  │  └─ dashboard/         # 首页统计
│  └─ pom.xml
├─ frontend/                # Vue 3 前端
│  ├─ src/api/              # Axios API 封装
│  ├─ src/router/           # 前端路由
│  ├─ src/store/            # Pinia 状态
│  └─ src/views/            # 页面视图
├─ sql/
│  └─ agriculture_platform_init.sql
└─ videos/                  # 本地农技视频资源
```

## 核心功能

### 系统管理
- 用户、角色、菜单、JWT 登录认证和路由权限控制。
- 6 种角色：管理员、农场管理员、溯源管理员、农业专家、审核员、普通农户。

### 农场管理
- 农场企业、地块、作物字典、设备、环境数据监测。
- 天气预测：天行 API 实时天气 + ECharts 气温趋势 + 土壤墒情分析。
- 产量预测：基于环境得分 × 农事得分 × 设备得分 × 基准亩产的多因子预测。

### 溯源管理
- 产品、批次、生产、加工、仓储、质检、物流、销售记录全链路追溯。

### 技术推广
- 农技文章：发布、审核、点赞、收藏、评论。
- 问答社区：提问、回答、点赞、收藏、采纳最佳回答。
- 农技讲座：发布、报名管理。
- 农技视频：上传、播放、点赞、收藏，支持封面图。
- 病虫害百科：知识库查阅。
- 知识图谱：作物-病虫害-农药-环境关联可视化 (ECharts 力导向图)。
- RAG 知识检索：文章索引、中文关键词抽取、片段级检索、答案生成。

### YOLO 病虫害识别
- 上传植物图片 → Roboflow YOLOv8 模型自动识别病虫害。
- 后端直连 Roboflow API 或通过 Python 微服务中继。
- 接口：`POST /api/yolo/detect`（需登录）。

### AI 智能问答
- 智能提问页面：上传图片 → YOLO 识别 → Agent AI 诊断 → 自动回复或转专家。
- Agent 评分 ≥ 90 自动回复，否则转人工专家审核。
- 接口：`POST /api/question/submit`（公开访问）。

## 面向人群

| 角色 | 标识 | 主要功能 |
| --- | --- | --- |
| 平台管理员 | ADMIN | 全部权限：用户/角色/菜单管理、内容审核 |
| 农场管理员 | FARM_ADMIN | 农场、地块、设备管理 |
| 溯源管理员 | TRACE_ADMIN | 产品、批次、全链路溯源记录管理 |
| 农业专家 | EXPERT | 发布文章、讲座、审核问答 |
| 审核员 | AUDITOR | 审核文章发布和内容合规 |
| 普通农户 | FARMER | 浏览内容、提问求助、报名讲座 |

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Node.js 18+ / npm 9+

### 1. 初始化数据库

```bash
mysql -u root -p < sql/agriculture_platform_init.sql
```

默认数据库连接在 `backend/src/main/resources/application.yml` 中配置。

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端默认地址：

- API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认地址：`http://localhost:5173`

### 4. 启动 YOLO 服务（可选）

```bash
cd kuangjia/yolo-service
set ROBOFLOW_API_KEY=你的Roboflow Key
python yolo_service.py
```

YOLO 服务地址：`http://localhost:8001`

### 5. 默认账号

| 用户名 | 密码 | 角色 |
| --- | --- | --- |
| admin | 123456 | 超级管理员 |
| farm_admin | 123456 | 农场管理员 |
| trace_admin | 123456 | 溯源企业用户 |
| expert | 123456 | 农技专家 |
| farmer | 123456 | 普通农户 |
| auditor | 123456 | 内容审核员 |

## 产量预测公式

```
预测亩产(kg/亩) = 基准亩产 × 环境得分 × 农事得分 × 设备得分
```

| 参数 | 含义 |
| --- | --- |
| 基准亩产 | 该作物历史平均亩产 |
| 环境得分 (0~1) | 温度、降水、光照、土壤墒情综合评估 |
| 农事得分 (0~1) | 施肥、灌溉、病虫害防治执行质量 |
| 设备得分 (0~1) | 传感器、灌溉设备在线率与数据质量 |

## API 列表

### YOLO 病虫害检测
| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/api/yolo/detect` | 上传图片进行病虫害识别（需登录） |

### 智能问答
| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/api/question/submit` | 智能提问（图片+描述，自动YOLO+AI诊断） |
| `GET` | `/api/question/list` | 问题列表 |
| `GET` | `/api/question/{id}` | 问题详情 |

### RAG 知识检索
| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/api/knowledge/rag/ask?query=...&topK=8` | RAG 答案、引用来源和检索片段 |
| `GET` | `/api/knowledge/rag/search` | 文档级检索 |
| `GET` | `/api/knowledge/rag/search/chunks` | 片段级检索 |
| `POST` | `/api/knowledge/rag/index/{articleId}` | 索引单篇文章 |
| `POST` | `/api/knowledge/rag/index/all` | 重建全部已发布文章索引 |

### 农技视频
| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/api/knowledge/videos/upload` | 上传视频（multipart/form-data） |
| `DELETE` | `/api/knowledge/videos/{id}` | 删除视频 |
| `GET` | `/api/knowledge/videos` | 视频列表 |
| `GET` | `/api/knowledge/videos/{id}` | 视频详情 |

## 部署建议

1. 修改数据库密码、JWT 密钥和 Roboflow API Key。
2. 执行 `npm run build`，将 `frontend/dist` 部署到 Nginx。
3. 后端使用 `mvn clean package -DskipTests` 构建 jar。
4. Nginx 反向代理 `/api`、`/videos` 和前端静态资源。
5. 生产环境建议启用 HTTPS，并将上传目录放到可持久化磁盘。
