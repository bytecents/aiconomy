# AIconomy

AIconomy is a financial management application designed to provide financial data analysis, budget tracking, and account
management features through an intuitive user interface. The project uses JavaFX for the client UI, follows a
microservices architecture with separate client, server, and AI modules, ensuring maintainability and scalability.

<p align="center">
  English
   • 
  <a href="https://github.com/bytecents/aiconomy/blob/main/README.cn.md">简体中文</a>
</p>

## Project Structure

The project is organized into three main modules:

```
├── README.md                    # Project documentation (English)
├── README.cn.md                 # Project documentation (Chinese)
├── aiconomy-client              # JavaFX client application
│   ├── package.txt              # Dependency list 
│   ├── pom.xml                  # Maven build configuration
│   ├── src/main                 # Main source
│   │   ├── java/com/se/aiconomy/client  # Client service
│   │   │   ├── Application      # startup classes
│   │   │   ├── common           # Shared utilities
│   │   │   └── controller       # UI controllers
│   │   └── resources            # Client resources
│   │       ├── assets           # Static assets 
│   │       ├── css              # Stylesheets
│   │       └── fxml             # FXML UI layouts 
│
├── aiconomy-server              # Backend service
│   ├── pom.xml                  # Maven build configuration
│   ├── src/main                 # Main source/resources
│   │   ├── java/com/se/aiconomy/server  # Server codebase
│   │   │   ├── common/utils     # Core utilities (CSV/Excel/JSON/File)
│   │   │   ├── handler          # Request handlers
│   │   │   └── model            # Data models (DTOs/Entities)
│   │   └── resources/data       # JSON data storage directory
│
├── data                         # Persistent data storage
│   ├── BankAccounts.json        # Bank account records
│   ├── Budgets.json             # Budget configurations
│   └── transactions.json        # Transaction history
│
└── pom.xml                      # Maven configuration
```

## Development Guide

### 1. Prerequisites

- **JDK 21** or higher
- **Maven** (for project build and dependency management)
- **JavaFX SDK** (included in JDK)

### 2. Installation and Setup

#### 2.1 Clone the Project

First, clone the project to your local machine:

```bash
git clone https://github.com/bytecents/aiconomy.git
cd aiconomy
```

#### 2.2 Build the Project

Use Maven to build all modules:

```bash
mvn clean install
```

#### 2.3 Run the Application

Use the following command to run the application:

```bash
cd aiconomy-client
mvn javafx:run
```

### 3. Module Structure

#### 3.1 Client Module (aiconomy-client)

- **Application**: Contains the main application class (`AIconomyApplication.java`)
- **Controllers**: Handle user interface logic
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
- **Services**: Client-side business logic
- **Resources**: FXML layouts and assets

#### 3.2 Server Module (aiconomy-server)

- **Handlers**: Process incoming requests
- **Services**: Implement core business logic
- **Models**: Define data structures
- **DAO**: Handle data persistence
- **Common**: Shared utilities and configurations

### 4. UI Structure (Client Module)

The client module uses JavaFX with FXML for UI definition:

- `Main`: Main application layout
- `Dashboard.`: Dashboard view
- `Accounts`: Accounts management
- `Analytics`: Financial analytics
- `Budgets`: Budget management
- `Transactions`: Transaction history
- `Settings`: Application settings
- `Signup`: Register management
- `AI`: Intelligent analytics
- `Analytics`: Transaction visualization
- `Login`: User authentication

### 5. Configuration

Each module has its own configuration files:

- Client: `src/main/resources/log4j2.xml`
- Server: Configuration files in `common/config`

### 6. Testing

Each module contains its own test directory:

- `src/test/java`: Unit tests
- Use `mvn test` to run tests for individual modules

### 7. Contributing

We welcome contributions to any of the modules:

1. Fork the repository
2. Create a feature branch:
   ```bash
   git checkout -b feature-branch
   ```
3. Make your changes
4. Run tests:
   ```bash
   mvn test
   ```
5. Commit your changes:
   ```bash
   git commit -m "<type>(<scope>): <description> \n Author: <Author>"
   ```
6. Push and create a Pull Request

### 8. Module Dependencies

- **Client**: Depends on Server module for data access
- **Server**: Depends on LangChain module for AI features

### 9. Logging

All modules use Log4j2 for logging. Configuration files are located in each module's resources directory.

### 10. Contributors

#### Team Leadership
- **Keyi Shi** - Project management and promoting

#### Technical selection
- **Zheyun Zhao** - Backend AI
- **Chengcheng Hu** - Frontend

#### Frontend Development
- **Chengcheng Hu**
  - Analytics module with AI interaction interface
  - Transaction page
  - Dashboard co-development with Zhong
  - Budget module with AI interaction interface

- **Yujie Yang**
  - Budget page
  - Account page
  - Settings page

- **Youhao Zhong**
  - User management system
  - Dashboard co-development with Hu
  - Accounts co-development with Yang
  - Budget module

#### Backend Development
- **Shiyu Li**
  - Budget system implementation
  - Account co-development with Shi
  - User management system
  - Settings service

- **Keyi Shi**
  - Transaction processing system
  - Account co-development with Li
  - Dashboard request handling

- **Zheyun Zhao**
  - AI-powered transaction processing
  - Transaction classification
  - Conflict resolution

#### Key Collaborations
- **Dashboard Integration**: Hu & Zhong (Frontend) ↔ Shi (Backend)
- **AI Pipeline**: Hu (Frontend) ↔ Zhao (Backend AI)
- **Transaction System**: Hu (Frontend) ↔ Shi & Zhao (Backend)
- **Budget System**: Yang & Zhong (Frontend) ↔ Li (Backend)
- **Account System**: Zhong (Fronted) ↔ Shi & Li & Shi (Backend)
- **User Management System**: Zhong & Yang (Frontend) ↔ Li (Backend)
