package com.olrtc.app.remote.ai.domain;

import com.olrtc.app.remote.ai.enums.AiModel;
import com.olrtc.common.core.utils.JsonUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author njy
 * @since 2023/3/31 11:16
 */
@Data
@Accessors(chain = true)
public class OpenAiReqBody implements Serializable {

    /**
     * ai model
     */
    private AiModel model;
    /**
     * prompt
     */
    private String  prompt;


    // https://platform.openai.com/tokenizer
    private Integer max_tokens;


    private Double temperature;


    private Integer top_p;


    private Integer n;

    private Boolean stream;

    private Object logprobs;


    private Object stop;

    public static OpenAiReqBody buildBaseOpenAiReqBody(String aiCheckCourseJobReqBody) {
        return JsonUtil.string2Obj(aiCheckCourseJobReqBody, OpenAiReqBody.class);
    }

}
