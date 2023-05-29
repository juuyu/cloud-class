package com.olrtc.app.random;

import com.olrtc.app.config.RedisCacheKey;
import com.olrtc.common.redis.utils.RedisUtil;

import java.time.Duration;
import java.util.Random;

/**
 * @author njy
 * @since 2023/4/28 10:08
 */
public class AuthCode {


    public static String genSixDigitCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        return String.valueOf(randomNumber);
    }


    public static String genEmailAuthCode(String email) {
        String key;
        String code;
        boolean hasKey;

        do {
            code = genSixDigitCode();
            key = RedisCacheKey.AUTH.MAIL_AUTH_CODE + code;
            hasKey = RedisUtil.hasKey(key);
        } while (hasKey);

        RedisUtil.setCacheObject(key, email, Duration.ofMinutes(6));
        return code;
    }


}
