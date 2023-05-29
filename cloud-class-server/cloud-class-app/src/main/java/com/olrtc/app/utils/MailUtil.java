package com.olrtc.app.utils;


import com.olrtc.app.model.domain.EmailMsg;
import com.olrtc.common.core.utils.JsonUtil;
import com.olrtc.common.core.utils.SpringUtil;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * @author njy
 * @since 2023/4/27 16:34
 */
@Slf4j
public class MailUtil {

    private static final JavaMailSender mailSender     = SpringUtil.getBean(JavaMailSender.class);
    private static final MailProperties mailProperties = SpringUtil.getBean(MailProperties.class);


    /**
     * 判断email格式是否正确
     *
     * @param email email
     */
    public static boolean isEmail(String email) {
        String regEx = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";
        return email.matches(regEx);
    }

    /**
     * 发送简单邮件
     *
     * @param emailMsg emailMsg
     */
    public static boolean commonEmail(EmailMsg emailMsg) {
        log.info("commonEmail() called with parameters => [emailMsg = {}]", JsonUtil.obj2String(emailMsg));
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailProperties.getUsername());
            message.setTo(emailMsg.getTos());
            message.setSubject(emailMsg.getSubject());
            message.setText(emailMsg.getContent());
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            log.error("发送邮件失败", e);
        }
        return false;
    }

    /**
     * 发送html邮件
     *
     * @param emailMsg emailMsg
     */
    public static boolean htmlEmail(EmailMsg emailMsg) {
        log.info("htmlEmail() called with parameters => [emailMsg = {}]", JsonUtil.obj2String(emailMsg));
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper minehelper = null;
            minehelper = new MimeMessageHelper(message, true);
            minehelper.setFrom(mailProperties.getUsername());
            minehelper.setTo(emailMsg.getTos());
            minehelper.setSubject(emailMsg.getSubject());
            minehelper.setText(emailMsg.getContent(), true);
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            log.error("发送邮件失败", e);
        }
        return false;
    }


}
