package com.ruoyi.project.music.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 音乐专辑对象 music_album
 *
 * @author ruoyi
 */
public class MusicAlbum extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 专辑ID */
    private Long id;

    /** 专辑名 */
    private String name;

    /** 封面路径 */
    private String coverPath;
    private Long songCount;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setCoverPath(String coverPath)
    {
        this.coverPath = coverPath;
    }

    public String getCoverPath()
    {
        return coverPath;
    }

    public Long getSongCount()
    {
        return songCount;
    }

    public void setSongCount(Long songCount)
    {
        this.songCount = songCount;
    }
}
