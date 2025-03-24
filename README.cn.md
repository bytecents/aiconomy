# AIconomy

AIconomy 是一款财务管理应用程序，旨在通过直观的用户界面提供财务数据分析、预算跟踪和账户管理功能。该项目使用 JavaFX 创建用户界面，并采用 MVC 架构将视图、控制器和服务层分离，确保了代码的可维护性和可扩展性。

## 项目结构

项目的目录结构如下：

```
.
├── README.md                                                   # 项目文档
├── pom.xml                                                     # Maven 构建配置文件
└── src
    └── main
        ├── java
        │   ├── com
        │   │   └── se
        │   │       └── aiconomy
        │   │           ├── AIconomyApplication.java            # 启动应用程序的主类
        │   │           ├── common                              # 公共工具类
        │   │           │   ├── CSVUtil.java                    # CSV 文件处理工具类
        │   │           │   └── JSONUtil.java                   # JSON 文件处理工具类
        │   │           ├── controller                          # 控制器层
        │   │           │   ├── AccountsController.java         # 账户控制器
        │   │           │   ├── AnalyticsController.java        # 分析控制器
        │   │           │   ├── BudgetsController.java          # 预算控制器
        │   │           │   ├── DashboardController.java        # 仪表盘控制器
        │   │           │   ├── SettingsController.java         # 设置控制器
        │   │           │   ├── SidebarController.java          # 侧边栏控制器
        │   │           │   └── TransactionsController.java     # 交易控制器
        │   │           ├── dao                                 # 数据访问层
        │   │           │   └── AccountDao.java                 # 账户数据访问对象
        │   │           ├── model                               # 数据模型
        │   │           │   └── langchain                       # 业务逻辑层（例如，交易数据模型）
        │   │           │       └── Transaction.java            # 交易数据模型
        │   │           └── service                             # 服务层
        │   │               ├── AccountsService.java            # 账户服务
        │   │               ├── AnalyticsService.java           # 分析服务
        │   │               ├── BudgetsService.java             # 预算服务
        │   │               ├── DashboardService.java           # 仪表盘服务
        │   │               ├── SettingsService.java            # 设置服务
        │   │               └── TransactionsService.java        # 交易服务
        │   └── module-info.java                                # 模块配置文件
        └── resources
            ├── assets                                          # 应用程序资源（图片）
            │   ├── accounts-active.png                         # 活动账户图标
            │   ├── analytics-active.png                        # 活动分析图标
            │   ├── dashboard-active.png                        # 活动仪表盘图标
            │   ├── logo.png                                    # 应用程序logo
            │   ├── settings-active.png                         # 活动设置图标
            ├── fxml                                            # FXML 文件（UI 布局）
            │   ├── accounts.fxml                               # 账户页面布局
            │   ├── analytics.fxml                              # 分析页面布局
            │   ├── dashboard.fxml                              # 仪表盘页面布局
            │   ├── main.fxml                                   # 主页面布局
            │   ├── settings.fxml                               # 设置页面布局
            │   └── transactions.fxml                           # 交易页面布局
            └── log4j2.xml                                      # 日志配置文件
```

## 开发指南

### 1. 环境要求

- **JDK 21** 或更高版本
- **Maven**（用于项目构建和依赖管理）

### 2. 安装和运行

#### 2.1 克隆项目

首先，克隆项目到你的本地机器：

```bash
git clone https://github.com/bytecents/aiconomy.git
cd aiconomy
```

#### 2.2 构建项目

使用 Maven 来构建项目：

```bash
mvn clean install
```

这将编译源代码、运行单元测试并生成可执行的 JAR 文件。

#### 2.3 运行应用程序

使用以下命令来运行应用程序：

```bash
mvn javafx:run
```

这将启动 JavaFX 应用程序并打开主界面。

### 3. 代码结构

- **`AIconomyApplication.java`**：应用程序的入口点，启动 JavaFX 应用程序。
- **`controller`**：包含所有控制器类。每个控制器类负责处理特定页面的业务逻辑，如账户控制器、交易控制器等。
- **`dao`**：数据访问层，负责与数据库或其他持久化存储进行交互。
- **`model`**：定义应用程序的数据模型，如交易模型、账户模型等。
- **`service`**：服务层，实现业务逻辑和数据处理功能。

### 4. FXML 结构

- **`fxml`** 文件夹包含应用程序的所有界面布局文件。每个 `.fxml` 文件对应一个页面，使用 JavaFX 的 `FXML` 语言描述界面。
- 例如，`main.fxml` 是主界面布局，`accounts.fxml` 是账户页面，`dashboard.fxml` 是仪表盘页面等。

### 5. 日志记录

该项目使用 **Log4j2** 进行日志记录，配置文件 `log4j2.xml` 位于 `resources` 文件夹中。你可以根据需要修改日志级别和输出设置。

### 6. 测试

- **单元测试**：所有服务类应包含单元测试，测试代码位于 `src/test/java` 文件夹中。
- **JUnit** 或 **TestFX**（用于 JavaFX 界面测试）可用于编写和运行测试。

### 7. 扩展和贡献

我们欢迎开发者为项目贡献代码或提交功能请求。在贡献代码之前，请确保你的更改已通过适当的单元测试。

#### 7.1 如何贡献

1. Fork 项目并克隆到本地。
2. 创建一个新分支进行修改：
   ```bash
   git checkout -b feature-branch
   ```
3. 在本地进行修改并提交：
   ```bash
   git commit -m "描述你的修改"
   ```
4. 推送分支到远程仓库：
   ```bash
   git push origin feature-branch
   ```
5. 提交 Pull Request 合并你的修改。
