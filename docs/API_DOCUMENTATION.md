# 创新创业平台 API 文档

## 概述

本文档描述了创新创业平台的 RESTful API 接口。所有 API 统一返回格式如下：

```json
{
  "code": 200,        // 状态码：200-成功，其他-失败
  "message": "操作成功", // 响应消息
  "data": {}          // 响应数据（类型根据接口不同而变化）
}
```

### 基础信息

- **基础URL**: `/api`
- **Content-Type**: `application/json`
- **认证方式**: JWT Token (通过请求头 `Authorization: Bearer {token}` 传递)

### 角色说明

| 角色代码 | 说明 |
|---------|------|
| `STUDENT` | 学生 |
| `TEACHER` | 教师 |
| `COLLEGE_ADMIN` | 学院管理员 |
| `SCHOOL_ADMIN` | 学校管理员 |

### 分页参数

列表查询接口支持分页，统一使用以下参数：

| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| `pageNum` | Integer | 否 | 当前页码，默认1 |
| `pageSize` | Integer | 否 | 每页大小，默认10 |

分页响应格式：

```json
{
  "pageNum": 1,
  "pageSize": 10,
  "total": 100,
  "totalPages": 10,
  "list": []
}
```

---

## 1. 认证接口

### 1.1 用户登录

- **URL**: `POST /api/auth/login`
- **权限**: 无限制
- **请求体**:

```json
{
  "username": "string",  // 必填，用户名
  "password": "string"   // 必填，密码
}
```

