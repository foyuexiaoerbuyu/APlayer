<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="歌曲名称" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入歌曲名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="歌手" prop="artistName">
        <el-input
          v-model="queryParams.artistName"
          placeholder="请输入歌手名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="流派" prop="genre">
        <el-input
          v-model="queryParams.genre"
          placeholder="请输入流派"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Upload" @click="handleUpload">上传音频</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="songList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="60" />
      <el-table-column label="封面" align="center" width="60">
        <template #default="scope">
          <el-image
            v-if="scope.row.coverPath"
            :src="coverUrl(scope.row.coverPath)"
            fit="cover"
            style="width: 40px; height: 40px; border-radius: 4px"
            preview-teleported
            :preview-src-list="[coverUrl(scope.row.coverPath)]"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="歌曲名称" align="center" prop="title" :show-overflow-tooltip="true" />
      <el-table-column label="歌手" align="center" prop="artistName" :show-overflow-tooltip="true" />
      <el-table-column label="专辑" align="center" prop="albumName" :show-overflow-tooltip="true" />
      <el-table-column label="流派" align="center" prop="genre" width="90" />
      <el-table-column label="时长" align="center" prop="durationText" width="90" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="560px" append-to-body>
      <el-form ref="songRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="歌曲名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入歌曲名称" />
        </el-form-item>
        <el-form-item label="歌手" prop="artistName">
          <el-input v-model="form.artistName" placeholder="请输入歌手名称" />
        </el-form-item>
        <el-form-item label="专辑" prop="albumName">
          <el-input v-model="form.albumName" placeholder="请输入专辑名称" />
        </el-form-item>
        <el-form-item label="流派" prop="genre">
          <el-input v-model="form.genre" placeholder="请输入流派（如 流行）" />
        </el-form-item>
        <el-form-item label="时长" prop="duration">
          <el-input-number v-model="form.duration" :min="0" :max="7200" controls-position="right" placeholder="秒" style="width: 200px" />
        </el-form-item>
        <el-form-item label="封面">
          <el-upload
            :show-file-list="false"
            :http-request="handleCoverUpload"
            accept="image/*"
          >
            <el-image v-if="form.coverPath" :src="coverUrl(form.coverPath)" fit="cover" style="width: 80px; height: 80px; border-radius: 6px" />
            <el-icon v-else><Plus /></el-icon>
            <div style="margin-left: 10px; display: inline-block; vertical-align: middle; line-height: 80px; color: #909399">点击上传封面</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 上传音频对话框 -->
    <el-dialog title="上传音频" v-model="uploadOpen" width="560px" append-to-body>
      <el-form ref="uploadRef" :model="uploadForm" label-width="80px">
        <el-form-item label="音频文件" required>
          <el-upload
            drag
            :auto-upload="false"
            :limit="1"
            accept="audio/mpeg,audio/mp3"
            :on-change="handleFileChange"
            :on-remove="() => (uploadForm.file = null)"
            style="width: 100%"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">将 MP3 文件拖到此处，或<em>点击选择</em></div>
          </el-upload>
        </el-form-item>
        <el-form-item label="歌曲名称">
          <el-input v-model="uploadForm.title" placeholder="留空则自动读取标签" />
        </el-form-item>
        <el-form-item label="歌手">
          <el-input v-model="uploadForm.artist" placeholder="留空则自动读取标签" />
        </el-form-item>
        <el-form-item label="专辑">
          <el-input v-model="uploadForm.album" placeholder="留空则自动读取标签" />
        </el-form-item>
        <el-form-item label="流派">
          <el-input v-model="uploadForm.genre" placeholder="如：流行" />
        </el-form-item>
        <el-form-item label="文件夹">
          <el-input v-model="uploadForm.folder" placeholder="上传目录子文件夹，默认 music" />
        </el-form-item>
        <el-form-item label="封面">
          <el-upload
            :auto-upload="false"
            :limit="1"
            accept="image/*"
            :on-change="handleCoverFileChange"
            :on-remove="() => (uploadForm.cover = null)"
            style="width: 100%"
          >
            <div class="el-upload__text">点击选择封面图片（可选）</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" :loading="uploading" @click="submitUpload">开始上传</el-button>
          <el-button @click="uploadOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Song">
