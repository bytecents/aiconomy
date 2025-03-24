# AIconomy

AIconomy is a financial management application designed to provide financial data analysis, budget tracking, and account management features through an intuitive user interface. The project uses JavaFX for the UI and follows an MVC architecture to separate the view, controller, and service layers, ensuring maintainability and scalability.

<p align="center">
  <a href="https://github.com/bytecents/aiconomy/blob/main/README.md">English</a>
   • 
  <a href="https://github.com/bytecents/aiconomy/blob/main/README.cn.md">简体中文</a>
</p>

## Project Structure

The project directory structure is as follows:

```
.
├── README.md                                                   # Project documentation
├── pom.xml                                                     # Maven build configuration
└── src
    └── main
        ├── java
        │   ├── com
        │   │   └── se
        │   │       └── aiconomy
        │   │           ├── AIconomyApplication.java            # Main class to launch the application
        │   │           ├── common                              # Common utility classes
        │   │           │   ├── CSVUtil.java                    # CSV file handling utility
        │   │           │   └── JSONUtil.java                   # JSON file handling utility
        │   │           ├── controller                          # Controller layer
        │   │           │   ├── AccountsController.java         # Accounts controller
        │   │           │   ├── AnalyticsController.java        # Analytics controller
        │   │           │   ├── BudgetsController.java          # Budgets controller
        │   │           │   ├── DashboardController.java        # Dashboard controller
        │   │           │   ├── SettingsController.java         # Settings controller
        │   │           │   ├── SidebarController.java          # Sidebar controller
        │   │           │   └── TransactionsController.java     # Transactions controller
        │   │           ├── dao                                 # Data access layer
        │   │           │   └── AccountDao.java                 # Account data access object
        │   │           ├── model                               # Data models
        │   │           │   └── langchain                       # Business logic layer (e.g., transaction models)
        │   │           │       └── Transaction.java            # Transaction model
        │   │           └── service                             # Service layer
        │   │               ├── AccountsService.java            # Accounts service
        │   │               ├── AnalyticsService.java           # Analytics service
        │   │               ├── BudgetsService.java             # Budgets service
        │   │               ├── DashboardService.java           # Dashboard service
        │   │               ├── SettingsService.java            # Settings service
        │   │               └── TransactionsService.java        # Transactions service
        │   └── module-info.java                                # Module configuration file
        └── resources
            ├── assets                                          # Application resources (images)
            │   ├── accounts-active.png                         # Active account icon
            │   ├── analytics-active.png                        # Active analytics icon
            │   ├── dashboard-active.png                        # Active dashboard icon
            │   ├── logo.png                                    # Application logo
            │   ├── settings-active.png                         # Active settings icon
            ├── fxml                                            # FXML files (UI layouts)
            │   ├── accounts.fxml                               # Accounts page layout
            │   ├── analytics.fxml                              # Analytics page layout
            │   ├── dashboard.fxml                              # Dashboard page layout
            │   ├── main.fxml                                   # Main page layout
            │   ├── settings.fxml                               # Settings page layout
            │   └── transactions.fxml                           # Transactions page layout
            └── log4j2.xml                                      # Log configuration file
```

## Development Guide

### 1. Prerequisites

- **JDK 21** or higher
- **Maven** (for project build and dependency management)

### 2. Installation and Setup

#### 2.1 Clone the Project

First, clone the project to your local machine:

```bash
git clone https://github.com/bytecents/aiconomy.git
cd aiconomy
```

#### 2.2 Build the Project

Use Maven to build the project:

```bash
mvn clean install
```

This will compile the source code, run unit tests, and generate the executable JAR file.

#### 2.3 Run the Application

Use the following command to run the application:

```bash
mvn javafx:run
```

This will start the JavaFX application and open the main interface.

### 3. Code Structure

- **`AIconomyApplication.java`**: The entry point of the application that starts the JavaFX application.
- **`controller`**: Contains all the controller classes. Each controller class handles the business logic for a specific page, such as the accounts controller, transactions controller, etc.
- **`dao`**: The data access layer responsible for interacting with the database or other persistence stores.
- **`model`**: Defines the application's data models, such as transaction models, account models, etc.
- **`service`**: The service layer, which implements business logic and data processing functionalities.

### 4. FXML Structure

- The **`fxml`** folder contains all the UI layout files of the application. Each `.fxml` file corresponds to a page in the application, described using JavaFX's `FXML` markup language.
- For example, `main.fxml` is the main page layout, `accounts.fxml` is for the accounts page, `dashboard.fxml` is for the dashboard, etc.

### 5. Logging

The project uses **Log4j2** for logging. The log configuration file `log4j2.xml` is located in the `resources` folder. You can modify the logging level and output settings as needed.

### 6. Testing

- **Unit Testing**: All service classes should have unit tests. Test code is located in the `src/test/java` folder.
- **JUnit** or **TestFX** (for JavaFX UI testing) can be used for writing and running tests.

### 7. Extending and Contributing

We welcome developers to contribute to the project or submit feature requests. Before contributing code, please ensure that your changes are properly unit tested.

#### 7.1 How to Contribute

1. Fork the project and clone it to your local machine.
2. Create a new branch for your changes:
   ```bash
   git checkout -b feature-branch
   ```
3. Make your changes locally and commit them:
   ```bash
   git commit -m "Describe your changes"
   ```
4. Push your branch to the remote repository:
   ```bash
   git push origin feature-branch
   ```
5. Submit a Pull Request to merge your changes.
