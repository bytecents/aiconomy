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
.
├── README.cn.md                             # Chinese documentation
├── README.md                                # English documentation
├── aiconomy-client/                         # Client module
│   ├── pom.xml                              # Client module Maven configuration
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/se/aiconomy/client/
│       │   │       ├── Application/         # Application entry and configuration
│       │   │       ├── common/              # Common utilities and helpers
│       │   │       │   └── utils/           # Utility classes (e.g., CSV processing)
│       │   │       ├── controller/          # JavaFX controllers for UI interaction
│       │   │       ├── model/               # Data models
│       │   │       │   └── dto/             # Data Transfer Objects
│       │   │       └── service/             # Client business logic services
│       │   └── resources/
│       │       ├── assets/                  # Static resources (images)
│       │       ├── fxml/                    # JavaFX layout files
│       │       └── log4j2.xml               # Logging configuration
│       └── test/                            # Test directory
│
├── aiconomy-langchain/                      # AI functionality module
│   ├── pom.xml                              # LangChain module Maven configuration
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/se/aiconomy/langchain/
│       │   │       ├── AIServices/          # AI service implementations
│       │   │       │   └── classification/  # Classification services
│       │   │       ├── chains/              # AI processing chains
│       │   │       │   └── chat/            # Chat functionality
│       │   │       ├── common/              # Common components
│       │   │       │   ├── chain/           # Base chain framework
│       │   │       │   ├── config/          # Configuration management
│       │   │       │   ├── model/           # Model configurations
│       │   │       │   ├── prompt/          # Prompt templates
│       │   │       │   └── utils/           # Utility classes
│       │   │       └── models/              # AI model definitions
│       │   └── resources/
│       └── test/                            # Test directory
│           └── java/
│               └── com/se/aiconomy/langchain/
│                   ├── AIservices/          # AI service tests
│                   ├── chain/               # Chain processing tests
│                   └── utils/               # Utility tests
│
├── aiconomy-server/                         # Server module
│   ├── pom.xml                              # Server module Maven configuration
│   └── src/
│       ├── main/
│       │   └── java/
│       │       └── com/se/aiconomy/server/
│       │           ├── common/              # Common components
│       │           │   ├── config/          # Server configurations
│       │           │   ├── exception/       # Exception handling
│       │           │   └── utils/           # Utility classes
│       │           ├── dao/                 # Data Access Objects
│       │           ├── handler/             # Request handlers
│       │           ├── model/               # Data models
│       │           │   ├── dto/             # Data Transfer Objects
│       │           │   └── entity/          # Database entities
│       │           └── service/             # Business logic services
│       └── test/                            # Test directory
│
└── pom.xml                                  # Root Maven configuration file
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
    - `AccountsController.java`
    - `AnalyticsController.java`
    - `BudgetsController.java`
    - `DashboardController.java`
    - `SettingsController.java`
    - `TransactionsController.java`
- **Services**: Client-side business logic
- **Resources**: FXML layouts and assets

#### 3.2 Server Module (aiconomy-server)

- **Handlers**: Process incoming requests
- **Services**: Implement core business logic
- **Models**: Define data structures
- **DAO**: Handle data persistence
- **Common**: Shared utilities and configurations

#### 3.3 LangChain Module (aiconomy-langchain)

- **AIServices**: AI service implementations
    - Classification services
    - Data analysis services
- **Chains**: Processing chain implementations
- **Models**: AI model definitions
- **Common**: Shared configurations and utilities

### 4. UI Structure (Client Module)

The client module uses JavaFX with FXML for UI definition:

- `main.fxml`: Main application layout
- `dashboard.fxml`: Dashboard view
- `accounts.fxml`: Accounts management
- `analytics.fxml`: Financial analytics
- `budgets.fxml`: Budget management
- `transactions.fxml`: Transaction history
- `settings.fxml`: Application settings

### 5. Configuration

Each module has its own configuration files:

- Client: `src/main/resources/log4j2.xml`
- Server: Configuration files in `common/config`
- LangChain: AI model configurations in `common/config`

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
- **LangChain**: Independent module for AI/ML processing

### 9. Logging

All modules use Log4j2 for logging. Configuration files are located in each module's resources directory.