- **响应**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "user": {
      "id": 1,
      "username": "zhangsan",
      "realName": "张三",
      "email": "zhangsan@example.com",
      "phone": "13800138000",
      "role": "STUDENT",
      "collegeId": 1,
      "collegeName": "计算机学院"
    }
  }
}
```

### 1.2 用户注册

- **URL**: `POST /api/auth/register`
- **权限**: 无限制
- **请求体**:

```json
{
  "username": "string",      // 必填，用户名
  "password": "string",      // 必填，密码（至少6位，包含字母和数字）
  "realName": "string",      // 必填，真实姓名
  "email": "string",         // 可选，邮箱格式
  "phone": "string",         // 可选，手机号格式
  "role": "string",          // 必填，角色：STUDENT/TEACHER/COLLEGE_ADMIN/SCHOOL_ADMIN
  "collegeId": 1             // 可选，所属学院ID
}
```

---

## 2. 用户管理接口

### 2.1 获取当前用户信息

- **URL**: `GET /api/users/me`
- **权限**: 登录用户
- **响应**: `LoginUserDTO` 用户信息

### 2.2 修改当前用户密码

- **URL**: `PUT /api/users/me/password`
- **权限**: 登录用户
- **请求体**:

```json
{
  "oldPassword": "string",  // 原密码
  "newPassword": "string"   // 新密码
}
```

### 2.3 获取用户列表

- **URL**: `GET /api/users`
- **权限**: 登录用户
- **查询参数**:
  - `pageNum`: 页码
  - `pageSize`: 每页大小
  - `username`: 用户名（可选，模糊查询）
  - `realName`: 真实姓名（可选，模糊查询）
  - `role`: 角色（可选）
  - `collegeId`: 学院ID（可选）
  - `status`: 状态 0-禁用 1-启用（可选）

### 2.4 创建用户

- **URL**: `POST /api/users`
- **权限**: 管理员
- **请求体**: `CreateUserDTO` 用户创建信息

### 2.5 获取用户详情

- **URL**: `GET /api/users/{id}`
- **权限**: 登录用户
- **路径参数**: `id` - 用户ID

### 2.6 更新用户信息

- **URL**: `PUT /api/users/{id}`
- **权限**: 管理员
- **路径参数**: `id` - 用户ID
- **请求体**: `User` 用户信息

### 2.7 更新用户状态

- **URL**: `PUT /api/users/{id}/status`
- **权限**: 管理员
- **路径参数**: `id` - 用户ID
- **请求体**:

```json
{
  "status": 1  // 0-禁用，1-启用
}
```

### 2.8 重置用户密码

- **URL**: `PUT /api/users/{id}/password/reset`
- **权限**: 管理员
- **路径参数**: `id` - 用户ID
- **请求体**:

```json
{
  "newPassword": "string"
}
```

### 2.9 删除用户

- **URL**: `DELETE /api/users/{id}`
- **权限**: 管理员
- **路径参数**: `id` - 用户ID

### 2.10 批量导入用户

- **URL**: `POST /api/users/import`
- **权限**: 学院管理员、学校管理员
- **Content-Type**: `multipart/form-data`
- **请求参数**:
  - `file`: Excel文件（.xlsx 或 .xls）

---

## 3. 活动管理接口

### 3.1 创建活动申报

- **URL**: `POST /api/activities`
- **权限**: 学生、教师
- **请求体**: `ActivityDTO` 活动信息

### 3.2 更新活动申报

- **URL**: `PUT /api/activities/{id}`
- **权限**: 学生、教师、学院管理员、学校管理员
- **路径参数**: `id` - 活动ID
- **请求体**: `ActivityDTO` 活动信息

### 3.3 上传活动海报

- **URL**: `POST /api/activities/{id}/poster`
- **权限**: 学院管理员、学校管理员
- **Content-Type**: `multipart/form-data`
- **路径参数**: `id` - 活动ID
- **请求参数**:
  - `file`: 图片文件

### 3.4 提交活动申报

- **URL**: `POST /api/activities/{id}/submit`
- **权限**: 学生、教师
- **路径参数**: `id` - 活动ID

### 3.5 学院管理员初审

- **URL**: `POST /api/activities/{id}/college-review`
- **权限**: 学院管理员、学校管理员
- **路径参数**: `id` - 活动ID
- **请求体**:

```json
{
  "approvalStatus": "APPROVED",  // APPROVED-通过，REJECTED-驳回
  "reviewComment": "string"      // 审核意见
}
```

### 3.6 学校管理员终审

- **URL**: `POST /api/activities/{id}/school-review`
- **权限**: 学校管理员、学院管理员
- **路径参数**: `id` - 活动ID
- **请求体**:

```json
{
  "approvalStatus": "APPROVED",
  "reviewComment": "string"
}
```

### 3.7 分页查询活动

- **URL**: `GET /api/activities`
- **权限**: 登录用户
- **查询参数**:
  - `pageNum`: 页码
  - `pageSize`: 每页大小
  - `status`: 活动状态（可选）
  - `approvalStatus`: 审批状态（可选）
  - `activityTypeId`: 活动类型ID（可选）
  - `keyword`: 关键词搜索（可选）

### 3.8 查询活动详情

- **URL**: `GET /api/activities/{id}`
- **权限**: 登录用户
- **路径参数**: `id` - 活动ID

### 3.9 活动报名

- **URL**: `POST /api/activities/{id}/register`
- **权限**: 学生、教师
- **路径参数**: `id` - 活动ID
- **请求体**:

```json
{
  "contactPhone": "string",  // 联系电话
  "email": "string",         // 邮箱
  "remark": "string"         // 备注
}
```

### 3.10 提交活动总结

- **URL**: `POST /api/activities/{id}/summary`
- **权限**: 学生、教师
- **路径参数**: `id` - 活动ID
- **请求体**: `ActivitySummary` 活动总结信息

### 3.11 获取活动总结

- **URL**: `GET /api/activities/{id}/summary`
- **权限**: 学生、教师、学院管理员
- **路径参数**: `id` - 活动ID

### 3.12 审批活动总结

- **URL**: `POST /api/activities/summaries/{summaryId}/review`
- **权限**: 学院管理员
- **路径参数**: `summaryId` - 总结ID
- **请求体**:

```json
{
  "approvalStatus": "APPROVED",
  "reviewComment": "string"
}
```

### 3.13 获取我报名的活动

- **URL**: `GET /api/activities/my-registrations`
- **权限**: 学生、教师

### 3.14 分页获取所有活动总结

- **URL**: `GET /api/activities/summaries`
- **权限**: 学院管理员
- **查询参数**:
  - `pageNum`: 页码
  - `pageSize`: 每页大小

### 3.15 取消报名

- **URL**: `DELETE /api/activities/registrations/{id}`
- **权限**: 学生、教师
- **路径参数**: `id` - 报名记录ID

### 3.16 删除活动

- **URL**: `DELETE /api/activities/{id}`
- **权限**: 学校管理员
- **路径参数**: `id` - 活动ID

### 3.17 批量导入活动

- **URL**: `POST /api/activities/import`
- **权限**: 学院管理员、学校管理员
- **Content-Type**: `multipart/form-data`
- **请求参数**:
  - `file`: Excel文件

---

## 4. 新闻管理接口

### 4.1 创建新闻稿

- **URL**: `POST /api/news`
- **权限**: 学院管理员、学校管理员
- **请求体**: `News` 新闻信息

### 4.2 提交新闻稿

- **URL**: `POST /api/news/{id}/submit`
- **权限**: 学院管理员、学校管理员
- **路径参数**: `id` - 新闻ID

### 4.3 审核新闻

- **URL**: `POST /api/news/{id}/review`
- **权限**: 学校管理员
- **路径参数**: `id` - 新闻ID
- **请求体**:

```json
{
  "approvalStatus": "APPROVED",  // APPROVED-通过，REJECTED-驳回
  "reviewComment": "string"
}
```

### 4.4 分页查询新闻

- **URL**: `GET /api/news`
- **权限**: 登录用户
- **查询参数**:
  - `pageNum`: 页码
  - `pageSize`: 每页大小
  - `title`: 标题（可选，模糊查询）
  - `status`: 状态（可选）
  - `approvalStatus`: 审批状态（可选）
  - `categoryId`: 分类ID（可选）
  - `authorId`: 作者ID（可选）

### 4.5 查询新闻详情

- **URL**: `GET /api/news/{id}`
- **权限**: 登录用户
- **路径参数**: `id` - 新闻ID

### 4.6 更新新闻

- **URL**: `PUT /api/news/{id}`
- **权限**: 学院管理员、学校管理员
- **路径参数**: `id` - 新闻ID
- **请求体**: `News` 新闻信息

### 4.7 删除新闻

- **URL**: `DELETE /api/news/{id}`
- **权限**: 学院管理员、学校管理员
- **路径参数**: `id` - 新闻ID

### 4.8 软删除新闻

- **URL**: `DELETE /api/news/{id}/soft`
- **权限**: 学院管理员、学校管理员
- **路径参数**: `id` - 新闻ID

### 4.9 批量删除新闻

- **URL**: `DELETE /api/news/batch`
- **权限**: 学院管理员、学校管理员
- **请求体**:

```json
{
  "ids": [1, 2, 3]
}
```

---

## 5. 团队管理接口

### 5.1 创建团队

- **URL**: `POST /api/teams`
- **权限**: 学生、教师
- **请求体**: `Team` 团队信息

### 5.2 更新团队

- **URL**: `PUT /api/teams/{id}`
- **权限**: 学生、教师
- **路径参数**: `id` - 团队ID
- **请求体**: `Team` 团队信息

### 5.3 添加团队成员

- **URL**: `POST /api/teams/{id}/members`
- **权限**: 学生、教师
- **路径参数**: `id` - 团队ID
- **请求体**:

```json
{
  "userId": 1
}
```

### 5.4 移除团队成员

- **URL**: `DELETE /api/teams/{id}/members/{memberId}`
- **权限**: 学生、教师
- **路径参数**:
  - `id`: 团队ID
  - `memberId`: 成员ID

### 5.5 获取团队详情

- **URL**: `GET /api/teams/{id}`
- **权限**: 登录用户
- **路径参数**: `id` - 团队ID

### 5.6 获取团队成员

- **URL**: `GET /api/teams/{id}/members`
- **权限**: 登录用户
- **路径参数**: `id` - 团队ID

### 5.7 获取所有团队成员（包括待审批）

- **URL**: `GET /api/teams/{id}/members/all`
- **权限**: 登录用户
- **路径参数**: `id` - 团队ID

### 5.8 获取我的团队

- **URL**: `GET /api/teams/my`
- **权限**: 登录用户

### 5.9 获取所有团队

- **URL**: `GET /api/teams`
- **权限**: 登录用户

### 5.10 导出团队列表

- **URL**: `GET /api/teams/export`
- **权限**: 学院管理员、学校管理员
- **响应**: Excel文件下载

### 5.11 批量导入团队

- **URL**: `POST /api/teams/import`
- **权限**: 学院管理员、学校管理员
- **Content-Type**: `multipart/form-data`
- **请求参数**:
  - `file`: Excel文件

### 5.12 获取团队关联项目

- **URL**: `GET /api/teams/{id}/projects`
- **权限**: 登录用户
- **路径参数**: `id` - 团队ID

### 5.13 检查用户是否是团队成员

- **URL**: `GET /api/teams/{id}/members/check`
- **权限**: 登录用户
- **路径参数**: `id` - 团队ID
- **查询参数**:
  - `userId`: 用户ID

### 5.14 审批团队成员申请

- **URL**: `POST /api/teams/{id}/members/{memberId}/review`
- **权限**: 学生、教师
- **路径参数**:
  - `id`: 团队ID
  - `memberId`: 成员ID
- **请求体**:

```json
{
  "approvalStatus": "APPROVED"  // APPROVED-通过，REJECTED-拒绝
}
```

### 5.15 获取待审批成员申请

- **URL**: `GET /api/teams/{id}/members/pending`
- **权限**: 学生、教师
- **路径参数**: `id` - 团队ID

### 5.16 删除团队

- **URL**: `DELETE /api/teams/{id}`
- **权限**: 学院管理员、学校管理员
- **路径参数**: `id` - 团队ID

### 5.17 批量删除团队

- **URL**: `DELETE /api/teams/batch`
- **权限**: 学院管理员、学校管理员
- **请求体**:

```json
{
  "ids": [1, 2, 3]
}
```

---

## 6. 项目管理接口

### 6.1 创建项目

- **URL**: `POST /api/projects`
- **权限**: 学生、教师
- **请求体**: `Project` 项目信息

### 6.2 更新项目

- **URL**: `PUT /api/projects/{id}`
- **权限**: 学生、教师
- **路径参数**: `id` - 项目ID
- **请求体**: `Project` 项目信息

### 6.3 提交项目

- **URL**: `POST /api/projects/{id}/submit`
- **权限**: 学生、教师
- **路径参数**: `id` - 项目ID

### 6.4 审核项目

- **URL**: `POST /api/projects/{id}/review`
- **权限**: 学院管理员、学校管理员
- **路径参数**: `id` - 项目ID
- **请求体**:

```json
{
  "status": "APPROVED",
  "reviewComment": "string"
}
```

### 6.5 获取项目详情

- **URL**: `GET /api/projects/{id}`
- **权限**: 登录用户
- **路径参数**: `id` - 项目ID

### 6.6 获取我的项目

- **URL**: `GET /api/projects/my`
- **权限**: 登录用户

### 6.7 获取所有项目

- **URL**: `GET /api/projects`
- **权限**: 登录用户
- **查询参数**:
  - `status`: 项目状态（可选）

### 6.8 获取无人接管项目

- **URL**: `GET /api/projects/unclaimed`
- **权限**: 登录用户

### 6.9 更换项目负责人

- **URL**: `POST /api/projects/{id}/transfer-leader`
- **权限**: 学生、教师
- **路径参数**: `id` - 项目ID
- **请求体**:

```json
{
  "newLeaderUserId": 1
}
```

### 6.10 虚位以待（招募负责人）

- **URL**: `POST /api/projects/{id}/vacate-leader`
- **权限**: 学生、教师
- **路径参数**: `id` - 项目ID

### 6.11 删除项目

- **URL**: `DELETE /api/projects/{id}`
- **权限**: 学校管理员
- **路径参数**: `id` - 项目ID

---

## 7. 空间预约接口

### 7.1 查询空间列表

- **URL**: `GET /api/spaces`
- **权限**: 登录用户
- **查询参数**:
  - `status`: 空间状态（可选）AVAILABLE-可用, MAINTENANCE-维护中, DISABLED-已禁用

### 7.2 查询空间详情

- **URL**: `GET /api/spaces/{id}`
- **权限**: 登录用户
- **路径参数**: `id` - 空间ID

### 7.3 修改空间状态

- **URL**: `PUT /api/spaces/{id}/status`
- **权限**: 学院管理员、学校管理员
- **路径参数**: `id` - 空间ID
- **请求体**:

```json
{
  "status": "AVAILABLE"
}
```

### 7.4 查询空间预约

- **URL**: `GET /api/spaces/{id}/reservations`
- **权限**: 登录用户
- **路径参数**: `id` - 空间ID
- **查询参数**:
  - `date`: 日期（ISO格式，如 2024-01-01）

### 7.5 查询空间占用时段

- **URL**: `GET /api/spaces/{id}/occupied-slots`
- **权限**: 登录用户
- **路径参数**: `id` - 空间ID
- **查询参数**:
  - `date`: 日期（必填，ISO格式）

### 7.6 提交预约申请

- **URL**: `POST /api/spaces/reservations`
- **权限**: 学生、教师
- **请求体**: `SpaceReservation` 预约信息

### 7.7 取消预约

- **URL**: `DELETE /api/spaces/reservations/{id}`
- **权限**: 学生、教师
- **路径参数**: `id` - 预约ID

### 7.8 查询我的预约

- **URL**: `GET /api/spaces/reservations/my`
- **权限**: 登录用户

### 7.9 审核预约申请

- **URL**: `POST /api/spaces/reservations/{id}/review`
- **权限**: 学院管理员、学校管理员
- **路径参数**: `id` - 预约ID
- **请求体**:

```json
{
  "approvalStatus": "APPROVED",
  "reviewComment": "string"
}
```

### 7.10 查询待审核预约

- **URL**: `GET /api/spaces/reservations/pending`
- **权限**: 学院管理员、学校管理员

### 7.11 按状态查询预约（管理员）

- **URL**: `GET /api/spaces/reservations/admin`
- **权限**: 所有登录用户
- **查询参数**:
  - `status`: 预约状态（可选）

---

## 8. 人员库接口

### 8.1 分页查询人员列表

- **URL**: `GET /api/persons`
- **权限**: 登录用户
- **查询参数**:
  - `pageNum`: 页码
  - `pageSize`: 每页大小
  - `personTypeId`: 人员类型ID（可选）
  - `keyword`: 关键词（可选）

### 8.2 查询人员详情

- **URL**: `GET /api/persons/{id}`
- **权限**: 登录用户
- **路径参数**: `id` - 人员ID

### 8.3 添加人员

- **URL**: `POST /api/persons`
- **权限**: 学院管理员、学校管理员
- **请求体**: `PersonLibrary` 人员信息

### 8.4 更新人员

- **URL**: `PUT /api/persons/{id}`
- **权限**: 学院管理员、学校管理员
- **路径参数**: `id` - 人员ID
- **请求体**: `PersonLibrary` 人员信息

### 8.5 删除人员

- **URL**: `DELETE /api/persons/{id}`
- **权限**: 学院管理员、学校管理员
- **路径参数**: `id` - 人员ID

### 8.6 批量导入人员

- **URL**: `POST /api/persons/import`
- **权限**: 学院管理员、学校管理员
- **Content-Type**: `multipart/form-data`
- **请求参数**:
  - `file`: Excel文件

---

## 9. 入驻申请接口

### 9.1 创建入驻申请

- **URL**: `POST /api/entry-applications`
- **权限**: 学生、教师
- **请求体**: `EntryApplication` 入驻申请信息

### 9.2 提交入驻申请

- **URL**: `POST /api/entry-applications/{id}/submit`
- **权限**: 学生、教师
- **路径参数**: `id` - 申请ID

### 9.3 审核入驻申请

- **URL**: `POST /api/entry-applications/{id}/review`
- **权限**: 学院管理员、学校管理员
- **路径参数**: `id` - 申请ID
- **请求体**:

```json
{
  "approvalStatus": "APPROVED",
  "reviewComment": "string"
}
```

### 9.4 确认入驻

- **URL**: `POST /api/entry-applications/{id}/confirm-entry`
- **权限**: 学校管理员
- **路径参数**: `id` - 申请ID

### 9.5 退出入驻

- **URL**: `POST /api/entry-applications/{id}/exit`
- **权限**: 学校管理员
- **路径参数**: `id` - 申请ID
- **请求体**:

```json
{
  "exitReason": "string"
}
```

### 9.6 分页查询入驻申请

- **URL**: `GET /api/entry-applications`
- **权限**: 登录用户
- **查询参数**:
  - `pageNum`: 页码
  - `pageSize`: 每页大小
  - `teamName`: 团队名称（可选）
  - `status`: 状态（可选）
  - `approvalStatus`: 审批状态（可选）
  - `applicationType`: 申请类型（可选）
  - `applicantId`: 申请人ID（可选）

### 9.7 查询入驻申请详情

- **URL**: `GET /api/entry-applications/{id}`
- **权限**: 登录用户
- **路径参数**: `id` - 申请ID

### 9.8 更新入驻申请

- **URL**: `PUT /api/entry-applications/{id}`
- **权限**: 学生、教师
- **路径参数**: `id` - 申请ID
- **请求体**: `EntryApplication` 入驻申请信息

### 9.9 删除入驻申请

- **URL**: `DELETE /api/entry-applications/{id}`
- **权限**: 学生、教师
- **路径参数**: `id` - 申请ID

### 9.10 查询我的入驻申请

- **URL**: `GET /api/entry-applications/my`
- **权限**: 登录用户

---

## 10. 基金申请接口

### 10.1 创建基金申请

- **URL**: `POST /api/fund-applications`
- **权限**: 学生、教师
- **请求体**: `FundApplication` 基金申请信息

### 10.2 提交基金申请

- **URL**: `POST /api/fund-applications/{id}/submit`
- **权限**: 学生、教师
- **路径参数**: `id` - 申请ID

### 10.3 审核基金申请

- **URL**: `POST /api/fund-applications/{id}/review`
- **权限**: 学院管理员、学校管理员
- **路径参数**: `id` - 申请ID
- **请求体**:

```json
{
  "approvalStatus": "APPROVED",
  "reviewComment": "string",
  "approvedAmount": 10000.00
}
```

### 10.4 查询基金申请详情

- **URL**: `GET /api/fund-applications/{id}`
- **权限**: 登录用户
- **路径参数**: `id` - 申请ID

### 10.5 查询所有基金申请

- **URL**: `GET /api/fund-applications`
- **权限**: 登录用户
- **查询参数**:
  - `approvalStatus`: 审批状态（可选）

### 10.6 查询我的基金申请

- **URL**: `GET /api/fund-applications/my`
- **权限**: 学生、教师

---

## 11. 信息对接接口

### 11.1 申请加入团队

- **URL**: `POST /api/information-link/teams/{teamId}/apply`
- **权限**: 学生、教师
- **路径参数**: `teamId` - 团队ID

### 11.2 申请接管项目

- **URL**: `POST /api/information-link/projects/{projectId}/takeover`
- **权限**: 教师
- **路径参数**: `projectId` - 项目ID

### 11.3 创建基金申请

- **URL**: `POST /api/information-link/fund-applications`
- **权限**: 学生、教师
- **请求体**: `FundApplication` 基金申请信息

### 11.4 审核基金申请

- **URL**: `POST /api/information-link/fund-applications/{id}/review`
- **权限**: 学校管理员
- **路径参数**: `id` - 申请ID
- **请求体**:

```json
{
  "approvalStatus": "APPROVED",
  "reviewComment": "string"
}
```

### 11.5 招募成员

- **URL**: `POST /api/information-link/projects/{projectId}/recruit`
- **权限**: 学生、教师
- **路径参数**: `projectId` - 项目ID
- **请求体**:

```json
{
  "userId": 1
}
```

---

## 12. 双创团队申请接口

### 12.1 创建申请

- **URL**: `POST /api/innovation-team-applications`
- **权限**: 登录用户
- **请求体**: `InnovationTeamApplicationDTO` 申请信息

### 12.2 更新申请

- **URL**: `PUT /api/innovation-team-applications/{id}`
- **权限**: 登录用户
- **路径参数**: `id` - 申请ID
- **请求体**: `InnovationTeamApplicationDTO` 申请信息

### 12.3 提交申请

- **URL**: `POST /api/innovation-team-applications/{id}/submit`
- **权限**: 登录用户
- **路径参数**: `id` - 申请ID

### 12.4 查询申请详情

- **URL**: `GET /api/innovation-team-applications/{id}`
- **权限**: 登录用户
- **路径参数**: `id` - 申请ID

### 12.5 查询我的申请

- **URL**: `GET /api/innovation-team-applications/my`
- **权限**: 登录用户

### 12.6 查询所有申请

- **URL**: `GET /api/innovation-team-applications`
- **权限**: 登录用户
- **查询参数**:
  - `status`: 状态（可选）

---

## 13. 项目申请表单接口

### 13.1 创建表单

- **URL**: `POST /api/project-application-forms`
- **权限**: 登录用户
- **请求体**: `ProjectApplicationFormDTO` 表单信息

### 13.2 更新表单

- **URL**: `PUT /api/project-application-forms/{id}`
- **权限**: 登录用户
- **路径参数**: `id` - 表单ID
- **请求体**: `ProjectApplicationFormDTO` 表单信息

### 13.3 提交表单

- **URL**: `POST /api/project-application-forms/{id}/submit`
- **权限**: 登录用户
- **路径参数**: `id` - 表单ID

### 13.4 查询表单详情

- **URL**: `GET /api/project-application-forms/{id}`
- **权限**: 登录用户
- **路径参数**: `id` - 表单ID

### 13.5 查询我的表单

- **URL**: `GET /api/project-application-forms/my`
- **权限**: 登录用户

### 13.6 查询所有表单

- **URL**: `GET /api/project-application-forms`
- **权限**: 登录用户
- **查询参数**:
  - `status`: 状态（可选）

---

## 14. 文件上传接口

### 14.1 通用文件上传

- **URL**: `POST /api/upload`
- **权限**: 登录用户
- **Content-Type**: `multipart/form-data`
- **请求参数**:
  - `file`: 文件
  - `dir`: 目录（可选，默认resume）支持：resume-简历, activity-qrcode-活动二维码, activity-poster-活动海报

---

## 15. 公共数据接口

### 15.1 获取新闻分类

- **URL**: `GET /api/news-categories`
- **权限**: 登录用户

### 15.2 获取空间类型

- **URL**: `GET /api/space-types`
- **权限**: 登录用户

### 15.3 获取人员类型

- **URL**: `GET /api/person-types`
- **权限**: 登录用户

### 15.4 获取活动类型

- **URL**: `GET /api/activity-types`
- **权限**: 登录用户

### 15.5 获取基金类型

- **URL**: `GET /api/fund-types`
- **权限**: 登录用户

### 15.6 获取学院列表

- **URL**: `GET /api/colleges`
- **权限**: 登录用户

---

## 16. 权限控制示例接口

以下接口用于演示权限控制的使用：

### 16.1 学生和教师可访问

- **URL**: `GET /api/examples/student-teacher`
- **权限**: 学生、教师

### 16.2 仅学生可访问

- **URL**: `GET /api/examples/student-only`
- **权限**: 学生

### 16.3 仅教师可访问

- **URL**: `GET /api/examples/teacher-only`
- **权限**: 教师

### 16.4 仅学院管理员可访问

- **URL**: `GET /api/examples/college-admin-only`
- **权限**: 学院管理员

### 16.5 仅学校管理员可访问

- **URL**: `GET /api/examples/school-admin-only`
- **权限**: 学校管理员

### 16.6 所有管理员可访问

- **URL**: `GET /api/examples/admin-all`
- **权限**: 学院管理员、学校管理员

### 16.7 所有角色可访问

- **URL**: `GET /api/examples/all-roles`
- **权限**: 学生、教师、管理员

### 16.8 公开接口（需登录）

- **URL**: `GET /api/examples/public`
- **权限**: 所有登录用户

---

## 附录 A: 状态码说明

| 状态码 | 说明 |
|-------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未登录或登录已过期 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 附录 B: 审批状态

| 状态值 | 说明 |
|-------|------|
| `PENDING` | 待审核 |
| `APPROVED` | 已通过 |
| `REJECTED` | 已驳回 |

## 附录 C: 项目状态

| 状态值 | 说明 |
|-------|------|
| `DRAFT` | 草稿 |
| `PENDING` | 待审核 |
| `APPROVED` | 已通过 |
| `REJECTED` | 已驳回 |
| `IN_PROGRESS` | 进行中 |
| `COMPLETED` | 已完成 |

## 附录 D: 申请表单状态

| 状态值 | 说明 |
|-------|------|
| `DRAFT` | 草稿 |
| `SUBMITTED` | 已提交 |
| `APPROVED` | 已通过 |
| `REJECTED` | 已驳回 |
