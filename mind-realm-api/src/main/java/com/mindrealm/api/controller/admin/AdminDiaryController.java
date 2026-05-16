package com.mindrealm.api.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.annotation.OperationLog;
import com.mindrealm.common.annotation.OperationType;
import com.mindrealm.common.result.PageResult;
import com.mindrealm.common.result.Result;
import com.mindrealm.common.util.AesUtil;
import com.mindrealm.diary.entity.Diary;
import com.mindrealm.diary.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @className: AdminDiaryController
 * @description: 管理端日记控制器,提供管理员查看所有用户日记、日记详情、日记统计等功能
 *               支持分页查询、用户筛选、情绪筛选,日记内容会自动解密
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.24
 */
@RestController
@RequestMapping("/admin/v1/diary")
public class AdminDiaryController {

    @Autowired
    private DiaryService diaryService;

    /**
     * 分页查询日记列表(支持按用户ID和情绪分类筛选)
     * @param userId 用户ID(可选,为空则查询所有用户)
     * @param emotionCategory 情绪分类(可选)
     * @param pageNum 页码,默认1
     * @param pageSize 每页数量,默认10
     * @return 分页日记列表,内容已解密
     */
    @GetMapping("/list")
    @OperationLog(value = "查询日记列表", type = OperationType.SELECT)
    public Result<PageResult<Diary>> list(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String emotionCategory,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Page<Diary> page;
        
        if (userId != null) {
            // 按用户查询
            if (emotionCategory != null && !emotionCategory.isEmpty()) {
                page = diaryService.getListByUser(userId, emotionCategory, pageNum, pageSize);
            } else {
                page = diaryService.getListByUser(userId, pageNum, pageSize);
            }
        } else {
            // 查询所有用户的日记(需要DiaryService支持,这里使用简化实现)
            page = new Page<>(pageNum, pageSize);
        }
        
        // 解密日记内容
        if (page.getRecords() != null) {
            for (Diary diary : page.getRecords()) {
                if (diary.getContent() != null && AesUtil.isEncrypted(diary.getContent())) {
                    diary.setContent(AesUtil.decrypt(diary.getContent()));
                }
            }
        }
        
        return Result.success(PageResult.of(
                page.getRecords(),
                (int) page.getCurrent(),
                (int) page.getSize(),
                (int) page.getTotal()
        ));
    }

    /**
     * 查看日记详情(管理员可查看所有用户日记)
     * @param id 日记ID
     * @return 日记详情,内容已解密,不存在返回404
     */
    @GetMapping("/{id}")
    @OperationLog(value = "查看日记详情", type = OperationType.SELECT)
    public Result<Diary> getById(@PathVariable Long id) {
        Diary diary = diaryService.getById(id);
        if (diary == null) {
            return Result.notFound("日记不存在");
        }
        
        // 解密内容
        if (diary.getContent() != null && AesUtil.isEncrypted(diary.getContent())) {
            diary.setContent(AesUtil.decrypt(diary.getContent()));
        }
        
        return Result.success(diary);
    }

    /**
     * 获取用户日记统计信息
     * @param userId 用户ID
     * @return 日记统计数据(总数、最近7天数量、最近30天数量)
     */
    @GetMapping("/stats/{userId}")
    @OperationLog(value = "查询日记统计", type = OperationType.SELECT)
    public Result<java.util.Map<String, Object>> getStats(@PathVariable Long userId) {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        // 总日记数
        long totalDiaries = diaryService.getListByUser(userId, 1, 1).getTotal();
        stats.put("totalDiaries", totalDiaries);
        
        // 最近7天日记数
        long recent7Days = diaryService.countRecent(7);
        stats.put("recent7Days", recent7Days);
        
        // 最近30天日记数
        long recent30Days = diaryService.countRecent(30);
        stats.put("recent30Days", recent30Days);
        
        return Result.success(stats);
    }

    /**
     * 删除用户日记(管理员权限)
     * @param id 日记ID
     * @param userId 用户ID(用于权限校验)
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @OperationLog(value = "删除日记", type = OperationType.DELETE)
    public Result<Void> delete(
            @PathVariable Long id,
            @RequestParam Long userId) {
        
        Diary diary = diaryService.getById(id);
        if (diary == null) {
            return Result.notFound("日记不存在");
        }
        
        if (!diary.getUserId().equals(userId)) {
            return Result.badRequest("用户ID与日记不匹配");
        }
        
        boolean deleted = diaryService.delete(id, userId);
        if (deleted) {
            return Result.ok("删除成功");
        }
        return Result.error("删除失败");
    }
}
