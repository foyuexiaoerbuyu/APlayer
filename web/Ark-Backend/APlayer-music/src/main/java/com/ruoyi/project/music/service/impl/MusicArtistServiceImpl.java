package com.ruoyi.project.music.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.project.music.domain.MusicArtist;
import com.ruoyi.project.music.mapper.MusicArtistMapper;
import com.ruoyi.project.music.service.IMusicArtistService;

/**
 * 音乐歌手 服务实现
 *
 * @author ruoyi
 */
@Service
public class MusicArtistServiceImpl implements IMusicArtistService
{
    @Autowired
    private MusicArtistMapper musicArtistMapper;

    @Override
    public List<MusicArtist> selectMusicArtistList(MusicArtist artist)
    {
        return musicArtistMapper.selectMusicArtistList(artist);
    }

    @Override
    public MusicArtist selectMusicArtistById(Long id)
    {
        return musicArtistMapper.selectMusicArtistById(id);
    }

    @Override
    public int insertMusicArtist(MusicArtist artist)
    {
        artist.setCreateTime(DateUtils.getNowDate());
        return musicArtistMapper.insertMusicArtist(artist);
    }

    @Override
    public int updateMusicArtist(MusicArtist artist)
    {
        artist.setUpdateTime(DateUtils.getNowDate());
        return musicArtistMapper.updateMusicArtist(artist);
    }

    @Override
    public int deleteMusicArtistByIds(Long[] ids)
    {
        return musicArtistMapper.deleteMusicArtistByIds(ids);
    }

    @Override
    public MusicArtist findOrCreate(String name)
    {
        if (name == null || name.trim().isEmpty())
        {
            return null;
        }
        MusicArtist exist = musicArtistMapper.selectMusicArtistByName(name.trim());
        if (exist != null)
        {
            return exist;
        }
        MusicArtist artist = new MusicArtist();
        artist.setName(name.trim());
        musicArtistMapper.insertMusicArtist(artist);
        return artist;
    }
}
