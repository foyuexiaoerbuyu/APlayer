package com.ruoyi.project.music.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.project.music.domain.MusicAlbum;
import com.ruoyi.project.music.domain.MusicArtist;
import com.ruoyi.project.music.domain.MusicResource;
import com.ruoyi.project.music.domain.MusicSong;
import com.ruoyi.project.music.service.IMusicAlbumService;
import com.ruoyi.project.music.service.IMusicArtistService;
import com.ruoyi.project.music.service.IMusicResourceService;
import com.ruoyi.project.music.service.IMusicSongService;

/**
 * 音乐曲库 信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/music")
public class MusicSongController extends BaseController
{
    @Autowired
    private IMusicSongService musicSongService;

    @Autowired
    private IMusicArtistService musicArtistService;

    @Autowired
    private IMusicAlbumService musicAlbumService;

    @Autowired
    private IMusicResourceService musicResourceService;

    @Autowired
    private MusicFileService musicFileService;

    /**
     * 查询歌曲列表（游客可访问）
     */
    @GetMapping("/song/list")
    public TableDataInfo list(MusicSong song)
    {
        startPage();
        List<MusicSong> list = musicSongService.selectMusicSongList(song);
        return getDataTable(list);
    }

    /**
     * 查询歌手列表（游客可访问）
     */
    @GetMapping("/artist/list")
    public TableDataInfo artistList(MusicArtist artist)
    {
        startPage();
        List<MusicArtist> list = musicArtistService.selectMusicArtistList(artist);
        return getDataTable(list);
    }

    /**
     * 查询专辑列表（游客可访问）
     */
    @GetMapping("/album/list")
    public TableDataInfo albumList(MusicAlbum album)
    {
        startPage();
        List<MusicAlbum> list = musicAlbumService.selectMusicAlbumList(album);
        return getDataTable(list);
    }

    /**
     * 查询流派列表（游客可访问）
     */
    @GetMapping("/genre/list")
    public TableDataInfo genreList()
    {
        startPage();
        List<MusicSong> list = musicSongService.selectGenreList();
        return getDataTable(list);
    }

    /**
     * 获取歌曲详细信息（游客可访问）
     */
    @GetMapping(value = "/song/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(musicSongService.selectMusicSongById(id));
    }

    /**
     * 新增歌曲
     */
    @Log(title = "音乐曲库", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('music:song:add')")
    @PostMapping("/song")
    public AjaxResult add(@RequestBody MusicSong song)
    {
        return toAjax(musicSongService.insertMusicSong(song));
    }

    /**
     * 修改歌曲
     */
    @Log(title = "音乐曲库", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('music:song:edit')")
    @PutMapping("/song")
    public AjaxResult edit(@RequestBody MusicSong song)
    {
        return toAjax(musicSongService.updateMusicSong(song));
    }

    /**
     * 删除歌曲（同时删除关联音频资源）
     */
    @Log(title = "音乐曲库", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('music:song:remove')")
    @DeleteMapping("/song/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        for (Long id : ids)
        {
            musicResourceService.deleteMusicResourceByIds(getResourceIdsBySong(id));
        }
        return toAjax(musicSongService.deleteMusicSongByIds(ids));
    }

    /**
     * 上传音频文件（自动解析元数据并入库）
     *
     * @param file 音频文件
     * @param title 歌曲标题(可选,缺省用文件名)
     * @param artist 歌手名(可选)
     * @param album 专辑名(可选)
     * @param cover 封面文件(可选)
     * @return 歌曲ID
     */
    @Log(title = "音乐曲库", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('music:song:add')")
    @PostMapping("/song/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "artist", required = false) String artist,
            @RequestParam(value = "album", required = false) String album,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "cover", required = false) MultipartFile cover,
            @RequestParam(value = "folder", required = false) String folder,
            @RequestParam(value = "coverOnly", required = false) Boolean coverOnly)
    {
        try
        {
            // 仅上传封面：直接保存封面并返回路径，不创建歌曲/歌手/专辑/资源记录
            if (coverOnly != null && coverOnly)
            {
                String coverPath = musicFileService.saveCoverOnly(cover);
                return AjaxResult.success(coverPath);
            }
            if (file.isEmpty())
            {
                return AjaxResult.error("上传文件不能为空");
            }
            // 保存音频并解析元数据（folder 为空时后端默认 music）
            AudioParseResult parse = musicFileService.saveAudio(file, cover, folder);
            Long artistId = null;
            Long albumId = null;
            // 歌手入库
            if (StringUtils.isNotEmpty(artist))
            {
                MusicArtist a = musicArtistService.findOrCreate(artist);
                if (a != null)
                {
                    artistId = a.getId();
                    artist = a.getName();
                }
            }
            else
            {
                artist = parse.getArtist();
                if (StringUtils.isNotEmpty(artist))
                {
                    MusicArtist a = musicArtistService.findOrCreate(artist);
                    if (a != null)
                    {
                        artistId = a.getId();
                        artist = a.getName();
                    }
                }
            }
            // 专辑入库
            if (StringUtils.isNotEmpty(album))
            {
                MusicAlbum al = musicAlbumService.findOrCreate(album, parse.getCoverPath());
                if (al != null)
                {
                    albumId = al.getId();
                    album = al.getName();
                }
            }
            // 歌曲入库
            MusicSong song = new MusicSong();
            song.setTitle(StringUtils.isNotEmpty(title) ? title : parse.getTitle());
            song.setArtistId(artistId);
            song.setArtistName(artist);
            song.setAlbumId(albumId);
            song.setAlbumName(album);
            song.setGenre(StringUtils.isNotEmpty(genre) ? genre : parse.getGenre());
            song.setCoverPath(parse.getCoverPath());
            song.setDuration(parse.getDuration());
            song.setStatus("0");
            musicSongService.insertMusicSong(song);
            // 资源入库
            MusicResource resource = new MusicResource();
            resource.setSongId(song.getId());
            resource.setResourceType("AUDIO");
            resource.setFormat(parse.getFormat());
            resource.setCodec(parse.getCodec());
            resource.setBitrate(parse.getBitrate());
            resource.setSampleRate(parse.getSampleRate());
            resource.setChannels(parse.getChannels());
            resource.setFileSize(parse.getFileSize());
            resource.setFilePath(parse.getFilePath());
            resource.setFileHash(parse.getFileHash());
            resource.setDuration(parse.getDuration());
            musicResourceService.insertMusicResource(resource);
            AjaxResult ajax = AjaxResult.success();
            ajax.put("songId", song.getId());
            return ajax;
        }
        catch (Exception e)
        {
            logger.error("上传歌曲失败", e);
            return AjaxResult.error("上传失败：" + e.getMessage());
        }
    }

    private Long[] getResourceIdsBySong(Long songId)
    {
        MusicResource query = new MusicResource();
        query.setSongId(songId);
        List<MusicResource> list = musicResourceService.selectMusicResourceList(query);
        return list.stream().map(MusicResource::getId).toArray(Long[]::new);
    }
}
