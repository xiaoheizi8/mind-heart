package com.mindrealm.api.service.sync;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.core.entity.StoryEsDocument;
import com.mindrealm.core.repository.StoryEsRepository;
import com.mindrealm.core.service.EsSyncHandler;
import com.mindrealm.core.service.impl.BaseEsSyncService;
import com.mindrealm.story.mapper.HeartStoryMapper;
import com.mindrealm.story.model.entity.HeartStory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @className: StoryEsSyncServiceImpl
 * @description: 故事ES同步服务，同时作为 EsSyncHandler（AOP实时同步）
 *               和 IEsSyncService（管理端手动同步）
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Service
public class StoryEsSyncServiceImpl
        extends BaseEsSyncService<HeartStory, StoryEsDocument>
        implements EsSyncHandler {

    private final HeartStoryMapper heartStoryMapper;
    private final StoryEsRepository storyEsRepository;

    public StoryEsSyncServiceImpl(ElasticsearchOperations elasticsearchOperations,
                                  StoryEsRepository storyEsRepository,
                                  HeartStoryMapper heartStoryMapper) {
        super(elasticsearchOperations, storyEsRepository, StoryEsDocument.class, "story");
        this.storyEsRepository = storyEsRepository;
        this.heartStoryMapper = heartStoryMapper;
    }

    // ==================== EsSyncHandler（AOP实时同步） ====================

    @Override
    public String getType() {
        return "story";
    }

    @Override
    public void onSave(Object entityOrId) {
        HeartStory story;
        if (entityOrId instanceof HeartStory hs) {
            story = hs;
        } else if (entityOrId instanceof Long id) {
            story = heartStoryMapper.selectById(id);
        } else {
            return;
        }
        // 仅同步已审核通过的故事到ES，防止未审核内容被搜索到
        if (story != null && story.getStatus() != null && story.getStatus() == 1) {
            storyEsRepository.save(toEsDocument(story));
        }
    }

    @Override
    public void onDelete(Long id) {
        storyEsRepository.deleteById(id);
    }

    // ==================== BaseEsSyncService（管理端手动同步） ====================

    @Override
    protected List<HeartStory> fetchFromDb(int page, int size) {
        IPage<HeartStory> result = heartStoryMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<HeartStory>()
                        .eq(HeartStory::getStatus, 1)
                        .orderByAsc(HeartStory::getId));
        return result.getRecords();
    }

    @Override
    protected StoryEsDocument convertToDocument(HeartStory story) {
        return toEsDocument(story);
    }

    private StoryEsDocument toEsDocument(HeartStory story) {
        return StoryEsDocument.builder()
                .id(story.getId())
                .userId(story.getUserId())
                .title(story.getTitle())
                .content(story.getContent())
                .emotionType(story.getEmotionType())
                .tags(story.getTags())
                .likeCount(story.getLikeCount())
                .commentCount(story.getCommentCount())
                .viewCount(story.getViewCount())
                .status(story.getStatus())
                .displayNickname(story.getDisplayNickname())
                .isAnonymous(story.getIsAnonymous())
                .createdAt(story.getCreatedAt())
                .updatedAt(story.getUpdatedAt())
                .publishedAt(story.getPublishedAt())
                .build();
    }

    @Override
    protected HeartStory fetchByIdFromDb(Long id) {
        return heartStoryMapper.selectById(id);
    }

    @Override
    protected long countInDb() {
        return heartStoryMapper.selectCount(
                new LambdaQueryWrapper<HeartStory>().eq(HeartStory::getStatus, 1));
    }
}
