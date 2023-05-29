package com.olrtc.app.web;

import com.olrtc.app.biz.MailBiz;
import com.olrtc.common.core.domain.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author njy
 * @since 2023/4/27 16:18
 */
@Slf4j
@RestController
@RequestMapping("mail")
@RequiredArgsConstructor
public class MailController {

    private final MailBiz mailBiz;

    /**
     * 发送邮箱验证码
     *
     * @param mail mail
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     */
    @GetMapping("registerCode")
    public R<Void> sendRegisterCode(String mail) {
        boolean f = mailBiz.sendRegisterCode(mail);
        return f ? R.ok("ok") : R.fail("发送失败");
    }


}
