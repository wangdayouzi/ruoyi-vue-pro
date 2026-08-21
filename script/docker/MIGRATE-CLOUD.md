# RuoYi-Vue-Pro → yudao-cloud 搬迁参考（迁移定制模块）

> 目标：**不动现有单体系统**，另起一个 yudao-cloud 后端仓库，把定制内容（AMF、钉钉登录、可选 reagent）移植过去。
> 适用：现有单体 = `xining-jdk17-main`（v2026.06，jdk17），前端 = `yudao-ui-admin-vue3-xn` + `yudao-ui-admin-uniapp`（与 cloud 共用同一套前端）。

---

## 一、定制范围盘点（来自 git diff，相对分叉点）

| 定制点 | 文件数 | 说明 |
|---|---|---|
| `yudao-module-reagent` | 49 | 试剂管理模块（**可选**，不在第一批范围内） |
| `yudao-module-amf` | 21 | OnlyOffice/申办方等定制模块 |
| `yudao-module-system` 登录/钉钉 | 14 | 登录改造 + 钉钉新版 OAuth2 |
| `sql/postgresql` | ~15 | 建表 + 菜单脚本（如 `1-amf-menu-pg.sql`） |
| `script/docker`、`yudao-server`、框架微调 | ~10 | 部署相关，cloud 需重写 |

其它模块（mall/erp/crm/iot/mes/wms/im/mp/pay/member/bpm/report）均为纯上游代码，**不搬**。

---

## 二、前置准备

1. **克隆 cloud 仓库**（二选一）：
   - GitHub：`git clone https://github.com/YunaiV/yudao-cloud.git`
   - Gitee：`git clone https://gitee.com/zhijiantianya/yudao-cloud.git`
   - 建议切 **jdk17 分支、2026.07 版本**（与前端 uniapp 已合并的 v2026.07 对齐）
2. **版本说明**：现有单体是 `2026.06`，cloud 用 `2026.07`，框架 API 可能有小差异，移植时逐处适配。
3. 本地环境：JDK17、Maven、Node.js 20+、pnpm（与现在一致）。

---

## 三、后端迁移步骤

### 1️⃣ AMF 模块 → 按 cloud 的 -api/-biz 结构移植

cloud 版每个业务模块拆成 `-api`（接口/DTO）与 `-biz`（实现）。AMF 是自包含模块，直接搬：

- 新建 `yudao-module-amf-api`：DTO、枚举、RPC 接口（如无对外调用可极简）
- 新建 `yudao-module-amf-biz`：
  - `controller`（admin/app 的 Controller，注解与 cloud 一致）
  - `service`（Service/ServiceImpl，含 OnlyOffice 相关逻辑）
  - `dal`（dataobject/mysql/redis）
  - `framework`（配置类、属性类）
  - `config`（`AmfConfiguration` 自动装配）
  - 独立启动类 `AmfServerApplication`（如作为独立服务）
- 根 `pom.xml` 注册两个子模块；`yudao-dependencies` 加版本管理（可选）
- **SQL**：把 `sql/postgresql` 里 amf 的建表 + 菜单脚本按 cloud 库导入

> 迁移时用 `git diff` 对照单体 `yudao-module-amf` 逐个文件搬，改包名（可选）/注解差异。

### 2️⃣ 钉钉登录 → 搬进 cloud 的 system-biz

单体 `yudao-module-system` 里的登录改动，对应 cloud 的 `yudao-module-system-biz`：

- `controller/admin/auth/AuthController.java` → 合并 `GET /system/auth/dingtalk/authorize-url`、`GET /system/auth/dingtalk/callback`
- `service/oauth2/DingTalkOAuthService.java` → 搬入 `system-biz`
- `framework/oauth2/DingTalkOAuthProperties.java` → 搬入 `system-biz`，注册进配置
- **配置**：`dingtalk.oauth2.*`（client-id/secret/redirect-uri/frontend-url）→ 写入 cloud 的 Nacos 配置或对应 yaml
- 依赖：确保 `system-biz` 的 pom 含钉钉相关依赖（HTTP 客户端等）

### 3️⃣ reagent 模块（可选，第二批）

同 AMF 方式：`yudao-module-reagent-api` + `-biz` + 启动类 + SQL。

### 4️⃣ 全局注意事项

- cloud 的 `yudao-framework` 基类/注解（如 `@PreAuthorize`、`CommonResult`、`TenantUtils`）包路径可能与单体略有差异，`import` 按 cloud 改
- 服务间调用改用 `-api` 的 RPC（Feign）而非本地直接调用；AMF/reagent 若不依赖其它业务模块则基本不受影响
- OnlyOffice 回调地址、文件存储路径等配置按 cloud 的 nacos/环境变量重配

