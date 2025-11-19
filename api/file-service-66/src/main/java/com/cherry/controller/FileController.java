package com.cherry.controller;

import cn.hutool.core.lang.UUID;
import com.cherry.api.feign.UserInfoMicroServiceFeign;
import com.cherry.grace.result.GraceJSONResult;
import com.cherry.grace.result.ResponseStatusEnum;
import com.cherry.manage.MinIOConfig;
import com.cherry.manage.MinIOUtils;
import com.cherry.pojo.Users;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
            suffix = originalFilename.substring(dotIndex);  // 包含 .
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
     * 朋友圈上传视频.
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



}
