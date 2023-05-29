package com.olrtc.app.biz.impl;

import com.olrtc.app.biz.FileBiz;
import com.olrtc.common.core.exception.BizException;
import com.olrtc.common.core.utils.StrUtil;
import com.olrtc.common.core.utils.file.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author njy
 * @since 2023/3/28 10:06
 */
@Slf4j
@Service
public class FileBizImpl implements FileBiz {


    @Override
    public boolean checkFileIsImg(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = FileUtil.getExtension(originalFilename);
        if (StrUtil.isBlank(extension)) {
            throw new BizException("文件格式错误");
        }
        return "png".equals(extension) || "jpg".equals(extension);
    }

    @Override
    public boolean checkFileIsOverSize(Long len, int size, String unit) {
        double fileSize = 0;
        if ("B".equalsIgnoreCase(unit)) {
            fileSize = (double) len;
        } else if ("K".equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1024;
        } else if ("M".equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1048576;
        } else if ("G".equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1073741824;
        }
        return (fileSize > size);
    }
}
