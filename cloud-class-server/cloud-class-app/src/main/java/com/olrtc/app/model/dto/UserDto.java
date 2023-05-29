package com.olrtc.app.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author njy
 * @since 2023/2/20 13:45
 */
@Data
@Accessors(chain = true)
public class UserDto {
    /**
     *
     */
    private Integer id;

    /**
     * 账号
     */
    private String userName;

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



}
