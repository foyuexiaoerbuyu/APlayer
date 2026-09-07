package com.ruoyi.project.music.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.project.music.domain.MusicAlbum;
import com.ruoyi.project.music.mapper.MusicAlbumMapper;
import com.ruoyi.project.music.service.IMusicAlbumService;

/**
 * 音乐专辑 服务实现
 *
 * @author ruoyi
 */
@Service
public class MusicAlbumServiceImpl implements IMusicAlbumService
{
    @Autowired
    private MusicAlbumMapper musicAlbumMapper;

    @Override
    public List<MusicAlbum> selectMusicAlbumList(MusicAlbum album)
    {
        return musicAlbumMapper.selectMusicAlbumList(album);
    }

    @Override
    public MusicAlbum selectMusicAlbumById(Long id)
    {
        return musicAlbumMapper.selectMusicAlbumById(id);
    }

    @Override
    public int insertMusicAlbum(MusicAlbum album)
    {
        album.setCreateTime(DateUtils.getNowDate());
        return musicAlbumMapper.insertMusicAlbum(album);
    }

    @Override
    public int updateMusicAlbum(MusicAlbum album)
    {
        album.setUpdateTime(DateUtils.getNowDate());
        return musicAlbumMapper.updateMusicAlbum(album);
    }

    @Override
    public int deleteMusicAlbumByIds(Long[] ids)
    {
        return musicAlbumMapper.deleteMusicAlbumByIds(ids);
    }

    @Override
    public MusicAlbum findOrCreate(String name, String coverPath)
    {
        if (name == null || name.trim().isEmpty())
        {
            return null;
        }
        MusicAlbum exist = musicAlbumMapper.selectMusicAlbumByName(name.trim());
        if (exist != null)
        {
            return exist;
        }
        MusicAlbum album = new MusicAlbum();
        album.setName(name.trim());
        album.setCoverPath(coverPath);
        musicAlbumMapper.insertMusicAlbum(album);
        return album;
    }
}
