package com.ruoyi.project.music.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 音乐歌手对象 music_artist
 *
 * @author ruoyi
 */
public class MusicArtist extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 歌手ID */
    private Long id;

    /** 歌手名 */
    private String name;
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

    public Long getSongCount()
    {
        return songCount;
    }

    public void setSongCount(Long songCount)
    {
        this.songCount = songCount;
    }
}
