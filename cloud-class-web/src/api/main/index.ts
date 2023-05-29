import TSRequest from '../index'

import {IDataType} from '../types'



export function getData(url: string, queryInfo?: any, header?: any) {
    const headerData = {
        ...header
    }

    return TSRequest.get<IDataType>({
        url: url,
        params: queryInfo,
        headers: headerData,
    })
} //获取列表数据

export function deleteData(url: string, queryInfo?: any) {
    const headerData = {
    }

    return TSRequest.delete<IDataType>({
        url: url,
        params: queryInfo,
        headers: headerData,
    })

} //删除方法

export function postData(url: string, queryInfo: any, header?: any) {
    const headerData = {
        ...header
    }
    return TSRequest.post<IDataType>({
        url: url,
        data: queryInfo,
        headers: headerData,
    })
} //新建用方法
export function putData(url: string, queryInfo: any, header?: any) {
    const headerData = {
        ...header
    }
    return TSRequest.put<IDataType>({
        url: url,
        data: queryInfo,
        headers: headerData,
    })
} //编辑用方法
export function postUploadData(url: string, formData: any, header?: any) {
    const headerData = {
        'Content-Type': 'multipart/form-data',
    }
    return TSRequest.post<IDataType>({
        url: url,
        data: formData,
        headers: headerData,
    })
} //文件上传
export function getDownload(url: string, header?: any) {
    const headerData = {
        responseType: 'blob',
    }
    return TSRequest.get<IDataType>({
        url: url,
        headers: headerData,
        responseType: 'blob',
    })
} //文件下载
export function putUploadData(url: string, formData: any) {
    const headerData = {
        'Content-Type': 'multipart/form-data',
    }
    return TSRequest.put<IDataType>({
        url: url,
        data: formData,
        headers: headerData,
    })
} //文件上传