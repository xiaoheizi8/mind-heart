package com.mindrealm.core.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.mindrealm.core.service.EmbeddingService;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @className: DashScopeEmbeddingService
 * @description: 阿里云百炼 Embedding 服务实现
 *              使用 text-embedding-v3 模型, 1024维
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.7
 */
@Service
public class DashScopeEmbeddingService implements EmbeddingService {

    private static final Logger logger = LoggerFactory.getLogger(DashScopeEmbeddingService.class);
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/embeddings/text-embedding/text-embedding";
    private static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");
    private static final int DIMENSION = 1024;
    private static final int MAX_BATCH_SIZE = 25;

    @Value("${spring-ai-alibaba.dashscope.api-key:}")
    private String apiKey;

    private final OkHttpClient httpClient = new OkHttpClient();
    private boolean available = false;

    @PostConstruct
    public void init() {
        available = apiKey != null && !apiKey.isEmpty();
        if (available) {
            logger.info("DashScope Embedding 服务已初始化, 模型: text-embedding-v3, 维度: {}", DIMENSION);
        } else {
            logger.warn("未配置 DashScope API Key, Embedding 服务不可用");
        }
    }

    @Override
    public float[] embed(String text) {
        if (!available || text == null || text.isEmpty()) {
            return new float[DIMENSION];
        }
        List<float[]> results = embedBatch(Collections.singletonList(text));
        return results.isEmpty() ? new float[DIMENSION] : results.get(0);
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        if (!available || texts == null || texts.isEmpty()) {
            return new ArrayList<>();
        }

        List<float[]> allEmbeddings = new ArrayList<>();

        // 分批处理,每批最多25条
        for (int i = 0; i < texts.size(); i += MAX_BATCH_SIZE) {
            List<String> batch = texts.subList(i, Math.min(i + MAX_BATCH_SIZE, texts.size()));
            try {
                List<float[]> batchEmbeddings = callEmbeddingApi(batch);
                allEmbeddings.addAll(batchEmbeddings);
            } catch (Exception e) {
                logger.error("Embedding API 调用失败: {}", e.getMessage());
                // 失败的批次填充零向量
                for (int j = 0; j < batch.size(); j++) {
                    allEmbeddings.add(new float[DIMENSION]);
                }
            }
        }

        return allEmbeddings;
    }

    private List<float[]> callEmbeddingApi(List<String> texts) throws IOException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "text-embedding-v3");

        JSONObject input = new JSONObject();
        input.put("texts", texts);
        requestBody.put("input", input);

        JSONObject parameters = new JSONObject();
        parameters.put("text_type", "document");
        requestBody.put("parameters", parameters);

        RequestBody body = RequestBody.create(JSON_TYPE, requestBody.toJSONString());
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown";
                logger.error("Embedding API 请求失败: {}, body: {}", response.code(), errorBody);
                throw new IOException("API request failed: " + response.code());
            }

            String responseBody = response.body().string();
            JSONObject jsonResponse = JSON.parseObject(responseBody);

            JSONObject output = jsonResponse.getJSONObject("output");
            if (output == null) {
                throw new IOException("API response missing output");
            }

            JSONArray embeddings = output.getJSONArray("embeddings");
            List<float[]> result = new ArrayList<>();

            for (int i = 0; i < embeddings.size(); i++) {
                JSONObject embObj = embeddings.getJSONObject(i);
                JSONArray embArray = embObj.getJSONArray("embedding");
                float[] vector = new float[DIMENSION];
                for (int j = 0; j < embArray.size() && j < DIMENSION; j++) {
                    vector[j] = embArray.getFloatValue(j);
                }
                result.add(vector);
            }

            return result;
        }
    }

    @Override
    public int getDimension() {
        return DIMENSION;
    }

    public boolean isAvailable() {
        return available;
    }
}
