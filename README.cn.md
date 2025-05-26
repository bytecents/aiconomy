# AIconomy

AIconomy 是一个金融管理应用程序，通过直观的用户界面提供财务数据分析、预算跟踪和账户管理功能。该项目使用 JavaFX
构建客户端界面，采用微服务架构，将客户端、服务器和 AI 模块分离，确保了可维护性和可扩展性。

## 项目结构

项目由三个主要模块组成：

```
├── README.md                    # 项目文档（英文版）
├── README.cn.md                 # 项目文档（中文版）
├── aiconomy-client              # JavaFX 客户端应用程序
│   ├── package.txt              # 依赖项清单 
│   ├── pom.xml                  # Maven构建配置
│   ├── src/main                 # 主源代码目录
│   │   ├── java/com/se/aiconomy/client  # 客户端
│   │   │   ├── Application      # 启动类
│   │   │   ├── common           # 公共工具类
│   │   │   └── controller       # UI控制器
│   │   └── resources            # 客户端资源文件
│   │       ├── assets           # 静态资源 
│   │       ├── css              # 样式表
│   │       └── fxml             # FXML界面布局文件 
│
├── aiconomy-server              # 服务端
│   ├── pom.xml                  # Maven构建配置
│   ├── src/main                 # 主源代码与资源目录
│   │   ├── java/com/se/aiconomy/server  # 服务端代码库
│   │   │   ├── common/utils     # 核心工具类（CSV/Excel/JSON/文件处理）
│   │   │   ├── handler          # 请求处理器
│   │   │   └── model            # 数据模型（DTO/实体类）
│   │   └── resources/data       # JSON数据存储目录
│
├── data                         # 持久化数据存储目录
│   ├── BankAccounts.json        # 银行账户记录
│   ├── Budgets.json             # 预算配置
│   └── transactions.json        # 交易历史记录
│
└── pom.xml                      # Maven父项目管理配置
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
  - `AccountsController`
  - `AnalyticsController`
  - `BaseController`
  - `BudgetsController`
  - `DashboardController`
  - `LoginController`
  - `SettingsController`
  - `TransactionsController`
  - `AIController`
  - `SignupController`
  - `SidebarController`
- **Services**: 客户端业务逻辑
- **Resources**: FXML 布局和资源文件

#### 3.2 服务器模块 (aiconomy-server)

- **Handlers**: 处理传入请求
- **Services**: 实现核心业务逻辑
- **Models**: 定义数据结构
- **DAO**: 处理数据持久化
- **Common**: 共享工具和配置

### 4. UI 结构（客户端模块）

客户端模块使用 JavaFX 和 FXML 定义 UI：

- `Main`: 主应用程序布局
- `Dashboard`: 仪表盘视图
- `Accounts`: 账户管理
- `Analytics`: 财务分析
- `Budgets`: 预算管理
- `Transactions`: 交易历史
- `Settings`: 应用程序设置
- `Signup`: 注册管理
- `AI`: 智能分析
- `Analytics`: 交易可视化
- `Login`: 用户认证

### 5. 配置

每个模块都有自己的配置文件：

- 客户端：`src/main/resources/log4j2.xml`
- 服务器：`common/config` 中的配置文件

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

### 10. 贡献者名单

#### 组长
- **时珂熠** - 项目管理和推进

#### 技术选型
- **赵哲昀** - 后端和AI
- **胡诚成** - 前端

#### 前端开发
- **胡诚成**
  - 数据分析模块（含AI交互界面）
  - 交易页面
  - 与钟悠豪共同开发主页界面
  - 预算AI交互功能

- **杨羽婕**
  - 预算页面
  - 账户页面
  - 设置页面

- **钟悠豪**
  - 用户管理系统
  - 与胡诚成共同开发主页界面
  - 与杨羽婕共同开发账户页面
  - 预算功能

#### 后端开发
- **李诗予**
  - 预算功能实现
  - 与时珂熠共同开发账户系统
  - 用户管理系统
  - 设置功能

- **时珂熠**
  - 交易处理系统
  - 与李诗予共同开发账户系统
  - 主页面请求处理

- **赵哲昀**
  - 基于AI的交易处理
  - 交易分类
  - 冲突处理

#### 协作部分
- **主页集成**: 胡诚成 & 钟悠豪 (前端) ↔ 时珂熠 (后端)
- **AI技术对接**: 胡诚成 (前端) ↔ 赵哲昀 (后端AI)
- **交易系统**: 胡诚成 (前端) ↔ 时珂熠 & 赵哲昀 (后端)
- **预算系统**: 杨羽婕 & 钟悠豪 (前端) ↔ 李诗予 (后端)
- **账户系统**: 钟悠豪 (前端) ↔ 时珂熠 & 李诗予 & 时珂熠 (后端)
- **用户管理系统**: 钟悠豪 & 杨羽婕 (前端) ↔ 李诗予 (后端)