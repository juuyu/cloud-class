package com.olrtc.common.redis.utils;

import com.olrtc.common.core.utils.SpringUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.redisson.api.RMap;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.HashSet;
import java.util.Set;


/**
 * 缓存操作工具类
 *
 * @author njy
 * @since 14:14 2023/2/3
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings(value = {"unchecked"})
public class CacheUtils {

    private static final CacheManager CACHE_MANAGER = SpringUtil.getBean(CacheManager.class);

    /**
     * 获取缓存组内所有的KEY
     *
     * @param cacheNames 缓存组名称
     */
    public static Set<Object> keys(String cacheNames) {
        RMap<Object, Object> rmap = null;
        Cache cache = CACHE_MANAGER.getCache(cacheNames);
        if (cache != null) {
            rmap = (RMap<Object, Object>) cache.getNativeCache();
        }
        return rmap != null ? rmap.keySet() : new HashSet<>();
    }

    /**
     * 获取缓存值
     *
     * @param cacheNames 缓存组名称
     * @param key        缓存key
     */
    public static <T> T get(String cacheNames, Object key) {
        Cache.ValueWrapper wrapper = null;
        Cache cache = CACHE_MANAGER.getCache(cacheNames);
        if (cache != null){
            wrapper = cache.get(key);
        }
        return wrapper != null ? (T) wrapper.get() : null;
    }

    /**
     * 保存缓存值
     *
     * @param cacheNames 缓存组名称
     * @param key        缓存key
     * @param value      缓存值
     */
    public static void put(String cacheNames, Object key, Object value) {
        Cache cache = CACHE_MANAGER.getCache(cacheNames);
        if (cache != null){
            cache.put(key, value);
        }
    }

    /**
     * 删除缓存值
     *
     * @param cacheNames 缓存组名称
     * @param key        缓存key
     */
    public static void evict(String cacheNames, Object key) {
        Cache cache = CACHE_MANAGER.getCache(cacheNames);
        if (cache != null){
            cache.evict(key);
        }
    }

    /**
     * 清空缓存值
     *
     * @param cacheNames 缓存组名称
     */
    public static void clear(String cacheNames) {
        Cache cache = CACHE_MANAGER.getCache(cacheNames);
        if (cache != null){
            cache.clear();
        }
    }

}
