package com.agriculture.knowledge.video.controller;

import com.agriculture.common.result.PageResult;
import com.agriculture.common.result.Result;
import com.agriculture.knowledge.video.entity.KnowledgeVideo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/knowledge/videos")
@RequiredArgsConstructor
@Tag(name = "农技视频")
@Slf4j
public class KnowledgeVideoController {

    private static final long MAX_VIDEO_SIZE = 200L * 1024 * 1024;

    private final IService<KnowledgeVideo> service;

    @Value("${app.video.path:D:/作业/texun/agriculture-platform/videos}")
    private String videoPath;

    @Value("${app.video.cover-path:D:/作业/texun/agriculture-platform/videos/covers}")
    private String coverPath;

    @GetMapping
    public Result<PageResult<KnowledgeVideo>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "12") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String cropType) {
        LambdaQueryWrapper<KnowledgeVideo> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            qw.like(KnowledgeVideo::getTitle, keyword);
        }
        if (cropType != null && !cropType.isBlank()) {
            qw.eq(KnowledgeVideo::getCropType, cropType);
        }
        qw.orderByDesc(KnowledgeVideo::getCreateTime);
        Page<KnowledgeVideo> page = service.page(new Page<>(pageNum, pageSize), qw);
        return Result.ok(new PageResult<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<KnowledgeVideo> getById(@PathVariable Long id) {
        KnowledgeVideo video = service.getById(id);
        if (video != null) {
            video.setViewCount(video.getViewCount() == null ? 1 : video.getViewCount() + 1);
            service.updateById(video);
        }
        return Result.ok(video);
    }

    @GetMapping("/recommend")
    public Result<List<KnowledgeVideo>> recommend(@RequestParam(defaultValue = "8") int size) {
        LambdaQueryWrapper<KnowledgeVideo> qw = new LambdaQueryWrapper<>();
        qw.orderByDesc(KnowledgeVideo::getViewCount).last("LIMIT " + size);
        return Result.ok(service.list(qw));
    }

    @PostMapping("/{id}/view")
    public Result<Void> recordView(@PathVariable Long id) {
        KnowledgeVideo video = service.getById(id);
        if (video != null) {
            video.setViewCount(video.getViewCount() == null ? 1 : video.getViewCount() + 1);
            service.updateById(video);
        }
        return Result.ok();
    }

    @PostMapping("/{id}/like")
    public Result<Void> like(@PathVariable Long id) {
        KnowledgeVideo video = service.getById(id);
        if (video != null) {
            video.setLikeCount(video.getLikeCount() == null ? 1 : video.getLikeCount() + 1);
            service.updateById(video);
        }
        return Result.ok();
    }

    @DeleteMapping("/{id}/like")
    public Result<Void> unlike(@PathVariable Long id) {
        KnowledgeVideo video = service.getById(id);
        if (video != null && video.getLikeCount() != null && video.getLikeCount() > 0) {
            video.setLikeCount(video.getLikeCount() - 1);
            service.updateById(video);
        }
        return Result.ok();
    }

    @PostMapping("/{id}/favorite")
    public Result<Void> favorite(@PathVariable Long id) {
        KnowledgeVideo video = service.getById(id);
        if (video != null) {
            video.setFavoriteCount(video.getFavoriteCount() == null ? 1 : video.getFavoriteCount() + 1);
            service.updateById(video);
        }
        return Result.ok();
    }

    @PostMapping("/{id}/cancel-favorite")
    public Result<Void> cancelFavorite(@PathVariable Long id) {
        KnowledgeVideo video = service.getById(id);
        if (video != null && video.getFavoriteCount() != null && video.getFavoriteCount() > 0) {
            video.setFavoriteCount(video.getFavoriteCount() - 1);
            service.updateById(video);
        }
        return Result.ok();
    }

    @Operation(summary = "扫描本地视频文件并入库")
    @PostMapping("/scan")
    public Result<Map<String, Object>> scan() {
        File dir = new File(videoPath);
        int count = 0;
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> {
                String lowerName = name.toLowerCase();
                return lowerName.endsWith(".mp4") || lowerName.endsWith(".avi") || lowerName.endsWith(".mov");
            });
            if (files != null) {
                for (File file : files) {
                    LambdaQueryWrapper<KnowledgeVideo> qw = new LambdaQueryWrapper<>();
                    qw.eq(KnowledgeVideo::getFileName, file.getName());
                    if (service.count(qw) > 0) {
                        continue;
                    }

                    KnowledgeVideo video = new KnowledgeVideo();
                    video.setTitle(file.getName().replaceFirst("\\.[^.]+$", ""));
                    video.setFileName(file.getName());
                    video.setVideoUrl("/videos/" + file.getName());
                    video.setCoverUrl("/videos/covers/" + file.getName().replaceFirst("\\.[^.]+$", ".jpg"));
                    video.setViewCount(0);
                    video.setLikeCount(0);
                    video.setFavoriteCount(0);
                    video.setStatus(1);
                    video.setCreateTime(LocalDateTime.now());
                    service.save(video);
                    count++;
                }
            }
        }
        return Result.ok(Map.of("count", count, "path", videoPath));
    }

    @Operation(summary = "上传视频")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<KnowledgeVideo> upload(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "cropType", required = false) String cropType,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam("video") MultipartFile video,
            @RequestParam(value = "cover", required = false) MultipartFile cover) {
        if (title == null || title.isBlank()) {
            return Result.fail("请输入视频标题");
        }
        if (video == null || video.isEmpty()) {
            return Result.fail("请选择视频文件");
        }
        if (video.getSize() > MAX_VIDEO_SIZE) {
            return Result.fail("视频大小不能超过 200MB");
        }

        String originalName = video.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            originalName = "video.mp4";
        }
        String ext = getFileExtension(originalName, ".mp4");
        if (!isAllowedVideoExt(ext)) {
            return Result.fail("仅支持 mp4、avi、mov、webm 格式的视频");
        }

        try {
            Files.createDirectories(Paths.get(videoPath));
            String savedName = UUID.randomUUID().toString().replace("-", "") + ext;
            Path videoPathObj = Paths.get(videoPath, savedName);
            Files.write(videoPathObj, video.getBytes());

            String coverSavedName = null;
            if (cover != null && !cover.isEmpty()) {
                String coverExt = getFileExtension(cover.getOriginalFilename(), ".jpg");
                if (!isAllowedCoverExt(coverExt)) {
                    return Result.fail("封面仅支持 jpg、jpeg、png、webp 格式");
                }
                Files.createDirectories(Paths.get(coverPath));
                coverSavedName = savedName.replaceFirst("\\.[^.]+$", coverExt);
                Files.write(Paths.get(coverPath, coverSavedName), cover.getBytes());
            }

            KnowledgeVideo savedVideo = new KnowledgeVideo();
            savedVideo.setTitle(title.trim());
            savedVideo.setDescription(description);
            savedVideo.setCropType(cropType);
            savedVideo.setTags(tags);
            savedVideo.setFileName(savedName);
            savedVideo.setVideoUrl("/videos/" + savedName);
            if (coverSavedName != null) {
                savedVideo.setCoverUrl("/videos/covers/" + coverSavedName);
            }
            savedVideo.setViewCount(0);
            savedVideo.setLikeCount(0);
            savedVideo.setFavoriteCount(0);
            savedVideo.setStatus(1);
            savedVideo.setCreateTime(LocalDateTime.now());
            service.save(savedVideo);

            log.info("视频上传成功: id={}, title={}, file={}", savedVideo.getId(), savedVideo.getTitle(), savedName);
            return Result.ok(savedVideo);
        } catch (IOException e) {
            log.error("视频上传失败", e);
            return Result.fail("视频上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除视频")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        KnowledgeVideo video = service.getById(id);
        if (video == null) {
            return Result.fail("视频不存在");
        }
        if (video.getFileName() != null) {
            try {
                Files.deleteIfExists(Paths.get(videoPath, video.getFileName()));
                if (video.getCoverUrl() != null) {
                    String coverName = video.getCoverUrl().replace("/videos/covers/", "");
                    Files.deleteIfExists(Paths.get(coverPath, coverName));
                }
            } catch (IOException e) {
                log.warn("删除视频文件失败: id={}", id, e);
            }
        }
        service.removeById(id);
        log.info("视频已删除: id={}, title={}", id, video.getTitle());
        return Result.ok();
    }

    private String getFileExtension(String fileName, String defaultExt) {
        if (fileName == null || !fileName.contains(".")) {
            return defaultExt;
        }
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }

    private boolean isAllowedVideoExt(String ext) {
        return ".mp4".equals(ext) || ".avi".equals(ext) || ".mov".equals(ext) || ".webm".equals(ext);
    }

    private boolean isAllowedCoverExt(String ext) {
        return ".jpg".equals(ext) || ".jpeg".equals(ext) || ".png".equals(ext) || ".webp".equals(ext);
    }
}
