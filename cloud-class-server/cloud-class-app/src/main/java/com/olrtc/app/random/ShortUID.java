package com.olrtc.app.random;

import com.olrtc.common.redis.utils.RedisUtil;

import java.time.Duration;

/**
 * @author njy
 * @since 2023/3/1 10:41
 */
public class ShortUID {

    private static final Seed   seed;
    private static final String uidCacheKey = "uid:";

    static {
        seed = new Seed(36, 3);
    }

    public static String genThreeDigitSeed() {
        return genThreeDigitSeed("",5);
    }


    public static String genThreeDigitSeed(String value, int day) {
        String uid;
        do {
            uid = seed.getNextString();
        } while (RedisUtil.isExistsObject(uidCacheKey + uid));
        RedisUtil.setCacheObject(uidCacheKey + uid, value, Duration.ofDays(day));
        return uid;
    }

    public static String getShortUIDValue(String uid){
        return RedisUtil.getCacheObject(uidCacheKey + uid);
    }


}
