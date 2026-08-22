package remix.myplayer.data.db.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import remix.myplayer.data.db.room.entity.ServerConfig

@Dao
interface ServerConfigDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrReplace(config: ServerConfig): Long

  @Query(
    """
    SELECT * from ServerConfig ORDER BY createAt DESC
  """
  )
  fun selectAll(): Flow<List<ServerConfig>>

  @Delete
  suspend fun delete(config: ServerConfig): Int
}
