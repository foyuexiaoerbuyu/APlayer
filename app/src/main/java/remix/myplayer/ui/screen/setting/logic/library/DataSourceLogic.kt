package remix.myplayer.ui.screen.setting.logic.library

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import remix.myplayer.R
import remix.myplayer.data.prefs.SettingPrefs
import remix.myplayer.ui.screen.setting.SwitchPreference
import remix.myplayer.viewmodel.libraryViewModel
import remix.myplayer.viewmodel.settingViewModel

/**
 * 数据源切换：本地媒体库 / 在线音乐服务器。
 * 切换后立即刷新曲库各 tab（歌曲/专辑/歌手/流派）。
 */
@Composable
fun DataSourceLogic() {
  val vm = settingViewModel
  val libraryVm = libraryViewModel
  val dataSourceMode by vm.dataSourceMode.collectAsStateWithLifecycle()

  SwitchPreference(
    title = stringResource(R.string.data_source),
    content = stringResource(
      if (dataSourceMode == SettingPrefs.DATA_SOURCE_SERVER) {
        R.string.data_source_server
      } else {
        R.string.data_source_local
      }
    ),
    checked = dataSourceMode == SettingPrefs.DATA_SOURCE_SERVER,
    onCheckedChange = { online ->
      vm.setDataSourceMode(if (online) SettingPrefs.DATA_SOURCE_SERVER else SettingPrefs.DATA_SOURCE_LOCAL)
      libraryVm.fetchMedia(clear = true)
    }
  )
}
