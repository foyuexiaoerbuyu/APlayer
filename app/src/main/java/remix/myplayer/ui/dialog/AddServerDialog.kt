package remix.myplayer.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import remix.myplayer.R
import remix.myplayer.data.db.room.entity.ServerConfig
import remix.myplayer.ui.widget.common.EditField
import remix.myplayer.viewmodel.serverViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddServerDialog(onPositive: (String, String, String, String, ServerConfig?) -> Unit) {
  val vm = serverViewModel
  val state by vm.addServerState.collectAsStateWithLifecycle()

  val alias = state.alias
  val account = state.account
  val pwd = state.pwd
  val server = state.server

  fun reset() {
    vm.updateAddServerState("", "", "", "")
  }

  NormalDialog(
    dialogState = state.dialogState,
    onDismissRequest = {
      reset()
    },
    titleRes = R.string.tab_server,
    positiveRes = if (state.editServer == null) R.string.add else R.string.update,
    onPositive = {
      reset()
      onPositive(alias, account, pwd, server, state.editServer)
    },
    negativeRes = null,
    custom = {
      Column {
        EditField(alias, R.string.alias, isError = alias.isEmpty()) {
          vm.updateAddServerState(alias = it)
        }
        EditField(
          account,
          R.string.account,
          isError = account.isEmpty(),
          contentType = ContentType.Username
        ) {
          vm.updateAddServerState(account = it)
        }
        EditField(
          pwd,
          R.string.pwd,
          isError = pwd.isEmpty(),
          contentType = ContentType.Password,
          keyboardType = KeyboardType.Password,
          visualTransformation = PasswordVisualTransformation()
        ) {
          vm.updateAddServerState(pwd = it)
        }
        EditField(
          server,
          R.string.server_hint_url,
          isError = server.isEmpty(),
          keyboardType = KeyboardType.Uri
        ) {
          vm.updateAddServerState(server = it)
        }
      }
    }
  )
}
