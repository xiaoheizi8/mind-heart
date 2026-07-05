package com.mindrealm.common.annotation;

import java.lang.annotation.*;

/**
 * @className: EsSync
 * @description: 标记需要自动同步到 Elasticsearch 的方法。
 *               切面会在方法成功返回后异步将数据写入ES。
 *               用法示例:
 *               <pre>
 *               &#64;EsSync(value = "diary")           // create/update, 自动获取返回值
 *               public Diary create(Diary diary) { ... }
 *
 *               &#64;EsSync(value = "diary", op = Op.DELETE)  // delete, 使用第一个Long参数作为ID
 *               public boolean delete(Long id, Long userId) { ... }
 *               </pre>
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EsSync {

    /**
     * 同步类型标识，对应 EsSyncHandler#getType()
     * 如: "diary", "story"
     */
    String value();

    /**
     * 操作类型
     */
    Op op() default Op.SAVE;

    enum Op {
        /** 保存/更新：从方法返回值获取实体 */
        SAVE,
        /** 删除：从方法第一个参数获取ID */
        DELETE
    }
}
