package com.ruoyi.project.music.service;

import java.util.List;
import com.ruoyi.project.music.domain.MusicAlbum;

/**
 * 音乐专辑 服务层
 *
 * @author ruoyi
 */
public interface IMusicAlbumService
{
    /**
     * 查询专辑列表
     *
     * @param album 专辑信息
     * @return 专辑集合
     */
    public List<MusicAlbum> selectMusicAlbumList(MusicAlbum album);

    /**
     * 根据ID查询专辑
     *
     * @param id 专辑ID
     * @return 专辑
     */
    public MusicAlbum selectMusicAlbumById(Long id);

    /**
     * 新增专辑
     *
     * @param album 专辑信息
     * @return 结果
     */
    public int insertMusicAlbum(MusicAlbum album);

    /**
     * 修改专辑
     *
     * @param album 专辑信息
     * @return 结果
     */
    public int updateMusicAlbum(MusicAlbum album);

    /**
     * 删除专辑
     *
     * @param ids 需要删除的专辑ID
     * @return 结果
     */
    public int deleteMusicAlbumByIds(Long[] ids);

    /**
     * 按名称查找，不存在则创建，返回专辑
     */
    public MusicAlbum findOrCreate(String name, String coverPath);
}
