package com.olrtc.common.security.domain;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 用户信息
 *
 * @author ruoyi
 */
@Data
@Accessors(chain = true)
public class LoginUser implements Serializable {

    private Integer id;
    /**
     * 账号
     */
    private String  userName;
    /**
     * 密码
     */
    private String  password;
    /**
     * 头像
     */
    private String  avatar;
    /**
     * 真实姓名
     */
    private String  realName;
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
    private String  tel;
    /**
     * 邮箱
     */
    private String   email;
    /**
     * 角色类型, ADMIN(1,”系统管理员"),TEA(2,”教师"),STU(3,”学生");
     */
    private Integer userType;

}
