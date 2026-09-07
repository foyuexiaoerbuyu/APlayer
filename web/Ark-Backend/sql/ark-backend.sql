CREATE TABLE `music_album` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '专辑ID',
                                        `name` varchar(200) NOT NULL COMMENT '专辑名',
                                        `cover_path` varchar(500) DEFAULT NULL COMMENT '封面路径',
                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                        `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                        `create_by` varchar(64) DEFAULT NULL,
                                        `update_by` varchar(64) DEFAULT NULL,
                                        `remark` varchar(500) DEFAULT NULL,
                                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='音乐专辑表';

CREATE TABLE `music_artist` (
                                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '歌手ID',
                                         `name` varchar(100) NOT NULL COMMENT '歌手名',
                                         `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                         `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                         `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
                                         `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
                                         `remark` varchar(500) DEFAULT NULL COMMENT '备注',
                                         PRIMARY KEY (`id`),
                                         UNIQUE KEY `uk_artist_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='音乐歌手表';

CREATE TABLE `music_lyric` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '歌词ID',
                                        `song_id` bigint NOT NULL COMMENT '歌曲ID',
                                        `content` text COMMENT '歌词内容(LRC)',
                                        `format` varchar(10) DEFAULT 'LRC' COMMENT '歌词格式',
                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                        PRIMARY KEY (`id`),
                                        KEY `idx_song` (`song_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='音乐歌词表';

CREATE TABLE `music_resource` (
                                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '资源ID',
                                           `song_id` bigint NOT NULL COMMENT '歌曲ID',
                                           `resource_type` varchar(20) DEFAULT 'AUDIO' COMMENT '资源类型(AUDIO/COVER/LYRIC)',
                                           `format` varchar(20) DEFAULT NULL COMMENT '格式(mp3/flac...)',
                                           `codec` varchar(20) DEFAULT NULL COMMENT '编码',
                                           `bitrate` int DEFAULT NULL COMMENT '码率(kbps)',
                                           `sample_rate` int DEFAULT NULL COMMENT '采样率(Hz)',
                                           `channels` int DEFAULT NULL COMMENT '声道数',
                                           `file_size` bigint DEFAULT NULL COMMENT '文件大小(字节)',
                                           `file_path` varchar(500) NOT NULL COMMENT '文件路径',
                                           `file_hash` varchar(64) DEFAULT NULL COMMENT '文件哈希',
                                           `duration` bigint DEFAULT NULL COMMENT '时长(秒)',
                                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                           `create_by` varchar(64) DEFAULT NULL,
                                           `update_by` varchar(64) DEFAULT NULL,
                                           `remark` varchar(500) DEFAULT NULL,
                                           `update_time` datetime DEFAULT NULL,
                                           PRIMARY KEY (`id`),
                                           KEY `idx_song` (`song_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='音乐音频资源表';

CREATE TABLE `music_song` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '歌曲ID',
                                       `title` varchar(200) NOT NULL COMMENT '歌曲标题',
                                       `artist_id` bigint DEFAULT NULL COMMENT '歌手ID',
                                       `artist_name` varchar(100) DEFAULT NULL COMMENT '歌手名(冗余)',
                                       `album_id` bigint DEFAULT NULL COMMENT '专辑ID',
                                       `album_name` varchar(200) DEFAULT NULL COMMENT '专辑名(冗余)',
                                       `genre` varchar(100) DEFAULT NULL COMMENT '流派',
                                       `cover_path` varchar(500) DEFAULT NULL COMMENT '封面路径',
                                       `duration` bigint DEFAULT '0' COMMENT '时长(秒)',
                                       `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `create_by` varchar(64) DEFAULT NULL,
                                       `update_by` varchar(64) DEFAULT NULL,
                                       `remark` varchar(500) DEFAULT NULL,
                                       PRIMARY KEY (`id`),
                                       KEY `idx_artist` (`artist_id`),
                                       KEY `idx_album` (`album_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='音乐歌曲表';
