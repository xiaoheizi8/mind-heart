package com.mindrealm.story.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mindrealm.common.entity.Notification;
import com.mindrealm.common.mapper.NotificationMapper;
import com.mindrealm.common.result.PageResult;
import com.mindrealm.story.model.entity.HeartStory;
import com.mindrealm.story.model.entity.StoryLike;
import com.mindrealm.story.model.entity.StoryComment;
import com.mindrealm.story.model.entity.StoryFavorite;
import com.mindrealm.story.model.vo.AdminStoryListVO;
import com.mindrealm.story.model.vo.AdminStoryVO;
import com.mindrealm.story.model.vo.StoryVO;
import com.mindrealm.story.model.vo.StoryListVO;
import com.mindrealm.story.model.converter.StoryConverter;
import com.mindrealm.story.service.HeartStoryService;
import com.mindrealm.story.mapper.HeartStoryMapper;
import com.mindrealm.story.mapper.StoryLikeMapper;
import com.mindrealm.story.mapper.StoryCommentMapper;
import com.mindrealm.story.mapper.StoryFavoriteMapper;
import com.mindrealm.common.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @className: HeartStoryServiceImpl
 * @description: 匿名故事服务实现类
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.5.22
 */
@Service
@RequiredArgsConstructor
public class HeartStoryServiceImpl extends ServiceImpl<HeartStoryMapper, HeartStory>
    implements HeartStoryService {

    private static final Logger log = LoggerFactory.getLogger(HeartStoryServiceImpl.class);

    private final StoryLikeMapper storyLikeMapper;
    private final StoryCommentMapper storyCommentMapper;
    private final StoryFavoriteMapper storyFavoriteMapper;
    private final NotificationMapper notificationMapper;
    private final UserService userService;

    // 敏感词列表 先基于内存
    private static final Set<String> SENSITIVE_WORDS = new HashSet<>(Arrays.asList(
            "自杀", "自残", "死", "杀", "恨", "讨厌自己", "活着没意思", "不想活", "结束生命",
            "暴力", "报复", "毁灭"
    ));
    private static final Pattern SENSITIVE_PATTERN = Pattern.compile(
            ".*(" + String.join("|", SENSITIVE_WORDS) + ").*"
    );

    // 举报关键词
    private static final Set<String> REPORT_WORDS = new HashSet<>(Arrays.asList(
            "举报", "违规", "广告", "诈骗", "涉黄", "涉政", "谣言"
    ));

    /**
     * 内容敏感词检测
     */
    private String detectSensitiveContent(String content) {
        if (!StringUtils.hasText(content)) return null;
        for (String word : SENSITIVE_WORDS) {
            if (content.contains(word)) {
                return word;
            }
        }
        return null;
    }

    /**
     * 发送通知
     */
    private void sendNotification(Long userId, String type, String title, String content) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .content(content)
                .isRead(0)
                .createdAt(LocalDateTime.now())
                .build();
        notificationMapper.insert(notification);
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long publishStory(Long userId, String title, String content, 
                            String emotionType, String tags, Boolean isAnonymous) {
        log.info("publishStory: userId={}, title={}", userId, title);
        
        HeartStory story = HeartStory.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .emotionType(emotionType)
                .tags(tags)
                .isAnonymous(isAnonymous ? 1 : 0)
                .status(0)  // 待审核
                .likeCount(0)
                .commentCount(0)
                .shareCount(0)
                .viewCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // 生成展示昵称
        if (isAnonymous) {
            story.setDisplayNickname(generateAnonymousNickname());
        } else {
            var user = userService.findById(userId);
            story.setDisplayNickname(user != null ? user.getNickname() : "匿名用户");
        }
        
        int result = baseMapper.insert(story);
        if (result > 0) {
            log.info("publishStory: 发布成功, storyId={}", story.getId());
            
            // 自动敏感词检测
            String sensitiveWord = detectSensitiveContent(story.getContent());
            if (sensitiveWord != null) {
                log.warn("publishStory: 检测到敏感词 '{}', storyId={}", sensitiveWord, story.getId());
                sendNotification(userId, "system", "故事审核提醒", 
                        "您的故事 \"" + title + "\" 因包含敏感内容正在审核中，请耐心等待。");
            } else {
                // 无敏感词,自动通过审核
                baseMapper.updateById(HeartStory.builder()
                        .id(story.getId())
                        .status(1)
                        .publishedAt(LocalDateTime.now())
                        .build());
                log.info("publishStory: 自动审核通过, storyId={}", story.getId());
            }
            
            return story.getId();
        }
        return 0L;
    }

    @Override
    public PageResult<StoryListVO> getStoryList(Integer page, Integer size, 
                                                        String emotionType, String sortBy) {
        log.debug("getStoryList: page={}, size={}, emotionType={}, sortBy={}", 
                  page, size, emotionType, sortBy);
        
        page = page == null || page < 1 ? 1 : page;
        size = size == null || size < 1 ? 10 : Math.min(size, 50);
        
        LambdaQueryWrapper<HeartStory> wrapper = new LambdaQueryWrapper<>();
        
        // 只查询已发布的
        wrapper.eq(HeartStory::getStatus, 1);
        
        // 情绪类型筛选
        if (emotionType != null && !emotionType.isEmpty()) {
            wrapper.eq(HeartStory::getEmotionType, emotionType);
        }
        
        // 排序方式
        if ("hot".equals(sortBy)) {
            wrapper.orderByDesc(HeartStory::getLikeCount, HeartStory::getCommentCount);
        } else if ("random".equals(sortBy)) {
            wrapper.last("ORDER BY RAND()");
        } else {
            wrapper.orderByDesc(HeartStory::getPublishedAt);
        }
        
        Page<HeartStory> storyPage = baseMapper.selectPage(new Page<>(page, size), wrapper);
        
        // 转换为VO
        List<StoryListVO> voList = storyPage.getRecords().stream()
                .map(StoryConverter::toStoryListVO)
                .collect(Collectors.toList());
        
        return PageResult.of(voList, page, size, storyPage.getTotal());
    }

    @Override
    public StoryVO getStoryDetail(Long storyId, Long userId) {
        log.debug("getStoryDetail: storyId={}, userId={}", storyId, userId);
        
        HeartStory story = baseMapper.selectById(storyId);
        if (story == null) {
            log.warn("getStoryDetail: 故事不存在, storyId={}", storyId);
            return null;
        }
        
        // 增加浏览数
        HeartStory update = new HeartStory();
        update.setId(storyId);
        update.setViewCount(story.getViewCount() + 1);
        baseMapper.updateById(update);
        
        // 判断是否已点赞/收藏
        Boolean liked = userId != null ? isLiked(storyId, userId) : null;
        Boolean favorited = userId != null ? isFavorited(storyId, userId) : null;
        
        return StoryConverter.toStoryVO(story, liked, favorited);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean likeStory(Long storyId, Long userId) {
        log.info("likeStory: storyId={}, userId={}", storyId, userId);
        
        // 检查是否已点赞
        if (isLiked(storyId, userId)) {
            log.warn("likeStory: 已点赞过, storyId={}, userId={}", storyId, userId);
            return false;
        }
        
        // 添加点赞记录
        StoryLike like = StoryLike.builder()
                .storyId(storyId)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build();
        storyLikeMapper.insert(like);
        
        // 更新点赞数
        updateLikeCount(storyId, 1);
        
        log.info("likeStory: 点赞成功, storyId={}, userId={}", storyId, userId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlikeStory(Long storyId, Long userId) {
        log.info("unlikeStory: storyId={}, userId={}", storyId, userId);
        
        LambdaQueryWrapper<StoryLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoryLike::getStoryId, storyId)
               .eq(StoryLike::getUserId, userId);
        
        int deleted = storyLikeMapper.delete(wrapper);
        
        if (deleted > 0) {
            updateLikeCount(storyId, -1);
            log.info("unlikeStory: 取消点赞成功, storyId={}, userId={}", storyId, userId);
            return true;
        }
        
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addComment(Long storyId, Long userId, String content, Long parentId) {
        log.info("addComment: storyId={}, userId={}, parentId={}", storyId, userId, parentId);
        
        StoryComment comment = StoryComment.builder()
                .storyId(storyId)
                .userId(userId)
                .parentId(parentId)
                .content(content)
                .status(1)
                .likeCount(0)
                .isTemplate(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        int result = storyCommentMapper.insert(comment);
        
        if (result > 0) {
            // 更新评论数
            updateCommentCount(storyId, 1);
            log.info("addComment: 评论成功, commentId={}", comment.getId());
            return comment.getId();
        }
        
        return 0L;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean favoriteStory(Long storyId, Long userId) {
        log.info("favoriteStory: storyId={}, userId={}", storyId, userId);
        
        // 检查是否已收藏
        if (isFavorited(storyId, userId)) {
            log.warn("favoriteStory: 已收藏过, storyId={}, userId={}", storyId, userId);
            return false;
        }
        
        StoryFavorite favorite = StoryFavorite.builder()
                .storyId(storyId)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build();
        
        int result = storyFavoriteMapper.insert(favorite);
        
        if (result > 0) {
            log.info("favoriteStory: 收藏成功, storyId={}, userId={}", storyId, userId);
            return true;
        }
        
        return false;
    }

    @Override
    public PageResult<StoryListVO> getUserFavorites(Long userId, Integer page, Integer size) {
        log.debug("getUserFavorites: userId={}, page={}, size={}", userId, page, size);
        
        page = page == null || page < 1 ? 1 : page;
        size = size == null || size < 1 ? 10 : Math.min(size, 50);
        
        // 查询用户收藏的故事ID
        LambdaQueryWrapper<StoryFavorite> favWrapper = new LambdaQueryWrapper<>();
        favWrapper.eq(StoryFavorite::getUserId, userId)
                  .orderByDesc(StoryFavorite::getCreatedAt);
        
        Page<StoryFavorite> favPage = storyFavoriteMapper.selectPage(
                new Page<>(page, size), favWrapper);
        
        // 获取收藏的故事详情
        List<StoryListVO> voList = favPage.getRecords().stream()
                .map(fav -> {
                    HeartStory story = baseMapper.selectById(fav.getStoryId());
                    return story != null ? StoryConverter.toStoryListVO(story) : null;
                })
                .filter(vo -> vo != null)
                .collect(Collectors.toList());
        
        return PageResult.of(voList, page, size, favPage.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditStory(Long storyId, Long auditorId, boolean approved, String rejectReason) {
        log.info("auditStory: storyId={}, auditorId={}, approved={}", storyId, auditorId, approved);
        
        HeartStory story = baseMapper.selectById(storyId);
        if (story == null) {
            log.warn("auditStory: 故事不存在, storyId={}", storyId);
            return false;
        }
        
        HeartStory update = new HeartStory();
        update.setId(storyId);
        update.setAuditorId(auditorId);
        update.setAuditTime(LocalDateTime.now());
        
        if (approved) {
            update.setStatus(1);  // 已通过
            update.setPublishedAt(LocalDateTime.now());
        } else {
            update.setStatus(2);  // 已拒绝
            update.setRejectReason(rejectReason);
        }
        
        int result = baseMapper.updateById(update);
        
        if (result > 0) {
            log.info("auditStory: 审核完成, storyId={}, status={}", storyId, update.getStatus());
            
            // 发送审核结果通知
            String title = approved ? "故事审核通过" : "故事审核未通过";
            String content = approved 
                    ? "您的故事 \"" + story.getTitle() + "\" 已审核通过，感谢您的分享！"
                    : "您的故事 \"" + story.getTitle() + "\" 未通过审核。" 
                      + (StringUtils.hasText(rejectReason) ? "原因：" + rejectReason : "");
            sendNotification(story.getUserId(), "system", title, content);
            
            return true;
        }
        
        return false;
    }

    @Override
    public PageResult<StoryListVO> getPendingStories(Integer page, Integer size, Integer status) {
        log.debug("getPendingStories: page={}, size={}, status={}", page, size, status);

        page = page == null || page < 1 ? 1 : page;
        size = size == null || size < 1 ? 10 : Math.min(size, 50);

        LambdaQueryWrapper<HeartStory> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(HeartStory::getStatus, status);
        }

        wrapper.orderByDesc(HeartStory::getCreatedAt);

        Page<HeartStory> storyPage = baseMapper.selectPage(new Page<>(page, size), wrapper);

        List<StoryListVO> voList = storyPage.getRecords().stream()
                .map(StoryConverter::toStoryListVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, page, size, storyPage.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStory(Long storyId, Long userId) {
        log.info("deleteStory: storyId={}, userId={}", storyId, userId);
        
        HeartStory story = baseMapper.selectById(storyId);
        if (story == null) {
            log.warn("deleteStory: 故事不存在, storyId={}", storyId);
            return false;
        }
        
        // 检查是否是作者或管理员(管理员role=4)
        var user = userService.findById(userId);
        boolean isAdmin = user != null && user.getRole() == 4;
        
        if (!story.getUserId().equals(userId) && !isAdmin) {
            log.warn("deleteStory: 无权限删除, storyId={}, userId={}", storyId, userId);
            return false;
        }
        
        // 软删除 - 更新状态
        HeartStory update = new HeartStory();
        update.setId(storyId);
        update.setStatus(3);  // 已下架
        
        int result = baseMapper.updateById(update);
        
        if (result > 0) {
            log.info("deleteStory: 删除成功, storyId={}", storyId);
            return true;
        }
        
        return false;
    }

    @Override
    public PageResult<AdminStoryListVO> getAdminStoryList(Integer page, Integer size, Integer status) {
        log.debug("getAdminStoryList: page={}, size={}, status={}", page, size, status);

        page = page == null || page < 1 ? 1 : page;
        size = size == null || size < 1 ? 10 : Math.min(size, 50);

        LambdaQueryWrapper<HeartStory> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(HeartStory::getStatus, status);
        }

        wrapper.orderByDesc(HeartStory::getCreatedAt);

        Page<HeartStory> storyPage = baseMapper.selectPage(new Page<>(page, size), wrapper);

        List<AdminStoryListVO> voList = storyPage.getRecords().stream()
                .map(StoryConverter::toAdminStoryListVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, page, size, storyPage.getTotal());
    }

    @Override
    public AdminStoryVO getAdminStoryDetail(Long storyId) {
        log.debug("getAdminStoryDetail: storyId={}", storyId);

        HeartStory story = baseMapper.selectById(storyId);
        if (story == null) {
            log.warn("getAdminStoryDetail: 故事不存在, storyId={}", storyId);
            return null;
        }

        return StoryConverter.toAdminStoryVO(story);
    }

    // ==================== 私有方法 ====================

    private boolean isLiked(Long storyId, Long userId) {
        LambdaQueryWrapper<StoryLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoryLike::getStoryId, storyId)
               .eq(StoryLike::getUserId, userId);
        return storyLikeMapper.selectCount(wrapper) > 0;
    }

    private boolean isFavorited(Long storyId, Long userId) {
        LambdaQueryWrapper<StoryFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoryFavorite::getStoryId, storyId)
               .eq(StoryFavorite::getUserId, userId);
        return storyFavoriteMapper.selectCount(wrapper) > 0;
    }

    private void updateLikeCount(Long storyId, int delta) {
        HeartStory story = baseMapper.selectById(storyId);
        if (story != null) {
            HeartStory update = new HeartStory();
            update.setId(storyId);
            update.setLikeCount(Math.max(0, story.getLikeCount() + delta));
            baseMapper.updateById(update);
        }
    }

    private void updateCommentCount(Long storyId, int delta) {
        HeartStory story = baseMapper.selectById(storyId);
        if (story != null) {
            HeartStory update = new HeartStory();
            update.setId(storyId);
            update.setCommentCount(Math.max(0, story.getCommentCount() + delta));
            baseMapper.updateById(update);
        }
    }

    private String generateAnonymousNickname() {
        String[] adjectives = {"温暖的", "沉默的", "孤独的", "坚强的", "乐观的", 
                              "安静的", "勇敢的", "善良的", "智慧的", "乐观的"};
        String[] nouns = {"星星", "月亮", "太阳", "云朵", "风", 
                         "雨", "彩虹", "落叶", "雪花", "小草"};
        
        int adjIdx = (int) (Math.random() * adjectives.length);
        int nounIdx = (int) (Math.random() * nouns.length);
        
        return adjectives[adjIdx] + nouns[nounIdx];
    }
}




