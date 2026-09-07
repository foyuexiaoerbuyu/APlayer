package com.ruoyi.project.music.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.project.music.domain.MusicResource;
import com.ruoyi.project.music.domain.MusicSong;
import com.ruoyi.project.music.service.IMusicResourceService;
import com.ruoyi.project.music.service.IMusicSongService;

/**
 * 音乐媒体流控制器：支持 HTTP Range 的音频播放与封面输出
 */
@RestController
@RequestMapping("/music/media")
public class MusicMediaController
{
    private static final Logger log = LoggerFactory.getLogger(MusicMediaController.class);

    @Autowired
    private IMusicResourceService musicResourceService;

    @Autowired
    private IMusicSongService musicSongService;

    /**
     * 音频流播放（支持 Range，Media3/ExoPlayer 可拖动进度）
     */
    @GetMapping("/audio/{songId}")
    public void playAudio(@PathVariable("songId") Long songId, HttpServletRequest request,
            HttpServletResponse response) throws IOException
    {
        MusicResource resource = musicResourceService.selectAudioResourceBySongId(songId);
        if (resource == null || resource.getFilePath() == null)
        {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        File file = new File(resource.getFilePath());
        if (!file.exists() || !file.isFile())
        {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String contentType = contentType(resource.getFormat());
        long fileLength = file.length();
        String range = request.getHeader("Range");
        long start = 0;
        long end = fileLength - 1;
        if (range != null && range.startsWith("bytes="))
        {
            try
            {
                String[] parts = range.substring(6).split("-");
                start = Long.parseLong(parts[0]);
                if (parts.length > 1 && !parts[1].isEmpty())
                {
                    end = Long.parseLong(parts[1]);
                }
                if (start > end || start >= fileLength)
                {
                    response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                    response.setHeader("Content-Range", "bytes */" + fileLength);
                    return;
                }
                if (end >= fileLength)
                {
                    end = fileLength - 1;
                }
                long contentLength = end - start + 1;
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
                response.setContentLengthLong(contentLength);
            }
            catch (Exception e)
            {
                start = 0;
                end = fileLength - 1;
            }
        }
        else
        {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentLengthLong(fileLength);
        }
        response.setContentType(contentType);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Cache-Control", "no-cache");

        try (RandomAccessFile raf = new RandomAccessFile(file, "r"))
        {
            raf.seek(start);
            long remain = end - start + 1;
            byte[] buffer = new byte[64 * 1024];
            OutputStream out = response.getOutputStream();
            int read;
            while (remain > 0 && (read = raf.read(buffer, 0, (int) Math.min(buffer.length, remain))) != -1)
            {
                out.write(buffer, 0, read);
                remain -= read;
            }
            out.flush();
        }
        catch (Exception e)
        {
            log.warn("音频流输出中断: songId={}", songId, e);
        }
    }

    /**
     * 封面输出
     */
    @GetMapping("/cover/{songId}")
    public void cover(@PathVariable("songId") Long songId, HttpServletResponse response) throws IOException
    {
        MusicSong song = musicSongService.selectMusicSongById(songId);
        if (song == null || song.getCoverPath() == null)
        {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // coverPath 存的是 /profile/music/cover/xxx，转换为本地路径
        String prefix = "/profile/";
        String rel = song.getCoverPath().startsWith(prefix)
                ? song.getCoverPath().substring(prefix.length())
                : song.getCoverPath();
        String baseDir = System.getProperty("ruoyi.profile");
        File file = new File(baseDir, rel);
        if (!file.exists() || !file.isFile())
        {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setContentType(guessImageType(file.getName()));
        response.setContentLengthLong(file.length());
        try (InputStream in = Files.newInputStream(file.toPath()))
        {
            in.transferTo(response.getOutputStream());
        }
    }

    private String contentType(String format)
    {
        if (format == null)
        {
            return "application/octet-stream";
        }
        switch (format.toLowerCase())
        {
            case "mp3":
                return "audio/mpeg";
            case "m4a":
                return "audio/mp4";
            case "aac":
                return "audio/aac";
            case "wav":
                return "audio/wav";
            case "flac":
                return "audio/flac";
            case "ogg":
            case "opus":
                return "audio/ogg";
            default:
                return "application/octet-stream";
        }
    }

    private String guessImageType(String name)
    {
        if (name.toLowerCase().endsWith(".png"))
        {
            return "image/png";
        }
        return "image/jpeg";
    }
}
