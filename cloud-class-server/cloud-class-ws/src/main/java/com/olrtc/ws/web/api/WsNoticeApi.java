package com.olrtc.ws.web.api;

import com.olrtc.common.core.domain.R;
import com.olrtc.ws.biz.NoticeApiBiz;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author njy
 * @since 2023/3/27 13:12
 */
@Slf4j
@RestController
@RequestMapping("api/notice")
@RequiredArgsConstructor
public class WsNoticeApi {

    private final NoticeApiBiz noticeApiBiz;

    /**
     * 通知某人退出
     *
     * @param userName userName
     * @return com.olrtc.common.core.domain.R<java.lang.Boolean>
     * @author njy
     * @since 13:26 2023/3/27
     */
    @GetMapping("kick/{userName}")
    public R<Boolean> noticeUserExit(@PathVariable("userName") String userName) {
        log.info("NoticeUserExit() called with parameters => [userName = {}]",userName);
        noticeApiBiz.noticeUserExit(userName);
        return R.ok("ok", true);
    }


}
