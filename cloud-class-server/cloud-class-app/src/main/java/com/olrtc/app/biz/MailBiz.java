package com.olrtc.app.biz;

/**
 * @author njy
 * @since 2023/4/27 16:32
 */
public interface MailBiz {

    /**
     * 发送邮箱验证码
     *
     * @param mail mail
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     */
    boolean sendRegisterCode(String mail);

    /**
     * 校验邮箱验证码
     *
     * @param email     email
     * @param emailCode emailCode
     * @return boolean
     */
    boolean checkEmailCode(String email, String emailCode);
}
