package com.mindrealm.story.model.converter;

import com.mindrealm.story.model.entity.HeartStory;
import com.mindrealm.story.model.entity.StoryComment;
import com.mindrealm.story.model.entity.WarmReplyTemplate;
import com.mindrealm.story.model.vo.AdminStoryListVO;
import com.mindrealm.story.model.vo.AdminStoryVO;
import com.mindrealm.story.model.vo.CommentVO;
import com.mindrealm.story.model.vo.StoryListVO;
import com.mindrealm.story.model.vo.StoryVO;
import com.mindrealm.story.model.vo.WarmReplyTemplateVO;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @className: StoryConverter
 * @description: 故事模块实体转换器
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.5.19
 */
public class StoryConverter {
    
    private StoryConverter() {
        // 私有构造函数
    }
    
    /**
     * 实体转换为故事详情VO
     */
    public static StoryVO toStoryVO(HeartStory story, Boolean liked, Boolean favorited) {
        if (story == null) {
            return null;
        }
        return StoryVO.builder()
                .id(story.getId())
                .title(story.getTitle())
                .content(story.getContent())
                .emotionType(story.getEmotionType())
                .tags(parseTags(story.getTags()))
                .coverImage(story.getCoverImage())
                .likeCount(story.getLikeCount())
                .commentCount(story.getCommentCount())
                .shareCount(story.getShareCount())
                .viewCount(story.getViewCount())
                .isAnonymous(story.getIsAnonymous() == 1)
                .displayNickname(story.getDisplayNickname())
                .createdAt(story.getCreatedAt())
                .publishedAt(story.getPublishedAt())
                .liked(liked)
                .favorited(favorited)
                .build();
    }
    
    /**
     * 实体转换为故事列表VO
     */
    public static StoryListVO toStoryListVO(HeartStory story) {
        if (story == null) {
            return null;
        }
        return StoryListVO.builder()
                .id(story.getId())
                .title(story.getTitle())
                .emotionType(story.getEmotionType())
                .tags(parseTags(story.getTags()))
                .coverImage(story.getCoverImage())
                .likeCount(story.getLikeCount())
                .commentCount(story.getCommentCount())
                .isAnonymous(story.getIsAnonymous() == 1)
                .displayNickname(story.getDisplayNickname())
                .createdAt(story.getCreatedAt())
                .build();
    }
    
    /**
     * 实体转换为评论VO
     */
    public static CommentVO toCommentVO(StoryComment comment, String nickname, String avatar,
                                        Boolean isReply, String replyToNickname) {
        if (comment == null) {
            return null;
        }
        return CommentVO.builder()
                .id(comment.getId())
                .storyId(comment.getStoryId())
                .nickname(nickname)
                .avatar(avatar)
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .isReply(isReply)
                .replyToNickname(replyToNickname)
                .createdAt(comment.getCreatedAt())
                .build();
    }
    
    /**
     * 实体转换为模板VO
     */
    public static WarmReplyTemplateVO toTemplateVO(WarmReplyTemplate template) {
        if (template == null) {
            return null;
        }
        return WarmReplyTemplateVO.builder()
                .id(template.getId())
                .category(template.getCategory())
                .content(template.getContent())
                .build();
    }
    
    /**
     * 解析标签字符串为列表
     */
    public static List<String> parseTags(String tags) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
    
    /**
     * 标签列表转字符串
     */
    public static String tagsToString(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return String.join(",", tags);
    }

    /**
     * 实体转换为管理员用故事详情VO
     */
    public static AdminStoryVO toAdminStoryVO(HeartStory story) {
        if (story == null) {
            return null;
        }
        return AdminStoryVO.builder()
                .id(story.getId())
                .userId(story.getUserId())
                .title(story.getTitle())
                .content(story.getContent())
                .emotionType(story.getEmotionType())
                .tags(parseTags(story.getTags()))
                .coverImage(story.getCoverImage())
                .likeCount(story.getLikeCount())
                .commentCount(story.getCommentCount())
                .shareCount(story.getShareCount())
                .viewCount(story.getViewCount())
                .isAnonymous(story.getIsAnonymous() == 1)
                .displayNickname(story.getDisplayNickname())
                .status(story.getStatus())
                .rejectReason(story.getRejectReason())
                .auditorId(story.getAuditorId())
                .auditTime(story.getAuditTime())
                .publishedAt(story.getPublishedAt())
                .createdAt(story.getCreatedAt())
                .updatedAt(story.getUpdatedAt())
                .build();
    }

    /**
     * 实体转换为管理员用故事列表VO
     */
    public static AdminStoryListVO toAdminStoryListVO(HeartStory story) {
        if (story == null) {
            return null;
        }
        return AdminStoryListVO.builder()
                .id(story.getId())
                .userId(story.getUserId())
                .title(story.getTitle())
                .emotionType(story.getEmotionType())
                .tags(parseTags(story.getTags()))
                .coverImage(story.getCoverImage())
                .likeCount(story.getLikeCount())
                .commentCount(story.getCommentCount())
                .isAnonymous(story.getIsAnonymous() == 1)
                .displayNickname(story.getDisplayNickname())
                .status(story.getStatus())
                .rejectReason(story.getRejectReason())
                .auditorId(story.getAuditorId())
                .auditTime(story.getAuditTime())
                .publishedAt(story.getPublishedAt())
                .createdAt(story.getCreatedAt())
                .build();
    }
}