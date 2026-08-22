package remix.myplayer.data.model.server

import kotlinx.serialization.Serializable

/** RuoYi 登录请求 */
@Serializable
data class LoginRequest(
  val username: String,
  val password: String
)

/** RuoYi 登录响应 { code, msg, token } */
@Serializable
data class LoginResponse(
  val code: Int = -1,
  val msg: String? = null,
  val token: String? = null
)

/** GET /music/song/list 响应 { code, total, rows } */
@Serializable
data class SongListResponse(
  val code: Int = -1,
  val msg: String? = null,
  val total: Long = 0,
  val rows: List<ServerSong> = emptyList()
)

/** 服务器歌曲（rows 内字段） */
@Serializable
data class ServerSong(
  val id: Long = 0,
  val title: String = "",
  val artistName: String? = null,
  val albumName: String? = null,
  val duration: Long = 0,
  val coverPath: String? = null,
  val genre: String? = null,
  val resource: ServerResource? = null
)

/** 服务器歌曲资源（music_resource 关联，filePath 为服务端真实存储路径，用于在线模式文件夹分组） */
@Serializable
data class ServerResource(
  val filePath: String? = null
)

/** GET /music/album/list 响应 */
@Serializable
data class AlbumListResponse(
  val code: Int = -1,
  val msg: String? = null,
  val total: Long = 0,
  val rows: List<ServerAlbum> = emptyList()
)

/** 服务器专辑（rows 内字段） */
@Serializable
data class ServerAlbum(
  val id: Long = 0,
  val name: String = "",
  val songCount: Long = 0
)

/** GET /music/artist/list 响应 */
@Serializable
data class ArtistListResponse(
  val code: Int = -1,
  val msg: String? = null,
  val total: Long = 0,
  val rows: List<ServerArtist> = emptyList()
)

/** 服务器歌手（rows 内字段） */
@Serializable
data class ServerArtist(
  val id: Long = 0,
  val name: String = "",
  val songCount: Long = 0
)

/** GET /music/genre/list 响应 */
@Serializable
data class GenreListResponse(
  val code: Int = -1,
  val msg: String? = null,
  val total: Long = 0,
  val rows: List<ServerGenre> = emptyList()
)

/** 服务器流派（rows 内字段） */
@Serializable
data class ServerGenre(
  val id: Long? = null,
  val genre: String = "",
  val genreCount: Long = 0
)
