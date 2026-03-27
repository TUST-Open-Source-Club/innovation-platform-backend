package com.abajin.innovation.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 本地文件存储工具类
 */
@Slf4j
@Component
public class FileStorageUtils {

    @Value("${file.storage.path:./uploads}")
    private String storagePath;

    @Value("${file.access.url:/uploads}")
    private String accessUrl;

    private Path rootPath;

    @PostConstruct
    public void init() {
        this.rootPath = Paths.get(storagePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootPath);
            log.info("文件存储根目录初始化成功: {}", rootPath);
        } catch (IOException e) {
            log.error("无法创建文件存储目录: {}", rootPath, e);
            throw new RuntimeException("无法创建文件存储目录", e);
        }
    }

    /**
     * 上传文件
     *
     * @param inputStream 文件输入流
     * @param dir         存储目录（如：resume, activity-poster）
     * @param filename    文件名
     * @return 文件访问路径
     */
    public String uploadFile(InputStream inputStream, String dir, String filename) throws IOException {
        // 创建目录路径：storage/dir/
        Path dirPath = rootPath.resolve(dir).normalize();
        Files.createDirectories(dirPath);

        // 生成唯一文件名
        String uniqueFilename = generateUniqueFilename(filename);

        // 目标文件路径
        Path targetPath = dirPath.resolve(uniqueFilename);

        // 保存文件
        Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);

        log.info("文件上传成功: {}", targetPath);

        // 返回访问路径
        return accessUrl + "/" + dir + "/" + uniqueFilename;
    }

    /**
     * 上传文件（简化版）
     *
     * @param inputStream 文件输入流
     * @param objectName  对象名称（包含目录，如：second/activity-poster/uuid.jpg）
     * @return 文件访问路径
     */
    public String uploadFile(InputStream inputStream, String objectName) throws IOException {
        // 解析对象名称获取目录和文件名
        int lastSlashIndex = objectName.lastIndexOf('/');
        String dir = lastSlashIndex > 0 ? objectName.substring(0, lastSlashIndex) : "";
        String filename = lastSlashIndex > 0 ? objectName.substring(lastSlashIndex + 1) : objectName;

        Path dirPath = rootPath.resolve(dir).normalize();
        Files.createDirectories(dirPath);

        Path targetPath = dirPath.resolve(filename);
        Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);

        log.info("文件上传成功: {}", targetPath);

        return accessUrl + "/" + objectName;
    }

    /**
     * 下载文件
     *
     * @param objectName 对象名称（包含目录）
     * @return 文件输入流
     */
    public InputStream downloadFile(String objectName) throws IOException {
        Path filePath = rootPath.resolve(objectName).normalize();
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("文件不存在: " + objectName);
        }
        return Files.newInputStream(filePath);
    }

    /**
     * 获取文件的本地路径
     *
     * @param objectName 对象名称
     * @return 文件路径
     */
    public Path getFilePath(String objectName) {
        return rootPath.resolve(objectName).normalize();
    }

    /**
     * 删除文件
     *
     * @param objectName 对象名称
     */
    public void deleteFile(String objectName) throws IOException {
        Path filePath = rootPath.resolve(objectName).normalize();
        Files.deleteIfExists(filePath);
        log.info("文件删除成功: {}", filePath);
    }

    /**
     * 检查文件是否存在
     *
     * @param objectName 对象名称
     * @return 是否存在
     */
    public boolean exists(String objectName) {
        Path filePath = rootPath.resolve(objectName).normalize();
        return Files.exists(filePath);
    }

    /**
     * 生成唯一文件名
     */
    private String generateUniqueFilename(String originalFilename) {
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString().replace("-", "") + ext;
    }

    /**
     * 获取文件访问URL
     *
     * @param objectName 对象名称
     * @return 访问URL
     */
    public String getFileUrl(String objectName) {
        return accessUrl + "/" + objectName;
    }

    /**
     * 从URL中提取对象名称
     *
     * @param fileUrl 文件URL
     * @return 对象名称
     */
    public String extractObjectNameFromUrl(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(accessUrl)) {
            return null;
        }
        return fileUrl.substring(accessUrl.length() + 1); // +1 for the leading slash
    }
}
