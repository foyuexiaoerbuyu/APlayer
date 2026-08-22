package remix.myplayer.ui.screen.server

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import remix.myplayer.R
import remix.myplayer.data.db.room.entity.ServerConfig
import remix.myplayer.data.model.audio.Song
import remix.myplayer.service.Command
import remix.myplayer.service.MusicService
import remix.myplayer.service.MusicServiceRemote
import remix.myplayer.ui.dialog.runWithLoading
import remix.myplayer.ui.nav.LocalNavController
import remix.myplayer.ui.nav.MessageNotifier
import remix.myplayer.ui.state.DataUiState
import remix.myplayer.ui.theme.LocalTheme
import remix.myplayer.ui.theme.icon
import remix.myplayer.ui.widget.app.BottomBar
import remix.myplayer.ui.widget.common.CommonAppBar
import remix.myplayer.ui.widget.common.PopupButton
import remix.myplayer.ui.widget.common.TextPrimary
import remix.myplayer.ui.widget.common.TextSecondary
import remix.myplayer.util.MusicUtil
import remix.myplayer.util.Util
import remix.myplayer.util.ext.clickWithRipple
import remix.myplayer.viewmodel.playbackViewModel
import remix.myplayer.viewmodel.serverViewModel
import remix.myplayer.viewmodel.settingViewModel

@Composable
fun ServerDetailScreen(server: ServerConfig) {
  val nav = LocalNavController.current
  val serverVM = serverViewModel
  val playbackVM = playbackViewModel
  val settingVM = settingViewModel
  val scope = rememberCoroutineScope()
  val songState by serverVM.songState.collectAsStateWithLifecycle()

  BackHandler {
    nav.popBackStack()
  }

  Scaffold(
    topBar = {
      CommonAppBar(
        title = server.alias,
        onBack = {
          nav.popBackStack()
        }
      )
    },
    containerColor = LocalTheme.current.mainBackground
  ) { contentPadding ->
    val showLoading = songState is DataUiState.Loading

    LaunchedEffect(songState) {
      when (songState) {
        is DataUiState.Error -> {
          nav.popBackStack()
          MessageNotifier.show(R.string.load_failed)
        }

        else -> {}
      }
    }

    Column(modifier = Modifier.padding(contentPadding)) {
      Box(modifier = Modifier.weight(1f)) {
        when (songState) {
          is DataUiState.Success -> {
            val songs = songState.get()
            if (songs.isEmpty()) {
              Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                TextSecondary(stringResource(R.string.server_no_songs))
              }
            } else {
              LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(songs, key = { it.data }) { song ->
                  ServerSongItem(
                    song,
                    onClick = {
                      if (showLoading) return@ServerSongItem
                      MusicServiceRemote.setPlayQueue(
                        songs,
                        MusicUtil.makeCmdIntent(Command.PLAY_AT)
                          .putExtra(
                            MusicService.EXTRA_POSITION,
                            songs.indexOfFirst { it.data == song.data }
                          )
                      )
                    },
                    onMenuClick = {
                      when (it) {
                        R.string.add_to_next_song -> {
                          Util.sendLocalBroadcast(
                            MusicUtil.makeCmdIntent(Command.ADD_TO_NEXT_SONG)
                              .putExtra(MusicService.EXTRA_SONG, song)
                          )
                        }

                        R.string.add_to_play_queue -> {
                          playbackVM.insertToQueue(listOf(song))
                        }

                        R.string.song_detail -> {
                          scope.runWithLoading {
                            serverVM.fetchMeta(song)
                            settingVM.showSongDetailDialog(song)
                          }
                        }
                      }
                    })
                }
              }
            }
          }

          is DataUiState.Error -> {}

          is DataUiState.Loading -> {}
        }

        if (showLoading) {
          LinearProgressIndicator(
            modifier = Modifier
              .fillMaxWidth()
              .align(Alignment.TopCenter),
            color = LocalTheme.current.primary
          )
        }
      }
      BottomBar()
    }
  }

  LaunchedEffect(server.id) {
    serverVM.loadSongs(server)
  }
}

@Composable
private fun ServerSongItem(
  song: Song.Remote,
  onClick: () -> Unit,
  onMenuClick: (Int) -> Unit
) {
  val theme = LocalTheme.current

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(64.dp)
      .clickWithRipple(false) {
        onClick()
      },
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      modifier = Modifier.padding(start = 12.dp),
      painter = painterResource(R.drawable.ic_audio_file_24dp),
      contentDescription = "IconServerSongItem",
      tint = theme.icon()
    )

    Column(
      modifier = Modifier
        .padding(horizontal = 12.dp)
        .weight(1f),
      horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center
    ) {
      TextPrimary(song.title)
      Spacer(Modifier.height(4.dp))
      TextSecondary(
        listOfNotNull(song.artist.ifEmpty { null }, song.album.ifEmpty { null })
          .joinToString(" - ")
          .ifEmpty { song.data }
      )
    }

    PopupButton(
      listOf(
        R.string.add_to_next_song,
        R.string.add_to_play_queue,
        R.string.song_detail
      ),
      contentDescription = "ServerSongPopupButton",
      onMenuClick = onMenuClick
    )
  }
}
