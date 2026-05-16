package com.mindrealm.diary.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.diary.entity.Diary;

import java.io.Serializable;
import java.util.List;

/**
 * @className: DiaryService
 * @description: 日记服务接口
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
public interface DiaryService {

    /**
     * 创建日记
     * @param diary 日记对象
     * @return 创建后的日记对象
     */
    Diary create(Diary diary);

    /**
     * 根据ID查询日记
     * @param id 日记ID
     * @return 日记对象,不存在返回null
     */
    Diary getById(Long id);

    /**
     * 查询用户的日记列表(带分页)
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Page<Diary> getListByUser(Long userId, int pageNum, int pageSize);

    /**
     * 查询用户的日记列表(支持情绪筛选)
     * @param userId 用户ID
     * @param emotionCategory 情绪分类
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Page<Diary> getListByUser(Long userId, String emotionCategory, int pageNum, int pageSize);

    /**
     * 删除日记
     * @param id 日记ID
     * @param userId 用户ID
     * @return 删除成功返回true
     */
    boolean delete(Long id, Long userId);

    /**
     * 获取用户最近N条日记
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 日记列表
     */
    List<Diary> getRecentByUser(Long userId, int limit);

    /**
     * 清除用户日记缓存
     * @param userId 用户ID
     */
    void clearCache(Long userId);

    /**
     * 更新日记
     * @param diary 日记对象
     * @return 更新后的日记
     */
    Diary update(Diary diary);

    /**
     * 分析文本情感
     * @param text 待分析文本
     * @return 情感分析结果
     */
    EmotionResult analyzeEmotion(String text);

    /**
     * 统计日记总数
     * @return 日记总数
     */
    long count();

    /**
     * 统计最近N天的日记数量
     * @param days 天数
     * @return 日记数量
     */
    long countRecent(int days);

    /**
     * 情感分析结果
     */
    class EmotionResult implements Serializable {
        private static final long serialVersionUID = 1L;

        private final double score;
        private final String category;

        public EmotionResult(double score, String category) {
            this.score = score;
            this.category = category;
        }

        public double getScore() { return score; }
        public String getCategory() { return category; }
    }
}
