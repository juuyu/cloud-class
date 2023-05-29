package com.olrtc.app.biz.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.olrtc.app.biz.CourseFileBiz;
import com.olrtc.app.model.dto.CourseFileDto;
import com.olrtc.app.model.entity.Course;
import com.olrtc.app.model.entity.CourseFile;
import com.olrtc.app.model.param.CourseFileAddParam;
import com.olrtc.app.service.CourseFileService;
import com.olrtc.app.service.CourseService;
import com.olrtc.common.core.exception.BizException;
import com.olrtc.common.core.utils.file.FileUtil;
import com.olrtc.common.security.domain.LoginUser;
import com.olrtc.common.security.utils.LoginUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author njy
 * @since 2023/3/14 22:43
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseFileBizImpl implements CourseFileBiz {

    private final CourseFileService courseFileService;
    private final CourseService     courseService;

    @Override
    public List<CourseFileDto> getCourseFiles(Integer courseId) {
        if (courseId == null) {
            throw new BizException("课程id不能为空");
        }
        List<CourseFile> files = courseFileService.list(new LambdaQueryWrapper<CourseFile>()
                .eq(CourseFile::getCourseId, courseId)
                .orderByDesc(CourseFile::getUploadTime)
        );
        if (CollectionUtils.isEmpty(files)) {
            return new ArrayList<>();
        }
        return files.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public boolean addCourseFile(CourseFileAddParam courseFileAddParam) {
        LoginUser loginUser = LoginUtil.getLoginUser();
        if (loginUser == null) {
            throw new BizException("未登录");
        }
        Course course = courseService.getById(courseFileAddParam.getCourseId());
        if (course == null) {
            throw new BizException("该课程不存在");
        }
        if (course.getStatus() != 2) {
            throw new BizException("该课程状态不正常");
        }
        if (course.getEndTime().before(new Date())) {
            throw new BizException("该课程已过期, 无法上传文件");
        }
        if (!course.getTeacherId().equals(loginUser.getId())) {
            throw new BizException("你不是该课程的老师");
        }
        String trueFileName = courseFileAddParam.getTrueFileName();
        String extension = FileUtil.getExtension(trueFileName);
        String cover = switch (extension) {
            case "docx" -> "http://localhost:9000/cloud-class/icon/word.svg";
            case "pdf" -> "http://localhost:9000/cloud-class/icon/pdf.svg";
            case "xlsx" -> "http://localhost:9000/cloud-class/icon/excel.svg";
            case "pptx" -> "http://localhost:9000/cloud-class/icon/ppt.svg";
            case "zip" -> "http://localhost:9000/cloud-class/icon/zip.svg";
            default -> "http://localhost:9000/cloud-class/icon/file.svg";
        };

        CourseFile courseFile = Convert.convert(CourseFile.class, courseFileAddParam);
        courseFile.setUploadUserName(loginUser.getRealName());
        courseFile.setUploadTime(new Date());
        courseFile.setCover(cover);

        boolean save = courseFileService.save(courseFile);
        return save;
    }

    @Override
    public void delCourseFile(Integer courseFileId) {
        Integer userId = LoginUtil.getUserId();
        if (userId == null) {
            throw new BizException("未登录");
        }
        CourseFile courseFile = courseFileService.getById(courseFileId);
        if (courseFile == null) {
            throw new BizException("该课程文件不存在");
        }
        Course course = courseService.getById(courseFile.getCourseId());
        if (!course.getTeacherId().equals(userId)) {
            throw new BizException("你不是该课程的老师,无法删除");
        }
        courseFileService.removeById(courseFileId);
    }


    private CourseFileDto convert(CourseFile courseFile) {
        CourseFileDto courseFileDto = Convert.convert(CourseFileDto.class, courseFile);
        courseFileDto.setUploadTime(DateUtil.formatDate(courseFile.getUploadTime()));
        return courseFileDto;
    }


}
