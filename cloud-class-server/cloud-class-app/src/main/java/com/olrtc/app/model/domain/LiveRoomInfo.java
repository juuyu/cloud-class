package com.olrtc.app.model.domain;

import com.olrtc.app.model.dto.CourseDto;
import com.olrtc.app.model.dto.UserDto;
import com.olrtc.app.model.entity.Live;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author njy
 * @since 2023/3/25 16:01
 */
@Data
@Accessors(chain = true)
public class LiveRoomInfo {

    // 房间号
    public String    roomID;
    // 是否开播
    public boolean   live;
    // recordId
    public String    recordId;
    // 开始时间
    public Date      start;
    // 结束时间
    public Date      end;
    // 摄像头开关
    public boolean   camera;
    // 屏幕开关
    public boolean   screen;
    // whiteboardUrl
    public String    whiteboardUrl;
    // live
    public Live      liveInfo;
    // 课程信息
    public CourseDto courseDto;
    // 教师信息
    public UserDto   teacherInfo;

}
