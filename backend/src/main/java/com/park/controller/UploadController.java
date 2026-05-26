package com.park.controller;

import com.park.common.Result;
import com.park.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/upload")
@RequiredArgsConstructor
public class UploadController {

    private final FileService fileService;

    /**
     * 图片上传（本地存储占位实现）
     * TODO: 后续接入 MinIO 时只需替换 FileService 实现
     */
    @PostMapping("/image")
    public Result<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.fail(400, "文件不能为空");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.fail(400, "文件大小不能超过 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.fail(400, "仅支持图片文件");
        }
        Map<String, Object> result = fileService.uploadImage(file.getBytes(), file.getOriginalFilename());
        return Result.ok(result);
    }

    @PostMapping("/video")
    public Result<Map<String, Object>> uploadVideo(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.fail(400, "文件不能为空");
        }
        if (file.getSize() > 100 * 1024 * 1024) {
            return Result.fail(400, "文件大小不能超过 100MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            return Result.fail(400, "仅支持视频文件");
        }
        Map<String, Object> result = fileService.uploadVideo(file.getBytes(), file.getOriginalFilename());
        return Result.ok(result);
    }
}
