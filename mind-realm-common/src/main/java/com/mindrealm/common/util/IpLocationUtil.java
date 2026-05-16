package com.mindrealm.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @className: IpLocationUtil
 * @description: IP归属地查询工具类
 *               支持离线IP库和在线API两种方式
 * @author: 一朝风月
 * @code: 面向自己，面向未来
 * @createTime: 2026.4.5
 */
@Component
public class IpLocationUtil {

    private static final Logger log = LoggerFactory.getLogger(IpLocationUtil.class);

    @Value("${app.ip.api-url:}")
    private String apiUrl;

    @Value("${app.ip.enabled:false}")
    private boolean enabled;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // IP缓存，避免频繁查询
    private static final Map<String, String> locationCache = new ConcurrentHashMap<>();

    /**
     * 获取IP归属地
     * @param ip IP地址
     * @return 归属地字符串
     */
    public static String getLocation(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "未知";
        }

        // 本地地址
        if (isLocalIp(ip)) {
            return "本地";
        }

        // 检查缓存
        String cached = locationCache.get(ip);
        if (cached != null) {
            return cached;
        }

        // 简单的IP段判断（离线模式）
        String location = getOfflineLocation(ip);
        
        // 缓存结果
        if (location != null) {
            locationCache.put(ip, location);
            return location;
        }

        return "未知";
    }

    /**
     * 离线IP归属地判断（基于IP段）
     */
    private static String getOfflineLocation(String ip) {
        // 解析IP
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return null;
        }

        try {
            int first = Integer.parseInt(parts[0]);
            int second = Integer.parseInt(parts[1]);

            // 常见IP段归属
            if (first == 10 || (first == 172 && second >= 16 && second <= 31) || 
                (first == 192 && second == 168)) {
                return "内网";
            }

            // 中国电信
            if (first == 1 || first == 14 || first == 27 || first == 36 || 
                first == 39 || first == 42 || first == 49 || first == 58 ||
                first == 61 || first == 106 || first == 110 || first == 112 ||
                first == 113 || first == 117 || first == 119 || first == 120 ||
                first == 123 || first == 124 || first == 125 || first == 180 ||
                first == 182 || first == 183 || first == 202 || first == 210 ||
                first == 211 || first == 218 || first == 219 || first == 220 ||
                first == 221 || first == 222 || first == 223) {
                // 根据第二段细分
                if (first == 1) return "美国";
                if (first == 14) return "美国";
                if (first == 27) return "美国";
                if (first == 58) return "韩国";
                if (first == 61 && second >= 128) return "台湾";
                if (first == 61) return "日本";
                if (first == 106 && second >= 80) return "印度";
                if (first == 110) return "印度";
                if (first == 112) return "韩国";
                if (first == 113) return "日本";
                if (first == 117) return "韩国";
                if (first == 119) return "印度";
                if (first == 120) return "新西兰";
                if (first == 123) return "韩国";
                if (first == 124) return "澳大利亚";
                if (first == 125) return "印度";
                if (first == 180) return "香港";
                if (first == 182) return "印度";
                if (first == 183) return "韩国";
                
                // 国内IP
                return "中国";
            }

            // 常见国家IP段
            if (first == 3 || first == 4 || first == 6 || first == 7 || 
                first == 8 || first == 9 || first == 11 || first == 12) {
                return "美国";
            }
            if (first == 24 || first == 25 || first == 26 || first == 64 || first == 65 || first == 66 || first == 67 || first == 68 || first == 69 || first == 70 || first == 71 || first == 72 || first == 73 || first == 74 || first == 75 || first == 76) {
                return "美国";
            }
            if (first == 77 || first == 78 || first == 79 || first == 80 || first == 81 || first == 82 || first == 83 || first == 84 || first == 85 || first == 86 || first == 87 || first == 88 || first == 89 || first == 90 || first == 91 || first == 92) {
                return "欧洲";
            }
            if (first == 93 || first == 94 || first == 95) {
                return "欧洲";
            }
            if (first == 126 || first == 133 || first == 150 || first == 153) {
                return "日本";
            }
            if (first == 147 || first == 145 || first == 143 || first == 141 || first == 137 || first == 136 || first == 134) {
                return "欧洲";
            }
            if (first == 129 || first == 130 || first == 131 || first == 132) {
                return "美国";
            }
            if (first == 138 || first == 139) {
                return "美国";
            }
            if (first == 140 || first == 142) {
                return "亚洲";
            }
            if (first == 144 || first == 146 || first == 148 || first == 149) {
                return "美国";
            }
            if (first == 151 || first == 152 || first == 155 || first == 156 || first == 157 || first == 158 || first == 159) {
                return "欧洲";
            }
            if (first == 160 || first == 161 || first == 162 || first == 163 || first == 164 || first == 165 || first == 166 || first == 167 || first == 168 || first == 169) {
                return "美国";
            }
            if (first == 170 || first == 171 || first == 172 || first == 173 || first == 174 || first == 175) {
                return "美国";
            }
            if (first == 176 || first == 177 || first == 178 || first == 179) {
                return "欧洲";
            }
            if (first == 184 || first == 185 || first == 186 || first == 187 || first == 188 || first == 189) {
                return "欧洲";
            }
            if (first == 190 || first == 191 || first == 192 || first == 193 || first == 194 || first == 195 || first == 196 || first == 197 || first == 198 || first == 199) {
                return "美国";
            }
            if (first == 200 || first == 201 || first == 202 || first == 203) {
                return "亚洲";
            }
            if (first == 204 || first == 205 || first == 206 || first == 207 || first == 208 || first == 209) {
                return "美国";
            }
            if (first == 212 || first == 213 || first == 214 || first == 215 || first == 216 || first == 217 || first == 218 || first == 219) {
                return "亚洲";
            }
            if (first == 220 || first == 221 || first == 222 || first == 223) {
                return "中国";
            }

            return "未知";
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断是否本地IP
     */
    private static boolean isLocalIp(String ip) {
        return "127.0.0.1".equals(ip) || 
               "0:0:0:0:0:0:0:1".equals(ip) || 
               "::1".equals(ip) ||
               "localhost".equalsIgnoreCase(ip) ||
               ip.startsWith("192.168.") ||
               ip.startsWith("10.") ||
               ip.startsWith("172.16.") ||
               ip.startsWith("172.17.") ||
               ip.startsWith("172.18.") ||
               ip.startsWith("172.19.") ||
               ip.startsWith("172.20.") ||
               ip.startsWith("172.21.") ||
               ip.startsWith("172.22.") ||
               ip.startsWith("172.23.") ||
               ip.startsWith("172.24.") ||
               ip.startsWith("172.25.") ||
               ip.startsWith("172.26.") ||
               ip.startsWith("172.27.") ||
               ip.startsWith("172.28.") ||
               ip.startsWith("172.29.") ||
               ip.startsWith("172.30.") ||
               ip.startsWith("172.31.");
    }

    /**
     * 清除缓存
     */
    public static void clearCache() {
        locationCache.clear();
    }
}
