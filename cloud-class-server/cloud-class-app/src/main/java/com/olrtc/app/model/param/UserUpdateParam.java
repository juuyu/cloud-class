package com.olrtc.app.model.param;

import lombok.Data;

/**
 * @author njy
 * @since 2023/4/27 16:22
 */
@Data
public class UserUpdateParam {
    private Integer userId;

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

}
