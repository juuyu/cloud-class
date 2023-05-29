package com.olrtc.app.remote.ai.domain;

import com.olrtc.app.remote.ai.enums.AiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author njy
 * @since 2023/3/31 11:23
 */
@Data
@Accessors(chain = true)
public class OpenAiRespBody {
    private String        id;
    private String        object;
    private long          created;
    private AiModel       model;
    private List<Choices> choices;
    private Usage         usage;


    @Data
    public static class Choices {
        private String  text;
        private Integer index;
        private Object  logprobs;
        private String  finish_reason;

    }

    @Data
    public static class Usage {
        private Integer prompt_tokens;
        private Integer completion_tokens;
        private Integer total_tokens;

    }

}