### 📌 数据库：直接复用现有库（推荐）

- yudao-cloud **默认多服务共用一个数据库**，靠表前缀区分模块（`system_`/`infra_`/`bpm_`/`amf_`/`reagent_`…）
- 现有 PostgreSQL `xining` 库**直接沿用**：用户/菜单/权限/租户/AMF/reagent 数据原样可用，无需重新初始化或导数据
- 所有服务在同一主机 → 数据库连接用本机/内网（`127.0.0.1` 或 docker `172.17.0.1`/`host.docker.internal`），无跨机损耗
- ⚠️ 注意：
  1. **版本增量 SQL**：单体 2026.06 → cloud 2026.07 可能有新增表/字段，用增量方式补（先备份，对比 cloud `sql/` 的增量 DDL），**不要覆盖式导入**
  2. **连接数**：N 个服务 × 各自连接池，同时连接数翻倍；调大 PG `max_connections` 或调小各服务连接池（如 `maximum-pool-size: 10`）
  3. 不要用 cloud 建库脚本覆盖现有库（那是给新装用的）

---

## 四、前端 / App（不用改代码）

- 管理后台 `yudao-ui-admin-vue3-xn` 与手机端 `yudao-ui-admin-uniapp` **与 cloud 共用同一套**，代码无需改动。
- AMF 页面已在管理后台 `src/views/amf/`，直接可用。
- 生产环境：**只改 nginx**，把 `/admin-api/` 代理目标从 `yudao-server:48080` 改为 **Gateway**（如 `yudao-gateway:48080`）。前端 dist 无需重新构建。
- 本地/测试环境：把 `VITE_BASE_URL`（xn）/ `VITE_SERVER_BASEURL`（uniapp）指向 Gateway。

---

## 五、部署（cloud 版 docker-compose）

参考内存规划（现有开启模块：system + infra + bpm + mes + amf + reagent）：

| 服务 | 建议 JVM（-Xmx） |
|---|---|
| Nacos（注册+配置中心） | 512M |
| Gateway | 256M |
| system-biz | 512M~1G |
| infra-biz | 256~512M |
| bpm-biz（Flowable） | 512M~1G |
| mes-biz | 256~512M |
| amf-biz | 256~512M |
| reagent-biz（可选） | 256~512M |

- 服务器建议 **16G**，底线 **8G**（砍 bpm/OnlyOffice 可压）
- 新增中间件：Nacos（+可选 Sentinel）
- 数据库仍用宿主机 PostgreSQL（共用库），Redis 不变
- nginx 代理目标改为 Gateway

**启动顺序**：Nacos → Redis/PostgreSQL → Gateway → 各 biz 服务 → nginx 前端。

---

## 六、验证与回归清单

- [ ] `mvn clean install -Dmaven.test.skip=true` 全模块编译通过
- [ ] 各服务启动成功，注册进 Nacos
- [ ] 账号密码登录 / 短信登录 / 验证码
- [ ] **钉钉扫码登录**（authorize-url → 回调 → oauth 落地页拿 token）
- [ ] 菜单/权限：登录后管理后台按权限显示 AMF 菜单
- [ ] AMF 核心流程：业务列表 CRUD + OnlyOffice 在线编辑（上传/下载/回调）
- [ ] 前端两端走 Gateway 代理正常，无跨域/404
- [ ] 后端日志（挂载 `logs/server`）、文件上传路径正常

---

## 七、风险与注意

1. **版本追平**：单体 2026.06 → cloud 2026.07+，框架 API 差异需逐个适配
2. **mes 模块**：cloud 版可能没有 mes，需确认取舍（先砍或后续补）
3. **只做代码移植 + 编译验证**：完整跑通需在服务器/本地起 Nacos+Gateway+DB
4. **数据**：现有库结构 cloud 基本兼容，业务数据可直接沿用，迁移前务必备份
5. **不是把单体改成 cloud**：本方案是「另起 cloud + 移植定制」，现有单体保持可回滚

---

## 八、时间线估算（仅定制部分）

| 项 | 量级 |
|---|---|
| clone cloud + 版本对齐 | 0.5 天 |
| AMF 拆 api/biz 移植 | 2~4 天 |
| 钉钉登录搬入 system-biz | 1~2 天 |
| SQL / 配置 / nacos | 1~2 天 |
| docker-compose 编排 | 1~2 天 |
| 前端切 Gateway + 回归 | 1~2 天 |

**合计约 1~2 周**（不含 reagent：+1~2 天）。

---

*本文档为搬迁参考，具体文件级清单在移植时按 `git diff` 逐个产出。*
