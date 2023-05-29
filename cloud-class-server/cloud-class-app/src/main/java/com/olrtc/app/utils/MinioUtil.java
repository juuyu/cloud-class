package com.olrtc.app.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.google.common.collect.HashMultimap;
import com.olrtc.app.config.CustomMinioClient;
import com.olrtc.app.config.properties.MinIoProperties;
import com.olrtc.app.web.FileController;
import com.olrtc.common.core.utils.JsonUtil;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Part;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author njy
 * @since 2022/11/15 09:13
 */
@Slf4j
@Component
public class MinioUtil {

    private static MinioClient minioClient;

    private static MinIoProperties minIoProperties;

    @Resource
    public void setMinioClient(MinioClient minioClient) {
        MinioUtil.minioClient = minioClient;
    }

    @Resource
    public void setMinIoProperties(MinIoProperties minIoProperties) {
        MinioUtil.minIoProperties = minIoProperties;
    }

    public static boolean bucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("bucketExists() called with exception => [bucketName = {}]", bucketName, e);
        }
        return false;
    }

    /**
     * 获取全部bucket, 失败返回空集合
     *
     * @return java.util.List<io.minio.messages.Bucket>
     */
    public static List<Bucket> getAllBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            log.error("getAllBuckets() called with exception => ", e);
        }
        return new ArrayList<>();
    }


    /**
     * 判断文件是否存在
     */
    public static boolean fileExists(String bucketName, String filename) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder().bucket(bucketName).object(filename).build()
            );
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void upload(String fileName, InputStream stream, String contentType) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minIoProperties.getCommonBucket())
                    .object(fileName)
                    .stream(stream, stream.available(), -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            log.error("文件上传失败", e);
        }
    }

    public static boolean upload(String bucketName, String fileName, InputStream stream, String contentType) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(stream, stream.available(), -1)
                    .contentType(contentType)
                    .build());
            return true;
        } catch (Exception e) {
            log.error("upload() called with exception => [bucketName = {}], [fileName = {}]", bucketName, fileName, e);
        }
        return false;
    }

    /* ------------------------------------------------------------------------------------------------------- */

    private static AmazonS3 amazonS3;

    @Resource
    public void setAmazonS3(AmazonS3 amazonS3) {
        MinioUtil.amazonS3 = amazonS3;
    }


    public static CompletableFuture<UploadPartResult> chunkUpload(UploadPartRequest uploadPartRequest, MultipartFile file) throws IOException {
        File targetFile = new File("/Users/yu/Desktop/流程图");
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        file.transferTo(targetFile);

        return CompletableFuture.supplyAsync(() -> {
            uploadPartRequest.withFile(targetFile);
            // Upload the part and add the response's ETag to our list.
            return amazonS3.uploadPart(uploadPartRequest);
        });
    }


    public static boolean merge(List<PartETag> partETags, String uploadId, String fileName, String bucketName) {
        log.info("merge() called with parameters => [partETags = {}], [uploadId = {}], [fileName = {}], [bucketName = {}]", partETags, uploadId, fileName, bucketName);

        // Complete the multipart upload.
        CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucketName, fileName,
                uploadId, partETags);

        amazonS3.completeMultipartUpload(compRequest);
        return true;
    }


    public static Map<String, Object> initMultiPart(String bucketName, String objectName, long fileSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, objectName);
            InitiateMultipartUploadResult initResponse = amazonS3.initiateMultipartUpload(initRequest);
            String uploadId = initResponse.getUploadId();
            result.put("uploadId", uploadId);

            List<UploadPartRequest> chunkList = new ArrayList<>();
            long chunkSize = 10 * 1024 * 1024;
            long filePosition = 0;
            for (int i = 1; filePosition < fileSize; i++) {
                // Because the last part could be less than 5 MB, adjust the part size as needed.
                chunkSize = Math.min(chunkSize, (fileSize - filePosition));
                // Create the request to upload a part.
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName)
                        .withKey(objectName)
                        .withUploadId(uploadId)
                        .withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withPartSize(chunkSize);

                chunkList.add(uploadRequest);
                filePosition += chunkSize;
            }
            FileController.partMap.put(uploadId, chunkList);

            result.put("chunkList", chunkList);
        } catch (Exception e) {
            return null;
        }

        return result;
    }










    /* ------------------------------------------------------------------------------------------------------- */

    private static CustomMinioClient customMinioClient;

    @Resource
    public void setCustomMinioClient(CustomMinioClient customMinioClient) {
        MinioUtil.customMinioClient = customMinioClient;
    }


    /**
     * 创建分块任务
     *
     * @param bucketName
     * @param objectName
     * @param chunks
     * @return Map<String, Object>
     * @author njy
     * @since 18:02 2023/3/30
     */
    public static Map<String, Object> initMultiPartUpload(String bucketName, String objectName, int chunks) {
        Map<String, Object> result = new HashMap<>();
        try {
            HashMultimap<String, String> headers = HashMultimap.create();
            headers.put("Content-Type", "application/octet-stream");

            String uploadId = customMinioClient.initMultiPartUpload(bucketName, null, objectName, headers, null);

            result.put("uploadId", uploadId);
            List<String> chunkList = new ArrayList<>();

            Map<String, String> reqParams = new HashMap<>();
            reqParams.put("uploadId", uploadId);
            for (int i = 1; i <= chunks; i++) {
                reqParams.put("chunkNumber", String.valueOf(i));
                String uploadUrl = customMinioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.PUT)
                                .bucket(bucketName)
                                .object(objectName)
                                .expiry(1, TimeUnit.DAYS)
                                .extraQueryParams(reqParams)
                                .build());
                chunkList.add(uploadUrl);
            }
            result.put("uploadUrls", chunkList);
        } catch (Exception e) {
            return null;
        }

        return result;
    }


    public static List<String> getExistParts(String bucketName, String objectName, String uploadId) {
        List<String> parts = new ArrayList<>();
        try {

            ListPartsResponse partResult = customMinioClient.listMultipart(bucketName, null, objectName, 1024, 0, uploadId, null, null);

            for (Part part : partResult.result().partList()) {
                parts.add(part.etag());
            }
        } catch (Exception e) {
            //
            e.printStackTrace();
            log.error("查询任务分片错误");
        }

        return parts;
    }

    /**
     *  最大分片1000
     */
