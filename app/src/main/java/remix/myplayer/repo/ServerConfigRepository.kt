package remix.myplayer.repo

import kotlinx.coroutines.flow.Flow
import remix.myplayer.data.db.room.dao.ServerConfigDao
import remix.myplayer.data.db.room.entity.ServerConfig
import javax.inject.Inject

interface ServerConfigRepository {
  fun allServers(): Flow<List<ServerConfig>>
  suspend fun insertOrReplace(config: ServerConfig)
  suspend fun delete(config: ServerConfig)
}

class ServerConfigRepoImpl @Inject constructor(private val dao: ServerConfigDao) :
  ServerConfigRepository {
  override fun allServers(): Flow<List<ServerConfig>> {
    return dao.selectAll()
  }

  override suspend fun insertOrReplace(config: ServerConfig) {
    dao.insertOrReplace(config)
  }

  override suspend fun delete(config: ServerConfig) {
    dao.delete(config)
  }
}
