# PMS 知识库权限 v3：目录锚点模型（设计与上线方案）

> **设计草案**：记录候选方案，当前版本未实施。

> 本文取代 `docs/pms-knowledge-acl-redesign.md`（v2：每对象 ACL 节点 + 13 位 + DENY + 作用域）。
> v3 的目标是：**权限按目录配置、文档跟随目录、移动按位置生效、底层可扩展**，并且能直接上测试/生产。

## 0. 已确认的业务口径

| 问题 | 结论 |
| --- | --- |
| 文档是否单独配权限 | **现阶段不需要**。文档跟随目录；分享链接作为“单份文档给特定人看”的替代 |
| 是否允许“看不到目录也能看文档” | **不允许**。目录不可见，其下文档一律不可达 |
| 只读但不许下载 | **需要**。用独立角色 `查看`（不可下载）；能下载用 `查看+下载` |
| 评论 | 跟随 `查看`，不单独设权限 |
| 打印 | 跟随 `下载`，与下载同一个角色 |
| 分享 | 仍是**链接分享（只读）**，不是加协作者；统一为仅 `管理` 可创建/关闭 |
| 移动到别的目录 | 一律**按新位置**解析；被移动目录自己的 ACL 清除（更深层目录自己的 ACL 保留），见 §4.2 |
| 库根默认 | 所有能进库的人默认 **`查看`（不可下载）**，可预览、可评论 |
| 权限粒度 | 目录/库上 **4 个固定角色**：查看 / 查看+下载 / 编辑 / 管理；不再暴露 13 位 |

## 1. 模型：目录是权限锚点

- 只有 **知识库（LIBRARY）** 和 **目录（FOLDER）** 可以挂 ACL，称为**锚点**。
- 文档（DOCUMENT）默认没有 ACL，权限由**离它最近的锚点**决定。
- 目录没有 ACL → 用上级锚点；有 ACL → 它和其下所有内容都用它，**不与上级合并**（最近优先，不是并集）。
- 库根永远有一个 ACL，作为兜底锚点。
- 解析器从第一天就支持“文档自己若有 ACL 则优先用文档的”，但当前 API/UI 不创建文档 ACL —— 这就是将来的扩展点，不需要再改解析规则。

这样同时满足：

- 目录批量配置；改目录立刻影响其下全部内容；
- 新建内容自动跟目录，不需要复制权限；
- 移动后按位置解析（文档、未设权限目录）；
- 不需要 DENY、不需要 path、不需要每个文档一条 ACL。

## 2. 角色与操作位

对外只有 **4 个固定角色**，内部仍用 13 个操作位做服务端校验（兼容现有 `require(action)`）。

| 角色 | 内部位 | 说明 |
| --- | --- | --- |
| `VIEWER` 查看 | `VIEW` + `INTERACT` | 看、预览、评论/点赞/关注；**不能下载/打印** |
| `DOWNLOADER` 查看+下载 | `VIEW` `INTERACT` `DOWNLOAD` `PRINT` | 在查看基础上可下载、可打印 |
| `EDITOR` 编辑 | `VIEW` `INTERACT` `DOWNLOAD` `PRINT` `CREATE` `EDIT` `DELETE` `MOVE` `REVIEW` | 新增/编辑/删除/移动/提交审阅；含下载 |
| `MANAGER` 管理 | `ALL`（含 `MANAGE` `SHARE` `PURGE` `EXPORT`） | 改权限、链接分享、彻底删除、导出 |

附加规则：

- 角色顺序：`VIEWER < DOWNLOADER < EDITOR < MANAGER`，同一级别命中多个时取最高角色。
- 评论/点赞/关注只要求 `VIEW`（内部 `INTERACT` 随 `VIEWER` 自动具备）。
- 打印只要求 `DOWNLOAD`，所以 `DOWNLOADER` 及以上可打印。
- 分享（创建/关闭链接）只要求 `SHARE`，即只有 `MANAGER`；链接本身只读，收件人不需要 ACL。
- 管理员（库创建人/库管理员/超级管理员）固定 `ALL`，不受目录 ACL 限制。

## 3. 有效权限解析

```
effective(object, user):
    if 用户不可读 object.library: return 0
    if 用户是库管理员: return ALL
    acl = resolveAcl(object)                  # 最近锚点
    if acl == null: return 0
    entry = 匹配 acl 中该用户的授权（见 §3.1）
    if entry == null: return 0
    return roleBits(entry.role)
```

### 3.1 同一 ACL 内多条命中时的优先级

