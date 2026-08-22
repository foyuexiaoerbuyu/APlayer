package remix.myplayer.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import remix.myplayer.R
import remix.myplayer.data.db.room.entity.ServerConfig
import remix.myplayer.data.model.audio.Song
import remix.myplayer.repo.ServerConfigRepository
import remix.myplayer.repo.ServerRepository
import remix.myplayer.repo.usecase.FetchMetaDataUseCase
import remix.myplayer.ui.dialog.DialogState
import remix.myplayer.ui.dialog.runWithLoading
import remix.myplayer.ui.nav.MessageNotifier
import remix.myplayer.ui.state.DataUiState
import remix.myplayer.util.ext.updateIf
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(
  private val serverConfigRepository: ServerConfigRepository,
  private val serverRepository: ServerRepository,
  private val fetchMetaDataUseCase: FetchMetaDataUseCase
) : ViewModel() {

  private val _serverList = MutableStateFlow<List<ServerConfig>>(emptyList())
  val serverList: StateFlow<List<ServerConfig>> = _serverList.asStateFlow()

  private val _songState =
    MutableStateFlow<DataUiState<List<Song.Remote>>>(DataUiState.Loading())
  val songState: StateFlow<DataUiState<List<Song.Remote>>> = _songState.asStateFlow()

  init {
    viewModelScope.launch {
      serverConfigRepository.allServers().collect {
        _serverList.value = it
      }
    }
  }

  fun loadSongs(config: ServerConfig) {
    _songState.value = DataUiState.Loading()
    viewModelScope.launch {
      val songs = try {
        withContext(Dispatchers.IO) {
          serverRepository.fetchSongs(config)
        }
      } catch (e: Exception) {
        Timber.e(e)
        _songState.value = DataUiState.Error(e)
        return@launch
      }
      _songState.value = DataUiState.Success(songs)
    }
  }

  /** 登录/保存成功或删除服务器后回调，由 UI 层设置为在线数据刷新入口 */
  var onMediaRefresh: (() -> Unit)? = null

  fun deleteServer(config: ServerConfig) = viewModelScope.launch {
    serverConfigRepository.delete(config)
    onMediaRefresh?.invoke()
  }

  /** 先登录校验，成功后才保存配置（含 token）。 */
  fun insertOrReplaceServer(config: ServerConfig) = viewModelScope.runWithLoading {
    val token = withContext(Dispatchers.IO) {
      runCatching { serverRepository.login(config) }.getOrNull()
    }
    if (token.isNullOrEmpty()) {
      MessageNotifier.show(R.string.server_login_failed)
      return@runWithLoading
    }
    serverConfigRepository.insertOrReplace(config)
    onMediaRefresh?.invoke()
  }

  private val _addServerState = MutableStateFlow(AddServerState(DialogState()))
  val addServerState = _addServerState.asStateFlow()

  fun updateAddServerState(
    alias: String? = null,
    account: String? = null,
    pwd: String? = null,
    server: String? = null
  ) {
    _addServerState.update {
      it.copy(
        alias = alias ?: it.alias,
        account = account ?: it.account,
        pwd = pwd ?: it.pwd,
        server = server ?: it.server
      )
    }
  }

  fun showAddServerDialog(editServer: ServerConfig? = null) {
    _addServerState.updateIf(
      condition = { !it.dialogState.isOpen },
      transform = {
        it.dialogState.show()
        it.copy(
          alias = editServer?.alias ?: it.alias,
          account = editServer?.account ?: it.account,
          pwd = editServer?.pwd ?: it.pwd,
          server = editServer?.server ?: it.server,
          editServer = editServer
        )
      }
    )
  }

  suspend fun fetchMeta(song: Song.Remote) = fetchMetaDataUseCase(song)
}

@Stable
data class AddServerState(
  val dialogState: DialogState,
  val editServer: ServerConfig? = null,
  val alias: String = "",
  val account: String = "",
  val pwd: String = "",
  val server: String = ""
)
