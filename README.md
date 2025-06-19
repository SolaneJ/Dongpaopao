## 项目概述
本项目是一个前后端分离项目，包含网关服务、订单服务、跑腿员服务等模块，借助 Spring Cloud 等技术构建微服务架构。

## 项目结构
```plaintext
├── .gitignore
├── README.md
├── backend
│   ├── .gitignore
│   ├── docker-compose.yml
│   ├── pao-gateway
│   │   ├── Dockerfile
│   │   ├── pom.xml
│   │   └── src
│   ├── pao-service
│   │   ├── pao-order-service
│   │   └── pom.xml
│   └── pom.xml
├── docs
│   └── erDiagram.mermaid
│   └── sequenceDiagram.mermaid
└── sql
    ├── t_cross_school_order.sql
    ├── t_dispatcher_info.sql
    ├── t_order.sql
    ├── t_order_log.sql
    ├── t_payment_record.sql
    └── t_school_relation.sql
```

## 技术栈
- **后端框架**: Spring Boot 3.3.7
- **微服务框架**: Spring Cloud 2023.0.3、Spring Cloud Alibaba 2023.0.3.2
- **服务发现与配置中心**: Nacos
- **网关**: Spring Cloud Gateway
- **熔断器**: Sentinel
- **构建工具**: Maven
- **数据库**: MySQL

## 运行项目
1. 切换后端目录：
   ```bash
   cd backend
   ```
2. 构建项目：
   ```bash
   mvn clean install
   ```
3. 启动服务：
   ```bash
   docker-compose up -d
   ```

## 注意事项
- 数据库配置需根据实际情况修改 `application.yml` 文件。