package com.olrtc.app.utils;

import cn.hutool.core.date.DateUtil;
import com.olrtc.app.model.entity.Course;
import com.olrtc.app.model.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author njy
 * @since 2023/4/11 09:13
 */
public class MessageUtil {

    private static final String INVITE_MESSAGE_FORMAT           = "<p style=\"text-align: start;\">尊敬的用户你好！</p><p style=\"text-align: start;\">${realName}&lt;${userName}&gt;邀请你加入他开设的课程《${courseName}》中。</p><p style=\"text-align: start;\">课程信息：</p><table style=\"width: 100%;\"><tbody><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">课程名</td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">${courseName}</td></tr><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">老师</td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">${realName}</td></tr><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">课程类型</td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">${courseType}</td></tr><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">课程时间</td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">${courseTime}</td></tr><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">课程人数</td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">${courseNum}</td></tr><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">课程安排</td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">${courseArrange}</td></tr><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">课程简介</td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">${courseIntro}<br></td></tr></tbody></table><p><br></p>";
    private static final String ALLOW_USER_JOIN_MESSAGE_FORMAT  = "<p>尊敬的用户你好！</p><p>你申请加入<strong>${realName}&lt;${userName}&gt;</strong>的《<span style=\"color: rgb(235, 144, 58);\">${courseName}</span>》课程的申请<span style=\"color: rgb(225, 60, 57);\">已通过</span>！</p>";
    private static final String REFUSE_USER_JOIN_MESSAGE_FORMAT = "<p>尊敬的用户你好！</p><p>很抱歉, 你申请加入<strong>${realName}&lt;${userName}&gt;</strong>的《<span style=\"color: rgb(235, 144, 58);\">${courseName}</span>》课程的申请<span style=\"color: rgb(225, 60, 57);\">未通过</span>！</p>";
    private static final String APPLY_MESSAGE_FORMAT            = "<p style=\"text-align: start;\">尊敬的用户你好！</p><p style=\"text-align: start;\"><strong>${realName}&lt;${userName}&gt;</strong>申请加入你开设的课程《<span style=\"color: rgb(235, 144, 58);\">${courseName}</span>》中。</p><table style=\"width: 100%;\"><tbody><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">姓名</td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">${realName}</td></tr><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">账号</td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">${userName}</td></tr><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">性别</td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">${sex}</td></tr><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">年龄</td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">${old}</td></tr><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">电话</td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">${tel}</td></tr><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">邮箱</td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">${email}</td></tr></tbody></table><p><br></p>";
    private static final String USER_JOIN_MESSAGE_FORMAT        = "<p style=\"text-align: start;\">尊敬的用户你好！</p><p style=\"text-align: start;\"><strong>${realName}&lt;${userName}&gt;</strong>成功加入你《<span style=\"color: rgb(235, 144, 58);\">${courseName}</span>》课程。</p>";
    private static final String USER_REFUSE_MESSAGE_FORMAT      = "<p style=\"text-align: start;\">尊敬的用户你好！</p><p style=\"text-align: start;\">很遗憾，<strong>${realName}&lt;${userName}</strong><span style=\"color: rgb(225, 60, 57);\">拒绝</span>加入你的《<span style=\"color: rgb(235, 144, 58);\">${courseName}</span>》课程。</p>";
    private static final String KICK_USER_MESSAGE_FORMAT        = "<p style=\"text-align: start;\">尊敬的用户你好！</p><p style=\"text-align: start;\">很遗憾，你被移出<strong>${realName}&lt;${userName}&gt;</strong>的《${courseName}》课程。</p>";
    private static final String LIVE_NOTICE_MESSAGE_FORMAT      = "<p style=\"text-align: start;\">尊敬的用户你好！</p><p style=\"text-align: start;\">你加入的<strong>${realName}&lt;${userName}&gt;</strong>的《${courseName}》课程，<span style=\"color: rgb(56, 158, 13);\">老师已经开始上课了，请及时进入课堂。</span></p><p style=\"text-align: start;\">";
    private static final String JOB_NOTICE_MESSAGE_FORMAT       = "<p style=\"text-align: start;\">尊敬的用户你好！</p><p style=\"text-align: start;\">你加入的《${courseName}》课程有一条新的作业发布，请及时完成。</p><table style=\"width: 100%;\"><tbody><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">作业名</td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">${jobName}</td></tr><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">截止日期</td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">${endTime}</td></tr></tbody></table>";
    private static final String JOB_CHECK_COMPLETE_FORMAT       = "<p style=\"text-align: start;\">尊敬的用户你好！</p><p style=\"text-align: start;\">你的作业已经被批改完成，请查看。</p><table style=\"width: 100%;\"><tbody><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">课程名</td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">${courseName}</td></tr><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">作业名</td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">${jobName}</td></tr><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">提交日期</td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">${commitTime}</td></tr><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">评分</td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">${score}</td></tr><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">评语</td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\">${comment}</td></tr></tbody></table><p>点击 <a href=\"${link}\" target=\"_blank\">链接</a> 下载。</p>";
    private static final String MAIL_AUTH_CODE_FORMAT           = "<h1>尊敬的用户：您好</h1><p>您正在<strong>cloud-class</strong>平台进行<span style=\"color: rgb(225, 60, 57);\">注册账号</span>操作，以下是你的验证码，有效期5分钟。</p><blockquote>${authCode}</blockquote><p><br></p>";
    //http://localhost/#/course/info/admin?courseId=2&courseCode=ecyqzvnjrpfhbgsl


