import axios from 'axios'
import type {AxiosInstance} from 'axios'
import type {TSRequestInterceptors, TSRequestConfig} from './type'
import router from '~/router'
import LocalCache from '~/utils/cache'
import {ElMessage} from "element-plus";
import {getToken} from "~/utils/auth";
const DEAFULT_LOADING = true


class TSRequest {
    instance: AxiosInstance
    interceptors?: TSRequestInterceptors
    showLoading: boolean

    constructor(config: TSRequestConfig) {
        // 创建axios实例
        this.instance = axios.create(config)

        // 保存基本信息
        this.showLoading = config.showLoading ?? DEAFULT_LOADING
        this.interceptors = config.interceptors

        // 使用拦截器
        // 1.从config中取出的拦截器是对应的实例的拦截器
        // this.instance.interceptors.request.use(
        //   this.interceptors?.requestInterceptor,
        //   this.interceptors?.requestInterceptorCatch,
        // )
        // this.instance.interceptors.response.use(
        //   this.interceptors?.responseInterceptor,
        //   this.interceptors?.responseInterceptorCatch,
        // )

        // 2.添加所有的实例都有的拦截器
        //发送请求时
        this.instance.interceptors.request.use(
            (config) => {
                if (this.showLoading) {
                    // if ("/login" !== config?.url) {
                    //     // 携带token的拦截
                    //     const token = getToken();
                    //     if (!token) {
                    //         LocalCache.clearCache()
                    //         ElMessage.error("登录信息已过期, 请先登录")
                    //         router.push('/')
                    //     }else {
                    //         config.headers.token = `${token}`;
                    //     }
                    // }
                }
                return config
            },
            (error) => {
                console.log('发送请求拦截失败')
                return error
            },
        )
        //接受请求时
        this.instance.interceptors.response.use(
            res => {
                // 将loading移除
                if (res.toString().indexOf('401') >= 0 || res.data.code === 401) {
                    ElMessage({
                        message: 'Warning, this is a warning message.',
                        type: 'warning',
                    })
                    LocalCache.clearCache()
                    router.push('/login')
                }
                return res
            },
            error => {
                // 将loading移除
                console.log('接受请求拦截失败', error, error.code)
                // 例子: 判断不同的HttpErrorCode显示不同的错误信息
                if (error.response?.status === 401) {
                    LocalCache.clearCache()
                    router.push('/login')
                }
                if (error.code === 'ECONNABORTED' || error.message === "Network Error" || error.message.includes("timeout")) {
                    return {data: {code: 'ERROR', data: null, msg: '请求超时，请稍后重试'}};
                }
                return {data: {code: 'ERROR', data: null, msg: error.response.data.msg}};
            }
        )
    }

    request<T = any>(config: TSRequestConfig<T>): Promise<T> {
        return new Promise((resolve, reject) => {
            // 1.单个请求对请求config的处理
            if (config.interceptors?.requestInterceptor) {
                config = config.interceptors.requestInterceptor(config)
            }

            // 2.判断是否需要显示loading
            if (config.showLoading === false) {
                this.showLoading = config.showLoading
            }

            this.instance
                .request<any, T>(config)
                .then((res) => {
                    // 1.单个请求对数据的处理
                    if (config.interceptors?.responseInterceptor) {
                        res = config.interceptors.responseInterceptor(res)
                    }
                    // 2.将showLoading设置true, 这样不会影响下一个请求
                    this.showLoading = DEAFULT_LOADING

                    // 3.将结果resolve返回出去
                    resolve(res)
                })
                .catch((err) => {
                    // 将showLoading设置true, 这样不会影响下一个请求
                    this.showLoading = DEAFULT_LOADING
                    reject(err)
                    return err
                })
        })
    }

    get<T = any>(config: TSRequestConfig<T>): Promise<T> {
        return this.request<T>({...config, method: 'GET'})
    }

    post<T = any>(config: TSRequestConfig<T>): Promise<T> {
        return this.request<T>({...config, method: 'POST'})
    }

    delete<T = any>(config: TSRequestConfig<T>): Promise<T> {
        return this.request<T>({...config, method: 'DELETE'})
    }

    patch<T = any>(config: TSRequestConfig<T>): Promise<T> {
        return this.request<T>({...config, method: 'PATCH'})
    }

    put<T = any>(config: TSRequestConfig<T>): Promise<T> {
        return this.request<T>({...config, method: 'PUT'})
    }
}

export default TSRequest
