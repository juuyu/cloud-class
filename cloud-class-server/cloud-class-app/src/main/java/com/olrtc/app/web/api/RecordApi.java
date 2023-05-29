package com.olrtc.app.web.api;

import com.olrtc.app.biz.RecordBiz;
import com.olrtc.common.core.domain.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author njy
 * @since 2023/4/21 10:00
 */
@RestController
@RequestMapping("api/record")
@RequiredArgsConstructor
public class RecordApi {

    private final RecordBiz recordBiz;


    @GetMapping("gen")
    public R<Void> recordGen(){
        return null;
    }

}
