// service统一出口
import Request from './request'
import { BASE_URL, TIME_OUT } from './request/config'
import {getToken} from '~/utils/auth'


const TSRequest = new Request({
  baseURL: BASE_URL,
  timeout: TIME_OUT,
  interceptors: {
    requestInterceptor: (config:any) => {
      return config
    },
    requestInterceptorCatch: (err:any) => {
      return err
    },
    responseInterceptor: (res:any) => {
      return res
    },
    responseInterceptorCatch: (err:any) => {
      return err
    }
  }
})

export default TSRequest
