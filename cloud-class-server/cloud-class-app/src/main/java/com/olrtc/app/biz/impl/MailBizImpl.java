package com.olrtc.app.biz.impl;

import com.olrtc.app.biz.MailBiz;
import com.olrtc.app.config.RedisCacheKey;
import com.olrtc.app.model.domain.EmailMsg;
import com.olrtc.app.model.entity.User;
import com.olrtc.app.random.AuthCode;
import com.olrtc.app.service.UserService;
import com.olrtc.app.utils.MailUtil;
import com.olrtc.app.utils.MessageUtil;
import com.olrtc.common.core.exception.BizException;
import com.olrtc.common.redis.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author njy
 * @since 2023/4/27 16:32
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailBizImpl implements MailBiz {

    private final UserService userService;

    @Override
    public boolean sendRegisterCode(String mail) {
        boolean email = MailUtil.isEmail(mail);
        if (!email) {
            throw new BizException("email格式不合法");
        }
        User user = userService.getUserInfoByEmail(mail);
        if (user != null) {
            throw new BizException("该邮箱已被注册");
        }
        String code = AuthCode.genEmailAuthCode(mail);
        String mailAuthMessage = MessageUtil.getMailAuthMessage(code);
        EmailMsg emailMsg = new EmailMsg(new String[]{mail}, "邮箱验证码", mailAuthMessage);
        return MailUtil.htmlEmail(emailMsg);
    }

    @Override
    public boolean checkEmailCode(String email, String emailCode) {
        String key = RedisCacheKey.AUTH.MAIL_AUTH_CODE + emailCode;
        String mail = RedisUtil.getCacheObject(key);
        if (mail != null && mail.equals(email)){
            RedisUtil.deleteObject(key);
            return true;
        }
        return false;
    }
}
