package com.olrtc.app.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import com.olrtc.common.mybatis.mode.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @TableName t_user
 */
@TableName(value ="t_user")
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private              Integer id;

    /**
     * 账号
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别, NO(1,”未知”),MAN(2,”男”),WOMAN(3,”女”);
     */
    private Integer sex;

    /**
     * 电话
     */
    private String tel;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 角色类型, ADMIN(1,”系统管理员"),TEA(2,”教师"),STU(3,”学生");
     */
    private Integer userType;

    /**
     *  0:停用, 1:正常
     */
    private Integer status;

    /**
     * 最后一次登录时间
     */
    private Date lastLoginTime;


}