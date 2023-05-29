package com.olrtc.app.biz;

import com.olrtc.app.model.dto.RecordDto;
import com.olrtc.app.model.param.RecordQueryParam;
import com.olrtc.app.model.param.RecordUpdateParam;
import com.olrtc.common.mybatis.core.page.PageQuery;
import com.olrtc.common.mybatis.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author njy
 * @since 2023/3/13 16:17
 */
public interface RecordBiz {

    /**
     * 上传切片
     *
     * @param slice slice
     */
    void uploadSliceFile(String recordId, MultipartFile slice);


    /**
     * 开始录制,返回录制id
     *
     * @param userId
     * @return java.lang.String
     * @author njy
     * @since 11:37 2023/4/19
     */
    String startRecord(Integer userId);

    /**
     * 结束录制
     *
     * @param recordId
     * @return void
     * @author njy
     * @since 13:42 2023/4/19
     */
    void stopRecord(String recordId);


    /**
     * 分页查询用户录制视频信息
     *
     * @param pageQuery
     * @param param
     * @return com.olrtc.common.mybatis.core.page.TableDataInfo<com.olrtc.app.model.dto.RecordDto>
     * @author njy
     * @since 17:46 2023/4/21
     */
    TableDataInfo<RecordDto> listUserRecord(PageQuery pageQuery, RecordQueryParam param);


    /**
     * 分页查询公开的录制视频
     *
     * @param pageQuery
     * @param param
     * @return com.olrtc.common.mybatis.core.page.TableDataInfo<com.olrtc.app.model.dto.RecordDto>
     */
    TableDataInfo<RecordDto> listOpenRecord(PageQuery pageQuery, RecordQueryParam param);

    /**
     * 查看回放是否已经生成
     *
     * @param videoId
     * @return boolean
     * @author njy
     * @since 17:55 2023/4/21
     */
    boolean recordIsGen(Integer videoId);

    /**
     * 删除录制视频
     *
     * @param id id
     */
    void delRecord(Integer id);

    /**
     * 更新录制视频信息
     *
     * @param recordUpdateParam recordUpdateParam
     * @return boolean
     */
    boolean updateRecord(RecordUpdateParam recordUpdateParam);

    /**
     * 更新录制视频封面
     */
    void uploadRecordCover(String recordId, MultipartFile cover);
}
