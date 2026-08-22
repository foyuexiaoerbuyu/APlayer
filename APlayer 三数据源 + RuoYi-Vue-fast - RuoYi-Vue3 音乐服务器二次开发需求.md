# APlayer 三数据源 + RuoYi-Vue-fast / RuoYi-Vue3 音乐服务器二次开发需求

## 一、项目目标

基于现有 APlayer Android 音乐播放器进行二次开发，同时增加一个独立的 Spring Boot 音乐服务器和 Web 管理后台。

本项目不是重新开发音乐播放器，也不是重新开发后台框架。

核心目标：

> **尽可能少修改 APlayer 原有代码，在保留 APlayer 本地音乐和 WebDAV 功能的基础上，新增第三种“服务器音乐”数据来源。**

后台直接基于现有 RuoYi 项目进行业务开发，不重新搭建 Spring Boot 后台框架。

---

# 二、现有项目位置

## Android

APlayer：

```text
D:\01code\02home_web\01Android\APlayer
```

GitHub：

[foyuexiaoerbuyu/APlayer](https://github.com/foyuexiaoerbuyu/APlayer.git?utm_source=chatgpt.com)

---

## 后端

RuoYi-Vue-fast：

```text
D:\01code\02home_web\01Android\APlayer\web\RuoYi-Vue-fast-master
```

使用现有 RuoYi-Vue-fast 作为：

> **Spring Boot 后端 + 权限 + 用户 + 菜单 + 数据权限 + REST API 基础框架**

不要重新创建一个新的 Spring Boot 项目。

---

## 管理前端

RuoYi-Vue3：

```text
D:\01code\02home_web\01Android\APlayer\web\RuoYi-Vue3-master
```

使用现有 RuoYi-Vue3 作为：

> **音乐服务器管理后台 Web 前端**

不要重新创建 Vue 项目。

---

# 三、总体架构

最终系统：

```text
                         ┌──────────────────────────┐
                         │      RuoYi-Vue3          │
                         │       管理后台            │
                         └────────────┬─────────────┘
                                      │
                                      │ REST API
                                      ▼
                         ┌──────────────────────────┐
                         │    RuoYi-Vue-fast        │
                         │      Spring Boot         │
                         │                          │
                         │  用户 / 权限 / 音乐业务    │
                         └────────────┬─────────────┘
                                      │
                        ┌─────────────┼─────────────┐
                        │             │             │
                        ▼             ▼             ▼
                      MySQL         Redis         文件存储
                                                   │
                                             MP3 / FLAC / M4A
                                             LRC / Cover
                                     
                                     
                         Android APlayer
                                │
                       MusicRepository
                                │
              ┌─────────────────┼─────────────────┐
              │                 │                 │
              ▼                 ▼                 ▼
         LocalSource       WebDavSource      ServerSource
              │                 │                 │
              ▼                 ▼                 ▼
          本地音乐             WebDAV         RuoYi API
```

---

# 四、三种数据来源

Android APlayer 最终必须支持：

```text
1. Local
2. WebDAV
3. Server
```

分别对应：

```text
Local
    ↓
APlayer 原有本地音乐功能

WebDAV
    ↓
APlayer 原有 WebDAV 功能

Server
    ↓
RuoYi-Vue-fast 音乐服务器
```

---

# 五、最重要的要求：APlayer 最小侵入式修改

这是整个项目最高优先级。

必须遵守：

> **尽可能少修改 APlayer 原有代码。**

不要为了增加 ServerSource：

- 重写播放器
- 重写 Media3
- 重写 ExoPlayer
- 重写 MediaSession
- 重写播放队列
- 重写本地音乐
- 重写 WebDAV
- 重写歌词系统
- 重写图片加载
- 重写数据库
- 重写整个 UI
- 删除原有功能
- 大规模移动现有代码
- 大规模重构现有架构

优先采用：

```text
新增 Adapter
新增 DataSource
新增 Repository
新增 Server API Client
新增少量 UI
```

解决问题。

---

# 六、开始开发前必须先分析源码

禁止拿到需求后立即大规模修改代码。

第一步必须分析：

## APlayer

重点分析：

```text
本地音乐
WebDAV
数据库
Repository
DAO
Model
ViewModel
Compose UI
MediaItem
播放队列
Media3
ExoPlayer
MusicService
歌词
图片
Retrofit
OkHttp
```

明确：

```text
数据从哪里进入
数据在哪里转换
数据在哪里进入播放队列
MediaItem 在哪里创建
播放 URL 在哪里生成
```

---

## RuoYi-Vue-fast

分析：

```text
模块结构
Controller
Service
Mapper
Entity
DTO
VO
权限
用户
菜单
角色
文件上传
字典
日志
配置
```

重点：

> **必须在现有 RuoYi-Vue-fast 模块体系上增加音乐业务。**

不要重新建立：

```text
Spring Boot
Security
JWT
权限
用户
```

这些基础能力。

直接复用 RuoYi 已有能力。

---

## RuoYi-Vue3

分析：

```text
layout
router
permission
views
components
api
store
```

然后在现有菜单和页面体系中增加：

```text
音乐管理
媒体管理
歌手
专辑
歌单
歌词
播放记录
用户
```

不要重新搭建 Vue 管理后台。

---

# 七、第一阶段必须先输出分析报告

在真正修改代码之前，输出：

## 1. APlayer 当前架构

```text
数据层
 ↓
Repository
 ↓
ViewModel
 ↓
Compose UI
 ↓
播放队列
 ↓
Media3
 ↓
ExoPlayer
```

实际情况必须以源码为准。

---

## 2. RuoYi-Vue-fast 当前架构

列出：

```text
现有模块
新增音乐模块应该放在哪里
需要新增哪些 Controller
需要新增哪些 Service
需要新增哪些 Mapper
需要新增哪些 Entity
```

---

## 3. RuoYi-Vue3 当前架构

列出：

```text
需要增加哪些页面
需要增加哪些 API
需要增加哪些菜单
需要增加哪些路由
```

---

## 4. 最小修改文件列表

明确：

```text
APlayer：
新增文件：
xxx

修改文件：
xxx

RuoYi：
新增文件：
xxx

修改文件：
xxx

Vue：
新增文件：
xxx

修改文件：
xxx
```

并解释：

> 每一个修改为什么必须修改。

如果可以通过新增 Adapter 实现：

> 不允许修改原代码。

---

# 八、APlayer 数据源设计

最终逻辑：

```text
                         MusicRepository
                                │
              ┌─────────────────┼─────────────────┐
              │                 │                 │
              ▼                 ▼                 ▼
         LocalSource       WebDavSource      ServerSource
              │                 │                 │
              ▼                 ▼                 ▼
         原有实现             原有实现        新增实现
```

---

# 九、不要强行重构 Local / WebDAV

非常重要。

如果 APlayer 当前已经有：

```text
Local Music
WebDAV
```

成熟实现：

> 直接复用。

如果需要接入统一接口：

> 使用 Adapter 包装。

不要为了统一接口：

```text
删除原代码
重新实现 Local
重新实现 WebDAV
```

---

# 十、ServerSource

新增：

```text
ServerSource
```

负责访问 RuoYi-Vue-fast 后端。

功能：

```text
歌曲
专辑
歌手
歌单
歌词
搜索
封面
音频
播放历史
播放进度
收藏
```

---

# 十一、推荐 ServerSource 结构

如果 APlayer 现有结构允许，可以设计：

```text
ServerSource
├── ServerApi
├── ServerRepository
├── ServerModel
├── ServerMapper
└── ServerConfig
```

但：

> 必须结合 APlayer 真实源码决定。

不要机械套结构。

---

# 十二、Retrofit / OkHttp

APlayer 已经存在 Retrofit / OkHttp 等网络能力时：

> **优先复用现有网络层。**

不要再引入：

```text
HttpClient
Ktor
Volley
新的 Retrofit
新的 OkHttp
```

除非源码分析后证明确实必要。

---

# 十三、ServerSource 登录

使用 RuoYi 已有用户体系。

Android：

```text
POST /api/login
```

实际接口必须根据 RuoYi-Vue-fast 当前实现确定。

登录后获得 Token。

之后：

```http
Authorization: Bearer xxxxx
```

具体 Token 方案：

> 直接适配现有 RuoYi 认证机制，不重新设计认证系统。

---

# 十四、服务器音乐播放

Android：

```text
APlayer
 ↓
Media3
 ↓
HTTP URL
 ↓
RuoYi-Vue-fast
 ↓
文件存储
```

服务器音频接口必须支持：

```http
Range
```

正确处理：

```http
206 Partial Content
```

必须支持：

- 播放
- 暂停
- Seek
- 拖动进度
- 后台播放
- 大文件
- 有声书
- Podcast

---

# 十五、不要把服务器音乐下载到本地数据库

服务器音乐：

```text
RuoYi
   ↓
API
   ↓
APlayer
```

不要：

```text
RuoYi
 ↓
复制到 APlayer 本地数据库
```

也不要把服务器音乐伪装成本地文件。

ServerSource 应保持独立。

---

# 十六、统一音乐模型

优先使用 APlayer 已有模型。

如果确实需要增加统一模型，可以增加：

```kotlin
MusicItem
```

核心：

```text
source
sourceId
title
artist
album
duration
coverUrl
playUrl
```

来源：

```text
LOCAL
WEBDAV
SERVER
```

---

# 十七、来源 ID

必须防止三个数据源 ID 冲突。

例如：

```text
LOCAL:123
WEBDAV:/music/test.flac
SERVER:123
```

逻辑上必须能够唯一识别。

如果 APlayer 已有合适机制：

> 直接复用。

否则增加：

```text
MusicSourceId
```

---

# 十八、ServerSource UI

只增加一个轻量的数据源入口。

例如：

```text
音乐来源

本地音乐
WebDAV
我的音乐服务器
```

进入：

```text
我的音乐服务器
```

显示：

```text
歌曲
专辑
歌手
歌单
搜索
收藏
最近播放
```

要求：

> 最大限度复用 APlayer 已有列表 UI。

不要重新设计整个 APlayer。

---

# 十九、服务器设置

增加服务器配置：

```text
服务器名称
服务器地址
用户名
密码
Token
启用状态
```

例如：

```text
我的音乐服务器

地址：
http://192.168.1.100:8080

用户名：
admin

密码：
******
```

配置应使用 APlayer 合适的现有配置存储机制。

不要为了服务器配置重新引入一套数据库。

---

# 二十、RuoYi-Vue-fast 后端

必须基于：

```text
D:\01code\02home_web\01Android\APlayer\web\RuoYi-Vue-fast-master
```

进行开发。

禁止：

```text
重新创建 Spring Boot 项目
重新创建 Security
重新创建 JWT
重新创建用户系统
重新创建权限系统
重新创建菜单系统
```

直接使用 RuoYi 原有能力。

---

# 二十一、后端业务模块

在 RuoYi 原有架构上增加音乐业务。

推荐：

```text
音乐内容
歌曲
专辑
歌手
歌单
歌词
媒体资源
播放历史
播放进度
收藏
```

如果 RuoYi 项目当前模块结构适合，可以建立独立业务包：

```text
music
```

或者：

```text
music-content
music-media
music-play
```

但：

> 以现有 RuoYi-Vue-fast 项目结构为准。

不要为了形式强行拆模块。

---

# 二十二、数据库设计

核心：

```text
music_content
music_song
music_artist
music_album
music_artist_relation
music_album_relation
music_resource
music_lyric
music_playlist
music_playlist_item
music_favorite
music_play_history
music_play_progress
```

---

# 二十三、music_content

设计：

```text
id
content_type
title
subtitle
description
cover_url
duration
status
create_time
update_time
```

content_type：

```text
SONG
PODCAST
AUDIOBOOK
```

虽然第一阶段主要实现 SONG：

> 数据模型必须为 Podcast / Audiobook 预留扩展能力。

不要现在实现三个完整业务系统。

---

# 二十四、music_resource

音频文件独立管理：

```text
id
content_id
resource_type
format
codec
bitrate
sample_rate
channels
file_size
file_path
file_hash
duration
create_time
```

支持：

```text
MP3
FLAC
M4A
AAC
OGG
WAV
```

实际格式以实现能力为准。

---

# 二十五、文件存储

第一阶段优先：

```text
服务器本地磁盘
```

例如：

```text
/data/music
/data/music-cover
/data/music-lyric
```

后续可以扩展：

```text
MinIO
S3
OSS
WebDAV
```

但第一阶段不要引入复杂对象存储系统。

---

# 二十六、音乐库扫描

后端提供音乐库扫描能力。

例如：

```text
/data/music
```

执行：

```text
扫描
 ↓
识别音频
 ↓
读取 ID3
 ↓
歌曲
 ↓
歌手
 ↓
专辑
 ↓
封面
 ↓
歌词
 ↓
Hash
 ↓
数据库
```

使用合适的 Java 音频元数据解析库。

---

# 二十七、文件 Hash

`music_resource` 增加：

```text
file_hash
```

用于：

```text
重复音乐检测
```

相同 Hash：

> 认为音频内容相同。

---

# 二十八、歌曲 API

Android 需要：

```text
GET /api/music/page
GET /api/music/{id}
GET /api/music/search
```

具体路径可以根据 RuoYi API 规范调整。

必须支持：

```text
分页
搜索
排序
```

---

# 二十九、专辑 API

```text
GET /api/album/page
GET /api/album/{id}
GET /api/album/{id}/songs
```

---

# 三十、歌手 API

```text
GET /api/artist/page
GET /api/artist/{id}
GET /api/artist/{id}/songs
```

---

# 三十一、歌单 API

```text
GET /api/playlist/my
GET /api/playlist/{id}
POST /api/playlist
PUT /api/playlist/{id}
DELETE /api/playlist/{id}
POST /api/playlist/{id}/songs
DELETE /api/playlist/{id}/songs/{songId}
```

---

# 三十二、歌词 API

```text
GET /api/lyric/{contentId}
```

支持：

```text
LRC
逐字歌词
翻译
```

优先适配 APlayer 现有歌词模型。

---

# 三十三、媒体 API

音频：

```text
GET /api/media/audio/{resourceId}
```

封面：

```text
GET /api/media/cover/{resourceId}
```

歌词：

```text
GET /api/media/lyric/{resourceId}
```

音频接口必须支持：

```text
HTTP Range
```

并正确返回：

```text
206 Partial Content
Content-Range
Content-Length
Accept-Ranges
```

---

# 三十四、播放进度

服务器：

```text
POST /api/play/progress
GET /api/play/progress/{contentId}
```

例如：

```json
{
    "contentId": 10001,
    "position": 152000,
    "duration": 320000
}
```

Android 定期同步。

服务器保存用户播放进度。

---

# 三十五、播放历史

```text
POST /api/play/history
GET /api/play/history
```

保存：

```text
用户
歌曲
播放时间
播放位置
播放时长
```

---

# 三十六、收藏

```text
POST /api/favorite/{contentId}
DELETE /api/favorite/{contentId}
GET /api/favorite/page
```

---

# 三十七、RuoYi-Vue3 管理后台

必须基于：

```text
D:\01code\02home_web\01Android\APlayer\web\RuoYi-Vue3-master
```

开发。

禁止：

```text
重新创建 Vue 项目
重新创建登录系统
重新创建权限系统
重新创建 Layout
重新创建 Router
```

直接复用 RuoYi-Vue3。

---

# 三十八、后台菜单

增加：

```text
音乐管理
├── 音乐内容
├── 歌曲
├── 专辑
├── 歌手
├── 歌词
├── 歌单
└── 媒体资源

音乐库
├── 音乐扫描
├── 文件管理
└── 重复检测

播放
├── 播放历史
├── 播放进度
└── 收藏

系统
└── 音乐服务器配置
```

实际菜单必须接入 RuoYi 原有菜单权限体系。

---

# 三十九、后台歌曲管理

支持：

```text
分页
搜索
新增
编辑
删除
批量删除
查看详情
上传音频
修改封面
修改歌词
```

字段至少：

```text
标题
艺术家
专辑
时长
封面
格式
码率
采样率
文件大小
状态
```

---

# 四十、后台音乐扫描

页面：

```text
音乐库 → 音乐扫描
```

支持：

```text
扫描路径
开始扫描
扫描进度
新增数量
更新数量
重复数量
失败数量
```

---

# 四十一、后台播放器

可以增加一个简单的 Web 播放器用于测试。

但：

> 不需要开发完整 Web 音乐播放器。

后台主要用于：

```text
管理
测试
试听
```

---

# 四十二、用户体系

直接使用 RuoYi 原有：

```text
sys_user
sys_role
sys_menu
```

Android 用户也使用 RuoYi 用户体系。

不要重新创建：

```text
music_user
```

除非源码分析后发现必须建立独立扩展表。

---

# 四十三、权限

直接使用 RuoYi 原有权限体系。

例如：

```text
music:song:list
music:song:add
music:song:edit
music:song:remove
music:album:list
music:artist:list
music:playlist:list
```

具体权限标识按照现有 RuoYi 规范生成。

---

# 四十四、不要过度设计

本项目主要是个人使用。

因此：

第一阶段不要：

```text
微服务
Nacos
Gateway
OAuth2
消息队列
分布式事务
Kubernetes
复杂对象存储
```

使用：

```text
RuoYi-Vue-fast
+
MySQL
+
本地文件
```

即可。

后续有需要再扩展。

---

# 四十五、开发阶段

## Phase 1：源码分析

只分析，不修改。

输出：

```text
APlayer 架构
RuoYi 后端架构
RuoYi Vue 架构
最小修改方案
文件修改清单
数据库设计
API 设计
```

---

## Phase 2：后端基础

基于 RuoYi-Vue-fast：

```text
音乐表
歌曲
专辑
歌手
资源
歌词
```

完成：

```text
CRUD
分页
搜索
文件上传
音频 Range 播放
```

---

## Phase 3：Android ServerSource

APlayer：

```text
Retrofit
 ↓
ServerSource
 ↓
服务器歌曲
 ↓
APlayer 原有 Model
 ↓
原有 MediaItem
 ↓
原有 ExoPlayer
```

先实现：

```text
登录
歌曲列表
歌曲详情
播放
封面
```

---

## Phase 4：补充业务

```text
专辑
歌手
歌单
搜索
歌词
收藏
播放历史
播放进度
```

---

## Phase 5：后台

RuoYi-Vue3：

```text
歌曲管理
专辑
歌手
歌词
歌单
文件
扫描
播放记录
```

---

# 四十六、验收标准

## APlayer 本地音乐

原功能全部正常。

## APlayer WebDAV

原功能全部正常。

## Server

可以：

```text
登录
歌曲列表
搜索
专辑
歌手
歌单
歌词
封面
播放
收藏
播放历史
播放进度
```

## 播放

Server 音乐必须支持：

```text
播放
暂停
继续
上一首
下一首
Seek
后台播放
锁屏
通知栏
蓝牙
```

这些功能：

> 优先使用 APlayer 原有实现。

---

# 四十七、最终架构

最终希望达到：

```text
                         APlayer Android
                                │
                         MusicRepository
                                │
              ┌─────────────────┼─────────────────┐
              │                 │                 │
              ▼                 ▼                 ▼
           LocalSource       WebDavSource      ServerSource
              │                 │                 │
              │                 │                 │
          原有实现            原有实现          RuoYi API
                                                  │
                                                  ▼
                                      RuoYi-Vue-fast
                                                  │
                                    ┌─────────────┼─────────────┐
                                    │             │             │
                                    ▼             ▼             ▼
                                  MySQL        文件系统       RuoYi用户
```

管理端：

```text
RuoYi-Vue3
      │
      ▼
RuoYi-Vue-fast
      │
      ├── 音乐
      ├── 歌手
      ├── 专辑
      ├── 歌词
      ├── 歌单
      ├── 媒体
      ├── 播放历史
      ├── 播放进度
      └── 收藏
```

---

# 四十八、最核心的要求再次强调

整个项目必须围绕以下原则开发：

### APlayer

```text
原来的东西尽量不动
```

### Local

```text
继续使用原有实现
```

### WebDAV

```text
继续使用原有实现
```

### Server

```text
新增 ServerSource
```

### 播放器

```text
继续使用 APlayer 原来的 Media3 / ExoPlayer
```

### 后端

```text
直接基于 RuoYi-Vue-fast
```

### 管理后台

```text
直接基于 RuoYi-Vue3
```

### 用户/权限

```text
直接复用 RuoYi
```

### 数据库

```text
在 RuoYi 数据库基础上增加 music_* 业务表
```

### 文件

```text
第一阶段使用服务器本地磁盘
```

---

# 四十九、最终目标

最终用户打开 APlayer：

```text
音乐来源

📱 本地音乐

☁ WebDAV

🌐 我的音乐服务器
```

选择：

```text
🌐 我的音乐服务器
```

即可看到：

```text
歌曲
专辑
歌手
歌单
搜索
收藏
最近播放
```

点击歌曲：

```text
Server
 ↓
HTTP Range
 ↓
Media3
 ↓
ExoPlayer
```

而：

```text
播放控制
播放队列
后台播放
锁屏
通知栏
蓝牙
歌词显示
```

全部尽可能继续使用 APlayer 原有能力。

最终形成：

> **APlayer = 三数据源统一的 Android 播放客户端**

> **RuoYi-Vue-fast = 音乐服务器 + REST API**

> **RuoYi-Vue3 = 音乐服务器管理后台**

而不是重新开发三个系统。

---

# 五十、执行纪律

在开始修改任何代码之前：

1. 分析 APlayer。
2. 分析 RuoYi-Vue-fast。
3. 分析 RuoYi-Vue3。
4. 输出三者当前架构。
5. 找出 APlayer 最小侵入点。
6. 输出新增/修改文件清单。
7. 输出数据库设计。
8. 输出 API 设计。
9. 输出完整数据流。
10. 确认设计合理后再实施。

**禁止直接大规模修改代码。**

如果发现现有项目已经存在类似功能：

> 优先复用。

如果发现可以通过 Adapter 实现：

> 优先 Adapter。

如果发现必须修改原代码：

> 只修改最小范围，并明确说明原因。

最终衡量标准不是“代码重构得多漂亮”，而是：

> **用最少的 APlayer 修改，实现 Local + WebDAV + Server 三种数据源，同时充分复用 RuoYi-Vue-fast 和 RuoYi-Vue3 原有能力。**