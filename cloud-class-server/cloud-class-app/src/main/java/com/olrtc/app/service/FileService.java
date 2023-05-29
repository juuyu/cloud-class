package com.olrtc.app.service;

import com.olrtc.app.model.entity.CourseJobCommit;

import java.util.concurrent.CompletableFuture;

/**
 * @author njy
 * @since 2023/4/7 09:56
 */
public interface FileService {

    /**
     * 收集用户作业提交信息
     *
     * @param courseJobCommit
     * @return void
     * @author njy
     * @since 09:58 2023/4/7
     */
    CompletableFuture<Void> collectUserJobCommit(CourseJobCommit courseJobCommit);

}
