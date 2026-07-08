# MindRealm v1.0 - 青少年心理数字孪生系统

<p align="center">
  <img src="mind-vite-admin/public/favicon.svg" alt="MindRealm Logo" width="120">
</p>

<p align="center">
  基于多模态情感计算的青少年心理健康服务平台
</p>

<p align="center">
  <img src="https://img.shields.io/badge/版本-v1.0.0-blue?style=for-the-badge" alt="Version">
  <a href="https://github.com/xiaoheizi8/mind-heart/stargazers">
    <img src="https://img.shields.io/github/stars/xiaoheizi8/mind-heart?style=for-the-badge" alt="Stars">
  </a>
  <a href="https://github.com/xiaoheizi8/mind-heart/network/members">
    <img src="https://img.shields.io/github/forks/xiaoheizi8/mind-heart?style=for-the-badge" alt="Forks">
  </a>
  <a href="https://github.com/xiaoheizi8/mind-heart/issues">
    <img src="https://img.shields.io/github/issues/xiaoheizi8/mind-heart?style=for-the-badge" alt="Issues">
  </a>
  <a href="https://github.com/xiaoheizi8/mind-heart/blob/main/LICENSE">
    <img src="https://img.shields.io/github/license/xiaoheizi8/mind-heart?style=for-the-badge" alt="License">
  </a>
  <a href="https://spring.io/projects/spring-boot">
    <img src="https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen?style=for-the-badge" alt="Spring Boot">
  </a>
  <a href="https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html">
    <img src="https://img.shields.io/badge/JDK-17-orange?style=for-the-badge" alt="JDK">
  </a>
</p>

<p align="center">
  <a href="#项目简介">项目简介</a> •
  <a href="#技术架构">技术架构</a> •
  <a href="#快速开始">快速开始</a> •
  <a href="#api-接口">API 文档</a> •
  <a href="#开源贡献">开源贡献</a> •
  <a href="#license">License</a>
</p>

<p align="center">
  🎉 <b>v1.0 正式迭代完毕！</b> 本项目已开源，欢迎 Star、Fork 和贡献代码！
</p>

---

## 项目简介

MindRealm（心域）是一个面向青少年的心理健康数字孪生系统，通过日记记录、情感分析、AI对话等方式，帮助青少年了解和管理自己的情绪状态，同时为心理咨询师提供风险预警和干预支持。

### 🌟 项目背景

随着青少年心理健康问题日益受到关注，传统的心理咨询方式面临着资源不足、响应不及时等问题。MindRealm 旨在通过 AI 技术和数字化手段，为青少年提供 7x24 小时的心理陪伴和支持。

### ✨ 技术亮点

- 🔐 **数据安全**: AES 加密存储日记，保护用户隐私
-  **智能情感分析**: 关键词 + AI 双策略，5 维情绪分类
- 💬 **AI 流式对话**: SSE 实时流式输出，3 种人格模式切换
- 🔍 **RAG 知识库**: 基于 Elasticsearch 的向量检索，心理健康知识问答
- 🚨 **风险预警**: 三级预警机制，自动识别危机信号
- 🎨 **RBAC 权限**: 完善的角色权限管理，支持管理员/咨询师/用户
- 📱 **跨平台**: 支持 H5、小程序、App 多端运行

### 核心功能

| 功能模块 | 描述 |
|----------|------|
| 📔 日记管理 | 记录心情日记，AES加密存储，自动情感分析 |
| 🎭 情感分析 | 关键词+AI双策略情感识别，5维情绪分类 |
| 🤖 AI对话 | 智能心理陪伴，支持SSE流式输出、人格切换 |
| 📚 知识库 | 心理健康知识库，支持RAG检索与问答 |
| 🚨 风险预警 | 自杀/自伤关键词识别，三级风险预警 |
| 👥 管理后台 | RBAC角色权限、用户管理、预警处理、系统监控 |

---

## 技术架构

### 后端技术栈

| 技术 | 版本     | 说明 |
|------|--------|------|
| Java | 17     | 运行环境 |
| Spring Boot | 3.4.5  | 核心框架 |
| Spring Security | 6.x | JWT认证与RBAC权限 |
| MyBatis-Plus | 3.5.15 | ORM框架 |
| MySQL | 8.0.33 | 数据库 |
| Redis | 7.x    | 缓存与会话 |
| Elasticsearch | 8.14.2 | 向量存储与混合搜索（RAG） |
| LangChain4j | 1.10.0 | AI对话集成 |
| Spring AI Alibaba | 1.0.3 | 阿里云百炼RAG |
| 阿里云OSS | 3.17.2 | 文件存储 |

### 架构优化

**Service层接口分离**：所有Service类采用标准的"接口+实现"模式，提高代码可维护性和可测试性。

