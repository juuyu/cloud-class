package com.olrtc.app.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 * 枚举转换器
 * @author luffy
 * @since 2022/7/1 上午 10:25
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class EnumConverter implements ConverterFactory<String, Enum<?>> {

    @Override
    public <T extends Enum<?>> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnum<>(targetType);
    }

    private static class StringToEnum<T extends Enum> implements Converter<String, T> {

        private final Class<T> enumType;

        public StringToEnum(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        @Nullable
        public T convert(String source) {
            if (source.isEmpty()) {
                // It's an empty enum identifier: reset the enum value to null.
                return null;
            }
            try {
                //先通过name获取枚举
                return (T) Enum.valueOf(enumType, source);
            } catch (Exception e) {
                Field[] declaredFields = enumType.getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    JsonValue[] annotationsByType = declaredField.getAnnotationsByType(JsonValue.class);
                    if (annotationsByType.length > 0) {
                        String name = declaredField.getName();
                        Object convert = Convert.convert(declaredField.getType(), source);
                        return getEnumObj(enumType, name, convert);
                    }
                }
            }
            return null;
        }

        private T getEnumObj(Class<T> clazz, String fieldName, Object source) {
            T[] enums = clazz.getEnumConstants();
            if (null != enums) {
                for (T e : enums) {
                    Object fieldValue = ReflectUtil.getFieldValue(e, fieldName);
                    if (fieldValue.equals(source)) {
                        return e;
                    }
                }
            }
            return null;
        }
    }
}

