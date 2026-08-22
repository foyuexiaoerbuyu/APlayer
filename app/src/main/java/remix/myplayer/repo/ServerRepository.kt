package remix.myplayer.repo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import remix.myplayer.data.db.room.entity.ServerConfig
import remix.myplayer.data.model.audio.Album
import remix.myplayer.data.model.audio.Artist
import remix.myplayer.data.model.audio.Folder
import remix.myplayer.data.model.audio.Genre
import remix.myplayer.data.model.audio.Song
import remix.myplayer.data.model.server.LoginRequest
import remix.myplayer.data.model.server.ServerSong
import remix.myplayer.request.network.ServerApi
import javax.inject.Inject

/**
 * 服务器音乐数据源：登录 + 歌曲/专辑/歌手/流派列表 → APlayer 原有模型。
 */
class ServerRepository @Inject constructor(
  private val serverApi: ServerApi,
  private val serverConfigRepository: ServerConfigRepository
) {

  /** 登录并返回 token，失败返回 null。成功后把 token 写入 config。 */
  suspend fun login(config: ServerConfig): String? {
    val response = withContext(Dispatchers.IO) {
      runCatching {
        serverApi.login(
          url = "${config.base()}/login",
          body = LoginRequest(config.account, config.pwd)
        )
      }.getOrNull()
    } ?: return null
    if (response.code != 200 || response.token.isNullOrEmpty()) {
      return null
    }
    config.token = response.token
    return response.token
  }

  /** 确保 config 有可用 token，必要时登录并持久化。 */
  private suspend fun ensureToken(config: ServerConfig): String? {
    var token = config.token
    if (token.isNullOrEmpty()) {
      token = login(config) ?: return null
      persistToken(config)
    }
    return token
  }

  /** 获取服务器歌曲列表并映射为 Song.Remote，支持按专辑/歌手/流派过滤。 */
  suspend fun fetchSongs(
    config: ServerConfig,
    albumId: Long? = null,
    artistId: Long? = null,
    genre: String? = null
  ): List<Song.Remote> {
    return fetchSongRows(config, albumId, artistId, genre).map { it.toSong(config) }
  }

  /** 获取服务器文件夹列表：按歌曲服务端存储路径（resource.filePath）的父目录分组。 */
  suspend fun fetchFolders(config: ServerConfig): List<Folder> {
    val rows = fetchSongRows(config)
    if (rows.isEmpty()) return emptyList()
    val folderMap = LinkedHashMap<String, MutableList<ServerSong>>()
    for (song in rows) {
      val filePath = song.resource?.filePath ?: continue
      // 兼容 Windows(\ )与 Linux(/) 路径分隔符
      val normalized = filePath.replace('\\', '/')
      val parentPath = normalized.substringBeforeLast("/").ifEmpty { "/" }
      folderMap.getOrPut(parentPath) { mutableListOf() }.add(song)
    }
    return folderMap.map { (path, songs) ->
      Folder(
        name = path.substringAfterLast("/").ifEmpty { path },
        count = songs.size,
        path = path
      )
    }
  }

  /** 获取指定文件夹（服务端存储路径父目录）下的歌曲。 */
  suspend fun fetchSongsInFolder(config: ServerConfig, folderPath: String): List<Song.Remote> {
    return fetchSongRows(config).filter { song ->
      song.resource?.filePath?.replace('\\', '/')?.substringBeforeLast("/") == folderPath
    }.map { it.toSong(config) }
  }

  /** 拉取歌曲原始行（带 token 过期重登），供歌曲/文件夹聚合复用。 */
  private suspend fun fetchSongRows(
    config: ServerConfig,
    albumId: Long? = null,
    artistId: Long? = null,
    genre: String? = null
  ): List<ServerSong> {
    val token = ensureToken(config) ?: return emptyList()
    var response = fetchList(config, token, albumId, artistId, genre) ?: return emptyList()
    if (response.code != 200) {
      // token 可能过期，重登一次
      val newToken = login(config) ?: return emptyList()
      persistToken(config)
      response = fetchList(config, newToken, albumId, artistId, genre) ?: return emptyList()
      if (response.code != 200) return emptyList()
    }
    return response.rows
  }

  /** 获取服务器专辑列表并映射为 APlayer Album。 */
  suspend fun fetchAlbums(config: ServerConfig): List<Album> {
    val token = ensureToken(config) ?: return emptyList()
    val response = withContext(Dispatchers.IO) {
      runCatching {
        serverApi.albumList(
          url = "${config.base()}/music/album/list",
          authorization = "Bearer $token",
          pageNum = 1,
          pageSize = 1000
        )
      }.getOrNull()
    } ?: return emptyList()
    if (response.code != 200) return emptyList()
    return response.rows.map {
      Album(
        albumID = it.id,
        album = it.name,
        artistID = 0,
        artist = "",
        count = it.songCount.toInt()
      )
    }
  }

  /** 获取服务器歌手列表并映射为 APlayer Artist。 */
  suspend fun fetchArtists(config: ServerConfig): List<Artist> {
    val token = ensureToken(config) ?: return emptyList()
    val response = withContext(Dispatchers.IO) {
      runCatching {
        serverApi.artistList(
          url = "${config.base()}/music/artist/list",
          authorization = "Bearer $token",
          pageNum = 1,
          pageSize = 1000
        )
      }.getOrNull()
    } ?: return emptyList()
    if (response.code != 200) return emptyList()
    return response.rows.map {
      Artist(
        artistID = it.id,
        artist = it.name,
        count = it.songCount.toInt()
      )
    }
  }

  /** 获取服务器流派列表并映射为 APlayer Genre。 */
  suspend fun fetchGenres(config: ServerConfig): List<Genre> {
    val token = ensureToken(config) ?: return emptyList()
    val response = withContext(Dispatchers.IO) {
      runCatching {
        serverApi.genreList(
          url = "${config.base()}/music/genre/list",
          authorization = "Bearer $token",
          pageNum = 1,
          pageSize = 1000
        )
      }.getOrNull()
    } ?: return emptyList()
    if (response.code != 200) return emptyList()
    return response.rows.map {
      Genre(
        id = it.id ?: 0L,
        genre = it.genre,
        count = it.genreCount.toInt()
      )
    }
  }

  private suspend fun fetchList(
    config: ServerConfig,
    token: String,
    albumId: Long? = null,
    artistId: Long? = null,
    genre: String? = null
  ) = withContext(Dispatchers.IO) {
    runCatching {
      serverApi.songList(
        url = "${config.base()}/music/song/list",
        authorization = "Bearer $token",
        pageNum = 1,
        pageSize = 1000,
        albumId = albumId,
        artistId = artistId,
        genre = genre
      )
    }.getOrNull()
  }

  private suspend fun persistToken(config: ServerConfig) {
    if (config.id > 0) {
      serverConfigRepository.insertOrReplace(config)
    }
  }

  private fun ServerSong.toSong(config: ServerConfig): Song.Remote {
    val base = config.base()
    val cover = if (!coverPath.isNullOrEmpty() && coverPath!!.startsWith("http")) {
      coverPath!!
    } else {
      "$base/music/media/cover/$id"
    }
    return Song.Remote(
      title = title.ifEmpty { "Song $id" },
      album = albumName ?: "",
      artist = artistName ?: "",
      duration = duration,
      data = "$base/music/media/audio/$id",
      size = 0,
      year = "",
      genre = genre ?: "",
      track = "",
      dateModified = 0,
      account = config.account,
      pwd = config.pwd,
      token = config.token,
      coverUrl = cover
    )
  }
}
