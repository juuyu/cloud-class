package com.olrtc.app.web;

import com.olrtc.app.random.ShortUID;
import com.olrtc.common.core.utils.StrUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author njy
 * @since 2023/4/2 00:57
 */
@Slf4j
@Controller
public class ShortLinkController {


    @RequestMapping(value = "s/{shortId}", method = RequestMethod.GET)
    public void redirect(@PathVariable("shortId") String shortId,
                         HttpServletResponse response) {
        log.info("redirect() called with parameters => [shortId = {}], [response = {}]",shortId, response);
        String trueUrl = ShortUID.getShortUIDValue(shortId);
        log.info(trueUrl);
        if (StrUtil.isBlank(trueUrl)) {
            return;
        }
        try {
            response.sendRedirect(trueUrl);
        } catch (IOException e) {
        }
    }

}
