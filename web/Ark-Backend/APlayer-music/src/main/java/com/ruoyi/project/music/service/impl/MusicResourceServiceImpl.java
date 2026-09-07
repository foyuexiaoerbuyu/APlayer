package com.ruoyi.project.music.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.project.music.domain.MusicResource;
import com.ruoyi.project.music.mapper.MusicResourceMapper;
import com.ruoyi.project.music.service.IMusicResourceService;

/**
 * 音乐音频资源 服务实现
 *
 * @author ruoyi
 */
@Service
public class MusicResourceServiceImpl implements IMusicResourceService
{
    @Autowired
    private MusicResourceMapper musicResourceMapper;

    @Override
    public List<MusicResource> selectMusicResourceList(MusicResource resource)
    {
        return musicResourceMapper.selectMusicResourceList(resource);
    }

    @Override
    public MusicResource selectMusicResourceById(Long id)
    {
        return musicResourceMapper.selectMusicResourceById(id);
    }

    @Override
    public MusicResource selectAudioResourceBySongId(Long songId)
    {
        return musicResourceMapper.selectAudioResourceBySongId(songId);
    }

    @Override
    public int insertMusicResource(MusicResource resource)
    {
        resource.setCreateTime(DateUtils.getNowDate());
        return musicResourceMapper.insertMusicResource(resource);
    }

    @Override
    public int updateMusicResource(MusicResource resource)
    {
        resource.setUpdateTime(DateUtils.getNowDate());
        return musicResourceMapper.updateMusicResource(resource);
    }

    @Override
    public int deleteMusicResourceByIds(Long[] ids)
    {
        return musicResourceMapper.deleteMusicResourceByIds(ids);
    }
}
