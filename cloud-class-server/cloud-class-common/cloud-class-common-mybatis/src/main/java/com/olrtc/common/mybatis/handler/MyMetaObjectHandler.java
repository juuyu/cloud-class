package com.olrtc.common.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.olrtc.common.mybatis.mode.entity.BaseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MyBatisPlus插入和删除统一操作
 *
 * @author luffy
 * @since 2022/5/13 下午 04:31
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MyMetaObjectHandler implements MetaObjectHandler {

    private final MetaObjectHandlerUtil metaObjectHandlerUtil;

    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            if (metaObject != null && metaObject.getOriginalObject() instanceof BaseEntity baseEntity) {
                Date current = baseEntity.getCreateTime() != null
                        ? baseEntity.getCreateTime() : new Date();
                baseEntity.setCreateTime(current);
                baseEntity.setUpdateTime(current);

                Integer creatorId = baseEntity.getCreatorId() != null
                        ? baseEntity.getUpdaterId() : metaObjectHandlerUtil.getUserIdOrDefault();
                // 当前已登录 且 创建人为空 则填充
                baseEntity.setCreatorId(creatorId);
                // 当前已登录 且 更新人为空 则填充
                baseEntity.setUpdaterId(creatorId);
            }
        } catch (Exception e) {
            log.error("===自动注入失败===");
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            if (metaObject != null && metaObject.getOriginalObject() instanceof BaseEntity baseEntity) {
                Date current = new Date();
                baseEntity.setUpdateTime(current);

                Integer updaterId = metaObjectHandlerUtil.getUserIdOrDefault();
                // 当前已登录 更新人填充(不管为不为空)
                baseEntity.setUpdaterId(updaterId);
            }
        } catch (Exception e) {
            log.error("===自动注入失败===");
        }
    }


}
