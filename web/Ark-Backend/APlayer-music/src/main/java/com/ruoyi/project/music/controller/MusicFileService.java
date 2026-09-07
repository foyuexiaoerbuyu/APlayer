package com.ruoyi.project.music.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.config.RuoYiConfig;

/**
 * 音乐文件服务：保存音频/封面文件，并解析音频元数据（时长、码率、采样率等）
 */
@Service
public class MusicFileService
{
    private static final Logger log = LoggerFactory.getLogger(MusicFileService.class);

    private static final List<String> AUDIO_EXT = Arrays.asList("mp3", "wav", "flac", "m4a", "aac", "ogg", "opus");

    /**
     * 保存音频文件并解析元数据
     */
    public AudioParseResult saveAudio(MultipartFile file, MultipartFile cover, String folder) throws Exception
    {
        String extension = getExt(file.getOriginalFilename());
        if (!AUDIO_EXT.contains(extension.toLowerCase()))
        {
            throw new IOException("不支持的文件格式：" + extension);
        }
        // 上传目录：folder 为空或为默认值 music 时上传到 music 根目录，否则上传到 music/子文件夹；
        // 目录名经 sanitize 过滤非法字符防路径穿越
        String subFolder = StringUtils.isEmpty(folder) ? "music" : sanitize(folder);
        String baseDir = "music".equals(subFolder)
                ? RuoYiConfig.getProfile() + "/music"
                : RuoYiConfig.getProfile() + "/music/" + subFolder;
        File dir = new File(baseDir);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        String fileName = System.currentTimeMillis() + "_" + sanitize(file.getOriginalFilename());
        File dest = new File(dir, fileName);
        file.transferTo(dest);
        String filePath = dest.getAbsolutePath();
        if (!dest.exists())
        {
            throw new IOException("文件保存失败");
        }

        AudioParseResult result = new AudioParseResult();
        result.setFilePath(filePath);
        result.setFileSize(dest.length());
        result.setFormat(extension.toLowerCase());
        result.setTitle(stripExt(file.getOriginalFilename()));

        // 解析 MP3 元数据
        parseMp3(dest, result);

        // 封面
        if (cover != null && !cover.isEmpty())
        {
            String coverName = System.currentTimeMillis() + "_" + sanitize(cover.getOriginalFilename());
            File coverDir = new File(RuoYiConfig.getProfile() + "/music/cover");
            if (!coverDir.exists())
            {
                coverDir.mkdirs();
            }
            cover.transferTo(new File(coverDir, coverName));
            result.setCoverPath("/profile/music/cover/" + coverName);
        }
        return result;
    }

    /**
     * 仅保存封面文件到 music/cover/ 目录，返回访问路径
     */
    public String saveCoverOnly(MultipartFile cover) throws IOException
    {
        if (cover == null || cover.isEmpty())
        {
            throw new IOException("封面文件不能为空");
        }
        String coverName = System.currentTimeMillis() + "_" + sanitize(cover.getOriginalFilename());
        File coverDir = new File(RuoYiConfig.getProfile() + "/music/cover");
        if (!coverDir.exists())
        {
            coverDir.mkdirs();
        }
        cover.transferTo(new File(coverDir, coverName));
        return "/profile/music/cover/" + coverName;
    }

    private String sanitize(String name)
    {
        if (StringUtils.isEmpty(name))
        {
            return "file";
        }
        String s = name.replaceAll("[\\\\/:*?\"<>|]", "_");
        return s.length() > 80 ? s.substring(s.length() - 80) : s;
    }

    /**
     * 解析 MP3 帧头：码率/采样率/声道/时长
     */
    private void parseMp3(File file, AudioParseResult result)
    {
        if (!"mp3".equalsIgnoreCase(result.getFormat()))
        {
            // 非 MP3 直接读文件大小估算（由前端/测试脚本补时长），此处保守置 0
            result.setDuration(0L);
            return;
        }
        try
        {
            byte[] head = new byte[4096];
            int len = (int) Math.min(file.length(), head.length);
            try (java.io.InputStream in = Files.newInputStream(Paths.get(file.getAbsolutePath())))
            {
                int read = in.read(head, 0, len);
                if (read <= 0)
                {
                    result.setDuration(0L);
                    return;
                }
            }
            // 跳过 ID3v2 头
            int offset = 0;
            if (head.length >= 10 && (head[0] & 0xFF) == 0x49 && (head[1] & 0xFF) == 0x44
                    && (head[2] & 0xFF) == 0x33)
            {
                int size = ((head[6] & 0x7F) << 21) | ((head[7] & 0x7F) << 14) | ((head[8] & 0x7F) << 7)
                        | (head[9] & 0x7F);
                offset = 10 + size;
                // 解析 ID3v2 文本帧(标题/歌手/专辑/流派)
                parseId3v2(head, len, result);
            }
            // 找有效帧头
            int frameStart = findFrameHeader(head, offset, len - 4);
            if (frameStart < 0)
            {
                result.setDuration(0L);
                return;
            }
            int b1 = head[frameStart + 1] & 0xFF;
            int b2 = head[frameStart + 2] & 0xFF;
            int version = (b1 >> 3) & 0x3; // 0=MPEG2.5 2=MPEG2 3=MPEG1
            int layer = (b1 >> 1) & 0x3; // 1=LayerIII
            int bitrateIndex = (b2 >> 4) & 0xF;
            int sampleIndex = (b2 >> 2) & 0x3;
            int channelMode = (b2 >> 6) & 0x3; // 3=单声道

            int[] bitrates = { 0, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, 0 };
            int[] sampleRatesV1 = { 44100, 48000, 32000, 0 };
            int[] sampleRatesV2 = { 22050, 24000, 16000, 0 };

            int kbps = bitrates[bitrateIndex];
            int sr = 0;
            if (version == 3)
            {
                sr = sampleRatesV1[sampleIndex];
            }
            else if (version == 2)
            {
                sr = sampleRatesV2[sampleIndex];
            }
            result.setBitrate(kbps * 1000);
            result.setSampleRate(sr);
            result.setChannels(channelMode == 3 ? 1 : 2);
            result.setCodec("mp3");
            if (kbps > 0)
            {
                // 时长 = (文件大小 - 标签头) * 8 / 码率
                long audioBytes = file.length() - frameStart;
                long durationMs = audioBytes * 8L / (kbps * 1000L);
                result.setDuration(durationMs / 1000L);
            }
        }
        catch (Exception e)
        {
            log.warn("解析MP3元数据失败", e);
            result.setDuration(0L);
        }
    }

