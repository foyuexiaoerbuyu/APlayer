package remix.myplayer.data.db.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * RuoYi 音乐服务器配置
 */
@kotlinx.serialization.Serializable
@Entity(tableName = "ServerConfig")
data class ServerConfig(
  var alias: String,
  var account: String,
  var pwd: String,
  var server: String,
  val createAt: Long = System.currentTimeMillis()
) : Serializable {

  @PrimaryKey(autoGenerate = true)
  var id: Int = 0

  var token: String? = null

  fun base(): String {
    val raw = server.removeSuffix("/")
    return if (raw.startsWith("http://") || raw.startsWith("https://")) raw else "http://$raw"
  }
}
