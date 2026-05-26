package com.park.service.impl;

import com.park.service.FileService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    // TODO: 后续替换为 MinIO 时只需修改此实现，Controller 层无需变动
    private static final String UPLOAD_ROOT = "uploads";
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024;

    @Override
    public Map<String, Object> uploadImage(byte[] fileBytes, String originalFilename) {
        if (fileBytes.length > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过 5MB");
        }

        String ext = getExtension(originalFilename);
        if (!ext.matches("(jpg|jpeg|png|gif|webp|bmp|svg)")) {
            throw new IllegalArgumentException("不支持的文件类型: " + ext);
        }

        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String newFileName = UUID.randomUUID().toString().substring(0, 8) + "_" + originalFilename;
        String relativePath = "/" + UPLOAD_ROOT + "/" + dateDir + "/" + newFileName;

        try {
            Path targetPath = Paths.get(UPLOAD_ROOT, dateDir);
            Files.createDirectories(targetPath);
            Files.write(targetPath.resolve(newFileName), fileBytes);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("url", relativePath);
        result.put("fileName", originalFilename);
        result.put("size", fileBytes.length);
        return result;
    }

    @Override
    public Map<String, Object> uploadVideo(byte[] fileBytes, String originalFilename) {
        if (fileBytes.length > MAX_VIDEO_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过 100MB");
        }

        String ext = getExtension(originalFilename);
        if (!ext.matches("(mp4|webm|mov|avi|mkv|flv)")) {
            throw new IllegalArgumentException("不支持的文件类型: " + ext);
        }

        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String newFileName = UUID.randomUUID().toString().substring(0, 8) + "_" + originalFilename;
        String relativePath = "/" + UPLOAD_ROOT + "/" + dateDir + "/" + newFileName;

        try {
            Path targetPath = Paths.get(UPLOAD_ROOT, dateDir);
            Files.createDirectories(targetPath);
            Files.write(targetPath.resolve(newFileName), fileBytes);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("url", relativePath);
        result.put("fileName", originalFilename);
        result.put("size", fileBytes.length);
        return result;
    }

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot == -1 ? "" : filename.substring(dot + 1).toLowerCase();
    }
}
