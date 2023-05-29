package com.olrtc.common.mybatis.core.page;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.olrtc.common.core.constants.HttpStatusConst;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


/**
 * 表格分页数据对象
 *
 * @author njy
 * @since 23:26 2023/2/23
 */
@Data
@NoArgsConstructor
public class TableDataInfo<T> implements Serializable {

    /**
     * 总记录数
     */
    private long total;

    /**
     * 列表数据
     */
    private List<T> rows;


    /**
     * 分页
     *
     * @param list  列表数据
     * @param total 总记录数
     */
    public TableDataInfo(List<T> list, long total) {
        this.rows = list;
        this.total = total;
    }

    public static <T> TableDataInfo<T> build(IPage<T> page) {
        TableDataInfo<T> rspData = new TableDataInfo<>();
        rspData.setRows(page.getRecords());
        rspData.setTotal(page.getTotal());
        return rspData;
    }

    public static <T> TableDataInfo<T> build(List<T> list) {
        TableDataInfo<T> rspData = new TableDataInfo<>();
        rspData.setRows(list);
        rspData.setTotal(list.size());
        return rspData;
    }

    public static <T> TableDataInfo<T> build() {
        return new TableDataInfo<>();
    }

}