同一个用户可能同时命中 `USER`、所属 `DEPT`、`ROLE`、`EVERYONE`。规则：**越具体越优先**，同级别取最高角色。

```
USER > DEPT > ROLE > EVERYONE
```

例：部门给“编辑”，单独给张三“查看” → 张三只读（因为 `USER` 比 `DEPT` 具体）。这样不需要 DENY 也能做“某人例外收窄”。

### 3.2 锚点解析

```
resolveAcl(FOLDER f):
    for cur = f; cur != null; cur = parent(cur):
        acl = aclMapper.selectByObject('FOLDER', cur.id)
        if acl != null: return acl
        if cur.parentId == 0: break
    return aclMapper.selectByObject('LIBRARY', f.libraryId)   # 必然存在

resolveAcl(DOCUMENT d):
    acl = aclMapper.selectByObject('DOCUMENT', d.id)          # 现阶段恒为空，为将来预留
    if acl != null: return acl
    folderId = d.folderId; parentId = d.parentId
    while folderId == 0 && parentId > 0:
        p = docMapper.selectById(parentId)
        folderId = p.folderId; parentId = p.parentId
    return folderId > 0 ? resolveAcl(folder(folderId)) : libraryAcl(d.libraryId)
```

目录树、搜索、收藏、最近访问、回收站都复用同一解析函数；列表场景按 `folderId` 分组解析一次，避免逐条查库。

## 4. 生命周期

### 4.1 创建

- 建库：同一事务创建库根 ACL，默认一条 `EVERYONE / VIEWER`（= 成员进门即可查看、可评论，但**默认不能下载**；需要下载时给 `查看+下载` 或更高角色）；库创建人仍由管理员身份获得 `ALL`。
- 建目录：**不创建 ACL**，自然跟随上级锚点。
- 建文档/上传：**不创建 ACL**，跟随所在目录/父文档的锚点。
- 导入内容包/复制：同样不复制源 ACL，落在目标位置后跟随目标锚点。

### 4.2 移动

- 文档：只改位置，权限自动按新位置解析。
- 目录：移动时**清除该目录自己的 ACL**，之后跟随新父目录；其子目录/子文档中“自己设过 ACL”的更深层锚点**保留**（它们是显式配置，不因上级移动被清除）。
- 跨知识库移动：
  - 目标库的准入仍然生效；
  - 被移动目录自己的 ACL 清除，并按目标库根 ACL 解析；
  - 前端二次确认，提示“将按目标知识库根目录权限生效”；
  - 写审计。
- 移动本身不再修改 `permission_id`，也不再搬运授权记录。

### 4.3 回收站与删除

- 删除到回收站：ACL 保留；恢复后仍按原位置/原 ACL。
- 恢复时父目录已删除：挂到库根，按库根 ACL（若自身有 ACL 则保留）。
- 彻底删除：删除子树内所有 ACL 与授权，写审计。

### 4.4 分享

- 仍是现有 `pms_knowledge_document_share`：令牌 + 可选访问码 + 有效期，只读；
- 创建/更新/关闭要求文档有效权限含 `SHARE`（`MANAGER`）；
- 分享链接访问不要求登录/库成员，也不参与 ACL 解析（保持现状），这是“单份文档给外部看”的唯一通道；
- 链接被转发即可访问（除非访问码/有效期），文档需在说明中提示。

## 5. 数据模型

### 5.1 新表