import { listSong, getSong, addSong, updateSong, delSong, uploadSong } from '@/api/music/song'
import { getToken } from '@/utils/auth'

const { proxy } = getCurrentInstance()

const songList = ref([])
const open = ref(false)
const uploadOpen = ref(false)
const loading = ref(true)
const uploading = ref(false)
const showSearch = ref(true)
const ids = ref([])
const multiple = ref(true)
const total = ref(0)
const title = ref('')

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    title: undefined,
    artistName: undefined,
    genre: undefined
  },
  rules: {
    title: [{ required: true, message: '歌曲名称不能为空', trigger: 'blur' }]
  },
  uploadForm: {
    file: null,
    title: undefined,
    artist: undefined,
    album: undefined,
    genre: undefined,
    folder: undefined,
    cover: null
  }
})

const { queryParams, form, rules, uploadForm } = toRefs(data)

function coverUrl(path) {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return import.meta.env.VITE_APP_BASE_API + '/music/media/cover/' + path
}

/** 查询歌曲列表 */
function getList() {
  loading.value = true
  listSong(queryParams.value).then(response => {
    const rows = response.rows || []
    rows.forEach(item => {
      if (item.duration != null) {
        const s = Number(item.duration)
        const m = Math.floor(s / 60)
        const sec = (s % 60).toString().padStart(2, '0')
        item.durationText = m + ':' + sec
      } else {
        item.durationText = '-'
      }
    })
    songList.value = rows
    total.value = response.total || 0
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    id: undefined,
    title: undefined,
    artistName: undefined,
    albumName: undefined,
    genre: undefined,
    duration: undefined,
    coverPath: undefined
  }
  proxy.resetForm('songRef')
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = '新增歌曲'
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const id = row.id || ids.value[0]
  getSong(id).then(response => {
    form.value = response.data
    open.value = true
    title.value = '修改歌曲'
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs['songRef'].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateSong(form.value).then(() => {
          proxy.$modal.msgSuccess('修改成功')
          open.value = false
          getList()
        })
      } else {
        addSong(form.value).then(() => {
          proxy.$modal.msgSuccess('新增成功')
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const songIds = row.id || ids.value
  proxy.$modal.confirm('是否确认删除歌曲编号为"' + songIds + '"的数据项？').then(() => {
    return delSong(songIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

/** 上传音频弹窗 */
function handleUpload() {
  uploadForm.value = { file: null, title: undefined, artist: undefined, album: undefined, genre: undefined, folder: undefined, cover: null }
  uploadOpen.value = true
}

function handleFileChange(file) {
  uploadForm.value.file = file.raw
}

function handleCoverFileChange(file) {
  uploadForm.value.cover = file.raw
}

/** 提交上传 */
function submitUpload() {
  if (!uploadForm.value.file) {
    proxy.$modal.msgWarning('请先选择音频文件')
    return
  }
  const fd = new FormData()
  fd.append('file', uploadForm.value.file)
  if (uploadForm.value.title) fd.append('title', uploadForm.value.title)
  if (uploadForm.value.artist) fd.append('artist', uploadForm.value.artist)
  if (uploadForm.value.album) fd.append('album', uploadForm.value.album)
  if (uploadForm.value.genre) fd.append('genre', uploadForm.value.genre)
  const folderVal = uploadForm.value.folder?.trim() || 'music'
  fd.append('folder', folderVal)
  if (uploadForm.value.cover) fd.append('cover', uploadForm.value.cover)
  uploading.value = true
  uploadSong(fd).then(response => {
    uploading.value = false
    proxy.$modal.msgSuccess(response.msg || '上传成功')
    uploadOpen.value = false
    getList()
  }).catch(() => {
    uploading.value = false
  })
}

/** 封面上传 */
function handleCoverUpload(options) {
  const fd = new FormData()
  fd.append('cover', options.file)
  // 复用上传接口：仅传封面文件时后端会忽略空音频
  fd.append('file', new File([new Blob([''])], 'empty.mp3', { type: 'audio/mpeg' }))
  fd.append('coverOnly', 'true')
  uploadSong(fd).then(response => {
    proxy.$modal.msgSuccess('封面上传成功')
    form.value.coverPath = response.data || form.value.coverPath
  }).catch(() => {
    proxy.$modal.msgError('封面上传失败')
  })
}

getList()
</script>