//    customMinioClient = new CustomMinioClient(MinioAsyncClient.builder()
//                .endpoint(properties.getUrl())
//            .credentials(properties.getAccessKey(), properties.getSecureKey())
//            .build());
//    ListPartsResponse partResult = customMinioClient.listMultipart(bucketName, null, objectName, 1000, 0, uploadId, null, null);
//    int partNumber = 1;
//        for (Part part : partResult.result().partList()) {
//        parts[partNumber - 1] = new Part(partNumber, part.etag());
//        partNumber++;
//    }
//    //合并分片
//        customMinioClient.mergeMultipartUpload(bucketName, null, objectName, uploadId, parts, null, null);

    /**
     * 文件合并
     */
    public static boolean mergeMultipartUpload(String bucketName, String objectName, String uploadId) {
        try {

            Part[] parts = new Part[1000];
            ListPartsResponse partResult = customMinioClient.listMultipart(bucketName, null, objectName, 1000, 0, uploadId, null, null);

            List<Part> partList = partResult.result().partList();

            log.info("----------------------------------------");
            log.info(String.valueOf(partList.size()));
            log.info(JsonUtil.obj2String(partList));


            int partNumber = 1;
            for (Part part : partResult.result().partList()) {
                log.info(JsonUtil.obj2String(part));
                parts[partNumber - 1] = new Part(partNumber, part.etag());
                partNumber++;
            }
            customMinioClient.mergeMultipartUpload(bucketName, null, objectName, uploadId, parts, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


}
