package com.ruoyi.project.music.service;

import java.util.List;
import com.ruoyi.project.music.domain.MusicArtist;

/**
 * 音乐歌手 服务层
 *
 * @author ruoyi
 */
public interface IMusicArtistService
{
    /**
     * 查询歌手列表
     *
     * @param artist 歌手信息
     * @return 歌手集合
     */
    public List<MusicArtist> selectMusicArtistList(MusicArtist artist);

    /**
     * 根据ID查询歌手
     *
     * @param id 歌手ID
     * @return 歌手
     */
    public MusicArtist selectMusicArtistById(Long id);

    /**
     * 新增歌手
     *
     * @param artist 歌手信息
     * @return 结果
     */
    public int insertMusicArtist(MusicArtist artist);

    /**
     * 修改歌手
     *
     * @param artist 歌手信息
     * @return 结果
     */
    public int updateMusicArtist(MusicArtist artist);

    /**
     * 删除歌手
     *
     * @param ids 需要删除的歌手ID
     * @return 结果
     */
    public int deleteMusicArtistByIds(Long[] ids);

    /**
     * 按名称查找，不存在则创建，返回歌手
     */
    public MusicArtist findOrCreate(String name);
}
