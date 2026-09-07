package com.ruoyi.project.music.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.project.music.domain.MusicSong;
import com.ruoyi.project.music.mapper.MusicSongMapper;
import com.ruoyi.project.music.service.IMusicSongService;

/**
 * 音乐歌曲 服务实现
 *
 * @author ruoyi
 */
@Service
public class MusicSongServiceImpl implements IMusicSongService
{
    @Autowired
    private MusicSongMapper musicSongMapper;

    @Override
    public List<MusicSong> selectMusicSongList(MusicSong song)
    {
        return musicSongMapper.selectMusicSongList(song);
    }

    @Override
    public MusicSong selectMusicSongById(Long id)
    {
        return musicSongMapper.selectMusicSongById(id);
    }

    /**
     * 查询流派列表(含歌曲数)
     *
     * @return 流派集合
     */
    @Override
    public List<MusicSong> selectGenreList()
    {
        return musicSongMapper.selectGenreList();
    }

    @Override
    public int insertMusicSong(MusicSong song)
    {
        song.setCreateTime(DateUtils.getNowDate());
        return musicSongMapper.insertMusicSong(song);
    }

    @Override
    public int updateMusicSong(MusicSong song)
    {
        song.setUpdateTime(DateUtils.getNowDate());
        return musicSongMapper.updateMusicSong(song);
    }

    @Override
    public int deleteMusicSongByIds(Long[] ids)
    {
        return musicSongMapper.deleteMusicSongByIds(ids);
    }
}
