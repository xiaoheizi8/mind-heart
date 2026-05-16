package com.mindrealm.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @className: SecurityUtil
 * @description: 安全工具类,提供SQL注入检测、XSS攻击防护、敏感词过滤等安全功能
 *               用于保护系统免受常见的Web安全威胁
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.24
 */
@Component
public class SecurityUtil {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "('|(\\'\\')|(;)|(--)|(\\b(SELECT|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|EXEC|EXECUTE|UNION|TRUNCATE|OR|AND)\\b))",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern XSS_SCRIPT_PATTERN = Pattern.compile(
            "(<[^>]*>|\\bjavascript:|on\\w+=|\\beval\\(|\\bdocument\\.|\\bwindow\\.|\\bcookie\\b)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern XSS_EVENT_PATTERN = Pattern.compile(
            "\\bon(load|click|error|submit|change|focus|blur|reset|select|abort|drag|key|mouse)\\s*=",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern HTML_TAG_PATTERN = Pattern.compile(
            "<[^>]+>",
            Pattern.CASE_INSENSITIVE
    );

    private static final List<String> DANGEROUS_KEYWORDS = List.of(
            "eval", "expression", "script", "cookie", "document", "window", 
            "iframe", "embed", "object", "applet", "meta", "link", "style"
    );

    public String sanitizeSql(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        String result = input;
        result = result.replace("'", "''");
        result = result.replace("--", "");
        result = result.replace(";", "");
        result = result.replace("/*", "");
        result = result.replace("*/", "");
        result = result.replace("xp_", "xp&#95;");
        result = result.replace("sp_", "sp&#95;");
        
        return result;
    }

    public String sanitizeXss(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String result = input;

        result = result.replace("<", "&lt;");
        result = result.replace(">", "&gt;");
        result = result.replace("\"", "&quot;");
        result = result.replace("'", "&#x27;");
        result = result.replace("/", "&#x2F;");

        result = result.replace("javascript:", "");
        result = result.replace("onerror=", "");
        result = result.replace("onclick=", "");
        result = result.replace("onload=", "");

        return result;
    }

    public boolean containsSqlInjection(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return SQL_INJECTION_PATTERN.matcher(input).find();
    }

    public boolean containsXss(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return XSS_SCRIPT_PATTERN.matcher(input).find() || XSS_EVENT_PATTERN.matcher(input).find();
    }

    public boolean containsMaliciousContent(String input) {
        return containsSqlInjection(input) || containsXss(input);
    }

    public String stripHtml(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return HTML_TAG_PATTERN.matcher(input).replaceAll("");
    }

    public String escapeHtml(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    public String maskSensitiveData(String data, String type) {
        if (data == null || data.isEmpty()) {
            return data;
        }

        return switch (type.toLowerCase()) {
            case "phone" -> {
                if (data.length() >= 7) {
                    yield data.substring(0, 3) + "****" + data.substring(data.length() - 4);
                }
                yield "****";
            }
            case "email" -> {
                int atIndex = data.indexOf('@');
                if (atIndex > 2) {
                    yield data.substring(0, 2) + "***" + data.substring(atIndex);
                }
                yield "***@***";
            }
            case "idcard" -> {
                if (data.length() >= 10) {
                    yield data.substring(0, 4) + "**********" + data.substring(data.length() - 4);
                }
                yield "****";
            }
            case "bankcard" -> {
                if (data.length() >= 8) {
                    yield "****" + data.substring(data.length() - 4);
                }
                yield "****";
            }
            default -> {
                int len = data.length();
                if (len <= 4) {
                    yield "****";
                }
                yield data.substring(0, 2) + "****" + data.substring(len - 2);
            }
        };
    }

    public String generateSecureToken(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder token = new StringBuilder();
        java.security.SecureRandom random = new java.security.SecureRandom();
        for (int i = 0; i < length; i++) {
            token.append(chars.charAt(random.nextInt(chars.length())));
        }
        return token.toString();
    }

    public boolean isValidFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        
        String invalidChars = "[\\\\/:*?\"<>|]";
        return !Pattern.compile(invalidChars).matcher(fileName).find();
    }

    public String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "unnamed";
        }
        
        fileName = fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
        fileName = fileName.replaceAll("\\.\\.", "_");
        
        if (fileName.length() > 255) {
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                String ext = fileName.substring(dotIndex);
                fileName = fileName.substring(0, 250) + ext;
            } else {
                fileName = fileName.substring(0, 255);
            }
        }
        
        return fileName;
    }
}
