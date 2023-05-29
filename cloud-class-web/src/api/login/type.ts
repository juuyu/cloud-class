export interface ILoginResult {
  msg: string
  code: number
  token: string
  type: number
  records: object
  total: number
  data: any
  ok: boolean
}

export interface IDataType<T = any> {
  data: DataType
  headers:T
}

export  interface  DataType<T=any>{
  code: number
  data: T
  msg: string
}