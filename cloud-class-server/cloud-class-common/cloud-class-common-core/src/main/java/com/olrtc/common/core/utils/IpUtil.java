package com.olrtc.common.core.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.olrtc.common.core.utils.http.HttpUtil;
import com.olrtc.common.core.utils.net.Ipv4Util;
import lombok.extern.slf4j.Slf4j;

/**
 * @author njy
 * @since 2023/2/23 13:55
 */
@Slf4j
public class IpUtil {
    //
//    // IP地址查询
    public static final String IP_URL = "https://whois.pconline.com.cn/ipJson.jsp";

    public static final String UNKNOWN = "未知地址";

    public static String getRealAddressByIP(String ip) {
        String address = UNKNOWN;
        if (StrUtil.isBlank(ip)) {
            return address;
        }
        // 内网不查询
        if (Ipv4Util.isInnerIP(ip)) {
            return "内网IP";
        }
        try {
            String res = HttpUtil.builder().url(IP_URL)
                    .addParam("ip", ip)
                    .addParam("json", "true")
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .get()
                    .sync();
            if (StrUtil.isBlank(res)) {
                log.error("获取地理位置异常 {}", ip);
                return UNKNOWN;
            }
            JsonNode jsonNode = JsonUtil.parserJsonStr(res);
            JsonNode proNode = jsonNode.get("pro");
            JsonNode cityNode = jsonNode.get("city");
            JsonNode regionNode = jsonNode.get("region");
            JsonNode addrNode = jsonNode.get("addr");
            String pro = proNode != null ? proNode.asText() : "";
            String city = cityNode != null ? cityNode.asText() : "";
            String region = regionNode != null ? regionNode.asText() : "";
            String addr = addrNode != null ? addrNode.asText() : "";
            return String.format("%s %s %s", pro, city, region);
        } catch (Exception e) {
            log.error("获取地理位置异常 {}", ip);
        }
        return address;
    }

    public static void main(String[] args) {
        System.out.println(getRealAddressByIP("47.122.7.182"));
    }


}
