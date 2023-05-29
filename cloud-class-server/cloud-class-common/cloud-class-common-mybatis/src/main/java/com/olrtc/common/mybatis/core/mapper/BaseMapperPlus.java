package com.olrtc.common.mybatis.core.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.olrtc.common.core.utils.BeanCopyUtil;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 自定义 Mapper 接口, 实现 自定义扩展
 *
 * @param <M> mapper 泛型
 * @param <T> table 泛型
 * @param <V> Dto 泛型
 * @author Lion Li
 * @since 2021-05-13
 */
@SuppressWarnings("unchecked")
public interface BaseMapperPlus<M, T, V> extends BaseMapper<T> {

    Log log = LogFactory.getLog(BaseMapperPlus.class);

    default Class<V> currentDtoClass() {
        return (Class<V>) ReflectionKit.getSuperClassGenericType(this.getClass(), BaseMapperPlus.class, 2);
    }

    default Class<T> currentModelClass() {
        return (Class<T>) ReflectionKit.getSuperClassGenericType(this.getClass(), BaseMapperPlus.class, 1);
    }

    default Class<M> currentMapperClass() {
        return (Class<M>) ReflectionKit.getSuperClassGenericType(this.getClass(), BaseMapperPlus.class, 0);
    }

    default List<T> selectList() {
        return this.selectList(new QueryWrapper<>());
    }

    /**
     * 批量插入
     */
    default boolean insertBatch(Collection<T> entityList) {
        return Db.saveBatch(entityList);
    }

    /**
     * 批量更新
     */
    default boolean updateBatchById(Collection<T> entityList) {
        return Db.updateBatchById(entityList);
    }

    /**
     * 批量插入或更新
     */
    default boolean insertOrUpdateBatch(Collection<T> entityList) {
        return Db.saveOrUpdateBatch(entityList);
    }

    /**
     * 批量插入(包含限制条数)
     */
    default boolean insertBatch(Collection<T> entityList, int batchSize) {
        return Db.saveBatch(entityList, batchSize);
    }

    /**
     * 批量更新(包含限制条数)
     */
    default boolean updateBatchById(Collection<T> entityList, int batchSize) {
        return Db.updateBatchById(entityList, batchSize);
    }

    /**
     * 批量插入或更新(包含限制条数)
     */
    default boolean insertOrUpdateBatch(Collection<T> entityList, int batchSize) {
        return Db.saveOrUpdateBatch(entityList, batchSize);
    }

    /**
     * 插入或更新(包含限制条数)
     */
    default boolean insertOrUpdate(T entity) {
        return Db.saveOrUpdate(entity);
    }

    default V selectDtoById(Serializable id) {
        return selectDtoById(id, this.currentDtoClass());
    }

    /**
     * 根据 ID 查询
     */
    default <C> C selectDtoById(Serializable id, Class<C> DtoClass) {
        T obj = this.selectById(id);
        if (obj == null) {
            return null;
        }
        return BeanCopyUtil.copy(obj, DtoClass);
    }

    default List<V> selectDtoBatchIds(Collection<? extends Serializable> idList) {
        return selectVoBatchIds(idList, this.currentDtoClass());
    }

    /**
     * 查询（根据ID 批量查询）
     */
    default <C> List<C> selectVoBatchIds(Collection<? extends Serializable> idList, Class<C> dtoClass) {
        List<T> list = this.selectBatchIds(idList);
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        return BeanCopyUtil.copyList(list, dtoClass);
    }

    default List<V> selectDtoByMap(Map<String, Object> map) {
        return selectDtoByMap(map, this.currentDtoClass());
    }

    /**
     * 查询（根据 columnMap 条件）
     */
    default <C> List<C> selectDtoByMap(Map<String, Object> map, Class<C> dtoClass) {
        List<T> list = this.selectByMap(map);
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        return BeanCopyUtil.copyList(list, dtoClass);
    }

    default V selectDtoOne(Wrapper<T> wrapper) {
        return selectDtoOne(wrapper, this.currentDtoClass());
    }

    /**
     * 根据 entity 条件，查询一条记录
     */
    default <C> C selectDtoOne(Wrapper<T> wrapper, Class<C> dtoClass) {
        T obj = this.selectOne(wrapper);
        if (obj == null) {
            return null;
        }
        return BeanCopyUtil.copy(obj, dtoClass);
    }

    default List<V> selectDtoList(Wrapper<T> wrapper) {
        return selectDtoList(wrapper, this.currentDtoClass());
    }

    /**
     * 根据 entity 条件，查询全部记录
     */
    default <C> List<C> selectDtoList(Wrapper<T> wrapper, Class<C> dtoClass) {
        List<T> list = this.selectList(wrapper);
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        return BeanCopyUtil.copyList(list, dtoClass);
    }

    default <P extends IPage<V>> P selectDtoPage(IPage<T> page, Wrapper<T> wrapper) {
        return selectDtoPage(page, wrapper, this.currentDtoClass());
    }

    /**
     * 分页查询DTO
     */
    default <C, P extends IPage<C>> P selectDtoPage(IPage<T> page, Wrapper<T> wrapper, Class<C> dtoClass) {
        IPage<T> pageData = this.selectPage(page, wrapper);
        IPage<C> dtoPage = new Page<>(pageData.getCurrent(), pageData.getSize(), pageData.getTotal());
        List<T> records = pageData.getRecords();
        if (records == null || records.isEmpty()) {
            return (P) dtoPage;
        }
        dtoPage.setRecords(BeanCopyUtil.copyList(pageData.getRecords(), dtoClass));
        return (P) dtoPage;
    }

}
