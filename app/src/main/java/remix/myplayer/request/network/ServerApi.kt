package remix.myplayer.request.network

import remix.myplayer.data.model.server.AlbumListResponse
import remix.myplayer.data.model.server.ArtistListResponse
import remix.myplayer.data.model.server.GenreListResponse
import remix.myplayer.data.model.server.LoginRequest
import remix.myplayer.data.model.server.LoginResponse
import remix.myplayer.data.model.server.SongListResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * RuoYi 音乐服务器 API。
 * 服务器地址由用户配置，全部接口使用 @Url 动态传入完整路径。
 */
interface ServerApi {

  /** POST /login */
  @POST
  suspend fun login(
    @Url url: String,
    @Body body: LoginRequest
  ): LoginResponse

  /** GET /music/song/list，支持按专辑/歌手/流派过滤 */
  @GET
  suspend fun songList(
    @Url url: String,
    @Header("Authorization") authorization: String,
    @Query("pageNum") pageNum: Int = 1,
    @Query("pageSize") pageSize: Int = 1000,
    @Query("albumId") albumId: Long? = null,
    @Query("artistId") artistId: Long? = null,
    @Query("genre") genre: String? = null
  ): SongListResponse

  /** GET /music/album/list */
  @GET
  suspend fun albumList(
    @Url url: String,
    @Header("Authorization") authorization: String,
    @Query("pageNum") pageNum: Int = 1,
    @Query("pageSize") pageSize: Int = 1000
  ): AlbumListResponse

  /** GET /music/artist/list */
  @GET
  suspend fun artistList(
    @Url url: String,
    @Header("Authorization") authorization: String,
    @Query("pageNum") pageNum: Int = 1,
    @Query("pageSize") pageSize: Int = 1000
  ): ArtistListResponse

  /** GET /music/genre/list */
  @GET
  suspend fun genreList(
    @Url url: String,
    @Header("Authorization") authorization: String,
    @Query("pageNum") pageNum: Int = 1,
    @Query("pageSize") pageSize: Int = 1000
  ): GenreListResponse
}