```sql
CREATE TABLE pms_knowledge_acl (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    library_id BIGINT NOT NULL,
    object_type VARCHAR(16) NOT NULL CHECK (object_type IN ('LIBRARY','FOLDER','DOCUMENT')),
    object_id BIGINT NOT NULL,
    version INT NOT NULL DEFAULT 0,
    creator VARCHAR(64), create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64), update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX pms_knowledge_acl_object_uk
    ON pms_knowledge_acl (tenant_id, object_type, object_id) WHERE deleted = 0;
CREATE INDEX pms_knowledge_acl_library_idx ON pms_knowledge_acl (tenant_id, library_id);

CREATE TABLE pms_knowledge_acl_entry (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    acl_id BIGINT NOT NULL REFERENCES pms_knowledge_acl(id),
    principal_type VARCHAR(16) NOT NULL CHECK (principal_type IN ('EVERYONE','USER','DEPT','ROLE')),
    principal_id BIGINT,
    role VARCHAR(16) NOT NULL CHECK (role IN ('VIEWER','DOWNLOADER','EDITOR','MANAGER')),
    creator VARCHAR(64), create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64), update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    CONSTRAINT pms_knowledge_acl_entry_principal_ck CHECK
        ((principal_type = 'EVERYONE' AND principal_id IS NULL)
      OR (principal_type <> 'EVERYONE' AND principal_id IS NOT NULL))
);
CREATE UNIQUE INDEX pms_knowledge_acl_entry_principal_uk
    ON pms_knowledge_acl_entry (tenant_id, acl_id, principal_type, principal_id)
    WHERE deleted = 0 AND principal_id IS NOT NULL;
CREATE UNIQUE INDEX pms_knowledge_acl_entry_everyone_uk
    ON pms_knowledge_acl_entry (tenant_id, acl_id)
    WHERE deleted = 0 AND principal_type = 'EVERYONE';

CREATE TABLE pms_knowledge_acl_audit (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    library_id BIGINT NOT NULL,
    acl_id BIGINT,
    object_type VARCHAR(16),
    object_id BIGINT,
    action VARCHAR(32) NOT NULL,       -- CREATE_ACL / UPDATE_ENTRIES / MOVE_TREE / DELETE_ACL
    operator_id BIGINT,
    before_snapshot TEXT,
    after_snapshot TEXT,
    creator VARCHAR(64), create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64), update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0
);
CREATE INDEX pms_knowledge_acl_audit_acl_idx ON pms_knowledge_acl_audit (acl_id);
CREATE INDEX pms_knowledge_acl_audit_library_idx ON pms_knowledge_acl_audit (library_id);
```

说明：

- `object_type` 允许 `DOCUMENT` 是为将来“文档锚点”预留；当前 API/UI 只允许 `LIBRARY`/`FOLDER`。
- 不再有 `parent_id` / `path` / `depth` / `inherit_parent` / `deny_bits`。
- `version` 只用于 ACL 保存并发控制。

### 5.2 与现有表的关系

- 现有 `pms_knowledge_acl_node` / `pms_knowledge_acl_grant` / `pms_knowledge_acl_audit`（v2）保留到验收后；
  迁移完成后新代码不再读写它们，验收通过再由下线脚本改名备份。
- `pms_knowledge_folder.permission_id` / `pms_knowledge_document.permission_id` 不再参与鉴权；
  迁移后建议置 0 或保留旧值（代码忽略），验收后删除列。
- 旧 `pms_knowledge_content_permission*`（首版共享权限）同样在验收后下线。

## 6. API 与前端

### 6.1 接口

- `GET /pms/kb/acl/get?objectType=LIBRARY|FOLDER&objectId=`：返回
  `{ id, libraryId, objectType, objectId, version, effectivePermissionBits, entries? }`
  - `entries` 仅在当前用户有 `MANAGE` 时返回；非管理员只返回有效位，不泄露成员名单。
  - 传 `DOCUMENT` 返回业务错误“文档权限跟随目录，请在目录上配置”。
- `PUT /pms/kb/acl/update`：
  `{ objectType, objectId, expectedVersion, entries: [{ principalType, principalId, role }] }`
  - `role` 只能是 `VIEWER / DOWNLOADER / EDITOR / MANAGER`。
  - 要求当前 ACL（或其上级）上的 `MANAGE`；版本不一致返回冲突。
- `GET /pms/kb/acl/audit-list?aclId=` 或 `?libraryId=`：`MANAGE`/库管理员可查。
- 文档详情、目录树、搜索继续返回 `effectivePermissionBits`；文档 VO 不再返回 `permissionId` 作为权限入口（可保留字段但前端不用）。

### 6.2 前端

- 目录详情、库主页“根目录权限”打开同一个 ACL 编辑器；
- 文档详情**隐藏**“协作/权限”入口；
- 编辑器只保留：主体（全员/用户/部门/角色）+ 角色下拉（查看 / 查看+下载 / 编辑 / 管理）；没有单独的下载开关；
- 保留“变更记录”；去掉“继承/独占开关”“上级来源”“13 位勾选”“拒绝列”；
- 移动弹窗：文档不再提示权限重置；跨库移动目录提示“将按目标库根目录权限生效”；
- 回收站：恢复按钮要求 `EDIT`，彻底删除要求 `PURGE`（`MANAGER`）。

## 7. 迁移方案（可直接执行）

### 7.1 顺序

1. 备份知识库相关表（`pg_dump -Fc -t 'public.pms_knowledge*'`）。
2. 执行建表 + 回填脚本（新文件 `sql/postgresql/pms-knowledge-permission-v3.sql`）。
3. 跑自检 SQL（见 7.3），全部符合预期。
4. 发布后端 + 前端（同版）。
5. 双账号验收。
6. 验收通过后执行下线脚本（改名备份，不直接 DROP）。