- 12个Service接口（I*Service.java）
- 12个Service实现类（*ServiceImpl.java）
- 统一构造函数注入
- 完整参数校验与事务管理

### 前端技术栈

| 平台 | 技术 | 说明 |
|------|------|------|
| 移动端 | uni-app + Vue 3 | 跨平台移动应用 |
| 管理端 | React + Vite + Ant Design | 后台管理系统 |

---

## 项目结构

```
mind-realm/
├── mind-realm-api/          # 主应用入口模块
│   ├── config/              # 安全配置、JWT过滤器
│   └── controller/          # REST API控制器
│       ├── admin/           # 管理端接口（RBAC鉴权）
│       └── ...              # 用户端接口
├── mind-realm-common/       # 公共模块
│   ├── entity/              # 实体类
│   ├── mapper/              # Mapper接口
│   ├── service/             # 业务服务接口与实现
│   ├── util/                # 工具类（AesUtil、JwtUtils等）
│   └── dto/                 # 数据传输对象
├── mind-realm-core/         # AI核心模块
│   └── service/             # AiChatService、RagService
├── mind-realm-diary/        # 日记模块（AES加密、情感分析）
├── mind-realm-emotion/      # 情感分析模块（关键词+AI策略）
├── mind-realm-warning/      # 预警模块（行为异常检测）
├── mind-ui-uniapp/          # 移动端前端（uni-app + Vue3）
├── mind-vite-admin/         # 管理端前端（React + Vite + Ant Design）
└── mind-realm-database/     # 数据库脚本
```

---

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 7.x
- Node.js 18+

---

## 快速开始

### 后端启动

```bash
# 1. 创建数据库
mysql -u root -p < mind-realm-database/sql/init.sql

# 2. 修改配置
vim mind-realm-api/src/main/resources/application-dev.yml

# 3. 编译打包
mvn clean package -DskipTests

# 4. 启动服务
java -jar mind-realm-api/target/mind-realm-api-1.0.0-SNAPSHOT.jar
```

### 前端启动

```bash
# 移动端
cd mind-ui-uniapp
npm install
npm run dev:h5

# 管理端
cd mind-vite-admin
npm install
npm run dev
```

### Windows 中间件部署教程

#### 1. MySQL 8.0 安装与配置

**下载与安装**:

