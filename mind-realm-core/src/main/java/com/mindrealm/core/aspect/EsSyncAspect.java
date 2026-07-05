package com.mindrealm.core.aspect;

import com.mindrealm.common.annotation.EsSync;
import com.mindrealm.core.service.EsSyncHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @className: EsSyncAspect
 * @description: ES同步切面，拦截 @EsSync 注解的方法，
 *               在方法成功返回后异步将数据同步到 Elasticsearch。
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Aspect
@Component
public class EsSyncAspect {

    private static final Logger log = LoggerFactory.getLogger(EsSyncAspect.class);

    private final Map<String, EsSyncHandler> handlerMap;

    /**
     * Spring 自动注入所有 EsSyncHandler 实现
     */
    public EsSyncAspect(List<EsSyncHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(
                        EsSyncHandler::getType,
                        h -> h
                ));
        log.info("ES同步切面已初始化，已注册的处理器: {}", handlerMap.keySet());
    }

    /**
     * SAVE操作：方法成功返回后异步同步到ES
     * 返回值可能是实体对象（Diary）、实体ID（Long）、或Boolean（此时从参数中取ID）
     */
    @AfterReturning(pointcut = "@annotation(esSync)", returning = "result")
    public void afterSave(JoinPoint joinPoint, EsSync esSync, Object result) {
        if (esSync.op() != EsSync.Op.SAVE || result == null) {
            return;
        }

        EsSyncHandler handler = handlerMap.get(esSync.value());
        if (handler == null) {
            log.warn("未找到 type=[{}] 的 EsSyncHandler，跳过ES同步", esSync.value());
            return;
        }

        // 如果返回Boolean(true)且没有实体对象，尝试从参数中提取ID
        Object syncTarget = result;
        if (result instanceof Boolean && (Boolean) result) {
            Long id = extractId(joinPoint.getArgs());
            if (id != null) {
                syncTarget = id;
            } else {
                return; // 无法提取ID，跳过
            }
        }

        asyncSync(handler, syncTarget, esSync.value());
    }

    /**
     * DELETE操作：从方法参数中获取ID，在方法成功返回后执行ES删除
     */
    @AfterReturning(pointcut = "@annotation(esSync)", returning = "result")
    public void afterDelete(JoinPoint joinPoint, EsSync esSync, Object result) {
        if (esSync.op() != EsSync.Op.DELETE) {
            return;
        }

        // DELETE：返回值Boolean为false表示删除失败，跳过ES同步
        if (result instanceof Boolean && !(Boolean) result) {
            return;
        }

        EsSyncHandler handler = handlerMap.get(esSync.value());
        if (handler == null) {
            log.warn("未找到 type=[{}] 的 EsSyncHandler，跳过ES删除", esSync.value());
            return;
        }

        // 从方法参数中取第一个 Long 类型参数作为ID
        Long id = extractId(joinPoint.getArgs());
        if (id == null) {
            log.warn("无法从方法参数中提取ID，跳过ES删除, type={}", esSync.value());
            return;
        }

        asyncDelete(handler, id, esSync.value());
    }

    @Async
    public void asyncSync(EsSyncHandler handler, Object entityOrId, String type) {
        try {
            handler.onSave(entityOrId);
            log.debug("ES同步成功: type={}", type);
        } catch (Exception e) {
            log.error("ES同步失败: type={}, error={}", type, e.getMessage());
        }
    }

    @Async
    public void asyncDelete(EsSyncHandler handler, Long id, String type) {
        try {
            handler.onDelete(id);
            log.debug("ES删除成功: type={}, id={}", type, id);
        } catch (Exception e) {
            log.error("ES删除失败: type={}, id={}, error={}", type, id, e.getMessage());
        }
    }

    /**
     * 从参数数组中提取第一个 Long 类型参数作为实体ID
     */
    private Long extractId(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Long) {
                return (Long) arg;
            }
        }
        return null;
    }
}
