package com.olrtc.app.web;

import com.olrtc.app.biz.RecordBiz;
import com.olrtc.app.model.dto.CourseDto;
import com.olrtc.app.model.dto.RecordDto;
import com.olrtc.app.model.param.CourseQueryParam;
import com.olrtc.app.model.param.RecordQueryParam;
import com.olrtc.app.service.CourseService;
import com.olrtc.common.core.domain.R;
import com.olrtc.common.mybatis.core.page.PageQuery;
import com.olrtc.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author njy
 * @since 2023/4/27 15:32
 */
@Slf4j
@RestController
@RequestMapping("open")
@RequiredArgsConstructor
public class OpenController {

    private final CourseService courseService;
    private final RecordBiz     recordBiz;


    /**
     * 获取公开课程列表
     *
     * @return com.olrtc.common.core.domain.R<com.olrtc.common.mybatis.core.page.TableDataInfo < com.olrtc.app.model.dto.CourseDto>>
     */
    @GetMapping("course")
    public R<TableDataInfo<CourseDto>> getOpenCourse(PageQuery pageQuery, CourseQueryParam param) {
        TableDataInfo<CourseDto> courseList = courseService.getOpenCourseList(pageQuery, param);
        return R.ok("查询成功", courseList);
    }

    /**
     * 获取公开视频列表
     *
     * @return com.olrtc.common.core.domain.R<com.olrtc.common.mybatis.core.page.TableDataInfo < com.olrtc.app.model.dto.CourseDto>>
     */
    @GetMapping("video")
    public R<TableDataInfo<RecordDto>> listUserRecord(PageQuery pageQuery, RecordQueryParam param) {
        log.info("listUserRecord() called with parameters => [pageQuery = {}], [param = {}]", pageQuery, param);
        TableDataInfo<RecordDto> videos = recordBiz.listOpenRecord(pageQuery, param);
        return R.ok("查询成功", videos);
    }

}