1. 访问 [MySQL 官网](https://dev.mysql.com/downloads/mysql/) 下载 Windows 版本
2. 选择 `mysql-8.0.xx-winx64.zip` (推荐 ZIP 版本,无需安装)
3. 解压到 `D:\tools\mysql\mysql-8.0`

**配置 my.ini**:

```ini
[mysqld]
port=3306
basedir=D:\tools\mysql\mysql-8.0
datadir=D:\tools\mysql\mysql-8.0\data
character-set-server=utf8mb4
default-authentication-plugin=mysql_native_password

[client]
port=3306
default-character-set=utf8mb4
```

**初始化与启动**:

```cmd
# 初始化数据库
cd D:\tools\mysql\mysql-8.0\bin
mysqld --initialize-insecure --user=mysql

# 安装服务
mysqld --install MySQL80

# 启动服务
net start MySQL80

# 登录 (无密码)
mysql -u root -p

# 创建数据库
CREATE DATABASE mind_realm CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 2. Redis 7.x 安装

**Windows 版本下载**:

1. 访问 [Redis for Windows](https://github.com/tporadowski/redis/releases)
2. 下载 `Redis-x64-7.x.x.zip`
3. 解压到 `D:\tools\redis`

**启动 Redis**:

```cmd
cd D:\tools\redis

# 启动 Redis 服务
redis-server.exe redis.windows.conf

# 另开一个窗口测试
redis-cli.exe
> PING
PONG
```

**设置为 Windows 服务** (可选):

```cmd
# 安装服务
redis-server --service-install redis.windows.conf --service-name Redis7

# 启动服务
net start Redis7
```

#### 3. Elasticsearch 8.14.2 安装与分词器配置

**下载与安装**:

1. 访问 [Elasticsearch 官网](https://www.elastic.co/cn/downloads/elasticsearch)
2. 下载 Windows 版本 `elasticsearch-8.14.2-windows-x86_64.zip`
3. 解压到 `D:\tools\es\elasticsearch-8.14.2`

**配置 elasticsearch.yml**:

```yaml
# 集群名称
cluster.name: mind-realm-es
# 节点名称
node.name: node-1
# 数据存储路径
path.data: D:\tools\es\elasticsearch-8.14.2\data
# 日志路径
path.logs: D:\tools\es\elasticsearch-8.14.2\logs
# 绑定地址
network.host: 0.0.0.0
# HTTP 端口
http.port: 9200
# 跨域配置
http.cors.enabled: true
http.cors.allow-origin: "*"
# 安全配置 (开发环境可禁用)
xpack.security.enabled: false
xpack.security.enrollment.enabled: false
```

**启动 Elasticsearch**:

```cmd
cd D:\tools\es\elasticsearch-8.14.2\bin

# 启动 ES
elasticsearch.bat

# 访问测试
http://localhost:9200
```

**安装 IK 分词器** (中文分词):

```cmd
# 进入 plugins 目录
cd D:\tools\es\elasticsearch-8.14.2\plugins

# 下载 IK 分词器
curl -L -o elasticsearch-analysis-ik-8.14.2.zip ^
  https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v8.14.2/elasticsearch-analysis-ik-8.14.2.zip

# 解压 (Windows 可用 7-Zip 或 WinRAR)
# 解压到 ik 文件夹

# 重启 Elasticsearch
```

**验证分词器**:

```bash
curl -X POST "http://localhost:9200/_analyze" -H "Content-Type: application/json" -d '{
  "analyzer": "ik_max_word",
  "text": "青少年心理健康"
}'
```

预期输出:
```json
{
  "tokens": [
    {"token": "青少年"},
    {"token": "青年"},
    {"token": "心理"},
    {"token": "心理健康"},
    {"token": "健康"}
  ]
}
```

#### 4. Kibana 8.14.2 安装 (可选)

**下载与配置**:

1. 下载 [Kibana](https://www.elastic.co/cn/downloads/kibana)
2. 解压到 `D:\dev\kibana-8.14.2-windows-x86_64`
3. 配置 `kibana.yml`:

```yaml
server.port: 5601
server.host: "0.0.0.0"
elasticsearch.hosts: ["http://localhost:9200"]
i18n.locale: "zh-CN"  # 中文界面
```

**启动**:

```cmd
cd D:\dev\kibana-8.14.2-windows-x86_64\bin
kibana.bat
```

访问: http://localhost:5601

---

### Docker 部署 (推荐)

```bash
# 构建并启动
docker-compose up -d
```

### 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 后端API | http://localhost:8080 | 主服务 |
| API文档 | http://localhost:8080/swagger-ui.html | OpenAPI 3.0 |
| 移动端 | http://localhost:5173 | H5预览 |
| 管理端 | http://localhost:5174 | 管理员/咨询师登录 |

---

## API 接口

### 用户端 API (`/api/v1`)

| 模块 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 认证 | /auth/login | POST | 登录 |
| 认证 | /auth/register | POST | 注册 |
| 用户 | /user/profile | GET | 获取个人信息 |
| 用户 | /user/profile | PUT | 更新个人信息 |
| 日记 | /diary/list | GET | 日记列表 |
| 日记 | /diary/create | POST | 创建日记 |
| 日记 | /diary/{id} | GET/DELETE | 日记详情/删除 |
| 日记 | /diary/report | GET | 情绪报告 |
| 日记 | /diary/stats | GET | 日记统计 |
| 聊天 | /chat/send | POST | AI对话 |
| 聊天 | /chat/stream | GET | SSE流式对话 |
| 聊天 | /chat/persona | GET/POST | 人格设置 |
| 聊天 | /chat/history | DELETE | 清除历史 |
| 情感 | /emotion/analyze | POST | 情感分析 |
| 情感 | /emotion/profile | GET | 情感画像 |
| 预警 | /warning/status | GET | 预警状态 |
| 预警 | /warning/list | GET | 预警列表 |
| 知识 | /knowledge/list | GET | 知识库列表 |
| 知识 | /knowledge/{id} | GET | 知识详情 |
| 通知 | /notification/list | GET | 通知列表 |
| 文件 | /file/upload/image | POST | 上传图片 |
| 文件 | /file/upload/audio | POST | 上传音频 |

### 管理端 API (`/admin/v1`)

| 模块 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 认证 | /auth/login | POST | 管理员登录 |
| 统计 | /stats/overview | GET | 系统概览 |
| 统计 | /stats/users | GET | 用户统计 |
| 统计 | /stats/diaries | GET | 日记统计 |
| 统计 | /stats/warnings | GET | 预警统计 |
| 用户 | /user/list | GET | 用户列表 |
| 用户 | /user/{id} | GET | 用户详情 |
| 用户 | /user/{id}/status | PUT | 更新用户状态 |
| 预警 | /warning/list | GET | 预警列表 |
| 预警 | /warning/{id}/handle | POST | 处理预警 |
| 知识 | /knowledge/list | GET | 知识库列表 |
| 知识 | /knowledge/create | POST | 添加知识 |
| 知识 | /knowledge/{id} | PUT/DELETE | 更新/删除 |

---

## 安全措施

| 措施 | 说明 |
|------|------|
| 🔐 密码加密 | MD5单向哈希 |
| 🎫 Token认证 | JWT无状态认证，Token含角色信息 |
| 🚨 风险预警 | 关键词识别 + 行为异常检测 |
| 🔒 权限控制 | RBAC基于角色的访问控制（用户/咨询师/管理员） |
| 📦 文件安全 | 阿里云OSS私有存储 |
| 🛡️ 日记加密 | AES对称加密存储，查询时自动解密 |

---

## 配置说明

### 核心配置项

> ⚠️ **安全提示**: 所有敏感配置已使用占位符，请参考 `application-dev.yml.example` 模板文件进行配置。

```yaml
# 数据库
spring.datasource.url: jdbc:mysql://localhost:3306/mind_realm