    public static String jobCheckMessage(String courseName,
                                         String jobName,
                                         String commitTime,
                                         String score,
                                         String comment,
                                         String link) {
        HashMap<String, String> map = new HashMap<>();
        map.put("courseName", courseName);
        map.put("jobName", jobName);
        map.put("commitTime", commitTime);
        map.put("score", score);
        map.put("comment", comment);
        map.put("link", link);
        return replacePlaceholders(JOB_CHECK_COMPLETE_FORMAT, map);
    }

    public static String jobNoticeMessage(String courseName,
                                          String jobName,
                                          String endTime,
                                          String link) {
        HashMap<String, String> map = new HashMap<>();
        map.put("courseName", courseName);
        map.put("jobName", jobName);
        map.put("endTime", endTime);
        return replacePlaceholders(JOB_NOTICE_MESSAGE_FORMAT, map);
    }

    public static String liveNoticeMessage(String realName,
                                           String userName,
                                           String courseName,
                                           String link) {
        HashMap<String, String> map = new HashMap<>();
        map.put("realName", realName);
        map.put("userName", userName);
        map.put("courseName", courseName);
        return replacePlaceholders(LIVE_NOTICE_MESSAGE_FORMAT, map);
    }


    /**
     * 获取邀请用户加入课程信息
     *
     * @param course
     * @param user
     * @return java.lang.String
     * @author njy
     * @since 15:58 2023/4/11
     */
    public static String getInviteMessage(Course course, User user) {
        String courseTime = DateUtil.formatDate(course.getStartTime()) +
                " ~ " +
                DateUtil.formatDate(course.getEndTime());
        HashMap<String, String> map = new HashMap<>();
        map.put("realName", user.getRealName());
        map.put("userName", user.getUserName());
        map.put("courseName", course.getCourseName());
        map.put("courseType", course.getCourseType().getType());
        map.put("courseTime", courseTime);
        map.put("courseNum", course.getUserNum().toString());
        map.put("courseArrange", CourseArrangeUtil.formatCourseSchedule(course.getWeekArrange()));
        map.put("courseIntro", course.getIntro());
        return replacePlaceholders(INVITE_MESSAGE_FORMAT, map);
    }

