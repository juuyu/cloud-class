package com.olrtc.app.web;

import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;
import com.olrtc.app.biz.FileBiz;
import com.olrtc.app.config.properties.MinIoProperties;
import com.olrtc.app.model.param.FileChunkParam;
import com.olrtc.app.utils.MinioUtil;
import com.olrtc.common.core.domain.R;
import com.olrtc.common.core.exception.BizException;
import com.olrtc.common.core.utils.StrUtil;
import com.olrtc.common.core.utils.file.MimeTypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author njy
 * @since 2023/3/2 11:39
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("upload")
public class FileController {

    private final FileBiz         fileBiz;
    private final MinIoProperties minIoProperties;


    /**
     * 上传图片
     *
     * @param file
     * @return com.olrtc.common.core.domain.R<java.lang.String>
     * @author njy
     * @since 15:23 2023/3/30
     */
    @PostMapping("img")
    public R<String> uploadImg(MultipartFile file) {
        if (file == null) {
            throw new BizException("文件不能为空");
        }
        if (!fileBiz.checkFileIsImg(file)) {
            return R.fail("文件上传失败,请上传符合格式的 png/jpg 类型的图片");
        }
        if (fileBiz.checkFileIsOverSize(file.getSize(), 5, "M")) {
            return R.fail("文件上传失败,请上传大小不超过5MB的图片");
        }
        String originalFilename = file.getOriginalFilename();
        String newFileName = "file/images/"
                + UUID.randomUUID().toString().replace("-", "")
                + "-"
                + originalFilename;
        try {
            InputStream inputStream = file.getInputStream();
            //异步上传
            Thread.startVirtualThread(() -> MinioUtil.upload(newFileName, inputStream, MimeTypeUtil.probeContentType(originalFilename)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //返回地址
        return R.ok("上传成功", minIoProperties.getBaseUrl()
                + minIoProperties.getCommonBucket()
                + "/"
                + newFileName);
    }


    private static final Map<String, List<PartETag>>          etagMap = new HashMap<>();
    public static final  Map<String, List<UploadPartRequest>> partMap = new HashMap<>();

    @Deprecated
    @PostMapping("file/chunk")
    public R<Void> uploadChunk(FileChunkParam fileChunkParam) throws ExecutionException, InterruptedException, IOException {
        log.info("uploadChunk() called with parameters => [fileChunkParam = {}]", fileChunkParam);
        String uploadId = fileChunkParam.getUploadId();
        List<UploadPartRequest> uploadPartRequests = partMap.get(uploadId);
        UploadPartRequest uploadPartRequest = uploadPartRequests.get(fileChunkParam.getCurrentChunk());

        UploadPartResult uploadPartResult = MinioUtil.chunkUpload(uploadPartRequest, fileChunkParam.getFile()).get();
        List<PartETag> partETags = etagMap.get(uploadId);
        partETags.add(uploadPartResult.getPartETag());
        log.info(partETags.toString());
        etagMap.put(uploadId, partETags);
        if (fileChunkParam.getChunks() - 1 == fileChunkParam.getCurrentChunk()) {
            MinioUtil.merge(partETags, uploadId, uploadPartRequest.getKey(), minIoProperties.getCommonBucket()
            );
        }

        return R.ok("ok");
    }

    @Deprecated
    @GetMapping("file/multiPart/start")
    public R<Map<String, Object>> initMultiPartUpload(String fileName, long fileSize) {
        log.info("initMultiPartUpload() called with parameters => [fileName = {}], [fileSize = {}]", fileName, fileSize);
        if (StrUtil.isBlank(fileName)) {
            throw new BizException("文件名不能为空");
        }

        String newFileName = "file/file/"
                + UUID.randomUUID().toString().replace("-", "")
                + "-"
                + fileName;

        Map<String, Object> res = MinioUtil.initMultiPart(minIoProperties.getCommonBucket(), newFileName, fileSize);
        assert res != null;
        res.put("fileName", newFileName);

        etagMap.put(res.get("uploadId").toString(), new ArrayList<>());

        return R.ok("ok", res);
    }


    /**
     * 上传文件
     *
     * @param file
     * @return com.olrtc.common.core.domain.R<java.lang.String>
     * @author njy
     * @since 15:23 2023/3/30
     */
    @PostMapping("file")
    public R<String> uploadFile(MultipartFile file) {
        if (file == null) {
            throw new BizException("文件不能为空");
        }
        if (fileBiz.checkFileIsOverSize(file.getSize(), 1024, "M")) {
            return R.fail("文件上传失败,请上传大小不超过1GB的文件");
        }
        String originalFilename = file.getOriginalFilename();
        String newFileName = "file/file/" + UUID.randomUUID().toString().replace("-", "") + "-" + originalFilename;
        String previewFileName = "preview/file/file/" + UUID.randomUUID().toString().replace("-", "") + "-" + originalFilename;

        try {
            InputStream inputStream = file.getInputStream();
            //异步上传
            Thread.startVirtualThread(() -> {
                // 下载版
                MinioUtil.upload(newFileName, inputStream, "application/octet-stream");
                // 预览版
//                MinioUtil.upload(newFileName, inputStream, MimeTypeUtil.probeContentType(originalFilename));
            });
            //返回地址
            return R.ok("上传成功", minIoProperties.getBaseUrl()
                    + minIoProperties.getCommonBucket()
                    + "/"
                    + newFileName);
        } catch (IOException e) {
            throw new BizException("上传失败, 请检查你的网络");
        }
    }


}
