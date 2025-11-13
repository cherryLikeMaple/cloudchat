package com.cherry.test;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
public class MinIoTest {

    @Test
    public void testUpload() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        // 创建客户端
        MinioClient minioClient = MinioClient.builder().endpoint("http://localhost:9000")
                .credentials("minioadmin", "minioadmin").build();

        // 如果没有bucket, 则需要创建
        String bucketName = "local-test";
        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());

        // 判断当前bucket是否存在
        // note 主业务写上面.
        if (!isExist) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        } else {
            System.out.println("当前  " + bucketName + "bucket已经存在");
        }

        // 上传本地文件到minio的服务器中
        minioClient.uploadObject(UploadObjectArgs.builder().bucket(bucketName)
                .object("myImage.jpg").filename("C:\\Users\\31673\\Pictures\\aurora_whispers.jpg")
                .build());
    }
}
