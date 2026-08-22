# 第四部分：数据流 & 第五部分：Repository

## 数据流分析

### Song对象创建流程

```mermaid
graph TD
    A[MediaStore扫描] --> B[SongRepository]
    B --> C[resolveSong Cursor]
    C --> D[Song.Local]
    
    E[WebDAV浏览] --> F[WebDavRepository]
    F --> G[Sardine API]
    G --> H[解析文件列表]
    H --> I[Song.Remote]
    
    J[SMB浏览] --> K[SmbRepository]
    K --> L[SMB协议]
    L --> M[解析文件列表]
    M --> I
```

**Song.Local**: 从 MediaStore Cursor 创建，包含完整元数据。

**Song.Remote**: 从远程文件列表创建，初始只有基本信息，通过 `FetchMetaDataUseCase` 异步获取详细元数据。

---

### Repository工作机制

```mermaid
graph TD
    A[UI层] --> B[ViewModel]
    B --> C[Repository接口]
    C --> D{数据来源}
    D -->|本地| E[MediaStore ContentResolver]
    D -->|远程| F[网络API/协议]
    D -->|缓存| G[Room数据库]
    E --> H[Cursor查询]
    H --> I[resolveSong]
    I --> J[Song.Local]
    F --> K[解析响应]
    K --> L[Song.Remote]
```

所有Repository遵循相同模式：
1. ViewModel调用Repository接口方法
2. Repository根据数据来源选择不同实现
3. 返回统一的数据模型

---

### Room存储机制

```mermaid
graph TD
    A[PlayListDao] --> B[PlayList表]
    B --> C[audioIds JSON数组]
    
    D[PlayQueueDao] --> E[PlayQueue表]
    E --> F[序列化的播放队列]
    
    G[HistoryDao] --> H[History表]
    H --> I[audio_id + play_count + last_play]
    
    J[WebDavDao] --> K[WebDav表]
    K --> L[账号配置信息]
    
    M[SmbDao] --> N[Smb表]
    N --> O[SMB配置信息]
    
    P[MetaDataCacheDao] --> Q[MetaDataCache表]
    Q --> R[远程歌曲元数据缓存]
```

---

### MediaStore扫描流程

```mermaid
sequenceDiagram
    participant Service as MusicService
    participant Observer as MediaStoreObserver
    participant Repo as SongRepository
    participant MediaStore as MediaStore
    
    Service->>Observer: registerContentObserver
    MediaStore-->>Observer: onChange()
    Observer->>Service: 发送 MEDIA_STORE_CHANGE 广播
    Service->>Repo: allSongs()
    Repo->>MediaStore: query(EXTERNAL_CONTENT_URI)
    MediaStore-->>Repo: Cursor
    Repo->>Repo: resolveSong() for each
    Repo-->>Service: List<Song>
```

**扫描过滤器** (在 `AbstractRepository.baseSelection`):
- `_data != ''`: 排除空路径
- `SIZE > settingPrefs.scanSize`: 排除过小文件
- `_ID NOT IN deleteIds`: 排除已删除歌曲
- `DATA NOT LIKE blacklist%`: 排除黑名单目录

---

### 播放历史保存流程

```mermaid
sequenceDiagram
    participant Service as MusicService
    participant HistoryRepo as HistoryRepository
    participant Dao as HistoryDao
    
    Service->>Service: onIsPlayingChanged(true)
    Service->>Service: updatePlayHistory()
    Service->>HistoryRepo: update(songId, checkDuplicate)
    HistoryRepo->>Dao: findByAudioId(songId)
    
    alt 记录存在
        Dao-->>HistoryRepo: History实体
        HistoryRepo->>Dao: update(play_count++, last_play=now)
    else 记录不存在
        Dao-->>HistoryRepo: null
        HistoryRepo->>Dao: insert(History)
    end
```

---

### 收藏保存流程

```mermaid
sequenceDiagram
    participant Service as MusicService
    participant PlaybackState as PlaybackFavoriteState
    participant PlayListRepo as PlayListRepository
    participant Dao as PlayListDao
    
    Service->>Service: handleCommand(Command.LOVE)
    Service->>PlaybackState: toggle(song)
    PlaybackState->>PlayListRepo: getFavorite()
    PlayListRepo->>Dao: findById(1)
    Dao-->>PlayListRepo: PlayList(isFavorite=true)
    
    alt 添加收藏
        PlayListRepo->>PlayListRepo: audioIds.add(songId)
        PlayListRepo->>Dao: update(playList)
        PlaybackState-->>Service: true
    else 取消收藏
        PlayListRepo->>PlayListRepo: audioIds.remove(songId)
        PlayListRepo->>Dao: update(playList)
        PlaybackState-->>Service: true
    end
```

