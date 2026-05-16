package com.mindrealm.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mindrealm.common.entity.UserProfile;
import com.mindrealm.common.mapper.UserProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @className: UserProfileService
 * @description: 用户画像服务,提供用户数字孪生画像的管理功能
 *               包括画像创建、查询、更新和AI分析结果存储
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Service
public class UserProfileService {

    @Autowired
    private UserProfileMapper userProfileMapper;

    /**
     * 根据用户ID获取画像
     * @param userId 用户ID
     * @return 用户画像,不存在返回null
     */
    public UserProfile getByUserId(Long userId) {
        LambdaQueryWrapper<UserProfile> wrapper = new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUserId, userId);
        return userProfileMapper.selectOne(wrapper);
    }

    /**
     * 创建或更新用户画像
     * @param userProfile 用户画像对象
     * @return 保存后的画像
     */
    public UserProfile save(UserProfile userProfile) {
        UserProfile existing = getByUserId(userProfile.getUserId());
        
        if (existing != null) {
            userProfile.setId(existing.getId());
            userProfile.setUpdatedAt(LocalDateTime.now());
            userProfileMapper.updateById(userProfile);
        } else {
            userProfile.setUpdatedAt(LocalDateTime.now());
            userProfileMapper.insert(userProfile);
        }
        
        return userProfile;
    }

    /**
     * 更新人格标签
     * @param userId 用户ID
     * @param personalityTags 人格标签JSON
     */
    public void updatePersonalityTags(Long userId, String personalityTags) {
        UserProfile profile = getByUserId(userId);
        if (profile == null) {
            profile = UserProfile.builder()
                    .userId(userId)
                    .personalityTags(personalityTags)
                    .build();
            save(profile);
        } else {
            profile.setPersonalityTags(personalityTags);
            profile.setUpdatedAt(LocalDateTime.now());
            userProfileMapper.updateById(profile);
        }
    }

    /**
     * 更新情绪模式
     * @param userId 用户ID
     * @param emotionalPattern 情绪模式JSON
     */
    public void updateEmotionalPattern(Long userId, String emotionalPattern) {
        UserProfile profile = getByUserId(userId);
        if (profile == null) {
            profile = UserProfile.builder()
                    .userId(userId)
                    .emotionalPattern(emotionalPattern)
                    .build();
            save(profile);
        } else {
            profile.setEmotionalPattern(emotionalPattern);
            profile.setUpdatedAt(LocalDateTime.now());
            userProfileMapper.updateById(profile);
        }
    }

    /**
     * 更新压力触发点
     * @param userId 用户ID
     * @param stressTriggers 压力触发点JSON
     */
    public void updateStressTriggers(Long userId, String stressTriggers) {
        UserProfile profile = getByUserId(userId);
        if (profile == null) {
            profile = UserProfile.builder()
                    .userId(userId)
                    .stressTriggers(stressTriggers)
                    .build();
            save(profile);
        } else {
            profile.setStressTriggers(stressTriggers);
            profile.setUpdatedAt(LocalDateTime.now());
            userProfileMapper.updateById(profile);
        }
    }

    /**
     * 更新最后分析结果
     * @param userId 用户ID
     * @param lastAnalysis 分析结果JSON
     */
    public void updateLastAnalysis(Long userId, String lastAnalysis) {
        UserProfile profile = getByUserId(userId);
        if (profile == null) {
            profile = UserProfile.builder()
                    .userId(userId)
                    .lastAnalysis(lastAnalysis)
                    .build();
            save(profile);
        } else {
            profile.setLastAnalysis(lastAnalysis);
            profile.setUpdatedAt(LocalDateTime.now());
            userProfileMapper.updateById(profile);
        }
    }

    /**
     * 删除用户画像
     * @param userId 用户ID
     * @return 删除成功返回true
     */
    public boolean deleteByUserId(Long userId) {
        LambdaQueryWrapper<UserProfile> wrapper = new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUserId, userId);
        return userProfileMapper.delete(wrapper) > 0;
    }
}
