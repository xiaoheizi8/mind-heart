package com.mindrealm.api.service.sync;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.core.entity.DiaryEsDocument;
import com.mindrealm.core.repository.DiaryEsRepository;
import com.mindrealm.core.service.EsSyncHandler;
import com.mindrealm.core.service.impl.BaseEsSyncService;
import com.mindrealm.diary.entity.Diary;
import com.mindrealm.diary.mapper.DiaryMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @className: DiaryEsSyncServiceImpl
 * @description: 日记ES同步服务，同时作为 EsSyncHandler（AOP实时同步）
 *               和 IEsSyncService（管理端手动同步）
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Service
public class DiaryEsSyncServiceImpl
        extends BaseEsSyncService<Diary, DiaryEsDocument>
        implements EsSyncHandler {

    private final DiaryMapper diaryMapper;
    private final DiaryEsRepository diaryEsRepository;

    public DiaryEsSyncServiceImpl(ElasticsearchOperations elasticsearchOperations,
                                  DiaryEsRepository diaryEsRepository,
                                  DiaryMapper diaryMapper) {
        super(elasticsearchOperations, diaryEsRepository, DiaryEsDocument.class, "diary");
        this.diaryEsRepository = diaryEsRepository;
        this.diaryMapper = diaryMapper;
    }

    // ==================== EsSyncHandler（AOP实时同步） ====================

    @Override
    public String getType() {
        return "diary";
    }

    @Override
    public void onSave(Object entityOrId) {
        Diary diary;
        if (entityOrId instanceof Diary d) {
            diary = d;
        } else if (entityOrId instanceof Long id) {
            diary = diaryMapper.selectById(id);
        } else {
            return;
        }
        if (diary != null) {
            diaryEsRepository.save(toEsDocument(diary));
        }
    }

    @Override
    public void onDelete(Long id) {
        diaryEsRepository.deleteById(id);
    }

    // ==================== BaseEsSyncService（管理端手动同步） ====================

    @Override
    protected List<Diary> fetchFromDb(int page, int size) {
        IPage<Diary> result = diaryMapper.selectPage(
                new Page<>(page, size),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Diary>()
                        .orderByAsc(Diary::getId));
        return result.getRecords();
    }

    @Override
    protected DiaryEsDocument convertToDocument(Diary diary) {
        return toEsDocument(diary);
    }

    private DiaryEsDocument toEsDocument(Diary diary) {
        return DiaryEsDocument.builder()
                .id(diary.getId())
                .userId(diary.getUserId())
                .content(diary.getContent())
                .emotionCategory(diary.getEmotionCategory())
                .emotionScore(diary.getEmotionScore())
                .emotionTags(diary.getEmotionTags())
                .aiAnalysis(diary.getAiAnalysis())
                .createdAt(diary.getCreatedAt())
                .build();
    }

    @Override
    protected Diary fetchByIdFromDb(Long id) {
        return diaryMapper.selectById(id);
    }

    @Override
    protected long countInDb() {
        return diaryMapper.selectCount(null);
    }
}