    /**
     * 获取用户申请加入课程信息
     *
     * @param course
     * @param user
     * @return java.lang.String
     * @author njy
     * @since 16:19 2023/4/11
     */
    public static String getApplyMessage(Course course, User user) {
        String sex = switch (user.getSex()) {
            case 2 -> "男";
            case 3 -> "女";
            default -> "未知";
        };
        HashMap<String, String> map = new HashMap<>();
        map.put("realName", user.getRealName());
        map.put("userName", user.getUserName());
        map.put("courseName", course.getCourseName());
        map.put("sex", sex);
        map.put("old", user.getAge().toString());
        map.put("tel", user.getTel());
        map.put("email", user.getEmail());
        return replacePlaceholders(APPLY_MESSAGE_FORMAT, map);
    }


    /**
     * 获取用户同意加入信息
     *
     * @param course
     * @param user
     * @return java.lang.String
     * @author njy
     * @since 16:21 2023/4/11
     */
    public static String userJoinMessage(Course course, User user) {
        HashMap<String, String> map = new HashMap<>();
        map.put("realName", user.getRealName());
        map.put("userName", user.getUserName());
        map.put("courseName", course.getCourseName());
        return replacePlaceholders(USER_JOIN_MESSAGE_FORMAT, map);
    }


    /**
     * 获取用户拒绝加入信息
     *
     * @param course
     * @param user
     * @return java.lang.String
     * @author njy
     * @since 16:22 2023/4/11
     */
    public static String userRefuseMessage(Course course, User user) {
        HashMap<String, String> map = new HashMap<>();
        map.put("realName", user.getRealName());
        map.put("userName", user.getUserName());
        map.put("courseName", course.getCourseName());
        return replacePlaceholders(USER_REFUSE_MESSAGE_FORMAT, map);
    }

    /**
     * 获取同意用户加入课程成功信息
     *
     * @param course
     * @param user
     * @return java.lang.String
     * @author njy
     * @since 15:59 2023/4/11
     */
    public static String getAllowUserJoinMessage(Course course, User user) {
        HashMap<String, String> map = new HashMap<>();
        map.put("realName", user.getRealName());
        map.put("userName", user.getUserName());
        map.put("courseName", course.getCourseName());
        return replacePlaceholders(ALLOW_USER_JOIN_MESSAGE_FORMAT, map);
    }

    /**
     * 获取拒绝用户加入课程信息
     *
     * @param course
     * @param user
     * @return java.lang.String
     * @author njy
     * @since 15:59 2023/4/11
     */
    public static String getRefuseUserJoinMessage(Course course, User user) {
        HashMap<String, String> map = new HashMap<>();
        map.put("realName", user.getRealName());
        map.put("userName", user.getUserName());
        map.put("courseName", course.getCourseName());
        return replacePlaceholders(REFUSE_USER_JOIN_MESSAGE_FORMAT, map);
    }


    /**
     * 获取踢人的信息通知
     *
     * @param course
     * @param user
     * @return java.lang.String
     * @author njy
     * @since 14:11 2023/4/12
     */
    public static String getKickUserMessage(Course course, User user) {
        HashMap<String, String> map = new HashMap<>();
        map.put("realName", user.getRealName());
        map.put("userName", user.getUserName());
        map.put("courseName", course.getCourseName());
        return replacePlaceholders(KICK_USER_MESSAGE_FORMAT, map);
    }


    /**
     * 获取邮箱验证码信息
     */
    public static String getMailAuthMessage(String authCode) {
        HashMap<String, String> map = new HashMap<>();
        map.put("authCode", authCode);
        return replacePlaceholders(MAIL_AUTH_CODE_FORMAT, map);
    }

    private static String replacePlaceholders(String str, Map<String, String> replacements) {
        String pattern = "\\$\\{(" + String.join("|", replacements.keySet()) + ")\\}";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(str);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            String replacement = replacements.get(matcher.group(1));
            if (replacement != null) {
                matcher.appendReplacement(buffer, replacement);
            }
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

}
