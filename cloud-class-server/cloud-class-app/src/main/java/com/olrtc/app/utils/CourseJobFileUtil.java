package com.olrtc.app.utils;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;

/**
 * @author njy
 * @since 2023/4/7 14:50
 */
@Slf4j
public class CourseJobFileUtil {

    // 作业保存格式模板
    public static final String COURSE_JOB_FILE_FORMAT = "<html><body><h1>%s</h1><h2>内容</h2><div style=\"border: 1px solid #CDD0D6;padding: 10px\">%s</div><h2>回答</h2><div style=\"border: 1px solid #CDD0D6;padding: 10px\">%s</div><h2>评价</h2><div style=\"border: 1px solid #CDD0D6;padding: 10px\">评分:&nbsp;%s&nbsp;/&nbsp;10</div><div style=\"border: 1px solid #CDD0D6;border-top: none;padding: 10px\">评语:&nbsp;%s</div></body></html>";


    private static final ConverterProperties baseConverterProperties = new ConverterProperties()
            .setFontProvider(
                    new DefaultFontProvider(true, true, true));

    //            converterProperties.setBaseUri("path/to/your/html/files/");


    /**
     * 生成pdf文件内容
     *
     * @param jobName          作业名
     * @param jobContent       作业内容
     * @param jobCommitContent 提交内容
     * @param jobCommitScore   评分
     * @param jobCommitComment 评语
     * @return java.lang.String
     * @author njy
     * @since 14:56 2023/4/7
     */
    public static String generateCourseJobPdfContent(String jobName,
                                                     String jobContent,
                                                     String jobCommitContent,
                                                     String jobCommitScore,
                                                     String jobCommitComment) {
        return String.format(COURSE_JOB_FILE_FORMAT,
                jobName, jobContent, jobCommitContent, jobCommitScore, jobCommitComment);

    }

    public static boolean generateJobCommitPdf(String pdfContent, String outputFilePath) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(outputFilePath);
            HtmlConverter.convertToPdf(pdfContent, os, baseConverterProperties);
            os.close();
            return true;
        } catch (Exception e) {
            log.error("html to pdf error, outputFilePath:{},err:{}", outputFilePath, e.getMessage());
        }
        return false;
    }


}