### 7.2 回填规则

- 每个 `LIBRARY` 节点 → `pms_knowledge_acl` 一条；确保每个库都有根 ACL，缺失则补一条
  `EVERYONE / VIEWER`。
- 每个 `FOLDER` 节点 → `pms_knowledge_acl` 一条。
- 每条 `pms_knowledge_acl_grant`：
  - `effective = allow_bits & ~deny_bits`；`effective = 0` 的记录跳过；
  - `role = MANAGER`（含 `MANAGE`）
    / `EDITOR`（含 `CREATE|EDIT|DELETE|MOVE|REVIEW` 任一）
    / `DOWNLOADER`（含 `DOWNLOAD|PRINT`）
    / 否则 `VIEWER`。
- `DOCUMENT` 节点：**一律丢弃**，文档改为跟随所属目录（按业务确认“直接按新目录继承”）；迁移脚本输出被丢弃的文档授权数量/清单备查，不中止。
- 审计：`LIBRARY/FOLDER` 的历史审计迁到新表；`DOCUMENT` 审计保留在旧表备份中。

### 7.3 自检 SQL（必须全部符合）

```sql
-- 每个库必须有 ACL
SELECT count(*) FROM pms_knowledge_library l
 LEFT JOIN pms_knowledge_acl a ON a.object_type='LIBRARY' AND a.object_id=l.id AND a.deleted=0
 WHERE l.deleted=0 AND a.id IS NULL;                       -- 期望 0

-- 有授权的文档必须与所属目录 ACL 一致（不一致需人工处理）
-- 见脚本内详细对比 SQL

-- 新表角色/开关分布
SELECT role, count(*) FROM pms_knowledge_acl_entry WHERE deleted=0 GROUP BY 1;

-- 每个 ACL 至少一条 entry（根 ACL 允许 EVERYONE）
SELECT count(*) FROM pms_knowledge_acl a
 WHERE a.deleted=0 AND NOT EXISTS (SELECT 1 FROM pms_knowledge_acl_entry e WHERE e.acl_id=a.id AND e.deleted=0);
```

### 7.4 回滚

- 新表保留、旧表未动：回滚应用版本即可恢复旧逻辑；
- 若已产生新数据，回滚前用备份恢复旧 ACL 表；新表可保留不影响旧版本。

## 8. 并发、审计、性能、安全

- **并发**：保存 ACL 先 `SELECT … FOR UPDATE` 锁 ACL 行，校验 `version`，条件更新 `version=version+1`；旧页面保存返回冲突。
- **审计**：ACL 创建/修改/移动/删除都写快照（主体、角色、下载开关、对象、操作人）。
- **性能**：
  - 单资源解析最多走“目录深度”次查询，通常 1~3 次；
  - 列表/树按目录分组，每个目录只解析一次；
  - 同一请求内缓存 `aclId → 条目`；
  - 不在文档上存 ACL，行数从“对象数”降到“目录数”。
- **安全**：
  - 不能通过文档绕过目录可见性；
  - 分享链接是显式的只读外发通道，需 `SHARE`（`MANAGER`）；
  - 管理员全权，无法限制管理员查看（如需，需另做“管理员也要受限”的需求，不在本期）；
  - 权限失败日志只记录对象编号、动作、用户编号，不记录标题。

## 9. 功能对照矩阵（确保不缺失）

| 功能/入口 | 要求 |
| --- | --- |
| 库列表、目录树、搜索、标签、收藏、最近访问、互动列表 | 每条结果的 `VIEW`（解析最近锚点） |
| 文档详情、属性、正文、预览、历史版本、审阅读取 | `VIEW` |
| 下载当前/历史文件 | `DOWNLOAD`（`VIEWER` 拒绝，`DOWNLOADER` 及以上允许） |
| 打印（OnlyOffice） | `DOWNLOAD` |
| 在库根/目录/父文档下新建、上传 | 父对象 `CREATE` |
| 重命名、编辑正文/属性、上传新版本、提交审阅 | 当前对象 `EDIT`（审阅含 `REVIEW`） |
| 删除（进回收站） | 当前对象 `DELETE` |
| 移动（同库/跨库） | 当前对象 `MOVE` + 目标父 `CREATE`；跨库额外确认 |
| 恢复 | 原对象 `EDIT` + 目标父 `CREATE` |
| 彻底删除（回收站） | 原对象 `PURGE`（`MANAGER`） |
| 修改 ACL | 当前 ACL `MANAGE`（`MANAGER`） |
| 创建/关闭分享链接 | 文档 `SHARE`（`MANAGER`） |
| 导入内容包 | 目标父 `CREATE`（库管理员仍可） |
| 导出内容包 | 根 ACL `EXPORT`（`MANAGER`）或库管理员 |
| 评论、点赞、关注 | `VIEW` |
| 库成员管理、公开/私有设置、审阅规则、属性/编号规则 | 库管理员（不变） |

