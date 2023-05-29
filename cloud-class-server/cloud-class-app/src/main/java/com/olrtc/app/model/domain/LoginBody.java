package com.olrtc.app.model.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * @author njy
 * @since 2023/2/16 17:17
 */
@Data
public class LoginBody {

    /**
     * 用户名
     */
//    @NotBlank(message = "用户名不能为空")
//    @Length(min = 6, max = 15, message = "用户名长度为6～15位")
    private String username;

    /**
     * 用户密码
     */
//    @NotBlank(message = "密码不能为空")
//    @Length(min = 6, max = 20, message = "密码长度为6～20位")
    private String password;

}

