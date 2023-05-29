package com.olrtc.common.core.convert;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.olrtc.common.core.utils.StrUtil;

import java.lang.reflect.Field;

/**
 * ReflectASMBeanCopier bean深拷贝, 基于ReflectASM实现
 *
 * @author njy
 * @since 2023/2/23 16:26
 */
public class ReflectASMBeanCopier {

    private static final String       GET = "get";
    private static final String       SET = "set";
    private final        MethodAccess sourceAccess;
    private final        MethodAccess targetAccess;
    private final        Field[]      sourceFields;
    private final        Field[]      targetFields;
    private final        int[]        getFieldIndexes;
    private final        int[]        setFieldIndexes;

    public ReflectASMBeanCopier(Class<?> sourceClass, Class<?> targetClass) {
        this.sourceAccess = MethodAccess.get(sourceClass);
        this.targetAccess = MethodAccess.get(targetClass);
        this.sourceFields = getAllFields(sourceClass);
        this.targetFields = getAllFields(targetClass);
        this.getFieldIndexes = new int[sourceFields.length];
        this.setFieldIndexes = new int[targetFields.length];
        initIndexes();
    }

    private void initIndexes() {
        for (int i = 0; i < sourceFields.length; i++) {
            Field field = sourceFields[i];
            getFieldIndexes[i] = sourceAccess.getIndex(GET + StrUtil.capitalize(field.getName()));
        }
        for (int i = 0; i < targetFields.length; i++) {
            Field field = targetFields[i];
            setFieldIndexes[i] = targetAccess.getIndex(SET + StrUtil.capitalize(field.getName()), field.getType());
        }
    }


    private static Field[] getAllFields(Class<?> clazz) {
        return clazz.getDeclaredFields();
    }

    public void copy(Object source, Object target) {
        for (int i = 0; i < sourceFields.length; i++) {
            try {
                Object value = sourceAccess.invoke(source, getFieldIndexes[i]);
                if (value != null) {
                    targetAccess.invoke(target, setFieldIndexes[i], value);
                }
            } catch (Exception e) {
                // 可能会没有对应字段, 忽略
            }
        }
    }


}