# Redis
spring.data.redis.host: localhost

# 阿里云OSS
aliyun.oss.endpoint: oss-cn-beijing.aliyuncs.com

# AI模型 (阿里云百炼)
spring-ai-alibaba.dashscope.api-key: ${DASHSCOPE_API_KEY}
spring-ai-alibaba.dashscope.chat.options.model: qwen-plus
```

### 环境变量配置

项目使用 `.yml.example` 模板文件管理配置，请复制并填写实际值:

```bash
cp mind-realm-api/src/main/resources/application-dev.yml.example \
   mind-realm-api/src/main/resources/application-dev.yml

# 然后编辑 application-dev.yml，填入你的 API Key 和密码
```

> 🔒 **安全最佳实践**: 生产环境请使用环境变量或密钥管理服务，不要将敏感信息写入配置文件。

---

## 开源贡献

欢迎为本项目贡献代码！请遵循以下流程:

### 贡献流程

1. **Fork 本仓库**
2. **创建特性分支**: `git checkout -b feature/AmazingFeature`
3. **提交更改**: `git commit -m 'Add some AmazingFeature'`
4. **推送到分支**: `git push origin feature/AmazingFeature`
5. **提交 Pull Request**

### 开发规范

- 遵循阿里巴巴 Java 开发规范
- 代码需要包含完整的注释
- 提交前确保编译通过: `mvn clean compile`
- 添加相应的单元测试
- 更新相关文档

### Git 提交规范

```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 重构代码
test: 测试用例
chore: 构建/辅助工具变动
```

### 注意事项

- ❌ 不要提交任何包含敏感信息的文件 (API Key、密码等)
- ❌ 不要提交 `application-dev.yml` 等配置文件
- ❌ 不要提交 `database/` 和 `docs/` 目录
- ✅ 使用 `.yml.example` 模板文件
- ✅ 提交前检查 `.gitignore` 是否已过滤敏感文件

---

## 默认账号

| 角色 | 用户名 | 密码 | 权限 |
|------|--------|------|------|
| 管理员 | admin | 123456 | 全部权限 |
| 咨询师 | counselor | 123456 | 预警处理 |

> ⚠️ 生产环境请务必修改默认密码

---

## 开发进度

### ✅ v1.0 已完成功能

- [x] 用户认证模块 (登录/注册/验证码/邮箱登录)
- [x] 日记管理模块 (CRUD/AES加密/情感分析/报告)
- [x] 情感分析模块 (关键词+AI双策略/5维情绪分类/情绪画像)
- [x] 预警系统模块 (关键词识别/行为异常检测/三级预警)
- [x] AI对话模块 (人格切换/RAG知识库/SSE流式输出)
- [x] 知识库模块 (心理知识/导入导出/检索问答)
- [x] 管理后台 (数据概览/用户管理/日记管理/预警处理/知识库/角色权限/系统监控)
- [x] RBAC权限体系 (JWT角色/菜单权限/接口鉴权)
- [x] 文件上传 (OSS集成)
- [x] Docker部署
- [x] 单元测试覆盖 (45+测试用例)

### 📋 后续规划

- [ ] 短信通知服务
- [ ] 语音日记与语音情感分析
- [ ] 视频微表情分析
- [ ] 认知行为引导 (CBT)
- [ ] 团体心理辅导

---

## 模块说明

### mind-realm-common
公共基础模块，包含实体类、Mapper、Service接口、工具类

### mind-realm-core
AI核心模块，包含AiChatService、AlibabaRagService、RagService、ConversationService

### mind-realm-diary
日记模块，包含DiaryEntity、DiaryService、日记情感分析

### mind-realm-emotion
情感分析模块，包含EmotionStrategy、EmotionProfileService

### mind-realm-warning
预警模块，包含WarningRecord、WarningService、NotificationService

---

## License

MIT License

---

<p align="center">
  Made with ❤️ for teenager mental health
</p>
## 作者最后想说的话
Java加油