package com.mindrealm.story.service;

import com.mindrealm.common.result.PageResult;
import com.mindrealm.story.model.entity.HeartStory;
import com.mindrealm.story.model.vo.AdminStoryListVO;
import com.mindrealm.story.model.vo.AdminStoryVO;
import com.mindrealm.story.model.vo.StoryVO;
import com.mindrealm.story.model.vo.StoryListVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @className: HeartStoryService
 * @description: 匿名故事服务接口
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
public interface HeartStoryService extends IService<HeartStory> {

    /**
     * 发布匿名故事
     *
     * @param userId 用户ID
     * @param title 标题
     * @param content 内容
     * @param emotionType 情绪类型
     * @param tags 标签
     * @param isAnonymous 是否匿名
     * @return 故事ID
     */
    Long publishStory(Long userId, String title, String content,
                      String emotionType, String tags, Boolean isAnonymous);

    /**
     * 获取故事列表（分页）
     *
     * @param page 页码
     * @param size 每页大小
     * @param emotionType 情绪类型筛选
     * @param sortBy 排序方式：hot/new/random
     * @return 分页结果
     */
    PageResult<StoryListVO> getStoryList(Integer page, Integer size,
                                        String emotionType, String sortBy);

    /**
     * 获取故事详情
     *
     * @param storyId 故事ID
     * @param userId 当前用户ID（用于判断是否已点赞/收藏）
     * @return 故事详情VO
     */
    StoryVO getStoryDetail(Long storyId, Long userId);

    /**
     * 点赞故事
     *
     * @param storyId 故事ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean likeStory(Long storyId, Long userId);

    /**
     * 取消点赞
     *
     * @param storyId 故事ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean unlikeStory(Long storyId, Long userId);

    /**
     * 发表评论
     *
     * @param storyId 故事ID
     * @param userId 用户ID
     * @param content 评论内容
     * @param parentId 父评论ID（可选）
     * @return 评论ID
     */
    Long addComment(Long storyId, Long userId, String content, Long parentId);

    /**
     * 收藏故事
     *
     * @param storyId 故事ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean favoriteStory(Long storyId, Long userId);

    /**
     * 获取用户的收藏列表
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    PageResult<StoryListVO> getUserFavorites(Long userId, Integer page, Integer size);

    /**
     * 审核故事（管理员/咨询师）
     *
     * @param storyId 故事ID
     * @param auditorId 审核人ID
     * @param approved 是否通过
     * @param rejectReason 拒绝原因
     * @return 是否成功
     */
    boolean auditStory(Long storyId, Long auditorId, boolean approved, String rejectReason);

    /**
     * 获取待审核的故事列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param status 状态筛选（可选，null表示所有状态）
     * @return 分页结果
     */
    PageResult<StoryListVO> getPendingStories(Integer page, Integer size, Integer status);

    /**
     * 删除故事（仅作者或管理员）
     *
     * @param storyId 故事ID
     * @param userId 操作用户ID
     * @return 是否成功
     */
    boolean deleteStory(Long storyId, Long userId);

    /**
     * 获取管理员用故事列表（包含userId）
     *
     * @param page 页码
     * @param size 每页大小
     * @param status 状态筛选（可选，null表示所有状态）
     * @return 分页结果
     */
    PageResult<AdminStoryListVO> getAdminStoryList(Integer page, Integer size, Integer status);

    /**
     * 获取管理员用故事详情（包含userId）
     *
     * @param storyId 故事ID
     * @return 故事详情
     */
    AdminStoryVO getAdminStoryDetail(Long storyId);

    /**
     * 管理员删除故事（无需用户权限校验）
     *
     * @param storyId 故事ID
     * @return 是否成功
     */
    boolean adminDeleteStory(Long storyId);
}
