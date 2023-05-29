package com.olrtc.app.biz.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.olrtc.app.biz.CourseJobBiz;
import com.olrtc.app.model.dto.CourseJobCommitDto;
import com.olrtc.app.model.dto.CourseJobDto;
import com.olrtc.app.model.dto.UserJobCommitDto;
import com.olrtc.app.model.dto.UserJobCommitInfoDto;
import com.olrtc.app.model.entity.*;
import com.olrtc.app.model.param.CourseJobAddParam;
import com.olrtc.app.model.param.CourseJobCheckParam;
import com.olrtc.app.model.param.CourseJobCommitParam;
import com.olrtc.app.model.param.CourseJobUpdateParam;
import com.olrtc.app.openai.AiCheckCourseJob;
import com.olrtc.app.service.*;
import com.olrtc.app.service.impl.FileServiceImpl;
import com.olrtc.common.core.exception.BizException;
import com.olrtc.common.core.utils.StrUtil;
import com.olrtc.common.core.utils.file.FileUtil;
import com.olrtc.common.security.domain.LoginUser;
import com.olrtc.common.security.utils.LoginUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author njy
 * @since 2023/3/14 22:56
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseJobBizImpl implements CourseJobBiz {

    private final CourseJobService       courseJobService;
    private final CourseService          courseService;
    private final CourseUserService      courseUserService;
    private final CourseJobCommitService courseJobCommitService;
    private final AiCheckCourseJob       aiCheckCourseJob;
    private final FileService            fileService;
    private final UserService            userService;
    private final MessageService         messageService;

    @Override
    public List<CourseJobDto> getCourseJobs(Integer courseId) {
        if (courseId == null) {
            throw new BizException("课程id不能为空");
        }
        List<CourseJob> courseJobs = courseJobService.list(new LambdaQueryWrapper<CourseJob>()
                .eq(CourseJob::getCourseId, courseId)
                .orderByDesc(CourseJob::getStartTime)
        );
        if (CollectionUtils.isEmpty(courseJobs)) {
            return new ArrayList<>();
        }
        return courseJobs.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public boolean addCourseJob(CourseJobAddParam courseJobAddParam) {
        LoginUser loginUser = LoginUtil.getLoginUser();
        if (loginUser == null) {
            throw new BizException("未登录");
        }
        Course course = courseService.getById(courseJobAddParam.getCourseId());
        if (course == null) {
            throw new BizException("该课程不存在");
        }
        if (!course.getTeacherId().equals(loginUser.getId())) {
            throw new BizException("你不是该课程的老师");
        }
        if (course.getStatus() != 2) {
            throw new BizException("该课程状态不正常");
        }
        if (course.getEndTime().before(new Date())) {
            throw new BizException("该课程已过期, 无法发布作业");
        }
        CourseJob job = courseJobService.getByJobName(courseJobAddParam.getJobName(), courseJobAddParam.getCourseId());
        if (job != null) {
            throw new BizException("该作业名已经存在, 无法添加");
        }
        CourseJob courseJob = Convert.convert(CourseJob.class, courseJobAddParam);
        courseJob.setStartTime(new Date());
        boolean save = courseJobService.save(courseJob);
        if (save) {
            messageService.sendJobNoticeMessage(courseJob);
        }
        return save;
    }

    @Override
    public boolean submit(CourseJobCommitParam courseJobCommitParam) {
        Integer courseJobId = courseJobCommitParam.getCourseJobId();
        CourseJob courseJob = courseJobService.getById(courseJobId);
        if (courseJob == null) {
            throw new BizException("作业不存在, 无法提交");
        }
        if (courseJob.getEndTime().before(new Date())) {
            throw new BizException("该作业提交截止日期已过, 无法提交");
        }
        Boolean aiReview = courseJob.getAiReview();

        if (courseJobCommitParam.getCourseJobCommitId() != null) {
            // update
            CourseJobCommit courseJobCommit = courseJobCommitService
                    .getById(courseJobCommitParam.getCourseJobCommitId());

            if (courseJobCommit != null) {
                if (courseJobCommit.getStatus().equals(2)) {
                    throw new BizException("你的作业已经评分, 无法重新提交");
                }
                courseJobCommit.setContent(courseJobCommitParam.getContent());
                courseJobCommit.setCommitFileLink(courseJobCommitParam.getCommitFileLink());
                courseJobCommit.setCommitTime(new Date());
                boolean f = courseJobCommitService.updateById(courseJobCommit);
                if (f) {
                    if (aiReview) {
                        aiCheck(courseJob, courseJobCommit);
                    }
                }
                return f;
            }
        }

        CourseJobCommit courseJobCommit = Convert.convert(CourseJobCommit.class, courseJobCommitParam);
        courseJobCommit.setCommitTime(new Date());
        courseJobCommit.setStatus(1);

        boolean save = courseJobCommitService.save(courseJobCommit);
        if (save) {
            if (aiReview) {
                aiCheck(courseJob, courseJobCommit);
            }
        }
        return save;
    }

    @Override
    public void delCourseJob(Integer courseJobId) {
        Integer userId = LoginUtil.getUserId();
        if (userId == null) {
            throw new BizException("未登录");
        }
        CourseJob courseJob = courseJobService.getById(courseJobId);
        if (courseJob == null) {
            throw new BizException("该课程作业不存在");
        }
        Course course = courseService.getById(courseJob.getCourseId());
        if (!course.getTeacherId().equals(userId)) {
            throw new BizException("你不是该课程的老师,无法删除");
        }
        courseJobService.removeById(courseJobId);
    }

    @Override
    public boolean updateCourseJob(CourseJobUpdateParam courseJobUpdateParam) {
        Integer userId = LoginUtil.getUserId();
        CourseJob courseJob = courseJobService.getById(courseJobUpdateParam.getId());
        if (courseJob == null)
            throw new BizException("课程作业不存在");
        Course course = courseService.getById(courseJob.getCourseId());
        if (course == null)
            throw new BizException("课程不存在");
        if (!course.getTeacherId().equals(userId))
            throw new BizException("你不是该课程的老师, 无权修改!");

        courseJob.setJobName(courseJobUpdateParam.getJobName());
        courseJob.setContent(courseJobUpdateParam.getContent());
        courseJob.setAiReview(courseJobUpdateParam.getAiReview());
        courseJob.setEndTime(courseJobUpdateParam.getEndTime());
        courseJob.setJobFileLink(courseJobUpdateParam.getJobFileLink());

        return courseJobService.updateById(courseJob);
    }

    @Override
    public CourseJobCommitDto getUserCommitJob(Integer courseJobId, Integer userId) {
        if (courseJobId == null || userId == null) {
            throw new BizException("param is null");
        }
        CourseJobCommit userCourseJobCommit = courseJobCommitService.getUserCourseJobCommit(courseJobId, userId);
        if (userCourseJobCommit == null) {
            return null;
        }
        return convertCourseJobCommit(userCourseJobCommit);
    }

    @Override
    public UserJobCommitInfoDto getCommitNumInfo(Integer courseJobId) {
        UserJobCommitInfoDto userJobCommitInfoDto = new UserJobCommitInfoDto();
        CourseJob courseJob = courseJobService.getById(courseJobId);
        if (courseJob == null) {
            throw new BizException("作业不存在");
        }
        List<CourseUser> courseUsers = courseUserService.getCourseUsersByCourseId(courseJob.getCourseId());
        long userNum = courseUsers.stream()
                .filter(courseUser -> courseUser.getStatus() == 2)
                .count();
        List<CourseJobCommit> commits = courseJobCommitService.getCourseJobCommitByCourseJobId(courseJobId);
        long checkNum = commits.stream()
                .filter(courseJobCommit -> courseJobCommit.getAiCheck() != null)
                .count();
        long commitPercent = 0;
        long checkPercent = 0;
        if (userNum != 0) {
            commitPercent = commits.size() * 100L / userNum;
        }
        if (commits.size() != 0) {
            checkPercent = checkNum * 100 / commits.size();
        }
        userJobCommitInfoDto.setTotalNum(userNum);
        userJobCommitInfoDto.setCommitNum(commits.size());
        userJobCommitInfoDto.setCheckNum(checkNum);
        userJobCommitInfoDto.setCommitPercent(commitPercent);
        userJobCommitInfoDto.setCheckPercent(checkPercent);
        return userJobCommitInfoDto;
    }

    @Override
    public List<UserJobCommitDto> getUserCommitInfoList(Integer courseJobId) {
        ArrayList<UserJobCommitDto> res = new ArrayList<>();
        List<CourseJobCommit> commits = courseJobCommitService.getCourseJobCommitByCourseJobId(courseJobId);
        commits.forEach(courseJobCommit -> {
            Integer userId = courseJobCommit.getStudentId();
            User user = userService.getById(userId);
            Boolean aiCheck = courseJobCommit.getAiCheck();
            String checkInfoTag = "warning";
            String checkInfo = "未批改";
            if (aiCheck != null) {
                if (aiCheck) {
                    checkInfoTag = "";
                    checkInfo = "AI批改";
                } else {
                    checkInfoTag = "success";
                    checkInfo = "人工批改";
                }
            }
            CourseJobCommitDto courseJobCommitDto = this.convertCourseJobCommit(courseJobCommit);
            res.add(new UserJobCommitDto()
                    .setUserId(user != null ? user.getId() : -1)
                    .setRealName(user != null ? user.getRealName() : "")
                    .setUserName(user != null ? user.getUserName() : "")
                    .setCommitTime(courseJobCommitDto.getCommitTime())
                    .setCheckInfoTag(checkInfoTag)
                    .setCheckInfo(checkInfo)
                    .setCourseJobCommitDto(courseJobCommitDto)
            );
        });
        return res;
    }

    @Override
    public void downloadCommitRes(Integer courseJobCommitId, HttpServletResponse response) {
        CourseJobCommit courseJobCommit = courseJobCommitService.getById(courseJobCommitId);
        if (courseJobCommit == null) {
            throw new BizException("该提交不存在");
        }
        if (!courseJobCommit.getStatus().equals(2)) {
            throw new BizException("该提交还未完成批阅, 请等待");
        }
        String summaryPath = courseJobCommit.getSummaryPath();
        if (StrUtil.isBlank(summaryPath)) {
            throw new BizException("pdf正在生成中, 请等待");
        }
        CourseJob courseJob = courseJobService.getById(courseJobCommit.getCourseJobId());
        User user = userService.getById(courseJobCommit.getStudentId());
        String fileName = user.getUserName() +
                "-" +
                user.getRealName() +
                "-" +
                courseJob.getJobName() +
                ".pdf";

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(summaryPath + "answer.pdf");
            out = response.getOutputStream();
            //设置返回文件信息
            response.setContentType("application/octet-stream;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            //java.net.URLEncoder.encode(fileName, String.valueOf(StandardCharsets.UTF_8)) + ".pdf"
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            int len = 0;
            // 定义字节流缓冲数组
            byte[] buf = new byte[2048];
            while ((len = in.read(buf, 0, 2048)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
        } catch (Exception e) {
            log.error("下载文件失败", e);
            throw new BizException("下载文件失败");
        } finally {
            //关闭流信息
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                log.error("流关闭错误");
            }
        }

    }

    @Override
    public void downloadCourseJobCommits(Integer courseJobId, HttpServletResponse response) {
        CourseJob courseJob = courseJobService.getById(courseJobId);
        if (courseJob == null) {
            throw new BizException("该作业不存在");
        }
        Course course = courseService.getById(courseJob.getCourseId());
        if (course == null) {
            throw new BizException("该课程不存在");
        }
        String filePath = FileServiceImpl.BASE_FILE_PATH +
                course.getCourseCode() +
                "/" +
                courseJob.getId();

        String outPath = FileServiceImpl.BASE_FILE_PATH +
                course.getCourseCode() +
                "/" +
                courseJob.getId() +
                ".zip";
        File file = new File(outPath);
        if (file.exists()) {
            file.delete();
        }
        try (FileOutputStream fos = new FileOutputStream(outPath)) {
            boolean zip = FileUtil.toZip(filePath, fos, true);
            if (zip) {
                try (InputStream in = new FileInputStream(outPath);
                     OutputStream out = response.getOutputStream()) {
                    response.setContentType("application/octet-stream;charset=utf-8");
                    response.setCharacterEncoding("UTF-8");
                    response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(courseJob.getJobName() + ".zip", StandardCharsets.UTF_8));
                    int len = 0;
                    // 定义字节流缓冲数组
                    byte[] buf = new byte[2048];
                    while ((len = in.read(buf, 0, 2048)) != -1) {
                        out.write(buf, 0, len);
                    }
                } catch (Exception e) {
                    log.error("下载文件失败", e);
                    throw new BizException("下载文件失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean check(CourseJobCheckParam courseJobCheckParam) {
        CourseJobCommit courseJobCommit = courseJobCommitService.getById(courseJobCheckParam.getCourseJobCommitId());
        if (courseJobCommit == null) {
            throw new BizException("提交信息不存在");
        }
        courseJobCommit.setScore(courseJobCheckParam.getScore());
        courseJobCommit.setComment(courseJobCheckParam.getComment());
        courseJobCommit.setAiCheck(false);
        courseJobCommit.setStatus(2);
        boolean f = courseJobCommitService.updateById(courseJobCommit);
        if (f) {
            // 收集作业
            fileService.collectUserJobCommit(courseJobCommit)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("收集作业错误");
                            return;
                        }
                        messageService.sendJobCheckMessage(courseJobCommit);
                    });
        }
        return f;
    }


    private void aiCheck(CourseJob courseJob, CourseJobCommit courseJobCommit) {
        aiCheckCourseJob.check(courseJob.getContent().replaceAll("\r\n|\r|\n", ""),
                        courseJobCommit.getContent().replaceAll("\r\n|\r|\n", ""))
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("ai评分错误");
                        // todo retry
                        return;
                    }
                    Boolean success = result.getSuccess();
                    if (success) {
                        courseJobCommit.setStatus(2);
                        courseJobCommit.setAiCheck(true);
                        courseJobCommit.setScore(result.getScore());
                        courseJobCommit.setComment(result.getComment());
                        boolean f = courseJobCommitService.updateById(courseJobCommit);
                        if (f) {
                            log.info("ai 批改完成");
                            // 收集作业
                            fileService.collectUserJobCommit(courseJobCommit)
                                    .whenComplete((result1, ex1) -> {
                                        if (ex1 != null) {
                                            log.error("收集作业错误");
                                            return;
                                        }
                                        messageService.sendJobCheckMessage(courseJobCommit);
                                    });
                        } else {
                            // todo retry
                            log.info("ai 批改失败");
                        }
                    }
                });
    }

    public CourseJobDto convert(CourseJob courseJob) {
        CourseJobDto courseJobDto = Convert.convert(CourseJobDto.class, courseJob);
        courseJobDto.setStartTime(DateUtil.formatDateTime(courseJob.getStartTime()));
        courseJobDto.setEndTime(DateUtil.formatDateTime(courseJob.getEndTime()));
        return courseJobDto;
    }

    public CourseJobCommitDto convertCourseJobCommit(CourseJobCommit courseJobCommit) {
        CourseJobCommitDto courseJobCommitDto = Convert.convert(CourseJobCommitDto.class, courseJobCommit);
        courseJobCommitDto.setCommitTime(DateUtil.formatDateTime(courseJobCommit.getCommitTime()));
        return courseJobCommitDto;
    }
}
