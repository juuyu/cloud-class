package com.olrtc.app.model.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author njy
 * @since 2023/2/16 17:51
 */
@Data
public class RegisterBody {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String  userName;
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String  password;
    /**
     * 邮箱
     */
    @NotBlank(message = "请输入邮箱")
    @Email(message = "格式错误")
    private String  email;


    private String emailCode;


}
