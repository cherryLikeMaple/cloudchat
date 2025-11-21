package com.cherry.controller;

import cn.hutool.core.lang.UUID;
import com.cherry.api.feign.UserInfoMicroServiceFeign;
import com.cherry.grace.result.GraceJSONResult;
import com.cherry.grace.result.ResponseStatusEnum;
import com.cherry.manage.MinIOConfig;
import com.cherry.manage.MinIOUtils;
import com.cherry.pojo.Users;
import com.cherry.util.FfmpegUtils;
import com.cherry.vo.MediaUploadVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author cherry
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private MinIOConfig minIOConfig;
    @Resource
    private UserInfoMicroServiceFeign userInfoMicroServiceFeign;
    @Resource
    private MinIOUtils minIOUtils;

    /**
     * 用户头像上传
     *
     * @param file
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/uploadFace")
    public GraceJSONResult uploadFace(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {

        // note 这里使用的是 openfeign 远程调用
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        // 用户id
        Long userId = loginUser.getId();
        // 文件名
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        String filename = String.join("/", "face", String.valueOf(userId), originalFilename);
        minIOUtils.uploadFile(minIOConfig.getBucketName(), filename, file.getInputStream());

        String faceUrl = minIOConfig.getFileHost()
                + "/"
                + minIOConfig.getBucketName()
                + "/"
                + "face/"
                + userId
                + "/"
                + originalFilename;

        return GraceJSONResult.ok(faceUrl);
    }

    /**
     * 朋友圈图片上传
     *
     * @param file
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/uploadFriendCircleImage")
    public GraceJSONResult uploadFriendCircleImage(@RequestParam("file") MultipartFile file,
                                                   HttpServletRequest request) throws Exception {

        // 1. 获取登录用户（OpenFeign 远程调用）
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        Long userId = loginUser.getId();

        // 2. 原始文件名校验
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        // 3. 生成一个不重复的文件名，避免同名覆盖
        String suffix = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex >= 0) {
            suffix = originalFilename.substring(dotIndex);
        }
        String newFileName = UUID.randomUUID().toString().replace("-", "") + suffix;

        // 4. MinIO 存储路径：friend_circle/image/{userId}/{newFileName}
        String objectName = String.join("/",
                "friend_circle",
                "image",
                String.valueOf(userId),
                newFileName
        );

        // 5. 上传到 MinIO
        minIOUtils.uploadFile(minIOConfig.getBucketName(), objectName, file.getInputStream());

        // 6. 拼接可访问的 URL
        String imageUrl = minIOConfig.getFileHost()
                + "/"
                + minIOConfig.getBucketName()
                + "/"
                + objectName;

        return GraceJSONResult.ok(imageUrl);
    }

    /**
     * 上传聊天模块的图片
     *
     * @param file
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/uploadChatImage")
    public GraceJSONResult uploadChatImage(@RequestParam("file") MultipartFile file,
                                           HttpServletRequest request) throws Exception {
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        long userId = loginUser.getId();

        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        // 1. 获取图片宽高：必须从字节流中读，否则 IO 会被消费掉
        // 一次读入内存（聊天图片一般很小）
        byte[] bytes = file.getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

        BufferedImage img = ImageIO.read(bais);
        if (img == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        int width = img.getWidth();
        int height = img.getHeight();

        // 2. 文件名处理
        String suffix = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex >= 0) {
            suffix = originalFilename.substring(dotIndex);
        }
        String newFileName = UUID.randomUUID().toString().replace("-", "") + suffix;

        String objectName = String.join("/",
                "chat",
                "image",
                String.valueOf(userId),
                newFileName
        );

        // 3. 使用新的流上传
        ByteArrayInputStream uploadStream = new ByteArrayInputStream(bytes);
        minIOUtils.uploadFile(minIOConfig.getBucketName(), objectName, uploadStream);

        // 4. 拼接 URL
        String imageUrl = minIOConfig.getFileHost()
                + "/"
                + minIOConfig.getBucketName()
                + "/"
                + objectName;

        // 5. 返回给前端（把宽和高也返回）
        MediaUploadVO vo = new MediaUploadVO();
        vo.setMediaUrl(imageUrl);
        vo.setWidth(width);
        vo.setHeight(height);

        return GraceJSONResult.ok(vo);
    }


    /**
     * 朋友圈上传视频.
     *
     * @param file
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/uploadFriendCircleVideo")
    public GraceJSONResult uploadFriendCircleVideo(@RequestParam("file") MultipartFile file,
                                                   HttpServletRequest request) throws Exception {

        // 1. 获取登录用户
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        Long userId = loginUser.getId();

        // 2. 原始文件名校验
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        // 3. 生成不重复文件名
        String suffix = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex >= 0) {
            suffix = originalFilename.substring(dotIndex);
        }
        String newFileName = UUID.randomUUID().toString().replace("-", "") + suffix;

        // 4. MinIO 存储路径：friend_circle/video/{userId}/{newFileName}
        String objectName = String.join("/",
                "friend_circle",
                "video",
                String.valueOf(userId),
                newFileName
        );

        // 5. 上传到 MinIO
        minIOUtils.uploadFile(minIOConfig.getBucketName(), objectName, file.getInputStream());

        // 6. 拼接访问 URL
        String videoUrl = minIOConfig.getFileHost()
                + "/"
                + minIOConfig.getBucketName()
                + "/"
                + objectName;

        return GraceJSONResult.ok(videoUrl);
    }


    /**
     * 聊天模块上传视频.
     */
    @PostMapping("/uploadChatVideo")
    public GraceJSONResult uploadChatVideo(@RequestParam("file") MultipartFile file,
                                           HttpServletRequest request) throws Exception {

        // 1. 获取登录用户
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        Long userId = loginUser.getId();

        // 2. 原始文件名校验
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        // 3. 生成不重复文件名
        String suffix = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex >= 0) {
            suffix = originalFilename.substring(dotIndex).toLowerCase();
        }
        // 如果需要可做格式校验：mp4/mov 等
        // if (!suffix.matches("\\.(mp4|mov|avi|mkv|flv)$")) { ... }

        String newFileName = UUID.randomUUID().toString().replace("-", "") + suffix;

        // ============= 先保存到本地临时文件，给 ffmpeg 用 =============

        File tempVideoFile = File.createTempFile("fc_video_", suffix);
        file.transferTo(tempVideoFile);

        // 4. 用 ffmpeg 获取视频信息
        FfmpegUtils.VideoInfo videoInfo;
        try {
            videoInfo = FfmpegUtils.getVideoInfo(tempVideoFile);
        } catch (Exception e) {
            e.printStackTrace();
            tempVideoFile.delete();
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        // 5. 生成封面图临时文件
        File tempCoverFile = File.createTempFile("fc_video_cover_", ".jpg");
        try {
            FfmpegUtils.generateCover(tempVideoFile, tempCoverFile);
        } catch (Exception e) {
            e.printStackTrace();
            tempVideoFile.delete();
            tempCoverFile.delete();
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        // ============= 上传到 MinIO =============

        // 视频存储路径：friend_circle/video/{userId}/{newFileName}
        String videoObjectName = String.join("/",
                "chat",
                "video",
                String.valueOf(userId),
                newFileName
        );

        // 封面图路径：friend_circle/video_cover/{userId}/{xxxx}.jpg
        String coverFileName = newFileName + "_cover.jpg";
        String coverObjectName = String.join("/",
                "chat",
                "video_cover",
                String.valueOf(userId),
                coverFileName
        );

        // 7. 上传视频
        try (InputStream videoIn = new FileInputStream(tempVideoFile)) {
            minIOUtils.uploadFile(minIOConfig.getBucketName(), videoObjectName, videoIn);
        }

        // 8. 上传封面图
        try (InputStream coverIn = new FileInputStream(tempCoverFile)) {
            minIOUtils.uploadFile(minIOConfig.getBucketName(), coverObjectName, coverIn);
        }

        // 9. 删除本地临时文件
        tempVideoFile.delete();
        tempCoverFile.delete();

        // 10. 拼接访问 URL
        String videoUrl = minIOConfig.getFileHost()
                + "/"
                + minIOConfig.getBucketName()
                + "/"
                + videoObjectName;

        String coverUrl = minIOConfig.getFileHost()
                + "/"
                + minIOConfig.getBucketName()
                + "/"
                + coverObjectName;

        // 11. 封装返回 VO
        MediaUploadVO vo = new MediaUploadVO();
        vo.setMediaUrl(videoUrl);
        vo.setCoverUrl(coverUrl);
        vo.setWidth(videoInfo.getWidth());
        vo.setHeight(videoInfo.getHeight());
        vo.setDuration(videoInfo.getDuration());

        return GraceJSONResult.ok(vo);
    }


    /**
     * 朋友圈上传视频测试.
     *
     * @param file
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/uploadChatVideoDemo")
    public GraceJSONResult uploadFriendCircleVideoDemo(@RequestParam("file") MultipartFile file,
                                                       HttpServletRequest request) throws Exception {

        // 1. 获取登录用户
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        Long userId = loginUser.getId();

        // 2. 原始文件名校验
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        // 3. 生成不重复文件名
        String suffix = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex >= 0) {
            suffix = originalFilename.substring(dotIndex);
        }
        String newFileName = UUID.randomUUID().toString().replace("-", "") + suffix;

        // 4. MinIO 存储路径：friend_circle/video/{userId}/{newFileName}
        String objectName = String.join("/",
                "chat",
                "video",
                String.valueOf(userId),
                newFileName
        );

        // 5. 上传到 MinIO
        minIOUtils.uploadFile(minIOConfig.getBucketName(), objectName, file.getInputStream());

        // 6. 拼接访问 URL
        String videoUrl = minIOConfig.getFileHost()
                + "/"
                + minIOConfig.getBucketName()
                + "/"
                + objectName;

        MediaUploadVO vo = new MediaUploadVO();
        vo.setMediaUrl(videoUrl);

        return GraceJSONResult.ok(vo);
    }

}
