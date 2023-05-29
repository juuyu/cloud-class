import {defineStore} from 'pinia'
import piniaStore from '~/store/index'
import {clearToken, setToken} from '~/utils/auth'
import {accountLoginRequest, LoginData, accountLogoutRequest} from '~/api/login/index'
import localCache from '~/utils/cache'

export const useIndexStore = defineStore('index', {
    getters: {},
    actions: {
        // 异步登录并存储token
        async login(loginForm: LoginData) {
            const {data, headers} = await accountLoginRequest(loginForm)
            console.log('登录', data)
            const token = headers?.token
            if (token) {
                setToken(token)
            }
            if (data?.code === 200) {
                localCache.setCache('userInfo', data.data)
            }
            return data
        },
        async logout() {
            const {data} = await accountLogoutRequest()
            console.log('退出', data)
            localCache.clearCache()
            clearToken()
            return data
        },
    },

})

export function useIndexOutsideStore() {
    return useIndexStore(piniaStore)
}
