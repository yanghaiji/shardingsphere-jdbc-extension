# shardingsphere-jdbc-kingbase 模块详解
这是一个 ShardingSphere JDBC 的 KingBase8（人大金仓数据库）扩展模块，基于 PostgreSQL 协议实现。由于 KingBase8 兼容 MySQL 协议，该模块通过复用 MySQL 的解析能力来实现对 KingBase8 数据库的支持。

📦 核心类详解
1. KingBase8DatabaseType.java
   作用：数据库类型注册器
   实现 ShardingSphere 的 DatabaseType 接口
   注册 JDBC URL 前缀 jdbc:kingbase8:，让 ShardingSphere 能识别 KingBase8 的数据库连接
   返回数据库类型名称为 kingbase8
   核心功能：告诉 ShardingSphere "我是 KingBase8 数据库，使用 kingbase8 开头的 JDBC URL"

2. KingBaseDialectDatabaseMetaData.java
   作用：数据库方言元数据定义
   实现 ShardingSphere 的 DialectDatabaseMetaData 接口
   定义 KingBase8 的数据库特性：
   引用字符：使用反引号 （与 PostgreSQL 一致）
- **额外数据类型**：JSON、GEOMETRY、YEAR 等 PostgreSQL 特有类型
- **NULL 值排序**：NULL 值排在最后（LOW 优先级）
- **保留字判断**：使用 PostgreSQL 的保留字列表
- **连接特性**：支持实例连接、支持三层存储结构
- **核心功能**：描述 KingBase8 的 SQL 语法规则和数据类型特征

---

#### **3. DatabaseMetaData.java**
**作用：PostgreSQL 保留字常量表**
- 定义了 PostgreSQL 数据库的保留字列表（共 150+ 个）
- 用于 SQL 解析时判断标识符是否为保留字
- 例如：`SELECT`、`TABLE`、`WHERE`、`ORDER` 等都不能作为表名或字段名
- **核心功能**：提供 SQL 语法校验的保留字字典

---

#### **4. DbType.java**
**作用：数据库类型枚举**
- 定义支持的数据库类型枚举值
- 当前只支持 `KING_BASE8("kingbase8")`
- 统一数据库类型命名的常量管理
- **核心功能**：避免硬编码字符串，统一管理数据库类型标识

---

#### **5. KingBase8ConnectionPropertiesParser.java**
**作用：JDBC 连接属性解析器**
- 实现 ShardingSphere 的 `ConnectionPropertiesParser` 接口
- 解析 `jdbc:kingbase8:` 开头的 JDBC URL
- **复用 PostgreSQL 的 URL 解析器**（StandardJdbcUrlParser）
- 设置默认连接参数（优化性能）：
    - 启用预编译语句缓存
    - 启用批量重写
    - 禁用 SSL
    - 零日期时间行为设置为 `round`
- **核心功能**：将 KingBase8 的 JDBC URL 转换为 ShardingSphere 标准的连接属性对象

---

#### **6. KingBase8ParserFacade.java**
**作用：SQL 解析器工厂**
- 实现 ShardingSphere 的 `DialectSQLParserFacade` 接口
- 返回 SQL 词法解析器类：`KingBase8SQLLexer`
- 返回 SQL 语法解析器类：`KingBase8SQLParser`
- **核心功能**：告诉 ShardingSphere 使用哪个类来解析 KingBase8 的 SQL 语句

---

#### **7. KingBase8SQLLexer.java**
**作用：SQL 词法分析器**
- 继承自 ANTLR 生成的 `PostgreSQLStatementLexer`
- 实现 ShardingSphere 的 `SQLLexer` 接口
- **直接复用 PostgreSQL 的词法分析器**
- **核心功能**：将 SQL 字符串拆分成 Token 流（如关键字、标识符、运算符等）

---

#### **8. KingBase8SQLParser.java**
**作用：SQL 语法解析器**
- 继承自 ANTLR 生成的 `PostgreSQLStatementParser`
- 实现 ShardingSphere 的 `SQLParser` 接口
- 调用 `execute()` 方法生成抽象语法树（AST）
- **直接复用 PostgreSQL 的语法分析器**
- **核心功能**：根据 PostgreSQL 语法规则将 Token 流组织成 AST 语法树

---

#### **9. KingBase8SQLStatementVisitorFacade.java**
**作用：SQL 语句访问器工厂**
- 实现 ShardingSphere 的 `SQLStatementVisitorFacade` 接口
- 返回 6 种 SQL 语句类型的访问器类：
    - **DML**（数据操作语言）：`PostgreSQLDMLStatementVisitor`（SELECT、INSERT、UPDATE、DELETE）
    - **DDL**（数据定义语言）：`PostgreSQLDDLStatementVisitor`（CREATE、ALTER、DROP）
    - **TCL**（事务控制语言）：`PostgreSQLTCLStatementVisitor`（COMMIT、ROLLBACK）
    - **DCL**（数据控制语言）：`PostgreSQLDCLStatementVisitor`（GRANT、REVOKE）
    - **DAL**（数据管理语言）：`PostgreSQLDALStatementVisitor`（SHOW、DESCRIBE）
- **全部复用 PostgreSQL 的访问器实现**
- **核心功能**：为不同类型的 SQL 语句提供对应的语义分析器

---

### **🔧 SPI 服务配置文件**

在 `src/main/resources/META-INF/services/` 目录下有 5 个文件，这是 Java SPI（Service Provider Interface）机制的配置文件：

| 文件名 | 注册的实现类 | 作用 |
|--------|-------------|------|
| `org.apache.shardingsphere.infra.database.core.type.DatabaseType` | `KingBase8DatabaseType` | 注册数据库类型 |
| `org.apache.shardingsphere.infra.database.core.metadata.database.DialectDatabaseMetaData` | `KingBaseDialectDatabaseMetaData` | 注册方言元数据 |
| `org.apache.shardingsphere.infra.database.core.connector.ConnectionPropertiesParser` | `KingBase8ConnectionPropertiesParser` | 注册连接属性解析器 |
| `org.apache.shardingsphere.sql.parser.spi.DialectSQLParserFacade` | `KingBase8ParserFacade` | 注册 SQL 解析器工厂 |
| `org.apache.shardingsphere.sql.parser.spi.SQLStatementVisitorFacade` | `KingBase8SQLStatementVisitorFacade` | 注册 SQL 访问器工厂 |

**核心功能**：这些文件让 ShardingSphere 能通过 Java SPI 自动发现并加载 KingBase8 的扩展实现

---

### **🎯 整体架构设计思路**

```
KingBase8 (人大金仓)
    ↓ 兼容 PostgreSQL 协议
复用 PostgreSQL 的 SQL 解析能力
    ↓
ShardingSphere 能识别和处理 KingBase8 的 SQL
    ↓
实现分库分表、读写分离等功能
```

**技术策略**：
1. **复用优于重写**：所有 SQL 解析相关的能力都直接继承 PostgreSQL 的实现
2. **最小化定制**：只定义了元数据和类型注册，没有修改解析逻辑
3. **SPI 机制集成**：通过 Java SPI 无缝接入 ShardingSphere 生态

---


