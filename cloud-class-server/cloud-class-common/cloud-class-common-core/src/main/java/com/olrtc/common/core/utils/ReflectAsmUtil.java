package com.olrtc.common.core.utils;

import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import org.springframework.objenesis.ObjenesisStd;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author njy
 * @since 2023/2/23 13:48
 */
public class ReflectAsmUtil {

    private static final Map<Class<?>, ObjectInstantiator<?>> INSTANTIATORS_CACHE = new ConcurrentHashMap<>();

    public static <T> T newInstance(Class<T> clazz) {
        try {
            ObjectInstantiator<T> instantiator = getInstantiator(clazz);
            return instantiator.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create instance of class " + clazz.getName(), e);
        }
    }

    private static <T> ObjectInstantiator<T> getInstantiator(Class<T> clazz) {
        ObjectInstantiator<?> instantiator = INSTANTIATORS_CACHE.get(clazz);
        if (instantiator == null) {
            instantiator = new ObjenesisStd().getInstantiatorOf(clazz);
            INSTANTIATORS_CACHE.put(clazz, instantiator);
        }
        return (ObjectInstantiator<T>) instantiator;
    }






}
