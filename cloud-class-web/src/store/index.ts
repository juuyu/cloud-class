import { createPinia } from 'pinia'
import { useIndexStore } from './modules/login'
import {useCommonStore} from './modules/common'

const pinia = createPinia()



export {
  useIndexStore,

  useCommonStore,
}
export default pinia
