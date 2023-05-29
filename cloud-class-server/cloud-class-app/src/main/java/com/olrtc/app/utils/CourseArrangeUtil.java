package com.olrtc.app.utils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author njy
 * @since 2023/2/21 11:17
 */
public class CourseArrangeUtil {


    public static final String emptyWeekArrange = "000000000000000000000000000000000000000000";
    public static final String fullWeekArrange  = "111111111111111111111111111111111111111111";
    /*

    课程每周安排:
    ---------------------------------------
    mon | tue | wed | thu | fri | sta | sun
    ---------------------------------------
     1
    ---------------------------------------
          1
    ---------------------------------------
                             1
    ---------------------------------------
                             1
    ---------------------------------------

    ---------------------------------------

    ---------------------------------------

    例如: 如上安排, "测试课程" 每周的安排是周一的第一节, 周二的第二节, 周五的3、4节,
    那么对应的数据库存储是 100000、010000、000000、000000、001100、000000、000000
    [32、16、00、00、12、00、00]
    32160000120000

    加入新课程判断用户已有的课程时间是否冲突, 将课程安排时间进行与(&)运算, 若不为0, 则冲突

    计算用户课程表:
    用户的课程表的每周课程安排进行异或(^)运算, 得到的结果则是用户的课程安排


     */

    private static final int WEEK_DAY    = 7;
    private static final int DAY_SECTION = 6;

    public static String getTodayArrange(String weekArrange) {
        String[] dayOfWeek = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w == 0)
            w = 7;

        // 获取今天课程安排
        int index = (w - 1) * DAY_SECTION;
        String todayArrange = weekArrange.substring(index, index + DAY_SECTION);
        // 判断今天是否有课
        if (todayArrange.equals("000000")) {
            return null;
        }
        // 获取今天课程具体安排
        List<String> result = new ArrayList<>();
        List<Integer> lessons = new ArrayList<>();
        for (int lesson = 1; lesson <= DAY_SECTION; lesson++) {
            if (todayArrange.charAt(lesson - 1) == '1') {
                lessons.add(lesson);
            }
        }
        if (lessons.size() > 0) {
            if (lessons.size() == DAY_SECTION) {
                result.add(dayOfWeek[w - 1] + "全天");
            } else {
                String lessonStr = lessons.stream()
                        .sorted()
                        .map(Object::toString)
                        .collect(Collectors.joining("、"));
                result.add(dayOfWeek[w - 1] + "第" + lessonStr + "节");
            }
        }
        return String.join(",  ", result);
    }


    public static String formatCourseSchedule(String scheduleString) {
        String[] dayOfWeek = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        List<String> result = new ArrayList<>();

        for (int day = 1; day <= WEEK_DAY; day++) {
            List<Integer> lessons = new ArrayList<>();
            for (int lesson = 1; lesson <= DAY_SECTION; lesson++) {
                int index = (day - 1) * DAY_SECTION + lesson - 1;
                if (scheduleString.charAt(index) == '1') {
                    lessons.add(lesson);
                }
            }
            if (lessons.size() > 0) {
                if (lessons.size() == DAY_SECTION) {
                    result.add(dayOfWeek[day - 1] + "全天");
                } else {
                    String lessonStr = lessons.stream()
                            .sorted()
                            .map(Object::toString)
                            .collect(Collectors.joining("、"));
                    result.add(dayOfWeek[day - 1] + "第" + lessonStr + "节");
                }
            }
        }

        return String.join(",  ", result);
    }


    /**
     * 生成用户课程表
     *
     * @param courseMap 用户课程集合, k:课程名   v:课程安排代码
     * @return java.util.ArrayList<java.util.ArrayList < java.lang.String>>
     * @author njy
     * @since 10:23 2023/2/22
     */
    public static ArrayList<ArrayList<String>> generateUserSchedule(HashMap<String, String> courseMap) {
        // 初始化课程表
        ArrayList<ArrayList<String>> schedule = new ArrayList<>(WEEK_DAY);
        for (int i = 0; i < WEEK_DAY; i++) {
            ArrayList<String> daySchedule = new ArrayList<>(DAY_SECTION);
            for (int j = 0; j < DAY_SECTION; j++) {
                daySchedule.add("");
            }
            schedule.add(daySchedule);
        }

        // 遍历选课信息，将课程填充到课程表中
        for (Map.Entry<String, String> entry : courseMap.entrySet()) {
            String courseName = entry.getKey();
            String courseSchedule = entry.getValue();
            for (int i = 0; i < WEEK_DAY; i++) {
                ArrayList<String> daySchedule = schedule.get(i);
                for (int j = 0; j < DAY_SECTION; j++) {
                    int index = i * 6 + j;
                    if (courseSchedule.charAt(index) == '1') {
                        String currentCourse = daySchedule.get(j);
                        if ("".equals(currentCourse)) {
                            daySchedule.set(j, courseName);
                        } else {
                            daySchedule.set(j, currentCourse + "|" + courseName);
                        }
                    }
                }
            }
        }
        return schedule;
    }


    public static String mergeUserCourseArrange(List<String> courseArrange) {
        long xorResult = courseArrange.stream()
                .map(s -> Long.parseLong(s, 2))
                .reduce(0L, (a, b) -> a ^ b);
        return String.format("%42s", Long.toBinaryString(xorResult)).replace(' ', '0');
    }


