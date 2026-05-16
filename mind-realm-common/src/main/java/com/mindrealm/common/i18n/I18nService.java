package com.mindrealm.common.i18n;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @className: I18nService
 * @description: 国际化服务,支持多语言情感分析
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.3
 */
@Component
public class I18nService {

    private static final Map<String, Map<String, String>> TRANSLATIONS = new HashMap<>();
    
    static {
        // 中文
        Map<String, String> zhCN = new HashMap<>();
        zhCN.put("positive", "积极");
        zhCN.put("negative", "消极");
        zhCN.put("anxiety", "焦虑");
        zhCN.put("anger", "愤怒");
        zhCN.put("calm", "平静");
        zhCN.put("neutral", "中性");
        zhCN.put("greeting", "你好,今天心情怎么样?");
        zhCN.put("encourage", "你很棒,继续加油!");
        zhCN.put("comfort", "我理解你的感受,慢慢来。");
        TRANSLATIONS.put("zh-CN", zhCN);
        
        // English
        Map<String, String> enUS = new HashMap<>();
        enUS.put("positive", "positive");
        enUS.put("negative", "negative");
        enUS.put("anxiety", "anxiety");
        enUS.put("anger", "anger");
        enUS.put("calm", "calm");
        enUS.put("neutral", "neutral");
        enUS.put("greeting", "Hello, how are you feeling today?");
        enUS.put("encourage", "You're doing great, keep going!");
        enUS.put("comfort", "I understand how you feel, take it slow.");
        TRANSLATIONS.put("en-US", enUS);
        
        // Japanese
        Map<String, String> jaJP = new HashMap<>();
        jaJP.put("positive", "ポジティブ");
        jaJP.put("negative", "ネガティブ");
        jaJP.put("anxiety", "不安");
        jaJP.put("anger", "怒り");
        jaJP.put("calm", "穏やか");
        jaJP.put("neutral", "中立");
        jaJP.put("greeting", "こんにちは,今日の気分はどうですか?");
        jaJP.put("encourage", "頑張っていますね,その調子で!");
        jaJP.put("comfort", "あなたの気持ちわかります,ゆっくり来吧。");
        TRANSLATIONS.put("ja-JP", jaJP);
    }

    /**
     * 获取翻译文本
     * @param key 翻译键
     * @param locale 语言区域(如zh-CN, en-US, ja-JP)
     * @return 翻译后的文本
     */
    public String translate(String key, String locale) {
        Map<String, String> translations = TRANSLATIONS.getOrDefault(locale, TRANSLATIONS.get("zh-CN"));
        return translations.getOrDefault(key, key);
    }

    /**
     * 翻译情感类别
     * @param category 情感类别
     * @param locale 语言区域
     * @return 本地化的情感类别
     */
    public String translateEmotionCategory(String category, String locale) {
        return translate(category, locale);
    }

    /**
     * 获取支持的语言列表
     * @return 语言代码列表
     */
    public String[] getSupportedLocales() {
        return TRANSLATIONS.keySet().toArray(new String[0]);
    }

    /**
     * 检测文本语言
     * @param text 待检测文本
     * @return 语言代码
     */
    public String detectLanguage(String text) {
        if (text == null || text.isEmpty()) {
            return "zh-CN";
        }
        
        // 简单语言检测(基于字符范围)
        boolean hasChinese = false;
        boolean hasJapanese = false;
        
        for (char c : text.toCharArray()) {
            if (c >= 0x4E00 && c <= 0x9FFF) {
                hasChinese = true;
            }
            if (c >= 0x3040 && c <= 0x309F || c >= 0x30A0 && c <= 0x30FF) {
                hasJapanese = true;
            }
        }
        
        if (hasJapanese) {
            return "ja-JP";
        } else if (hasChinese) {
            return "zh-CN";
        } else {
            return "en-US";
        }
    }

    /**
     * 获取问候语
     * @param locale 语言区域
     * @return 问候语
     */
    public String getGreeting(String locale) {
        return translate("greeting", locale);
    }

    /**
     * 获取鼓励语
     * @param locale 语言区域
     * @return 鼓励语
     */
    public String getEncouragement(String locale) {
        return translate("encourage", locale);
    }

    /**
     * 获取安慰语
     * @param locale 语言区域
     * @return 安慰语
     */
    public String getComfort(String locale) {
        return translate("comfort", locale);
    }
}