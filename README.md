# 宿舍信息管理系统（Dormitory）

基于 **Spring Boot 2.6.13 + Spring Security + Thymeleaf + Spring Data JPA + MySQL** 的高校学生宿舍管理系统。
面向三类角色（系统管理员 / 宿管员 / 学生），覆盖学生信息管理、宿舍房源管理、住宿安排、申请处理、用户权限管理与数据备份恢复等完整功能。

---

## 一、技术栈

| 层 | 技术 |
| --- | --- |
| 后端框架 | Spring Boot 2.6.13 |
| 安全框架 | Spring Security（BCrypt 加密、角色权限控制） |
| 模板引擎 | Thymeleaf + thymeleaf-extras-java8time |
| ORM | Spring Data JPA（Hibernate） |
| 数据库 | MySQL 5.7 / 8.0 |
| 构建工具 | Maven |
| 前端 | HTML5 + CSS3 + JavaScript + Bootstrap 5 |

## 二、运行环境要求

- JDK 8 或以上（项目按 JDK 8 编译，JDK 11/17 亦可运行）
- Maven 3.6+
- MySQL 5.7 / 8.0（已在 MySQL 8.0 上完整验证）

## 三、快速开始（3 分钟跑起来）

### 第 1 步：创建数据库（Navicat 或命令行）

方式一：Navicat 图形界面

1. 打开 Navicat，连接本地 MySQL；
2. 右键 → 新建数据库，名称填 `Dormitory`，字符集选 `utf8mb4`，排序规则 `utf8mb4_unicode_ci`；
3. 双击打开该数据库 → 工具栏「查询」→「新建查询」；
4. 打开本仓库 `sql/dormitory.sql`，全选复制粘贴到查询窗口，点击「运行」；
5. 表结构 + 演示数据一次性建好（可重复执行，`CREATE TABLE IF NOT EXISTS` + `ON DUPLICATE KEY UPDATE`）。

方式二：命令行

```bash
mysql -uroot -p -e "CREATE DATABASE Dormitory DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -uroot -p --default-character-set=utf8mb4 Dormitory < sql/dormitory.sql
```

### 第 2 步：修改数据库账号密码

打开 `src/main/resources/application.yml`，把 `username` / `password` 改成本机 MySQL 的账号密码：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/Dormitory?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root        # ← 改成你的 MySQL 用户名
    password: root        # ← 改成你的 MySQL 密码