//    public static int[] calCourseIsRepeat(String weekArranged, String newWeekArrange) {
//        for (int i = 0; i < WEEK_DAY; i++) {
//            for (int j = 0; j < DAY_SECTION; j++) {
//                int index = i * 6 + j;
//                if (weekArranged.charAt(index) == '1' && newWeekArrange.charAt(index) == '1') {
//
//
//
//                }
//            }
//        }
//
////        long l = Long.parseLong(weekArranged, 2);
////        long l1 = Long.parseLong(newWeekArrange, 2);
//
//
//    }

    public static boolean isCourseRepeat(String weekArranged, String newWeekArrange) {
        if (emptyWeekArrange.equals(weekArranged)) {
            return false;
        }
        for (int i = 0; i < WEEK_DAY * DAY_SECTION; i++) {
            if (weekArranged.charAt(i) == '1' && newWeekArrange.charAt(i) == '1') {
                return true;
            }
        }
        return false;
    }

    public static boolean isCourseRepeat1(String weekArranged, String newWeekArrange) {
        long l = Long.parseLong(weekArranged, 2);
        long l1 = Long.parseLong(newWeekArrange, 2);
        return (l & l1) == 0;
    }

    /**
     * 解析课程安排
     *
     * @author njy
     * @since 11:32 2023/2/21
     */
    public static ArrayList<ArrayList<String>> parseWeekArrange1(String weekArrange, String courseName) {
        ArrayList<ArrayList<String>> res = new ArrayList<>(7);
        for (int i = 0; i < weekArrange.length(); i += 6) {
            String dayArrange = weekArrange.substring(i, i + 6);
            ArrayList<String> day = new ArrayList<>(6);
            for (int j = 0; j < dayArrange.length(); j++) {
                char ch = dayArrange.charAt(j);
                if (ch == '1') {
                    day.add(courseName);
                } else {
                    day.add("");
                }
            }
            res.add(day);
        }
        return res;
    }

    public static List<List<String>> parseCourseWeekArrangeParallel(String weekArrange, String courseName) {
        return IntStream.rangeClosed(0, 6)
                .parallel() // 将流转换为并行流
                .mapToObj(i -> weekArrange.substring(i * 6, i * 6 + 6))
                .map(dayArrange -> {
                    List<String> day = new ArrayList<>(Collections.nCopies(6, ""));
                    IntStream.range(0, dayArrange.length())
                            .parallel() // 将流转换为并行流
                            .filter(j -> dayArrange.charAt(j) == '1')
                            .forEach(j -> day.set(j, courseName));
                    return day;
                })
                .collect(Collectors.toList());
    }


    public static void main(String[] args) {
        String todayArrange = getTodayArrange("000000000000000000011011000000000000000000");
        System.out.println(todayArrange);
    }
}

