# ShardingSphere JDBC Extension

ShardingSphere JDBC 扩展项目，提供对国产数据库（人大金仓）的支持以及 Nacos 配置中心的集成。

## 📦 项目结构

```
shardingsphere-jdbc-extension/ 
├── shardingsphere-jdbc-kingbase # 人大金仓数据库适配模块 
├── shardingsphere-jdbc-nacos-config # Nacos 配置中心支持模块##
```
🧩 模块说明
### 1. shardingsphere-jdbc-kingbase

人大金仓（KingBase）数据库适配模块，提供以下功能：
- KingBase 数据库类型定义
- 数据库元数据解析
- SQL 词法分析和语法解析
- 方言支持

**核心类：**
- `KingBase8DatabaseType` - 数据库类型定义
- `KingBaseDialectDatabaseMetaData` - 数据库元数据
- `KingBase8SQLParser` - SQL 解析器
- `KingBase8ConnectionPropertiesParser` - 连接属性解析

### 2. shardingsphere-jdbc-nacos-config

Nacos 配置中心集成模块，提供以下功能：
- Nacos 配置加载
- 动态数据源管理
- URL 地址解析

**核心类：**
- `NacosShardingSphereURL` - Nacos 地址定义
- `NacosShardingSphereURLLoadEngine` - 配置加载引擎
- `NacosDriverDataSourceCache` - 数据源缓存

## 🔧 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 基础运行环境 |
| Maven | - | 项目构建工具 |
| Apache ShardingSphere | 5.5.2 | 分布式数据库中间件 |
| Spring Cloud Alibaba Nacos | 2023.0.1.2 | 配置中心 |

## 📚 依赖的开源组件

### 核心依赖

#### 1. Apache ShardingSphere
- **GroupId:** `org.apache.shardingsphere`
- **Version:** 5.5.2
- **License:** Apache License 2.0
- **官网:** https://shardingsphere.apache.org/
- **说明:** 分布式数据库生态系统，提供分库分表、读写分离等功能

**子组件：**
- `shardingsphere-jdbc` - ShardingSphere JDBC 核心
- `shardingsphere-infra-database-core` - 数据库基础设施核心
- `shardingsphere-infra-database-postgresql` - PostgreSQL 数据库支持（KingBase 基于 PostgreSQL）
- `shardingsphere-parser-sql-engine` - SQL 解析引擎

#### 2. Spring Cloud Alibaba Nacos Config
- **GroupId:** `com.alibaba.cloud`
- **ArtifactId:** `spring-cloud-starter-alibaba-nacos-config`
- **Version:** 2023.0.1.2
- **License:** Apache License 2.0
- **官网:** https://nacos.io/
- **说明:** 阿里巴巴开源的动态服务发现、配置管理和服务管理平台

#### 3. Nacos Client
- **传递依赖**
- **说明:** Nacos 客户端，用于连接 Nacos 配置中心

### 可选/Provided 依赖

以下依赖在编译时提供，运行时由主项目提供：

- `shardingsphere-infra-database-core` (provided)
- `shardingsphere-infra-database-postgresql` (provided)
- `shardingsphere-parser-sql-engine` (provided)

## 🚀 快速开始

### Maven 依赖

#### 人大金仓数据库支持


└── pom.xml # 父工程配置

```xml
<dependency> 
    <groupId>com.shardingsphere.jdbc</groupId> 
    <artifactId>shardingsphere-jdbc-kingbase</artifactId> 
    <version>1.0-SNAPSHOT</version> 
</dependency>
```
#### KingBase 数据源配置

这里的配置与官网集成的配置相同，详细参考官网文档：https://shardingsphere.apache.org/document/current/en/overview/



#### Nacos 配置中心支持


└── pom.xml # 父工程配置

```xml
<dependency> 
    <groupId>com.shardingsphere.jdbc</groupId> 
    <artifactId>shardingsphere-jdbc-nacos-config</artifactId> 
    <version>1.0-SNAPSHOT</version> 
</dependency>
```
#### 配置中心集成
```yaml
spring:
  application:
    name: sharding-jdbc-demo
    version: 1.0.0
  cloud:
    compatibility-verifier:
      enabled: false
    nacos:
      config:
        namespace: dev
        server-addr: 127.0.0.1:18848
        group: DEFAULT_GROUP
        import-check:
          enabled: false
  datasource:
  # 新版本与老版本的区别，新版本的驱动类名改为了org.apache.shardingsphere.driver.ShardingSphereDriver
    # 配置 DataSource Driver
    driver-class-name: com.shardingsphere.jdbc.nacos.config.driver.NacosShardingSphereDriver
    # 指定 YAML 配置文件
    url: jdbc:shardingsphere:nacos:sharding.yaml,sharing2.yaml?serverAddr=${spring.cloud.nacos.config.server-addr}&namespace=${spring.cloud.nacos.config.namespace}

```

## ⚙️ SPI 注册

本项目通过 Java SPI 机制自动注册扩展：

### KingBase 模块

- `org.apache.shardingsphere.infra.database.core.type.DatabaseType`
- `org.apache.shardingsphere.infra.database.core.metadata.database.DialectDatabaseMetaData`
- `org.apache.shardingsphere.infra.database.core.connector.ConnectionPropertiesParser`
- `org.apache.shardingsphere.sql.parser.spi.DialectSQLParserFacade`
- `org.apache.shardingsphere.sql.parser.spi.SQLStatementVisitorFacade`

### Nacos 模块

- `org.apache.shardingsphere.infra.url.spi.ShardingSphereURLLoader`

## 📄 开源协议

- **Apache ShardingSphere:** Apache License 2.0
- **Nacos:** Apache License 2.0
- **本项目:** Apache License 2.0

## 🔗 相关链接

- [Apache ShardingSphere 官方文档](https://shardingsphere.apache.org/document/current/)
- [Nacos 官方文档](https://nacos.io/docs/)
- [人大金仓数据库](http://www.kingbase.com.cn/)

## 📝 注意事项

1. KingBase 模块依赖 PostgreSQL 相关组件，因为 KingBase 基于 PostgreSQL 开发
2. 使用 provided 作用域的依赖需要确保运行环境中已包含相应依赖
3. Nacos 配置中心需要预先部署 Nacos Server

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

---

**版本:** 1.0-SNAPSHOT  
**维护者:** ShardingSphere JDBC Extension Team



