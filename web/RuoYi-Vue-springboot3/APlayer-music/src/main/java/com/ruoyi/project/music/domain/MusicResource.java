package com.ruoyi.project.music.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 音乐音频资源对象 music_resource
 *
 * @author ruoyi
 */
public class MusicResource extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 资源ID */
    private Long id;

    /** 歌曲ID */
    private Long songId;

    /** 资源类型(AUDIO/COVER/LYRIC) */
    private String resourceType;

    /** 格式(mp3/flac...) */
    private String format;

    /** 编码 */
    private String codec;

    /** 码率(kbps) */
    private Integer bitrate;

    /** 采样率(Hz) */
    private Integer sampleRate;

    /** 声道数 */
    private Integer channels;

    /** 文件大小(字节) */
    private Long fileSize;

    /** 文件路径 */
    private String filePath;

    /** 文件哈希 */
    private String fileHash;

    /** 时长(秒) */
    private Long duration;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setSongId(Long songId)
    {
        this.songId = songId;
    }

    public Long getSongId()
    {
        return songId;
    }

    public void setResourceType(String resourceType)
    {
        this.resourceType = resourceType;
    }

    public String getResourceType()
    {
        return resourceType;
    }

    public void setFormat(String format)
    {
        this.format = format;
    }

    public String getFormat()
    {
        return format;
    }

    public void setCodec(String codec)
    {
        this.codec = codec;
    }

    public String getCodec()
    {
        return codec;
    }

    public void setBitrate(Integer bitrate)
    {
        this.bitrate = bitrate;
    }

    public Integer getBitrate()
    {
        return bitrate;
    }

    public void setSampleRate(Integer sampleRate)
    {
        this.sampleRate = sampleRate;
    }

    public Integer getSampleRate()
    {
        return sampleRate;
    }

    public void setChannels(Integer channels)
    {
        this.channels = channels;
    }

    public Integer getChannels()
    {
        return channels;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFileHash(String fileHash)
    {
        this.fileHash = fileHash;
    }

    public String getFileHash()
    {
        return fileHash;
    }

    public void setDuration(Long duration)
    {
        this.duration = duration;
    }

    public Long getDuration()
    {
        return duration;
    }
}
