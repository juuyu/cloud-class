function generateCourseSchedule(courseArray: number[][]): string {
    const schedule: number[] = Array(42).fill(0); // 初始化一个长度为42的数组，初始值都为0

    courseArray.forEach(([day, lesson]) => {
        const index = (day - 1) * 6 + lesson - 1; // 根据星期几和第几节课计算出在数组中的下标
        schedule[index] = 1; // 将该位置上的值设置为1，表示有课
    });

    return schedule.join(''); // 将数组转换为字符串并返回
}


function parseCourseSchedule(scheduleString: string): number[][] {
    const schedule: number[][] = [];
    try {
        for (let i = 0; i < scheduleString.length; i++) {
            if (scheduleString[i] === '1') {
                const day = Math.floor(i / 6) + 1; // 根据下标计算出星期几
                const lesson = (i % 6) + 1; // 根据下标计算出第几节课
                schedule.push([day, lesson]);
            }
        }
        return schedule;
    } catch (e) {
        return []
    }
}

function formatCourseSchedule(scheduleString: string): string {
    //   const schedule: number[][] = parseCourseSchedule(scheduleString); // 先使用上一个函数将字符串解析为二维数组

    const dayOfWeek: string[] = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
    const result: string[] = [];

    try {
        for (let day = 1; day <= 7; day++) {
            const lessons: number[] = [];
            for (let lesson = 1; lesson <= 6; lesson++) {
                const index = (day - 1) * 6 + lesson - 1; // 根据星期几和第几节课计算出在数组中的下标
                if (scheduleString[index] === '1') {
                    lessons.push(lesson);
                }
            }
            if (lessons.length > 0) {
                if (lessons.length === 6) {
                    result.push(dayOfWeek[day - 1] + '全天');
                } else {
                    const lessonStr = lessons.sort((a, b) => a - b).join('、');
                    result.push(dayOfWeek[day - 1] + '第' + lessonStr + '节');
                }
            }
        }
        return result.join(',  ');
    } catch (e) {
        return scheduleString
    }
}


export {generateCourseSchedule, parseCourseSchedule, formatCourseSchedule}