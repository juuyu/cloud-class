package com.olrtc.common.core.utils.net;

/**
 * @author njy
 * @since 2023/3/9 13:24
 */

import com.olrtc.common.core.text.CharsetKit;
import com.olrtc.common.core.utils.StrUtil;
import com.olrtc.common.core.utils.collection.ListUtil;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IPV4地址工具类
 */
public class Ipv4Util {

    private static final String IPV4 = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)$";

    /**
     * IP v4
     */
    public final static Pattern IPV4_PATTERN     = Pattern.compile(IPV4);

    public static final String  LOCAL_IP = "127.0.0.1";

    /**
     * IP段的分割符
     */
    public static final String IP_SPLIT_MARK = "-";

    /**
     * IP与掩码的分割符
     */
    public static final String IP_MASK_SPLIT_MARK = "/";

    /**
     * 最大掩码位
     */
    public static final int IP_MASK_MAX = 32;




    /**
     * 判定是否为内网IPv4<br>
     * 私有IP：
     * <pre>
     * A类 10.0.0.0-10.255.255.255
     * B类 172.16.0.0-172.31.255.255
     * C类 192.168.0.0-192.168.255.255
     * </pre>
     * 当然，还有127这个网段是环回地址
     *
     * @param ipAddress IP地址
     * @return 是否为内网IP
     * @since 5.7.18
     */
    public static boolean isInnerIP(String ipAddress) {
        boolean isInnerIp;
        long ipNum = ipv4ToLong(ipAddress);

        long aBegin = ipv4ToLong("10.0.0.0");
        long aEnd = ipv4ToLong("10.255.255.255");

        long bBegin = ipv4ToLong("172.16.0.0");
        long bEnd = ipv4ToLong("172.31.255.255");

        long cBegin = ipv4ToLong("192.168.0.0");
        long cEnd = ipv4ToLong("192.168.255.255");

        isInnerIp = isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd) || LOCAL_IP.equals(ipAddress);
        return isInnerIp;
    }



    /**
     * 根据ip地址(xxx.xxx.xxx.xxx)计算出long型的数据
     * 方法别名：inet_aton
     *
     * @param strIP IP V4 地址
     * @return long值
     */
    public static long ipv4ToLong(String strIP) {
        final Matcher matcher = IPV4_PATTERN.matcher(strIP);
        if (matcher.matches()) {
            return matchAddress(matcher);
        }
//		Validator.validateIpv4(strIP, "Invalid IPv4 address!");
//		final long[] ip = Convert.convert(long[].class, StrUtil.split(strIP, CharUtil.DOT));
//		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
        throw new IllegalArgumentException("Invalid IPv4 address!");
    }

    //-------------------------------------------------------------------------------- Private method start


    /**
     * 将匹配到的Ipv4地址的4个分组分别处理
     *
     * @param matcher 匹配到的Ipv4正则
     * @return ipv4对应long
     */
    private static long matchAddress(Matcher matcher) {
        long addr = 0;
        for (int i = 1; i <= 4; ++i) {
            addr |= Long.parseLong(matcher.group(i)) << 8 * (4 - i);
        }
        return addr;
    }

    /**
     * 指定IP的long是否在指定范围内
     *
     * @param userIp 用户IP
     * @param begin  开始IP
     * @param end    结束IP
     * @return 是否在范围内
     */
    private static boolean isInner(long userIp, long begin, long end) {
        return (userIp >= begin) && (userIp <= end);
    }
    //-------------------------------------------------------------------------------- Private method end
}

