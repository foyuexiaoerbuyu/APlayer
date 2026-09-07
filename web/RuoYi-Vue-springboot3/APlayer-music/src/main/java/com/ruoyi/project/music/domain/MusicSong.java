package com.ruoyi.project.music.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 音乐歌曲对象 music_song
 *
 * @author ruoyi
 */
public class MusicSong extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 歌曲ID */
    private Long id;

    /** 歌曲标题 */
    private String title;

    /** 歌手ID */
    private Long artistId;

    /** 歌手名(冗余) */
    private String artistName;

    /** 专辑ID */
    private Long albumId;

    /** 专辑名(冗余) */
    private String albumName;

    /** 流派 */
    private String genre;

    /** 封面路径 */
    private String coverPath;

    /** 时长(秒) */
    private Long duration;

    /** 状态(0正常 1停用) */
    private String status;

    /** 流派歌曲数(流派列表返回) */
    private Long genreCount;

    /** 关联音频资源(列表/详情返回) */
    private MusicResource resource;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }

    public void setArtistId(Long artistId)
    {
        this.artistId = artistId;
    }

    public Long getArtistId()
    {
        return artistId;
    }

    public void setArtistName(String artistName)
    {
        this.artistName = artistName;
    }

    public String getArtistName()
    {
        return artistName;
    }

    public void setAlbumId(Long albumId)
    {
        this.albumId = albumId;
    }

    public Long getAlbumId()
    {
        return albumId;
    }

    public void setAlbumName(String albumName)
    {
        this.albumName = albumName;
    }

    public String getAlbumName()
    {
        return albumName;
    }

    public void setGenre(String genre)
    {
        this.genre = genre;
    }

    public String getGenre()
    {
        return genre;
    }

    public void setCoverPath(String coverPath)
    {
        this.coverPath = coverPath;
    }

    public String getCoverPath()
    {
        return coverPath;
    }

    public void setDuration(Long duration)
    {
        this.duration = duration;
    }

    public Long getDuration()
    {
        return duration;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public Long getGenreCount()
    {
        return genreCount;
    }

    public void setGenreCount(Long genreCount)
    {
        this.genreCount = genreCount;
    }

    public MusicResource getResource()
    {
        return resource;
    }

    public void setResource(MusicResource resource)
    {
        this.resource = resource;
    }
}
