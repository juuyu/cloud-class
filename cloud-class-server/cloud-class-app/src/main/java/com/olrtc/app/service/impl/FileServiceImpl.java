package com.olrtc.app.service.impl;

import com.olrtc.app.config.properties.MinIoProperties;
import com.olrtc.app.model.entity.Course;
import com.olrtc.app.model.entity.CourseJob;
import com.olrtc.app.model.entity.CourseJobCommit;
import com.olrtc.app.model.entity.User;
import com.olrtc.app.service.*;
import com.olrtc.app.utils.CourseJobFileUtil;
import com.olrtc.app.utils.MinioUtil;
import com.olrtc.common.core.utils.StrUtil;
import com.olrtc.common.core.utils.file.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

/**
 * @author njy
 * @since 2023/4/7 09:56
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final CourseService          courseService;
    private final CourseJobService       courseJobService;
    private final CourseJobCommitService courseJobCommitService;
    private final UserService            userService;
    private final MinIoProperties        minIoProperties;

    public static final String BASE_FILE_PATH     = "/Users/yu/my/docker/cloud-class/data/job/";
    public static final String USER_FILE_FORMAT   = "%s-%s";
    public static final String OUTPUT_PATH_FORMAT = "%s/%s/%s/";

    @Override
    public CompletableFuture<Void> collectUserJobCommit(CourseJobCommit courseJobCommit) {
        return CompletableFuture.runAsync(() -> {
            CourseJob courseJob = courseJobService.getById(courseJobCommit.getCourseJobId());

            String pdfContent = CourseJobFileUtil.generateCourseJobPdfContent(courseJob.getJobName(),
                    courseJob.getContent(),
                    courseJobCommit.getContent(),
                    courseJobCommit.getScore().toString(),
                    courseJobCommit.getComment());

            Course course = courseService.getById(courseJob.getCourseId());
            User user = userService.getById(courseJobCommit.getStudentId());

            String userFileName = String.format(USER_FILE_FORMAT, user.getUserName(), user.getRealName());
            String courseJobPath = String.format(OUTPUT_PATH_FORMAT, course.getCourseCode(), courseJob.getId(), userFileName);

            String outPutPath = BASE_FILE_PATH + courseJobPath;
            File file = new File(outPutPath);
            if (!file.exists()) {
                boolean f = file.mkdirs();
                if (!f) {
                    log.error("创建文件夹失败");
                    return;
                }
            }
            String commitFileLink = courseJobCommit.getCommitFileLink();
            if (StrUtil.isNotBlank(commitFileLink)) {
                String extension = FileUtil.getExtension(commitFileLink);
                downloadFileFormUrl(commitFileLink, outPutPath, "附件." + extension);
            }
            boolean f = CourseJobFileUtil.generateJobCommitPdf(pdfContent, outPutPath + "answer.pdf");
            if (f) {
                String newFileName = courseJobPath + user.getUserName() +
                        "-" +
                        user.getRealName() +
                        "-" +
                        courseJob.getJobName() +
                        ".pdf";
                uploadPdf(newFileName, outPutPath + "answer.pdf");
                courseJobCommit.setSummaryPath(outPutPath);
                courseJobCommit.setSummaryLink(minIoProperties.getBaseUrl() +
                        minIoProperties.getCommonBucket() +
                        "/" +
                        newFileName);
                courseJobCommitService.updateById(courseJobCommit);
            } else {
                log.error("生成pdf失败");
            }
        });
    }

    private CompletableFuture<Void> uploadPdf(String fileName, String filePath) {
        return CompletableFuture.runAsync(() -> {
            try (FileInputStream in = new FileInputStream(filePath)) {
                MinioUtil.upload(fileName, in, "application/octet-stream");
            } catch (Exception e) {
                log.error("文件上传失败,err:", e);
            }
        });
    }

    private CompletableFuture<Void> downloadFileFormUrl(String fileUrl, String savePath, String fileName) {
        return CompletableFuture.runAsync(() -> {
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //设置超时间为20秒
                conn.setConnectTimeout(20 * 1000);
                //得到输入流
                InputStream inputStream = conn.getInputStream();
                //获取自己数组
                byte[] getData = readInputStream(inputStream);
                //文件保存位置
                File saveDir = new File(savePath);
                if (!saveDir.exists()) {
                    boolean f = saveDir.mkdirs();
                    if (!f) {
                        log.error("创建文件夹失败");
                        return;
                    }
                }
                File file = new File(saveDir + File.separator + fileName);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(getData);
                fos.close();
                inputStream.close();
            } catch (Exception e) {
                log.error("下载文件失败, url:{}", fileUrl, e);
            }
        });
    }

    private byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

}
