import type { AxiosRequestConfig, AxiosResponse,AxiosError } from 'axios'

export interface TSRequestInterceptors<T = AxiosResponse> {
  requestInterceptor?: (config: AxiosRequestConfig) => AxiosRequestConfig
  requestInterceptorCatch?: (error: any) => any
  responseInterceptor?: (res: T) => T
  responseInterceptorCatch?: (error: AxiosError) => AxiosError
}

export interface TSRequestConfig<T = AxiosResponse> extends AxiosRequestConfig {
  interceptors?: TSRequestInterceptors<T>
  showLoading?: boolean
}
