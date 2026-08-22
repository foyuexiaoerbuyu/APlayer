import request from '@/utils/request'

// 查询歌曲列表
export function listSong(query) {
  return request({
    url: '/music/song/list',
    method: 'get',
    params: query
  })
}

// 查询歌曲详细
export function getSong(id) {
  return request({
    url: '/music/song/' + id,
    method: 'get'
  })
}

// 新增歌曲
export function addSong(data) {
  return request({
    url: '/music/song',
    method: 'post',
    data: data
  })
}

// 修改歌曲
export function updateSong(data) {
  return request({
    url: '/music/song',
    method: 'put',
    data: data
  })
}

// 删除歌曲
export function delSong(ids) {
  return request({
    url: '/music/song/' + ids,
    method: 'delete'
  })
}

// 上传音频文件（自动解析标签，可指定 title/artist/album/genre/cover）
export function uploadSong(data) {
  return request({
    url: '/music/song/upload',
    method: 'post',
    data: data,
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 60000
  })
}

// 查询专辑列表
export function listAlbum() {
  return request({
    url: '/music/album/list',
    method: 'get',
    params: { pageNum: 1, pageSize: 1000 }
  })
}

// 查询歌手列表
export function listArtist() {
  return request({
    url: '/music/artist/list',
    method: 'get',
    params: { pageNum: 1, pageSize: 1000 }
  })
}
