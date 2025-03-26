# AIconomy

AIconomy 是一个金融管理应用程序，通过直观的用户界面提供财务数据分析、预算跟踪和账户管理功能。该项目使用 JavaFX
构建客户端界面，采用微服务架构，将客户端、服务器和 AI 模块分离，确保了可维护性和可扩展性。

## 项目结构

项目由三个主要模块组成：

```
.
├── README.cn.md                             # 中文文档
├── README.md                                # 英文文档
├── aiconomy-client/                         # 客户端模块
│   ├── pom.xml                              # 客户端模块 Maven 配置
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/se/aiconomy/client/
│       │   │       ├── Application/         # 应用程序入口和配置
│       │   │       ├── common/              # 公共工具和辅助类
│       │   │       │   └── utils/           # 工具类（如 CSV 处理）
│       │   │       ├── controller/          # JavaFX 控制器，处理 UI 交互
│       │   │       ├── model/               # 数据模型
│       │   │       │   └── dto/             # 数据传输对象
│       │   │       └── service/             # 客户端业务逻辑服务
│       │   └── resources/
│       │       ├── assets/                  # 静态资源（图片）
│       │       ├── fxml/                    # JavaFX 布局文件
│       │       └── log4j2.xml               # 日志配置
│       └── test/                            # 测试目录
│
├── aiconomy-langchain/                      # AI 功能模块
│   ├── pom.xml                              # LangChain 模块 Maven 配置
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/se/aiconomy/langchain/
│       │   │       ├── AIServices/          # AI 服务实现
│       │   │       │   └── classification/  # 分类服务
│       │   │       ├── chains/              # AI 处理链
│       │   │       │   └── chat/            # 聊天功能
│       │   │       ├── common/              # 公共组件
│       │   │       │   ├── chain/           # 基础链框架
│       │   │       │   ├── config/          # 配置管理
│       │   │       │   ├── model/           # 模型配置
│       │   │       │   ├── prompt/          # 提示词模板
│       │   │       │   └── utils/           # 工具类
│       │   │       └── models/              # AI 模型定义
│       │   └── resources/
│       └── test/                            # 测试目录
│           └── java/
│               └── com/se/aiconomy/langchain/
│                   ├── AIservices/          # AI 服务测试
│                   ├── chain/               # 链处理测试
│                   └── utils/               # 工具类测试
│
├── aiconomy-server/                         # 服务器模块
│   ├── pom.xml                              # 服务器模块 Maven 配置
│   └── src/
│       ├── main/
│       │   └── java/
│       │       └── com/se/aiconomy/server/
│       │           ├── common/              # 公共组件
│       │           │   ├── config/          # 服务器配置
│       │           │   ├── exception/       # 异常处理
│       │           │   └── utils/           # 工具类
│       │           ├── dao/                 # 数据访问对象
│       │           ├── handler/             # 请求处理器
│       │           ├── model/               # 数据模型
│       │           │   ├── dto/             # 数据传输对象
│       │           │   └── entity/          # 数据库实体
│       │           └── service/             # 业务逻辑服务
│       └── test/                            # 测试目录
│
└── pom.xml                                  # 根 Maven 配置文件
```

## 开发指南

### 1. 前置要求

- **JDK 21** 或更高版本
- **Maven**（用于项目构建和依赖管理）
- **JavaFX SDK**（包含在 JDK 中）

### 2. 安装和设置

#### 2.1 克隆项目

首先，将项目克隆到本地机器：

```bash
git clone https://github.com/bytecents/aiconomy.git
cd aiconomy
```

#### 2.2 构建项目

使用 Maven 构建所有模块：

```bash
mvn clean install
```

#### 2.3 运行应用程序

使用以下命令运行应用程序：

```bash
cd aiconomy-client
mvn javafx:run
```

### 3. 模块结构

#### 3.1 客户端模块 (aiconomy-client)

- **Application**: 包含主应用程序类（`AIconomyApplication.java`）
- **Controllers**: 处理用户界面逻辑
    - `AccountsController.java`
    - `AnalyticsController.java`
    - `BudgetsController.java`
    - `DashboardController.java`
    - `SettingsController.java`
    - `TransactionsController.java`
- **Services**: 客户端业务逻辑
- **Resources**: FXML 布局和资源文件

#### 3.2 服务器模块 (aiconomy-server)

- **Handlers**: 处理传入请求
- **Services**: 实现核心业务逻辑
- **Models**: 定义数据结构
- **DAO**: 处理数据持久化
- **Common**: 共享工具和配置

#### 3.3 LangChain 模块 (aiconomy-langchain)

- **AIServices**: AI 服务实现
    - 分类服务
    - 数据分析服务
- **Chains**: 处理链实现
- **Models**: AI 模型定义
- **Common**: 共享配置和工具

### 4. UI 结构（客户端模块）

客户端模块使用 JavaFX 和 FXML 定义 UI：

- `main.fxml`: 主应用程序布局
- `dashboard.fxml`: 仪表盘视图
- `accounts.fxml`: 账户管理
- `analytics.fxml`: 财务分析
- `budgets.fxml`: 预算管理
- `transactions.fxml`: 交易历史
- `settings.fxml`: 应用程序设置

### 5. 配置

每个模块都有自己的配置文件：

- 客户端：`src/main/resources/log4j2.xml`
- 服务器：`common/config` 中的配置文件
- LangChain：`common/config` 中的 AI 模型配置

### 6. 测试

每个模块包含自己的测试目录：

- `src/test/java`: 单元测试
- 使用 `mvn test` 运行各个模块的测试

### 7. 贡献指南

我们欢迎对任何模块的贡献：

1. Fork 仓库
2. 创建功能分支：
   ```bash
   git checkout -b feature-branch
   ```
3. 进行修改
4. 运行测试：
   ```bash
   mvn test
   ```
5. 提交更改：
   ```bash
   git commit -m "feat(module): description"
   ```
6. 推送并创建 Pull Request

### 8. 模块依赖

- **客户端**：依赖服务器模块进行数据访问
- **服务器**：依赖 LangChain 模块实现 AI 功能
- **LangChain**：独立的 AI/ML 处理模块

### 9. 日志记录

所有模块都使用 Log4j2 进行日志记录。配置文件位于各模块的 resources 目录中。
