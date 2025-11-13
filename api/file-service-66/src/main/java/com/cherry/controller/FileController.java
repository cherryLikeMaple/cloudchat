package com.cherry.controller;

import com.cherry.api.feign.UserInfoMicroServiceFeign;
import com.cherry.grace.result.GraceJSONResult;
import com.cherry.grace.result.ResponseStatusEnum;
import com.cherry.manage.MinIOConfig;
import com.cherry.manage.MinIOUtils;
import com.cherry.pojo.Users;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

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
}
