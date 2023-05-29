package com.olrtc.common.core.utils;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.olrtc.common.core.convert.ReflectASMBeanCopier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.beans.BeanMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 基于 ReflectAsm 的bean深拷贝工具(性能高)
 *
 * @author njy
 * @since 2023/2/23 17:36
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanCopyUtil {


    /**
     * 单对象基于class创建拷贝
     *
     * @param source 数据来源实体
     * @param desc   描述对象 转换后的对象
     * @return desc
     */
    public static <T, V> V copy(T source, Class<V> desc) {
        if (source == null) {
            return null;
        }
        if (desc == null) {
            return null;
        }
        final V target = ConstructorAccess.get(desc).newInstance();
        return copy(source, target);
    }

    /**
     * 单对象基于对象创建拷贝
     *
     * @param source 数据来源实体
     * @param desc   转换后的对象
     * @return desc
     */
    public static <T, V> V copy(T source, V desc) {
        if (source == null) {
            return null;
        }
        if (desc == null) {
            return null;
        }

        /*
        基于cglib的bean深拷贝工具(已弃用)
        在Java 9以上使用, 启动参数添加[--add-opens java.base/java.lang=ALL-UNNAMED]

        BeanCopier beanCopier = CglibBeanCopy.BeanCopierCache.INSTANCE.get(source.getClass(), desc.getClass(), null);
        beanCopier.copy(source, desc, null);
         */
        ReflectASMBeanCopier reflectASMBeanCopier = ReflectASMBeanCopierCache.INSTANCE.get(source.getClass(), desc.getClass());
        reflectASMBeanCopier.copy(source, desc);
        return desc;
    }

    /**
     * 列表对象基于class创建拷贝
     *
     * @param sourceList 数据来源实体列表
     * @param desc       描述对象 转换后的对象
     * @return desc
     */
    public static <T, V> List<V> copyList(List<T> sourceList, Class<V> desc) {
        if (sourceList == null) {
            return null;
        }
        if (sourceList.isEmpty()) {
            return new ArrayList<>();
        }
        return sourceList.stream()
                .map(source -> {
                    V target = ConstructorAccess.get(desc).newInstance();
                    copy(source, target);
                    return target;
                })
                .collect(Collectors.toList());
    }

    /**
     * bean拷贝到map
     *
     * @param bean 数据来源实体
     * @return map对象
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, Object> copyToMap(T bean) {
        if (bean == null) {
            return null;
        }
        return BeanMap.create(bean);
    }

    /**
     * map拷贝到bean
     *
     * @param map       数据来源
     * @param beanClass bean类
     * @return bean对象
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanClass) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        if (beanClass == null) {
            return null;
        }
        T bean = ConstructorAccess.get(beanClass).newInstance();
        return mapToBean(map, bean);
    }

    /**
     * map拷贝到bean
     *
     * @param map  数据来源
     * @param bean bean对象
     * @return bean对象
     */
    public static <T> T mapToBean(Map<String, Object> map, T bean) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        if (bean == null) {
            return null;
        }
        BeanMap.create(bean).putAll(map);
        return bean;
    }

    /**
     * map拷贝到map
     *
     * @param map   数据来源
     * @param clazz 返回的对象类型
     * @return map对象
     */
    public static <T, V> Map<String, V> mapToMap(Map<String, T> map, Class<V> clazz) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        if (clazz == null) {
            return null;
        }
        Map<String, V> copyMap = new LinkedHashMap<>(map.size());
        map.forEach((k, v) -> copyMap.put(k, copy(v, clazz)));
        return copyMap;
    }


    /**
     * ReflectASMBeanCopier属性缓存<br>
     */
    public enum ReflectASMBeanCopierCache {
        /**
         * BeanCopier属性缓存单例
         */
        INSTANCE;

        private static final Map<String, ReflectASMBeanCopier> cache = new ConcurrentHashMap<>();

        public ReflectASMBeanCopier get(Class<?> srcClass, Class<?> targetClass) {
            String key = genKey(srcClass, targetClass);
            ReflectASMBeanCopier beanCopier;
            if (cache.containsKey(key)) {
                beanCopier = cache.get(key);
            } else {
                beanCopier = new ReflectASMBeanCopier(srcClass, targetClass);
                cache.put(key, beanCopier);
            }
            return beanCopier;
        }


        /**
         * 获得类与转换器生成的key
         *
         * @param srcClass    源Bean的类
         * @param targetClass 目标Bean的类
         * @return 属性名和Map映射的key
         */
        private String genKey(Class<?> srcClass, Class<?> targetClass) {
            return srcClass.getName() + "-" + targetClass.getName();
        }
    }

}