```

数据库表结构由 `sql/dormitory.sql` 初始化，因此 `ddl-auto` 保持 `none`，无需让 JPA 自动建表。

### 第 3 步：启动项目

IDEA 方式（推荐）：

1. IDEA → File → Open，选择本 `Dormitory` 目录（即 `pom.xml` 所在目录）；
2. 等待 Maven 依赖自动下载完成；
3. 找到 `src/main/java/com/cxt/dormitory/DormitoryApplication.java`，右键 → Run 'DormitoryApplication'；
4. 控制台出现 `Started DormitoryApplication in x.xxx seconds` 即启动成功。

命令行方式：

```bash
mvn clean package -DskipTests
java -jar target/Dormitory-0.0.1-SNAPSHOT.jar
```

启动后浏览器访问：**http://localhost:8080**

## 四、演示账号

| 角色 | 账号 | 密码 | 说明 |
| --- | --- | --- | --- |
| 系统管理员 | `admin` | `admin123` | 全部功能 + 用户管理 + 备份恢复 |
| 宿管员 | `manager` | `manager123` | 学生/宿舍/住宿/申请处理 |
| 学生 | `student01` | `student123` | 查看与维护个人信息、提交申请（王小明） |
| 学生 | `student02` | `stu02@123` | （李思思） |
| 学生 | `student03` | `stu03@123` | （赵宇航） |

> 密码均已使用 BCrypt 加密存储；此外程序启动时还会自动兜底创建这批账号，即使清空 `t_user` 表，重启后仍可登录。

## 五、功能模块与权限

| 模块 | 功能点 | 管理员 | 宿管员 | 学生 |
| --- | --- | :-: | :-: | :-: |
| 首页仪表盘 | 房源/学生/在住/待处理统计、最近动态 | ✓ | ✓ | ✓ |
| 学生信息 | 学生档案增删改查、关键字搜索 | ✓ | ✓ | — |
| 我的信息 | 查看本人档案、更新联系方式 | ✓ | ✓ | ✓ |
| 楼栋/房型/房间 | 宿舍房源增删改查、床位占用可视化 | ✓ | ✓ | — |
| 住宿安排 | 办理入住/调宿/退宿、按床位安排 | ✓ | ✓ | — |
| 申请处理 | 学生提交申请（类型/调宿/报修）、宿管审批 | ✓ | ✓（处理） | ✓（提交） |
| 用户管理 | 系统账号增删改查、启用/禁用 | ✓ | — | — |
| 数据备份 | mysqldump 在线备份、下载、一键恢复 | ✓ | — | — |

未登录访问任意页面会跳转登录页；越权访问返回 403 页面。

## 六、数据备份与恢复说明

- 备份功能在页面上点击「立即备份」即可，本质是调用本机 MySQL 的 `mysqldump` 命令导出 `.sql` 文件到项目 `backups/` 目录，并在 `t_backup` 表中留下记录。
- 备份文件可随时下载，也可在页面上「恢复」回数据库。
- `application.yml` 中的配置项可按需修改：

```yaml
dorm:
  backup:
    dir: backups                 # 备份文件输出目录
    mysqldump-path: mysqldump    # 本机 mysqldump 命令或绝对路径
    mysql-path: mysql            # 本机 mysql 命令或绝对路径
```

- Windows 下如果 `mysqldump` 不在 PATH 中，请改为绝对路径，例如：
  `mysqldump-path: C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqldump.exe`
- 程序已内置容错：若本机 `my.ini` 配置异常导致命令行工具解析失败，会自动加 `--no-defaults` 参数重试，一般无需额外处理。

## 七、项目结构

```
Dormitory
├── sql/
│   └── dormitory.sql              # 数据库脚本（Navicat 直接执行）
├── src/main/java/com/cxt/dormitory/
│   ├── DormitoryApplication.java  # 启动类
│   ├── config/                    # 安全配置、密码编码器、初始数据
│   ├── controller/                # 各模块控制器
│   ├── service/                   # 业务逻辑
│   ├── repository/                # Spring Data JPA 仓库
│   ├── entity/                    # 实体（对应 8 张表）
│   └── dto/                       # 视图对象
├── src/main/resources/
│   ├── application.yml            # 主配置
│   ├── templates/                 # Thymeleaf 页面
│   └── static/                    # 静态资源（Bootstrap 已本地化）
└── pom.xml
```

数据库共 8 张表：`t_user`（用户账户）、`t_student`（学生）、`t_building`（楼栋）、`t_room_type`（房型）、`t_room`（房间）、`t_stay`（住宿记录）、`t_apply`（申请）、`t_backup`（备份记录）。

## 八、常见问题

1. **启动报 `Access denied for user 'root'@'localhost'`**
   `application.yml` 里的数据库密码不对，改成本机 MySQL 密码。

2. **启动报 `Unknown database 'Dormitory'`**
   数据库还没建，回到第三步第 1 步用 `sql/dormitory.sql` 初始化。

3. **页面 403 或不停跳登录页**
   确认用演示账号登录，且没有浏览器缓存旧 Cookie；退出登录（/logout）后重登。

4. **备份按钮点了没反应/报错**
   检查 `dorm.backup.mysqldump-path` 是否指向本机 mysqldump；若在 PATH 中可直接写 `mysqldump`。

5. **想清空数据重新演示**
   Navicat 中删除 `Dormitory` 数据库后重新执行 `sql/dormitory.sql` 即可。
