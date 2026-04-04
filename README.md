# MyBatis AutoMapper - Automatic Code Generation Tool

**MyBatis AutoMapper** is a powerful and efficient automatic code generation tool designed to streamline the development of MyBatis-based applications. It analyzes database table metadata and automatically generates a complete set of boilerplate code, significantly reducing repetitive manual coding tasks.

## Features

- **Intelligent Table Metadata Extraction**: Automatically analyzes database tables using SQL commands (`SHOW TABLE STATUS`, `SHOW FULL FIELDS`, `SHOW INDEX`) to extract comprehensive table information.

- **Complete Code Generation Pipeline**: Generates the following components for each database table:
  - PO (Persistent Object) classes - Entity classes representing database tables
  - Query Beans - Query parameter objects for flexible data retrieval
  - Mapper XML files - Dynamic SQL mappings for MyBatis
  - Mapper interfaces - Java interfaces for database operations
  - Service layer - Business logic interfaces
  - Service implementations - Concrete service layer implementations
  - Controller classes - RESTful API endpoints

- **Smart Type Mapping**: Automatically maps MySQL field types to corresponding Java types with intelligent type conversion strategies.

- **Flexible Naming Conventions**: Supports customizable naming conventions for packages, classes, and fields.

- **Metadata-aware Generation**: 
  - Detects primary keys and indexes
  - Preserves field comments
  - Generates appropriate annotations for serialization

## Architecture

The project follows a modular builder pattern architecture:

- **Table Metadata Layer**: Responsible for connecting to the database and extracting table structure, field information, and index details.

- **Code Generation Layer**: Each builder handles specific component generation:
  - BuildPo - PO class generation
  - BuildQuery - Query Bean generation
  - BuildMapper - Mapper interface generation
  - BuildMapperXml - XML SQL mapping generation
  - BuildService - Service interface generation
  - BuildServiceImpl - Service implementation generation
  - BuildController - REST controller generation

- **Data Model Layer**: Core data structures (TableInfo, FieldInfo) represent metadata extracted from database tables.

- **Utility Layer**: Common utilities for string manipulation, JSON processing, date handling, and property management.

## Quick Start

1. **Configure Database Connection**: Set up your database connection properties in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/your_database
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
2.**Run the Generator**: Execute the main application:
```bash
java -jar Main.jar
```

3.**Generated Output**: The tool will automatically generate all necessary code files based on your database schema.

## Technology Stack

- **Java Version**: JDK 21
- **Database**: MySQL 8.0.33
- **Build Tool**: Maven
- **Libraries**:
  - MySQL Connector Java
  - Apache Commons Lang3
  - FastJSON2 for JSON processing
  - SLF4J + Logback for logging

## Project Structure

MybatisAutoMapper/  
├── Main/ # Core generator module  
│ └── src/main/java/com/main/  
│ ├── Application.java # Main entry point  
│ ├── bean/ # Data models (TableInfo, FieldInfo)  
│ ├── builder/ # Code generation builders  
│ └── utils/ # Utility classes  
└── Demo/ # Demo application module  
└── src/main/  
├── java/ # Generated code and demo app  
└── resources/ # Configuration files  


## Workflow

The generation process follows these steps:

1. **Initialization**: Load configuration and establish database connection
2. **Metadata Extraction**: Extract table information, field details, and indexes
3. **Code Generation**: For each table, generate all required components:
   - PO classes with proper type mappings
   - Query beans for flexible queries
   - Mapper interfaces and XML files
   - Service layer interfaces and implementations
   - REST controllers with CRUD operations

## Benefits

- **Productivity Boost**: Reduces boilerplate coding by up to 80%
- **Consistency**: Ensures uniform code structure across the project
- **Error Reduction**: Minimizes manual coding errors
- **Maintainability**: Easy to regenerate code when database schema changes
- **Customization**: Flexible configuration for different project requirements
