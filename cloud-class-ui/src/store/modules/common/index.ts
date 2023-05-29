import {defineStore} from 'pinia'
import piniaStore from '~/store/index'
import {
    getData,
    postData,
    putData,
    deleteData,
    postUploadData,
} from '~/api/main/index'
import {CommonState} from './types';

export const useCommonStore = defineStore('common', {
    state: (): CommonState => ({
        screenStreamList: null
    }),
    getters: {
        pageListData(state) {
            return (pageName: string) => {
                return (state as any)[`${pageName}Data`]
            }
        },

    },
    actions: {
        async getPageListAction(payload: any) {
            const pageName = payload.pageName
            let pageUrl = ''
            switch (pageName) {

            }
            let {data} = await getData(pageUrl, payload.queryInfo)
            switch (pageName) {

            }
            return data
        }, //获取数据方法
        async getPageListSearch(payload: any) {
            const pageName = payload.pageName

            let pageUrl = ''
            switch (pageName) {

            }
            const {data} = await getData(pageUrl, payload.queryInfo)
            return data
        }, //获取数据方法
        async postCreateData(payload: any) {
            const pageName = payload.pageName

            let pageUrl = ''
            switch (pageName) {
            }
            console.log('新建', payload.queryInfo)
            const {data} = await postData(pageUrl, payload.queryInfo)
            return data
        }, //新建数据方法
        async postEditData(payload: any) {
            const pageName = payload.pageName
            let pageUrl = ''
            switch (pageName) {
                //发送聊天内容
                case 'chatSend':
                    pageUrl = 'ws/send'
                    break;
            }
            console.log('编辑', payload.queryInfo)
            const {data} = await postData(pageUrl, payload.queryInfo)
            return data
        }, //编辑数据方法
        async putEditData(payload: any) {
            const pageName = payload.pageName
            let pageUrl = ''
            switch (pageName) {

            }
            console.log('编辑', payload.queryInfo)
            const {data} = await putData(pageUrl, payload.queryInfo)
            return data
        }, //put编辑数据方法
        async deleteClearedData(payload: any) {
            const pageName = payload.pageName
            const id = payload.id
            let pageUrl = ''
            switch (pageName) {
            }
            const {data} = await deleteData(pageUrl)
            return data
        }, //删除方法
        async postUploadData(payload: any) {
            const pageName = payload.pageName
            const id = payload.id
            let pageUrl = ''
            switch (pageName) {

            }
            const {data} = await postUploadData(pageUrl, payload.formData)
            return data
        }, //上传方法
    },
})


