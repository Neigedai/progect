package com.park.service;

import java.util.Map;

public interface FileService {
    /**
     * 上传文件并返回访问信息
     * 当前为本地存储占位实现，后续可替换为 MinIO 等
     */
    Map<String, Object> uploadImage(byte[] fileBytes, String originalFilename);
    Map<String, Object> uploadVideo(byte[] fileBytes, String originalFilename);
}
