import {ref} from 'vue'
import localCache from "~/utils/cache";

const weatherInfo = ref('今日晴，0℃ - 10℃，天气寒冷，注意添加衣物。')
const amapKey = 'your amapKey'


function getWeatherInfo(): any {
    getWeather().then(r => {
        console.log(r)
    })
    return weatherInfo
}

async function getCityCode(): Promise<string | undefined> {
    // const url = 'https://restapi.amap.com/v3/ip?key=' + amapKey
    const url = '/amap/ip?key=' + amapKey
    const response = await fetch(url, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    })
    const res = await response.json()
    if (res?.status === '1') {
        return res.adcode
    }
    return undefined
}

async function getWeather() {
    const weatherInfoCache = localCache.getCache("weatherInfo")
    if (weatherInfoCache) {
        console.log("get weather info from cache, weatherInfo: " + weatherInfoCache)
        weatherInfo.value = weatherInfoCache
    }
    const ipUrl = '/amap/ip?key=' + amapKey
    const ipResponse = await fetch(ipUrl, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    })
    const ipRes = await ipResponse.json()
    if (ipRes?.status === '1') {
        const wUrl = '/amap/weather/weatherInfo?key=' + amapKey + '&extensions=base&city=' + ipRes.adcode
        const wResponse = await fetch(wUrl, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        })
        const res = await wResponse.json()
        if (res?.status === '1') {
            const s = res.lives[0]
            weatherInfo.value = s.city + ' 天气：' + s.weather + ' 温度：' + s.temperature + '摄氏度 风向：' + s.winddirection + ' 风力：' + s.windpower + '级 空气湿度：' + s.humidity
            console.log("weatherInfo: " + weatherInfo.value)
            localCache.setCache("weatherInfo", weatherInfo.value)
        }
    }
}

export {getWeatherInfo}
