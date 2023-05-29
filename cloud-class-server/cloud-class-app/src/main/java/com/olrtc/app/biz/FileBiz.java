package com.olrtc.app.biz;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author njy
 * @since 2023/3/28 10:06
 */
public interface FileBiz {

    /**
     * 检查文件是否是图片
     *
     * @param file
     * @return boolean
     * @author njy
     * @since 11:03 2023/3/28
     */
    boolean checkFileIsImg(MultipartFile file);


    /**
     * 检查文件是否超过size
     *
     * @param len
     * @param size
     * @param unit
     * @return boolean
     * @author njy
     * @since 11:14 2023/3/28
     */
    boolean checkFileIsOverSize(Long len, int size, String unit);

}
