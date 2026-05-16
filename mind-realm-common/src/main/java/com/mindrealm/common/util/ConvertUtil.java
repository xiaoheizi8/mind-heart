package com.mindrealm.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindrealm.common.dto.UserDTO;
import com.mindrealm.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @className: ConvertUtil
 * @description: 对象转换工具类,提供对象与DTO之间的转换、JSON解析等功能
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Component
public class ConvertUtil {

    private static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        ConvertUtil.objectMapper = objectMapper;
    }

    /**
     * User转UserDTO
     * @param user 用户实体
     * @return 用户DTO
     */
    public static UserDTO toUserDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPhone(user.getPhone());
        dto.setNickname(user.getNickname());
        dto.setAvatar(user.getAvatar());
        dto.setAge(user.getAge());
        dto.setGender(user.getGender());
        dto.setRole(user.getRole());
        dto.setGuardianPhone(user.getGuardianPhone());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    /**
     * 解析JSON数组字符串
     * @param json JSON字符串
     * @return 字符串列表
     */
    public static List<String> parseJsonArray(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    /**
     * 对象转JSON字符串
     * @param obj 待转换对象
     * @return JSON字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}