package com.olrtc.app.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.olrtc.app.model.domain.AiCheckCourseJobRes;
import com.olrtc.app.remote.ai.OpenAiProxyService;
import com.olrtc.app.remote.ai.OpenAiService;
import com.olrtc.app.remote.ai.domain.OpenAiReqBody;
import com.olrtc.app.remote.ai.domain.OpenAiRespBody;
import com.olrtc.app.remote.ai.enums.AiModel;
import com.olrtc.common.core.utils.JsonUtil;
import com.olrtc.common.core.utils.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author njy
 * @since 2023/4/3 17:43
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiCheckCourseJob {

    private final        OpenAiProxyService openAiProxyService;
    private final        OpenAiService      openAiService;
    private static final String             PROMPT_FORMAT = "1. 问题是: \"%s\"。2. 回答是: \"%s\"。3. 请给出该回答的评分及评语(满分10分,请严格评分;The return format is{\"score\":9,\"comment\":\"这里是评语\"})";


    private static final String aiCheckCourseJobReqBody = JsonUtil.obj2String(
            new OpenAiReqBody()
                    .setModel(AiModel.DAV_003)
                    .setMax_tokens(500)
                    .setTemperature((double) 0)
                    .setTop_p(1)
                    .setN(1)
                    .setStream(false)
                    .setLogprobs(null)
                    .setStop(null)
    );


    public CompletableFuture<AiCheckCourseJobRes> check(String question, String answer) {
        log.info("check() called with parameters => [question = {}], [answer = {}]", question, answer);
        OpenAiReqBody openAiReqBody = OpenAiReqBody.buildBaseOpenAiReqBody(aiCheckCourseJobReqBody);
        openAiReqBody.setPrompt(String.format(PROMPT_FORMAT, question, answer));
        log.info(JsonUtil.obj2String(openAiReqBody));
        return CompletableFuture.supplyAsync(() -> {
            AiCheckCourseJobRes aiCheckCourseJobRes = new AiCheckCourseJobRes();
            aiCheckCourseJobRes.setSuccess(false);
            try {
                OpenAiRespBody openAiRespBody = openAiService.invokeOpenAiService(openAiReqBody);
//                OpenAiRespBody openAiRespBody = openAiProxyService.invokeOpenAiCompletions(openAiReqBody);
                log.info(JsonUtil.obj2String(openAiRespBody));
                if (openAiRespBody == null) {
                    log.error("连接openAi失败");
                    return aiCheckCourseJobRes;
                }
                List<OpenAiRespBody.Choices> choicesList = openAiRespBody.getChoices();
                if (choicesList.isEmpty()) {
                    log.error("请求openAi失败");
                    return aiCheckCourseJobRes;
                }
                OpenAiRespBody.Choices choices = choicesList.get(0);
                String text = choices.getText();
                String openAiResp = parserOpenAiResp(text);
                if (StrUtil.isNotBlank(openAiResp)) {
                    JsonNode jsonNode = JsonUtil.parserJsonStr(openAiResp);
                    if (jsonNode != null) {
                        JsonNode scoreNode = jsonNode.get("score");
                        JsonNode commentNode = jsonNode.get("comment");
                        if (scoreNode == null || commentNode == null) {
                            log.error("open返回数据不合规");
                            return aiCheckCourseJobRes;
                        }
                        int score = scoreNode.asInt();
                        String comment = commentNode.asText();
                        aiCheckCourseJobRes.setSuccess(true);
                        aiCheckCourseJobRes.setScore(score);
                        aiCheckCourseJobRes.setComment(comment);
                    } else {
                        log.error("openAi返回数据不合规");
                    }
                    return aiCheckCourseJobRes;
                } else {
                    log.error("连接openAi失败");
                }
            } catch (Exception e) {
                log.error("ai评分发送错误", e);
            }
            return aiCheckCourseJobRes;
        });
    }

    private String parserOpenAiResp(String text) {
        int startIndex = text.indexOf("{");
        int endIndex = text.lastIndexOf("}");
        if (startIndex != -1 && endIndex != -1) {
            return text.substring(startIndex, endIndex + 1);
        }
        return "";
    }


    public CompletableFuture<AiCheckCourseJobRes> retry(String question, String answer) {
        return check(question, answer);
    }

}
