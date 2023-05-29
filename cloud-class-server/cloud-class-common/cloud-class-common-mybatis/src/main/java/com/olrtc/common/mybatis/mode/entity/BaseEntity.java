package com.olrtc.common.mybatis.mode.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * entity 基类
 *
 * @author njy
 * @since 2023/2/22 13:41
 */
@Data
public class BaseEntity implements Serializable {
    /**
     * 创建人id
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer creatorId;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date    createTime;
    /**
     * 更新人id
     */
    @TableField(fill = FieldFill.UPDATE)
    private Integer updaterId;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private Date    updateTime;
    /**
     * 逻辑删除标志，1：已删除，0：未删除
     */
    @TableLogic
    private Integer deleted;
}

