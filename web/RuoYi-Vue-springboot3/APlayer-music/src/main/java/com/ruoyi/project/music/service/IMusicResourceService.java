package com.ruoyi.project.music.service;

import java.util.List;
import com.ruoyi.project.music.domain.MusicResource;

/**
 * 音乐音频资源 服务层
 *
 * @author ruoyi
 */
public interface IMusicResourceService
{
    /**
     * 查询资源列表
     *
     * @param resource 资源信息
     * @return 资源集合
     */
    public List<MusicResource> selectMusicResourceList(MusicResource resource);

    /**
     * 根据ID查询资源
     *
     * @param id 资源ID
     * @return 资源
     */
    public MusicResource selectMusicResourceById(Long id);

    /**
     * 根据歌曲ID查询音频资源
     *
     * @param songId 歌曲ID
     * @return 资源
     */
    public MusicResource selectAudioResourceBySongId(Long songId);

    /**
     * 新增资源
     *
     * @param resource 资源信息
     * @return 结果
     */
    public int insertMusicResource(MusicResource resource);

    /**
     * 修改资源
     *
     * @param resource 资源信息
     * @return 结果
     */
    public int updateMusicResource(MusicResource resource);

    /**
     * 删除资源
     *
     * @param ids 需要删除的资源ID
     * @return 结果
     */
    public int deleteMusicResourceByIds(Long[] ids);
}