    private int findFrameHeader(byte[] data, int start, int max)
    {
        for (int i = start; i < max && i < data.length - 1; i++)
        {
            int b0 = data[i] & 0xFF;
            if (b0 != 0xFF)
            {
                continue;
            }
            int b1 = data[i + 1] & 0xFF;
            if ((b1 & 0xE0) != 0xE0)
            {
                continue;
            }
            int layer = (b1 >> 1) & 0x3;
            if (layer == 0)
            {
                continue;
            }
            return i;
        }
        return -1;
    }

    /**
     * 解析 ID3v2 文本帧：TIT2 标题、TPE1 歌手、TALB 专辑、TCON 流派
     */
    private void parseId3v2(byte[] data, int len, AudioParseResult result)
    {
        try
        {
            if (len < 10)
            {
                return;
            }
            // 版本
            int major = data[3] & 0xFF;
            int pos = 10;
            while (pos + 10 <= len)
            {
                String frameId = new String(data, pos, 4, "ISO-8859-1");
                int frameSize;
                if (major == 4)
                {
                    frameSize = ((data[pos + 4] & 0x7F) << 21) | ((data[pos + 5] & 0x7F) << 14)
                            | ((data[pos + 6] & 0x7F) << 7) | (data[pos + 7] & 0x7F);
                }
                else
                {
                    frameSize = ((data[pos + 4] & 0xFF) << 24) | ((data[pos + 5] & 0xFF) << 16)
                            | ((data[pos + 6] & 0xFF) << 8) | (data[pos + 7] & 0xFF);
                }
                if (frameSize <= 0 || pos + 10 + frameSize > len)
                {
                    break;
                }
                int textStart = pos + 10;
                String value = decodeId3Text(data, textStart, frameSize);
                if (StringUtils.isNotEmpty(value))
                {
                    if ("TIT2".equals(frameId) && StringUtils.isEmpty(result.getTitle()))
                    {
                        result.setTitle(value);
                    }
                    else if ("TPE1".equals(frameId))
                    {
                        result.setArtist(value);
                    }
                    else if ("TALB".equals(frameId))
                    {
                        result.setAlbum(value);
                    }
                    else if ("TCON".equals(frameId))
                    {
                        result.setGenre(value);
                    }
                }
                pos = pos + 10 + frameSize;
            }
        }
        catch (Exception e)
        {
            log.debug("解析ID3v2标签失败", e);
        }
    }

    private String decodeId3Text(byte[] data, int start, int size)
    {
        if (size < 1)
        {
            return "";
        }
        int encoding = data[start] & 0xFF;
        byte[] content = Arrays.copyOfRange(data, start + 1, start + size);
        try
        {
            String text;
            if (encoding == 1 || encoding == 2)
            {
                // UTF-16(含BOM) / UTF-16BE
                text = new String(content, "UTF-16");
            }
            else if (encoding == 3)
            {
                text = new String(content, "UTF-8");
            }
            else
            {
                text = new String(content, "ISO-8859-1");
            }
            // 去掉末尾 NUL 分隔符
            int idx = text.indexOf('\u0000');
            if (idx >= 0)
            {
                text = text.substring(0, idx);
            }
            return text.trim();
        }
        catch (Exception e)
        {
            return "";
        }
    }

    private String stripExt(String name)
    {
        if (StringUtils.isEmpty(name))
        {
            return "";
        }
        int idx = name.lastIndexOf('.');
        return idx > 0 ? name.substring(0, idx) : name;
    }

    private String getExt(String name)
    {
        if (StringUtils.isEmpty(name))
        {
            return "";
        }
        int idx = name.lastIndexOf('.');
        return idx >= 0 ? name.substring(idx + 1) : "";
    }
}
