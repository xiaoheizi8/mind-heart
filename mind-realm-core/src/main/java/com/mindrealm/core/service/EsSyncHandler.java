package com.mindrealm.core.service;

/**
 * @className: EsSyncHandler
 * @description: ES同步处理器接口，每种业务实体类型提供一个实现。
 *               实现类需注册为 Spring Bean，切面会自动发现并路由。
 *               onSave 接受 Object 以兼容两种场景：
 *               1) 方法返回实体对象（如 create/update 返回 Diary）
 *               2) 方法返回 Long ID（如 publishStory 返回 storyId）
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
public interface EsSyncHandler {

    /**
     * 同步类型标识，与 @EsSync("value") 对应
     * 如: "diary", "story"
     */
    String getType();

    /**
     * 保存/更新实体到ES
     * @param entityOrId 业务实体对象 或 实体ID（当方法返回Long时）
     */
    void onSave(Object entityOrId);

    /**
     * 从ES删除实体
     * @param id 实体主键ID
     */
    void onDelete(Long id);
}
