import TSRequest from '../index'
import {IDataType} from './type'
import LocalCache from '~/utils/cache'
import {getToken} from '~/utils/auth'
import router from "~/router";
enum LoginAPI {
  AccountLogin = '/login',
  AccountLogout = '/logout',

}
interface LoginRes {
  token: string
}
export interface LoginData {
  username: string
  password: string
}
function errorData(){
  LocalCache.clearCache()
  router.push('/login')
}

//登录
export function accountLoginRequest(account: LoginData) {
  return TSRequest.post<IDataType>({
    url: LoginAPI.AccountLogin,
    data: account,
  }) 
}



//登出
export function accountLogoutRequest() {
  const token = getToken()
  if(!token){
    console.log('当前账号不在线')
    errorData()
    return {data:{code:'ERROR',data:null,msg:'当前账号不在线'}};
  }
  const headerData = {
    token: token,
  }
  return TSRequest.post<IDataType>({
    url: LoginAPI.AccountLogout,
    headers: headerData,
  })
}
