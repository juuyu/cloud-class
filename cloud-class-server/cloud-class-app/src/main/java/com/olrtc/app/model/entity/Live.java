package com.olrtc.app.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.olrtc.common.mybatis.mode.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @TableName t_live
 */
@TableName(value ="t_live")
@Data
@EqualsAndHashCode(callSuper = true)
public class Live extends BaseEntity {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 房间号
     */
    private String roomId;

    /**
     * 摄像头publish地址
     */
    private String userVideoPublish;

    /**
     * 摄像头live地址
     */
    private String userVideoPlay;

    /**
     * 屏幕publish地址
     */
    private String screenVideoPublish;

    /**
     * 屏幕live地址
     */
    private String screenVideoPlay;

    /**
     * 外键, 课程id
     */
    private Integer courseId;


}