收藏通过特殊的 PlayList(id=1) 实现。

---

### 歌词关联流程

```mermaid
graph TD
    A[歌曲开始播放] --> B[LyricManager.updateLyrics]
    B --> C[LyricSearcher.search]
    C --> D{搜索顺序}
    D -->|1| E[EmbeddedProvider]
    D -->|2| F[LocalFileProvider]
    D -->|3| G[UriProvider]
    D -->|4| H[NetworkProvider]
    D -->|5| I[IgnoredProvider]
    
    E --> J[从音频文件读取内嵌歌词]
    F --> K[扫描本地.lrc文件]
    G --> L[从指定URI读取]
    H --> M{网络来源}
    M -->|KuGou| N[KuGouProvider]
    M -->|NetEase| O[NetEaseProvider]
    M -->|QQ| P[QQProvider]
    
    J --> Q[LrcParser解析]
    K --> Q
    L --> Q
    N --> Q
    O --> Q
    P --> Q
    Q --> R[LyricLine列表]
    R --> S[更新UI显示]
```

---

## Repository详细分析

### 1. AbstractRepository

**基类职责**:
- `baseSelection`: 构建基础查询条件
- `baseSelectionArgs`: 构建查询参数
- `baseProjection`: 定义查询字段
- `resolveSong()`: 从 Cursor 解析 Song 对象

**继承关系**:
```mermaid
graph TD
    A[AbstractRepository] --> B[SongRepoImpl]
    A --> C[AlbumRepoImpl]
    A --> D[ArtistRepoImpl]
    A --> E[FolderRepoImpl]
    A --> F[GenreRepoImpl]
```

### 2. SongRepository

**核心方法**:

| 方法 | 功能 |
|------|------|
| `allSongs()` | 获取所有本地歌曲 |
| `getSongs(selection, selectionValues, sortOrder)` | 条件查询 |
| `song(id)` | 根据ID获取单首歌曲 |
| `getSongsByModels(models)` | 根据模型获取歌曲列表 |
| `getSongsByGenreId(genreId)` | 根据流派获取歌曲 |
| `getLastAddedSongs()` | 获取最近添加的歌曲 |

**`getSongsByModels()` 支持的模型**:
- `Song`: 直接返回
- `Album`: 查询 `ALBUM_ID = ?`
- `Artist`: 查询 `ARTIST_ID = ?`
- `Genre`: 通过 `Genres.Members` 查询
- `Folder`: 过滤路径匹配
- `PlayList`: 查询 `audioIds` 列表中的歌曲

### 3. PlayListRepository

**核心功能**:
- 歌单CRUD操作
- 收藏功能（id=1的特殊歌单）
- 自定义排序支持

**歌单存储结构**:
```kotlin
data class PlayList(
    val id: Long,
    val name: String,
    val audioIds: ArrayList<Long>,  // JSON序列化存储
    val date: Long
)
```

### 4. HistoryRepository

**播放历史统计**:
- 记录播放次数
- 记录最后播放时间
- 支持去重检查

### 5. WebDavRepository & SmbRepository

**远程配置管理**:
- 保存/读取连接配置
- 账号密码加密存储
- 支持多个连接

### 6. UseCase层

**用例类**:
- `DeleteSongUseCase`: 删除歌曲
- `ExportPlayListUseCase`: 导出歌单
- `FetchMetaDataUseCase`: 获取远程歌曲元数据
- `PlayFromUriUseCase`: 从URI播放

---

## Repository依赖关系

```mermaid
graph LR
    subgraph Repository层
        A[SongRepository] --> B[AbstractRepository]
        C[AlbumRepository] --> B
        D[ArtistRepository] --> B
        E[GenreRepository] --> B
        F[FolderRepository] --> B
        G[PlayListRepository] --> H[PlayListDao]
        I[HistoryRepository] --> J[HistoryDao]
        K[WebDavRepository] --> L[WebDavDao]
        M[SmbRepository] --> N[SmbDao]
        O[PlayQueueRepository] --> P[PlayQueueDao]
    end
    
    subgraph 数据源
        B --> Q[MediaStore]
        H --> R[Room]
        J --> R
        L --> R
        N --> R
        P --> R
    end
```

---

## 关键设计特点

1. **统一接口**: 所有Repository都有接口定义，便于测试和替换
2. **本地优先**: 本地歌曲通过MediaStore直接读取，不缓存到Room
3. **远程延迟加载**: Remote歌曲先创建轻量对象，再异步获取元数据
4. **策略模式**: 歌词搜索采用策略模式，支持多种来源
5. **Hilt注入**: Repository通过Hilt注入，解耦依赖关系