## 10. 测试与验收

### 10.1 单元测试

- 锚点解析：目录有 ACL / 无 ACL / 多层目录 / 库根兜底；
- 文档：所在目录、父文档链、库根；
- 角色位映射：VIEWER / DOWNLOADER / EDITOR / MANAGER；下载与打印只在 DOWNLOADER 及以上；
- 命中优先级：USER > DEPT > ROLE > EVERYONE；同级并集；
- 管理员 `ALL`；非成员 0；目录不可见 → 子文档 0；
- 保存并发冲突；最后管理者保护（MANAGER 至少一条）；
- 移动：文档跟随、无 ACL 目录跟随、有 ACL 目录保留、跨库清空；
- 回收站恢复/彻底删除；审计落库。

### 10.2 验收矩阵

| # | 场景 | 预期 |
| --- | --- | --- |
| 1 | 公开库成员、根 ACL 默认 EVERYONE 查看 | 可预览、可评论，下载/打印拒绝；授予“查看+下载”后可下载 |
| 2 | 目录设“质量部编辑”，其他人无授权 | 质量部可编辑，其他人看不到该目录及内容 |
| 3 | 文档在不可见目录下 | 直达、搜索、下载均拒绝 |
| 4 | 角色为“查看”（不含下载） | 可预览、可评论，下载/打印拒绝 |
| 5 | 部门编辑 + 某人单独“查看” | 某人只读且不能下载（USER 覆盖 DEPT） |
| 6 | 新建子目录/文档 | 自动跟随最近锚点 |
| 7 | 移动未设权限目录到受限目录 | 立刻按新目录 |
| 8 | 移动自己设过权限的目录 | 清除自身 ACL，按新父目录；更深层自己的 ACL 保留 |
| 9 | 跨库移动 | 清除被移动目录自己的 ACL，按目标库根；更深层自己的 ACL 保留；二次确认 |
| 10 | 彻底删除/恢复 | 按 MANAGER/EDITOR 校验 |
| 11 | 分享链接 | MANAGER 创建；外部只读；撤销/过期失效 |
| 12 | 并发保存 ACL | 后者冲突，不覆盖 |
| 13 | 审计 | 可查操作人、时间、前后差异 |

### 10.3 证据

- 后端编译、`PmsKnowledge*Test` 全绿；
- 迁移脚本在演练库执行 + 自检 SQL；
- 前端 `pnpm ts:check` 中 pms/kb 无错误；
- 浏览器双账号回归（两库四角色、移动、下载角色、分享）。

## 11. 风险与边界

1. **文档级授权被取消**：迁移时文档授权一律丢弃，按所属目录解析（已确认）；将来若需要，启用“文档锚点”即可。
2. **没有 DENY**：收窄靠“更具体的角色覆盖”（USER 覆盖 DEPT）或单独建受限目录；无法表达“排除某个部门中的某个人”之外的复杂规则。
3. **移动会清除被移动目录自身的 ACL**：移动后按新父目录解析；更深层目录自己的 ACL 保留。跨库移动还会受目标库准入影响，前端必须二次确认。
4. **分享链接可被转发**：这是现有分享的固有属性；需要严格控制时用访问码/有效期。
5. **管理员不受目录限制**：与现状一致；若合规要求“管理员也不能看”，需单独立项。
6. **域名/字段命名**：v3 新表与 v2 并存到验收，期间不要同时写入两套。

## 12. 实施顺序

1. 评审本文，确认 §0 口径与 §4.2 移动规则。
2. 编写 `pms-knowledge-permission-v3.sql`（建表 + 回填 + 自检）与下线脚本。
3. 改服务端：`PmsKnowledgeAclService` 解析/保存/移动/审计；业务入口改为按对象解析；移除文档 ACL 创建。
4. 改前端：目录/库 ACL 编辑器、隐藏文档权限入口、移动提示、回收站按角色。
5. 迁移演练 + 单元测试；发布测试环境验收。
6. 验收通过后下线 v2/首版表。
