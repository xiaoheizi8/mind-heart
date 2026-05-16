package com.mindrealm.common.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindrealm.common.service.SmsService;
import com.mindrealm.common.util.ValidatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @className: AliyunSmsServiceImpl
 * @description: 阿里云短信服务实现
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Service
@ConditionalOnProperty(name = "notification.sms.provider", havingValue = "aliyun")
public class AliyunSmsServiceImpl implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(AliyunSmsServiceImpl.class);

    private static final String API_URL = "https://dysmsapi.aliyuncs.com/";
    private static final String SIGNATURE_METHOD = "HMAC-SHA1";
    private static final String SIGNATURE_VERSION = "1.0";
    private static final String VERSION = "2017-05-25";
    private static final String ACTION = "SendSms";
    private static final String REGION_ID = "cn-hangzhou";

    @Value("${notification.sms.aliyun.access-key-id:}")
    private String accessKeyId;

    @Value("${notification.sms.aliyun.access-key-secret:}")
    private String accessKeySecret;

    @Value("${notification.sms.aliyun.sign-name:心域}")
    private String signName;

    @Value("${notification.sms.aliyun.template-code.warning:SMS_WARNING_TEMPLATE}")
    private String warningTemplateCode;

    @Value("${notification.sms.aliyun.template-code.verify-code:SMS_VERIFY_TEMPLATE}")
    private String verifyCodeTemplateCode;

    @Value("${notification.sms.dry-run:false}")
    private boolean dryRun;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public SmsResult send(String phone, String templateCode, Map<String, String> params) {
        if (!ValidatorUtil.isPhone(phone)) {
            return SmsResult.fail("手机号格式错误");
        }

        if (ValidatorUtil.isEmpty(templateCode)) {
            return SmsResult.fail("模板代码不能为空");
        }

        if (dryRun) {
            log.info("SMS Dry-run: phone={}, template={}, params={}", phone, templateCode, params);
            return SmsResult.success("dry-run-" + System.currentTimeMillis());
        }

        if (ValidatorUtil.isEmpty(accessKeyId) || ValidatorUtil.isEmpty(accessKeySecret)) {
            log.warn("阿里云短信未配置，模拟发送成功");
            return SmsResult.success("mock-" + System.currentTimeMillis());
        }

        try {
            Map<String, String> queryParams = buildQueryParams(phone, templateCode, params);
            String signature = calculateSignature(queryParams);
            queryParams.put("Signature", signature);

            String url = buildUrl(queryParams);
            log.debug("SMS请求URL: {}", url);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return parseResponse(response.getBody());
        } catch (Exception e) {
            log.error("短信发送异常: phone={}, error={}", phone, e.getMessage());
            return SmsResult.fail("发送异常: " + e.getMessage());
        }
    }

    @Override
    public SmsResult sendWarningSms(String phone, String userName, int riskLevel) {
        String riskLevelDesc = switch (riskLevel) {
            case 3 -> "高风险";
            case 2 -> "中风险";
            default -> "低风险";
        };

        Map<String, String> params = new HashMap<>();
        params.put("name", userName);
        params.put("level", riskLevelDesc);

        return send(phone, warningTemplateCode, params);
    }

    @Override
    public SmsResult sendVerifyCode(String phone, String code) {
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        return send(phone, verifyCodeTemplateCode, params);
    }

    private Map<String, String> buildQueryParams(String phone, String templateCode, Map<String, String> templateParams) {
        Map<String, String> params = new TreeMap<>();
        params.put("AccessKeyId", accessKeyId);
        params.put("Action", ACTION);
        params.put("Format", "JSON");
        params.put("PhoneNumbers", phone);
        params.put("RegionId", REGION_ID);
        params.put("SignName", signName);
        params.put("SignatureMethod", SIGNATURE_METHOD);
        params.put("SignatureNonce", UUID.randomUUID().toString());
        params.put("SignatureVersion", SIGNATURE_VERSION);
        params.put("TemplateCode", templateCode);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        params.put("Timestamp", sdf.format(new Date()));
        params.put("Version", VERSION);

        if (templateParams != null && !templateParams.isEmpty()) {
            try {
                params.put("TemplateParam", objectMapper.writeValueAsString(templateParams));
            } catch (Exception e) {
                log.warn("序列化模板参数失败", e);
            }
        }

        return params;
    }

    private String calculateSignature(Map<String, String> params) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(percentEncode(entry.getKey()))
              .append("=")
              .append(percentEncode(entry.getValue()));
        }

        String stringToSign = "GET&" + percentEncode("/") + "&" + percentEncode(sb.toString());

        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec keySpec = new SecretKeySpec(
                (accessKeySecret + "&").getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        mac.init(keySpec);
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(signData);
    }

    private String percentEncode(String value) {
        if (value == null) return "";
        try {
            return URLEncoder.encode(value, "UTF-8")
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (Exception e) {
            return value;
        }
    }

    private String buildUrl(Map<String, String> params) {
        StringBuilder sb = new StringBuilder(API_URL).append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.charAt(sb.length() - 1) != '?') {
                sb.append("&");
            }
            sb.append(percentEncode(entry.getKey()))
              .append("=")
              .append(percentEncode(entry.getValue()));
        }
        return sb.toString();
    }

    private SmsResult parseResponse(String responseBody) {
        try {
            JsonNode json = objectMapper.readTree(responseBody);
            String code = json.path("Code").asText();
            
            if ("OK".equals(code)) {
                String bizId = json.path("BizId").asText();
                log.info("短信发送成功: bizId={}", bizId);
                return SmsResult.success(bizId);
            } else {
                String message = json.path("Message").asText();
                log.warn("短信发送失败: code={}, message={}", code, message);
                return SmsResult.fail(message);
            }
        } catch (Exception e) {
            log.error("解析短信响应失败: {}", e.getMessage());
            return SmsResult.fail("响应解析失败");
        }
    }
}
