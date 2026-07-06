package com.mindrealm.api.controller.admin;

import com.mindrealm.common.result.Result;
import com.mindrealm.mq.constant.KafkaTopics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @className: AdminMqController
 * @description: Kafka 消息队列管理接口
 *               - 查看死信消息列表
 *               - 手动重放/删除死信消息
 *               - Topic 消费状态统计
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@RestController
@RequestMapping("/admin/v1/mq")
@Tag(name = "消息队列管理", description = "Kafka 消息队列监控和管理接口")
public class AdminMqController {

    private static final Logger log = LoggerFactory.getLogger(AdminMqController.class);

    // TODO: 集成 KafkaAdminClient 实现真实的死信消息查询和重放
    // 当前返回占位数据，待后续集成 KafkaAdminClient + dead_letter_record 表

    /**
     * 获取各 Topic 状态
     */
    @GetMapping("/stats")
    @Operation(summary = "获取 Topic 状态统计")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("topics", List.of(
                Map.of("name", KafkaTopics.ES_SYNC, "partitions", 3, "status", "active"),
                Map.of("name", KafkaTopics.WARNING_ANALYZE, "partitions", 3, "status", "active"),
                Map.of("name", KafkaTopics.NOTIFICATION_SEND, "partitions", 3, "status", "active"),
                Map.of("name", KafkaTopics.ES_SYNC_DLT, "partitions", 1, "status", "active"),
                Map.of("name", KafkaTopics.WARNING_ANALYZE_DLT, "partitions", 1, "status", "active"),
                Map.of("name", KafkaTopics.NOTIFICATION_SEND_DLT, "partitions", 1, "status", "active")
        ));
        stats.put("message", "详细指标请查看 /actuator/prometheus 的 kafka_* 指标");
        return Result.success(stats);
    }

    /**
     * 分页查询死信消息
     */
    @GetMapping("/dead-letters")
    @Operation(summary = "查询死信消息列表")
    public Result<Map<String, Object>> getDeadLetters(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String topic) {
        log.info("查询死信消息: pageNum={}, pageSize={}, topic={}", pageNum, pageSize, topic);

        // TODO: 从 dead_letter_record 表查询
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", 0);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("records", Collections.emptyList());
        result.put("message", "死信消息表功能待实现，需创建 dead_letter_record 表");
        return Result.success(result);
    }

    /**
     * 手动重放死信消息
     */
    @PostMapping("/dead-letters/{topic}/{partition}/{offset}/replay")
    @Operation(summary = "手动重放死信消息")
    public Result<String> replayDeadLetter(
            @PathVariable String topic,
            @PathVariable int partition,
            @PathVariable long offset) {
        log.info("重放死信消息: topic={}, partition={}, offset={}", topic, partition, offset);

        // TODO: 通过 KafkaAdminClient 从 DLT 读取消息并重新发布到原 Topic
        return Result.success("消息重放已提交: topic=" + topic + ", partition=" + partition +
                ", offset=" + offset);
    }

    /**
     * 删除死信消息（丢弃不处理）
     */
    @DeleteMapping("/dead-letters/{topic}/{partition}/{offset}")
    @Operation(summary = "删除死信消息")
    public Result<String> deleteDeadLetter(
            @PathVariable String topic,
            @PathVariable int partition,
            @PathVariable long offset) {
        log.info("删除死信消息: topic={}, partition={}, offset={}", topic, partition, offset);

        // TODO: 通过 KafkaAdminClient 删除指定 offset 的消息（或标记为已处理）
        return Result.success("死信消息已标记删除");
    }
}
