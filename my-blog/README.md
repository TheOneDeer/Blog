# 个人博客系统 - 后端服务

## 项目简介

这是一个基于Spring Boot开发的个人文档管理系统，支持用户注册登录、文档上传管理、分类管理和搜索功能。

## 技术栈

- **后端框架**：Spring Boot 3.5.7
- **数据库**：MySQL 8.0
- **持久化**：MyBatis 3.0.5
- **缓存**：Redis 7 (已配置，暂未使用)
- **认证**：JWT
- **密码加密**：BCrypt
- **Java版本**：18

## 快速开始

### 1. 环境要求

- JDK 18+
- Maven 3.6+
- MySQL 8.0
- Redis 7.0 (可选)

### 2. 数据库配置

数据库名称：`blog`

数据库表已创建完成，包含：
- `user` - 用户表
- `document` - 文档表
- `category` - 分类表
- `tag` - 标签表
- `document_tag` - 文档标签关联表
- `access_log` - 访问日志表

### 3. 修改配置

编辑 `src/main/resources/application.properties` 文件，确保以下配置正确：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/blog?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8
spring.datasource.username=root
spring.datasource.password=root

# Redis配置（可选）
spring.data.redis.host=localhost
spring.data.redis.port=6379

# 文件上传配置
file.upload.dir=D:/upload
```

### 4. 启动服务

#### 方式一：IntelliJ IDEA

1. 右键 `MyBlogApplication.java`
2. 选择 `Run 'MyBlogApplication'`

#### 方式二：Maven命令

```bash
mvn spring-boot:run
```

#### 方式三：JAR包运行

```bash
mvn clean package
java -jar target/my-blog-0.0.1-SNAPSHOT.jar
```

### 5. 验证服务

服务启动后访问：http://localhost:8080

## API文档

### 用户管理

- **POST** `/api/v1/users/register` - 用户注册
- **POST** `/api/v1/users/login` - 用户登录
- **GET** `/api/v1/users/info` - 获取当前用户信息

### 文档管理

- **POST** `/api/v1/documents/upload` - 上传文档
- **GET** `/api/v1/documents` - 获取文档列表（支持分页）
- **GET** `/api/v1/documents/{id}` - 获取文档详情
- **PUT** `/api/v1/documents/{id}` - 更新文档信息
- **DELETE** `/api/v1/documents/{id}` - 删除文档

### 分类管理

- **POST** `/api/v1/categories` - 创建分类
- **GET** `/api/v1/categories` - 获取分类列表
- **PUT** `/api/v1/categories/{id}` - 更新分类
- **DELETE** `/api/v1/categories/{id}` - 删除分类

### 搜索功能

- **GET** `/api/v1/search?keyword=xxx&page=1&pageSize=10` - 搜索文档

## 使用示例

### 1. 用户注册

```bash
curl -X POST http://localhost:8080/api/v1/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com"
  }'
```

### 2. 用户登录

```bash
curl -X POST http://localhost:8080/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### 3. 上传文档

```bash
curl -X POST http://localhost:8080/api/v1/documents/upload \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@test.pdf" \
  -F "title=测试文档" \
  -F "description=这是测试文档"
```

## 常见问题

### 端口被占用

如果遇到 "Port 8080 was already in use" 错误：

```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# 或修改配置文件中的端口
server.port=8081
```

### 数据库连接失败

1. 确保MySQL服务已启动
2. 检查用户名密码是否正确
3. 确认数据库 `blog` 已创建

### 文件上传失败

1. 确保上传目录存在（默认：`D:/upload`）
2. 检查文件大小是否超过100MB
3. 确认文件类型符合要求（PDF, DOCX, DOC, TXT）

## 项目结构

```
src/
├── main/
│   ├── java/com/theonedeer/myblog/
│   │   ├── controller/    # 控制器层
│   │   ├── service/       # 服务层
│   │   ├── mapper/        # MyBatis Mapper接口
│   │   ├── entity/        # 实体类
│   │   ├── dto/           # 数据传输对象
│   │   ├── common/        # 公共类
│   │   ├── exception/     # 异常处理
│   │   ├── util/         # 工具类
│   │   └── config/        # 配置类
│   └── resources/
│       ├── mapper/        # MyBatis XML映射文件
│       └── application.properties
└── test/
    └── java/
        └── com/theonedeer/myblog/
```

## 开发说明

- **统一响应格式**：所有API返回格式为 `Result<T>`
- **JWT认证**：需要认证的接口在Header中添加 `Authorization: Bearer TOKEN`
- **权限控制**：用户只能操作自己的文档和分类
- **文件类型限制**：仅支持PDF、DOCX、DOC、TXT格式
- **分页查询**：列表查询支持 `page` 和 `pageSize` 参数

## License

MIT
