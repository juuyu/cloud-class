package com.olrtc.app.model.param;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author njy
 * @since 2023/3/30 16:33
 */
@Data
public class FileChunkParam {

    // 文件
    private MultipartFile file;

    private String uploadId;
    // 当前chunk
    private int           currentChunk;
    // chunk nums
    private int           chunks;

}
