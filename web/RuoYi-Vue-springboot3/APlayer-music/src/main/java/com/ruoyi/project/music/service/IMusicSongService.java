package com.ruoyi.project.music.service;

import java.util.List;
import com.ruoyi.project.music.domain.MusicSong;

/**
 * 音乐歌曲 服务层
 *
 * @author ruoyi
 */
public interface IMusicSongService
{
    /**
     * 查询歌曲列表
     *
     * @param song 歌曲信息
     * @return 歌曲集合
     */
    public List<MusicSong> selectMusicSongList(MusicSong song);

    /**
     * 根据ID查询歌曲
     *
     * @param id 歌曲ID
     * @return 歌曲
     */
    public MusicSong selectMusicSongById(Long id);

    /**
     * 查询流派列表(含歌曲数)
     *
     * @return 流派集合
     */
    public List<MusicSong> selectGenreList();

    /**
     * 新增歌曲
     *
     * @param song 歌曲信息
     * @return 结果
     */
    public int insertMusicSong(MusicSong song);

    /**
     * 修改歌曲
     *
     * @param song 歌曲信息
     * @return 结果
     */
    public int updateMusicSong(MusicSong song);

    /**
     * 删除歌曲
     *
     * @param ids 需要删除的歌曲ID
     * @return 结果
     */
    public int deleteMusicSongByIds(Long[] ids);
}
